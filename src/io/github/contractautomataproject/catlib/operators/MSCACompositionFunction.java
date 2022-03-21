package io.github.contractautomataproject.catlib.operators;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.Ranked;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.action.Action;
import io.github.contractautomataproject.catlib.automaton.label.action.IdleAction;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Class implementing the composition of Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>
 * 
 * @author Davide Basile
 */

public class MSCACompositionFunction extends CompositionFunction<String,Action,State<String>,CALabel,ModalTransition<String,Action,State<String>,CALabel>,Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> {

	public MSCACompositionFunction(List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut,Predicate<CALabel> pruningPred)
	{
		super(aut, MSCACompositionFunction::computeRank, CALabel::match,
				State::new, ModalTransition::new,
				(e, ee,rank) -> MSCACompositionFunction.createLabel(e, ee, rank, aut), 
				(lab, rank, shift)->new CALabel(shift(lab,rank,shift)),
				Automaton::new, pruningPred);
	}

	private static Integer computeSumPrincipal(ModalTransition<String,Action,State<String>,CALabel> etra, Integer eind, List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut)
	{
		return IntStream.range(0, eind)
				.map(i->aut.get(i).getRank())
				.sum()+etra.getLabel().getOffererOrRequester();
	}
	
	public static Integer computeRank(List<? extends Ranked> aut) {
		return aut.stream()
				.map(Ranked::getRank).mapToInt(Integer::intValue).sum();
	}
	
	private static CALabel createLabel(TIndex e, TIndex ee, Integer rank,List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut) {
		Integer principal1 = computeSumPrincipal(e.tra,e.ind,aut);//index of principal in e
		Integer principal2 = computeSumPrincipal(ee.tra,ee.ind,aut);//index of principal in ee
		Action action1 = e.tra.getLabel().getAction();
		Action action2 = ee.tra.getLabel().getAction();
		return 	new CALabel(IntStream.range(0, rank)
				.mapToObj(i->(i==principal1)?action1:(i==principal2)?action2:new IdleAction())
				.collect(Collectors.toList()));
	}


	/**
	 * Build a CALabel by shifting of some positions the index of principals moving in the label
	 * @param lab the object label to shift
	 * @param rank the rank of the label to be created
	 * @param shift the position to shift
	 */
	private static List<Action> shift(CALabel lab, Integer rank, Integer shift){
		if (rank==null||rank<=0||lab==null||shift==null||shift<0||lab.getRank()+shift>rank)
			throw new IllegalArgumentException("Null argument or shift="+shift+" is negative "
					+ "or out of rank");

		List<Action> l = new ArrayList<>(rank);
		l.addAll(Stream.generate(IdleAction::new).limit(shift).collect(Collectors.toList()));
		l.addAll(lab.getLabel());
		if (rank-l.size()>0)
			l.addAll(Stream.generate(IdleAction::new).limit(rank.longValue()-l.size()).collect(Collectors.toList()));
		return l;
	}
}