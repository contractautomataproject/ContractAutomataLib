package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.label.action.OfferAction;
import io.github.contractautomata.catlib.automaton.label.action.RequestAction;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.operations.interfaces.TetraFunction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.Silent.class)
public class CompositionFunctionTest {

    @Mock BasicState<String> bs0;
    @Mock BasicState<String> bs1;
    @Mock BasicState<String> bs2;
    @Mock State<String> cs11;
    @Mock State<String> cs12;
    @Mock State<String> cs13;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t11;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t12;
    @Mock
    Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> a1;

    @Mock State<String> cs21;
    @Mock State<String> cs22;
    @Mock State<String> cs23;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t21;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t22;
    @Mock Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> a2;
    @Mock Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> a3;

    @Mock IdleAction ia;
    @Mock OfferAction offact1;
    @Mock RequestAction reqact1;
    @Mock OfferAction offact2;
    @Mock RequestAction reqact2;

    @Mock State<String> csc1;
    @Mock State<String> csc2;
    @Mock State<String> csc21;
    @Mock State<String> csc22;
    @Mock State<String> csc3;
    @Mock CALabel lab1_2;
    @Mock CALabel lab2_21;
    @Mock CALabel lab2_22;
    @Mock CALabel lab22_3;
    @Mock CALabel lab21_3;
    @Mock ModalTransition<String,Action,State<String>,CALabel> tc1_2;
    @Mock ModalTransition<String,Action,State<String>,CALabel> tc2_21;
    @Mock ModalTransition<String,Action,State<String>,CALabel> tc2_22;
    @Mock ModalTransition<String,Action,State<String>,CALabel> tc21_3;
    @Mock ModalTransition<String,Action,State<String>,CALabel> tc22_3;
    @Mock Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> comp;

    Function<List<BasicState<String>>,State<String>> createState;
    BiPredicate<CALabel, CALabel> match;
    TetraFunction<State<String>,CALabel,State<String>,ModalTransition.Modality,ModalTransition<String,Action,State<String>,CALabel>> createTransition;
    Function<List<Action>,CALabel> createLabel;

    @Mock Function<Set<ModalTransition<String,Action,State<String>,CALabel>>,
            Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>>> createAutomaton;

    CompositionFunction<String, State<String>, CALabel, ModalTransition<String,Action,State<String>, CALabel>,
            Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>>> cf;
   
