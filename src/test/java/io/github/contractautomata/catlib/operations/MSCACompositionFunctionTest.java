package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertThrows;

public class MSCACompositionFunctionTest {

    @Test
    public void constructorException() throws Exception {
        List<Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, CALabel>>> aut = Collections.emptyList();
        assertThrows(IllegalArgumentException.class, ()-> new MSCACompositionFunction<>(aut,null));
    }
}