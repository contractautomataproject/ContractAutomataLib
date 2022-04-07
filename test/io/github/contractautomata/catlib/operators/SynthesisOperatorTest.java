package io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.Strict.class)
public class SynthesisOperatorTest {

    @Mock State<String> cs11;
    @Mock State<String> cs12;
    @Mock State<String> cs13;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t11;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t12;
    @Mock Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> a1;

    @Mock Predicate<CALabel> req;
    @Mock Function<Set<ModalTransition<String,Action,State<String>,CALabel>>,
            Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>>> createAut;

     SynthesisOperator<String, Action, State<String>, CALabel,
            ModalTransition<String,Action,State<String>,CALabel>,
            Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>>> so;

    @Before
    public void setUp() throws Exception {
//      when(cs11.toString()).thenReturn(List.of("0","0").toString())
//      when(cs12.toString()).thenReturn(List.of("1","0").toString());
//      when(cs13.toString()).thenReturn(List.of("1","2").toString());

        when(cs13.isFinalState()).thenReturn(true);

        CALabel lab11 = mock(CALabel.class);
        when(t11.getLabel()).thenReturn(lab11);
        when(t11.getSource()).thenReturn(cs11);
        when(t11.getTarget()).thenReturn(cs12);
//      when(t11.toString()).thenReturn("([0, 0],[?a1,!a1],[1, 0])");

        CALabel lab12 = mock(CALabel.class);
        when(t12.getLabel()).thenReturn(lab12);
        when(t12.getSource()).thenReturn(cs12);
        when(t12.getTarget()).thenReturn(cs13);
//      when(t12.toString()).thenReturn("([1, 0],[!a2,?a2],[1, 2])");

        when(a1.getTransition()).then(args -> new HashSet<>(Arrays.asList(t11, t12)));
        when(a1.getStates()).thenReturn(Set.of(cs11, cs12, cs13));
        when(a1.getInitial()).thenReturn(cs11);

        so = new SynthesisOperator<>((x,t,bad) -> false, x->!x.isRequest(), createAut);
    }

    @Test
    public void testGetReq() {
        so = new SynthesisOperator<>((x,t,bad) -> false, req, createAut);
        assertEquals(req,so.getReq());
    }

    @Test
    public void testGetCreateAut() {
        assertEquals(createAut,so.getCreateAut());
    }

    @Test
    public void apply() {
        so.apply(a1);
        verify(createAut).apply(Set.of(t11,t12));
    }


    @Test
    public void applyReturnNotNull() {
        when(createAut.apply(any())).thenReturn(a1);
        assertNotNull(so.apply(a1));
    }


    @Test
    public void applyWithPruningPred() {
        so = new SynthesisOperator<>((x,t,bad)->true,
                (x,t,bad) -> false, x->!x.isRequest(), createAut);
        so.apply(a1);
        verify(createAut,never()).apply(any());
    }


    @Test
    public void applyNull() {
        when(t11.getLabel().isRequest()).thenReturn(true);
        so.apply(a1);
        verify(createAut,never()).apply(any());
    }


    @Test
    public void applyCoverPruningReq() {
        when(t12.getLabel().isRequest()).thenReturn(true);
        when(cs12.isFinalState()).thenReturn(true);
        so.apply(a1);
        verify(createAut).apply(Set.of(t11));
    }


    @Test
    public void applyCoverRemoveDanglingTransitionsNotReachable() {
        when(t11.getTarget()).thenReturn(cs13);
        so.apply(a1);
        verify(createAut).apply(Set.of(t11));
    }


    @Test
    public void applyCoverForwardVisitAlreadyVisited() {
        when(t12.getTarget()).thenReturn(cs12);
        when(cs12.isFinalState()).thenReturn(true);
        so.apply(a1);
        verify(createAut).apply(Set.of(t11,t12));
    }

    @Test
    public void applyCoverRemoveTransitionNotSuccessful() {
        when(t12.getSource()).thenReturn(cs11);
        when(cs11.isFinalState()).thenReturn(true);
        so.apply(a1);
        verify(createAut).apply(Set.of(t12));
    }

    @Test
    public void applyForbiddenPredicateCoverMutation() {
        so = new SynthesisOperator<>((x,t,a)->true, x->!x.isRequest(), createAut);
        so.apply(a1);
        verify(createAut).apply(Set.of(t11,t12));
    }


    @Test
    public void applyCoverForbiddenPredicate() {
        so = new SynthesisOperator<>((x,t,a)->true, x->!x.isRequest(), createAut);
        when(t12.getLabel().isRequest()).thenReturn(true);
        when(cs12.isFinalState()).thenReturn(true);
        so.apply(a1);
        verify(createAut, never()).apply(any());
    }


    @Test
    public void applyCoverMutationBackwardVisit() {
        State<String> cs14 = mock(State.class);
        ModalTransition<String,Action,State<String>,CALabel> t13 = mock(ModalTransition.class);
        ModalTransition<String,Action,State<String>,CALabel> t14 = mock(ModalTransition.class);
        when(t13.getSource()).thenReturn(cs11);
        when(t13.getTarget()).thenReturn(cs14);
        when(t14.getSource()).thenReturn(cs14);
        when(t14.getTarget()).thenReturn(cs14);

        when(a1.getTransition()).then(inv->new HashSet<>(Set.of(t11,t12,t13,t14)));
        when(a1.getStates()).thenReturn(Set.of(cs11, cs12, cs13,cs14));

        so = new SynthesisOperator<>((x,t,bad) -> false, x->true, createAut);
        so.apply(a1);

        verify(createAut).apply(Set.of(t11,t12));
    }

    @Test
    public void applyEmptyTransitionsSet() {
        when(t11.getLabel().isRequest()).thenReturn(true);
        when(cs11.isFinalState()).thenReturn(true);
        so.apply(a1);
        verify(createAut,never()).apply(any());
    }

    @Test
    public void applyException() {
        assertThrows(IllegalArgumentException.class,()->so.apply(null));
    }
}