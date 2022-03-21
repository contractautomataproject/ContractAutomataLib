package io.github.contractautomataproject.catlib.automaton.label.action;

import io.github.contractautomataproject.catlib.automaton.label.Matchable;

import java.util.Objects;

public class RequestAction extends Action {

    private static final String REQUEST="?";

    public RequestAction(String label) {
        super(label);
    }

    @Override
    public String toString(){
        return  REQUEST+this.getLabel();
    }

    @Override
    public boolean match(Action arg) {
        return arg instanceof OfferAction && super.match(arg);
    }

    public static Action parseAction(String act) {
        if (isRequest(act))
            return new RequestAction(act.substring(1));

        throw new IllegalArgumentException();
    }

    public static boolean isRequest(String action) {
        return action.startsWith(REQUEST) && action.length()>1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(REQUEST+this.getLabel());
    }
}