    @Before
    public void setUp(){

        when(cs11.isInitial()).thenReturn(true);
        when(cs11.getState()).thenReturn(asList(bs0,bs0));

        when(cs12.isInitial()).thenReturn(false);
        when(cs12.getState()).thenReturn(asList(bs1,bs0));

        when(cs13.isInitial()).thenReturn(false);
        when(cs13.getState()).thenReturn(asList(bs1,bs2));

        when(cs21.isInitial()).thenReturn(true);
        when(cs21.getState()).thenReturn(Collections.singletonList(bs0));

        when(cs22.isInitial()).thenReturn(false);
        when(cs22.getState()).thenReturn(Collections.singletonList(bs1));

        when(cs23.isInitial()).thenReturn(false);
        when(cs23.getState()).thenReturn(Collections.singletonList(bs2));

        when(csc1.getState()).thenReturn(asList(bs0,bs0,bs0,bs0));

        when(csc2.isFinalState()).thenReturn(false);
        when(csc2.getState()).thenReturn(asList(bs1,bs0,bs1,bs0));

        when(csc21.isFinalState()).thenReturn(false);
        when(csc21.getState()).thenReturn(asList(bs1,bs2,bs1,bs0));

        when(csc22.isFinalState()).thenReturn(false);
        when(csc22.getState()).thenReturn(asList(bs1,bs0,bs2,bs0));

        when(csc3.isFinalState()).thenReturn(true);
        when(csc3.getState()).thenReturn(asList(bs1,bs2,bs2,bs0));

        when(ia.toString()).thenReturn("-");
        when(offact1.toString()).thenReturn("!a1");
        when(reqact1.toString()).thenReturn("?a1");
        when(offact2.toString()).thenReturn("!a2");
        when(reqact2.toString()).thenReturn("?a2");

        CALabel lab11 = mock(CALabel.class);
        when(t11.getLabel()).thenReturn(lab11);
        when(t11.isNecessary()).thenReturn(true);
        when(lab11.getContent()).thenReturn(List.of(reqact1,ia));
        when(t11.getTarget()).thenReturn(cs12);
        when(t11.getModality()).thenReturn(ModalTransition.Modality.URGENT);

        CALabel lab21 = mock(CALabel.class);
        when(t21.getLabel()).thenReturn(lab21);
        when(lab21.getContent()).thenReturn(List.of(offact1));
        when(t21.getTarget()).thenReturn(cs22);
        when(t21.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);

        when(lab1_2.getContent()).thenReturn(List.of(reqact1,ia,offact1,ia));
        when(tc1_2.getLabel()).thenReturn(lab1_2);
        when(tc1_2.getSource()).thenReturn(csc1);
        when(tc1_2.getTarget()).thenReturn(csc2);
        when(tc1_2.isNecessary()).thenReturn(true);
        when(tc1_2.isUrgent()).thenReturn(true);

        CALabel lab12 = mock(CALabel.class);
        when(lab12.getContent()).thenReturn(List.of(offact2,reqact2));
        when(t12.getLabel()).thenReturn(lab12);
        when(t12.getTarget()).thenReturn(cs13);
        when(t12.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);

        CALabel lab22 = mock(CALabel.class);
        when(t22.getLabel()).thenReturn(lab22);
        when(lab22.getContent()).thenReturn(List.of(reqact2));
        when(t22.getTarget()).thenReturn(cs23);
        when(t22.getModality()).thenReturn(ModalTransition.Modality.LAZY);

        when(tc2_21.getLabel()).thenReturn(lab2_21);
        when(lab2_21.getContent()).thenReturn(List.of(offact2,reqact2,ia,ia));
        when(tc2_21.getSource()).thenReturn(csc2);
        when(tc2_21.getTarget()).thenReturn(csc21);

        when(lab2_22.isRequest()).thenReturn(true);
        when(tc2_22.getLabel()).thenReturn(lab2_22);
        when(lab2_22.getContent()).thenReturn(List.of(ia,ia,reqact2,ia));
        when(tc2_22.getSource()).thenReturn(csc2);
        when(tc2_22.getTarget()).thenReturn(csc22);
        when(tc2_22.isNecessary()).thenReturn(true);
        when(tc2_22.isLazy()).thenReturn(true);

        when(tc22_3.getLabel()).thenReturn(lab22_3);
        when(lab22_3.getContent()).thenReturn(List.of(offact2,reqact2,ia,ia));
        when(tc22_3.getSource()).thenReturn(csc22);
        when(tc22_3.getTarget()).thenReturn(csc3);

        when(lab21_3.isRequest()).thenReturn(true);
        when(lab21_3.getContent()).thenReturn(List.of(ia,ia,reqact2,ia));
        when(tc21_3.getLabel()).thenReturn(lab21_3);
        when(tc21_3.getSource()).thenReturn(csc21);
        when(tc21_3.getTarget()).thenReturn(csc3);
        when(tc21_3.isNecessary()).thenReturn(true);
        when(tc2_22.isLazy()).thenReturn(true);

        when(a1.getStates()).thenReturn(Set.of(cs11, cs12, cs13));
        when(a1.getForwardStar(cs11)).thenReturn(Set.of(t11));
        when(a1.getForwardStar(cs12)).thenReturn(Set.of(t12));
        when(a1.getForwardStar(cs13)).thenReturn(Collections.emptySet());
        when(a1.getRank()).thenReturn(2);

        when(a2.getStates()).thenReturn(Set.of(cs21, cs22, cs23));
        when(a2.getForwardStar(cs21)).thenReturn(Set.of(t21));
        when(a2.getForwardStar(cs22)).thenReturn(Set.of(t22));
        when(a2.getForwardStar(cs23)).thenReturn(Collections.emptySet());
        when(a2.getRank()).thenReturn(1);

        when(a3.getStates()).thenReturn(Collections.singleton(cs21));
        when(a3.getRank()).thenReturn(1);
        when(a3.getForwardStar(cs21)).thenReturn(Collections.emptySet());

        match = (l1,l2)->{
            if (l1.equals(l2))
                throw new IllegalArgumentException(); //for mutation testing
            return ((l1.equals(lab11))&&(l2.equals(lab21)));};
        createState = l -> {
            if (l.equals(csc1.getState()))
                return csc1;
            else if (l.equals(csc2.getState()))
                return csc2;
            else if (l.equals(csc21.getState()))
                return csc21;
            else if (l.equals(csc22.getState()))
                return csc22;
            else if (l.equals(csc3.getState()))
                return csc3;
            System.out.println(l);
            throw new UnsupportedOperationException();
        };
        createTransition =
                (s1,lab,s2,m)-> {
                    if (s1.equals(tc1_2.getSource()) && lab.getContent().toString().equals(tc1_2.getLabel().getContent().toString()) && s2.equals(tc1_2.getTarget()) && m.equals(ModalTransition.Modality.URGENT))
                        return tc1_2;
                    if (s1==tc2_21.getSource() && lab.getContent().toString().equals(tc2_21.getLabel().getContent().toString()) && s2==tc2_21.getTarget() && m.equals(ModalTransition.Modality.PERMITTED))
                        return tc2_21;
                    if (s1.equals(tc2_22.getSource()) && lab.getContent().toString().equals(tc2_22.getLabel().getContent().toString()) && s2.equals(tc2_22.getTarget()) && m.equals(ModalTransition.Modality.LAZY))
                        return tc2_22;
                    if (s1==tc22_3.getSource() && lab.getContent().toString().equals(tc22_3.getLabel().getContent().toString()) && s2==tc22_3.getTarget() && m.equals(ModalTransition.Modality.PERMITTED))
                        return tc22_3;
                    if (s1==tc21_3.getSource() && lab.getContent().toString().equals(tc21_3.getLabel().getContent().toString()) && s2==tc21_3.getTarget() && m.equals(ModalTransition.Modality.LAZY))
                        return tc21_3;
                    System.out.println(s1 + " " + lab.getContent() + " " +s2 + " " + tc1_2.getLabel().getContent().toString());
                    throw new UnsupportedOperationException();
                };
        createLabel = l -> {
            if (l.toString().equals(tc1_2.getLabel().getContent().toString())) return lab1_2;
            if (l.toString().equals(tc2_21.getLabel().getContent().toString())) return lab2_21;
            if (l.toString().equals(tc2_22.getLabel().getContent().toString())) return lab2_22;
            if (l.toString().equals(tc21_3.getLabel().getContent().toString())) return lab21_3;
            if (l.toString().equals(tc22_3.getLabel().getContent().toString())) return lab22_3;
            System.out.println(l + " " + tc1_2.getLabel().getContent() + " throwing now");
            throw new UnsupportedOperationException();
        };
    }

