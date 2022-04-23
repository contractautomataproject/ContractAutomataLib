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
public class OfferActionTest {
	
	private OfferAction oa;
	@Mock
    RequestAction ra;

	@Before
	public void setUp() throws Exception {
		oa = new OfferAction("test");
		ra = mock(RequestAction.class);
		when(ra.getLabel()).thenReturn("test");
	}

	@After
	public void tearDown() throws Exception {
		oa = null;
	}

	@Test
	public void testHashCode() {
		assertEquals(oa.hashCode(), new OfferAction("test").hashCode());
	}
	
	@Test
	public void testHashCodeNotEquals() {
		assertNotEquals(oa.hashCode(), new OfferAction("different").hashCode());
	}

	@Test
	public void testToString() {
		assertEquals("!test",oa.toString());
	}

	@Test
	public void testMatch() {
		assertTrue(oa.match(ra));
	}
	
	@Test
	public void testNotMatchType() {
		assertFalse(oa.match(oa));
	}
	
	@Test
	public void testNotMatchLabel() {
		when(ra.getLabel()).thenReturn("different");
		assertFalse(oa.match(ra));
	}

	@Test
	public void testEqualsObject() {
		assertEquals(oa, new OfferAction("test"));
	}
	
	@Test
	public void testEqualsObjectSame() {
		assertEquals(oa, oa);
	}

	@Test
	public void testNotEqualsNull() {
		assertNotEquals(oa, null);
	}
	
	@Test
	public void testNotEqualsClass() {
		assertNotEquals(oa, "String");
	}
	
	@Test
	public void testNotEqualsLabel() {
		assertNotEquals(oa, new OfferAction("different"));
	}
	

}
