package io.github.contractautomata.catlib.automaton.state;

import io.github.contractautomata.catlib.automaton.Ranked;

/**
 * Class implementing an abstract state of an automaton. <br>
 * An abstract state can be either initial or final, or none, <br>
 * and has a label (its content). <br>
 *
 * @author Davide Basile
 *
 * @param <T> generic type of the content of the state
 */
public abstract class AbstractState<T> implements Ranked {

	/**
	 * the content of the state
	 */
	private final T label;

	/**
	 * Constructs an abstract state from its label (content).
	 * Label must be non-null
	 * @param label the content of the state
	 */
	protected AbstractState(T label) {
		if (label==null)
			throw new IllegalArgumentException();
		
		this.label=label;
	}

	/**
	 * Getter of the content (of type T) of the state
	 * @return the content of the state
	 */
	public T getState() {
		return label;
	}

	/**
	 * Returns true if the state is final
	 * @return true if the state is final
	 */
	public abstract boolean isFinalState();

	/**
	 * Returns true if the state is initial
	 * @return true if the state is initial
	 */
	public abstract boolean isInitial();
}