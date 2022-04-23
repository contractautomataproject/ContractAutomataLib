package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.operations.interfaces.TetraFunction;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.IntStream;

/**
 * Class implementing the Model Checking Function. <br>
 * This is implemented by instantiating the <tt>CompositionFunction</tt> to the case where
 * two automata are composed: the first is a contract automaton, whilst the second is a generic
 * automaton describing a property. <br>
 * The output is a synchronous product between the contract automaton and the property. <br>
 *
 *     @param <S1> the generic type of the content of states
 *     @param <S> the generic type of states, must be a subtype of <tt>State&lt;S1&gt;</tt>
 *     @param <L> the generic type of the labels, must be a subtype of <tt>Label&lt;Action&gt;</tt>
 *     @param <T> the generic type of a transitions, must be a subtype of <tt>ModalTransition&lt;S1,Action,S,L&gt;</tt>
 *     @param <A> the generic type of the automata, must be a subtype of <tt>Automaton&lt;S1,Action,S,T &gt;</tt>
 *
 *
 * @author Davide Basile
 *
 */
public class ModelCheckingFunction<S1,S extends State<S1>,L extends Label<Action>,
		T extends ModalTransition<S1,Action,S,L>,A extends Automaton<S1,Action,S,T>>
		extends CompositionFunction<S1,S,L,T,A>

{
	/**
	 * The constructor of a model checking function.<br>
	 * The match function of <tt>CompositionFunction</tt> is instantiated to match two labels with
	 * the same action content (in the style of a synchronous product). <br>
	 * The pruning predicate of <tt>CompositionFunction</tt> is instantiated to prune labels of transitions
	 * where the automaton is not synchronizing with the property (and vice-versa). <br>
	 * The rank of the property must be 1. <br>
	 *
	 * @param aut  the automaton to verify
	 * @param prop the property to verify
	 * @param createState	a function with argument the list of operands state, and as result the composed state
	 * @param createTransition	a function taking as arguments the composed source state, composed label, composed target state and composed modality, and returns the created transition
	 * @param createLabel a function taking as arguments a list of actions, and returns the composed label
	 * @param createAutomaton a function taking as argument the set of transitions of the composition, and returns the composed automaton
	 */
	public ModelCheckingFunction(A aut, A prop,
						  Function<List<BasicState<S1>>,S> createState,
						  TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition,
						  Function<List<Action>,L> createLabel,
						  Function<Set<T>,A> createAutomaton) {
		super(Arrays.asList(aut, prop),
				(l1,l2)-> l1.getAction()
						.getLabel()
						.equals(l2.getContent().get(0).getLabel()),
				createState, createTransition, createLabel,createAutomaton,
				l->{	List<Action> listAct = l.getContent();
					return ((listAct.get(l.getRank()-1) instanceof IdleAction)||
							IntStream.range(0, l.getRank()-1)
									.mapToObj(listAct::get)
									.allMatch(IdleAction.class::isInstance));});

		if (prop.getRank()!=1)
			throw new IllegalArgumentException();

	}

}