package io.github.davidebasile.contractautomata.automaton.transition;


import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;



/**
 * Transition of a modal service contract automaton
 * 
 * @author Davide Basile
 *
 */
public class MSCATransition extends ModalTransition<List<BasicState>,String,CAState,CALabel> {


	public MSCATransition(CAState source, CALabel label, CAState target, Modality type) {
		super(source, label, target, type);
	}

	/**
	 * 
	 * @param tr the set of transitions to check
	 * @param badStates the set of badstates to check
	 * @param controllabilityPred the controllability predicate
	 * @return true if the transition is uncontrollable against the parameters
	 */
	public boolean isUncontrollable(Set<? extends MSCATransition> tr, Set<CAState> badStates, BiPredicate<MSCATransition,MSCATransition> controllabilityPred)
	{
		if (this.isUrgent())
			return true;
		if (this.isPermitted())//||(this.getLabel().isMatch()&&tr.contains(this))
			return false;
		return !tr.parallelStream()
				.filter(t->t.getLabel().isMatch()
						&& !badStates.contains(t.getSource()))
				//	&&!badStates.contains(t.getTarget())//guaranteed to hold if the pruning predicate has bad.contains(x.getTarget())
				.anyMatch(t->controllabilityPred.test(t,this));
	}



	/*	//**
	 *
	 * @param t	set of transitions
	 * @return   source states of transitions in t 
	 *//*
	static Set<CAState> getSources(Set<? extends MSCATransition> t)
	{
		return t.parallelStream()
				.map(MSCATransition::getSource)
				.collect(Collectors.toSet());
	}*/

}