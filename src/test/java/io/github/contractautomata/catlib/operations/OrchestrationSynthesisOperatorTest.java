package io.github.contractautomata.catlib.operations;

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
import org.mockito.*;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.Silent.class)
public class OrchestrationSynthesisOperatorTest {

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    Automaton<String, Action, State<String>,
                            ModalTransition<String, Action, State<String>, CALabel>> aut;

    @Mock CALabel lab;

    @Mock BasicState<String> bs1;
    @Mock BasicState<String> bs2;
    @Mock BasicState<String> bs3;

    @Mock State<String> cs11;
    @Mock State<String> cs12;
    @Mock State<String> cs13;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t11;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t12;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t13;

    @Captor ArgumentCaptor<BiPredicate
            <ModalTransition<String, Action, State<String>, CALabel>,
                    ModalTransition<String, Action, State<String>, CALabel>>> predicateCaptor;

    OrchestrationSynthesisOperator<String> oso;

    @Before
    public void setUp() throws Exception {

        when(cs11.isInitial()).thenReturn(true);
        when(cs13.isFinalState()).thenReturn(true);
        when(cs11.getState()).thenReturn(List.of(bs1));
        when(cs12.getState()).thenReturn(List.of(bs2));
        when(cs13.getState()).thenReturn(List.of(bs3));

        when(t11.getLabel()).thenReturn(lab);
        when(t11.getSource()).thenReturn(cs11);
        when(t11.getTarget()).thenReturn(cs12);
        when(t11.isPermitted()).thenReturn(true);

//      when(t11.toString()).thenReturn("([0, 0],[?a1,!a1],[1, 0])");

        when(t12.getLabel()).thenReturn(lab);
        when(t12.getSource()).thenReturn(cs12);
        when(t12.getTarget()).thenReturn(cs13);

//      when(t12.toString()).thenReturn("([1, 0],[!a2,?a2],[1, 2])");

        when(aut.getTransition()).then(args -> new HashSet<>(Arrays.asList(t11, t12)));
        when(aut.getStates()).thenReturn(Set.of(cs11, cs12, cs13));
        when(aut.getInitial()).thenReturn(cs11);

        oso = new OrchestrationSynthesisOperator<>(l->true);
    }

    @Test
    public void apply(){
        when(t11.isPermitted()).thenReturn(false);
        assertNotNull(oso.apply(aut));
    }


    @Test
    public void applyCoverReturnTrueMutationUncontrollable(){
        CALabel lab2 = mock(CALabel.class);

        when(cs12.isFinalState()).thenReturn(true);
  //      when(t12.isNecessary()).thenReturn(true);
  //      when(t12.isLazy()).thenReturn(true);
        when(t12.getLabel()).thenReturn(lab2);
        oso = new OrchestrationSynthesisOperator<>(l->!l.equals(lab2));

        assertEquals(1, oso.apply(aut).getTransition().size());
    }


    @Test
    public void applyCoverReturnFalseMutationUncontrollable(){
        CALabel lab2 = mock(CALabel.class);
        when(cs12.isFinalState()).thenReturn(true);

   //     when(t12.isNecessary()).thenReturn(true);
        when(t12.isUncontrollable(any(),any(),any())).thenReturn(true);
        when(t12.getLabel()).thenReturn(lab2);
        oso = new OrchestrationSynthesisOperator<>(l->!l.equals(lab2));

        when(t13.getLabel()).thenReturn(lab);
        when(t13.getSource()).thenReturn(cs12);
        when(t13.getTarget()).thenReturn(cs13);
   //     when(t13.toString()).thenReturn("tr13");
        when(t13.isPermitted()).thenReturn(true);

        when(aut.getTransition()).then(args -> new HashSet<>(Arrays.asList(t11, t12, t13)));

        assertNull(oso.apply(aut));
       // assertEquals(Set.of(t11,t13), oso.apply(aut).getTransition());
    }




    @Test
    public void testChangeLabel() {
        oso = new OrchestrationSynthesisOperator<>(l->true,(Automaton)null);
        when(lab.getRank()).thenReturn(1);
        when(lab.getRequester()).thenReturn(0);
        when(lab.getCoAction()).thenReturn(mock(OfferAction.class));
        assertNotNull(oso.getChangeLabel().apply(lab));
    }

    @Test
    public void applyUncontrollablePredicateFalseDifferentRequester() {

        when(t11.isPermitted()).thenReturn(false);
        oso = new OrchestrationSynthesisOperator<>(l->false);
        assertNull(oso.apply(aut));

        verify(t11,times(2)).isUncontrollable(anySet(),anySet(),predicateCaptor.capture());

        BiPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                        ModalTransition<String, Action, State<String>, CALabel>> pred = predicateCaptor.getValue();

        CALabel lab2 = mock(CALabel.class);
        when(lab.getRequester()).thenReturn(1);
        when(lab2.getRequester()).thenReturn(2);
        when(t12.getLabel()).thenReturn(lab2);
        assertFalse(pred.test(t11,t12));
    }

