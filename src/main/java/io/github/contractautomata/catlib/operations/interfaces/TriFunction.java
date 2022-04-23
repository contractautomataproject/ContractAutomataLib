package io.github.contractautomata.catlib.operations.interfaces;

/**
 * A function over three arguments.
 * 
 * @author Davide Basile
 *
 * @param <T> first argument
 * @param <U> second argument
 * @param <V> third argument
 * @param <Z> returned class
 */
public interface TriFunction<T,U,V,Z> {
	Z apply(T arg1, U arg2, V arg3);
}
