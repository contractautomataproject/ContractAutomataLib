package io.github.contractautomata.catlib.requirements;

import io.github.contractautomata.catlib.automaton.label.CALabel;

import java.util.function.Predicate;

/**
 * The predicate of Agreement over CALabels.
 * It holds if a CALabel is not a request.
 * @author Davide Basile
 */
public class Agreement implements Predicate<CALabel> {

	/**
	 * Returns  true if l is not a request.
	 * @param l  the CALabel to test
	 * @return true if l is not a request
	 */
	@Override
	public boolean test(CALabel l) {
		return !l.isRequest();
	}

}
