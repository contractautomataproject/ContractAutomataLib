package io.github.davidebasile.contractautomata.operators;

import java.util.List;
import java.util.function.Predicate;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.ModalTransition;

/**
 * Class implementing the mpc operator
 * @author Davide Basile
 *
 */
public class MpcSynthesisOperator extends ModelCheckingSynthesisOperator
{

	/**
	 * 
	 * @param req the invariant requirement (e.g. agreement)
	 */
	public MpcSynthesisOperator(Predicate<CALabel> req) {
		super((x,t,bad) -> x.isUrgent(), req, null);
	}	
	
	
	/**
	 * 
	 * @param req the invariant requirement (e.g. agreement)
	 * @param prop the property to enforce expressed as an automaton
	 */
	public MpcSynthesisOperator(Predicate<CALabel> req,	 
			Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>>  prop)
	{
		super((x,t,bad) -> x.isUrgent(), req, prop);
	}	
	

	/**
	 * invokes the synthesis method for synthesising the mpc
	 * @param aut the plant automaton
	 * @return the synthesised most permissive controller
	 */
	@Override
	public ModalAutomaton<CALabel> apply(Automaton<List<BasicState>,List<String>,CAState,
			ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> aut) {

		if (aut.getTransition().parallelStream()
				.anyMatch(t-> t.isLazy()))
			throw new UnsupportedOperationException("The automaton contains semi-controllable transitions");
		
		return super.apply(aut);
	}

}
