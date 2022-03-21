package io.github.contractautomataproject.catlib.automaton.label.action;

import io.github.contractautomataproject.catlib.automaton.label.Matchable;

import java.util.Objects;

public class OfferAction extends Action  {

    private static final String OFFER="!";

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

    public static boolean isOffer(String action) {
        return action.startsWith(OFFER) && action.length()>1;
    }

    public static Action parseAction(String act) {
        if (isOffer(act))
          return new OfferAction(act.substring(1));

        throw new IllegalArgumentException();
    }

    @Override
    public int hashCode() {
        return Objects.hash(OFFER+this.getLabel());
    }

}
