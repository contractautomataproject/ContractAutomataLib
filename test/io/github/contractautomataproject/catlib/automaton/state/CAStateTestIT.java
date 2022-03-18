package io.github.contractautomataproject.catlib.automaton.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;


public class CAStateTestIT {
	private  CAState<String> test;
	
	@Before
	public void setup() {		
		BasicState<String> bs0 = new BasicState<String>("0",true,false);
		BasicState<String> bs1 = new BasicState<String>("1",true,false);
		BasicState<String> bs2 = new BasicState<String>("2",true,false);
		
		List<CAState<String>> l = new ArrayList<>();
		l.add(new CAState<>(Arrays.asList(bs0,bs1))); 
		l.add(new CAState<>(Arrays.asList(bs2)));
		
		test = CAState.createStateByFlattening(l);
	}

	@Test
	public void testIsInitial() {
		assertTrue(test.isInitial());
	}
	@Test
	public void testIsFinal() {
		assertFalse(test.isFinalstate());
	}
	
	
	@Test
	public void testToString() {
		assertEquals("[label=0,initial=true, label=1,initial=true, label=2,initial=true]", test.toString());
	}
	
}