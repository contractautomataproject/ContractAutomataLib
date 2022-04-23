package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.family.Product;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.Silent.class)
public class ProductOrchestrationSynthesisOperatorTest {

    @Mock(answer = Answers.RETURNS_SMART_NULLS)
    Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>, CALabel>> aut;

    @Mock
    Product p;
    @Mock CALabel lab;

    @Mock
    BasicState<String> bs1;
    @Mock BasicState<String> bs2;
    @Mock BasicState<String> bs3;

    @Mock State<String> cs11;
    @Mock State<String> cs12;
    @Mock State<String> cs13;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t11;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t12;


    ProductOrchestrationSynthesisOperator<String> oso;

    @Before
    public void setUp() {

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

        oso = new ProductOrchestrationSynthesisOperator<>(l->true, p);
    }

    @Test
    public void apply() {
        when(p.checkRequired(any())).thenReturn(true);
        assertNotNull(oso.apply(aut));
    }


    @Test
    public void applyNullMissingRequired() {
        assertNull(oso.apply(aut));
    }


    @Test
    public void applyNullAllForbidden() {
        when(p.isForbidden(any())).thenReturn(true);
        assertNull(oso.apply(aut));
    }


    @Test
    public void applyNullAllPruned() {
        when(p.checkRequired(any())).thenReturn(true);
        oso = new ProductOrchestrationSynthesisOperator<>(l->false, p);
        assertNull(oso.apply(aut));
    }
}