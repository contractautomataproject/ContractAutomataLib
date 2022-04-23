package io.github.contractautomata.catlib.operations;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.OfferAction;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;

/**
 * Class implementing the union function.  <br>
 * This is the standard union operation of Finite State Automata, obtained by adding a
 * new initial state with outgoing transitions to the initial states of the operands. <br>
 * These new transitions have a dummy label. <br>
 * Before being unified, the automata are relabeled to avoid having duplicate states. <br>
 *
 * @author Davide Basile
 *
 */
public class UnionFunction implements Function<List<Automaton<String, Action,State<String>, ModalTransition<String,Action,State<String>,CALabel>>>,Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>>{


	/**
	 * Compute the union function.
	 *
	 * @param aut list of operands automata
	 * @return the automaton union of the automata in aut
	 */
	@Override
	public Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> apply(List<Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>>> aut)
	{
		if (aut==null||aut.isEmpty())
			throw new IllegalArgumentException();

		if (aut.parallelStream()
				.anyMatch(Objects::isNull))
			throw new IllegalArgumentException();

		int rank=aut.get(0).getRank();
		if (aut.stream()
				.map(Automaton::getRank)
				.anyMatch(x->x!=rank))
			throw new IllegalArgumentException("Automata with different ranks!");

		if (aut.parallelStream()
				.map(Automaton::getStates)
				.flatMap(Set::stream)
				.map(State::getState)
				.flatMap(List::stream)
				.map(BasicState<String>::getState)
				.anyMatch(s->s.contains("_")))
			throw new IllegalArgumentException("Illegal label containing _ in some basic state");

		Set<ModalTransition<String,Action,State<String>,CALabel>> uniontr= new HashSet<>(aut.stream()
				.map(x->x.getTransition().size())
				.reduce(Integer::sum)
				.orElse(0)+aut.size());  //Initialized to the total number of transitions

		//storing initial states of aut
		List<State<String>> initialStates = aut.stream()
				.map(Automaton::getInitial)
				.collect(Collectors.toList());

		//relabeling, removing initial states
		List<Set<ModalTransition<String,Action,State<String>,CALabel>>> relabeled=IntStream.range(0, aut.size())
				.mapToObj(id ->new RelabelingOperator<String,CALabel>(CALabel::new,
						s->(id+"_"+s),
						s->false,
						BasicState::isFinalState)
						.apply(aut.get(id)))
				.collect(Collectors.toList());

		//new initial state
		State<String> newInitial = new State<>(IntStream.range(0,rank)
				.mapToObj(i->new BasicState<>("0",true,false))
				.collect(Collectors.toList()));


		uniontr.addAll(IntStream.range(0, relabeled.size())
				.mapToObj(i->new ModalTransition<>(
						newInitial,new CALabel(rank, 0, new OfferAction("dummy")),
						relabeled.get(i).parallelStream()
								.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
								.filter(s->IntStream.range(0, s.getRank())
										.allMatch(j->s.getState().get(j).getState().split("_")[1].equals(initialStates.get(i).getState().get(j).getState())))
								.findFirst().orElseThrow(RuntimeException::new),
						ModalTransition.Modality.PERMITTED))
				.collect(Collectors.toSet())); //adding transition from new initial state to previous initial states

		uniontr.addAll(relabeled.stream()
				.flatMap(Set::stream)
				.collect(Collectors.toSet())); //adding all the other transitions

		return new Automaton<>(uniontr);
	}

}
