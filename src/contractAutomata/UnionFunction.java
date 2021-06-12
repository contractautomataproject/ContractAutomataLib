package contractAutomata;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UnionFunction implements Function<List<MSCA>,MSCA>{

	/**
	 * 
	 * @param aut	list of operands automata
	 * @return compute the union of the FMCA in aut
	 */
	@Override
	public MSCA apply(List<MSCA> aut)
	{
		if (aut==null||aut.size()==0)
			throw new IllegalArgumentException();

		
		if (aut.get(0)==null)
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
		.map(BasicState::getLabel)
		.anyMatch(s->s.contains("_")))
			throw new IllegalArgumentException("Illegal label containing _ in some basic state");
	
		
		//relabeling
		IntStream.range(0, aut.size())
		.forEach(id ->{
			aut.get(id).getStates().forEach(x->{
				x.getState().forEach(s->{
					if (!s.getLabel().contains("_"))
						s.setLabel(id+"_"+s.getLabel());
				});
			});
		}); 

		//new initial state
		CAState newinitial = new CAState(IntStream.range(0,rank)
				.mapToObj(i->new BasicState("0",true,false))
				.collect(Collectors.toList()),0,0);

		Set<MSCATransition> uniontr= new HashSet<>(aut.stream()
				.map(x->x.getTransition().size())
				.reduce(Integer::sum)
				.orElse(0)+aut.size());  //Initialized to the total number of transitions

		uniontr.addAll(IntStream.range(0, aut.size())
				.mapToObj(i->new MSCATransition(newinitial,new CALabel(rank, 0, "!dummy"),aut.get(i).getInitial(),MSCATransition.Modality.PERMITTED))
				.collect(Collectors.toSet())); //adding transition from new initial state to previous initial states

		//remove old initial states, I need to do this now
		aut.parallelStream()
		.flatMap(a->a.getStates().stream())
		.filter(CAState::isInitial)
		.forEach(x->x.setInitial(false));

		uniontr.addAll(IntStream.range(0, aut.size())
				.mapToObj(i->aut.get(i).getTransition())
				.flatMap(Set::stream)
				.collect(Collectors.toSet())); //adding all other transitions

		return new MSCA(uniontr);
	}


}
