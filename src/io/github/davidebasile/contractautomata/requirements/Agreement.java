package io.github.davidebasile.contractautomata.requirements;

import io.github.davidebasile.contractautomata.automaton.label.CALabel;

/**
 * The agreement predicate over MSCATransitions
 * @author Davide Basile
 *
 */
public class Agreement extends  AgreementModelChecking<CALabel> {

	@Override
	public boolean test(CALabel l) {
		return !l.isRequest();
	}

}
