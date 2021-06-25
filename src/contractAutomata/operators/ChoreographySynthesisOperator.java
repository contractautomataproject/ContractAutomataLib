package contractAutomata.operators;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import contractAutomata.automaton.Automaton;
import contractAutomata.automaton.MSCA;
import contractAutomata.automaton.label.Label;
import contractAutomata.automaton.state.BasicState;
import contractAutomata.automaton.state.CAState;
import contractAutomata.automaton.transition.MSCATransition;
import contractAutomata.automaton.transition.Transition;

public class ChoreographySynthesisOperator implements UnaryOperator<MSCA> {

	private Predicate<MSCATransition> req;
	private Function<Stream<MSCATransition>,Optional<MSCATransition>> choice=Stream::findAny;
	private Automaton<String,BasicState,Transition<String,BasicState,Label>>  prop=null;
	
	public ChoreographySynthesisOperator(Predicate<MSCATransition> req){
		this.req=req;
	}
	
	public ChoreographySynthesisOperator(Predicate<MSCATransition> req, 
			Automaton<String,BasicState,Transition<String,BasicState,Label>>  prop){
		this(req);
		this.prop=prop;
	}
	
	public ChoreographySynthesisOperator(Predicate<MSCATransition> req, 
			Function<Stream<MSCATransition>,Optional<MSCATransition>> choice){
		this(req);
		this.choice=choice;
	}

	/** 
	 * invokes the synthesis method for synthesising the choreography
	 * @return the synthesised choreography, removing only one transition violating the branching condition 
	 * each time no further updates are possible. The transition to remove is chosen nondeterministically with findAny().
	 * 
	 */
	@Override
	public MSCA apply(MSCA aut)
	{
		if (aut.getTransition().parallelStream()
				.anyMatch(t-> !t.isPermitted()&&t.getLabel().isRequest()))
			throw new UnsupportedOperationException("The automaton contains necessary requests that are not allowed in the choreography synthesis");

		MSCATransition toRemove=null; 
		Set<String> violatingbc = new HashSet<>();

		SynthesisOperator synth;
		MSCA chor;
		do 
		{ 
			synth=new SynthesisOperator((x,t,bad) -> violatingbc.contains(x.toCSV()),
						(x,st,bad) -> isUncontrollableChoreography(x,st, bad),req,prop);

			chor = synth.apply(aut);
			if (chor==null)
				break;
			final Set<MSCATransition> trf = chor.getTransition();
			toRemove=choice.apply(chor.getTransition().parallelStream()
					.filter(x->!satisfiesBranchingCondition(x,trf, new HashSet<CAState>())))
					.orElse(null);
		} while (toRemove!=null && violatingbc.add(toRemove.toCSV()));
		return chor;
	}

	private boolean  isUncontrollableChoreography(MSCATransition tra, Set<? extends MSCATransition> str, Set<CAState> badStates)
	{
		return 	tra.isUncontrollable(str,badStates, 
				(t,tt) -> t.getLabel().getOfferer().equals(tt.getLabel().getOfferer())//the same offerer
				&&t.getLabel().getAction().equals(tt.getLabel().getAction()) //the same offer 
				&&t.getSource().equals(tt.getSource()));//the same global source state
	}

	/**
	 * 
	 * @param trans the set of transitions to check
	 * @param bad  the set of bad (dangling) states
	 * @return true if the set of transitions and bad states violate the branching condition
	 */
	public boolean satisfiesBranchingCondition(MSCATransition tra, Set<MSCATransition> trans, Set<CAState> bad) 
	{
//		if (!req.test(tra)||bad.contains(tra.getSource()) || bad.contains(tra.getTarget()))
//			return false;		//ignore tra transition because it is going to be pruned in the synthesis

		final Set<MSCATransition> ftr = trans.parallelStream()
				.filter(x->req.test(x)&&!bad.contains(x.getSource())&&!bad.contains(x.getTarget()))
				.collect(Collectors.toSet()); //only valid candidates

		return ftr.parallelStream()
				.map(x->x.getSource())
				.filter(x->x!=tra.getSource()&&
				tra.getSource().getState().get(tra.getLabel().getOfferer()).getState()
				.equals(x.getState().get(tra.getLabel().getOfferer()).getState()))
				//it's not the same state of tra but sender is in the same state of this

				.allMatch(s -> ftr.parallelStream()
						.anyMatch(x->x.getSource()==s && tra.getLabel().equals(x.getLabel()))
						//for all such states there exists an outgoing transition with the same label of tra
						);
	}
}
