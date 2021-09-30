package io.github.davidebasile.contractautomata.automaton;

/**
 * Interface of a ranked object
 * 
 * @author Davide Basile
 *
 */
public interface Ranked {
	public default Integer getRank() { return 1;}
}