    @Test
    public void applyUncontrollablePredicateFalseDifferentState() {

        when(t11.isPermitted()).thenReturn(false);
        oso = new OrchestrationSynthesisOperator<>(l->false);
        assertNull(oso.apply(aut));

        verify(t11,times(2)).isUncontrollable(anySet(),anySet(),predicateCaptor.capture());

        BiPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>> pred = predicateCaptor.getValue();

        CALabel lab2 = mock(CALabel.class);
        when(lab.getRequester()).thenReturn(0);
        when(lab2.getRequester()).thenReturn(0);
        when(t12.getLabel()).thenReturn(lab2);
        assertFalse(pred.test(t11,t12));
    }


    @Test
    public void applyUncontrollablePredicateFalseNoRequestNoMatch() {

        when(t11.isPermitted()).thenReturn(false);
        oso = new OrchestrationSynthesisOperator<>(l->false);
        assertNull(oso.apply(aut));

        verify(t11,times(2)).isUncontrollable(anySet(),anySet(),predicateCaptor.capture());

        BiPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>> pred = predicateCaptor.getValue();

        CALabel lab2 = mock(CALabel.class);
        when(lab.getRequester()).thenReturn(0);
        when(lab2.getRequester()).thenReturn(0);
        when(t12.getLabel()).thenReturn(lab2);
        when(cs12.getState()).thenReturn(List.of(bs1));
        assertFalse(pred.test(t11,t12));
    }

    @Test
    public void applyUncontrollablePredicateFalseRequestDifferentAction() {

        when(t11.isPermitted()).thenReturn(false);
        oso = new OrchestrationSynthesisOperator<>(l->false);
        assertNull(oso.apply(aut));

        verify(t11,times(2)).isUncontrollable(anySet(),anySet(),predicateCaptor.capture());

        BiPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>> pred = predicateCaptor.getValue();

        CALabel lab2 = mock(CALabel.class);
        when(lab.getRequester()).thenReturn(0);
        when(lab2.getRequester()).thenReturn(0);
        when(t12.getLabel()).thenReturn(lab2);
        when(cs12.getState()).thenReturn(List.of(bs1));
        when(lab2.isRequest()).thenReturn(true);

        Action a = mock(Action.class);
        Action b = mock(Action.class);

        when(lab.getAction()).thenReturn(a);
        when(lab2.getCoAction()).thenReturn(b);

        assertFalse(pred.test(t11,t12));
    }

    @Test
    public void applyUncontrollablePredicateFalseMatchDifferentAction() {

        when(t11.isPermitted()).thenReturn(false);
        oso = new OrchestrationSynthesisOperator<>(l->false);
        assertNull(oso.apply(aut));

        verify(t11,times(2)).isUncontrollable(anySet(),anySet(),predicateCaptor.capture());

        BiPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>> pred = predicateCaptor.getValue();

        CALabel lab2 = mock(CALabel.class);
        when(lab.getRequester()).thenReturn(0);
        when(lab2.getRequester()).thenReturn(0);
        when(t12.getLabel()).thenReturn(lab2);
        when(cs12.getState()).thenReturn(List.of(bs1));
        when(lab2.isMatch()).thenReturn(true);

        Action a = mock(Action.class);
        Action b = mock(Action.class);

        when(lab.getAction()).thenReturn(a);
        when(lab2.getAction()).thenReturn(b);

        assertFalse(pred.test(t11,t12));
    }

    @Test
    public void applyUncontrollablePredicateTrueMatch() {

        when(t11.isPermitted()).thenReturn(false);
        oso = new OrchestrationSynthesisOperator<>(l->false);
        assertNull(oso.apply(aut));

        verify(t11,times(2)).isUncontrollable(anySet(),anySet(),predicateCaptor.capture());

        BiPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>> pred = predicateCaptor.getValue();

        Action a = mock(Action.class);
        when(lab.getRequester()).thenReturn(0);
        when(lab.isMatch()).thenReturn(true);
        when(lab.getAction()).thenReturn(a);
        assertTrue(pred.test(t11,t11));
    }


    @Test
    public void applyUncontrollablePredicateTrueRequest() {

        when(t11.isPermitted()).thenReturn(false);
        oso = new OrchestrationSynthesisOperator<>(l->false);
        assertNull(oso.apply(aut));

        verify(t11,times(2)).isUncontrollable(anySet(),anySet(),predicateCaptor.capture());

        BiPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>> pred = predicateCaptor.getValue();

        Action a = mock(Action.class);
        when(lab.getRequester()).thenReturn(0);
        when(lab.isRequest()).thenReturn(true);
        when(lab.getAction()).thenReturn(a);
        when(lab.getCoAction()).thenReturn(a);
        assertTrue(pred.test(t11,t11));
    }

    @Test
    public void applyException() {
        when(lab.isOffer()).thenReturn(true);
        assertThrows(UnsupportedOperationException.class, () -> oso.apply(aut));
    }


    @Test
    public void applyException2() {
        when(t11.isPermitted()).thenReturn(false);
        when(lab.isOffer()).thenReturn(true);
        assertThrows(UnsupportedOperationException.class, () -> oso.apply(aut));
    }
}