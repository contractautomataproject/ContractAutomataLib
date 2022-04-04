package io.github.contractautomata.catlib.family;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Strict.class)
public class PartialProductGeneratorTest {

    @Mock Product p1;
    @Mock Product p2;
    @Mock Product p3;
    @Mock Feature f1;




    @Before
    public void setUp() throws Exception {
        when(p1.getForbidden()).thenReturn(Collections.singleton(f1));
        when(p1.getRequired()).thenReturn(Collections.emptySet());
        when(p2.getForbidden()).thenReturn(Collections.emptySet());
        when(p2.getRequired()).thenReturn(Collections.singleton(f1));

        when(p1.removeFeatures(Collections.singleton(f1))).thenReturn(p3);
        when(p2.removeFeatures(Collections.singleton(f1))).thenReturn(p3);
        when(p3.removeFeatures(any())).thenReturn(p3);
    }

    @Test
    public void apply() {
        PartialProductGenerator ppg = new PartialProductGenerator();
        assertEquals(Set.of(p1,p2,p3),ppg.apply(Set.of(p1,p2)));
    }
}