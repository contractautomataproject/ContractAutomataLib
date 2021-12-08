package io.github.davidebasile.contractautomata.operators;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.MSCATransition;
import io.github.davidebasile.contractautomata.automaton.transition.Transition;

/**
 * Class implementing the abstract synthesis operator
 * 
 * @author Davide Basile
 *
 */
public class SynthesisOperator implements UnaryOperator<MSCA>{

	private Map<CAState,Boolean> reachable;
	private Map<CAState,Boolean> successful;
	private TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> pruningPred;
	private final TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> forbiddenPred;
	private	Function<MSCA,Set<CAState>> getForbiddenStates=a->Collections.emptySet();

	/**
	 * 
	 * @param pruningPredicate  the pruning predicate 
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 */
	public SynthesisOperator(TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> pruningPredicate,
			TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> forbiddenPredicate, 
			Predicate<MSCATransition> req) {
		super();
		this.pruningPred = (x,t,bad) -> bad.contains(x.getTarget())|| !req.test(x) || pruningPredicate.test(x, t, bad);
		this.forbiddenPred = (x,t,bad) -> !t.contains(x)&&forbiddenPredicate.test(x, t, bad);
	}

	/**
	 * 
	 * @param pruningPredicate the pruning predicate
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param prop another property to enforce expressed by an automaton
	 */
	public SynthesisOperator(TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> pruningPredicate,
			TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> forbiddenPredicate,
			Predicate<MSCATransition> req,
			Automaton<String,BasicState,Transition<String,BasicState,Label>>  prop) {
		this(pruningPredicate,forbiddenPredicate,req);
		if (prop!=null)
			getForbiddenStates = a -> new ModelCheckingFunction().apply(a, prop);
	}

	/**
	 * This constructor does not use any pruning predicate
	 * 
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
	 */
	public SynthesisOperator(TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> forbiddenPredicate, 
			Predicate<MSCATransition> req) {
		this((x,t,bad) -> false, forbiddenPredicate,req);
	}

	/**
	 * This constructor does not use any pruning predicate
	 *   
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param prop another property to enforce expressed by an automaton
	 */
	public SynthesisOperator(TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> forbiddenPredicate,
			Predicate<MSCATransition> req,
			Automaton<String,BasicState,Transition<String,BasicState,Label>>  prop) {
		this((x,t,bad) -> false, forbiddenPredicate,req);
		if (prop!=null)
			getForbiddenStates = a -> new ModelCheckingFunction().apply(a, prop);

	}

	/** 
	 * invokes the synthesis
	 * @param arg1 the plant automaton to which the synthesis is performed
	 * @return the synthesised automaton
	 * 
	 */
	@Override
	public MSCA apply(MSCA arg1) {
		{
			MSCA aut= new RelabelingOperator().apply(arg1);//creating an exact copy

			Set<MSCATransition> trbackup = new HashSet<MSCATransition>(aut.getTransition());
			Set<CAState> statesbackup= aut.getStates(); 
			CAState init = aut.getInitial();
			Set<CAState> R = new HashSet<CAState>(getDanglingStates(aut, statesbackup,init));//R0
			R.addAll(getForbiddenStates.apply(aut));		
			boolean update=false;
			do{
				final Set<CAState> Rf = new HashSet<CAState>(R); 
				final Set<MSCATransition> trf= new HashSet<MSCATransition>(aut.getTransition());

				if (aut.getTransition().removeAll(aut.getTransition().parallelStream()
						.filter(x->pruningPred.test(x,trf, Rf))
						.collect(Collectors.toSet()))) //Ki
					R.addAll(getDanglingStates(aut, statesbackup,init));

				R.addAll(trbackup.parallelStream() 
						.filter(x->forbiddenPred.test(x,trf, Rf))
						.map(MSCATransition::getSource)
						.collect(Collectors.toSet())); //Ri

				update=Rf.size()!=R.size()|| trf.size()!=aut.getTransition().size();
			} while(update);


			if (R.contains(init)||aut.getTransition().size()==0)
				return null;

			//remove dangling transitions
			aut.getTransition().removeAll(aut.getTransition().parallelStream()
					.filter(x->!reachable.get(x.getSource())||!successful.get(x.getTarget()))
					.collect(Collectors.toSet()));

			return aut;
		}
	}

