package io.github.contractautomata.catlib.automaton.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class ITStateTest {
	private  State<String> test;
	
	@Before
	public void setup() {		
		BasicState<String> bs0 = new BasicState<>("0",true,false);
		BasicState<String> bs1 = new BasicState<>("1",true,false);
		BasicState<String> bs2 = new BasicState<>("2",true,false);
		
		test = new State<>(List.of(bs0,bs1,bs2));
	}

	@Test
	public void testIsInitial() {
		assertTrue(test.isInitial());
	}
	@Test
	public void testIsFinal() {
		assertFalse(test.isFinalState());
	}
	
	
	@Test
	public void testToString() {
		assertEquals("[0, 1, 2]", test.toString());
	}
	
}