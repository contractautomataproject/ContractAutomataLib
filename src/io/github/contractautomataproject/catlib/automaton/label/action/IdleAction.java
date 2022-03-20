package io.github.contractautomataproject.catlib.automaton.label.action;

public class IdleAction extends Action{

    public static final String IDLE="-";

    public IdleAction() {
        super(IDLE);
    }

    public static boolean isIdle(String action) {
        return action.equals(IDLE);
    }

}
