package io.github.contractautomataproject.catlib.operators;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.Ranked;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

/**
 * Class implementing the composition of Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>
 * 
 * @author Davide Basile
 */

public class MSCACompositionFunction extends CompositionFunction<String,String,State<String>,CALabel,ModalTransition<String,String,State<String>,CALabel>,Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> {

	public MSCACompositionFunction(List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut,Predicate<CALabel> pruningPred)
	{
		super(aut, MSCACompositionFunction::computeRank,(l1,l2)->l1.match(l2),
				State::new,ModalTransition<String,String,State<String>,CALabel>::new, 
				(e, ee,rank) -> MSCACompositionFunction.createLabel(e, ee, rank, aut), 
				CALabel::new, Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>::new, pruningPred);
	}

	private static Integer computeSumPrincipal(ModalTransition<String,String,State<String>,CALabel> etra, Integer eind, List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut)
	{
		return IntStream.range(0, eind)
				.map(i->aut.get(i).getRank())
				.sum()+etra.getLabel().getOffererOrRequester();
	}
	
	public static Integer computeRank(List<? extends Ranked> aut) {
		return aut.stream()
				.map(Ranked::getRank)
				.collect(Collectors.summingInt(Integer::intValue));
	}
	
	public static CALabel createLabel(TIndex e, TIndex ee, Integer rank,List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut) {
		return new CALabel(rank,
				computeSumPrincipal(e.tra,e.ind,aut),//index of principal in e
				computeSumPrincipal(ee.tra,ee.ind,aut),	//index of principal in ee										
				e.tra.getLabel().getPrincipalAction(),ee.tra.getLabel().getPrincipalAction());
	}
	
}