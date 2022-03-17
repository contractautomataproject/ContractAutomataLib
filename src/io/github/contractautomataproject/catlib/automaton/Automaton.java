package io.github.contractautomataproject.catlib.automaton;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.contractautomataproject.catlib.automaton.label.Label;
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
public class Automaton<S1,L1,S extends State<S1>,T extends Transition<S1,L1,S,? extends Label<L1>>> implements Ranked
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
				.noneMatch(State::isFinalstate))
			throw new IllegalArgumentException("No Final States!");
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
	public Set<T> getForwardStar(State<?> source) {
		return this.tra.parallelStream()
				.filter(x->x.getSource().equals(source))
				.collect(Collectors.toSet());
	}

	@Override
	public String toString() {
		StringBuilder pr = new StringBuilder();
		int rank = this.getRank();
		pr.append("Rank: "+rank+System.lineSeparator());
		pr.append("Initial state: " +this.getInitial().getState().toString()+System.lineSeparator());
		pr.append("Final states: ["+printFinalStates()+"]"+System.lineSeparator());
		pr.append("Transitions: "+System.lineSeparator());
		this.tra.stream()
		.sorted((t1,t2)->t1.toString().compareTo(t2.toString()))
		.forEach(t->pr.append("("+t.getSource().getState()+","+t.getLabel().toString()+","+t.getTarget().getState()+")"
				+System.lineSeparator()));
		return pr.toString();
	}

	private String printFinalStates() {
		StringBuilder pr = new StringBuilder();
		Set<S> states = this.getStates();
		for (int i=0;i<this.getRank();i++) {
			pr.append(Arrays.toString(
					states.stream()
					.filter(S::isFinalstate)
					.map(S::getState)
					.sorted((x,y)->x.toString().compareTo(y.toString()))
					.toArray()));
		}
		return pr.toString();
	}
}