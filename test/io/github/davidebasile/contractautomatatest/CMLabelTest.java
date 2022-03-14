package io.github.davidebasile.contractautomatatest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.CMLabel;

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
		assertEquals("Alice",cm_of.getId());
	}
	
	@Test
	public void testConstructorPartnerOf(){
		assertEquals("Bob", cm_of.getPartner());
	}
	
	@Test
	public void testConstructorIdReq(){
		assertEquals("Bob", cm_req.getId());
	}
	

	@Test
	public void testConstructorPartnerReq(){
		assertEquals("Alice", cm_req.getPartner());
	}
	
	@Test
	public void testMatch() {
		assertTrue(cm_of.match(cm_req));
	}
	

	@Test
	public void equalsSame() {
		assertEquals(cm_of, cm_of);
	}
	
	@Test
	public void equalsFalse() {
		Assert.assertNotEquals(cm_of, cm_req);
	}
	

	@Test
	public void equalsTrue() {
		CMLabel equal = new CMLabel("Alice","Bob","!apple");
		CMLabel cm_of = new CMLabel("Alice","Bob","!apple");
		assertEquals(equal, cm_of);
	}
	
	@Test
	public void hashCodeTrue() {
		CMLabel equal = new CMLabel("Alice","Bob","!apple");
	    CMLabel	cm_of = new CMLabel("Alice","Bob","!apple");
		assertEquals(cm_of.hashCode(),equal.hashCode());
	}
	
	@Test
	public void testToStringOffer() {
		assertEquals("[Alice_Bob@!apple]", cm_of.toString());
	}
	
	@Test
	public void testToStringRequest() {
		assertEquals("[Alice_Bob@?apple]", cm_req.toString());
	}
	
	@Test
	public void testMatchException() {
		List<String> t = Arrays.asList("?a");
		CALabel test = new CALabel(t);
		assertThatThrownBy(()->cm_of.match(test))
		.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void testConstructorException() {
		assertThatThrownBy(()->new CMLabel("@?a"))
		.isInstanceOf(IllegalArgumentException.class);
	}
}
