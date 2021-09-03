package contractAutomataTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

import contractAutomata.automaton.label.CALabel;
import contractAutomata.automaton.label.CMLabel;

public class CMLabelTest {
	CMLabel cm_of;
	CMLabel cm_req;
	
	@Before
	public void setup() {
		cm_of = new CMLabel("Alice","Bob","!apple");
		cm_req = new CMLabel("Alice","Bob","?apple");
	}
	
	@Test
	public void testOffer(){
		assertTrue(cm_of.isOffer());
	}
	

	@Test
	public void testRequest(){
		assertTrue(cm_req.isRequest());
	}
	
	
	@Test
	public void testConstructorIdOf(){
		assertTrue(cm_of.getId().equals("Alice"));
	}
	
	@Test
	public void testConstructorPartnerOf(){
		assertTrue(cm_of.getPartner().equals("Bob"));
	}
	
	@Test
	public void testConstructorIdReq(){
		assertTrue(cm_req.getId().equals("Bob"));
	}
	

	@Test
	public void testConstructorPartnerReq(){
		assertTrue(cm_req.getPartner().equals("Alice"));
	}
	
	@Test
	public void testMatch() {
		assertTrue(cm_of.match(cm_req));
	}
	

	@Test
	public void equalsSame() {
		assertTrue(cm_of.equals(cm_of));
	}
	
	@Test
	public void equalsFalse() {
		assertFalse(cm_of.equals(cm_req));
	}
	

	@Test
	public void equalsTrue() {
		CMLabel equal = 
				cm_of = new CMLabel("Alice","Bob","!apple");
		assertTrue(cm_of.equals(equal));
	}
	
	@Test
	public void hashCodeTrue() {
		CMLabel equal = 
				cm_of = new CMLabel("Alice","Bob","!apple");
		assertTrue(cm_of.hashCode()==equal.hashCode());
	}
	
	@Test
	public void testToStringOffer() {
		assertEquals(cm_of.toString(),"[Alice_Bob@!apple]");
	}
	
	@Test
	public void testToStringRequest() {
		assertEquals(cm_req.toString(),"[Alice_Bob@?apple]");
	}
	
	@Test
	public void testMatchException() {
		CALabel test = new CALabel(Arrays.asList("?a"));
		assertThatThrownBy(()->cm_of.match(test))
		.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructorException() {
		assertThatThrownBy(()->new CMLabel("@?a"))
		.isInstanceOf(IllegalArgumentException.class);
	}
}
