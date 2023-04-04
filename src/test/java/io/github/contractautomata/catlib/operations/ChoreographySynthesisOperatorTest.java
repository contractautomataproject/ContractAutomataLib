package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.OfferAction;
import io.github.contractautomata.catlib.automaton.label.action.RequestAction;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.operations.interfaces.TriPredicate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.Silent.class)
public class ChoreographySynthesisOperatorTest {

    @Mock ModalTransition<String,Action,State<String>,CALabel> tra;
    @Mock Set<ModalTransition<String,Action,State<String>,CALabel>> trans;
    @Mock Set<State<String>> bad;

    @Mock
    Automaton<String, Action, State<String>,
                            ModalTransition<String, Action, State<String>, CALabel>> aut;

    @Mock CALabel lab;
    @Mock CALabel lab2;
    @Mock OfferAction oa;

    @Mock State<String> cs1;
    @Mock State<String> cs2;
    @Mock State<String> cs3;
    @Mock State<String> cs21;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t11;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t12;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t13;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t14;

    @Captor
    ArgumentCaptor<TriPredicate
            <ModalTransition<String, Action, State<String>, CALabel>,
                    Set<ModalTransition<String, Action, State<String>, CALabel>>,
                    Set<State<String>>>> predicateCaptor;

    ChoreographySynthesisOperator<String> cso;

    @Before
    public void setUp() {
        //value objects
        BasicState<String> bString = new BasicState<>("bString",true,false);
        BasicState<String> bs2 = new BasicState<>("bs2",false,false);
        BasicState<String> bs21 = new BasicState<>("bs2",false,false);
        BasicState<String> bs3 = new BasicState<>("bs3",false,true);

        when(cs1.isInitial()).thenReturn(true);
        when(cs3.isFinalState()).thenReturn(true);
        when(cs1.getState()).thenReturn(List.of(bString));
        when(cs2.getState()).thenReturn(List.of(bs2));
        when(cs21.getState()).thenReturn(List.of(bs21));
        when(cs3.getState()).thenReturn(List.of(bs3));

        when(lab.getOfferer()).thenReturn(0);
        when(lab.getAction()).thenReturn(oa);
        when(lab2.getOfferer()).thenReturn(0);
        when(lab2.getAction()).thenReturn(oa);

        when(t11.getLabel()).thenReturn(lab);
        when(t11.getSource()).thenReturn(cs1);
        when(t11.getTarget()).thenReturn(cs2);
        when(t11.isPermitted()).thenReturn(true);


        when(t12.getLabel()).thenReturn(lab);
        when(t12.getSource()).thenReturn(cs2);
        when(t12.getTarget()).thenReturn(cs3);
        when(t12.isPermitted()).thenReturn(true);


        when(t13.getLabel()).thenReturn(lab);
        when(t13.getSource()).thenReturn(cs1);
        when(t13.getTarget()).thenReturn(cs21);
        when(t13.isPermitted()).thenReturn(true);

        when(t14.getLabel()).thenReturn(lab2);
        when(t14.getSource()).thenReturn(cs21);
        when(t14.getTarget()).thenReturn(cs3);

        when(aut.getTransition()).then(args -> new HashSet<>(Arrays.asList(t11, t12, t13, t14)));
        when(aut.getStates()).thenReturn(Set.of(cs1, cs2, cs3, cs21));
        when(aut.getInitial()).thenReturn(cs1);

        cso = new ChoreographySynthesisOperator<String>(l->true);
    }


    
    @Test
    public void apply() {
        Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>, CALabel>> chor = cso.apply(aut);
        assertNotNull(chor);
        assertEquals(2, chor.getTransition().size());
    }

    @Test
    public void applyNull() {
        cso = new ChoreographySynthesisOperator<>(l->false);
        assertNull(cso.apply(aut));
    }


    @Test
    public void applyNullKillMutant() {
        when(t11.isPermitted()).thenReturn(false);
        when(t13.isPermitted()).thenReturn(false);
        CALabel lab3 = mock(CALabel.class);
        when(t11.getLabel()).thenReturn(lab3);
        when(t11.isUncontrollable(any(),any(),any())).thenReturn(true);
        cso = new ChoreographySynthesisOperator<>(l->!l.equals(lab3));
        assertNull(cso.apply(aut));
    }

    @Test
    public void applyNullKillMutantSecondConstructor() {
        when(t11.isPermitted()).thenReturn(false);
        when(t13.isPermitted()).thenReturn(false);
        CALabel lab3 = mock(CALabel.class);
        when(t11.getLabel()).thenReturn(lab3);
        when(t11.isUncontrollable(any(),any(),any())).thenReturn(true);
        cso = new ChoreographySynthesisOperator<String>(l->!l.equals(lab3),(Automaton) null);
        assertNull(cso.apply(aut));
    }

    @Test
    public void applyNullKillMutantThirdConstructor() {
        when(t11.isPermitted()).thenReturn(false);
        when(t13.isPermitted()).thenReturn(false);
        CALabel lab3 = mock(CALabel.class);
        when(t11.getLabel()).thenReturn(lab3);
        when(t11.isUncontrollable(any(),any(),any())).thenReturn(true);
        cso = new ChoreographySynthesisOperator<String>(l->!l.equals(lab3),Stream::findAny);
        assertNull(cso.apply(aut));
    }

    @Test
    public void applyConstructor() {
        cso = new ChoreographySynthesisOperator<>(l->true,(Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, Label<Action>>>)
                null);
        assertNotNull(cso.apply(aut));
    }

    @Test
    public void applyConstructorChoice() {
        Function<Stream<ModalTransition<String, Action,State<String>,CALabel>>,Optional<ModalTransition<String,Action,State<String>,CALabel>>>
                choice=Stream::findAny;

        cso = new ChoreographySynthesisOperator<>(l->true,choice);
        assertNotNull(cso.apply(aut));
    }

