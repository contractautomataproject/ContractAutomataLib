package contractAutomata;

import java.util.function.UnaryOperator;

public class OrchestrationSynthesisOperator implements UnaryOperator<MSCA> {

	private final SynthesisFunction synth = new SynthesisFunction();

	/**
	 * invokes the synthesis method for synthesising the orchestration in agreement
	 * @return the synthesised orchestration in agreement
	 */
	@Override
	public MSCA apply(MSCA aut)
	{
		if (aut.getTransition().parallelStream()
				.anyMatch(t-> !t.isPermitted()&&t.getLabel().isOffer()))
			throw new UnsupportedOperationException("The automaton contains necessary offers that are not allowed in the orchestration synthesis");

		return synth.apply(aut,(x,t,bad) -> bad.contains(x.getTarget())|| x.getLabel().isRequest(), 
				(x,t,bad) -> //(x.isUrgent()&&!t.contains(x))||(!x.isUrgent()&&
		!t.contains(x)&&x.isUncontrollableOrchestration(t, bad));
	}


}
