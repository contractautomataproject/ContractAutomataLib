package io.github.contractautomataproject.catlib.automaton.state;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class BasicStateTest {

	@Test
	public void readCSVtest() {
		BasicState<String> b = new BasicState<String>("0",true,false);
		assertEquals(BasicState.readCSV(b.toCSV()).toString(), b.toString());
	}
	
}
