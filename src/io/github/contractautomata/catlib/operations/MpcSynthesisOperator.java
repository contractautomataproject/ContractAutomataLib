package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;

import java.util.function.Predicate;

/**
 * Class implementing the most permissive controller synthesis operator.<br>
 *
 *	The implemented algorithm is formally specified in Definition 2.3 and Theorem 5.3 of
 * <ul>
 *     <li>Basile, D., et al., 2020.
 *      Synthesis of Orchestrations and Choreographies: Bridging the Gap between Supervisory Control and Coordination of Services. Logical Methods in Computer Science, vol. 16(2), pp. 9:1 - 9:29.
 *      (<a href="https://doi.org/10.23638/LMCS-16(2:9)2020">https://doi.org/10.23638/LMCS-16(2:9)2020</a>)</li>
 * </ul>
 *
 * @param <S1> the type of the content of states
 * @author Davide Basile
 *
 */
public class MpcSynthesisOperator<S1> extends ModelCheckingSynthesisOperator<S1, State<S1>, CALabel,
		ModalTransition<S1, Action,State<S1>,CALabel>,
		Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>>,
		Label<Action>,
		ModalTransition<S1,Action,State<S1>,Label<Action>>,
		Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,Label<Action>>>>
{

	/**
	 * Constructor for the mpc synthesis enforcing the requirement req.
	 *
	 * @param req the invariant requirement (e.g. agreement)
	 */
	public MpcSynthesisOperator(Predicate<CALabel> req) {
		super((x,t,bad) -> x.isUrgent(), req,
				Automaton::new,CALabel::new,ModalTransition::new,State::new);
	}



	/**
	 * Constructor for the mpc synthesis enforcing the requirement req and property prop.
	 *
	 * @param req the invariant requirement (e.g. agreement)
	 * @param prop the property to enforce expressed as an automaton
	 */
	public MpcSynthesisOperator(Predicate<CALabel> req,
								Automaton<S1,Action,State<S1>, ModalTransition<S1,Action,State<S1>,Label<Action>>> prop)
	{
		super((x,t,bad) -> x.isUrgent(), req, prop,
				t->new CALabel(t.getRank(),t.getRequester(),t.getCoAction()),
				Automaton::new,CALabel::new,ModalTransition::new,State::new,Label::new,ModalTransition::new,Automaton::new);
	}
	

	/**
	 * Applies  the mpc synthesis to aut.
	 * The argument must not contain lazy transitions.
	 *
	 * @param aut the plant automaton to which the synthesis is applied
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
