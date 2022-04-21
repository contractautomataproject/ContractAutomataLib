package io.github.contractautomata.catlib.automaton.label;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.contractautomata.catlib.automaton.Ranked;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;

/**
 * Class representing a Label of a transition. <br>
 * Each label contains a tuple of elements of unconstrained generic type, its content. <br>
 * The rank is the number of elements in the labels. Labels can be matched by other labels thanks <br>
 * to the Matchable interface. <br>
 *
 * @author Davide Basile
 *
 */
public class Label<T> implements Ranked,Matchable<Label<T>>{
	
	/**
	 * the content of the label
	 */
	private final List<T> content;

	/**
	 * Constructor for a label
	 *
	 * @param content the content of the label
	 */
	public Label(List<T> content) {
		super();
		if (content ==null || content.isEmpty())
			throw new IllegalArgumentException();
		this.content = new ArrayList<>(content);
	}

	/**
	 * Getter of the content of this label
	 * @return the content of this label
	 */
	public List<T> getContent() {
		return new ArrayList<>(content);
	}


	/**
	 * This method requires a label to be a list of actions, and requires
	 * the actions in the label to be either idle or all equals, and at least
	 * one action must not be idle.
	 * It returns the unique action.
	 *
	 * @return  the (unique) action of the label
	 */
	public Action getAction(){
		Action act = (Action) this.content.stream()
				.filter(a->!(a instanceof IdleAction) && (a instanceof Action))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);//someone must not be idle

		if  (!this.content.stream()
				.filter(a->!(a instanceof IdleAction))
				.allMatch(l->((Action) l).getLabel().equals(act.getLabel())))
			throw new IllegalArgumentException();

		return act;
	}

	/**
	 * Implementation of the match method of the Matchable interface.
	 * Two labels match if their content is equal.
	 *
	 * @param arg  the other label to match.
	 * @return	true if this label matches with arg label.
	 */
	@Override
	public boolean match(Label<T> arg) {
		return this.content.equals(arg.content);
	}


	/**
	 * Overrides the method of the object class
	 * @return the hashcode of this object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(content);
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
		if (obj == null ||getClass() != obj.getClass())
			return false;
		return Objects.equals(content, ((Label<?>) obj).content);
	}


	/**
	 * Print a String representing this object
	 * @return a String representing this object
	 */
	@Override
	public String toString() {
		return content.toString();
	}

	/**
	 * Method inherited from the interface Ranked.
	 * It returns the rank of the label.
	 * @return the rank of the label.
	 */
	@Override
	public Integer getRank() {
		return content.size();
	}
}