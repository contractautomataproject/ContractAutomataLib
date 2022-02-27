package io.github.davidebasile.contractautomata.requirements;

import java.util.function.Predicate;

import io.github.davidebasile.contractautomata.automaton.label.CALabel;

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
