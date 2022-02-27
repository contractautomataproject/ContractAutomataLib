package deprecated;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.ModalTransition;
import io.github.davidebasile.contractautomata.automaton.transition.Transition;
import io.github.davidebasile.contractautomata.operators.RelabelingOperator;
import io.github.davidebasile.contractautomata.operators.TriPredicate;

/**
 * Class implementing the abstract synthesis operator
 * 
 * @author Davide Basile
 *
 */
public class SynthesisOperatorOld2<L extends Label<List<String>>> implements UnaryOperator<ModalAutomaton<L>>{

	private Map<CAState,Boolean> reachable;
	private Map<CAState,Boolean> successful;
	private final Function<List<String>,L> createLabel;
	private TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,L>, Set<ModalTransition<List<BasicState>,List<String>,CAState,L>>, Set<CAState>> pruningPred;
	private final TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,L>, Set<ModalTransition<List<BasicState>,List<String>,CAState,L>>, Set<CAState>> forbiddenPred;

	/**
	 * 
	 * @param pruningPredicate  the pruning predicate 
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 */
	public SynthesisOperatorOld2(TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,L>, Set<ModalTransition<List<BasicState>,List<String>,CAState,L>>, Set<CAState>> pruningPredicate,
			TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,L>, Set<ModalTransition<List<BasicState>,List<String>,CAState,L>>, Set<CAState>> forbiddenPredicate, 
			Predicate<L> req, 
			Function<List<String>,L> createLabel) {
		super();
		this.createLabel=createLabel;
		this.pruningPred = (x,t,bad) -> bad.contains(x.getTarget())|| !req.test(x.getLabel()) || pruningPredicate.test(x, t, bad);
		this.forbiddenPred = (x,t,bad) -> !t.contains(x)&&forbiddenPredicate.test(x, t, bad);
	}


	/**
	 * This constructor does not use any pruning predicate
	 * 
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
	 */
	public SynthesisOperatorOld2(TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,L>, Set<ModalTransition<List<BasicState>,List<String>,CAState,L>>, Set<CAState>> forbiddenPredicate, 
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
	public ModalAutomaton<L> apply(ModalAutomaton<L> arg1) {
		{
			ModalAutomaton<L> aut= new RelabelingOperator<L>(createLabel).apply(arg1);
			//creating an exact copy
			//Automaton<CS,CL,S,T> aut = arg1.getCopy();
			
			
			Set<ModalTransition<List<BasicState>,List<String>,CAState,L>> trbackup = new HashSet<>(aut.getTransition());
			Set<CAState> statesbackup= aut.getStates(); 
			CAState init = aut.getInitial();
			Set<CAState> R = new HashSet<>(getDanglingStates(aut, statesbackup,init));//R0
			boolean update=false;
			do{
				final Set<CAState> Rf = new HashSet<>(R); 
				final Set<ModalTransition<List<BasicState>,List<String>,CAState,L>> trf= new HashSet<>(aut.getTransition());

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
	private Set<CAState> getDanglingStates(ModalAutomaton<L> aut, Set<CAState> states, CAState initial)
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

	private void forwardVisit(ModalAutomaton<L> aut, CAState currentstate)
	{ 
		this.reachable.put(currentstate, true);  //currentstate.setReachable(true);
		aut.getForwardStar(currentstate).forEach(x->{
			if (!this.reachable.get(x.getTarget()))//!x.getTarget().isReachable())
				forwardVisit(aut,x.getTarget());
		});
	}

	private void backwardVisit(ModalAutomaton<L> aut, CAState currentstate)
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


