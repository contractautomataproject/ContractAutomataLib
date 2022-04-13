package io.github.contractautomata.catlib.automaton.label;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.github.contractautomata.catlib.automaton.Ranked;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;

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


	public Action getAction(){
		Action act = (Action) this.label.stream()
				.filter(a->!(a instanceof IdleAction) && (a instanceof Action))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);//someone must not be idle

		if  (!this.label.stream()
				.filter(a->!(a instanceof IdleAction))
				.allMatch(l->((Action) l).getLabel().equals(act.getLabel())))
			throw new IllegalArgumentException();

		return act;
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