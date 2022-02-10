package io.github.davidebasile.contractautomata.operators;

import java.util.Set;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.MSCATransition;
import io.github.davidebasile.contractautomata.automaton.transition.Transition;

/**
 * Class implementing the orchestration synthesis operator
 * 
 * @author Davide Basile
 *
 */
public class OrchestrationSynthesisOperator implements UnaryOperator<MSCA> {

	private final SynthesisOperator synth;

	/**
	 * 
	 * @param req the invariant requirement (e.g. agreement)
	 */
	public OrchestrationSynthesisOperator(Predicate<MSCATransition> req)
	{
		this.synth = new SynthesisOperator((x,st,bad) -> isUncontrollableOrchestration(x,st, bad),req);
	}
	
	/**
	 * 
	 * @param req the invariant requirement (e.g. agreement)
	 * @param prop the property to enforce expressed as an automaton
	 */
	public OrchestrationSynthesisOperator(Predicate<MSCATransition> req, 
			Automaton<String,String,BasicState,Transition<String,String,BasicState,Label<String>>>  prop)
	{
		this.synth = new SynthesisOperator((x,st,bad) -> isUncontrollableOrchestration(x,st, bad),req, prop);
	}
	
	/**
	 * invokes the synthesis method for synthesising the orchestration
	 * @param aut the plant automaton
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
