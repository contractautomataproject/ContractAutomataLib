package io.github.contractautomataproject.catlib.automaton.label.action;

public class IdleAction extends Action{

    private static final String IDLE="-";

    public IdleAction() {
        super(IDLE);
    }

    public static boolean isIdle(String action) {
        return action.equals(IDLE);
    }

    public static Action parseAction(String act) {
        if (isIdle(act))
            return new IdleAction();
        else throw new IllegalArgumentException();
    }
}
