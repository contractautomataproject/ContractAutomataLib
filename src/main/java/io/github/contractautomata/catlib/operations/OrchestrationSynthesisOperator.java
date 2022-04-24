package io.github.contractautomata.catlib.operations;

import java.util.Set;
import java.util.function.Predicate;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;

/**
 * Class implementing the orchestration synthesis operator.<br>
 *
 *	The implemented algorithm is formally specified in Definition 3.2 and Theorem 5.4 of
 * <ul>
 *     <li>Basile, D., et al., 2020.
 *      Synthesis of Orchestrations and Choreographies: Bridging the Gap between Supervisory Control and Coordination of Services. Logical Methods in Computer Science, vol. 16(2), pp. 9:1 - 9:29.
 *      (<a href="https://doi.org/10.23638/LMCS-16(2:9)2020">https://doi.org/10.23638/LMCS-16(2:9)2020</a>)</li>
 * </ul>
 *
 *
 * @param <S1> the type of the content of states
 * @author Davide Basile
 *
 */
public class OrchestrationSynthesisOperator<S1> extends ModelCheckingSynthesisOperator<S1, State<S1>, CALabel,
		ModalTransition<S1, Action,State<S1>,CALabel>,
		Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>>,
		Label<Action>,
		ModalTransition<S1,Action,State<S1>,Label<Action>>,
		Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,Label<Action>>>>
		{

	/**
	 * Constructor for the orchestration synthesis operator enforcing the requirement req.
	 *
	 * @param req the invariant requirement (e.g. agreement)
	 */
	public OrchestrationSynthesisOperator(Predicate<CALabel> req){
		super(OrchestrationSynthesisOperator::isUncontrollableOrchestration,req,
				Automaton::new,CALabel::new,ModalTransition::new,State::new);

	}

	/**
	 *
	 * Constructor for the orchestration synthesis operator enforcing the requirement req and property prop.
	 * 
	 * @param req the invariant requirement (e.g. agreement)
	 * @param prop the property to enforce expressed as an automaton
	 */
	public OrchestrationSynthesisOperator(Predicate<CALabel> req,
			Automaton<S1,Action,State<S1>, ModalTransition<S1,Action,State<S1>,Label<Action>>> prop){
		super(OrchestrationSynthesisOperator::isUncontrollableOrchestration,req, prop,
				l->new CALabel(l.getRank(),l.getRequester(),l.getCoAction()),
				Automaton::new,CALabel::new,ModalTransition::new,State::new,Label::new,ModalTransition::new,Automaton::new);
	}

	/**
	 * Applies the orchestration synthesis to aut.
	 * The argument must not contain necessary offers.
	 *
	 * @param aut the plant automaton to which the synthesis is applied.
	 * @return the synthesised orchestration.
	 */
	@Override
	public Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>> apply(Automaton<S1,Action,State<S1>,ModalTransition<S1, Action,State<S1>,CALabel>> aut)
	{
		if (aut.getTransition().parallelStream()
				.anyMatch(t-> !t.isPermitted()&&t.getLabel().isOffer()))
			throw new UnsupportedOperationException("The automaton contains necessary offers that are not allowed in the orchestration synthesis");

		return super.apply(aut);
	}


	private static <S1> boolean isUncontrollableOrchestration(ModalTransition<S1,Action,State<S1>,CALabel> tra,
															  Set<? extends ModalTransition<S1,Action,State<S1>,CALabel>> str,
															  Set<State<S1>> badStates)
	{
		  return tra.isUncontrollable(str,badStates,
				(t,tt) -> (t.getLabel().getRequester().equals(tt.getLabel().getRequester()))//the same requesting principal
				&&(t.getSource().getState().get(t.getLabel().getRequester())
						.equals(tt.getSource().getState().get(tt.getLabel().getRequester())))//in the same local source state
				&&(tt.getLabel().isRequest()
						&&t.getLabel().getAction().equals(tt.getLabel().getCoAction())||
						tt.getLabel().isMatch()
								&&t.getLabel().getAction().equals(tt.getLabel().getAction())));//doing the same request
	}
}
