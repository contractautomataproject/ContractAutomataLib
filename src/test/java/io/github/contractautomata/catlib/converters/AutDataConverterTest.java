package io.github.contractautomata.catlib.converters;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.AutomatonTest;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.transition.Transition;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static it.io.github.contractautomata.catlib.automaton.ITAutomatonTest.dir;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.StrictStubs.class)
public class AutDataConverterTest {

	@SuppressWarnings("unchecked")
	static Label<Action> l = mock(Label.class);
	
    @Mock Label<Action> lab;

    @Spy Function<List<Action>,Label<Action>> createLabel;

    private AutDataConverter<Label<Action>> adc;

    private static  Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, Label<Action>>> axb;

    @BeforeClass
    public static void setup() throws IOException {
        when(l.getRank()).thenReturn(2);
        when(l.toString()).thenReturn("mock");
        axb = new AutDataConverter<>(new Function<>() {
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
        adc.importMSCA(dir+"(AxB).data");
        verify(createLabel,times(5)).apply(Mockito.anyList());

    }


    @Test
    public void testImportMSCANecessary() throws IOException {
        when(lab.getRank()).thenReturn(1);
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
                .filter(State::isFinalState)
                .collect(Collectors.toList()).toString());
    }

    @Test
    public void testImportEmptyFile()  {
        assertThrows(IllegalArgumentException.class,
                ()->adc.importMSCA(dir+"emptyfile.data"));
    }
    
    @Test
    public void testImportMSCANoDataFormat()  {
        assertThrows("Not a .data format",IllegalArgumentException.class,()->adc.importMSCA(dir+"test"));
    }

    @Test
    public void testImportMSCANoFile()  {
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
    public void testExportMSCAWithoutSuffix() throws IOException {
        Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, Label<Action>>> aut =
                adc.importMSCA(dir+"(AxB).data");

        adc.exportMSCA(dir+"(AxB)_export",aut);

        assertTrue(AutomatonTest.autEquals(aut,adc.importMSCA(dir+"(AxB)_export.data")));
    }


    @Test
    public void testExportMSCAWithSuffix() throws IOException {
        Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, Label<Action>>> aut =
                adc.importMSCA(dir+"(AxB).data");

        adc.exportMSCA(dir+"(AxB)_export.data",aut);

        assertTrue(AutomatonTest.autEquals(aut,adc.importMSCA(dir+"(AxB)_export.data")));
    }

    @Test
    public void testExportException() {
        assertThrows(IllegalArgumentException.class, ()->
        adc.exportMSCA("",null)); //without extension

    }
}