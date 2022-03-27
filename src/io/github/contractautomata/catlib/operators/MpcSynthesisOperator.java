package io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;

import java.util.function.Predicate;

/**
 * Class implementing the mpc operator
 * @author Davide Basile
 *
 */
public class MpcSynthesisOperator<S1> extends ModelCheckingSynthesisOperator<S1,State<S1>,CALabel,
		ModalTransition<S1,Action,State<S1>,CALabel>,
		Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>>,
		Label<Action>,
		ModalTransition<S1,Action,State<S1>,Label<Action>>,
		Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,Label<Action>>>>
{

	/**
	 *
	 * @param req the invariant requirement (e.g. agreement)
	 */
	public MpcSynthesisOperator(Predicate<CALabel> req) {
		super((x,t,bad) -> x.isUrgent(), req,
				Automaton::new,CALabel::new,ModalTransition::new,State::new);
	}



	/**
	 *
	 * @param req the invariant requirement (e.g. agreement)
	 * @param prop the property to enforce expressed as an automaton
	 */
	public MpcSynthesisOperator(Predicate<CALabel> req,
								Automaton<S1,Action,State<S1>, ModalTransition<S1,Action,State<S1>,Label<Action>>> prop)
	{
		super((x,t,bad) -> x.isUrgent(), req, prop,t->new CALabel(t.getRank(),t.getRequester(),t.getCoAction()),
				Automaton::new,CALabel::new,ModalTransition::new,State::new,Label::new,ModalTransition::new,Automaton::new);
	}
	

	/**
	 * invokes the synthesis method for synthesising the mpc
	 * @param aut the plant automaton
	 * @return the synthesised most permissive controller
	 */
	@Override
	public Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>> apply(Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>> aut) {

		if (aut.getTransition().parallelStream()
				.anyMatch(ModalTransition::isLazy))
			throw new UnsupportedOperationException("The automaton contains semi-controllable transitions");
		
		return super.apply(aut);
	}

}
