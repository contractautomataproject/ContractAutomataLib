package io.github.contractautomata.catlib.automaton.label.action;

/**
 * Interface for an addressed action.
 * An addressed action must provide a method to retrieve the corresponding address.
 *
 * @author Davide Basile
 */
public interface AddressedAction {
    /**
     * Returns the address of this object
     * @return the address of this object
     */
    Address getAddress();
}
