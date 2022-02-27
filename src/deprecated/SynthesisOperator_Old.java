package deprecated;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.ModalTransition;
import io.github.davidebasile.contractautomata.operators.TriPredicate;

/**
 * Class implementing the abstract synthesis operator
 * 
 * @author Davide Basile
 *
 */
public class SynthesisOperator_Old implements UnaryOperator<ModalAutomaton<CALabel>>{

	private Map<CAState,Boolean> reachable;
	private Map<CAState,Boolean> successful;
	private TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>, Set<CAState>> pruningPred;
	private final TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>, Set<CAState>> forbiddenPred;
	private	Function<ModalAutomaton<CALabel>,ModalAutomaton<CALabel>> getAnnotatedAut=a->a;//.getCopy();

	/**
	 * 
	 * @param pruningPredicate  the pruning predicate 
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 */
	public SynthesisOperator_Old(TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>, Set<CAState>> pruningPredicate,
			TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>, Set<CAState>> forbiddenPredicate, 
			Predicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> req) {
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
//	public SynthesisOperator_Old(TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>, Set<CAState>> pruningPredicate,
//			TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>, Set<CAState>> forbiddenPredicate,
//			Predicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> req,
//		//  Automaton<L,V, S extends State<L>,T extends Transition<L,V,S,? extends Label<V>>>
//			Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>>  prop) {
//		this(pruningPredicate,forbiddenPredicate,req);
//		if (prop!=null)
//			getAnnotatedAut = a -> new ModelCheckingFunction().apply(a.relaxAsAutomaton(), ModelCheckingFunction.convert(prop));
//	}

	/**
	 * This constructor does not use any pruning predicate
	 * 
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
	 */
	public SynthesisOperator_Old(TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>, Set<CAState>> forbiddenPredicate, 
			Predicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> req) {
		this((x,t,bad) -> false, forbiddenPredicate,req);
	}

	/**
	 * This constructor does not use any pruning predicate
	 *   
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param prop another property to enforce expressed by an automaton
	 */
//	public SynthesisOperator_Old(TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>, Set<CAState>> forbiddenPredicate,
//			Predicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> req,
//			Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>>  prop) {
//		this((x,t,bad) -> false, forbiddenPredicate,req);
//		if (prop!=null)
//			getAnnotatedAut = a -> new ModelCheckingFunction().apply(a, prop);
//
//	}

	/** 
	 * invokes the synthesis
	 * @param arg1 the plant automaton to which the synthesis is performed
	 * @return the synthesised automaton
	 * 
	 */
	@Override
	public ModalAutomaton<CALabel> apply(ModalAutomaton<CALabel> arg1) {
		{
			//ModalAutomaton<CALabel> aut= new RelabelingOperator().apply(arg1);//creating an exact copy
			ModalAutomaton<CALabel> aut = getAnnotatedAut.apply(arg1);
			
			Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> trbackup = new HashSet<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>(aut.getTransition());
			Set<CAState> statesbackup= aut.getStates(); 
			CAState init = aut.getInitial();
			Set<CAState> R = new HashSet<CAState>(getDanglingStates(aut, statesbackup,init));//R0
		//	R.addAll(getForbiddenStates.apply(aut));		
			boolean update=false;
			do{
				final Set<CAState> Rf = new HashSet<CAState>(R); 
				final Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> trf= new HashSet<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>(aut.getTransition());

				if (aut.getTransition().removeAll(aut.getTransition().parallelStream()
						.filter(x->pruningPred.test(x,trf, Rf))
						.collect(Collectors.toSet()))) //Ki
					R.addAll(getDanglingStates(aut, statesbackup,init));

				R.addAll(trbackup.parallelStream() 
						.filter(x->forbiddenPred.test(x,trf, Rf))
						.map(ModalTransition<List<BasicState>,List<String>,CAState,CALabel>::getSource)
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
	private Set<CAState> getDanglingStates(ModalAutomaton<CALabel> aut, Set<CAState> states, CAState initial)
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

	private void forwardVisit(ModalAutomaton<CALabel> aut, CAState currentstate)
	{ 
		this.reachable.put(currentstate, true);  //currentstate.setReachable(true);
		aut.getForwardStar(currentstate).forEach(x->{
			if (!this.reachable.get(x.getTarget()))//!x.getTarget().isReachable())
				forwardVisit(aut,x.getTarget());
		});
	}

	private void backwardVisit(ModalAutomaton<CALabel> aut, CAState currentstate)
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


