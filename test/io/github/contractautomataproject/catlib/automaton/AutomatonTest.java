package io.github.contractautomataproject.catlib.automaton;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.transition.Transition;

@RunWith(MockitoJUnitRunner.class)
public class AutomatonTest {
	
	@Mock BasicState<String> s0mock;
	@Mock BasicState<String> s1mock;
	@Mock BasicState<String> s2mock;
	@Mock Transition<String,String,BasicState<String>,Label<String>> t1mock;
	@Mock Transition<String,String,BasicState<String>,Label<String>> t2mock;
	
	Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>> prop;
	
	@Before
	public void setup() {
		when(s0mock.isInitial()).thenReturn(true);
		when(s0mock.isFinalstate()).thenReturn(false);
		when(s0mock.getState()).thenReturn("0");
		
		when(s1mock.isInitial()).thenReturn(false);
		when(s1mock.isFinalstate()).thenReturn(true);
		when(s1mock.getState()).thenReturn("1");
		
		when(s2mock.isInitial()).thenReturn(false);
		when(s2mock.isFinalstate()).thenReturn(true);
		when(s2mock.getState()).thenReturn("2");

		when(t1mock.getSource()).thenReturn(s0mock);
		when(t1mock.getTarget()).thenReturn(s1mock);
		when(t1mock.toString()).thenReturn("(0,m,1)");
		when(t1mock.getRank()).thenReturn(1);
		
		when(t2mock.getSource()).thenReturn(s0mock);
		when(t2mock.getTarget()).thenReturn(s2mock);
		when(t2mock.toString()).thenReturn("(0,m,2)");
		when(t2mock.getRank()).thenReturn(1);	

		prop = new Automaton<>(Set.of(t1mock,t2mock));
	}

	@Test
	public void testString() {
		String test = "Rank: 1"+System.lineSeparator() + 
				"Initial state: 0"+System.lineSeparator() + 
				"Final states: [[1, 2]]"+System.lineSeparator() + 
				"Transitions: "+System.lineSeparator() + 
				"(0,m,1)"+System.lineSeparator() + 
				"(0,m,2)"+System.lineSeparator();
		assertEquals(prop.toString(),test);
	}
	
	@Test
	public void testGetForwardStar() {
		assertEquals(Set.of(t1mock,t2mock), prop.getForwardStar(s0mock));
	}
	
	@Test
	public void testGetTransitions() {
		assertEquals(Set.of(t1mock,t2mock), prop.getTransition());
	}
	
	@Test
	public void testGetNumStates() {
		assertEquals(3,prop.getNumStates());
	}
	
	//************************************exceptions*********************************************

	@Test
	public void constructorTest_Exception_nullArgument() {
		assertThatThrownBy(() -> new Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>>(null))
		.isInstanceOf(NullPointerException.class);
	}

	@Test
	public void constructorTest_Exception_emptyTransitions() {
		Set<Transition<String,String, BasicState<String>,Label<String>>> s = new HashSet<>();
		assertThatThrownBy(() -> new Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>>(s))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("No transitions");

	}

	@Test
	public void constructor_Exception_nullArgument() throws Exception {
		Set<Transition<String,String, BasicState<String>,Label<String>>> s = new HashSet<>();
		s.add(null);
		assertThatThrownBy(() -> new Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>>(s))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Null element");
	}

	@Test
	public void constructor_Exception_differentRank() throws Exception {
		when(t2mock.getRank()).thenReturn(2);
		assertThatThrownBy(() -> new Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>>(Set.of(t1mock,t2mock)))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Transitions with different rank");
	}

	@Test
	public void noInitialState_exception() throws Exception
	{
		when(s1mock.isInitial()).thenReturn(true);
		assertThatThrownBy(() -> new Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>>(Set.of(t1mock,t2mock)))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Not Exactly one Initial State found!");
	}

	@Test
	public void noFinalStatesInTransitions_exception() throws Exception
	{
		when(s1mock.isFinalstate()).thenReturn(false);
		when(s2mock.isFinalstate()).thenReturn(false);
		assertThatThrownBy(() -> new Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>>(Set.of(t1mock,t2mock)))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("No Final States!");
	}

}
