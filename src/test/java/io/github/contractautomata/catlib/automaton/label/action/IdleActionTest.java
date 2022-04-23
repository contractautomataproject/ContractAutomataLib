package io.github.contractautomata.catlib.automaton.label.action;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IdleActionTest {
	
	private IdleAction ia;

	@Before
	public void setUp() throws Exception {
		ia = new IdleAction();
	}

	@After
	public void tearDown() throws Exception {
		ia = null;
	}

	@Test
	public void test() {
		assertEquals("-",ia.getLabel());
	}

}
