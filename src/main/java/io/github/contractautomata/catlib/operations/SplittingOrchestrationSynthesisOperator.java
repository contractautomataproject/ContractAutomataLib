package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.label.action.TauAction;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static io.github.contractautomata.catlib.automaton.transition.ModalTransition.Modality.PERMITTED;
import static io.github.contractautomata.catlib.automaton.transition.ModalTransition.Modality.URGENT;


/**
 * Class implementing the new orchestration synthesis operator.<br>
 *
 * @author Davide Basile
 *
 */
public class SplittingOrchestrationSynthesisOperator extends MpcSynthesisOperator<String>
{
	private final Predicate<ModalTransition<String,Action,State<String>,CALabel>> pruningPred;

	public SplittingOrchestrationSynthesisOperator(Predicate<CALabel> req){
		super(req);
		this.pruningPred=t->false;

	}

	public SplittingOrchestrationSynthesisOperator(Predicate<CALabel> req,  Predicate<ModalTransition<String,Action,State<String>,CALabel>> pruningPred){
		super(req);
		this.pruningPred=pruningPred;
	}

	public SplittingOrchestrationSynthesisOperator(Predicate<CALabel> req, Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,Label<Action>>> prop){
		super(req,prop);
		this.pruningPred=t->false;
	}

	@Override
	public Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> apply(Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>> aut)
	{
		ProjectionFunction<String> pf = new ProjectionFunction<>();
		return this.apply(IntStream.range(0, aut.getRank())
				.mapToObj(i->pf.apply(aut,i,t->t.getLabel().getRequester()))
				.collect(Collectors.toList()));
	}

	/**
	 * Applies the orchestration synthesis to aut.
	 *
	 */
	public Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> apply(List<Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>>> laut)
	{
		if (laut.stream()
				.map(Automaton::getTransition)
				.anyMatch(ts->ts.parallelStream()
						.anyMatch(t-> (!t.isPermitted()&&t.getLabel().isOffer()) || t.getLabel().isTau())))
			throw new IllegalArgumentException("Some automaton contains necessary offers that are not allowed in the orchestration synthesis or some action is tau");

		if (laut.stream()
				.anyMatch(a->a.getRank()>1))
			throw new IllegalArgumentException("Only principals are allowed");

		//compose encoded principals
		Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, CALabel>> comp =
				new MSCACompositionFunction<>(encodePrincipals(laut), t->this.getReq().negate().test(t.getLabel()) || pruningPred.test(t)).apply(Integer.MAX_VALUE);

		if (Objects.isNull(comp))
			return null;

		//apply mpc synthesis to the encoded automata
		Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, CALabel>> mpc = super.apply(comp);

		if (Objects.isNull(mpc))
			return null;

		return mpc;//decode(mpc,laut);
	}


	/**
	 * each lazy transition is unfolded into an urgent tau move followed by an optional transition with the same original label.
	 * It is necessary to introduce a new state, with a new state label. Therefore, the type of the label of the state
	 * is instantiated to String.
	 */
	private List<Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>>> encodePrincipals(List<Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>>> laut){
		return laut.stream()
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

											//the checks at the beginning of the method ensures that only necessary requests are lazy, and principals cannot have matches
											if (t.getLabel().isRequest()) {
												label.set(t.getLabel().getRequester(), new TauAction(t.getLabel().getAction().getLabel()));//the label cannot be a request
												String stateLabel = t.getSource().getState().get(t.getLabel().getRequester()).getState() + "_" + t.getLabel().getAction().getLabel() + "_" + t.getTarget().getState().get(t.getLabel().getRequester()).getState();
												intermediate.set(t.getLabel().getRequester(), new BasicState<>(stateLabel, false, false, false));
											}

											State<String> intermediateState = new State<>(intermediate);
											ModalTransition<String, Action, State<String>, CALabel> t1 = new ModalTransition<>(t.getSource(), new CALabel(label), intermediateState, URGENT);
											ModalTransition<String, Action, State<String>, CALabel> t2 = new ModalTransition<>(intermediateState, t.getLabel(), t.getTarget(), PERMITTED);
											return List.of(t1, t2);
										}
									}));

					return new Automaton<>(
							aut.getTransition().parallelStream()
									.flatMap(t -> map.get(t).stream())
									.collect(Collectors.toSet()));
				}).collect(Collectors.toList());
	}
}
//END OF CLASS










