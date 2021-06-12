package contractAutomata;

import java.util.Set;
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

		return synth.apply(aut,(x,st,bad) -> bad.contains(x.getTarget())|| x.getLabel().isRequest(), 
				(x,st,bad) -> //(x.isUrgent()&&!t.contains(x))||(!x.isUrgent()&&
		!st.contains(x)&&isUncontrollableOrchestration(x,st, bad));
	}


	public boolean isUncontrollableOrchestration(MSCATransition tra,Set<? extends MSCATransition> str, Set<CAState> badStates)
	{
		return 	tra.isUncontrollable(str,badStates, 
				(t,tt) -> (t.getLabel().getRequester().equals(tt.getLabel().getRequester()))//the same requesting principal
				&&(t.getSource().getState().get(t.getLabel().getRequester())
						.equals(tt.getSource().getState().get(tt.getLabel().getRequester())))//in the same local source state					
				&&(tt.getLabel().isRequest()&&t.getLabel().getAction().equals(tt.getLabel().getCoAction())|| 
						tt.getLabel().isMatch()&&t.getLabel().getAction().equals(tt.getLabel().getAction())));//doing the same request
	}


}
