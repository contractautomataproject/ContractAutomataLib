package io.github.contractautomataproject.catlib.automaton.label;

import java.util.Collection;
import java.util.Objects;

import io.github.contractautomataproject.catlib.automaton.Ranked;

/**
 * Class representing a Label of a transition
 * 
 * @author Davide Basile
 *
 */
public class Label<T> implements Ranked,Matchable<Label<T>>{
	
	/**
	 * the action performed by the label
	 */
	private final T action;

	public Label(T action) {
		super();
		if (action==null)
			throw new IllegalArgumentException();
		this.action = action;
	}
	

	public T getAction() {
		return action;
	}
	
	@Override
	public boolean match(Label<T> arg) {
		return this.action.equals(arg.action);
	}

	@Override
	public int hashCode() {
		return Objects.hash(action);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null ||getClass() != obj.getClass())
			return false;
		return Objects.equals(action, ((Label<?>) obj).action);
	}

	@Override
	public String toString() {
		return action.toString();
	}	
	
	public String toCSV() {
		return "[action=" +action+"]";
	}
	
	@Override
	public Integer getRank() {
		if (action instanceof Collection<?>) {
			return ((Collection<?>)action).size();
		}
		return 1;
	}
}