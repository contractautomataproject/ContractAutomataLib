package io.github.contractautomataproject.catlib.operators;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

/**
 * Class implementing the union function
 * 
 * @author Davide Basile
 *
 */
public class UnionFunction implements Function<List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>>,Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>>{

	/**
	 * 
	 * @param aut list of operands automata
	 * @return compute the union of the FMCA in aut
	 */
	@Override
	public Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> apply(List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut)
	{
		if (aut==null||aut.isEmpty())
			throw new IllegalArgumentException();
		
		if (aut.parallelStream()
				.anyMatch(Objects::isNull))
			throw new IllegalArgumentException();

		int rank=aut.get(0).getRank(); 
		if (aut.stream()
				.map(Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>::getRank)
				.anyMatch(x->x!=rank))
			throw new IllegalArgumentException("Automata with different ranks!"); 

		if (aut.parallelStream()
		.map(Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>::getStates)
		.flatMap(Set::stream)
		.map(State<String>::getState)
		.flatMap(List::stream)
		.map(BasicState<String>::getState)
		.anyMatch(s->s.contains("_")))
			throw new IllegalArgumentException("Illegal label containing _ in some basic state");
	
		Set<ModalTransition<String,String,State<String>,CALabel>> uniontr= new HashSet<>(aut.stream()
				.map(x->x.getTransition().size())
				.reduce(Integer::sum)
				.orElse(0)+aut.size());  //Initialized to the total number of transitions
		
		//storing initial states of aut
		List<State<String>> initialStates = aut.stream()
				.map(Automaton::getInitial)
				.collect(Collectors.toList());
		
		//relabeling, removing initial states
		List<Set<ModalTransition<String,String,State<String>,CALabel>>> relabeled=IntStream.range(0, aut.size())
		.mapToObj(id ->new RelabelingOperator<CALabel>(CALabel::new, s->s.contains("_")?s:(id+"_"+s),s->false,BasicState::isFinalstate)
				.apply(aut.get(id)))
		.collect(Collectors.toList());

		//new initial state
		State<String> newinitial = new State<>(IntStream.range(0,rank)
				.mapToObj(i->new BasicState<>("0",true,false))
				.collect(Collectors.toList()));


		uniontr.addAll(IntStream.range(0, relabeled.size())
				.mapToObj(i->new ModalTransition<String,String,State<String>,CALabel>(
						newinitial,new CALabel(rank, 0, "!dummy"),
						relabeled.get(i).parallelStream()
						.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
						.filter(s->IntStream.range(0, s.getRank())
								.allMatch(j->s.getState().get(j).getState().split("_")[1].equals(initialStates.get(i).getState().get(j).getState())))
						.findFirst().orElseThrow(RuntimeException::new),
						ModalTransition.Modality.PERMITTED))
				.collect(Collectors.toSet())); //adding transition from new initial state to previous initial states

		uniontr.addAll(IntStream.range(0, relabeled.size())
				.mapToObj(relabeled::get)
				.flatMap(Set::stream)
				.collect(Collectors.toSet())); //adding all other transitions

		return new Automaton<>(uniontr);
	}

}
