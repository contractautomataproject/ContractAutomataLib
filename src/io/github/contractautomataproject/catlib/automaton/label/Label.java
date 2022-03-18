package io.github.contractautomataproject.catlib.automaton.label;

import java.util.ArrayList;
import java.util.List;
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
	private final List<T> action;

	public Label(List<T> action) {
		super();
		if (action==null || action.isEmpty())
			throw new IllegalArgumentException();
		this.action = action;
	}
	

	public List<T> getAction() {
		return new ArrayList<T>(action);
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
	
	@Override
	public Integer getRank() {
		return action.size();
	}
}