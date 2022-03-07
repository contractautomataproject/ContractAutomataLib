package io.github.contractautomataproject.catlib.operators;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.transition.ModalTransition;
import io.github.contractautomataproject.catlib.transition.Transition;

/**
 * Class implementing the abstract synthesis operator
 * 
 * @author Davide Basile
 *
 */
public class SynthesisOperator<CS,CL,S extends State<CS>,
L extends Label<CL>,T extends ModalTransition<CS,CL,S,L>> implements UnaryOperator<Automaton<CS,CL,S,T>>{

	private Map<S,Boolean> reachable;
	private Map<S,Boolean> successful;
	private TriPredicate<T, Set<T>, Set<S>> pruningPred;
	private final TriPredicate<T, Set<T>, Set<S>> forbiddenPred;
	private final Function<Automaton<CS,CL,S,T>,Automaton<CS,CL,S,T>> duplicateAut;
	private final Predicate<L> req;

	/**
	 * 
	 * @param pruningPredicate  the pruning predicate 
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 */
	public SynthesisOperator(TriPredicate<T, Set<T>, Set<S>> pruningPredicate,
			TriPredicate<T, Set<T>, Set<S>> forbiddenPredicate, 
			Predicate<L> req,
			Function<Automaton<CS,CL,S,T>,Automaton<CS,CL,S,T>> duplicateAut) {
		super();
		this.pruningPred = (x,t,bad) -> bad.contains(x.getTarget())|| !req.test(x.getLabel()) || pruningPredicate.test(x, t, bad);
		this.forbiddenPred = (x,t,bad) -> !t.contains(x)&&forbiddenPredicate.test(x, t, bad);
		this.duplicateAut=duplicateAut;
		this.req=req;
	}


	/**
	 * This constructor does not use any pruning predicate
	 * 
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
	 */
	public SynthesisOperator(TriPredicate<T, Set<T>, Set<S>> forbiddenPredicate, 
			Predicate<L> req,
			Function<Automaton<CS,CL,S,T>,Automaton<CS,CL,S,T>> duplicateAut) {
		this((x,t,bad) -> false, forbiddenPredicate,req,duplicateAut);
	}
	

	public void setPruningPred(TriPredicate<T, Set<T>, Set<S>> pruningPredicate, Predicate<L> req) {
		this.pruningPred =  (x,t,bad) -> bad.contains(x.getTarget())|| !req.test(x.getLabel()) || pruningPredicate.test(x, t, bad);
	}

	
	
	public Predicate<L> getReq() {
		return req;
	}


	/** 
	 * invokes the synthesis
	 * @param arg1 the plant automaton to which the synthesis is performed
	 * @return the synthesised automaton
	 * 
	 */
	@Override
	public Automaton<CS,CL,S,T> apply(Automaton<CS,CL,S,T> arg1) {
		{
			if (arg1==null)
				throw new IllegalArgumentException();
			//creating an exact copy
			Automaton<CS,CL,S,T> aut= duplicateAut.apply(arg1);
						
			
			Set<T> trbackup = new HashSet<T>(aut.getTransition());
			Set<S> statesbackup= aut.getStates(); 
			S init = aut.getInitial();
			Set<S> R = new HashSet<S>(getDanglingStates(aut, statesbackup,init));//R0
			boolean update=false;
			do{
				final Set<S> Rf = new HashSet<S>(R); 
				final Set<T> trf= new HashSet<T>(aut.getTransition());

				if (aut.getTransition().removeAll(aut.getTransition().parallelStream()
						.filter(x->pruningPred.test(x,trf, Rf))
						.collect(Collectors.toSet()))) //Ki
					R.addAll(getDanglingStates(aut, statesbackup,init));

				R.addAll(trbackup.parallelStream() 
						.filter(x->forbiddenPred.test(x,trf, Rf))
						.map(Transition::getSource)
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
	private Set<S> getDanglingStates(Automaton<CS,CL,S,T> aut, Set<S> states, S initial)
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

	private void forwardVisit(Automaton<CS,CL,S,T> aut, S currentstate)
	{ 
		this.reachable.put(currentstate, true);  //currentstate.setReachable(true);
		aut.getForwardStar(currentstate).forEach(x->{
			if (!this.reachable.get(x.getTarget()))//!x.getTarget().isReachable())
				forwardVisit(aut,x.getTarget());
		});
	}

	private void backwardVisit(Automaton<CS,CL,S,T> aut, S currentstate)
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



//
// an attempt to perform a fixpoint computation with Stream.iterate failed because the synthesis is mutating 
// the automaton and because it does not simplify the code
//

///**
// * 
// * @param state
// * @return true if the successful value of state has changed
// */
//private boolean forwardNeighbourVisit(S state)
//{	
//	boolean b = state.isSuccessful();
//	state.setSuccessful(Arrays.stream(this.getTransitionsWithSource(state))
//			.map(FMCATransition::getTarget)
//			.anyMatch(S::isSuccessful));
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
//private boolean backwardNeighbourVisit(S state)
//{	
//	boolean b = state.isReachable();
//	
//	state.setReachable(Arrays.stream(this.getTransitionsWithTarget(state))
//			.map(FMCATransition::getSource)
//			.anyMatch(S::isReachable));
//	return b!=state.isReachable();
//	
//}	


