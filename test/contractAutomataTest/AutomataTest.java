package contractAutomataTest;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Test;

import contractAutomata.automaton.Automaton;
import contractAutomata.automaton.label.Label;
import contractAutomata.automaton.state.BasicState;
import contractAutomata.automaton.transition.Transition;

public class AutomataTest {

	@Test
	public void testString() {
		BasicState s0 = new BasicState("0",true,false);
		BasicState s1 = new BasicState("1",false,true);
		BasicState s2 = new BasicState("2",false,true);
		Transition<String, BasicState,Label> t1 = new Transition<>(s0, new Label("m"), s1);
		Transition<String, BasicState,Label> t2 = new Transition<>(s0, new Label("m"), s2);
		Automaton<String, BasicState,Transition<String, BasicState,Label>> prop  = new Automaton<>(Set.of(t1,t2));
		String test = "Rank: 1"+System.lineSeparator() + 
				"Initial state: 0"+System.lineSeparator() + 
				"Final states: [[1, 2]]"+System.lineSeparator() + 
				"Transitions: "+System.lineSeparator() + 
				"(0,m,1)"+System.lineSeparator() + 
				"(0,m,2)"+System.lineSeparator();
		assertEquals(prop.toString(),test);
	}
}
