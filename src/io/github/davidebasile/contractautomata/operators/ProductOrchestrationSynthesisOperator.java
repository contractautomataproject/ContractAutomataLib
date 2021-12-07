package io.github.davidebasile.contractautomata.operators;

import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.transition.MSCATransition;
import io.github.davidebasile.contractautomata.family.Product;

/**
 * Class implenenting the orchestration synthesis for a specific product
 * 
 * @author Davide Basile
 *
 */
public class ProductOrchestrationSynthesisOperator  implements UnaryOperator<MSCA> {
	private final OrchestrationSynthesisOperator synth;
	private final Product p;
	
	/**
	 * 
	 * @param req the invariant to enforce (e.g. agreement or strong agreement)
	 * @param p  the product to synthesise
	 */
	public ProductOrchestrationSynthesisOperator(Predicate<MSCATransition> req, Product p) {
		this.p=p;
		this.synth=new OrchestrationSynthesisOperator(x->req.test(x)&&!p.isForbidden(x));
	}
	
	/**
	 * @param aut the plant automaton
	 * @return the synthesised orchestration of product p
	 */
	public MSCA apply(MSCA aut)
	{
		MSCA a= synth.apply(aut);

		if (a!=null&&!p.checkRequired(a.getTransition()))
			return null;
		
		return a;
	}


}
