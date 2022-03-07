package io.github.davidebasile.contractautomata.requirements;

import io.github.davidebasile.contractautomata.automaton.label.CALabel;

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
