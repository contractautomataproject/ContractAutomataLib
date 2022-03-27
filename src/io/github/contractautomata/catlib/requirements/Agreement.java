package io.github.contractautomata.catlib.requirements;

import io.github.contractautomata.catlib.automaton.label.CALabel;

import java.util.function.Predicate;

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
