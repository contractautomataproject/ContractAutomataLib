package io.github.contractautomata.catlib.operations.interfaces;

public interface TetraPredicate<T, U, V, Z> {
    boolean test(T arg1, U arg2, V arg3, Z arg4);
}