    @Test
    public void testApply() {
        when(createAutomaton.apply(any())).thenReturn(comp);
        cf = new CompositionFunction<>(List.of(a1,a2,a3),match,createState,createTransition,createLabel,createAutomaton,t->t.getLabel().isRequest());
        assertNotNull(cf.apply(Integer.MAX_VALUE)); //for mutation testing
        verify(createAutomaton).apply(Set.of(tc1_2,tc2_21,tc2_22,tc21_3));
    }


    @Test
    public void testApplyTwoAutomata() {
        when(csc1.getState()).thenReturn(asList(bs0,bs0,bs0));
        when(csc2.getState()).thenReturn(asList(bs1,bs0,bs1));
        when(csc21.getState()).thenReturn(asList(bs1,bs2,bs1));
        when(csc22.getState()).thenReturn(asList(bs1,bs0,bs2));
        when(csc3.getState()).thenReturn(asList(bs1,bs2,bs2));
        when(lab1_2.getContent()).thenReturn(List.of(reqact1,ia,offact1));
        when(lab2_21.getContent()).thenReturn(List.of(offact2,reqact2,ia));
        when(lab2_22.getContent()).thenReturn(List.of(ia,ia,reqact2));
        when(lab21_3.getContent()).thenReturn(List.of(ia,ia,reqact2));

        cf = new CompositionFunction<>(List.of(a1,a2),match,createState,createTransition,createLabel,createAutomaton,t->t.getLabel().isRequest());
        cf.apply(Integer.MAX_VALUE);
        verify(createAutomaton).apply(Set.of(tc1_2,tc2_21,tc2_22,tc21_3));
    }

