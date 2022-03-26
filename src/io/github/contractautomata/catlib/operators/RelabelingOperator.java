package io.github.contractautomata.catlib.operators;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;

/**
 * Class implementing the relabeling operator
 * @author Davide Basile
 *
 */
public class RelabelingOperator<S1, L extends Label<Action>> implements
		Function<Automaton<S1, Action,State<S1>, ModalTransition<S1,Action,State<S1>,L>>,
				Set<ModalTransition<S1, Action, State<S1>, L>>> {


	private final UnaryOperator<S1> relabel;
	private final Function<List<Action>,L> createLabel;
	private final Predicate<BasicState<S1>> initialStatePred;
	private final Predicate<BasicState<S1>> finalStatePred;

	public RelabelingOperator(Function<List<Action>,L> createLabel,
							  UnaryOperator<S1> relabel,
							  Predicate<BasicState<S1>> initialStatePred,
			Predicate<BasicState<S1>> finalStatePred) {
		this.createLabel=createLabel;
		this.relabel=relabel;
		this.initialStatePred= initialStatePred;
		this.finalStatePred= finalStatePred;
	}

	
	@Override
	public Set<ModalTransition<S1, Action, State<S1>, L>> apply(Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,L>> aut)
	{	
		if (aut.getTransition().isEmpty())
			throw new IllegalArgumentException();

		Map<BasicState<S1>,BasicState<S1>> clonedstate = aut.getStates().stream()
				.flatMap(x->x.getState().stream())
				.distinct()
				.collect(Collectors.toMap(Function.identity(), 
						s-> new BasicState<>(relabel.apply(s.getState()),
                                initialStatePred.test(s), finalStatePred.test(s))));

		Map<State<S1>,State<S1>> clonedcastates  = aut.getStates().stream()
				.collect(Collectors.toMap(Function.identity(), 
						x-> new State<>(x.getState().stream()
                                .map(clonedstate::get)
                                .collect(Collectors.toList()))));

		return  aut.getTransition().stream()
				.map(t-> new ModalTransition<>(clonedcastates.get(t.getSource()),
						createLabel.apply(t.getLabel().getLabel()),
						clonedcastates.get(t.getTarget()),
						t.getModality()))
				.collect(Collectors.toSet());

	}
}