package io.github.davidebasile.contractautomata.automaton.state;

import io.github.davidebasile.contractautomata.automaton.Ranked;

/**
 * Abstract class encoding a state
 * 
 * @author Davide Basile
 *
 * @param <T> generic type of the instance variable of the state
 */
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
