package io.github.contractautomataproject.catlib.operators;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.label.action.Action;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Class implementing the model checking function. 
 * This is implemented by instantiating and applying the composition function, 
 * composing the model with the property.
 * 
 * @author Davide Basile
 *
 */
public class ModelCheckingFunction<S1> extends CompositionFunction<S1,State<S1>,Label<Action>,
ModalTransition<S1,Action,State<S1>,Label<Action>>,Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,Label<Action>>>>

{

	public ModelCheckingFunction(Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>> aut,
			Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,Label<Action>>> prop,
			Predicate<Label<Action>> pruningPred) {
		super(Arrays.asList(new Automaton<>(aut.getTransition() //converting calabels to Label<S1>
				.parallelStream()
				.map(t->{Label<Action> lab = t.getLabel();
					return new ModalTransition<>(t.getSource(),lab,t.getTarget(),t.getModality());})
				.collect(Collectors.toSet())),prop),
				(l1,l2)->new CALabel(l1.getLabel()).getAction().getLabel().equals(l2.getLabel().get(0).getLabel()), //match
				State::new, ModalTransition::new, Label::new,Automaton::new,pruningPred);

	}
}