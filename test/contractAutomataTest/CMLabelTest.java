package contractAutomataTest;

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
		cm_of = new CMLabel("Alice_Bob@!apple");
		cm_req = new CMLabel("Alice_Bob@?apple");
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
	public void testMatchException() {
		CALabel test = new CALabel(Arrays.asList("?a"));
		assertThatThrownBy(()->cm_of.match(test))
		.isInstanceOf(IllegalArgumentException.class);
	}
}
