package io.github.contractautomata.catlib.requirements;

import io.github.contractautomata.catlib.automaton.label.CALabel;

/**
 * The strong agreement predicate over MSCATransitions
 * 
 * @author Davide Basile
 *
 */
public class StrongAgreement extends StrongAgreementModelChecking<CALabel> {

	@Override
	public boolean test(CALabel l) {
		return l.isMatch();
	}

}
