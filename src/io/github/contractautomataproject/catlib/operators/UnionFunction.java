package io.github.contractautomataproject.catlib.operators;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

/**
 * Class implementing the union function
 * 
 * @author Davide Basile
 *
 */
public class UnionFunction implements Function<List<ModalAutomaton<CALabel>>,ModalAutomaton<CALabel>>{

	/**
	 * 
	 * @param aut list of operands automata
	 * @return compute the union of the FMCA in aut
	 */
	@Override
	public ModalAutomaton<CALabel> apply(List<ModalAutomaton<CALabel>> aut)
	{
		if (aut==null||aut.isEmpty())
			throw new IllegalArgumentException();
		
		if (aut.parallelStream()
				.anyMatch(x->Objects.isNull(x)))
			throw new IllegalArgumentException();

		int rank=aut.get(0).getRank(); 
		if (aut.stream()
				.map(ModalAutomaton<CALabel>::getRank)
				.anyMatch(x->x!=rank))
			throw new IllegalArgumentException("Automata with different ranks!"); 

		if (aut.parallelStream()
		.map(ModalAutomaton<CALabel>::getStates)
		.flatMap(Set::stream)
		.map(CAState::getState)
		.flatMap(List::stream)
		.map(BasicState<String>::getState)
		.anyMatch(s->s.contains("_")))
			throw new IllegalArgumentException("Illegal label containing _ in some basic state");
	
		
		//relabeling
		List<ModalAutomaton<CALabel>> relabeled=IntStream.range(0, aut.size())
		.mapToObj(id ->new RelabelingOperator<CALabel>(CALabel::new, s->s.contains("_")?s:(id+"_"+s))
				.apply(aut.get(id)))
		.collect(Collectors.toList());

		//new initial state
		CAState newinitial = new CAState(IntStream.range(0,rank)
				.mapToObj(i->new BasicState<String>("0",true,false))
				.collect(Collectors.toList())//,0,0
				);

		Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>> uniontr= new HashSet<>(relabeled.stream()
				.map(x->x.getTransition().size())
				.reduce(Integer::sum)
				.orElse(0)+relabeled.size());  //Initialized to the total number of transitions

		uniontr.addAll(IntStream.range(0, relabeled.size())
				.mapToObj(i->new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(newinitial,new CALabel(rank, 0, "!dummy"),relabeled.get(i).getInitial(),ModalTransition.Modality.PERMITTED,CAState::new))
				.collect(Collectors.toSet())); //adding transition from new initial state to previous initial states

		//remove old initial states, I need to do this now
		relabeled.parallelStream()
		.flatMap(a->a.getStates().stream())
		.filter(CAState::isInitial)
		.forEach(x->x.setInitial(false));

		uniontr.addAll(IntStream.range(0, relabeled.size())
				.mapToObj(i->relabeled.get(i).getTransition())
				.flatMap(Set::stream)
				.collect(Collectors.toSet())); //adding all other transitions

		return new ModalAutomaton<CALabel>(uniontr);
	}

}
