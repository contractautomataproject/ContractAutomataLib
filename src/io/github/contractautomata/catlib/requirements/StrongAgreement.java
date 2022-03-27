package io.github.contractautomata.catlib.requirements;

import io.github.contractautomata.catlib.automaton.label.CALabel;

import java.util.function.Predicate;

/**
 * The strong agreement predicate over MSCATransitions
 * 
 * @author Davide Basile
 *
 */
public class StrongAgreement implements Predicate<CALabel> {

	@Override
	public boolean test(CALabel l) {
		return l.isMatch();
	}

}
