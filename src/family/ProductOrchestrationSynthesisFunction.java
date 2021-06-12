package family;

import java.util.function.BiFunction;

import contractAutomata.MSCA;
import contractAutomata.SynthesisFunction;

public class ProductOrchestrationSynthesisFunction implements BiFunction<MSCA,Product,MSCA> {
	private final SynthesisFunction synth = new SynthesisFunction();
	
	/**
	 * @return the synthesised orchestration/mpc of product p in agreement
	 * 
	 */
	public MSCA apply(MSCA aut,Product p)
	{
		MSCA a= synth.apply(aut, (x,t,bad) -> 
		x.getLabel().isRequest()||bad.contains(x.getTarget())||p.isForbidden(x), 
		(x,t,bad) -> (!x.isUrgent()&&x.isUncontrollableOrchestration(t, bad))||(x.isUrgent()&&!t.contains(x)));

		if (a!=null&&!p.checkRequired(a.getTransition()))
			return null;
		
		return a;
	}

}
