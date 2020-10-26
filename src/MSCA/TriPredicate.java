package MSCA;

/**
 * used in the synthesis for readability, instead of using a function taking one argument and
 * returning a bipredicate where to apply such argument
 * @author Davide
 *
 * @param <T>
 * @param <U>
 * @param <V>
 */
public interface TriPredicate<T,U,V> {
	public boolean test(T arg1, U arg2, V arg3);
}

