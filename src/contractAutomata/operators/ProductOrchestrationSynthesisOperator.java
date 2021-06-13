package contractAutomata.operators;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import contractAutomata.MSCA;
import contractAutomata.MSCATransition;
import family.Product;

public class ProductOrchestrationSynthesisOperator  implements UnaryOperator<MSCA> {
	private final OrchestrationSynthesisOperator synth;
	private Product p;
	
	public ProductOrchestrationSynthesisOperator(Predicate<MSCATransition> req, Product p) {
		this.p=p;
		this.synth=new OrchestrationSynthesisOperator(x->req.test(x)&&!p.isForbidden(x));
	}
	
	/**
	 * @return the synthesised orchestration/mpc of product p in agreement
	 */
	public MSCA apply(MSCA aut)
	{
		MSCA a= synth.apply(aut);

		if (a!=null&&!p.checkRequired(a.getTransition()))
			return null;
		
		return a;
	}


}
