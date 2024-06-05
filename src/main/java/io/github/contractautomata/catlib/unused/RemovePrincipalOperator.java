package io.github.contractautomata.catlib.unused;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.operations.interfaces.TetraFunction;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RemovePrincipalOperator<S1,
        S extends State<S1>,
        L extends Label<Action>,
        T extends ModalTransition<S1,Action,S,L>,
        A extends Automaton<S1,Action,S,T>> implements BiFunction<A,Integer,A> {

    private final TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition;
    private final Function<Set<T>,A> createAutomaton;
    private final Function<List<BasicState<S1>>,S> createState;
    private final Function<List<Action>,L> createLabel;

    public RemovePrincipalOperator( Function<List<BasicState<S1>>,S> createState,
                                    TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition,
                                    Function<Set<T>,A> createAutomaton,
                                    Function<List<Action>,L> createLabel){
        this.createState=createState;
        this.createTransition=createTransition;
        this.createAutomaton=createAutomaton;
        this.createLabel=createLabel;

    }

    @Override
    public A apply(A aut, Integer i){
        if (i>=aut.getRank())
            throw new IllegalArgumentException("The index of the principal is greater than the rank of the automaton");

        Function<List<BasicState<S1>>, List<BasicState<S1>>> remove_i = l -> {
            l.remove((int) i);
            return l;
        };
        Map<List<BasicState<S1>>, S> mapState = aut.getStates()
                .parallelStream()
                .map(s->remove_i.apply(s.getState()))
                .distinct()
                .collect(Collectors.toMap(s->s, createState));

        Set<T> setTr = aut.getTransition().parallelStream()
                .filter(t-> IntStream.range(0,t.getRank())
                        .filter(j->j!=i)
                        .anyMatch(j->!(t.getLabel().getContent().get(j) instanceof IdleAction)))
                //filter out transitions where all principals, apart from i, are idle
                .map(t->{
                    List<Action> label = t.getLabel().getContent();
                    label.remove((int) i);
                    return createTransition.apply(mapState.get(remove_i.apply(t.getSource().getState())),createLabel.apply(label),
                            mapState.get(remove_i.apply(t.getTarget().getState())),t.getModality());
                }).collect(Collectors.toSet());

        return createAutomaton.apply(setTr);
    }
}
