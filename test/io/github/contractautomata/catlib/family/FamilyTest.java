package io.github.contractautomata.catlib.family;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.Strict.class)
public class FamilyTest {

    @Mock Feature f1;
    @Mock Feature f2;
    @Mock Feature f3;
    @Mock Product p1;
    @Mock Product p2;
    @Mock Product p3;
    @Mock Product p4;
    Set<Product> set;

    Family family;

    @Before
    public void setUp() throws Exception {

        f1 = mock(Feature.class);
        f2 = mock(Feature.class);
        f3 = mock(Feature.class);

        when(p1.getRequired()).thenReturn(Collections.singleton(f1));
        when(p1.getForbidden()).thenReturn(new HashSet<>(Arrays.asList(f2, f3)));
        when(p1.getForbiddenAndRequiredNumber()).thenReturn(3);

        when(p2.getRequired()).thenReturn(Collections.singleton(f1));
        when(p2.getForbidden()).thenReturn(Collections.singleton(f3));
        when(p2.getForbiddenAndRequiredNumber()).thenReturn(2);

        when(p3.getRequired()).thenReturn(Collections.singleton(f1));
        when(p3.getForbidden()).thenReturn(Collections.singleton(f2));
        when(p3.getForbiddenAndRequiredNumber()).thenReturn(2);

        when(p4.getRequired()).thenReturn(Collections.singleton(f3));
        when(p4.getForbidden()).thenReturn(Collections.emptySet());
        when(p4.getForbiddenAndRequiredNumber()).thenReturn(1);

        set = new HashSet<>(Arrays.asList(p1, p2, p3, p4));
        family = new Family(set);

    }

    @Test
    public void testGetProducts() {
        assertEquals(set,family.getProducts());
    }

    @Test
    public void testGetPo() {
        Map<Product, Map<Boolean,Set<Product>>> po =
        Map.of(p1, Map.of(false, Collections.emptySet(),true,Set.of(p2,p3)),
                p2, Map.of(false, Set.of(p1),true, Collections.emptySet()),
                    p3, Map.of(false, Set.of(p1),true, Collections.emptySet()),
                      p4, Map.of(false, Collections.emptySet(),true, Collections.emptySet()));

        assertEquals(po, family.getPo());
    }

    @Test
    public void testGetMaximumDepth() {
        assertEquals(4,family.getMaximumDepth());
    }

    @Test
    public void testGetSubProductsOfProductP1() {
        assertEquals(Collections.emptySet(),family.getSubProductsOfProduct(p1));
    }

    @Test
    public void testGetSubProductsOfProductP2() {
        assertEquals(Collections.singleton(p1),family.getSubProductsOfProduct(p2));
    }

    @Test
    public void testGetSuperProductsOfProductP1() {
        assertEquals(Set.of(p2,p3),family.getSuperProductsOfProduct(p1));
    }


    @Test
    public void testGetSuperProductsOfProductP2() {
        assertEquals(Collections.emptySet(),family.getSuperProductsOfProduct(p2));
    }

    @Test
    public void testGetMaximalProducts() {
        assertEquals(Set.of(p2,p3,p4),family.getMaximalProducts());
    }

    @Test
    public void testGetSuperProductsTransitive() {
        when(p1.getRequired()).thenReturn(Collections.emptySet());
        when(p1.getForbidden()).thenReturn(Set.of(f1,f2,f3));
        when(p1.getForbiddenAndRequiredNumber()).thenReturn(3);

        when(p2.getRequired()).thenReturn(Collections.emptySet());
        when(p2.getForbidden()).thenReturn(Set.of(f2,f3));
        when(p2.getForbiddenAndRequiredNumber()).thenReturn(2);

        when(p4.getRequired()).thenReturn(Collections.emptySet());
        when(p4.getForbidden()).thenReturn(Collections.singleton(f3));
        when(p4.getForbiddenAndRequiredNumber()).thenReturn(1);

        set = new HashSet<>(Arrays.asList(p1, p2, p4));
        family = new Family(set);

        assertEquals(Set.of(p1,p2),family.getSubProductsOfProduct(p4));
    }

    @Test
    public void testGetSubProductsNotClosedTransitively(){

        when(p1.getRequired()).thenReturn(Collections.emptySet());
        when(p1.getForbidden()).thenReturn(Set.of(f1,f2,f3));
        when(p1.getForbiddenAndRequiredNumber()).thenReturn(3);

        when(p2.getRequired()).thenReturn(Collections.emptySet());
        when(p2.getForbidden()).thenReturn(Set.of(f2,f3));
        when(p2.getForbiddenAndRequiredNumber()).thenReturn(2);

        when(p3.getRequired()).thenReturn(Collections.emptySet());
        when(p3.getForbidden()).thenReturn(Set.of(f1,f3));
        when(p3.getForbiddenAndRequiredNumber()).thenReturn(2);

        when(p4.getRequired()).thenReturn(Collections.emptySet());
        when(p4.getForbidden()).thenReturn(Collections.singleton(f3));
        when(p4.getForbiddenAndRequiredNumber()).thenReturn(1);

        set = new HashSet<>(Arrays.asList(p1, p2, p3, p4));
        family = new Family(set);

        assertEquals(Set.of(p2,p3),family.getSubProductsNotClosedTransitively(p4));
    }

    @Test
    public void testGetMaximalProductsHighDifference(){
        when(p1.getRequired()).thenReturn(Collections.emptySet());
        when(p1.getForbidden()).thenReturn(Set.of(f1,f2,f3));
        when(p1.getForbiddenAndRequiredNumber()).thenReturn(3);

        when(p4.getRequired()).thenReturn(Collections.emptySet());
        when(p4.getForbidden()).thenReturn(Collections.singleton(f3));
        when(p4.getForbiddenAndRequiredNumber()).thenReturn(1);

        set = new HashSet<>(Arrays.asList(p1, p4));
        family = new Family(set);

        assertEquals(Set.of(p4),family.getMaximalProducts());
    }

    @Test
    public void testHashCode() {
        assertEquals(family.hashCode(),new Family(set).hashCode());
    }


    @Test
    public void testHashCodeNotEquals() {
        assertNotEquals(family.hashCode(),new Family(Set.of(p1)).hashCode());
    }

    @Test
    public void testEquals() {
        assertEquals(family,new Family(set));
    }

    @Test
    public void testEqualsSame() {
        assertEquals(family,family);
    }

    @Test
    public void testNotEqualsNull() {
        assertNotEquals(family,null);
    }

    @Test
    public void testNotEqualsClass() {
        assertNotEquals(family,new Object());
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(family,new Family(Set.of(p1)));
    }

    @Test
    public void testToString() {
        assertEquals("Family [products=[p1]]",new Family(Set.of(p1)).toString());
    }

    @Test
    public void testConstructorException()
    {
        assertThatThrownBy(() -> new Family(null))
                .isInstanceOf(NullPointerException.class);
    }

    @Test
    public void testConstructorException2()
    {
        assertThatThrownBy(() -> new Family(null,null,null))
                .isInstanceOf(NullPointerException.class);
    }

}