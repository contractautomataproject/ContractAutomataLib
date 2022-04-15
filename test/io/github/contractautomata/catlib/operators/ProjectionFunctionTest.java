package io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.*;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.ToIntFunction;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.Silent.class)
public class ProjectionFunctionTest {

    @Mock Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> aut;
    @Mock ToIntFunction<ModalTransition<String,Action,State<String>,CALabel>> getNecessaryPrincipal;

    @Mock OfferAction act;
    @Mock AddressedOfferAction addressedOfferAction;
    @Mock RequestAction ract;
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

    ProjectionFunction<String> pf;

    @Before
    public void setUp() throws Exception {

        when(bs1.isInitial()).thenReturn(true);
        when(bs2.isFinalState()).thenReturn(true);
        when(bs3.isFinalState()).thenReturn(true);

//        when(cs11.isInitial()).thenReturn(true);
//        when(cs12.isFinalState()).thenReturn(true);
//        when(cs13.isFinalState()).thenReturn(true);
        when(cs11.getState()).thenReturn(List.of(bs1,bs1));
        when(cs12.getState()).thenReturn(List.of(bs2,bs2));
        when(cs13.getState()).thenReturn(List.of(bs3,bs3));

//        when(cs11.getRank()).thenReturn(2);
//        when(cs12.getRank()).thenReturn(2);
//        when(cs13.getRank()).thenReturn(2);
//        when(lab.getRank()).thenReturn(2);
//        when(lab2.getRank()).thenReturn(2);
        when(lab.getAction()).thenReturn(act);
        when(lab2.getAction()).thenReturn(act);
//        when(lab.getCoAction()).thenReturn(ract);
        when(lab2.getCoAction()).thenReturn(ract);
        when(lab2.isMatch()).thenReturn(true);
        when(lab2.getOfferer()).thenReturn(0);
        when(lab2.getRequester()).thenReturn(1);

        when(t11.getLabel()).thenReturn(lab);
        when(t11.getSource()).thenReturn(cs11);
        when(t11.getTarget()).thenReturn(cs12);
//        when(t11.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);

//      when(t11.toString()).thenReturn("([0, 0],[?a1,!a1],[1, 0])");

        when(t12.getLabel()).thenReturn(lab2);
        when(t12.getSource()).thenReturn(cs12);
        when(t12.getTarget()).thenReturn(cs13);
//        when(t12.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);

//      when(t12.toString()).thenReturn("([1, 0],[!a2,?a2],[1, 2])");

        when(aut.getRank()).thenReturn(2);
        when(aut.getTransition()).then(args -> new HashSet<>(Arrays.asList(t11, t12)));
        //        when(aut.getStates()).thenReturn(Set.of(cs11, cs12, cs13));
//        when(aut.getInitial()).thenReturn(cs11);

        pf = new ProjectionFunction<>();
    }

    @Test
    public void applyFilteringTransitions() {
        when(t11.isLazy()).thenReturn(true);
        when(t12.isLazy()).thenReturn(true);
        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> proj =
                pf.apply(aut,0,getNecessaryPrincipal);

        assertEquals(2, proj.getTransition().size());
        assertTrue(proj.getTransition().stream()
                .map(ModalTransition::getModality).allMatch(m->m.equals(ModalTransition.Modality.LAZY)));
    }

    @Test
    public void applyFilteringTransitions2() {
        when(lab2.getOfferer()).thenReturn(1);
        when(lab2.getRequester()).thenReturn(0);
        when(t11.isPermitted()).thenReturn(true);
        when(getNecessaryPrincipal.applyAsInt(t12)).thenReturn(1);

        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> proj =
                pf.apply(aut,0,getNecessaryPrincipal);

        assertEquals(2, proj.getTransition().size());
        assertTrue(proj.getTransition().stream()
                .map(ModalTransition::getModality).allMatch(m->m.equals(ModalTransition.Modality.PERMITTED)));

    }

    @Test
    public void applyFilteringTransitions3() {
        when(lab.getOffererOrRequester()).thenReturn(2);
        when(bs2.isInitial()).thenReturn(true);
        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> proj =
                pf.apply(aut,0,getNecessaryPrincipal);

        assertEquals(1,proj.getTransition().size());
        assertTrue(proj.getTransition().stream().allMatch(t->t.getLabel().getAction().equals(act)
        && t.getModality().equals(ModalTransition.Modality.URGENT)));

    }


    @Test
    public void applyFilteringTransitions4() {
        when(lab.isRequest()).thenReturn(true);

        when(lab2.getOfferer()).thenReturn(2);
        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> proj =
                pf.apply(aut,0,getNecessaryPrincipal);

        assertEquals(1, proj.getTransition().size());

        assertTrue(proj.getTransition().stream().allMatch(t->t.getLabel().getAction().equals(act)));
    }

    @Test
    public void applyCreateAddressUnsupportedMatch() {
        when(lab2.isMatch()).thenReturn(false);
        pf = new ProjectionFunction<>(true);
        assertThrows(UnsupportedOperationException.class, ()->
                pf.apply(aut,0,getNecessaryPrincipal));
    }

    @Test
    public void applyCreateAddressUnsupportedAction() {
        when(lab.isMatch()).thenReturn(true);
        when(lab2.isMatch()).thenReturn(true);
        when(lab.getAction()).thenReturn(addressedOfferAction);
        when(lab2.getAction()).thenReturn(addressedOfferAction);

        pf = new ProjectionFunction<>(true);
        assertThrows(UnsupportedOperationException.class, ()->
             pf.apply(aut,0,getNecessaryPrincipal));
    }

    @Test
    public void applyCreateAddressOffer() {
        when(lab.isMatch()).thenReturn(true);
        when(lab2.isMatch()).thenReturn(true);
        pf = new ProjectionFunction<>(true);

        when(act.getLabel()).thenReturn("act");
        when(lab.getRequester()).thenReturn(0);
        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> proj =
                pf.apply(aut,0,getNecessaryPrincipal);
        assertNotNull(proj);
        assertTrue(proj.getTransition().stream().map(t->t.getLabel().getAction())
                .allMatch(a-> a instanceof  AddressedOfferAction));
    }

    @Test
    public void applyCreateAddressRequest() {
        when(lab.isMatch()).thenReturn(true);
        when(lab2.isMatch()).thenReturn(true);
        pf = new ProjectionFunction<>(true);

        when(act.getLabel()).thenReturn("act");
        when(lab.getOfferer()).thenReturn(1);
        when(lab.getRequester()).thenReturn(0);
        when(lab2.getOfferer()).thenReturn(1);
        when(lab2.getRequester()).thenReturn(0);
        Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> proj =
                pf.apply(aut,0,getNecessaryPrincipal);
        assertNotNull(proj);
        assertTrue(proj.getTransition().stream().map(t->t.getLabel().getAction())
                .allMatch(a-> a instanceof  AddressedRequestAction));
    }
    @Test
    public void applyException() {
        assertThrows(IllegalArgumentException.class, ()->pf.apply(aut,  2,getNecessaryPrincipal));
    }

    @Test
    public void applyException2() {
        assertThrows(IllegalArgumentException.class, ()->pf.apply(aut,-1,getNecessaryPrincipal));
    }
}