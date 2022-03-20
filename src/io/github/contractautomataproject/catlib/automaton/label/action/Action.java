package io.github.contractautomataproject.catlib.automaton.label.action;

import java.util.Objects;

public class Action {

    private final String label;

    public Action(String label) {
        Objects.requireNonNull(label);
        this.label = label;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return label.equals(action.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(label);
    }

    @Override
    public String toString() {
        return label;
    }

    public String getLabel() {
        return label;
    }
}
