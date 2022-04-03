package io.github.contractautomata.catlib.family;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

public class FeatureTest {

	@Test
	public void testConstructorAction() {
		Feature f = new Feature("!a");
		assertNotEquals("a",f.getName());
	}
	

	@Test
	public void testEqualsSame() {
		Feature p = new Feature("cherry");
		assertEquals(p,p);
	}

	@Test
	public void testNotEqualsClass() {
		Feature p = new Feature("cherry");
		assertNotEquals(p,new Object());
	}

	@Test
	public void testEquals() {
		Feature p = new Feature("cherry");
		Feature pp = new Feature("cherry");
		assertEquals(p,pp);
	}

	@Test
	public void testNotEquals() {
		Feature p = new Feature("cherry");
		Feature pp = new Feature("ananas");
		assertNotEquals(p,pp);
	}


	@Test
	public void testNotEqualsNull() {
		Feature p = new Feature("cherry");
		assertNotEquals(p,null);
	}

	@Test
	public void testToString() {
		Feature p = new Feature("cherry");
		assertEquals("cherry",p.toString());
	}


	@Test
	public void testToStringNotEquals() {
		Feature p = new Feature("cherry");
		Feature pp = new Feature("ananas");
		assertNotEquals(p.toString(),pp.toString());
	}

	public void testHashCode() {
		Feature p = new Feature("cherry");
		Feature pp = new Feature("cherry");
		assertEquals(p.hashCode(),pp.hashCode());
	}


	@Test
	public void testHashCodeNotEquals() {
		Feature p = new Feature("cherry");
		Feature pp = new Feature("ananas");
		assertNotEquals(p.hashCode(),pp.hashCode());
	}


	//***EXCEPTIONS
	@Test
	public void testConstructorExceptionNullArgument() {
		assertThatThrownBy(() -> new Feature(null))
		.isInstanceOf(IllegalArgumentException.class);
	}

}
