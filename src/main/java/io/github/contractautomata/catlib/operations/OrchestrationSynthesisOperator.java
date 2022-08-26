package io.github.contractautomata.catlib.operations;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.transition.Transition;


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
	private static boolean reachabilityLazy =false;

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
								&&t.getLabel().getAction().equals(tt.getLabel().getAction()))//doing the same request
						&& (!reachabilityLazy||isReachableWithoutMoving(tt.getSource(),t.getSource(),t.getLabel().getRequester(),str,badStates)));
	}

	/**
	 *
	 * @param from
	 * @param to
	 * @param principal
	 * @param str
	 * @param badStates
	 * @param <S1>
	 * @return  true iff
	 * 	  the source state of "to" is reachable from the source state of "from" by only using transitions in
	 * 	  "str" whose states are not in "badStates" and in all those states the source state of "principal" does not change
	 */
	private static <S1> boolean isReachableWithoutMoving(State<S1> from, State<S1> to,
														 Integer principal,
														 Set<? extends ModalTransition<S1,Action,State<S1>,CALabel>> str,
														 Set<State<S1>> badStates){
		if (from.equals(to)) return true;
		Queue<State<S1>> toVisit = new LinkedList<>(List.of(from));
		Queue<State<S1>> visited = new LinkedList<>();
		while(!toVisit.isEmpty()) {
			State<S1> currentstate = toVisit.remove();
			visited.add(currentstate);
			Set<State<S1>> toAdd = str.parallelStream()
					.filter(x -> x.getSource().equals(currentstate)
							&& x.getLabel().getContent().get(principal) instanceof IdleAction
							&& !visited.contains(x.getTarget())
							&& !badStates.contains(x.getTarget()))
					.map(Transition::getTarget)
					.collect(Collectors.toSet());
			if (toAdd.contains(to))
				return true;
			toVisit.addAll(toAdd);
		}
		return false;
	}

	public static void setReachabilityLazy(){
		OrchestrationSynthesisOperator.reachabilityLazy =true;
	}
}
