package io.github.contractautomata.catlib.automaton.label.action;

import static org.junit.Assert.*;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.Strict.class)
public class RequestActionTest {
	
	private RequestAction ra;
	@Mock
    OfferAction oa;

	@Before
	public void setUp() throws Exception {
		ra = new RequestAction("test");
		oa = mock(OfferAction.class);
		when(oa.getLabel()).thenReturn("test");
	}

	@After
	public void tearDown() throws Exception {
		ra = null;
	}

	@Test
	public void testHashCode() {
		assertEquals(ra.hashCode(), new RequestAction("test").hashCode());
	}
	
	@Test
	public void testHashCodeNotEquals() {
		assertNotEquals(ra.hashCode(), new RequestAction("different").hashCode());
	}

	@Test
	public void testToString() {
		assertEquals("?test",ra.toString());
	}

	@Test
	public void testMatch() {
		assertTrue(ra.match(oa));
	}
	
	@Test
	public void testNotMatchType() {
		assertFalse(ra.match(ra));
	}
	
	@Test
	public void testNotMatchLabel() {
		when(oa.getLabel()).thenReturn("different");
		assertFalse(ra.match(oa));
	}

	@Test
	public void testEqualsObject() {
		assertEquals(ra, new RequestAction("test"));
	}
	
	@Test
	public void testEqualsObjectSame() {
		assertEquals(ra, ra);
	}

	@Test
	public void testNotEqualsNull() {
		assertNotEquals(ra, null);
	}
	
	@Test
	public void testNotEqualsClass() {
		assertNotEquals(ra, "String");
	}
	
	@Test
	public void testNotEqualsLabel() {
		assertNotEquals(ra, new RequestAction("different"));
	}
	

}
