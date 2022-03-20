package io.github.contractautomataproject.catlib.automaton.label.action;

import java.util.Objects;

public class RequestAction extends Action{

    public static final String REQUEST="?";

    public RequestAction(String label) {
        super(label);
    }

    public static boolean isRequest(String action) {
        return action.startsWith(REQUEST) && action.length()>1;
    }

    @Override
    public String toString(){
        return REQUEST+super.toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(REQUEST+this.getLabel());
    }

    public static Action parseAction(String act) {
        if (isRequest(act))
            return new RequestAction(act.substring(1));
        else throw new IllegalArgumentException();
    }
}
