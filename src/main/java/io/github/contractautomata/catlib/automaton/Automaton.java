package io.github.contractautomata.catlib.automaton;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.state.AbstractState;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.Transition;


/**
 * This class implements an automaton. <br>
 * An automaton has a set of transitions, a set of states, an initial state and a set of final states. <br>
 * The types of states, transitions, labels of transitions, are all generics and must extend the corresponding <br>
 * super-class. <br>
 * Each automaton object is ranked: it can represent either a single principal, or an ensemble of principals. <br>
 * States and labels are tuples whose size equals the rank of the automaton. <br>
 * 
 * @author Davide Basile
 *
 * @param <S1> the generic type in State&lt;S1&gt;, the content of a state.
 * @param <L1> the generic type in Label&lt;L1&gt;, the content of a label.
 * @param <S> the generic type of states
 * @param <T> the generic type of transitions
 */
public class Automaton<S1,L1, S extends State<S1>,T extends Transition<S1,L1,S,? extends Label<L1>>> implements Ranked
{ 

	/**
	 * The set of transitions of the automaton
	 */
	private final Set<T> tra;

	/**
	 * This constructor builds an automaton from its set of transitions.
	 *
	 * @param tr the set of transitions, required to be non-null, non-empty, without null elements, all transitions having
	 *           the same rank, with exactly one initial state, with at least a final state, and each state must be represented by
	 *           a single object. The set of states is derived from source and target states of each transition.
	 */
	public Automaton(Set<T> tr) 
	{
		Objects.requireNonNull(tr);

		if (tr.isEmpty())
			throw new IllegalArgumentException("No transitions");

		if (tr.parallelStream()
				.anyMatch(Objects::isNull))
			throw new IllegalArgumentException("Null element");

		T tt = tr.stream().findFirst().orElse(null);
		if (tr.parallelStream()
				.anyMatch(t->!t.getRank().equals(tt.getRank())))
			throw new IllegalArgumentException("Transitions with different rank");

		this.tra=new HashSet<>(tr);

		Set<S> states = tra.parallelStream()
				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
				.collect(Collectors.toSet());

		if (states.parallelStream()
				.filter(S::isInitial)
				.count()!=1)
			throw new IllegalArgumentException("Not Exactly one Initial State found! ");

		if (states.parallelStream()
				.noneMatch(AbstractState::isFinalState))
			throw new IllegalArgumentException("No Final States!");
		
		if(states.stream()
				.anyMatch(x-> states.stream()
						.anyMatch(y->x!=y && x.getState().equals(y.getState()))))
			throw new IllegalArgumentException("Transitions have ambiguous states (different objects for the same state).");

	}

	/**
	 * Getter of the set of transitions
	 * @return the set of transitions
	 */
	public  Set<T> getTransition()
	{
		return new HashSet<>(tra);
	}

	/**
	 * Returns the states of the automaton.
	 * @return all states that appear in at least one transition.
	 */
	public Set<S> getStates()
	{
		return tra.parallelStream()
				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
				.collect(Collectors.toSet()); //without equals, duplicates objects are detected
	}
	
	/**
	 * Returns a map where for each entry the key is the index of principal, and the value is its set of basic states.
	 * It is required that states are lists of basic states.
	 * @return a map where for each entry the key is the index of principal, and the value is its set of basic states
	 */
	public Map<Integer,Set<BasicState<S1>>> getBasicStates()
	{
		return this.getStates().stream()
				.flatMap(cs->cs.getState().stream()
						.map(bs-> new AbstractMap.SimpleEntry<>(cs.getState().indexOf(bs), bs)))
				.collect(Collectors.groupingBy(Entry::getKey, Collectors.mapping(Entry::getValue, Collectors.toSet())));

	}

	/**
	 * Returns the unique initial state
	 * @return the unique initial state
	 */
	public S getInitial()
	{
		return  tra.parallelStream()
				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
				.filter(S::isInitial)
				.findFirst().orElseThrow(NullPointerException::new);
	}

	/**
	 * Method inherited from the interface Ranked.
	 * It returns the rank of the automaton.
	 * @return the rank of the automaton
	 */
	@Override
	public Integer getRank()
	{
		return this.tra.iterator().next().getRank();
	}

	/**
	 * It returns the number of states of the automaton.
	 * @return the number of states of the automaton.
	 */
	public int getNumStates()
	{
		return this.getStates().size();
	}

	/**
	 * Returns the set of transitions outgoing from the state source
	 * @param source the source state of the forward star
	 * @return set of transitions outgoing state source
	 */
	public Set<T> getForwardStar(AbstractState<?> source) {
		return this.tra.parallelStream()
				.filter(x->x.getSource().equals(source))
				.collect(Collectors.toSet());
	}


	/**
	 * Print a String representing this object
	 * @return a String representing this object
	 */
	@Override
	public String toString() {
		StringBuilder pr = new StringBuilder();
		int rank = this.getRank();
		pr.append("Rank: ").append(rank).append(System.lineSeparator());
		pr.append("Initial state: ").append(this.getInitial().toString()).append(System.lineSeparator());
		pr.append("Final states: ["); 
		for (int i=0;i<this.getRank();i++) 
			pr.append(Arrays.toString(
					this.getBasicStates().get(i).stream()
					.filter(BasicState::isFinalState)
					.map(BasicState::getState)
					.sorted()
					.toArray()));
		pr.append("]").append(System.lineSeparator());
		pr.append("Committed states: [");
		for (int i=0;i<this.getRank();i++)
			pr.append(Arrays.toString(
					this.getBasicStates().get(i).stream()
							.filter(BasicState::isCommitted)
							.map(BasicState::getState)
							.sorted()
							.toArray()));
		pr.append("]").append(System.lineSeparator());
		pr.append("Transitions: ").append(System.lineSeparator());
		this.getTransition().stream()
				.sorted(Comparator.comparing(T::toString))
				.forEach(t-> pr.append(t).append(System.lineSeparator()));
		return pr.toString();
	}

	/**
	 * true if the automaton is determistic, false otherwise
	 *
	 * @return true if the automaton is determistic, false otherwise
	 */
	public boolean isDeterministic(){
		return this.getTransition().parallelStream()
				.noneMatch(t->this.getTransition().parallelStream()
						.filter(tt->tt!=t)
						.noneMatch(tt->t.getSource().equals(tt.getSource()) && t.getLabel().equals(tt.getLabel())));
	}
}