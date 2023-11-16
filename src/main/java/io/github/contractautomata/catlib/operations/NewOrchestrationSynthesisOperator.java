package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.label.action.OfferAction;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.transition.Transition;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * Class implementing the new orchestration synthesis operator.<br>
 *
 *
 * @author Davide Basile
 *
 */
public class NewOrchestrationSynthesisOperator
{

	private final Predicate<CALabel> req;

	public NewOrchestrationSynthesisOperator(Predicate<CALabel> req){
		this.req=req;
	}

	/**
	 * Applies the orchestration synthesis to aut.
	 * The automata must not contain necessary offers.
	 *
	 * @param laut the list of automaton to compose to obtain the plant automaton, to which the mpc synthesis is applied
	 * @return the synthesised orchestration.
	 */
	public Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> apply(List<Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>>> laut)
	{
		if (laut.stream()
				.map(Automaton::getTransition)
				.anyMatch(s->s.parallelStream()
						.anyMatch(t-> !t.isPermitted()&&t.getLabel().isOffer() || t.getLabel().getAction().getLabel().startsWith("tau_"))))
			throw new UnsupportedOperationException("Some automaton contains necessary offers that are not allowed in the orchestration synthesis or some action is labelled with tau_");

		if (laut.stream().anyMatch(a->a.getRank()>1))
			throw new UnsupportedOperationException("All automata must be principal");

		List<Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, CALabel>>> backup = laut;

		//encode the principal automata
		laut = laut.stream()
				.map(aut->
				{
					//each lazy transition is unfolded into two linked transitions, one uncontrollable and one controllable
					Map<ModalTransition<String, Action, State<String>, CALabel>, List<ModalTransition<String, Action, State<String>, CALabel>>> map =
							aut.getTransition().parallelStream()
									.collect(Collectors.toMap(t -> t, t -> {
										if (!t.getModality().equals(ModalTransition.Modality.LAZY))
											return List.of(t);
										else {
											List<Action> label = IntStream.range(0, t.getLabel().getRank())
													.mapToObj(i -> new IdleAction())
													.collect(Collectors.toList());

											List<BasicState<String>> intermediate = new ArrayList<>(t.getSource().getState());

											if (t.getLabel().isOffer() || t.getLabel().isMatch()) {
												label.set(t.getLabel().getOfferer(), new OfferAction("tau_" + t.getLabel().getAction().getLabel()));
												String stateLabel = t.getSource().getState().get(t.getLabel().getOfferer()).getState() + "_" + t.getLabel().getAction().getLabel() + "_" + t.getTarget().getState().get(t.getLabel().getOfferer()).getState();
												intermediate.set(t.getLabel().getOfferer(), new BasicState<>(stateLabel, false, false));//the new basic state has the same label of the source
											}

											if (t.getLabel().isRequest()) {
												label.set(t.getLabel().getRequester(), new OfferAction("tau_" + t.getLabel().getAction().getLabel()));//the label cannot be a request
												String stateLabel = t.getSource().getState().get(t.getLabel().getRequester()).getState() + "_" + t.getLabel().getAction().getLabel() + "_" + t.getTarget().getState().get(t.getLabel().getRequester()).getState();
												intermediate.set(t.getLabel().getRequester(), new BasicState<>(stateLabel, false, false));
											}

											State<String> intermediateState = new State<>(intermediate);
											ModalTransition<String, Action, State<String>, CALabel> t1 = new ModalTransition<>(t.getSource(), new CALabel(label), intermediateState, ModalTransition.Modality.URGENT);
											ModalTransition<String, Action, State<String>, CALabel> t2 = new ModalTransition<>(intermediateState, t.getLabel(), t.getTarget(), ModalTransition.Modality.PERMITTED);
											return List.of(t1, t2);
										}
									}));

					return new Automaton<>(
							aut.getTransition().parallelStream()
									.flatMap(t -> map.get(t).stream())
									.collect(Collectors.toSet()));
				}).collect(Collectors.toList());


		//compose and apply mpc synthesis to the encoded automaton

		Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, CALabel>> comp =
				new MSCACompositionFunction<>(laut, t->t.getLabel().isRequest()).apply(Integer.MAX_VALUE);

		Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, CALabel>> mpc =
				new MpcSynthesisOperator<String>(this.req).apply(comp);

		if (Objects.isNull(mpc))
			return null;

		//decode the orchestration
		Set<ModalTransition<String, Action, State<String>, CALabel>> str = mpc.getTransition();

		//for each state, perform a visit traversing only tau moves, and add each non-tau outgoing
		//transition from each visited state to the starting state of the visit
		str.addAll(mpc.getStates().parallelStream()
				.flatMap(startstate->{
					Map<State<String>,Boolean> map = mpc.getStates().stream()
							.collect(Collectors.toMap(x -> x, x -> false));
					map.put(startstate, true);
					Queue<State<String>> toVisit = new LinkedList<>(List.of(startstate));
					Set<ModalTransition<String, Action, State<String>, CALabel>> newTrans = new HashSet<>();
					while(!toVisit.isEmpty()) {
						State<String> currentstate = toVisit.remove();
						newTrans.addAll(mpc.getForwardStar(currentstate)
								.stream()
								.filter(t->!t.isUrgent())
								.map(t->new ModalTransition<>(startstate,t.getLabel(),t.getTarget(), t.getModality()))
								.collect(Collectors.toSet()));
						Map<State<String>, Boolean> toAdd =
								mpc.getForwardStar(currentstate).stream()
								.filter(t -> t.isUrgent() &&
										Boolean.FALSE.equals(map.get(t.getTarget())))
								.map(Transition::getTarget)
								.distinct()
								.collect(Collectors.toMap(x -> x, x -> true));
						map.putAll(toAdd);
						toVisit.addAll(toAdd.keySet());
					}
					return newTrans.stream();})
				.collect(Collectors.toSet()));

		//removing the tau moves
		str = str.parallelStream()
				.filter(t -> !t.getLabel().getAction().getLabel().startsWith("tau_"))
				.collect(Collectors.toSet());

		//restore the lazy modality for the interested transitions
		str = str.parallelStream()
				.map(t->{
					if (t.getLabel().isOffer())
						return t;
					int requester = t.getLabel().getRequester();
					String state = t.getSource().getState().get(requester).getState().split("_")[0];
					String action = t.getLabel().getAction().getLabel();
					if (backup.get(requester).getTransition().stream()
							.anyMatch(pt->
									pt.getSource().getState().get(0).getState().equals(state) &&
									pt.getLabel().getAction().getLabel().equals(action) &&
									pt.getModality().equals(ModalTransition.Modality.LAZY)))
						return new ModalTransition<>(t.getSource(),t.getLabel(),t.getTarget(), ModalTransition.Modality.LAZY);
					else
						return t;

				}).collect(Collectors.toSet());

		//remove dangling states and transitions (the synthesis is reused)
		return new OrchestrationSynthesisOperator<String>(this.req).apply(new Automaton<>(str));

	}
}