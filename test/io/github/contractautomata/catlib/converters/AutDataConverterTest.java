package io.github.contractautomata.catlib.converters;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.ITAutomatonTest;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.transition.Transition;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class AutDataConverterTest {

    @Mock Label<Action> lab;

    @Spy Function<List<Action>,Label<Action>> createLabel;

    private AutDataConverter<Label<Action>> adc;

    private static final String dir = System.getProperty("user.dir")+ File.separator+"test_resources"+File.separator;

    private static  Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, Label<Action>>> axb;

    @BeforeClass
    public static void setup() throws IOException {
        Label<Action> l = Mockito.mock(Label.class);
        when(l.getRank()).thenReturn(2);
        when(l.toString()).thenReturn("mock");
        axb = new AutDataConverter<Label<Action>>(new Function<List<Action>, Label<Action>>() {
            @Override
            public Label<Action> apply(List<Action> actions) {
                return l;
            }
        }).importMSCA(dir+"(AxB).data");
    }


    @Before
    public void setUp() throws Exception {
        createLabel =  Mockito.spy(new Function<List<Action>, Label<Action>>() {
            @Override
            public Label<Action> apply(List<Action> actions) {
                return lab;
            }
        });

        when(lab.getRank()).thenReturn(2);
        when(lab.toString()).thenReturn("[test,test]");

        adc = new AutDataConverter<>(createLabel);
    }

    @After
    public void tearDown() throws Exception {
    }


    @Test
    public void testImportInitial() {
        assertEquals("[0, 0]",axb.getInitial().toString());
    }

    @Test
    public void testFinalStates() {
        assertEquals("[[2, 2]]",axb.getStates().parallelStream()
                .filter(State::isFinalState)
                .collect(Collectors.toList()).toString());
    }

    @Test
    public void testFinalStatesPrincipal0() {
        assertEquals("[label=2,final=true]",axb.getBasicStates().get(0)
                .stream().filter(BasicState::isFinalState)
                .collect(Collectors.toList()).toString());
    }

    @Test
    public void testFinalStatesPrincipal1() {
        assertEquals("[label=2,final=true]",axb.getBasicStates().get(1)
                .stream().filter(BasicState::isFinalState)
                .collect(Collectors.toList()).toString());
    }

    @Test
    public void testTransitions() {
       assertEquals("[([0, 0],mock,[1, 1]), ([1, 1],mock,[1, 2]), ([1, 1],mock,[2, 1]), ([1, 2],mock,[2, 2]), ([2, 1],mock,[2, 2])]",
               axb.getTransition()
                       .stream()
                       .sorted(Comparator.comparing(Transition::toString))
                       .collect(Collectors.toList()).toString());
    }


    @Test
    public void testImportMSCA() throws IOException {
        Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, Label<Action>>> aut =
                adc.importMSCA(dir+"(AxB).data");

        verify(createLabel,times(5)).apply(Mockito.anyList());

    }


    @Test
    public void testImportMSCANecessary() throws IOException {
        when(lab.getRank()).thenReturn(1);
        Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, Label<Action>>> aut =
                adc.importMSCA(dir+"BusinessClient.data");

        verify(createLabel,times(6)).apply(Mockito.anyList());
    }

    @Test
    public void testImportProp() throws IOException {
        when(lab.getRank()).thenReturn(1);
        adc.importMSCA(dir+"prop.data");
        verify(createLabel,times(1)).apply(List.of(new Action("cherry")));
        verify(createLabel,times(1)).apply(List.of(new Action("blueberry")));
        verify(createLabel,times(1)).apply(List.of(new Action("ananas")));
    }


    @Test
    public void testImportIllFinalStates() throws IOException {
        when(lab.getRank()).thenReturn(1);
        Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, Label<Action>>> aut =
                adc.importMSCA(dir+"illformed7.data");

        assertEquals("[[3]]",aut.getStates().parallelStream()
                .filter(s->s.isFinalState())
                .collect(Collectors.toList()).toString());
    }

    @Test
    public void testImportEmptyFile() throws IOException {
        assertThrows(IllegalArgumentException.class,
                ()->adc.importMSCA(dir+"emptyfile.data"));
    }
    
    @Test
    public void testImportMSCANoDataFormat() throws IOException {
        assertThrows("Not a .data format",IllegalArgumentException.class,()->adc.importMSCA(dir+"test"));
    }

    @Test
    public void testImportMSCANoFile() throws IOException {
        assertThrows(FileNotFoundException.class,()->adc.importMSCA(dir+"fdafdfdaf.data"));
    }

    @Test
    public void testWrongFormatData_exception() {
        assertThrows(IllegalArgumentException.class,() -> adc.importMSCA(dir+"BusinessClient.mxe"));
    }

    @Test
    public void testEmptyFileName_exception() throws NumberFormatException {
        assertThrows("Empty file name",IllegalArgumentException.class,() -> 
            adc.exportMSCA("", null));
    }


    @Test
    public void testImportIllActions_exception() throws NumberFormatException {
        assertThrows(IllegalArgumentException.class,() -> adc.importMSCA(dir+"illformed.data"));
    }

    @Test
    public void testImportIllRankStatesHigher_exception() throws NumberFormatException {
        assertThrows("Ill-formed transitions, different ranks",IOException.class,() -> adc.importMSCA(dir+"illformed2.data"));
                
    }


    @Test
    public void testImportIllRankActions_exception() throws NumberFormatException {
        assertThrows("Ill-formed transitions, different ranks",IOException.class,() -> adc.importMSCA(dir+"illformed6.data"));
    }

    @Test
    public void testImportIllRankStatesLower_exception() throws NumberFormatException {
        assertThrows("Ill-formed transitions, different ranks",IOException.class,() -> adc.importMSCA(dir+"illformed3.data"));
                
    }

    @Test
    public void testImportIllRankInitialStatesLower_exception() throws NumberFormatException {
        assertThrows("Initial state with different rank",IllegalArgumentException.class,() -> adc.importMSCA(dir+"illformed4.data"));
    }

    @Test
    public void testImportIllRankFinalStatesLower_exception() throws NumberFormatException {
        assertThrows("Final states with different rank",IllegalArgumentException.class,() -> adc.importMSCA(dir+"illformed5.data"));
                
    }

    @Test
    public void testExportMSCA() throws IOException {
        Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, Label<Action>>> aut =
                adc.importMSCA(dir+"(AxB).data");

        adc.exportMSCA(dir+"(AxB)_export",aut); //without extension

        assertTrue(ITAutomatonTest.autEquals(aut,adc.importMSCA(dir+"(AxB)_export.data")));
    }

    @Test
    public void testExportException() throws IOException {
        assertThrows(IllegalArgumentException.class, ()->
        adc.exportMSCA("",null)); //without extension

    }
}