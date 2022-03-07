package io.github.contractautomataproject.catlib.requirements;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;

/**
 * the model can also performs actions in between those provided by the property
 * 
 * @author Davide Basile
 *
 */
public class AgreementModelChecking<T extends Label<List<String>>> implements Predicate<T>{

	@Override
	public boolean test(T l) {
		return IntStream.range(0, l.getRank()-1)
		.allMatch(i->l.getAction().get(i).equals(CALabel.idle));	
	} 

}
