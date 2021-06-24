package contractAutomata.automaton.state;

import contractAutomata.automaton.Ranked;

public abstract class State<T> implements Ranked {
	final private T state;
	
	public State(T label) {
		if (label==null)
			throw new IllegalArgumentException();
		
		this.state=label;
	}
	
	public abstract boolean isFinalstate();
	
	public abstract boolean isInitial();
	
	public abstract void setInitial(boolean init);
	
	public T getState() {
		return state;
	}
}
