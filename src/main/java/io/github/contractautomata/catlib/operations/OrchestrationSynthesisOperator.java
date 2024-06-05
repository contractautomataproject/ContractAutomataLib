package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.transition.Transition;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;


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

	public static final int ORIGINAL_LAZY = 1; //no reachability is required
	public static final int REFINED_LAZY = 2; //published at ICE 2023, reachability is required

	private static int version=ORIGINAL_LAZY;

	/**
	 * Constructor for the orchestration synthesis operator enforcing the requirement req.
	 *
	 * @param req the invariant requirement (e.g. agreement)
	 */
	public OrchestrationSynthesisOperator(Predicate<CALabel> req){
		super((t,str,sst)->t.isUncontrollable(str,sst,OrchestrationSynthesisOperator::controllabilityPredicate),req,
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
		super((t,str,sst)->t.isUncontrollable(str,sst,OrchestrationSynthesisOperator::controllabilityPredicate),req, prop,
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

	private static <S1> boolean controllabilityPredicate(ModalTransition<S1,Action,State<S1>,CALabel> tra,
														 Set<ModalTransition<S1,Action,State<S1>,CALabel>> str,
														 Set<State<S1>> badStates){
		return str.parallelStream()
				.filter(t->t.getLabel().isMatch()
						&& !badStates.contains(t.getSource()))//	badStates does not contain target of t,
				//  guaranteed to hold because the pruning predicate of the synthesis has bad.contains(x.getTarget())
				.noneMatch(t->
						(t.getLabel().getRequester().equals(tra.getLabel().getRequester()))//the same requesting principal
								&&(t.getSource().getState().get(t.getLabel().getRequester())
								.equals(tra.getSource().getState().get(tra.getLabel().getRequester())))//in the same local source state
								&&(tra.getLabel().isRequest()
								&&t.getLabel().getAction().equals(tra.getLabel().getCoAction())||
								tra.getLabel().isMatch()
										&&t.getLabel().getAction().equals(tra.getLabel().getAction()))//doing the same request
								&& (version!=REFINED_LAZY||isReachableWithoutMoving(tra.getSource(),t.getSource(),t.getLabel().getRequester(),str,badStates)));

	}

	/**
	 *	  true iff the source state of "to" is reachable from the source state of "from" by only using transitions in
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


	public static void setRefinedLazy(){
		OrchestrationSynthesisOperator.version = REFINED_LAZY;
	}

	public static void setOriginalLazy(){
		OrchestrationSynthesisOperator.version = ORIGINAL_LAZY;
	}

	public static int getVersion() { return OrchestrationSynthesisOperator.version; }
}
