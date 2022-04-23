package io.github.contractautomata.catlib.automaton.label.action;

import io.github.contractautomata.catlib.automaton.label.Matchable;

import java.util.Objects;

/**
 * Class implementing an action of a label.
 * Actions are matchable, i.e., they can match other actions.
 *
 * @author Davide Basile
 */
public class Action implements Matchable<Action> {

    /**
     * the content/label of this action
     */
    private final String label;

    /**
     * Constructor for an action.
     *
     * @param label the label of the action, must be non-null
     */
    public Action(String label) {
        Objects.requireNonNull(label);
        this.label = label;
    }


    /**
     * Print a String representing this object
     * @return a String representing this object
     */
    @Override
    public String toString() {
        return this.getLabel();
    }

    /**
     * Getter of the content of this action
     * @return the label of this action
     */
    public String getLabel() {
        return label;
    }

    /**
     * Implementation of the interface Matchable.
     * True if this action is matching arg.
     * Two actions match if they have the same content.
     *
     * @param arg the other action to match
     * @return true if this action matches arg
     */
    @Override
    public boolean match(Action arg) {
        return label.equals(arg.getLabel());
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
        Action action = (Action) o;
        return Objects.equals(label, action.label);
    }


    /**
     * Overrides the method of the object class
     * @return the hashcode of this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(label);
    }
}
