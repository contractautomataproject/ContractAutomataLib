package io.github.contractautomataproject.catlib.requirements;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;

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
