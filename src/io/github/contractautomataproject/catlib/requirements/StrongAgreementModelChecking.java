package io.github.contractautomataproject.catlib.requirements;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.label.action.Action;
import io.github.contractautomataproject.catlib.automaton.label.action.IdleAction;

/**
 * the model can only performs the action provided by the property
 * 
 * @author Davide Basile
 *
 */
public class StrongAgreementModelChecking<T extends Label<Action>> implements Predicate<T>{

	@Override
	public boolean test(T l) {
		//only transitions where both aut and prop moves together are allowed
		List<Action> listAct = l.getLabel();
		return !((listAct.get(l.getRank()-1) instanceof IdleAction)||
				 IntStream.range(0, l.getRank()-1)
						.mapToObj(listAct::get)
						 .allMatch(a->a instanceof IdleAction));
	} 

}
