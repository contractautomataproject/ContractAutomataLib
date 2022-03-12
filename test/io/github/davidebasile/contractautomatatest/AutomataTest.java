package io.github.davidebasile.contractautomatatest;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.transition.Transition;

public class AutomataTest {

	@Test
	public void testString() {
		BasicState<String> s0 = new BasicState<String>("0",true,false);
		BasicState<String> s1 = new BasicState<String>("1",false,true);
		BasicState<String> s2 = new BasicState<String>("2",false,true);
		Transition<String,String,BasicState<String>,Label<String>> t1 = new Transition<>(s0, new Label<String>("m"), s1,BasicState::new);
		Transition<String,String,BasicState<String>,Label<String>> t2 = new Transition<>(s0, new Label<String>("m"), s2,BasicState::new);
		Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>> prop  = new Automaton<>(Set.of(t1,t2));
		String test = "Rank: 1"+System.lineSeparator() + 
				"Initial state: 0"+System.lineSeparator() + 
				"Final states: [[1, 2]]"+System.lineSeparator() + 
				"Transitions: "+System.lineSeparator() + 
				"(0,m,1)"+System.lineSeparator() + 
				"(0,m,2)"+System.lineSeparator();
		assertEquals(prop.toString(),test);
	}
}
