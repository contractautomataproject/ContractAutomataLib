package io.github.contractautomata.catlib.operations;

import java.util.function.Predicate;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.family.Product;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.state.State;

/**
 * Class implementing the orchestration synthesis for a specific product of a product line. <br>
 * This is a further specialization of the orchestration synthesis where the requirement also checks  <br>
 * that an action must not be forbidden by the product, and in the resulting synthesised automaton   <br>
 * all required actions must be reachable (otherwise an empty orchestration is returned).  <br>
 *
 *
 * This operation is formally specified in Definition 14 of
 * <ul>
 *     <li>Basile, D. et al., 2020.
 *     Controller synthesis of service contracts with variability. Science of Computer Programming, vol. 187, pp. 102344.
 *      (<a href="https://doi.org/10.1016/j.scico.2019.102344">https://doi.org/10.1016/j.scico.2019.102344</a>)</li>
 * </ul>
 *
 * @param <S1> the type of the content of states
 *
 * @author Davide Basile
 *
 */
public class ProductOrchestrationSynthesisOperator<S1>  extends OrchestrationSynthesisOperator<S1> {
	private final Product p;
	
	/**
	 * The constructor for the product orchestration synthesis operator.
	 *
	 * @param req the invariant to enforce (e.g. agreement or strong agreement).
	 * @param p the product to use for the synthesis operation.
	 */
	public ProductOrchestrationSynthesisOperator(Predicate<CALabel> req,  Product p) {
		super(x->req.test(x)&&!p.isForbidden(x));
		this.p=p;
	}
	
	/**
	 * Apply the product orchestration synthesis operator to aut.
	 * @param aut the plant automaton to which the synthesis is applied.
	 * @return the synthesised orchestration of product p
	 */
	@Override
	public Automaton<S1, Action,State<S1>, ModalTransition<S1,Action,State<S1>,CALabel>> apply(Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>> aut)
	{
		Automaton<S1, Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>> a= super.apply(aut);

		if (a!=null&&!p.checkRequired(a.getTransition()))
			return null;
		
		return a;
	}
}
