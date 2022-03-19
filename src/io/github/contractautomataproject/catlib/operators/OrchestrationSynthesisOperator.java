package io.github.contractautomataproject.catlib.operators;

import java.util.Set;
import java.util.function.Predicate;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;

/**
 * Class implementing the orchestration synthesis operator
 * 
 * @author Davide Basile
 *
 */
public class OrchestrationSynthesisOperator extends ModelCheckingSynthesisOperator {

	/**
	 * 
	 * @param req the invariant requirement (e.g. agreement)
	 */
	public OrchestrationSynthesisOperator(Predicate<CALabel> req){
		super(OrchestrationSynthesisOperator::isUncontrollableOrchestration,req, null, null,null);
	}

	/**
	 * 
	 * @param req the invariant requirement (e.g. agreement)
	 * @param prop the property to enforce expressed as an automaton
	 */
	public OrchestrationSynthesisOperator(Predicate<CALabel> req, 
			Predicate<Label<String>> reqmc,
			Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>> prop){
		super(OrchestrationSynthesisOperator::isUncontrollableOrchestration,req, reqmc, prop, 
				t->new CALabel(t.getRank(),t.getRequester(),t.getCoAction()));
	}

	/**
	 * invokes the synthesis method for synthesising the orchestration
	 * @param aut the plant automaton
	 * @return the synthesised orchestration 
	 */
	@Override
	public Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> apply(Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut)
	{
		if (aut.getTransition().parallelStream()
				.anyMatch(t-> !t.isPermitted()&&t.getLabel().isOffer()))
			throw new UnsupportedOperationException("The automaton contains necessary offers that are not allowed in the orchestration synthesis");

		return super.apply(aut);
	}


	private static boolean isUncontrollableOrchestration(ModalTransition<String,String,State<String>,CALabel> tra,Set<? extends ModalTransition<String,String,State<String>,CALabel>> str, Set<State<String>> badStates)
	{
		return 	tra.isUncontrollable(str,badStates, 
				(t,tt) -> (t.getLabel().getRequester().equals(tt.getLabel().getRequester()))//the same requesting principal
				&&(t.getSource().getState().get(t.getLabel().getRequester())
						.equals(tt.getSource().getState().get(tt.getLabel().getRequester())))//in the same local source state					
				&&(tt.getLabel().isRequest()&&t.getLabel().getPrincipalAction().equals(tt.getLabel().getCoAction())|| 
						tt.getLabel().isMatch()&&t.getLabel().getPrincipalAction().equals(tt.getLabel().getPrincipalAction())));//doing the same request
	}
}