    @Test
    public void satisfiesBranchingCondition() {
        assertTrue(cso.satisfiesBranchingCondition(tra,trans,bad));
    }


    @Test
    public void applyException() {
        when(t11.isPermitted()).thenReturn(false);
        when(t12.isPermitted()).thenReturn(false);
        when(t13.isPermitted()).thenReturn(false);
        when(lab.isRequest()).thenReturn(true);
        assertThrows(UnsupportedOperationException.class, ()->cso.apply(aut));
    }

    @Test
    public void testChangeLabel() {
        cso = new ChoreographySynthesisOperator<>(l->true, (Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, Label<Action>>>)
                null);
        when(lab.getRank()).thenReturn(1);
        when(lab.getAction()).thenReturn(mock(RequestAction.class));
        assertNotNull(cso.getChangeLabel().apply(lab));
    }

    private TriPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> getPredicate(){
        when(t11.isPermitted()).thenReturn(false);
        cso = new ChoreographySynthesisOperator<>(l->false);
        assertNull(cso.apply(aut));
        verify(t11,times(2)).isUncontrollable(anySet(),anySet(),predicateCaptor.capture());

        return predicateCaptor.getValue();
    }

    @Test
    public void applyControllabilityPredicateNoMatchTransitions() {
        TriPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = getPredicate();

        when(lab.getOfferer()).thenReturn(1);
        when(lab2.getOfferer()).thenReturn(1);
        when(t12.getLabel()).thenReturn(lab2);
        when(lab.getAction()).thenReturn(oa);
        when(lab2.getAction()).thenReturn(oa);
        when(t12.getSource()).thenReturn(cs1);
        assertTrue(pred.test(t11,Set.of(t12),Collections.emptySet()));
    }

    @Test
    public void applyControllabilityPredicateContainsBadState() {
        TriPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = getPredicate();

        when(lab.getOfferer()).thenReturn(1);
        when(lab2.getOfferer()).thenReturn(2);
        when(t12.getLabel()).thenReturn(lab2);
        when(lab2.isMatch()).thenReturn(true);
        assertTrue(pred.test(t11,Set.of(t12),Set.of(cs2)));
    }

    @Test
    public void applyControllabilityPredicateFalseDifferentRequester() {
        TriPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = getPredicate();

        when(lab.getOfferer()).thenReturn(1);
        when(lab2.getOfferer()).thenReturn(2);
        when(t12.getLabel()).thenReturn(lab2);
        when(lab2.isMatch()).thenReturn(true);
        assertTrue(pred.test(t11,Set.of(t12),Collections.emptySet()));
    }


    @Test
    public void applyControllabilityPredicateFalseDifferentAction() {
        TriPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = getPredicate();

        when(lab2.getAction()).thenReturn(mock(RequestAction.class));

        when(lab.getOfferer()).thenReturn(1);
        when(lab2.getOfferer()).thenReturn(1);
        when(t12.getLabel()).thenReturn(lab2);

        when(lab2.isMatch()).thenReturn(true);

        assertTrue(pred.test(t11,Set.of(t12),Collections.emptySet()));
    }

    @Test
    public void applyControllabilityPredicateFalseDifferentSources() {
        TriPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = getPredicate();

        CALabel lab2 = mock(CALabel.class);
        when(lab2.getAction()).thenReturn(oa);

        when(lab.getOfferer()).thenReturn(1);
        when(lab2.getOfferer()).thenReturn(1);
        when(t12.getLabel()).thenReturn(lab2);

        when(lab2.isMatch()).thenReturn(true);

        assertTrue(pred.test(t11,Set.of(t12),Collections.emptySet()));
    }

    @Test
    public void applyControllabilityPredicateIsControllable() {

        when(t12.isPermitted()).thenReturn(false);

        cso = new ChoreographySynthesisOperator<>(l->false);

        TriPredicate<ModalTransition<String, Action, State<String>, CALabel>,
                Set<ModalTransition<String, Action, State<String>, CALabel>>,
                Set<State<String>>> pred = getPredicate();

        CALabel lab2 = mock(CALabel.class);
        when(lab2.getAction()).thenReturn(oa); //same action

        when(lab.getOfferer()).thenReturn(1);
        when(t12.getLabel()).thenReturn(lab2);
        when(lab2.getOfferer()).thenReturn(1); //same offerer
        when(t12.getSource()).thenReturn(cs1); //same state
        when(lab2.isMatch()).thenReturn(true);

        assertFalse(pred.test(t11,Set.of(t12),Collections.emptySet()));
    }

    @Test
    public void testEmptyFTRreq(){
        cso = new ChoreographySynthesisOperator<String>(l->false);
        assertTrue(cso.satisfiesBranchingCondition(null, Collections.singleton(t11),null));
    }


    @Test
    public void testEmptyFTRbadContainsSource(){
        assertTrue(cso.satisfiesBranchingCondition(null, Collections.singleton(t11),Collections.singleton(cs1)));
    }


    @Test
    public void testEmptyFTRbadContainsTarget(){
        assertTrue(cso.satisfiesBranchingCondition(null, Collections.singleton(t11),Collections.singleton(cs2)));
    }


    @Test
    public void testSatisfiesBranchingConditionFalse(){
        assertFalse(cso.satisfiesBranchingCondition(t12, Set.of(t12,t14),Collections.emptySet()));
    }


    @Test
    public void testSatisfiesBranchingConditionTrueNonempty(){
        when(t14.getLabel()).thenReturn(lab);
        assertTrue(cso.satisfiesBranchingCondition(t12, Set.of(t12,t14),Collections.emptySet()));
    }

}