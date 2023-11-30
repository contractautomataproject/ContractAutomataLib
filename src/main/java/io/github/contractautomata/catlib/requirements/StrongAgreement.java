package io.github.contractautomata.catlib.requirements;

import io.github.contractautomata.catlib.automaton.label.CALabel;

import java.util.function.Predicate;

/**
 * The predicate of Strong Agreement over CALabels.
 * Strong agreement holds if the label is a match.
 * 
 * @author Davide Basile
 *
 */
public class StrongAgreement implements Predicate<CALabel> {

	/**
	 * Returns true if l is a match, ignore tau moves.
	 * @param l  the label to test.
	 * @return true if l is a match, ignore tau moves.
	 */
	@Override
	public boolean test(CALabel l) {
		return l.isMatch() || l.isTau();
	}

}
