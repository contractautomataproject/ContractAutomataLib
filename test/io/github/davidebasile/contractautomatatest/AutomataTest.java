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
		BasicState s0 = new BasicState("0",true,false);
		BasicState s1 = new BasicState("1",false,true);
		BasicState s2 = new BasicState("2",false,true);
		Transition<String,String,BasicState,Label<String>> t1 = new Transition<>(s0, new Label<String>("m"), s1);
		Transition<String,String,BasicState,Label<String>> t2 = new Transition<>(s0, new Label<String>("m"), s2);
		Automaton<String,String, BasicState,Transition<String,String, BasicState,Label<String>>> prop  = new Automaton<>(Set.of(t1,t2));
		String test = "Rank: 1"+System.lineSeparator() + 
				"Initial state: 0"+System.lineSeparator() + 
				"Final states: [[1, 2]]"+System.lineSeparator() + 
				"Transitions: "+System.lineSeparator() + 
				"(0,m,1)"+System.lineSeparator() + 
				"(0,m,2)"+System.lineSeparator();
		assertEquals(prop.toString(),test);
	}
}
