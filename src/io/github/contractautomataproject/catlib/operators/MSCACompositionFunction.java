package io.github.contractautomataproject.catlib.operators;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.Ranked;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

/**
 * Class implementing the composition of ModalAutomaton<CALabel>
 * 
 * @author Davide Basile
 */

public class MSCACompositionFunction extends CompositionFunction<List<BasicState<String>>,List<String>,CAState,CALabel,ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>,ModalAutomaton<CALabel>> {

	public MSCACompositionFunction(List<ModalAutomaton<CALabel>> aut,Predicate<CALabel> pruningPred)
	{
		super(aut, MSCACompositionFunction::computeRank,(l1,l2)->l1.match(l2),
				CAState::new,ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>::new, 
				(e, ee,rank) -> MSCACompositionFunction.createLabel(e, ee, rank, aut), 
				CALabel::new, ModalAutomaton<CALabel>::new, pruningPred);
	}

	private static Integer computeSumPrincipal(ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel> etra, Integer eind, List<ModalAutomaton<CALabel>> aut)
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
	
	public static CALabel createLabel(TIndex e, TIndex ee, Integer rank,List<ModalAutomaton<CALabel>> aut) {
		return new CALabel(rank,
				computeSumPrincipal(e.tra,e.ind,aut),//index of principal in e
				computeSumPrincipal(ee.tra,ee.ind,aut),	//index of principal in ee										
				e.tra.getLabel().getPrincipalAction(),ee.tra.getLabel().getPrincipalAction());
	}
	
}