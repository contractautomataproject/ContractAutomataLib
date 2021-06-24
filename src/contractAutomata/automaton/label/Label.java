package contractAutomata.automaton.label;

import java.util.Objects;

import contractAutomata.automaton.Ranked;

public class Label implements Ranked,Matchable<Label>{
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Label other = (Label) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return action;
	}	
}