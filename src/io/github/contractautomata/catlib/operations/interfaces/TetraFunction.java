package io.github.contractautomata.catlib.operations.interfaces;

/**
 * A function over four arguments.
 *
 * @param <T>
 * @param <U>
 * @param <V>
 * @param <W>
 * @param <Z>
 */
public interface TetraFunction<T,U,V,W,Z> {
    Z apply(T arg1, U arg2, V arg3,W arg4);
}
