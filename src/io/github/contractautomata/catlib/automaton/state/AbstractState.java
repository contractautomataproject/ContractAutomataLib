package io.github.contractautomata.catlib.automaton.state;

import io.github.contractautomata.catlib.automaton.Ranked;

/**
 * class encoding a state
 * 
 * @author Davide Basile
 *
 * @param <T> generic type of the instance variable of the state
 */
public abstract class AbstractState<T> implements Ranked {
	private final T label;
	
	protected AbstractState(T label) {
		if (label==null)
			throw new IllegalArgumentException();
		
		this.label=label;
	}
	
	public T getState() {
		return label;
	}
	
	public abstract boolean isFinalState();
	
	public abstract boolean isInitial();
}