    @Test
    public void testApplyNecessaryMatchOnSecondPrincipal() {
        when(t11.isNecessary()).thenReturn(false);
        when(t21.getModality()).thenReturn(ModalTransition.Modality.URGENT);
        cf = new CompositionFunction<>(List.of(a1,a2,a3),match,createState,createTransition,createLabel,createAutomaton,t->t.getLabel().isRequest());
        cf.apply(Integer.MAX_VALUE);
        verify(createAutomaton).apply(Set.of(tc1_2,tc2_21,tc2_22,tc21_3));
    }

    @Test
    public void testApplyLazyNotRequest() {
        when(tc2_22.getLabel().isRequest()).thenReturn(false);
        cf = new CompositionFunction<>(List.of(a1,a2,a3),match,createState,createTransition,createLabel,createAutomaton,t->t.getLabel().isRequest());
        cf.apply(Integer.MAX_VALUE);
        verify(createAutomaton).apply(Set.of(tc1_2,tc2_21,tc2_22,tc21_3,tc22_3));
    }

    @Test
    public void testApplyPruningPredReturnNull() {
        when(tc2_22.isNecessary()).thenReturn(false);
        when(tc21_3.isNecessary()).thenReturn(false);
        cf = new CompositionFunction<>(List.of(a1,a2,a3),match,createState,createTransition,createLabel,createAutomaton,t->t.getLabel().isRequest());
        cf.apply(Integer.MAX_VALUE);
        verify(createAutomaton,never()).apply(any());
    }

    @Test
    public void testApplyPruningPred() {
        when(tc2_22.isNecessary()).thenReturn(false);
        when(tc21_3.isNecessary()).thenReturn(false);
        when(csc2.isFinalState()).thenReturn(true);
        cf = new CompositionFunction<>(List.of(a1,a2,a3),match,createState,createTransition,createLabel,createAutomaton,t->t.getLabel().isRequest());
        cf.apply(Integer.MAX_VALUE);
        verify(createAutomaton,times(1)).apply(Set.of(tc1_2,tc2_21));
    }

    @Test
    public void testBadSourceStateInitial() {
        when(tc1_2.getLabel().isRequest()).thenReturn(true);
        cf = new CompositionFunction<>(List.of(a1,a2,a3),match,createState,createTransition,createLabel,createAutomaton,t->t.getLabel().isRequest());
        cf.apply(Integer.MAX_VALUE);
        verify(createAutomaton,never()).apply(any());
    }

    @Test
    public void testBadSourceStateNotInitial() {
        when(tc2_22.getLabel().isRequest()).thenReturn(true);
        when(tc2_22.isUrgent()).thenReturn(true);
        when(csc2.isFinalState()).thenReturn(true);// for mutation testing
        cf = new CompositionFunction<>(List.of(a1,a2,a3),match,createState,createTransition,createLabel,createAutomaton,t->t.getLabel().isRequest());
        cf.apply(Integer.MAX_VALUE);
        verify(createAutomaton).apply(Collections.singleton(tc1_2));
    }

    @Test
    public void testApplyBounded() {
        cf = new CompositionFunction<>(List.of(a1,a2,a3),match,createState,createTransition,createLabel,createAutomaton,null);
        cf.apply(1);
        verify(createAutomaton,times(1)).apply(Set.of(tc1_2));
        assertFalse(cf.isFrontierEmpty());
        cf.apply(2);
        verify(createAutomaton,times(2)).apply(Set.of(tc1_2,tc2_21,tc2_22));
        assertFalse(cf.isFrontierEmpty());
        cf.apply(3);
        verify(createAutomaton,times(3)).apply(Set.of(tc1_2,tc2_21,tc2_22,tc21_3,tc22_3));
        cf.apply(4);
        assertTrue(cf.isFrontierEmpty());
    }

    @Test
    public void testIsFrontierEmpty() {
        cf = new CompositionFunction<>(List.of(a1,a2,a3),match,createState,createTransition,createLabel,createAutomaton,null);
        assertTrue(cf.isFrontierEmpty());
    }

    @Test
    public void testGetPruningPred() {
        cf = new CompositionFunction<>(List.of(a1,a2,a3),match,createState,createTransition,createLabel,createAutomaton,null);
        assertNull(cf.getPruningPred());
    }


    @Test
    public void testGetPruningPredNotNull() {
        cf = new CompositionFunction<>(List.of(a1,a2,a3),match,createState,createTransition,createLabel,createAutomaton,l->true);
        assertNotNull(cf.getPruningPred()); //for mutation testing
    }
}