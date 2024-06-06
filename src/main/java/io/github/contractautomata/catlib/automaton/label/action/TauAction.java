package io.github.contractautomata.catlib.automaton.label.action;

import java.util.Objects;

/**
 * Class implementing a request action.
 *
 * @author Davide Basile
 */
public class TauAction extends Action {

    /**
     * Constant symbol denoting a tau move
     */
    public static final String TAU="tau_";

    /**
     * Constructor for a tau action
     * @param label the label of this action
     */
    public TauAction(String label) {
        super(label);
    }


    /**
     * Print a String representing this object
     * @return a String representing this object
     */
    @Override
    public String toString(){
        return  TAU+this.getLabel();
    }


    /**
     * A tau action matches no action.
     * @param arg the other action to match
     * @return true if this actions matches arg
     */
    @Override
    public boolean match(Action arg) {
        return false;
    }


    /**
     * Overrides the method of the object class
     * @return the hashcode of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(TAU+this.getLabel());
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
