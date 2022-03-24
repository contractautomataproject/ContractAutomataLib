package io.github.contractautomataproject.catlib.automaton.label.action;

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
public class AddressedOfferActionTest {
	
	private AddressedOfferAction oa;
	private OfferAction noa;
	@Mock AddressedRequestAction ra;
	@Mock Address adr;
	@Mock Address adr2;

	@Before
	public void setUp() throws Exception {
		oa = new AddressedOfferAction("test",adr);
		ra = mock(AddressedRequestAction.class);
		when(ra.getLabel()).thenReturn("test");
		when(ra.getAddress()).thenReturn(adr);
		when(adr.match(adr)).thenReturn(true);
		when(adr.toString()).thenReturn("1_2@");
	}

	@After
	public void tearDown() throws Exception {
		oa = null;
	}

	@Test
	public void testHashCode() {
		assertEquals(oa.hashCode(), new AddressedOfferAction("test",adr).hashCode());
	}
	
	@Test
	public void testHashCodeNotEquals() {
		assertNotEquals(oa.hashCode(), new AddressedOfferAction("different",adr).hashCode());
	}

	@Test
	public void testToString() {
		assertEquals("1_2@!test",oa.toString());
	}

	@Test
	public void testMatch() {
		assertTrue(oa.match(ra));
	}
	
	@Test
	public void testNotMatchNotAddressed() {
		assertFalse(oa.match(noa));
	}
	
	@Test
	public void testNotMatchSuperType() {
		assertFalse(oa.match(oa));
	}
	
	@Test
	public void testNotMatchSuper() {
		when(ra.getLabel()).thenReturn("different");
		assertFalse(oa.match(ra));
	}
	
	@Test
	public void testNotMatchAddress() {
		when(adr.match(adr)).thenReturn(false);
		assertFalse(oa.match(ra));
	}


	@Test
	public void testEqualsObject() {
		assertEquals(oa, new AddressedOfferAction("test",adr));
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
		assertNotEquals(oa, new AddressedOfferAction("different",adr));
	}
	
	@Test
	public void testNotEqualsAddress() {
		assertNotEquals(oa, new AddressedOfferAction("test",adr2));
	}
	

}
