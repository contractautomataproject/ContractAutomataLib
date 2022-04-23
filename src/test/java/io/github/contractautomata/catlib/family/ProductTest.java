package io.github.contractautomata.catlib.family;


import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.Strict.class)
public class ProductTest {

	Product p;
	Product ps;

	@Mock Feature f1;
	@Mock Feature f2;
	@Mock Feature f3;

	@Mock Feature f4;
	@Mock Feature f5;

	@Mock ModalTransition<String, Action, State<String>, CALabel> tr;
	@Mock ModalTransition<String, Action, State<String>, CALabel> tr2;
	@Mock ModalTransition<String, Action, State<String>, CALabel> tr3;

	@Mock CALabel lab;
	@Mock CALabel lab2;

	@Mock Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut;

	Set<Feature> required;
	Set<Feature> forbidden;


	@Before
	public void setUp(){
		f1 = mock(Feature.class);
		f2 = mock(Feature.class);
		f3 = mock(Feature.class);

		when(f1.getName()).thenReturn("f1");
		when(f2.getName()).thenReturn("f2");
		when(f3.getName()).thenReturn("f3");

		required = new HashSet<Feature>(Arrays.asList(f1));
		forbidden = new HashSet<Feature>(Arrays.asList(f2,f3));

		p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		ps = new Product(required,forbidden);

		Action act = mock(Action.class);

		when(tr.getLabel()).thenReturn(lab);
		when(lab.getAction()).thenReturn(act);
		when(act.getLabel()).thenReturn("f1");

		Action act2 = mock(Action.class);

		when(tr2.getLabel()).thenReturn(lab2);
		when(lab2.getAction()).thenReturn(act2);
		when(act2.getLabel()).thenReturn("f2");

		CALabel lab3 = mock(CALabel.class);
		Action act3 = mock(Action.class);

		when(tr3.getLabel()).thenReturn(lab3);
		when(lab3.getAction()).thenReturn(act3);
		when(act3.getLabel()).thenReturn("f3");

	}

	@Test
	public void testGetRequired(){
		assertEquals(required,ps.getRequired());
	}

	@Test
	public void testGetForbidden(){
		assertEquals(forbidden,ps.getForbidden());
	}

	@Test
	public void testGetRequiredAndForbiddenNumber() {
		assertEquals(3,ps.getForbiddenAndRequiredNumber());
	}

	@Test
	public void testRemoveFeaturesRequired() {
		Set<Feature> rem = new HashSet<>(Arrays.asList(f1,f2));
		assertEquals(Collections.emptySet(), ps.removeFeatures(rem).getRequired());
	}


	@Test
	public void testRemoveFeaturesForbidden() {
		Set<Feature> rem = new HashSet<>(Arrays.asList(f2));
		assertEquals(new HashSet<>(Arrays.asList(f3)), ps.removeFeatures(rem).getForbidden());
	}

	@Test
	public void testEqualsSame() {
		assertEquals(p,p);
	}

	@Test
	public void testNotEqualsClass() {
		assertNotEquals(p,new Object());
	}

	@Test
	public void testEquals () {
		Product pp = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		assertEquals(p,pp);
	}

	@Test
	public void testNotEqualsRequired() {
		Product pp = new Product(new String[] {"cherry","apple"}, new String[] {"blueberry"});
		assertNotEquals(p,pp);
	}

	@Test
	public void testNotEqualsForbidden() {
		Product pp = new Product(new String[] {"cherry","ananas"}, new String[] {"lemon"});
		assertNotEquals(p,pp);
	}

	@Test
	public void testNotEqualsNull() {
		assertNotEquals(p,null);
	}

	@Test
	public void testHashCode() {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		Product pp = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		assertEquals(p.hashCode(),pp.hashCode());
	}

	@Test
	public void testHashCodeNotEquals() {
		Product pp = new Product(new String[] {"cherry","apple"}, new String[] {"blueberry"});
		assertNotEquals(p.hashCode(),pp.hashCode());
	}

	@Test
	public void testToString() {
		assertEquals(p.toString(),"R:[cherry, ananas];" + System.lineSeparator()+
				"F:[blueberry];"+System.lineSeparator());
	}

	@Test
	public void testCheckRequiredTrue()
	{
		assertTrue(ps.checkRequired(new HashSet<>(Arrays.asList(tr))));
	}


	@Test
	public void testCheckRequiredFalse()
	{
		assertFalse(ps.checkRequired(new HashSet<>(Arrays.asList(tr2,tr3))));
	}


	@Test
	public void testCheckForbiddenTrue()
	{
		assertTrue(ps.checkForbidden(new HashSet<>(Arrays.asList(tr))));
	}


	@Test
	public void testCheckForbiddenFalse()
	{
		assertFalse(ps.checkForbidden(new HashSet<>(Arrays.asList(tr,tr2))));
	}


	@Test
	public void testIsForbiddenTrue() {
		assertTrue(ps.isForbidden(lab2));
	}


	@Test
	public void testIsForbiddenFalse() {
		assertFalse(ps.isForbidden(lab));
	}

	@Test
	public void testIsValidTrue(){
		when(aut.getTransition()).thenReturn(new HashSet<>(Arrays.asList(tr)));
		assertTrue(ps.isValid(aut));
	}


	@Test
	public void testIsValidFalseContainsForbidden(){
		when(aut.getTransition()).thenReturn(new HashSet<>(Arrays.asList(tr,tr2)));
		assertFalse(ps.isValid(aut));
	}


	@Test
	public void testIsValidFalseDoesNotContainRequired(){
		when(aut.getTransition()).thenReturn(new HashSet<>(Arrays.asList()));
		assertFalse(ps.isValid(aut));
	}

	// exceptions

	@Test
	public void testConstructFeaturesExceptionRequiredContainedInForbidden() {
		required.add(f2);
		assertThrows("A feature is both required and forbidden", IllegalArgumentException.class,() -> new Product(required, forbidden));
	}


	@Test
	public void testConstructFeaturesExceptionForbiddenContainedInRequired() {
		forbidden.add(f1);
		assertThrows("A feature is both required and forbidden", IllegalArgumentException.class,() -> new Product(required, forbidden));
	}


	@Test
	public void testConstructorExceptionNullArgument1() {
		String[] arg = new String[0];
		assertThrows(NullPointerException.class, () -> new Product(null,arg));
	}

	@Test
	public void testConstructorExceptionNullArgument2() {
		Set<Feature> arg = new HashSet<>();
		assertThrows(NullPointerException.class, () -> new Product(null,arg));
	}

}
