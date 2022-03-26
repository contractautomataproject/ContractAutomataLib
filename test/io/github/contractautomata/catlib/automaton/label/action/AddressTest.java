package io.github.contractautomata.catlib.automaton.label.action;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class AddressTest {
	
	private Address adr;

	@Before
	public void setUp() throws Exception {
		adr = new Address("1","2");
	}

	@After
	public void tearDown() throws Exception {
		adr = null;
	}

	@Test
	public void testHashCode() {
		assertEquals(adr.hashCode(), new Address("1","2").hashCode());
	}
	
	@Test
	public void testHashCodeNotEquals() {
		assertNotEquals(adr.hashCode(), new Address("2","2").hashCode());
	}

	@Test
	public void testEqualsObject() {
		assertEquals(adr, new Address("1","2"));
	}
	
	@Test
	public void testEqualsSameObject() {
		assertEquals(adr, adr);
	}
	
	@Test
	public void testNotEqualsNull() {
		assertNotEquals(adr, null);
	}
	
	@Test
	public void testNotEqualsClass() {
		assertNotEquals(adr, "test");
	}
	
	@Test
	public void testNotEqualsSender() {
		assertNotEquals(adr, new Address("2","2"));
	}
	
	@Test
	public void testNotEqualsReceiver() {
		assertNotEquals(adr, new Address("1","1"));
	}

	@Test
	public void testToString() {
		assertEquals("1_2@",adr.toString());
	}

	@Test
	public void testMatch() {
		assertTrue(adr.match(new Address("1","2")));
	}
	
	@Test
	public void testMatchFalseSender() {
		assertFalse(adr.match(new Address("2","2")));
	}
	
	@Test
	public void testMatchFalseReceiver() {
		assertFalse(adr.match(new Address("1","1")));
	}

	@Test
	public void testConstructorExceptionSenderEmpty() {
		assertThrows(IllegalArgumentException.class, ()->new Address("","2"));
	}
	
	@Test
	public void testConstructorExceptionReceiverEmpty() {
		assertThrows(IllegalArgumentException.class, ()->new Address("1",""));
	}
}
