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
	
//	@Override
//	public String toString() {
//		return state.toString();//"[init=" + init + ", fin=" + fin + ", label=" + label + "]";
//	}

	
//	/**
//	 * 
//	 * @return an encoding of the object as comma separated values
//	 */
//	public String toCSV()
//	{
//		return "[state="+state+"]";
//	}
	
//	public abstract <U extends State<T>> U getCopy();
}