//	/**
//	 * remove the tau moves. However, it introduces non-determinism
//	 */
//	private Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>> decode(Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>> mpc,
//																												List<Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>>> laut){
//
//		//decode the orchestration
//		Set<ModalTransition<String, Action, State<String>, CALabel>> str = mpc.getTransition();
//
//		//for each state, perform a visit traversing only tau moves, and add each non-tau outgoing
//		//transition from each visited state to the starting state of the visit
//		str.addAll(mpc.getStates().parallelStream()
//				.flatMap(startstate->{
//					Map<State<String>,Boolean> map = mpc.getStates().stream()
//							.collect(Collectors.toMap(x -> x, x -> false));
//					map.put(startstate, true);
//					Queue<State<String>> toVisit = new LinkedList<>(List.of(startstate));
//					Set<ModalTransition<String, Action, State<String>, CALabel>> newTrans = new HashSet<>();
//					while(!toVisit.isEmpty()) {
//						State<String> currentstate = toVisit.remove();
//						newTrans.addAll(mpc.getForwardStar(currentstate)
//								.stream()
//								.filter(t->!t.getLabel().isTau())
//								.map(t->new ModalTransition<>(startstate,t.getLabel(),t.getTarget(), t.getModality()))
//								.collect(Collectors.toSet()));
//						Map<State<String>, Boolean> toAdd =
//								mpc.getForwardStar(currentstate).stream()
//										.filter(t -> t.getLabel().isTau() &&
//												Boolean.FALSE.equals(map.get(t.getTarget())))
//										.map(Transition::getTarget)
//										.distinct()
//										.collect(Collectors.toMap(x -> x, x -> true));
//						map.putAll(toAdd);
//						toVisit.addAll(toAdd.keySet());
//					}
//					return newTrans.stream();})
//				.collect(Collectors.toSet()));
//
//		//removing the tau moves
//		str = str.parallelStream()
//				.filter(t -> !t.getLabel().isTau())
//				.collect(Collectors.toSet());
//
//		//restore the lazy modality for the interested transitions
//		str = str.parallelStream()
//				.map(t->{
//					if (t.getLabel().isOffer())
//						return t;
//					int requester = t.getLabel().getRequester();
//					String state = t.getSource().getState().get(requester).getState().split("_")[0];
//					String action = t.getLabel().getAction().getLabel();
//					if (laut.get(requester).getTransition().stream()
//							.anyMatch(pt->
//									pt.getSource().getState().get(0).getState().equals(state) &&
//											pt.getLabel().getAction().getLabel().equals(action) &&
//											pt.getModality().equals(ModalTransition.Modality.LAZY)))
//						return new ModalTransition<>(t.getSource(),t.getLabel(),t.getTarget(), ModalTransition.Modality.LAZY);
//					else
//						return t;
//
//				}).collect(Collectors.toSet());
//
//		//remove dangling states and transitions (the synthesis is reused)
//		return new OrchestrationSynthesisOperator<String>(this.getReq()).apply(new Automaton<>(str));
//	}