	/**
	 * @return	states who do not reach a final state or are unreachable
	 */
	private Set<CAState> getDanglingStates(MSCA aut, Set<CAState> states, CAState initial)
	{

		//all states' flags are reset
		this.reachable=states.parallelStream()   //this.getStates().forEach(s->{s.setReachable(false);	s.setSuccessful(false);});
				.collect(Collectors.toMap(x->x, x->false));
		this.successful=states.parallelStream()
				.collect(Collectors.toMap(x->x, x->false));

		//set reachable
		forwardVisit(aut, initial);  

		//set successful
		states.forEach(
				x-> {if (x.isFinalstate()&&this.reachable.get(x))//x.isReachable())
					backwardVisit(aut,x);});  

		return states.parallelStream()
				.filter(x->!(reachable.get(x)&&this.successful.get(x)))  //!(x.isReachable()&&x.isSuccessful()))
				.collect(Collectors.toSet());
	}

	private void forwardVisit(MSCA aut, CAState currentstate)
	{ 
		this.reachable.put(currentstate, true);  //currentstate.setReachable(true);
		aut.getForwardStar(currentstate).forEach(x->{
			if (!this.reachable.get(x.getTarget()))//!x.getTarget().isReachable())
				forwardVisit(aut,x.getTarget());
		});
	}

	private void backwardVisit(MSCA aut, CAState currentstate)
	{ 
		this.successful.put(currentstate, true); //currentstate.setSuccessful(true);

		aut.getTransition().stream()
		.filter(x->x.getTarget().equals(currentstate))
		.forEach(x->{
			if (!this.successful.get(x.getSource()))//!x.getSource().isSuccessful())
				backwardVisit(aut, x.getSource());
		});
	}
}



//Pair<Set<CAState>, Set<MSCATransition>> seed=
//new Pair<Set<CAState>, Set<MSCATransition>>(R, aut.getTransition());
//
//Pair<Set<CAState>, Set<MSCATransition>> result = new Fixpoint<Pair<Set<CAState>, Set<MSCATransition>>>().apply(seed,p->
//{
//final Set<CAState> Rf = new HashSet<CAState>(p.getFirst()); 
//final Set<MSCATransition> trf= new HashSet<MSCATransition>(p.getSecond());
//
//if (aut.getTransition().removeAll(aut.getTransition().parallelStream()
//	.filter(x->pruningPred.test(x,trf, Rf))
//	.collect(Collectors.toSet()))) //Ki
//R.addAll(getDanglingStates(aut, statesbackup,init));
//
//R.addAll(trbackup.parallelStream() 
//	.filter(x->forbiddenPred.test(x,trf, Rf))
//	.map(MSCATransition::getSource)
//	.collect(Collectors.toSet())); //Ri
//return new Pair<Set<CAState>, Set<MSCATransition>>(R,aut.getTransition());});

//class Fixpoint<T> implements BiFunction<T,UnaryOperator<T>,T> {
//
//	@Override
//	public T apply(T t, UnaryOperator<T> u) {
//		return Stream.iterate(t,e->u.apply(e))
//				.distinct()
//				.reduce((x,y)->y).orElseThrow(IllegalArgumentException::new);
//		
//	}
//}










///**
// * 
// * @param state
// * @return true if the successful value of state has changed
// */
//private boolean forwardNeighbourVisit(CAState state)
//{	
//	boolean b = state.isSuccessful();
//	state.setSuccessful(Arrays.stream(this.getTransitionsWithSource(state))
//			.map(FMCATransition::getTarget)
//			.anyMatch(CAState::isSuccessful));
//
//	return b!=state.isSuccessful();
//	
//}
//
///**
// * 
// * @param state
// * @return true if the reachable value of state has changed
// */
//private boolean backwardNeighbourVisit(CAState state)
//{	
//	boolean b = state.isReachable();
//	
//	state.setReachable(Arrays.stream(this.getTransitionsWithTarget(state))
//			.map(FMCATransition::getSource)
//			.anyMatch(CAState::isReachable));
//	return b!=state.isReachable();
//	
//}	


