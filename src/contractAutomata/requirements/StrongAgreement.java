package contractAutomata.requirements;

import java.util.function.Predicate;

import contractAutomata.automaton.transition.MSCATransition;

/**
 * The strong agreement predicate over MSCATransitions
 * 
 * @author Davide Basile
 *
 */
public class StrongAgreement implements Predicate<MSCATransition> {

	@Override
	public boolean test(MSCATransition t) {
		return t.getLabel().isMatch();
	}

}
