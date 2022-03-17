package io.github.contractautomataproject.catlib.automaton.state;

import static org.junit.Assert.assertEquals;

import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

public class BasicStateTest {
	BasicState<String> b0;
	BasicState<String> b1;
	BasicState<String> b2; 
	BasicState<String> b3; 
	
	@Before
	public void setup() {
		b0 = new BasicState<String>("0",true,false);
		b1 = new BasicState<String>("1",false,true);
		b2 = new BasicState<String>("2",true,true);
		b3 = new BasicState<String>("3",false,false);
	}
	
	@Test
	public void testToStringInitial() {
		assertEquals("label=0,initial=true", b0.toString());
	}
	
	@Test
	public void testToStringFinal() {
		assertEquals("label=1,final=true", b1.toString());
	}
	
	@Test
	public void testToStringInitialFinal() {
		assertEquals("label=2,final=true,initial=true", b2.toString());
	}
	
	@Test
	public void testToString() {
		assertEquals("label=3", b3.toString());
	}

	@Test
	public void testReadCSVInitial() {
		assertEquals(BasicState.readCSV(b0.toString()).toString(), b0.toString());
	}
	
	@Test
	public void testReadCSVFinal() {
		assertEquals(BasicState.readCSV(b1.toString()).toString(), b1.toString());
	}
	
	@Test
	public void testReadCSVInitialFinal() {
		assertEquals(BasicState.readCSV(b2.toString()).toString(), b2.toString());
	}
	
	@Test
	public void testReadCSV() {
		assertEquals(BasicState.readCSV(b3.toString()).toString(), b3.toString());
	}
	
	@Test
	public void testExceptionConstructor() {
		Assert.assertThrows(IllegalArgumentException.class, ()->new BasicState<String>(null,true,false));
	}
}
