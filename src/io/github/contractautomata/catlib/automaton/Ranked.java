package io.github.contractautomata.catlib.automaton;

/**
 * This interface is implemented by ranked elements.
 * An element is ranked if it has a rank.
 * An element of rank 1 represents a principal.
 * For ranks greater than one, the corresponding element
 * represents an ensemble of principals.
 * 
 * @author Davide Basile
 *
 */
public interface Ranked {
	/**
	 * Returns the rank of this object
	 * @return the rank of this object
	 */
	Integer getRank();
}
