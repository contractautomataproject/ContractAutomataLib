package contractAutomata;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SynthesisFunction implements TriFunction<MSCA,TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>>, 
														TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>>,MSCA>{

	private Map<CAState,Boolean> reachable;
	private Map<CAState,Boolean> successful;

	@Override
	public MSCA apply(MSCA arg1, TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> arg2,
			TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> arg3) {
		{
			MSCA aut=copy(arg1);
			TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> pruningPred=arg2;
			TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> forbiddenPred=arg3;
			
			Set<MSCATransition> trbackup = new HashSet<MSCATransition>(aut.getTransition());
			Set<CAState> statesbackup= aut.getStates(); 
			CAState init = aut.getInitial();
			Set<CAState> R = new HashSet<CAState>(getDanglingStates(aut, statesbackup,init));//R0
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
	
	private MSCA copy(MSCA aut)
	{	
		Map<BasicState,BasicState> clonedstate = aut.getStates().stream()
				.flatMap(x->x.getState().stream())
				.distinct()
				.collect(Collectors.toMap(Function.identity(), s->new BasicState(s.getLabel(),s.isInit(),s.isFin())));

		Map<CAState,CAState> clonedcastates  = aut.getStates().stream()
				.collect(Collectors.toMap(Function.identity(), 
						x->new CAState(x.getState().stream()
								.map(s->clonedstate.get(s))
								.collect(Collectors.toList()),
								x.getX(),x.getY())));

		return new MSCA(aut.getTransition().stream()
				.map(t->new MSCATransition(clonedcastates.get(t.getSource()),
						t.getLabel().getClone(),
						clonedcastates.get(t.getTarget()),
						t.getModality()))
				.collect(Collectors.toSet()));
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
