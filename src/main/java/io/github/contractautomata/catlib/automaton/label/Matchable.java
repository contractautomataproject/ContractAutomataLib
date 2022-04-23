package io.github.contractautomata.catlib.automaton.label;

/**
 * Interface for a matchable element. <br>
 * This interface is implemented by all classes that <br>
 * provide a match method to match other objects of type T. <br>
 * 
 * @author Davide Basile
 *
 * @param <T> the type of the object to match with
 */
public interface Matchable<T> {
	/**
	 * Returns true if this object matches with arg
	 * @param arg the object to match of type T
	 * @return true if this object matches with arg
	 */
	boolean match(T arg);
}