//
//	private Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>> encode(Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>> aut) {
//		Set<ModalTransition<String, Action, State<String>, CALabel>> ts = aut.getTransition();
//
//		Set<ModalTransition<String, Action, State<String>, CALabel>> removed = new HashSet<>();
//		ModalTransition<String, Action, State<String>, CALabel> t = ts.stream()
//				.filter(ModalTransition::isLazy)
//				.findFirst().orElse(null);
//
//		int ind=0;
//		while(t!=null)
//		{
////			try {
////				new AutDataConverter<>(CALabel::new).exportMSCA("test_"+ind, new Automaton<>(ts));
////			} catch (IOException e) {
////				throw new RuntimeException(e);
////			}
//			removed.add(t);
//			State<String> source = t.getSource();
//			final int requester = t.getLabel().getRequester();
//
//			List<Action> label = IntStream.range(0, t.getLabel().getRank())
//					.mapToObj(i -> new IdleAction())
//					.collect(Collectors.toList());
//
//			List<BasicState<String>> intermediate = new ArrayList<>(source.getState());
//
//			//the checks at the beginning of the method ensures that only necessary requests and matches are lazy
//			//if it is lazy there is always a requester
//			label.set(requester, new OfferAction("tau_" + t.getLabel().getAction().getLabel()));//the label cannot be a request
//			String stateLabelReq = t.getSource().getState().get(requester).getState() + "_" + t.getLabel().getAction().getLabel() + "_" + t.getTarget().getState().get(requester).getState();
//			intermediate.set(requester, new BasicState<>(stateLabelReq, false, false));
//
//			//in case of match set also the offerer
////			if (t.getLabel().isMatch()){
////				label.set(t.getLabel().getOfferer(), new RequestAction("tau_" + t.getLabel().getAction().getLabel()));//the label cannot be a request
////				String stateLabelOff = t.getSource().getState().get(t.getLabel().getOfferer()).getState() + "_" + t.getLabel().getAction().getLabel() + "_" + t.getTarget().getState().get(t.getLabel().getOfferer()).getState();
////				intermediate.set(t.getLabel().getOfferer(), new BasicState<>(stateLabelOff, false, false));
////			}
//
//			State<String> intermediateState = new State<>(intermediate);
//			ts.remove(t);
//			ts.addAll(ts.parallelStream()
//					.filter(x->x.getSource().equals(source))
//					.filter(x -> !removed.contains(x) && (x.getLabel().getContent().get(requester) instanceof IdleAction))
//					.map(tr -> new ModalTransition<>(intermediateState, tr.getLabel(), tr.getTarget(), tr.getModality()))
//					.collect(Collectors.toSet()));
//			ts.add(new ModalTransition<>(source, new CALabel(label), intermediateState, URGENT));
//			ts.add(new ModalTransition<>(intermediateState, t.getLabel(), t.getTarget(), PERMITTED));
//			t = ts.stream()
//					.filter(ModalTransition::isLazy)
//					.findFirst().orElse(null);
//
//			ind++;
//		}
//		return new Automaton<>(ts);
//	}
//	private Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>, Label<Action>>> encodeProp(Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>, Label<Action>>> prop)
//	{
//		if (Objects.isNull(prop))
//			return null;
//		Set<ModalTransition<String, Action, State<String>, Label<Action>>> ts = prop.getTransition();
//
//		ModalTransition<String, Action, State<String>, Label<Action>> t = ts.stream()
//				.filter(ModalTransition::isLazy)
//				.findFirst().orElse(null);
//		Set<ModalTransition<String, Action, State<String>, Label<Action>>> removed = new HashSet<>();
//		while(t!=null)
//		{
//			removed.add(t);
//			BasicState<String> bs = new BasicState<>(t.getSource().getState().get(0).getState() + "_tau" + t.getLabel().getAction().getLabel(),false, false);
//			State<String> source = t.getSource();
//			State<String> intermediate = new State<>(List.of(bs));
//			Label<Action> tau = new Label<>(List.of(new Action("tau_" + t.getLabel().getAction().getLabel())));
//			ts.remove(t);
//			ts.addAll(ts.parallelStream()
//					.filter(x->x.getSource().equals(source))
//					.filter(tr -> !removed.contains(tr))
//					.map(tr -> new ModalTransition<>(intermediate, tr.getLabel(), tr.getTarget(), tr.getModality()))
//					.collect(Collectors.toSet()));
//			ts.add(new ModalTransition<>(source, tau, intermediate, PERMITTED));
//			ts.add(new ModalTransition<>(intermediate, t.getLabel(), t.getTarget(), PERMITTED));
//			t = ts.stream()
//				.filter(ModalTransition::isLazy)
//				.findFirst().orElse(null);
//		}
//		return new Automaton<>(ts);
//	}
//

 //	 * the property accepts also all tau moves of the automaton comp

//	private Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>, Label<Action>>> addTaus(Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>, Label<Action>>> prop,
//																													   Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, CALabel>> comp){
//		if (Objects.isNull(prop))
//			return null;
//		Set<String> taus = comp.getTransition().parallelStream()
//				.map(t->t.getLabel().getAction().getLabel())
//				.filter(s->s.startsWith("tau_"))
//				.collect(Collectors.toSet());
//
//		Set<ModalTransition<String, Action, State<String>, Label<Action>>> collect = prop.getStates().stream()
//				.flatMap(s -> taus.stream()
//						.map(act -> new ModalTransition<>(s, new Label<>(List.of(new Action(act))), s, PERMITTED)))
//				.collect(Collectors.toSet());
//
//		collect.addAll(prop.getTransition());
//
//		return new Automaton<>(collect);
//	}