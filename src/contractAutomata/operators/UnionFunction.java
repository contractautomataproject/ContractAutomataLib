package contractAutomata.operators;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import contractAutomata.automaton.MSCA;
import contractAutomata.automaton.label.CALabel;
import contractAutomata.automaton.state.BasicState;
import contractAutomata.automaton.state.CAState;
import contractAutomata.automaton.transition.MSCATransition;

public class UnionFunction implements Function<List<MSCA>,MSCA>{

	/**
	 * 
	 * @param aut	list of operands automata
	 * @return compute the union of the FMCA in aut
	 */
	@Override
	public MSCA apply(List<MSCA> aut)
	{
		if (aut==null||aut.isEmpty())
			throw new IllegalArgumentException();
		
		if (aut.parallelStream()
				.anyMatch(x->Objects.isNull(x)))
			throw new IllegalArgumentException();

		int rank=aut.get(0).getRank(); 
		if (aut.stream()
				.map(MSCA::getRank)
				.anyMatch(x->x!=rank))
			throw new IllegalArgumentException("Automata with different ranks!"); 

		if (aut.parallelStream()
		.map(MSCA::getStates)
		.flatMap(Set::stream)
		.map(CAState::getState)
		.flatMap(List::stream)
		.map(BasicState::getState)
		.anyMatch(s->s.contains("_")))
			throw new IllegalArgumentException("Illegal label containing _ in some basic state");
	
		
		//relabeling
		List<MSCA> relabeled=IntStream.range(0, aut.size())
		.mapToObj(id ->new RelabelingOperator(s->s.contains("_")?s:(id+"_"+s)).apply(aut.get(id)))
		.collect(Collectors.toList());

		//new initial state
		CAState newinitial = new CAState(IntStream.range(0,rank)
				.mapToObj(i->new BasicState("0",true,false))
				.collect(Collectors.toList()),0,0);

		Set<MSCATransition> uniontr= new HashSet<>(relabeled.stream()
				.map(x->x.getTransition().size())
				.reduce(Integer::sum)
				.orElse(0)+relabeled.size());  //Initialized to the total number of transitions

		uniontr.addAll(IntStream.range(0, relabeled.size())
				.mapToObj(i->new MSCATransition(newinitial,new CALabel(rank, 0, "!dummy"),relabeled.get(i).getInitial(),MSCATransition.Modality.PERMITTED))
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

		return new MSCA(uniontr);
	}

}
