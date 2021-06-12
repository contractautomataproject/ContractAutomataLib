package contractAutomata;

import java.util.function.UnaryOperator;

public class MpcSynthesisOperator implements UnaryOperator<MSCA> {
		
	private final SynthesisFunction synth = new SynthesisFunction();

	/**
	 * invokes the synthesis method for synthesising the mpc in agreement
	 * @return the synthesised most permissive controller in agreement
	 */
	@Override
	public MSCA apply(MSCA aut) {
		if (aut.getTransition().parallelStream()
				.anyMatch(t-> t.isLazy()))
			throw new UnsupportedOperationException("The automaton contains semi-controllable transitions");

		return synth.apply(aut,(x,t,bad) -> bad.contains(x.getTarget())|| x.getLabel().isRequest(), 
				(x,t,bad) -> x.isUrgent()&&!t.contains(x));

	}

}
