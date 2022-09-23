package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.label.action.OfferAction;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.operations.interfaces.TetraPredicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.Silent.class)
public class OrchestrationSynthesisOperatorTest {

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    Automaton<String, Action, State<String>,
                            ModalTransition<String, Action, State<String>, CALabel>> aut;

    @Mock CALabel lab;
    @Mock CALabel lab2;

    @Mock BasicState<String> bs1;
    @Mock BasicState<String> bs2;
    @Mock BasicState<String> bs3;
    @Mock BasicState<String> bs4;

    @Mock State<String> cs11;
    @Mock State<String> cs12;
    @Mock State<String> cs13;
    @Mock State<String> cs14;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t11;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t12;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t13;

    @Captor ArgumentCaptor<TetraPredicate
            <ModalTransition<String, Action, State<String>, CALabel>,
                        ModalTransition<String, Action, State<String>, CALabel>,
        Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>>> predicateCaptor;

    OrchestrationSynthesisOperator<String> oso;

    @Before
    public void setUp() throws Exception {
        when(cs11.isInitial()).thenReturn(true);
        when(cs13.isFinalState()).thenReturn(true);
        when(cs11.getState()).thenReturn(List.of(bs1));
        when(cs12.getState()).thenReturn(List.of(bs2));
        when(cs13.getState()).thenReturn(List.of(bs3));
        when(cs14.getState()).thenReturn(List.of(bs4));

        when(t11.getLabel()).thenReturn(lab);
        when(t11.getSource()).thenReturn(cs11);
        when(t11.getTarget()).thenReturn(cs12);
        when(t11.isPermitted()).thenReturn(true);

//      when(t11.toString()).thenReturn("([0, 0],[?a1,!a1],[1, 0])");

        when(t12.getLabel()).thenReturn(lab);
        when(t12.getSource()).thenReturn(cs12);
        when(t12.getTarget()).thenReturn(cs13);

//      when(t12.toString()).thenReturn("([1, 0],[!a2,?a2],[1, 2])");

        when(t13.getLabel()).thenReturn(lab2);
        when(t13.getSource()).thenReturn(cs11);
        when(t13.getTarget()).thenReturn(cs14);

        when(aut.getTransition()).then(args -> new HashSet<>(Arrays.asList(t11, t12, t13)));
        when(aut.getStates()).thenReturn(Set.of(cs11, cs12, cs13, cs14));
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

        when(cs12.isFinalState()).thenReturn(true);
  //      when(t12.isNecessary()).thenReturn(true);
  //      when(t12.isLazy()).thenReturn(true);
        when(t12.getLabel()).thenReturn(lab2);
        oso = new OrchestrationSynthesisOperator<>(l->!l.equals(lab2));

        assertEquals(1, oso.apply(aut).getTransition().size());
    }

    @Test
    public void applyCoverReturnFalseMutationUncontrollable(){
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
        oso = new OrchestrationSynthesisOperator<>(l->true,null);
        when(lab.getRank()).thenReturn(1);
        when(lab.getRequester()).thenReturn(0);
        when(lab.getCoAction()).thenReturn(mock(OfferAction.class));
        assertNotNull(oso.getChangeLabel().apply(lab));
    }

    private TetraPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                    ModalTransition<String, Action, State<String>, CALabel>,
                    Set<ModalTransition<String, Action, State<String>, CALabel>>,
                    Set<State<String>>> beforeUncontrollablePredicateTest(){
        when(t11.isPermitted()).thenReturn(false);
        oso = new OrchestrationSynthesisOperator<>(l->false);
        assertNull(oso.apply(aut));
        verify(t11,times(2)).isUncontrollable(anySet(),anySet(),predicateCaptor.capture());
        return predicateCaptor.getValue();
    }

    @Test
    public void applyUncontrollablePredicateFalseDifferentRequester() {
        TetraPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                    ModalTransition<String, Action, State<String>, CALabel>,
                    Set<ModalTransition<String, Action, State<String>, CALabel>>,
                    Set<State<String>>> pred = beforeUncontrollablePredicateTest();

        when(lab.getRequester()).thenReturn(1);
        when(lab2.getRequester()).thenReturn(2);
        when(t12.getLabel()).thenReturn(lab2);
        assertFalse(pred.test(t11,t12, Collections.emptySet(),Collections.emptySet()));
    }

