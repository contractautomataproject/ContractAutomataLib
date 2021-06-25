package contractAutomata.operators;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import contractAutomata.automaton.Automaton;
import contractAutomata.automaton.MSCA;
import contractAutomata.automaton.label.Label;
import contractAutomata.automaton.state.BasicState;
import contractAutomata.automaton.transition.MSCATransition;
import contractAutomata.automaton.transition.Transition;

public class MpcSynthesisOperator implements UnaryOperator<MSCA> {
		
	private final SynthesisOperator synth;

	public MpcSynthesisOperator(Predicate<MSCATransition> req) {
		super();
		this.synth = new SynthesisOperator((x,t,bad) -> x.isUrgent(), req);
	}	
	
	
	public MpcSynthesisOperator(Predicate<MSCATransition> req,	 
			Automaton<String,BasicState,Transition<String,BasicState,Label>>  prop)
	{
		super();
		this.synth = new SynthesisOperator((x,t,bad) -> x.isUrgent(), req, prop);
	}	
	

	/**
	 * invokes the synthesis method for synthesising the mpc
	 * @return the synthesised most permissive controller
	 */
	@Override
	public MSCA apply(MSCA aut) {
		if (aut.getTransition().parallelStream()
				.anyMatch(t-> t.isLazy()))
			throw new UnsupportedOperationException("The automaton contains semi-controllable transitions");

		return synth.apply(aut);

	}

}
