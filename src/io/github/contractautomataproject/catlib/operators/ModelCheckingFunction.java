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
import io.github.contractautomataproject.catlib.automaton.label.action.Action;
import io.github.contractautomataproject.catlib.automaton.label.action.IdleAction;
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
public class ModelCheckingFunction extends CompositionFunction<String,Action,State<String>,Label<Action>,
ModalTransition<String,Action,State<String>,Label<Action>>,Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,Label<Action>>>>

{

	public ModelCheckingFunction(Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut,
			Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,Label<Action>>> prop,
			Predicate<Label<Action>> pruningPred) {
		super(Arrays.asList(new Automaton<>(aut.getTransition() //converting labels to Label<String>
				.parallelStream()
				.map(t->{Label<Action> lab = t.getLabel();
					return new ModalTransition<>(t.getSource(),lab,t.getTarget(),t.getModality());})
				.collect(Collectors.toSet())),
				prop),
				MSCACompositionFunction::computeRank,
				(l1,l2)->new CALabel(l1.getLabel()).getAction().getLabel().equals(l2.getLabel().get(0).getLabel()), //match
				State::new, 
				ModalTransition::new, 
				(e, ee,rank) -> new Label<>(Stream.concat(e.tra.getLabel().getLabel().stream(),
                                ee.tra.getLabel().getLabel().stream())
                        .collect(Collectors.toList())),
				(lab, rank, shift) ->{ 
					List<Action> l = new ArrayList<>(rank);
					l.addAll(Stream.generate(IdleAction::new).limit(shift).collect(Collectors.toList()));
					l.addAll(lab.getLabel());
					if (rank-l.size()>0)
						l.addAll(Stream.generate(IdleAction::new).limit(rank.longValue()-l.size()).collect(Collectors.toList()));
					return new Label<>(l);
				}, 
				Automaton::new,
				pruningPred);

	}
}