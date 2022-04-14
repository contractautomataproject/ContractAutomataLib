package io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.OfferAction;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Strict.class)
public class MpcSynthesisOperatorTest {

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>, CALabel>> aut;

    @Mock CALabel lab;
    @Mock CALabel lab2;

    @Mock
    BasicState<String> bs1;
    @Mock BasicState<String> bs2;
    @Mock BasicState<String> bs3;

    @Mock State<String> cs11;
    @Mock State<String> cs12;
    @Mock State<String> cs13;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t11;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t12;

    MpcSynthesisOperator<String> mso;

    @Before
    public void setUp() throws Exception {
        when(cs11.isInitial()).thenReturn(true);
        when(cs12.isFinalState()).thenReturn(true);
        when(cs13.isFinalState()).thenReturn(true);
        when(cs11.getState()).thenReturn(List.of(bs1));
        when(cs12.getState()).thenReturn(List.of(bs2));
        when(cs13.getState()).thenReturn(List.of(bs3));

        when(t11.getLabel()).thenReturn(lab);
        when(t11.getSource()).thenReturn(cs11);
        when(t11.getTarget()).thenReturn(cs12);

//      when(t11.toString()).thenReturn("([0, 0],[?a1,!a1],[1, 0])");

        when(t12.getLabel()).thenReturn(lab2);
        when(t12.getSource()).thenReturn(cs12);
        when(t12.getTarget()).thenReturn(cs13);

//      when(t12.toString()).thenReturn("([1, 0],[!a2,?a2],[1, 2])");

        when(aut.getTransition()).then(args -> new HashSet<>(Arrays.asList(t11, t12)));
        when(aut.getStates()).thenReturn(Set.of(cs11, cs12, cs13));
        when(aut.getInitial()).thenReturn(cs11);

        mso = new MpcSynthesisOperator<>(l->true);

    }



    @Test
    public void testChangeLabel() {
        mso = new MpcSynthesisOperator<>(l->true,null);
        when(lab.getRank()).thenReturn(1);
        when(lab.getRequester()).thenReturn(0);
        when(lab.getCoAction()).thenReturn(mock(OfferAction.class));
        assertNotNull(mso.getChangeLabel().apply(lab));
    }


    @Test
    public void apply() {
        assertNotNull(mso.apply(aut));
    }


    @Test
    public void applyWithPruning() {
        mso = new MpcSynthesisOperator<>(l->!l.equals(lab2));
        assertNotNull(mso.apply(aut));
    }


    @Test
    public void applyNull() {
        when(t12.isUrgent()).thenReturn(true);
        mso = new MpcSynthesisOperator<>(l->!l.equals(lab2));
        assertNull(mso.apply(aut));
    }


    @Test
    public void applyWithPruningConstructor() {
        mso = new MpcSynthesisOperator<>(l->!l.equals(lab2), null);
        assertNotNull(mso.apply(aut));
    }

    @Test
    public void applyNullConstructor() {
        when(t12.isUrgent()).thenReturn(true);
        mso = new MpcSynthesisOperator<>(l->!l.equals(lab2),null);
        assertNull(mso.apply(aut));
    }

    @Test
    public void applyException() {
        when(t11.isLazy()).thenReturn(true);
        assertThrows(UnsupportedOperationException.class, () -> mso.apply(aut));
    }
}