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
public class AddressedRequestActionTest {
	
	private AddressedRequestAction ara;
	private RequestAction noa;
	@Mock
    AddressedOfferAction aoa;
	@Mock
    Address adr;
	@Mock Address adr2;

	@Before
	public void setUp() throws Exception {
		ara = new AddressedRequestAction("test",adr);
		aoa = mock(AddressedOfferAction.class);
		when(aoa.getLabel()).thenReturn("test");
		when(aoa.getAddress()).thenReturn(adr);
		when(adr.match(adr)).thenReturn(true);
		when(adr.toString()).thenReturn("1_2@");
	}

	@After
	public void tearDown() throws Exception {
		ara = null;
	}

	@Test
	public void testHashCode() {
		assertEquals(ara.hashCode(), new AddressedRequestAction("test",adr).hashCode());
	}
	
	@Test
	public void testHashCodeNotEquals() {
		assertNotEquals(ara.hashCode(), new AddressedRequestAction("different",adr).hashCode());
	}

	@Test
	public void testToString() {
		assertEquals("1_2@?test",ara.toString());
	}

	@Test
	public void testMatch() {
		assertTrue(ara.match(aoa));
	}
	
	@Test
	public void testNotMatchNotAddressed() {
		assertFalse(ara.match(noa));
	}
	
	@Test
	public void testNotMatchSuperType() {
		assertFalse(ara.match(ara));
	}
	
	@Test
	public void testNotMatchSuper() {
		when(aoa.getLabel()).thenReturn("different");
		assertFalse(ara.match(aoa));
	}
	
	@Test
	public void testNotMatchAddress() {
		when(adr.match(adr)).thenReturn(false);
		assertFalse(ara.match(aoa));
	}


	@Test
	public void testEqualsObject() {
		assertEquals(ara, new AddressedRequestAction("test",adr));
	}
	
	@Test
	public void testEqualsObjectSame() {
		assertEquals(ara, ara);
	}

	@Test
	public void testNotEqualsNull() {
		assertNotEquals(ara, null);
	}
	
	@Test
	public void testNotEqualsClass() {
		assertNotEquals(ara, "String");
	}
	
	@Test
	public void testNotEqualsLabel() {
		assertNotEquals(ara, new AddressedRequestAction("different",adr));
	}
	
	@Test
	public void testNotEqualsAddress() {
		assertNotEquals(ara, new AddressedRequestAction("test",adr2));
	}
	

}
