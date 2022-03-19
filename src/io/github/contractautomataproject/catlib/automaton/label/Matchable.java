package io.github.contractautomataproject.catlib.automaton.label;

/**
 * Interface of a matchable object
 * 
 * @author Davide Basile
 *
 * @param <T> the type of the object to match with
 */
public interface Matchable<T> {
	boolean match(T arg);
}
