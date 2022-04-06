package io.github.contractautomata.catlib.requirements;

import io.github.contractautomata.catlib.automaton.label.CALabel;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.Strict.class)
public class StrongAgreementTest {

    @Mock
    CALabel lab;

    @Test
    public void testTrue() {
        when(lab.isMatch()).thenReturn(true);
        assertTrue(new StrongAgreement().test(lab));
    }

    @Test
    public void testFalse() {
        when(lab.isMatch()).thenReturn(false);
        assertFalse(new StrongAgreement().test(lab));
    }
}