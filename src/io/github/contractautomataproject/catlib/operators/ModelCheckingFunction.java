package io.github.contractautomataproject.catlib.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;

/**
 * Class implementing the model checking function. 
 * This is implemented by instantiating and applying the composition function, 
 * composing the model with the property.
 * 
 * @author Davide Basile
 *
 */
public class ModelCheckingFunction extends CompositionFunction<String,String,State<String>,Label<String>,
ModalTransition<String,String,State<String>,Label<String>>,Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>>> 

{

	public ModelCheckingFunction(Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut, 
			Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>> prop, 
			Predicate<Label<String>> pruningPred) {
		super(Arrays.asList(new Automaton<>(aut.getTransition() //converting labels to Label<String>
				.parallelStream()
				.map(t->{Label<String> lab = t.getLabel();
					return new ModalTransition<>(t.getSource(),lab,t.getTarget(),t.getModality());})
				.collect(Collectors.toSet())),
				prop),
				MSCACompositionFunction::computeRank,
				(l1,l2)->new CALabel(l1.getAction()).getUnsignedAction().equals(l2.getAction().get(0)), //match
				State::new, 
				ModalTransition::new, 
				(e, ee,rank) -> new Label<>(Stream.concat(e.tra.getLabel().getAction().stream(),
                                ee.tra.getLabel().getAction().stream())
                        .collect(Collectors.toList())),
				(lab, rank, shift) ->{ 
					List<String> l = new ArrayList<>(rank);
					l.addAll(Stream.generate(()->CALabel.IDLE).limit(shift).collect(Collectors.toList()));
					l.addAll(lab.getAction());
					if (rank-l.size()>0)
						l.addAll(Stream.generate(()->CALabel.IDLE).limit(rank.longValue()-l.size()).collect(Collectors.toList()));
					return new Label<>(l);
				}, 
				Automaton::new,
				pruningPred);

	}
}

interface PentaFunction<A,B,C,D,E,F>{
	F apply(A arg1, B arg2, C arg3, D arg4, E arg5);
}