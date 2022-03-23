package io.github.contractautomataproject.catlib.automaton.label.action;

import java.util.Objects;

public class OfferAction extends Action  {

    public static final String OFFER="!";

    public OfferAction(String label) {
        super(label);
    }

    @Override
    public String toString(){
       return  OFFER+this.getLabel();
    }


    @Override
    public boolean match(Action arg) {
        return (arg instanceof RequestAction) && super.match(arg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(OFFER+this.getLabel());
    }

}
