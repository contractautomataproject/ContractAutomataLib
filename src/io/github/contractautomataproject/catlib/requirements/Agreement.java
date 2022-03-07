package io.github.contractautomataproject.catlib.requirements;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;

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
