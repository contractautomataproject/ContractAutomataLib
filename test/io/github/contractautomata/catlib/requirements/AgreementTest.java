package io.github.contractautomata.catlib.requirements;

import io.github.contractautomata.catlib.automaton.label.CALabel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Strict.class)
public class AgreementTest {

    @Mock CALabel lab;

    @Test
    public void testTrue() {
        when(lab.isRequest()).thenReturn(false);
        assertTrue(new Agreement().test(lab));
    }

    @Test
    public void testFalse() {
        when(lab.isRequest()).thenReturn(true);
        assertFalse(new Agreement().test(lab));
    }
}