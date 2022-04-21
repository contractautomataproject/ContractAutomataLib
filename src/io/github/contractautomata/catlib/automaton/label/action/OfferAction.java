package io.github.contractautomata.catlib.automaton.label.action;

import java.util.Objects;

/**
 * Class implementing an offer action.
 *
 * @author Davide Basile
 */
public class OfferAction extends Action  {

    /**
     * Constant symbol denoting an offer
     */
    public static final String OFFER="!";

    /**
     * Constructor for an offer action
     * @param label the label of the action
     */
    public OfferAction(String label) {
        super(label);
    }


    /**
     * Print a String representing this object
     * @return a String representing this object
     */
    @Override
    public String toString(){
       return  OFFER+this.getLabel();
    }

    /**
     * An offer action matches a request action with the same label.
     * @param arg the other action to match
     * @return true if this actions matches arg
     */
    @Override
    public boolean match(Action arg) {
        return (arg instanceof RequestAction) && super.match(arg);
    }

    /**
     * Overrides the method of the object class
     * @return the hashcode of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(OFFER+this.getLabel());
    }


    /**
     * Overrides the method of the object class
     * @param o the other object to compare to
     * @return true if the two objects are equal
     */
    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }
}
