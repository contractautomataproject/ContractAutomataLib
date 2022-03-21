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
	private final List<T> label;

	public Label(List<T> label) {
		super();
		if (label==null || label.isEmpty())
			throw new IllegalArgumentException();
		this.label = new ArrayList<>(label);
	}
	

	public List<T> getLabel() {
		return new ArrayList<>(label);
	}
	
	@Override
	public boolean match(Label<T> arg) {
		return this.label.equals(arg.label);
	}

	@Override
	public int hashCode() {
		return Objects.hash(label);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null ||getClass() != obj.getClass())
			return false;
		return Objects.equals(label, ((Label<?>) obj).label);
	}

	@Override
	public String toString() {
		return label.toString();
	}	
	
	@Override
	public Integer getRank() {
		return label.size();
	}
}