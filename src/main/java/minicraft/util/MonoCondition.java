package minicraft.util;

@FunctionalInterface
public interface MonoCondition<T> {
	boolean check(T arg);
}
