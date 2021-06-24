package contractAutomata.operators;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import contractAutomata.automaton.MSCA;
import contractAutomata.automaton.transition.MSCATransition;

public class MpcSynthesisOperator implements UnaryOperator<MSCA> {
		
	private final SynthesisOperator synth;

	public MpcSynthesisOperator(Predicate<MSCATransition> req) {
		super();
		this.synth = new SynthesisOperator((x,t,bad) -> bad.contains(x.getTarget())|| !req.test(x), 
				(x,t,bad) -> x.isUrgent()&&!t.contains(x));
	}	
	
	/**
	 * invokes the synthesis method for synthesising the mpc in agreement
	 * @return the synthesised most permissive controller in agreement
	 */
	@Override
	public MSCA apply(MSCA aut) {
		if (aut.getTransition().parallelStream()
				.anyMatch(t-> t.isLazy()))
			throw new UnsupportedOperationException("The automaton contains semi-controllable transitions");

		return synth.apply(aut);

	}

}
