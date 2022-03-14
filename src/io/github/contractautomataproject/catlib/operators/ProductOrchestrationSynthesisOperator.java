package io.github.contractautomataproject.catlib.operators;

import java.util.function.Predicate;

import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.family.Product;

/**
 * Class implenenting the orchestration synthesis for a specific product
 * 
 * @author Davide Basile
 *
 */
public class ProductOrchestrationSynthesisOperator  extends OrchestrationSynthesisOperator {
	private final Product p;
	
	/**
	 * 
	 * @param req the invariant to enforce (e.g. agreement or strong agreement)
	 * @param p  the product to synthesise
	 */
	public ProductOrchestrationSynthesisOperator(Predicate<CALabel> req,  Product p) {
		super(x->req.test(x)&&!p.isForbidden(x));
		this.p=p;
	}
	
	/**
	 * @param aut the plant automaton
	 * @return the synthesised orchestration of product p
	 */
	@Override
	public ModalAutomaton<CALabel> apply(ModalAutomaton<CALabel> aut)
	{
		ModalAutomaton<CALabel> a= super.apply(aut);

		if (a!=null&&!p.checkRequired(a.getTransition()))
			return null;
		
		return a;
	}
}
