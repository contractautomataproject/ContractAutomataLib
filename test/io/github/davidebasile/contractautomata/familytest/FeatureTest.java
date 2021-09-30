package io.github.davidebasile.contractautomata.familytest;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
/*
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
 */
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import io.github.davidebasile.contractautomata.family.Feature;

public class FeatureTest {

	@Test
	public void constructorTestAction() {
		Feature f = new Feature("!a");
		assertTrue(f.getName().equals("a"));
	}
	

	@Test
	public void testEquals1() {
		Feature p = new Feature("cherry");
		assertEquals(p.equals(p),true);
	}

	@Test
	public void testEquals2() {
		Feature p = new Feature("cherry");
		assertEquals(p.equals(null),false);
	}

	@Test
	public void testEquals3() {
		Feature p = new Feature("cherry");
		assertEquals(p.equals(new Object()),false);
	}

	@Test
	public void testEquals4 () {
		Feature p = new Feature("cherry");
		
		Feature pp = new Feature("cherry");
		assertEquals(p,pp);
	}

	@Test
	public void testEquals5() {
		Feature p = new Feature("cherry");
		
		Feature pp = new Feature("ananas");
		assertFalse(p.equals(pp));
	}

	//***EXCEPTIONS
	@Test
	public void constructorTest_Exception_nullArgument() {
		assertThatThrownBy(() -> new Feature(null))
		.isInstanceOf(IllegalArgumentException.class);
	}

}
