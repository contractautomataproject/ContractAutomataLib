package io.github.davidebasile.contractautomata.requirements;

import java.util.function.Predicate;

import io.github.davidebasile.contractautomata.automaton.transition.MSCATransition;

/**
 * The agreement predicate over MSCATransitions
 * @author Davide Basile
 *
 */
public class Agreement implements Predicate<MSCATransition> {

	@Override
	public boolean test(MSCATransition t) {
		return !t.getLabel().isRequest();
	}

}
