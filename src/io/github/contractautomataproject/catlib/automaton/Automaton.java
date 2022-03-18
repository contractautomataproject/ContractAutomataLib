package io.github.contractautomataproject.catlib.automaton;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.AbstractState;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.transition.Transition;


/**
 * Class representing an Automaton
 * 
 * @author Davide Basile
 *
 * @param <S1> the generic type in State<S1>
 * @param <L1> the generic type in Label<L1>
 * @param <S> the generic type of states
 * @param <T> the generic type of transitions
 */
public class Automaton<S1,L1, S extends State<S1>,T extends Transition<S1,L1,S,? extends Label<L1>>> implements Ranked
{ 

	/**
	 * transitions of the automaton
	 */
	private final Set<T> tra;

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

		Set<S> states = this.getStates();

		if (states.parallelStream()
				.filter(S::isInitial)
				.count()!=1)
			throw new IllegalArgumentException("Not Exactly one Initial State found! ");

		if (states.parallelStream()
				.noneMatch(AbstractState::isFinalstate))
			throw new IllegalArgumentException("No Final States!");
		
		if(this.getStates().stream()
				.anyMatch(x-> states.stream()
						.filter(y->x!=y && x.getState().equals(y.getState()))
						.count()!=0))
			throw new IllegalArgumentException("Transitions have ambiguous states (different objects for the same state).");

	}

	public  Set<T> getTransition()
	{
		return new HashSet<>(tra);
	}

	/**
	 * @return all  states that appear in at least one transition
	 */
	public final Set<S> getStates()
	{
		return tra.parallelStream()
				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
				.collect(Collectors.toSet()); //without equals, duplicates objects are detected
	}
	
	/**
	 * 
	 * @return a map where for each entry the key is the index of principal, and the value is its set of basic states
	 */
	public Map<Integer,Set<BasicState<S1>>> getBasicStates()
	{

		return this.getStates().stream()
				.flatMap(cs->cs.getState().stream()
						.map(bs->new AbstractMap.SimpleEntry<Integer,BasicState<S1>>(cs.getState().indexOf(bs),bs)))
				.collect(Collectors.groupingBy(Entry::getKey, Collectors.mapping(Entry::getValue, Collectors.toSet())));

	}


	public S getInitial()
	{
		return  tra.parallelStream()
				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
				.filter(S::isInitial)
				.findFirst().orElseThrow(NullPointerException::new);
	}

	@Override
	public Integer getRank()
	{
		return this.tra.iterator().next().getRank();
	}
	
	public int getNumStates()
	{
		return this.getStates().size();
	}

	/**
	 * 
	 * @param source source state of the forward star
	 * @return set of transitions outgoing state source
	 */
	public Set<T> getForwardStar(AbstractState<?> source) {
		return this.tra.parallelStream()
				.filter(x->x.getSource().equals(source))
				.collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		StringBuilder pr = new StringBuilder();
		int rank = this.getRank();
		pr.append("Rank: "+rank+System.lineSeparator());
		pr.append("Initial state: " +this.getInitial().print()+System.lineSeparator());
		pr.append("Final states: ["); 
		for (int i=0;i<this.getRank();i++) 
			pr.append(Arrays.toString(
					this.getBasicStates().get(i).stream()
					.filter(BasicState<S1>::isFinalstate)
					.map(BasicState<S1>::getState)
					.sorted()
					.toArray()));
		pr.append("]"+System.lineSeparator());
		pr.append("Transitions: "+System.lineSeparator());
		this.getTransition().stream()
		.sorted((t1,t2)->t1.toString().compareTo(t2.toString()))
		.forEach(t->pr.append(t.print()+System.lineSeparator()));
		return pr.toString();
	}
}