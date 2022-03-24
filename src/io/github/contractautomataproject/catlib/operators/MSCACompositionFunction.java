package io.github.contractautomataproject.catlib.operators;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.action.Action;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;

import java.util.List;
import java.util.function.Predicate;

/**
 * Class implementing the composition of Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<String>,CALabel>>
 * 
 * @author Davide Basile
 */

public class MSCACompositionFunction<S1> extends CompositionFunction<S1,State<S1>,CALabel,ModalTransition<S1,Action,State<S1>,CALabel>,Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>>> {

	public MSCACompositionFunction(List<Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>>> aut, Predicate<CALabel> pruningPred)
	{
		super(aut,CALabel::match,State::new,ModalTransition::new,CALabel::new,Automaton::new, pruningPred);
	}

}