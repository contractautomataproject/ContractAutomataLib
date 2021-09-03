package contractAutomata.automaton;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import contractAutomata.automaton.label.Label;
import contractAutomata.automaton.state.State;
import contractAutomata.automaton.transition.Transition;


/** 
 * Class representing a Modal Service Contract Automaton
 * 
 * 
 * @author Davide Basile
 *
 */
public class Automaton<L, S extends State<L>,T extends Transition<L,S,? extends Label>>
{ 

	/**
	 * transitions of the automaton
	 */
	private final Set<T> tra;

	public Automaton(Set<T> tr) 
	{
		if (tr == null)
			throw new IllegalArgumentException("Null argument");

		if (tr.isEmpty())
			throw new IllegalArgumentException("No transitions");

		if (tr.parallelStream()
				.anyMatch(Objects::isNull))
			throw new IllegalArgumentException("Null element");

		T tt = tr.stream().findFirst().orElse(null);
		if (tr.parallelStream()
				.anyMatch(t->t.getRank()!=tt.getRank()))
			throw new IllegalArgumentException("Transitions with different rank");

		this.tra=tr;

		Set<? extends State<?>> states = this.getStates();

		if (states.parallelStream()
				.filter(State::isInitial)
				.count()!=1)
			throw new IllegalArgumentException("Not Exactly one Initial State found! ");

		if (!states.parallelStream()
				.filter(State::isFinalstate)
				.findAny().isPresent())
			throw new IllegalArgumentException("No Final States!");
	}

	public  Set<T> getTransition()
	{
		return tra;
	}

	/**
	 * @return all  states that appear in at least one transition
	 */
	public Set<S> getStates()
	{
		return this.getTransition().parallelStream()
				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
				.collect(Collectors.toSet()); //without equals, duplicates objects are detected
	}

	public S getInitial()
	{
		return this.getStates().parallelStream()
				.filter(State::isInitial)
				.findFirst().orElseThrow(NullPointerException::new);
	}

	public int getRank()
	{
		return this.getTransition().iterator().next().getRank();
	}
	
	public int getNumStates()
	{
		return this.getStates().size();
	}

	public Set<T> getForwardStar(State<?> source) {
		return this.getTransition().parallelStream()
				.filter(x->x.getSource().equals(source))
				.collect(Collectors.toSet());
	}

//	public Set<String> geActions(){
//		return this.getTransition().parallelStream()
//		.map(t->t.getLabel().getAction())  
//		.collect(Collectors.toSet());
//	}

	@Override
	public String toString() {
		StringBuilder pr = new StringBuilder();
		int rank = this.getRank();
		Set<? extends State<?>> states = this.getStates();
		pr.append("Rank: "+rank+System.lineSeparator());

		pr.append("Initial state: " +this.getInitial().toString()+System.lineSeparator());
		pr.append("Final states: [");
		for (int i=0;i<rank;i++) {
			pr.append(Arrays.toString(
					states.stream()
					.filter(State::isFinalstate)
					.map(State::getState)
					.sorted((x,y)->x.toString().compareTo(y.toString()))
					//.mapToInt(Integer::parseInt)
					.toArray()));
		}
		pr.append("]"+System.lineSeparator());
		pr.append("Transitions: "+System.lineSeparator());
		this.getTransition().stream()
		.sorted((t1,t2)->t1.toString().compareTo(t2.toString()))
		.forEach(t->pr.append(t.toString()+System.lineSeparator()));
		
		return pr.toString();
	}

}


//END OF THE CLASS