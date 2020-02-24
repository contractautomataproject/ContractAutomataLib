package FSA;

public class State {
	private boolean initial;
	private boolean accepting;
	public State() {
		this.initial = false;
		this.accepting=false;
	}
	public boolean isInitial() {
		return initial;
	}
	public void setInitial(boolean initial) {
		this.initial = initial;
	}
	public boolean isAccepting() {
		return accepting;
	}
	public void setAccepting(boolean accepting) {
		this.accepting = accepting;
	}
	

}
