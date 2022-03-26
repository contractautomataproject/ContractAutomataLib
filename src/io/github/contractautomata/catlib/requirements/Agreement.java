package io.github.contractautomata.catlib.requirements;

import io.github.contractautomata.catlib.automaton.label.CALabel;

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
