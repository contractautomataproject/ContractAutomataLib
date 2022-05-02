package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
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
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.Strict.class)
public class ModelCheckingSynthesisOperatorTest {

    final static RuntimeException re = new RuntimeException();

    @Mock Label<Action> lab;
    @Mock IdleAction ia;
    @Mock Action a1;
    @Mock Action a2;

    @Mock BasicState<String> bs0;
    @Mock State<String> cs11;
    @Mock State<String> cs21;
    @Mock State<String> comp1;

    @Mock Label<Action> lab1conv;
    @Mock CALabel lab1;
    @Mock CALabel labcompreverse;
    @Mock Label<Action> lab2;
    @Mock Label<Action> labcomp;
    @Mock Label<Action> labCompNoMatch;

    @Mock Predicate<CALabel> req;


    @Mock
    Automaton<String, Action, State<String>,
                            ModalTransition<String, Action, State<String>,CALabel>> aut;

    @Mock Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>,Label<Action>>> autconv;

    @Mock Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>,Label<Action>>> prop;


    @Mock Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>,Label<Action>>> comp;


    @Mock Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>,CALabel>> compconv;

    @Mock  ModalTransition<String, Action, State<String>,CALabel> t1;
    @Mock  ModalTransition<String, Action, State<String>,Label<Action>> t1conv;
    @Mock  ModalTransition<String, Action, State<String>,CALabel> tcompreverse;
    @Mock  ModalTransition<String, Action, State<String>,Label<Action>> tp1;
    @Mock  ModalTransition<String, Action, State<String>,Label<Action>> tcomp;
    @Mock  ModalTransition<String, Action, State<String>,Label<Action>> tcompNoMatch;

    @Mock Function<List<BasicState<String>>,State<String>> createState;

    TetraFunction<State<String>,CALabel,State<String>,ModalTransition.Modality,
            ModalTransition<String, Action, State<String>,CALabel>>
            createTransition;

    @Mock Function<List<Action>,CALabel> createLabel;

    @Mock UnaryOperator<CALabel> changeLabel;

    @Mock Function<Set<ModalTransition<String, Action, State<String>,CALabel>>,Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>,CALabel>>> createAutomaton;


    @Mock Function<List<Action>,Label<Action>> createLabelProp;
    TetraFunction<State<String>,Label<Action>,State<String>,ModalTransition.Modality, ModalTransition<String, Action, State<String>,Label<Action>>>
            createTransitionProp;
    @Mock Function<Set<ModalTransition<String, Action, State<String>,Label<Action>>>,Automaton<String, Action, State<String>,
            ModalTransition<String, Action, State<String>,Label<Action>>>> createAutomatonProp;

    ModelCheckingSynthesisOperator<String,State<String>,CALabel,
            ModalTransition<String, Action, State<String>,CALabel>,
            Automaton<String, Action, State<String>,
                    ModalTransition<String, Action, State<String>,CALabel>>,
            Label<Action>,ModalTransition<String, Action, State<String>,Label<Action>>,
                    Automaton<String, Action, State<String>,
                            ModalTransition<String, Action, State<String>,Label<Action>>>> mcso;