    @Test
    public void applyUncontrollablePredicateFalseDifferentState() {
        TetraPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = beforeUncontrollablePredicateTest();


        CALabel lab2 = mock(CALabel.class);
        when(lab.getRequester()).thenReturn(0);
        when(lab2.getRequester()).thenReturn(0);
        when(t12.getLabel()).thenReturn(lab2);
        assertFalse(pred.test(t11,t12, Collections.emptySet(),Collections.emptySet()));
    }


    @Test
    public void applyUncontrollablePredicateFalseNoRequestNoMatch() {
        TetraPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = beforeUncontrollablePredicateTest();


        when(lab.getRequester()).thenReturn(0);
        when(lab2.getRequester()).thenReturn(0);
        when(t12.getLabel()).thenReturn(lab2);
        when(cs12.getState()).thenReturn(List.of(bs1));
        assertFalse(pred.test(t11,t12, Collections.emptySet(),Collections.emptySet()));
    }

    @Test
    public void applyUncontrollablePredicateFalseRequestDifferentAction() {
        TetraPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = beforeUncontrollablePredicateTest();


        when(lab.getRequester()).thenReturn(0);
        when(lab2.getRequester()).thenReturn(0);
        when(t12.getLabel()).thenReturn(lab2);
        when(cs12.getState()).thenReturn(List.of(bs1));
        when(lab2.isRequest()).thenReturn(true);

        Action a = mock(Action.class);
        Action b = mock(Action.class);

        when(lab.getAction()).thenReturn(a);
        when(lab2.getCoAction()).thenReturn(b);

        assertFalse(pred.test(t11,t12, Collections.emptySet(),Collections.emptySet()));
    }

    @Test
    public void applyUncontrollablePredicateFalseMatchDifferentAction() {
        TetraPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = beforeUncontrollablePredicateTest();


        when(lab.getRequester()).thenReturn(0);
        when(lab2.getRequester()).thenReturn(0);
        when(t12.getLabel()).thenReturn(lab2);
        when(cs12.getState()).thenReturn(List.of(bs1));
        when(lab2.isMatch()).thenReturn(true);

        Action a = mock(Action.class);
        Action b = mock(Action.class);

        when(lab.getAction()).thenReturn(a);
        when(lab2.getAction()).thenReturn(b);

        assertFalse(pred.test(t11,t12, Collections.emptySet(),Collections.emptySet()));
    }

    @Test
    public void applyUncontrollablePredicateTrueMatch() {
        TetraPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = beforeUncontrollablePredicateTest();


        Action a = mock(Action.class);
        when(lab.getRequester()).thenReturn(0);
        when(lab.isMatch()).thenReturn(true);
        when(lab.getAction()).thenReturn(a);
        assertTrue(pred.test(t11,t11, Collections.emptySet(),Collections.emptySet()));
    }


    @Test
    public void applyUncontrollablePredicateTrueRequest() {
        TetraPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = beforeUncontrollablePredicateTest();


        Action a = mock(Action.class);
        when(lab.getRequester()).thenReturn(0);
        when(lab.isRequest()).thenReturn(true);
        when(lab.getAction()).thenReturn(a);
        when(lab.getCoAction()).thenReturn(a);
        assertTrue(pred.test(t11,t11, Collections.emptySet(),Collections.emptySet()));
    }


    @Test
    public void testReachableWithoutMovingFromEqualsToTrueSameTransition() {
        OrchestrationSynthesisOperator.setReachabilityLazy();

        TetraPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = beforeUncontrollablePredicateTest();


        Action a = mock(Action.class);
        when(lab.getRequester()).thenReturn(0);
        when(lab.isRequest()).thenReturn(true);
        when(lab.getAction()).thenReturn(a);
        when(lab.getCoAction()).thenReturn(a);
        assertTrue(pred.test(t11,t11, Collections.emptySet(),Collections.emptySet()));
    }

