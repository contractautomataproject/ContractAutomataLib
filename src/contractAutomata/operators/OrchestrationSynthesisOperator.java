package contractAutomata.operators;

import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import contractAutomata.automaton.MSCA;
import contractAutomata.automaton.state.CAState;
import contractAutomata.automaton.transition.MSCATransition;

public class OrchestrationSynthesisOperator implements UnaryOperator<MSCA> {

	private final SynthesisOperator synth;

	public OrchestrationSynthesisOperator(Predicate<MSCATransition> req)
	{
		this.synth = new SynthesisOperator((x,st,bad) -> bad.contains(x.getTarget())|| !req.test(x), 
				(x,st,bad) -> //(x.isUrgent()&&!t.contains(x))||(!x.isUrgent()&&
		!st.contains(x)&&isUncontrollableOrchestration(x,st, bad));
	}
	
	/**
	 * invokes the synthesis method for synthesising the orchestration
	 * @return the synthesised orchestration 
	 */
	@Override
	public MSCA apply(MSCA aut)
	{
		if (aut.getTransition().parallelStream()
				.anyMatch(t-> !t.isPermitted()&&t.getLabel().isOffer()))
			throw new UnsupportedOperationException("The automaton contains necessary offers that are not allowed in the orchestration synthesis");

		return synth.apply(aut);
	}


	private boolean isUncontrollableOrchestration(MSCATransition tra,Set<? extends MSCATransition> str, Set<CAState> badStates)
	{
		return 	tra.isUncontrollable(str,badStates, 
				(t,tt) -> (t.getLabel().getRequester().equals(tt.getLabel().getRequester()))//the same requesting principal
				&&(t.getSource().getState().get(t.getLabel().getRequester())
						.equals(tt.getSource().getState().get(tt.getLabel().getRequester())))//in the same local source state					
				&&(tt.getLabel().isRequest()&&t.getLabel().getAction().equals(tt.getLabel().getCoAction())|| 
						tt.getLabel().isMatch()&&t.getLabel().getAction().equals(tt.getLabel().getAction())));//doing the same request
	}


}
