package io.github.contractautomata.catlib.unused;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.transition.Transition;
import io.github.contractautomata.catlib.operations.interfaces.TetraFunction;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * this class is used to turn all transitions of an automaton to permitted,
 * and revert them.
 * The idea was to perform model checking on an automaton where also necessary
 * transitions can be pruned. Therefore, first necessary transitions are hidden,
 * model checking is applied, and the transitions are reverted to necessary
 * on the automaton result of the model checking.
 * This has been later changed so that the composition already can ignore modalities.
 *
 * @param <S1>
 * @param <S>
 * @param <L>
 * @param <T>
 * @param <A>
 */
public class HideNecessaryModality<S1,
        S extends State<S1>,
        L extends Label<Action>,
        T extends ModalTransition<S1,Action,S,L>,
        A extends Automaton<S1,Action,S,T>> {

    private final Map<Transition<S1, Action, S, L>, T> tr2modtr;

    private final Map<String, Transition<S1, Action, S, L>> string2tr;

    private final TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition;
    private final Function<Set<T>,A> createAutomaton;


    public HideNecessaryModality(A aut,
                                 TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition,
                                 Function<Set<T>,A> createAutomaton){
        this.createTransition=createTransition;
        this.createAutomaton=createAutomaton;

        tr2modtr = aut.getTransition().parallelStream()
                .collect(Collectors.toMap(t->new Transition<>(t.getSource(),t.getLabel(),t.getTarget()), t->t));

//        Map<T, Transition<S1, Action, S, L>> modtr2tr = tr2modtr.entrySet()
//                .stream()
//                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));

        string2tr = tr2modtr.keySet().stream()
                .collect(Collectors.toMap(Transition::toString, t->t));
    }

    public A getAutAllPermitted(){
        return createAutomaton.apply(tr2modtr.keySet()
                .parallelStream()
                .map(t->createTransition.apply(t.getSource(),t.getLabel(),t.getTarget(), ModalTransition.Modality.PERMITTED))
                .collect(Collectors.toSet()));
    }

    public  A getAutWithNecessary(A aut){
        return  createAutomaton.apply(aut.getTransition().parallelStream()
                .map(t->tr2modtr.get(string2tr.get(new Transition<>(t.getSource(),t.getLabel(),t.getTarget()).toString())))
                .collect(Collectors.toSet()));
    }
}


//    public Automaton<S1, Action, S, Transition<S1, Action, S, L>> getEncoded(){
//        return new Automaton<>(tr2modtr.keySet());
//    }
//
//    public  A decode( Automaton<S1, Action, S, Transition<S1, Action, S, L>>  aut){
//        return createAutomaton.apply(aut.getTransition().parallelStream()
//                .map(t->tr2modtr.get(string2tr.get(t.toString())))
//                .collect(Collectors.toSet()));
//    }