    @Test
    public void testReachableWithoutMovingFromEqualsTrue() {
        OrchestrationSynthesisOperator.setReachabilityLazy();

        TetraPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = beforeUncontrollablePredicateTest();

        Action a = mock(Action.class);
        CALabel lab3 = mock(CALabel.class);

        when(t13.getLabel()).thenReturn(lab2);

        when(cs12.getState()).thenReturn(List.of(bs1));
        when(t11.getLabel()).thenReturn(lab3);

        when(lab2.getRequester()).thenReturn(0);
        when(lab.getRequester()).thenReturn(0);

        when(lab2.isRequest()).thenReturn(true);
        when(lab.getAction()).thenReturn(a);
        when(lab2.getCoAction()).thenReturn(a);

        when(lab2.getContent()).thenReturn(List.of(a));
        when(lab.getContent()).thenReturn(List.of(a));
        when(lab3.getContent()).thenReturn(List.of(new IdleAction()));

        assertTrue(pred.test(t12,t13, aut.getTransition(),Collections.emptySet()));
    }

    @Test
    public void testReachableWithoutMovingFromEqualsFalseNotIdle() {
        OrchestrationSynthesisOperator.setReachabilityLazy();

        TetraPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = beforeUncontrollablePredicateTest();

        Action a = mock(Action.class);
        when(t13.getLabel()).thenReturn(lab2);

        when(cs12.getState()).thenReturn(List.of(bs1));

        when(lab2.getRequester()).thenReturn(0);
        when(lab.getRequester()).thenReturn(0);

        when(lab2.isRequest()).thenReturn(true);
        when(lab.getAction()).thenReturn(a);
        when(lab2.getCoAction()).thenReturn(a);

        when(lab2.getContent()).thenReturn(List.of(a));
        when(lab.getContent()).thenReturn(List.of(a));

        assertFalse(pred.test(t12,t13, aut.getTransition(),Collections.emptySet()));
    }


    @Test
    public void testReachableWithoutMovingFromEqualsFalseAlreadyVisited() {
        OrchestrationSynthesisOperator.setReachabilityLazy();

        TetraPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = beforeUncontrollablePredicateTest();

        Action a = mock(Action.class);
        CALabel lab3 = mock(CALabel.class);

        when(t11.getTarget()).thenReturn(cs11);
        when(t13.getLabel()).thenReturn(lab2);

        when(cs12.getState()).thenReturn(List.of(bs1));
        when(t11.getLabel()).thenReturn(lab3);

        when(lab2.getRequester()).thenReturn(0);
        when(lab.getRequester()).thenReturn(0);

        when(lab2.isRequest()).thenReturn(true);
        when(lab.getAction()).thenReturn(a);
        when(lab2.getCoAction()).thenReturn(a);

        when(lab2.getContent()).thenReturn(List.of(a));
        when(lab.getContent()).thenReturn(List.of(a));
        when(lab3.getContent()).thenReturn(List.of(new IdleAction()));

        assertFalse(pred.test(t12,t13, aut.getTransition(),Collections.emptySet()));
    }


    @Test
    public void testReachableWithoutMovingFromEqualsFalseBadState() {
        OrchestrationSynthesisOperator.setReachabilityLazy();

        TetraPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = beforeUncontrollablePredicateTest();

        Action a = mock(Action.class);
        CALabel lab3 = mock(CALabel.class);

        when(t13.getLabel()).thenReturn(lab2);

        when(cs12.getState()).thenReturn(List.of(bs1));
        when(t11.getLabel()).thenReturn(lab3);

        when(lab2.getRequester()).thenReturn(0);
        when(lab.getRequester()).thenReturn(0);

        when(lab2.isRequest()).thenReturn(true);
        when(lab.getAction()).thenReturn(a);
        when(lab2.getCoAction()).thenReturn(a);

        when(lab2.getContent()).thenReturn(List.of(a));
        when(lab.getContent()).thenReturn(List.of(a));
        when(lab3.getContent()).thenReturn(List.of(new IdleAction()));

        assertFalse(pred.test(t12,t13, aut.getTransition(),Collections.singleton(cs12)));
    }

    @Test
    public void testSetReachabilityLazy(){
        OrchestrationSynthesisOperator.setReachabilityLazy();
        assertTrue(OrchestrationSynthesisOperator.getReachabilityLazy());
    }

    @Test
    public void testResetReachabilityLazy(){
        OrchestrationSynthesisOperator.resetReachabilityLazy();
        assertFalse(OrchestrationSynthesisOperator.getReachabilityLazy());
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