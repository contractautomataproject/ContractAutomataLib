package CA;

public class State {
	private boolean accepting;
	private boolean initial;
	
	
	public State(boolean accepting, boolean initial) {
		super();
		this.accepting = accepting;
		this.initial = initial;
	}
	
	public boolean isAccepting() {
		return accepting;
	}
	
	public void setAccepting(boolean accepting) {
		this.accepting = accepting;
	}
	
	public boolean isInitial() {
		return initial;
	}
	
	public void setInitial(boolean initial) {
		this.initial = initial;
	}
}
