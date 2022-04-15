package io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.OfferAction;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.transition.Transition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.Strict.class)
public class UnionFunctionTest {

    @Mock
    Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>, CALabel>> aut;

    @Mock
    Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>, CALabel>> aut2;

    @Mock BasicState<String> bs;
    @Mock BasicState<String> bs1;
    @Mock BasicState<String> bs2;
    @Mock State<String> cs1;
    @Mock State<String> cs12;
    @Mock State<String> cs2;
    @Mock CALabel lab;
    @Mock OfferAction act;

    @Mock ModalTransition<String,Action,State<String>,CALabel> t1;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t2;

    UnionFunction uf;

    @Before
    public void setUp() throws Exception {
        when(aut.getRank()).thenReturn(1);
        when(aut2.getRank()).thenReturn(1);

        when(bs.getState()).thenReturn("bs");
        when(bs1.getState()).thenReturn("bs1");
        when(bs2.getState()).thenReturn("bs2");
        when(bs2.isFinalState()).thenReturn(true);
        when(bs1.isFinalState()).thenReturn(true);
        when(cs1.getState()).thenReturn(List.of(bs));
        when(cs12.getState()).thenReturn(List.of(bs1));
        when(cs2.getState()).thenReturn(List.of(bs2));
//        when(cs1.getRank()).thenReturn(1);
//        when(cs2.getRank()).thenReturn(1);
//        when(cs1.isFinalState()).thenReturn(true);
//        when(cs2.isFinalState()).thenReturn(true);

        when(lab.getLabel()).thenReturn(List.of(act));

        when(t1.getSource()).thenReturn(cs12);
        when(t1.getLabel()).thenReturn(lab);
        when(t1.getTarget()).thenReturn(cs1);
        when(t1.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);
        when(t2.getSource()).thenReturn(cs2);
        when(t2.getLabel()).thenReturn(lab);
        when(t2.getTarget()).thenReturn(cs2);
        when(t2.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);

        when(aut.getStates()).thenReturn(Set.of(cs1,cs12));
        when(aut2.getStates()).thenReturn(Collections.singleton(cs2));

        when(aut.getTransition()).thenReturn(Collections.singleton(t1));
        when(aut2.getTransition()).thenReturn(Collections.singleton(t2));

        when(aut.getInitial()).thenReturn(cs1);
        when(aut2.getInitial()).thenReturn(cs2);

        uf = new UnionFunction();
    }

    @Test
    public void apply() {
        Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>, CALabel>> union = uf.apply(List.of(aut,aut2));
        assertTrue(union.getTransition()
                .stream().filter(t->t.getSource().isInitial())
                .map(Transition::getTarget)
                .map(State::getState)
                .map(l->l.get(0).getState().split("_")[1])
                .allMatch(s->s.equals(aut.getInitial().getState().get(0).getState()) ||
                        s.equals(aut2.getInitial().getState().get(0).getState())));
        //the new initial state is correctly connected to the initial states of the operands
    }

    @Test
    public void testNull() {
        assertThrows(IllegalArgumentException.class,()->uf.apply(null));
    }

    @Test
    public void testEmpty() {
        assertThrows(IllegalArgumentException.class, ()->uf.apply(Collections.emptyList()));
    }

    @Test
    public void testNullElement() {
        assertThrows(IllegalArgumentException.class, ()->uf.apply(new ArrayList<>(Collections.singleton(null))));
    }

    @Test
    public void testDifferentRank() {
        when(aut2.getRank()).thenReturn(2);
        assertThrows(IllegalArgumentException.class, ()->uf.apply(List.of(aut,aut2)));
    }

    @Test
    public void testIllegalLabel() {
        when(bs.getState()).thenReturn("bs_0");
        assertThrows(IllegalArgumentException.class, ()->uf.apply(List.of(aut,aut2)));
    }

    @Test
    public void testListSizeForMutation() {
        if (System.getProperty("java.version").startsWith("17"))
            return;

        List<Automaton<String, Action, State<String>,
                ModalTransition<String, Action, State<String>, CALabel>>> list =
                Mockito.spy(new ArrayList<>(List.of(aut,aut2)));
        when(list.size()).thenReturn(3);
        assertThrows(IndexOutOfBoundsException.class, ()->uf.apply(list));

        when(list.size()).thenReturn(-1);
        try{
            uf.apply(list);
        } catch(IllegalArgumentException e) {
            assertEquals("No transitions", e.getMessage());
            return;
        }
        Assert.fail();
    }

}