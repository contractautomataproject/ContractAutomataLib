package io.github.contractautomataproject.catlib.automaton.state;

import io.github.contractautomataproject.catlib.automaton.Ranked;

/**
 * class encoding a state
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
	
	public T getState() {
		return state;
	}
	
	public abstract boolean isFinalstate();
	
	public abstract boolean isInitial();

	public abstract String toCSV();

}

//@Override
//public int hashCode() {
//	return Objects.hash(state.hashCode());
//}
//
// equals could cause errors of duplication of states in transitions to go undetected. 	
//@Override
//public boolean equals(Object obj) {
//	if (this == obj)
//		return true;
//	if (obj == null)
//		return false;
//	if (getClass() != obj.getClass())
//		return false;
//	State<?> other = (State<?>) obj;
//	if ((state == null) && (other.state != null))
//			return false;
//	return (state.equals(other.state));
//}
//public abstract <U extends State<T>> U getCopy();

