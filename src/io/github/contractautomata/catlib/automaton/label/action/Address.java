package io.github.contractautomata.catlib.automaton.label.action;

import io.github.contractautomata.catlib.automaton.label.Matchable;

import java.util.Objects;

/**
 * Class implementing the address of an action.
 * An address is formed by a sender and a receiver.
 * Two addresses are matching if they have the same sender and receiver.
 * Addressed actions are using this class to represent the address of the action.
 *
 * @author Davide Basile
 */
public class Address implements Matchable<Address> {

    /**
     * the sender
     */
    private final String sender;

    /**
     * the receiver
     */
    private final String receiver;

    /**
     * constant symbol used for separating the sender from the receiver
     */
    public static final String ID_SEPARATOR = "_";

    /**
     * constant symbol used for separating the address from the action
     */
    public static final String ACTION_SEPARATOR = "@";

    /**
     * Constructor for an address
     * @param sender the sender, must be non-null and non-empty
     * @param receiver the receiver, must be non-null and non-empty
     */
    public Address(String sender, String receiver) {
        Objects.requireNonNull(sender);
        Objects.requireNonNull(receiver);

        this.sender = sender;
        this.receiver = receiver;

        if (this.sender.isEmpty()||this.receiver.isEmpty())
            throw new IllegalArgumentException();
    }

    /**
     * Overrides the method of the object class
     * @param o the other object to compare to
     * @return true if the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Address address = (Address) o;
        return Objects.equals(sender, address.sender) && Objects.equals(receiver, address.receiver);
    }


    /**
     * Overrides the method of the object class
     * @return the hashcode of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(sender, receiver);
    }


    /**
     * Print a String representing this object
     * @return a String representing this object
     */
    @Override
    public String toString() {
        return this.sender +ID_SEPARATOR+this.receiver +ACTION_SEPARATOR;
    }

    /**
     * Two addresses are matching if they have the same sender and receiver.
     * @param arg the other address to match
     * @return true if the addresses are matching
     */
    @Override
    public boolean match(Address arg) {
        return sender.equals(arg.sender)&&receiver.equals(arg.receiver);
    }
}
