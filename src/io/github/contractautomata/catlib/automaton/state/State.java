package io.github.contractautomata.catlib.automaton.state;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Class implementing a state of an Automaton. <br>
 * A state is a tuple (list) of basic states of principals. <br>
 * A state has a rank. Rank 1 is for an ensemble containing a single principal. <br>
 * A rank greater than one is for an ensemble of states of principals. <br>
 * 
 * @author Davide Basile
 *
 *
 * @param <T> generic type of the content the basic states
 * 
 */
public class State<T> extends AbstractState<List<BasicState<T>>> {

	/**
	 * Constructor for a State
	 * @param listState  the list of basic states
	 */
	public State(List<BasicState<T>> listState){
		super(listState);
		if (listState.isEmpty() || listState.stream().anyMatch(Objects::isNull))
			throw new IllegalArgumentException();
	}

	/**
	 * Method inherited from the interface Ranked.
	 * It returns the rank of the state.
	 * @return the rank of the state
	 */
	@Override
	public Integer getRank() {
		return this.getState().size();
	}

	/**
	 * Returns true if the state is initial
	 * @return true if the state is initial
	 */
	@Override
	public boolean isInitial() {
		return this.getState().stream().allMatch(BasicState::isInitial);
	}

	/**
	 * Returns true if the state is final
	 * @return true if the state is final
	 */
	@Override
	public boolean isFinalState() {
		return this.getState().stream().allMatch(BasicState::isFinalState);
	}

	/**
	 * Getter of the content of this state
	 * @return the list of basic states
	 */
	@Override
	public  List<BasicState<T>> getState() {
		return new ArrayList<>(super.getState());
	}


	/**
	 * Print a String representing this object
	 * @return a String representing this object
	 */
	@Override
	public String toString() {
		return this.getState().stream()
				.map(BasicState<T>::getState)
				.collect(Collectors.toList()).toString();
	}

	// equals could cause errors of duplication of states in transitions to go undetected. 	

}