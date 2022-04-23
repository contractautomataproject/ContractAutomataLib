package io.github.contractautomata.catlib.automaton.label.action;

/**
 * Class implementing an idle action.
 *
 * @author Davide Basile
 */
public class IdleAction extends Action{

    /**
     * Constant symbol denoting an idle action
     */
    public static final String IDLE="-";

    /**
     * Constructor for an idle action
     */
    public IdleAction() {
        super(IDLE);
    }
}
