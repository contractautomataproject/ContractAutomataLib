package io.github.contractautomata.catlib.automaton.label.action;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.Strict.class)
public class TauActionTest {
	
	private TauAction ta;

	@Mock
	Action act;

	@Before
	public void setUp() throws Exception {
		ta = new TauAction("test");
	}

	@After
	public void tearDown() throws Exception {
		ta = null;
	}

	@Test
	public void testToString() {
		assertEquals("tau_test",ta.toString());
	}

	@Test
	public void testMatch() {
		assertFalse(ta.match(act));
	}

	@Test
	public void testHashCode() {
		assertEquals(ta.hashCode(), new TauAction("test").hashCode());
	}

	@Test
	public void testHashCodeNotEquals() {
		assertNotEquals(ta.hashCode(), new TauAction("different").hashCode());
	}


	@Test
	public void testEqualsObject() {
		assertEquals(ta, new TauAction("test"));
	}

	@Test
	public void testEqualsObjectSame() {
		assertEquals(ta, ta);
	}

	@Test
	public void testNotEqualsNull() {
		assertNotEquals(ta, null);
	}

	@Test
	public void testNotEqualsClass() {
		assertNotEquals(ta, "String");
	}

	@Test
	public void testNotEqualsLabel() {
		assertNotEquals(ta, new TauAction("different"));
	}

}
