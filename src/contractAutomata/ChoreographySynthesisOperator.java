package contractAutomata;

import java.util.HashSet;
import java.util.Set;
import java.util.function.UnaryOperator;

public class ChoreographySynthesisOperator implements UnaryOperator<MSCA> {

	private final SynthesisFunction synth = new SynthesisFunction();

	/** 
	 * invokes the synthesis method for synthesising the choreography in strong agreement
	 * @return the synthesised choreography in strong agreement, removing only one transition violating the branching condition 
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
		MSCA chor;
		do 
		{ 
			chor = synth.apply(aut,(x,t,bad) -> !x.getLabel().isMatch()||bad.contains(x.getTarget())||violatingbc.contains(x.toCSV()),
					(x,t,bad) -> (!t.contains(x)&&x.isUncontrollableChoreography(t, bad)));
			if (chor==null)
				break;
			final Set<MSCATransition> trf = chor.getTransition();
			toRemove=(chor.getTransition().parallelStream()
					.filter(x->!x.satisfiesBranchingCondition(trf, new HashSet<CAState>()))
					.findAny() 
					.orElse(null));
		} while (toRemove!=null && violatingbc.add(toRemove.toCSV()));
		return chor;
	}

}
