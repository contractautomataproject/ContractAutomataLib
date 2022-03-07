package io.github.contractautomataproject.catlib.operators;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

/**
 * Class implementing the abstract synthesis operator
 * 
 * @author Davide Basile
 *
 */
public class ModalAutomatonSynthesisOperator<L extends Label<List<String>>> extends 
	SynthesisOperator<List<BasicState>,List<String>,CAState,L,ModalTransition<List<BasicState>,List<String>,CAState,L>>{

	/**
	 * 
	 * @param pruningPredicate  the pruning predicate 
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 */
	public ModalAutomatonSynthesisOperator(
			TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,L>, Set<ModalTransition<List<BasicState>,List<String>,CAState,L>>, Set<CAState>> pruningPredicate,
			TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,L>, Set<ModalTransition<List<BasicState>,List<String>,CAState,L>>, Set<CAState>> forbiddenPredicate, 
			Predicate<L> req, 
			Function<List<String>,L> createLabel) {
		super(pruningPredicate,forbiddenPredicate,req,
				a->new RelabelingOperator<L>(createLabel).apply(new ModalAutomaton<L>(a.getTransition())).relaxAsAutomaton());
	}


	/**
	 * This constructor does not use any pruning predicate
	 * 
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
	 */
	public ModalAutomatonSynthesisOperator(TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,L>, Set<ModalTransition<List<BasicState>,List<String>,CAState,L>>, Set<CAState>> forbiddenPredicate, 
			Predicate<L> req, 
			Function<List<String>,L> createLabel) {
		this((x,t,bad) -> false, forbiddenPredicate,req,createLabel);
	}

	/** 
	 * invokes the synthesis
	 * @param arg1 the plant automaton to which the synthesis is performed
	 * @return the synthesised automaton
	 * 
	 */
	@Override
	public ModalAutomaton<L> apply(Automaton<List<BasicState>,List<String>,CAState,
			ModalTransition<List<BasicState>,List<String>,CAState,L>> arg1) {
		Automaton<List<BasicState>,List<String>,CAState,
		ModalTransition<List<BasicState>,List<String>,CAState,L>> a = super.apply(arg1);
		if (a!=null)
			return new ModalAutomaton<L>(super.apply(arg1).getTransition());
		else 
			return null;
	}

}
