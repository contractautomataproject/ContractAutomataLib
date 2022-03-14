package io.github.contractautomataproject.catlib.operators;

import java.util.List;
import java.util.function.Predicate;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

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
		super((x,t,bad) -> x.isUrgent(), req, null, null, null);
	}	
	
	
	/**
	 * 
	 * @param req the invariant requirement (e.g. agreement)
	 * @param prop the property to enforce expressed as an automaton
	 */
	public MpcSynthesisOperator(Predicate<CALabel> req, Predicate<Label<List<String>>> reqmc,	 
			Automaton<String,String,BasicState<String>,ModalTransition<String,String,BasicState<String>,Label<String>>>  prop)
	{
		super((x,t,bad) -> x.isUrgent(), req, reqmc, prop,t->new CALabel(t.getRank(),t.getRequester(),t.getCoAction()));
	}	
	

	/**
	 * invokes the synthesis method for synthesising the mpc
	 * @param aut the plant automaton
	 * @return the synthesised most permissive controller
	 */
	@Override
	public ModalAutomaton<CALabel> apply(ModalAutomaton<CALabel> aut) {

		if (aut.getTransition().parallelStream()
				.anyMatch(ModalTransition::isLazy))
			throw new UnsupportedOperationException("The automaton contains semi-controllable transitions");
		
		return super.apply(aut);
	}

}
