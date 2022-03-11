//package io.github.contractautomataproject.catlib.automaton;
//
//import java.util.List;
//import java.util.Set;
//import java.util.function.Predicate;
//
//import io.github.contractautomataproject.catlib.automaton.label.Label;
//import io.github.contractautomataproject.catlib.automaton.state.BasicState;
//import io.github.contractautomataproject.catlib.automaton.state.CAState;
//import io.github.contractautomataproject.catlib.operators.SynthesisOperator;
//import io.github.contractautomataproject.catlib.transition.ModalTransition;
//
///**
// * Class implementing the abstract synthesis operator
// * 
// * @author Davide Basile
// *
// */
//public class ModalAutomatonSynthesisOperator<L extends Label<List<String>>> extends 
//	SynthesisOperator<List<BasicState>,List<String>,CAState,L,ModalTransition<List<BasicState>,List<String>,CAState,L>,ModalAutomaton<L>>{
//
//	/**
//	 * 
//	 * @param pruningPredicate  the pruning predicate 
//	 * @param forbiddenPredicate the forbidden predicate
//	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
//	 */
//	public ModalAutomatonSynthesisOperator(
//			TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,L>, Set<ModalTransition<List<BasicState>,List<String>,CAState,L>>, Set<CAState>> pruningPredicate,
//			TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,L>, Set<ModalTransition<List<BasicState>,List<String>,CAState,L>>, Set<CAState>> forbiddenPredicate, 
//			Predicate<L> req) {
//		super(pruningPredicate,forbiddenPredicate,req,ModalAutomaton::new);
//	}
//
//
//	/**
//	 * This constructor does not use any pruning predicate
//	 * 
//	 * @param forbiddenPredicate the forbidden predicate
//	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
//	 */
//	public ModalAutomatonSynthesisOperator(TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,L>, Set<ModalTransition<List<BasicState>,List<String>,CAState,L>>, Set<CAState>> forbiddenPredicate, 
//			Predicate<L> req) {
//		this((x,t,bad) -> false, forbiddenPredicate,req);
//	}
//
//}
