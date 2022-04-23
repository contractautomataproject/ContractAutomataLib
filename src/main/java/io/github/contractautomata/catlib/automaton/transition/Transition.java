package io.github.contractautomata.catlib.automaton.transition;

import io.github.contractautomata.catlib.automaton.Ranked;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.state.State;

import java.util.Objects;

/**
 * Class implementing a Transition of an Automaton. <br>
 * States and Labels are generics, and must inherit from the corresponding <br>
 * super class. <br>
 * 
 * @author Davide Basile
 *
 * @param <S1> generic type of the content of S
 * @param <L1> generic type of the content of L
 * @param <S> generic type of the state
 * @param <L> generic type of the label 
 */
public class Transition<S1,L1, S extends State<S1>,L extends Label<L1>> {
	/**
	 * the source state
	 */
	private final S source;

	/**
	 * the target state
	 */
	private final S target;

	/**
	 * the label
	 */
	private final L label;


	/**
	 * Constructing a transition from a source and target states and a label
	 * Parameters must be non-null, and must have the same rank.
	 *
	 * @param source  the source state
	 * @param label the label
	 * @param target the target state
	 */
	public Transition(S source, L label, S target){
		check(source,label,target);
		this.source=source;
		this.label=label;
		this.target=target;
	}

	private void check(S source, L label, S target) {
		if (source==null || label==null || target==null)
			throw new IllegalArgumentException("source, label or target null");
		if (!(source.getRank().equals(target.getRank())&&label.getRank().equals(source.getRank()))) 
			throw new IllegalArgumentException("source, label or target with different ranks");
	}

	/**
	 * Getter of source state
	 * @return source state
	 */
	public S getSource()
	{
		return source;
	}

	/**
	 * Getter of target state
	 * @return target state
	 */
	public S getTarget()
	{
		return target;
	}

	/**
	 * Getter of label
	 * @return label
	 */
	public L getLabel()
	{
		return label;
	}

	/**
	 * Method inherited from the interface Ranked.
	 * It returns the rank of the transition.
	 * @return the rank of the transition
	 */
	public Integer getRank()
	{
		return label.getRank();
	}


	/**
	 * Overrides the method of the object class
	 * @return the hashcode of this object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(source.hashCode(),label.hashCode(),target.hashCode());
	}


	/**
	 * Overrides the method of the object class
	 * @param obj the other object to compare to
	 * @return true if the two objects are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transition<?,?,? extends Ranked, ? extends Ranked> other = (Transition<?,?,?, ?>) obj;
		return label.equals(other.getLabel())&&source.equals(other.getSource())&&target.equals(other.getTarget());
	}


	/**
	 * Print a String representing this object
	 * @return a String representing this object
	 */
	@Override
	public String toString() {
		return "("+source+","+label+","+target+")";
	}

}



