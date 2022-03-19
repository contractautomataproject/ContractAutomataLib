package io.github.contractautomataproject.catlib.automaton.state;

import static org.junit.Assert.assertEquals;

import java.util.List;

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
	public void testGetRankEquals() {
		assertEquals(1,b1.getRank().intValue());
	}
	
	@Test
	public void testGetRankNotEquals() {
		Assert.assertNotEquals(2,b1.getRank().intValue());
	}
	
	@Test
	public void testToString() {
		assertEquals("label=3", b3.toString());
	}
	
	@Test
	public void testExceptionConstructorNull() {
		Assert.assertThrows(IllegalArgumentException.class, ()->new BasicState<String>(null,true,false));
	}
	
	@Test
	public void testExceptionConstructorListBasicState() {
		List<BasicState<String>> list = List.of(b1);
		Assert.assertThrows(UnsupportedOperationException.class, ()->new BasicState<List<BasicState<String>>>(list,true,false));
	}
	
	@Test
	public void testExceptionConstructorListString() {
		
		Assert.assertEquals(1,new BasicState<List<String>>(List.of("test","test"),true,false).getRank().intValue());
	}
}
