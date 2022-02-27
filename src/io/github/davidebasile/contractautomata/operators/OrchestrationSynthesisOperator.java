package io.github.davidebasile.contractautomata.operators;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.ModalTransition;

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
		super((x,st,bad) -> isUncontrollableOrchestration(x,st, bad),req, null);
	}

	/**
	 * 
	 * @param req the invariant requirement (e.g. agreement)
	 * @param prop the property to enforce expressed as an automaton
	 */
	public OrchestrationSynthesisOperator(Predicate<CALabel> req, 
			Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>>  prop){
		super((x,st,bad) -> isUncontrollableOrchestration(x,st, bad),req, prop);
	}

	/**
	 * invokes the synthesis method for synthesising the orchestration
	 * @param aut the plant automaton
	 * @return the synthesised orchestration 
	 */
	@Override
	public ModalAutomaton<CALabel> apply(Automaton<List<BasicState>,List<String>,CAState,
			ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> aut)
	{
		if (aut.getTransition().parallelStream()
				.anyMatch(t-> !t.isPermitted()&&t.getLabel().isOffer()))
			throw new UnsupportedOperationException("The automaton contains necessary offers that are not allowed in the orchestration synthesis");

		return super.apply(aut);
	}


	private static boolean isUncontrollableOrchestration(ModalTransition<List<BasicState>,List<String>,CAState,CALabel> tra,Set<? extends ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> str, Set<CAState> badStates)
	{
		return 	tra.isUncontrollable(str,badStates, 
				(t,tt) -> (t.getLabel().getRequester().equals(tt.getLabel().getRequester()))//the same requesting principal
				&&(t.getSource().getState().get(t.getLabel().getRequester())
						.equals(tt.getSource().getState().get(tt.getLabel().getRequester())))//in the same local source state					
				&&(tt.getLabel().isRequest()&&t.getLabel().getTheAction().equals(tt.getLabel().getCoAction())|| 
						tt.getLabel().isMatch()&&t.getLabel().getTheAction().equals(tt.getLabel().getTheAction())));//doing the same request
	}


}
