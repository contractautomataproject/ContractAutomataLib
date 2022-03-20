package io.github.contractautomataproject.catlib.automaton.label.action;

import java.util.Objects;

public class OfferAction extends Action{

    public static final String OFFER="!";

    public OfferAction(String label) {
        super(label);
    }


    @Override
    public String toString(){
        return OFFER+this.getLabel();
    }


    @Override
    public int hashCode() {
        return Objects.hashCode(OFFER+this.getLabel());
    }

    public static boolean isOffer(String action) {
        return action.startsWith(OFFER) && action.length()>1;
    }


}
