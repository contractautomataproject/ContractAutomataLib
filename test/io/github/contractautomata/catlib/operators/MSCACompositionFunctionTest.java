package io.github.contractautomata.catlib.operators;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertThrows;

public class MSCACompositionFunctionTest {

    @Test
    public void constructorException() throws Exception {
        assertThrows(IllegalArgumentException.class, ()-> new MSCACompositionFunction<>(Collections.emptyList(),null));
    }
}