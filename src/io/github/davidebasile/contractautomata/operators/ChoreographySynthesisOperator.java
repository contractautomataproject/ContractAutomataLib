package io.github.davidebasile.contractautomata.operators;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.ModalTransition;

/**
 * Class implementing the Choreography Synthesis
 * 
 * @author Davide Basile
 *
 */
public class ChoreographySynthesisOperator implements UnaryOperator<ModalAutomaton<CALabel>> {

	private Predicate<CALabel> req;
	private Function<Stream<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>,Optional<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>> choice=Stream::findAny;
	private Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>> prop=null;
	
	public ChoreographySynthesisOperator(Predicate<CALabel> req){
		this.req=req;
	}
	
	public ChoreographySynthesisOperator(Predicate<CALabel> req, 
			Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>>  prop){
		this(req);
		this.prop=prop;
	}
	
	public ChoreographySynthesisOperator(Predicate<CALabel> req, 
			Function<Stream<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>,Optional<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>> choice){
		this(req);
		this.choice=choice;
	}

	/** 
	 * invokes the synthesis method for synthesising the choreography
	 * @param aut the plant automaton to which the synthesis is performed
	 * @return the synthesised choreography, removing only one transition violating the branching condition 
	 * each time no further updates are possible. The transition to remove is chosen nondeterministically.
	 * 
	 */
	@Override
	public ModalAutomaton<CALabel> apply(ModalAutomaton<CALabel> aut)
	{
		if (aut.getTransition().parallelStream()
				.anyMatch(t-> !t.isPermitted()&&t.getLabel().isRequest()))
			throw new UnsupportedOperationException("The automaton contains necessary requests that are not allowed in the choreography synthesis");

		ModalTransition<List<BasicState>,List<String>,CAState,CALabel> toRemove=null; 
		Set<String> violatingbc = new HashSet<>();

		ModelCheckingSynthesisOperator synth;
		ModalAutomaton<CALabel> chor;
		do 
		{ 
			synth=new ModelCheckingSynthesisOperator((x,t,bad) -> violatingbc.contains(x.toCSV()),
						(x,st,bad) -> isUncontrollableChoreography(x,st, bad),req,prop);
			chor = synth.apply(aut);
			if (chor==null)
				break;
			final Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> trf = chor.getTransition();
			toRemove=choice.apply(chor.getTransition().parallelStream()
					.filter(x->!satisfiesBranchingCondition(x,trf, new HashSet<CAState>())))
					.orElse(null);
		} while (toRemove!=null && violatingbc.add(toRemove.toCSV()));
		return chor;
	}

	private static boolean  isUncontrollableChoreography(ModalTransition<List<BasicState>,List<String>,CAState,CALabel> tra, Set<? extends ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> str, Set<CAState> badStates)
	{
		return 	tra.isUncontrollable(str,badStates, 
				(t,tt) -> t.getLabel().getOfferer().equals(tt.getLabel().getOfferer())//the same offerer
				&&t.getLabel().getTheAction().equals(tt.getLabel().getTheAction()) //the same offer 
				&&t.getSource().equals(tt.getSource()));//the same global source state
	}

	/**
	 * @param tra the transition to check
	 * @param trans the set of transitions to check against tra
	 * @param bad  the set of bad (dangling) states to check
	 * @return true if the set of transitions and bad states violate the branching condition
	 */
	public boolean satisfiesBranchingCondition(ModalTransition<List<BasicState>,List<String>,CAState,CALabel> tra, Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> trans, Set<CAState> bad) 
	{
//		if (!req.test(tra)||bad.contains(tra.getSource()) || bad.contains(tra.getTarget()))
//			return false;		//ignore tra transition because it is going to be pruned in the synthesis

		final Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> ftr = trans.parallelStream()
				.filter(x->req.test(x.getLabel())&&!bad.contains(x.getSource())&&!bad.contains(x.getTarget()))
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








///**
// * @return the synthesised choreography in strong agreement, 
// * removing at each iteration all transitions violating branching condition.
// */
//public FMCA choreographySmaller()
//{
//	return synthesis(x-> {return (t,bad) -> 
//				!x.isMatch()||bad.contains(x.getTarget())||!x.satisfiesBranchingCondition(t, bad);},
//			x -> {return (t,bad) -> bad.contains(x.getTarget())&&x.isUncontrollableChoreography(t, bad);});
//}
