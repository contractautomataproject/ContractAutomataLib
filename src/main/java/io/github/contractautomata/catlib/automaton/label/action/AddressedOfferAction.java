package io.github.contractautomata.catlib.automaton.label.action;

import java.util.Objects;

/**
 * Class implementing an addressed offer action.
 * It extends offer action and implements addressed action.
 *
 * @author  Davide Basile
 */
public class AddressedOfferAction extends OfferAction implements AddressedAction{

    /**
     * the address of the action
     */
    private final Address address;

    /**
     * Constructor for an addressed offer action
     * @param label the label of the action
     * @param address the address of the action
     */
    public AddressedOfferAction(String label, Address address) {
        super(label);
        Objects.requireNonNull(address);
        this.address=address;
    }

    /**
     * Getter of the address of this action
     * @return the address of this action
     */
    @Override
    public Address getAddress() {
        return address;
    }

    /**
     * Redefinition of the match of an action.
     * Returns true if arg is an addressed action, the corresponding
     * addresses are matching as well as their super classes.
     * For example, an addressed offer action matches an addressed request action
     * if both addresses are matching and the offer is matching the request.
     *
     * @param arg the other action to match
     * @return true if the two actions are matching.
     */
    @Override
    public boolean match(Action arg) {
        return  (arg instanceof AddressedAction) && 
        		address.match(((AddressedAction)arg).getAddress()) && 
        		super.match(arg);
    }


    /**
     * Print a String representing this object
     * @return a String representing this object
     */
    @Override
    public String toString() {
        return address.toString()+super.toString();
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
        if (!super.equals(o)) return false;
        AddressedOfferAction that = (AddressedOfferAction) o;
        return Objects.equals(address, that.address);
    }


    /**
     * Overrides the method of the object class
     * @return the hashcode of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), address);
    }
}
