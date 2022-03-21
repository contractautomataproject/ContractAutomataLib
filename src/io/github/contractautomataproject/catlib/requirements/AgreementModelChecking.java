package io.github.contractautomataproject.catlib.requirements;

import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.label.action.Action;
import io.github.contractautomataproject.catlib.automaton.label.action.IdleAction;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

/**
 * the model can also performs actions in between those provided by the property
 * 
 * @author Davide Basile
 *
 */
public class AgreementModelChecking<T extends Label<Action>> implements Predicate<T>{

	@Override
	public boolean test(T l) {
		List<Action> listAct = l.getLabel();
		return !(IntStream.range(0, l.getRank()-1)
				.mapToObj(listAct::get)
				.map(Action::getLabel)
				.allMatch(IdleAction::isIdle));
	} 

}
