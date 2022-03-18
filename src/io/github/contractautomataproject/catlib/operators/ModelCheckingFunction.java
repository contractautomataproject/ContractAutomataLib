package io.github.contractautomataproject.catlib.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

/**
 * Class implementing the model checking function. 
 * This is implemented by instantiating and applying the composition function, 
 * composing the model with the property.
 * 
 * @author Davide Basile
 *
 */
public class ModelCheckingFunction extends CompositionFunction<List<BasicState<String>>,List<String>,CAState<String>,Label<List<String>>,ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,Label<List<String>>>,ModalAutomaton<Label<List<String>>>> 
{

	public ModelCheckingFunction(ModalAutomaton<CALabel> aut, 
			Automaton<String,String,BasicState<String>,ModalTransition<String,String,BasicState<String>,Label<String>>> prop, 
			Predicate<Label<List<String>>> pruningPred) {
		super(Arrays.asList(new ModalAutomaton<>(aut.getTransition() //converting labels to Label<List<Stri
				.parallelStream()
				.map(t->{Label<List<String>> lab = t.getLabel();
					return new ModalTransition<>(t.getSource(),lab,t.getTarget(),t.getModality());})
				.collect(Collectors.toSet())),
				ModelCheckingFunction.convert(prop, Label::new, ModalTransition::new, ModalAutomaton::new)), 
				MSCACompositionFunction::computeRank,
				(l1,l2)->new CALabel(l1.getAction()).getUnsignedAction().equals(l2.getAction().get(0)), //match
				CAState::createStateByFlattening, 
				ModalTransition::new, 
				(e, ee,rank) -> new Label<List<String>>(Stream.concat(e.tra.getLabel().getAction().stream(), 
						ee.tra.getLabel().getAction().stream())
						.collect(Collectors.toList())), 
				(lab, rank, shift) ->{ 
					List<String> l = new ArrayList<>(rank);
					l.addAll(Stream.generate(()->CALabel.IDLE).limit(shift).collect(Collectors.toList()));
					l.addAll(lab.getAction());
					if (rank-l.size()>0)
						l.addAll(Stream.generate(()->CALabel.IDLE).limit(rank.longValue()-l.size()).collect(Collectors.toList()));
					return new Label<List<String>>(l);
				}, 
				ModalAutomaton::new,
				pruningPred);

	}

	/**
	 *
	 * @param aut the automaton to convert
	 * @param createLabel	the constructor of a label
	 * @param createTransition	the constructor of a transition
	 * @param createAut	the constructor of the automaton
	 * @return	aut converted into an extension of ModalAutomaton
	 */
	private static <L extends Label<List<String>>, T extends  ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,L>, 
	A extends ModalAutomaton<L>> A convert(
			Automaton<String,String,BasicState<String>,ModalTransition<String,String,BasicState<String>,Label<String>>>  aut,
			Function<List<String>,L> createLabel, 
			TetraFunction<CAState<String>,L,CAState<String>,ModalTransition.Modality,T> createTransition, 
			Function<Set<T>,A> createAut)
	{
		Map<BasicState<String>,CAState<String>> bs2cs = aut.getStates().stream()
				.collect(Collectors.toMap(Function.identity(), s->new CAState<String>(Arrays.asList(s))));

		return createAut.apply(aut.getTransition().parallelStream()
				.map(t->createTransition.apply(bs2cs.get(t.getSource()),
						createLabel.apply(List.of(t.getLabel().getAction())),
						bs2cs.get(t.getTarget()), 
						t.getModality()))
				.collect(Collectors.toSet()));
	}

}

interface PentaFunction<A,B,C,D,E,F>{
	public F apply(A arg1, B arg2, C arg3, D arg4, E arg5);
}