package io.github.contractautomata.catlib.operations.interfaces;

/**
 * A predicate over three arguments.  * Used in the synthesis method of MSCA for readability. 
 * @author Davide Basile
 *
 * @param <T> generic type of the first argument
 * @param <U> generic type of the second argument
 * @param <V> generic type of the third argument
 */
public interface TriPredicate<T,U,V> {
	boolean test(T arg1, U arg2, V arg3);
}

