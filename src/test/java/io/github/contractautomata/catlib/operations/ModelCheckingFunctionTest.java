package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
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
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.Strict.class)
public class ModelCheckingFunctionTest {

    @Mock IdleAction ia;
    @Mock Action a1;
    @Mock Action a2;

    @Mock BasicState<String> bs0;
    @Mock State<String> cs11;
    @Mock State<String> cs21;
    @Mock State<String> comp1;

    @Mock Label<Action> lab1;
    @Mock Label<Action> lab2;
    @Mock Label<Action> labcomp;
    @Mock Label<Action> labCompNoMatch;

    @Mock Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>,Label<Action>>> aut;

    @Mock Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>,Label<Action>>> prop;

    @Mock  ModalTransition<String, Action, State<String>,Label<Action>> t1;
    @Mock  ModalTransition<String, Action, State<String>,Label<Action>> tp1;
    @Mock  ModalTransition<String, Action, State<String>,Label<Action>> tcomp;
    @Mock  ModalTransition<String, Action, State<String>,Label<Action>> tcompNoMatch;

    @Mock Function<List<BasicState<String>>,State<String>> createState;
    TetraFunction<State<String>,Label<Action>,State<String>,ModalTransition.Modality, ModalTransition<String, Action, State<String>,Label<Action>>> createTransition;
    @Mock Function<List<Action>,Label<Action>> createLabel;
    @Mock Function<Set<ModalTransition<String, Action, State<String>,Label<Action>>>,Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>,Label<Action>>>> createAutomaton;

    ModelCheckingFunction<String,State<String>,Label<Action>,
            ModalTransition<String, Action, State<String>,Label<Action>>,
            Automaton<String, Action, State<String>,
                    ModalTransition<String, Action, State<String>,Label<Action>>>> mc;

    @Before
    public void setUp(){
        when(cs11.isInitial()).thenReturn(true);
        when(cs11.getState()).thenReturn(asList(bs0,bs0));
        when(cs21.isInitial()).thenReturn(true);
        when(cs21.getState()).thenReturn(asList(bs0));
        when(comp1.isFinalState()).thenReturn(true);
        when(comp1.getState()).thenReturn(asList(bs0,bs0,bs0));
        when(aut.getStates()).thenReturn(Set.of(cs11));
        when(prop.getStates()).thenReturn(Set.of(cs21));

        when(a1.getLabel()).thenReturn("a");
        when(a2.getLabel()).thenReturn("a");

        when(lab1.getContent()).thenReturn(List.of(ia,a2));
        when(lab1.getAction()).thenReturn(a2);
        when(t1.getLabel()).thenReturn(lab1);
        when(t1.getTarget()).thenReturn(cs11);

        when(lab2.getContent()).thenReturn(List.of(a1));
        when(tp1.getLabel()).thenReturn(lab2);
        when(tp1.getTarget()).thenReturn(cs21);
        when(tp1.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);

        when(labcomp.getRank()).thenReturn(3);
        when(labcomp.getContent()).thenReturn(List.of(ia,a2,a1));
        when(tcomp.getSource()).thenReturn(comp1);
        when(tcomp.getLabel()).thenReturn(labcomp);
        when(tcomp.getTarget()).thenReturn(comp1);

        when(labCompNoMatch.getRank()).thenReturn(3);
        when(labCompNoMatch.getContent()).thenReturn(List.of(ia,a2,ia));
        when(tcompNoMatch.getLabel()).thenReturn(labCompNoMatch);

        when(aut.getForwardStar(cs11)).thenReturn(Collections.singleton(t1));
        when(prop.getForwardStar(cs21)).thenReturn(Collections.singleton(tp1));

        when(aut.getRank()).thenReturn(2);
        when(prop.getRank()).thenReturn(1);

        when(createState.apply(comp1.getState())).thenReturn(comp1);
        when(createLabel.apply(labcomp.getContent())).thenReturn(labcomp);

        createTransition = (s,l,t,m) -> {
            if (s!=null && s.equals(comp1) && l!=null && l.equals(labcomp) && t!=null && t.equals(comp1)) return tcomp;
            else return tcompNoMatch;
        };
    }


    @Test
    public void testConstructor(){
        mc = new ModelCheckingFunction<>(aut,prop,createState,createTransition,createLabel,createAutomaton);
        mc.apply(Integer.MAX_VALUE);
        verify(createAutomaton).apply(any());
    }


    @Test
    public void testConstructorNoMatch(){
        when(a2.getLabel()).thenReturn("b");
        mc = new ModelCheckingFunction<>(aut,prop,createState,createTransition,createLabel,createAutomaton);
        mc.apply(Integer.MAX_VALUE);
        verify(createAutomaton, never()).apply(any());
    }

    @Test
    public void testConstructorPruningPredTrue1(){
        when(labcomp.getContent()).thenReturn(List.of(ia,a2,ia));
        mc = new ModelCheckingFunction<>(aut,prop,createState,createTransition,createLabel,createAutomaton);
        mc.apply(Integer.MAX_VALUE);
        verify(createAutomaton,never()).apply(any());
    }

    @Test
    public void testConstructorPruningPredTrue2(){
        when(labcomp.getContent()).thenReturn(List.of(ia,ia,a1));
        mc = new ModelCheckingFunction<>(aut,prop,createState,createTransition,createLabel,createAutomaton);
        mc.apply(Integer.MAX_VALUE);
        verify(createAutomaton,never()).apply(any());
    }

    @Test
    public void constructorException(){
        when(prop.getRank()).thenReturn(2);
        assertThrows(IllegalArgumentException.class, ()->new ModelCheckingFunction<>(aut,prop,createState,createTransition,createLabel,createAutomaton));
        verify(prop, times(2)).getRank();
    }
}