package io.github.davidebasile.contractautomata.requirements;

import java.util.function.Predicate;

import io.github.davidebasile.contractautomata.automaton.label.CALabel;

/**
 * The agreement predicate over MSCATransitions
 * @author Davide Basile
 *
 */
public class Agreement implements Predicate<CALabel> {

	@Override
	public boolean test(CALabel l) {
		return !l.isRequest();
	}

}
