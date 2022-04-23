package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Strict.class)
public class RelabelingOperatorTest {

    Function<List<Action>, CALabel> createLabel;
    UnaryOperator<String> relabel;

    @Mock Predicate<BasicState<String>> initialStatePred;
    @Mock Predicate<BasicState<String>> finalStatePred;


    @Mock CALabel lab;
    @Mock CALabel lab2;

    @Mock BasicState<String> bs1;
    @Mock BasicState<String> bs2;
    @Mock BasicState<String> bs3;

    @Mock State<String> cs11;
    @Mock State<String> cs12;
    @Mock State<String> cs13;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t11;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t12;


    @Mock
    Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>, CALabel>> aut;

    RelabelingOperator<String,CALabel> ro;

    @Before
    public void setUp() throws Exception {
//        when(cs11.isInitial()).thenReturn(true);
//        when(cs12.isFinalState()).thenReturn(true);
//        when(cs13.isFinalState()).thenReturn(true);
        when(cs11.getState()).thenReturn(List.of(bs1));
        when(cs12.getState()).thenReturn(List.of(bs2));
        when(cs13.getState()).thenReturn(List.of(bs3));

//        when(cs11.getRank()).thenReturn(1);
//        when(cs12.getRank()).thenReturn(1);
//        when(cs13.getRank()).thenReturn(1);
        when(lab.getRank()).thenReturn(1);
    //    when(lab2.getRank()).thenReturn(1);

        when(t11.getLabel()).thenReturn(lab);
        when(t11.getSource()).thenReturn(cs11);
        when(t11.getTarget()).thenReturn(cs12);
        when(t11.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);

//      when(t11.toString()).thenReturn("([0, 0],[?a1,!a1],[1, 0])");

        when(t12.getLabel()).thenReturn(lab2);
        when(t12.getSource()).thenReturn(cs12);
        when(t12.getTarget()).thenReturn(cs13);
        when(t12.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);

//      when(t12.toString()).thenReturn("([1, 0],[!a2,?a2],[1, 2])");

        when(aut.getTransition()).then(args -> new HashSet<>(Arrays.asList(t11, t12)));
        when(aut.getStates()).thenReturn(Set.of(cs11, cs12, cs13));
        //       when(aut.getInitial()).thenReturn(cs11);

        createLabel = l -> lab;
        relabel = s -> "relabel";

        ro = new RelabelingOperator<>(createLabel,relabel,initialStatePred,finalStatePred);
    }

    @Test
    public void apply() {
        Set<ModalTransition<String,Action,State<String>,CALabel>> set = ro.apply(aut);
        assertFalse(set.isEmpty());
        assertTrue(set.stream().allMatch(Objects::nonNull));
    }


    @Test
    public void applyException() {
        when(aut.getTransition()).thenReturn(Collections.emptySet());
        assertThrows(IllegalArgumentException.class, () -> ro.apply(aut));
    }
}