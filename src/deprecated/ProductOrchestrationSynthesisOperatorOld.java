//package deprecated;
//
//import java.util.function.Predicate;
//import java.util.function.UnaryOperator;
//
//import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
//import io.github.davidebasile.contractautomata.automaton.label.CALabel;
//import io.github.davidebasile.contractautomata.family.Product;
//import io.github.davidebasile.contractautomata.operators.OrchestrationSynthesisOperator;
//
///**
// * Class implenenting the orchestration synthesis for a specific product
// * 
// * @author Davide Basile
// *
// */
//public class ProductOrchestrationSynthesisOperatorOld  implements UnaryOperator<ModalAutomaton<CALabel>> {
//	private final OrchestrationSynthesisOperator synth;
//	private final Product p;
//	
//	/**
//	 * 
//	 * @param req the invariant to enforce (e.g. agreement or strong agreement)
//	 * @param p  the product to synthesise
//	 */
//	public ProductOrchestrationSynthesisOperatorOld(Predicate<CALabel> req, Product p) {
//		this.p=p;
//		this.synth=new OrchestrationSynthesisOperator(x->req.test(x)&&!p.isForbidden(x));
//	}
//	
//	/**
//	 * @param aut the plant automaton
//	 * @return the synthesised orchestration of product p
//	 */
//	public ModalAutomaton<CALabel> apply(ModalAutomaton<CALabel> aut)
//	{
//		ModalAutomaton<CALabel> a= synth.apply(aut);
//
//		if (a!=null&&!p.checkRequired(a.getTransition()))
//			return null;
//		
//		return a;
//	}
//
//
//}
