package io.github.contractautomata.catlib.automaton.state;

import java.util.List;

/**
 * Class implementing a BasicState of an Automaton. <br>
 * A BasicState implements an AbstractState of rank 1, i.e., <br>
 * it is the internal state of a single principal. <br>
 * 
 * @author Davide Basile
 *
 * @param <T> generic type of the content of the basic state
 */
public class BasicState<T> extends AbstractState<T>{
	/**
	 * the flag signalling if the state is initial
	 */
	private final boolean init;

	/**
	 * the flag signalling if the state is final
	 */
	private final boolean fin;

	/**
	 * Constructor for a BasicState.
	 * Label must not be a list of elements, and elements
	 * cannot be instances of abstract state.
	 * In other words, a basic state cannot contain inner states.
	 *
	 * @param label the content of the state
	 * @param init  true if it is initial
	 * @param fin true if it is final
	 */
	public BasicState(T label, Boolean init, Boolean fin) {
		super(label);
		if (label instanceof List<?> && ((List<?>)label).get(0) instanceof AbstractState)
			throw new UnsupportedOperationException();
		this.init=init;
		this.fin=fin;
	}

	/**
	 * Method inherited from the interface Ranked.
	 * The rank of the basic state is always one.
	 * @return the rank of the basic state, always one.
	 */
	@Override
	public Integer getRank() {
		return 1;
	}

	/**
	 * Returns true if the state is final
	 * @return true if the state is final
	 */
	@Override
	public boolean isFinalState() {
		return fin;
	}

	/**
	 * Returns true if the state is initial
	 * @return true if the state is initial
	 */
	@Override
	public boolean isInitial() {
		return init;
	}

	/**
	 * Print a String representing this object
	 * @return a String representing this object
	 */
	@Override
	public String toString() {
		String finalstate= (this.isFinalState())?",final=true":"";
		String initial= (this.isInitial())?",initial=true":"";
		return "label="+this.getState()+finalstate+initial;
	}
	
	// equals could cause errors of duplication of states in transitions to go undetected. 	
}