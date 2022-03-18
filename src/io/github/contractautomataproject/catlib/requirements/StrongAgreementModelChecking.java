package io.github.contractautomataproject.catlib.requirements;

import java.util.function.Predicate;
import java.util.stream.IntStream;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;

/**
 * the model can only performs the action provided by the property
 * 
 * @author Davide Basile
 *
 */
public class StrongAgreementModelChecking<T extends Label<String>> implements Predicate<T>{

	@Override
	public boolean test(T l) {
		//only transitions where both aut and prop moves together are allowed
		return l.getAction().get(l.getRank()-1).equals(CALabel.IDLE)||
		IntStream.range(0, l.getRank()-1)
		.allMatch(i->l.getAction().get(i).equals(CALabel.IDLE));	
	} 

}
