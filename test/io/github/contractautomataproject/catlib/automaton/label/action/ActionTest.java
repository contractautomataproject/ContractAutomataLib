package io.github.contractautomataproject.catlib.automaton.label.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;


public class ActionTest {
	
	private Action act;

	@Before
	public void setUp() throws Exception {
		act = new Action("test");
	}

	@After
	public void tearDown() throws Exception {
		act = null;
	}

	@Test
	public void testHashCode() {
		assertEquals(act.hashCode(), new Action("test").hashCode());
	}
	
	@Test
	public void testHashCodeFalse() {
		assertNotEquals(act.hashCode(), new Action("different").hashCode());
	}

	@Test
	public void testToString() {
		assertEquals("test",act.toString());
	}

	@Test
	public void testGetLabel() {
		assertEquals("test",act.getLabel());
	}

	@Test
	public void testMatchTrue() {
		assertTrue(act.match(new Action("test")));
	}
	
	@Test
	public void testMatchFalse() {
		assertFalse(act.match(new Action("different")));
	}

	@Test
	public void testEqualsObject() {
		assertEquals(act, new Action("test"));
	}
	
	@Test
	public void testEqualsSame() {
		assertEquals(act, act);
	}
	
	@Test
	public void testNotEqualsNull() {
		assertNotEquals(act, null);
	}
	
	@Test
	public void testNotEqualsClass() {
		assertNotEquals(act, "test");
	}
	
	@Test
	public void testNotEqualsLabel() {
		assertNotEquals(act, new Action("different"));
	}

}