    @Before
    public void setUp() throws Throwable {
        Mockito.withSettings().defaultAnswer(Answers.RETURNS_SMART_NULLS);

        doReturn(true).when(cs11).isInitial();
        doReturn(true).when(cs11).isFinalState();
        doReturn(asList(bs0,bs0)).when(cs11).getState();

        doReturn(true).when(cs21).isInitial();
//        doReturn(true).when(cs21).isFinalState();
        doReturn(List.of(bs0)).when(cs21).getState();
        doReturn(true).when(comp1).isFinalState();
        doReturn(asList(bs0,bs0,bs0)).when(comp1).getState();

        doReturn("a").when(a1).getLabel();
        doReturn("a").when(a2).getLabel();

//        doReturn(List.of(ia,a2)).when(lab1).getLabel();
        doReturn(List.of(ia,a2)).when(lab1conv).getContent();
        doReturn(a2).when(lab1conv).getAction();

        doReturn(cs11).when(t1).getSource();
        doReturn(lab1).when(t1).getLabel();
        doReturn(cs11).when(t1).getTarget();
//        doReturn(cs11).when(t1conv).getSource();
        doReturn(lab1conv).when(t1conv).getLabel();
        doReturn(cs11).when(t1conv).getTarget();

        doReturn(List.of(a1)).when(lab2).getContent();
        doReturn(lab2).when(tp1).getLabel();
        doReturn(cs21).when(tp1).getTarget();
        doReturn(ModalTransition.Modality.PERMITTED).when(tp1).getModality();

//        doReturn(3).when(labcomp).getRank();
        doReturn(List.of(ia,a2,a1)).when(labcomp).getContent();
        doReturn(comp1).when(tcomp).getSource();
        doReturn(labcomp).when(tcomp).getLabel();
        doReturn(comp1).when(tcomp).getTarget();
        doReturn(ModalTransition.Modality.PERMITTED).when(tcomp).getModality();
        doReturn(3).when(tcomp).getRank();


//        doReturn(3).when(labcompreverse).getRank();
//        doReturn(List.of(ia,a2,ia)).when(labcompreverse).getLabel();
        doReturn(comp1).when(tcompreverse).getSource();
        doReturn(labcompreverse).when(tcompreverse).getLabel();
        doReturn(comp1).when(tcompreverse).getTarget();
//        doReturn(ModalTransition.Modality.PERMITTED).when(tcompreverse).getModality();
//        doReturn(3).when(tcompreverse).getRank();

//        doReturn(3).when(labCompNoMatch).getRank();
//        doReturn(List.of(ia,a2,ia)).when(labCompNoMatch).getLabel();
//        doReturn(labCompNoMatch).when(tcompNoMatch).getLabel();

        doReturn(cs11).when(aut).getInitial();
//        doReturn(cs11).when(autconv).getInitial();
//        doReturn(cs21).when(prop).getInitial();
//        doReturn(comp1).when(comp).getInitial();
        doReturn(comp1).when(compconv).getInitial();
        doReturn(Set.of(cs11)).when(aut).getStates();
        doReturn(Set.of(cs11)).when(autconv).getStates();
        doReturn(Set.of(cs21)).when(prop).getStates();
//        doReturn(Set.of(comp1)).when(comp).getStates();
        doReturn(Set.of(comp1)).when(compconv).getStates();
//        doReturn(Collections.singleton(t1)).when(aut).getForwardStar(cs11);
        doReturn(Collections.singleton(t1conv)).when(autconv).getForwardStar(cs11);
        doReturn(Collections.singleton(tp1)).when(prop).getForwardStar(cs21);
//        doReturn(Collections.singleton(tcomp)).when(comp).getForwardStar(comp1);
//        doReturn(Collections.singleton(tcompreverse)).when(compconv).getForwardStar(comp1);
//        doReturn(2).when(aut).getRank();
        doReturn(2).when(autconv).getRank();
        doReturn(1).when(prop).getRank();
//        doReturn(3).when(comp).getRank();
//        doReturn(3).when(compconv).getRank();

        doAnswer(args -> new HashSet<>(List.of(t1))).when(aut).getTransition();
//        doAnswer(args -> new HashSet<>(Arrays.asList(tp1))).when(prop).getTransition();
//        doAnswer(args -> new HashSet<>(Arrays.asList(t1conv))).when(autconv).getTransition();
        doAnswer(args -> new HashSet<>(List.of(tcomp))).when(comp).getTransition();
        doAnswer(args -> new HashSet<>(List.of(tcompreverse))).when(compconv).getTransition();

        createTransitionProp = (s,l,t,m) -> {
            if (s!=null && s.equals(t1.getSource()) && l!=null && l.equals(t1.getLabel()) && t!=null && t.equals(t1.getTarget())) return t1conv;
            if (s!=null && s.equals(comp1) && l!=null && l.equals(labcomp) && t!=null && t.equals(comp1)) return tcomp;
            else return tcompNoMatch;
        };

        createTransition = (s,l,t,m) -> {
            if (s!=null && s.equals(comp1) && l!=null && l.equals(labcompreverse) && t!=null && t.equals(comp1)) return tcompreverse;
            else throw new RuntimeException();
        };

        when(createState.apply(comp1.getState())).thenReturn(comp1);
        when(createLabel.apply(List.of(ia,a2, new IdleAction()))).thenReturn(labcompreverse);
        when(createLabelProp.apply(labcomp.getContent())).thenReturn(labcomp);
        when(createAutomatonProp.apply(Set.of(t1conv))).thenReturn(autconv);//when converting
        when(createAutomatonProp.apply(Set.of(tcomp))).thenReturn(comp);//after composing autconv with prop
        when(createAutomaton.apply(Set.of(tcompreverse))).thenReturn(compconv);//when reverting


        when(req.test(any())).thenReturn(true);

        when(changeLabel.apply(any())).thenReturn(labcompreverse);

        mcso = new ModelCheckingSynthesisOperator<>((x,t,bad) -> false,req,prop,changeLabel,createAutomaton,createLabel,
                createTransition,createState,createLabelProp,createTransitionProp,createAutomatonProp);

    }

    @Test
    public void getChangeLabel() {
        assertEquals(changeLabel,mcso.getChangeLabel());
    }

    @Test
    public void apply() {
        assertNotNull(mcso.apply(aut));
        verify(createAutomatonProp).apply(Set.of(t1conv));
        verify(createAutomatonProp).apply(Set.of(tcomp));
        verify(changeLabel,never()).apply(any());
        verify(createAutomaton, times(2)).apply(Set.of(tcompreverse));
    }


    @Test
    public void applyModelCheckingNull(){
        when(createAutomatonProp.apply(Set.of(tcomp))).thenReturn(null);//after composing autconv with prop
        assertNull(mcso.apply(aut));
    }

    @Test
    public void applyCoverChangeLabel() {
        when(tcomp.isLazy()).thenReturn(true);
        when(tcomp.isNecessary()).thenReturn(true);
        when(labcomp.getContent()).thenReturn(List.of(ia,a2,ia));//pruningpred true in model checking
        assertNotNull(mcso.apply(aut));
        verify(changeLabel).apply(any());
    }


    @Test
    public void applyCoverChangeLabelBranchNotSatisfyReq() {
        when(req.test(any())).thenReturn(false);
        when(tcomp.isLazy()).thenReturn(true);
        when(tcomp.isNecessary()).thenReturn(true);
        when(labcomp.getContent()).thenReturn(List.of(ia,a2,ia));//pruningpred true in model checking
        mcso.apply(aut);
        verify(changeLabel,never()).apply(any());
    }



    @Test
    public void applyCoverChangeLabelBranchNotSatisfyLazy() {
        when(tcomp.isNecessary()).thenReturn(true);
        when(labcomp.getContent()).thenReturn(List.of(ia,a2,ia));//pruningpred true in model checking
        mcso.apply(aut);
        verify(changeLabel,never()).apply(any());
    }

    @Test
    public void applyWithoutModelChecking(){
        mcso = new ModelCheckingSynthesisOperator<>((x,t,bad) -> false,req,createAutomaton,createLabel,
                createTransition,createState);
        when(createAutomaton.apply(Set.of(t1))).thenReturn(aut);
        assertNotNull(mcso.apply(aut));
        verify(createAutomaton).apply(Set.of(t1));
    }
}