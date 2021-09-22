package contractAutomata.automaton.label;

import java.util.Objects;

import contractAutomata.automaton.Ranked;

/**
 * Class representing a Label of a transition
 * 
 * @author Davide Basile
 *
 */
public class Label implements Ranked,Matchable<Label>{
	
	/**
	 * the action performed by the label
	 */
	private final String action;

	public Label(String action) {
		super();
		if (action==null || action.length()==0)
			throw new IllegalArgumentException();
		this.action = action;
	}

	public String getAction() {
		return action;
	}
	
	@Override
	public boolean match(Label arg) {
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
		return Objects.equals(action, ((Label) obj).action);
	}

	@Override
	public String toString() {
		return action;
	}	
}