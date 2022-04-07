package io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.label.action.OfferAction;
import io.github.contractautomata.catlib.automaton.label.action.RequestAction;
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

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.Strict.class)
public class SynthesisOperatorTest {
//    @Mock TriPredicate<ModalTransition<String, Action, State<String>,CALabel>,
//            Set<ModalTransition<String,Action,State<String>,CALabel>>,
//            Set<State<String>>> pruningPredicate;
//    @Mock TriPredicate<ModalTransition<String, Action, State<String>,CALabel>,
//            Set<ModalTransition<String,Action,State<String>,CALabel>>,
//            Set<State<String>>> forbiddenPredicate;
    @Mock Predicate<CALabel> req;
    @Mock Function<Set<ModalTransition<String,Action,State<String>,CALabel>>,
            Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>>> createAut;

     SynthesisOperator<String, Action, State<String>, CALabel,
            ModalTransition<String,Action,State<String>,CALabel>,
            Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>>> so;


     //////////////////////////////////////

    @Mock BasicState<String> bs0;
    @Mock BasicState<String> bs1;
    @Mock BasicState<String> bs2;
    @Mock State<String> cs11;
    @Mock State<String> cs12;
    @Mock State<String> cs13;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t11;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t12;
    @Mock Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> a1;


    @Mock
    IdleAction ia;
    @Mock
    OfferAction offact1;
    @Mock
    RequestAction reqact1;
    @Mock OfferAction offact2;
    @Mock RequestAction reqact2;


    @Before
    public void setUp() throws Exception {
//        when(cs11.isInitial()).thenReturn(true);
        when(cs11.toString()).thenReturn(List.of("0","0").toString());
//        when(cs11.getState()).thenReturn(asList(bs0,bs0));

        when(cs12.toString()).thenReturn(List.of("1","0").toString());
 //       when(cs12.getState()).thenReturn(asList(bs1,bs0));

//        when(cs13.isInitial()).thenReturn(false);
        when(cs13.isFinalState()).thenReturn(true);
//        when(cs13.toString()).thenReturn(List.of("1","2").toString());
        when(cs13.getState()).thenReturn(asList(bs1,bs2));


//        when(ia.toString()).thenReturn("-");
//        when(offact1.getLabel()).thenReturn("a1");
//        when(offact1.toString()).thenReturn("!a1");
//        when(reqact1.getLabel()).thenReturn("a1");
//        when(reqact1.toString()).thenReturn("?a1");
//        when(offact2.getLabel()).thenReturn("a2");
//        when(offact2.toString()).thenReturn("!a2");
//        when(reqact2.getLabel()).thenReturn("a2");
//        when(reqact2.toString()).thenReturn("?a2");

        CALabel lab11 = mock(CALabel.class);
    //    when(lab11.getLabel()).thenReturn(List.of(reqact1,offact1));
    //    when(lab11.getAction()).thenReturn(offact1);

        when(t11.getLabel()).thenReturn(lab11);
    //    when(t11.isNecessary()).thenReturn(true);
        when(t11.getSource()).thenReturn(cs11);
        when(t11.getTarget()).thenReturn(cs12);
    //    when(t11.getModality()).thenReturn(ModalTransition.Modality.URGENT);
//        when(t11.getRank()).thenReturn(2);
        when(t11.toString()).thenReturn("([0, 0],[?a1,!a1],[1, 0])");

        CALabel lab12 = mock(CALabel.class);
    //    when(lab12.getAction()).thenReturn(offact2);
    //    when(lab12.getLabel()).thenReturn(List.of(offact2,reqact2));
        when(t12.getLabel()).thenReturn(lab12);
        when(t12.getSource()).thenReturn(cs12);
        when(t12.getTarget()).thenReturn(cs13);
    //    when(t12.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);
//        when(t12.getRank()).thenReturn(2);
        when(t12.toString()).thenReturn("([1, 0],[!a2,?a2],[1, 2])");


        when(a1.getTransition()).then(args -> new HashSet<>(Arrays.asList(t11, t12)));
        when(a1.getStates()).thenReturn(Set.of(cs11, cs12, cs13));
   //     when(a1.getBasicStates()).thenReturn(Map.of(1,Set.of(bs0,bs2),2,Set.of(bs0,bs1)));
   //     when(a1.getForwardStar(cs11)).thenReturn(Set.of(t11));
   //     when(a1.getForwardStar(cs12)).thenReturn(Set.of(t12));
   //     when(a1.getForwardStar(cs13)).thenReturn(Collections.emptySet());
        when(a1.getInitial()).thenReturn(cs11);
    //    when(a1.getRank()).thenReturn(2);


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