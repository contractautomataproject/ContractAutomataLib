package io.github.contractautomataproject.catlib.automaton.label.action;

import io.github.contractautomataproject.catlib.automaton.label.Matchable;

import java.util.Objects;

public class Action implements Matchable<Action> {

    private final String label;

    public Action(String label) {
        Objects.requireNonNull(label);
        this.label = label;
    }

    @Override
    public String toString() {
        return this.getLabel();
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean match(Action arg) {
        return label.equals(arg.label);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return Objects.equals(label, action.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }
}
