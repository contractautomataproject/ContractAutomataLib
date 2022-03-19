package io.github.contractautomataproject.catlib.operators;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

/**
 * Class implementing the relabeling operator
 * @author Davide Basile
 *
 */
public class RelabelingOperator<L extends Label<String>> implements Function<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,L>>, Set<ModalTransition<String,String,State<String>,L>>> {
	private final UnaryOperator<String> relabel;
	private final Function<List<String>,L> createLabel;
	private final Predicate<BasicState<String>> initialStatePred;
	private final Predicate<BasicState<String>> finalStatePred;

	public RelabelingOperator(Function<List<String>,L> createLabel, UnaryOperator<String> relabel,Predicate<BasicState<String>> initialStatePred, 
			Predicate<BasicState<String>> finalStatePred) {
		this.createLabel=createLabel;
		this.relabel=relabel;
		this.initialStatePred= initialStatePred;
		this.finalStatePred= finalStatePred;
	}

	
	@Override
	public Set<ModalTransition<String,String,State<String>,L>> apply(Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,L>> aut)
	{	
		if (aut.getTransition().isEmpty())
			throw new IllegalArgumentException();

		Map<BasicState<String>,BasicState<String>> clonedstate = aut.getStates().stream()
				.flatMap(x->x.getState().stream())
				.distinct()
				.collect(Collectors.toMap(Function.identity(), 
						s-> new BasicState<>(relabel.apply(s.getState()),
                                initialStatePred.test(s), finalStatePred.test(s))));

		Map<State<String>,State<String>> clonedcastates  = aut.getStates().stream()
				.collect(Collectors.toMap(Function.identity(), 
						x-> new State<>(x.getState().stream()
                                .map(clonedstate::get)
                                .collect(Collectors.toList()))));

		return aut.getTransition().stream()
				.map(t-> new ModalTransition<>(clonedcastates.get(t.getSource()),
                        createLabel.apply(t.getLabel().getAction()),
                        clonedcastates.get(t.getTarget()),
                        t.getModality()))
				.collect(Collectors.toSet());
	}
}