package io.github.contractautomataproject.catlib.automaton;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
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
	@Mock Transition<String,String,BasicState<String>,Label<String>> t3mock;
	@Mock Label<String> lab;

	Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>> prop;
//
//	@Mock BasicState<List<BasicState<String>>> s3mock;
//	@Mock BasicState<List<BasicState<String>>> s4mock;	
//	@Mock Label<List<Label<String>>> lab2;
//	@Mock Transition<List<BasicState<String>>,List<Label<String>>,BasicState<List<BasicState<String>>>,Label<List<String>>> t4mock;
//	
//
//	Automaton<List<String>,List<String>,BasicState<List<String>>,Transition<List<String>,List<String>,BasicState<List<String>>,Label<List<String>>>> prop2;
	
	
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

		when(lab.toString()).thenReturn("m");
		
		when(t1mock.getSource()).thenReturn(s0mock);
		when(t1mock.getTarget()).thenReturn(s1mock);
		when(t1mock.getLabel()).thenReturn(lab);
		when(t1mock.getRank()).thenReturn(1);
		
		when(t2mock.getSource()).thenReturn(s0mock);
		when(t2mock.getTarget()).thenReturn(s2mock);
		when(t2mock.getLabel()).thenReturn(lab);
		when(t2mock.getRank()).thenReturn(1);	
		

		when(t3mock.getSource()).thenReturn(s1mock);
		when(t3mock.getTarget()).thenReturn(s2mock);
		when(t3mock.getRank()).thenReturn(1);	
		

//		when(s3mock.isInitial()).thenReturn(true);
//		when(s3mock.isFinalstate()).thenReturn(false);
//		when(s3mock.getState()).thenReturn(List.of("0","0"));
//		
//		when(s4mock.isInitial()).thenReturn(false);
//		when(s4mock.isFinalstate()).thenReturn(true);
//		when(s4mock.getState()).thenReturn(List.of("1","1"));
//	
//		when(lab2.toString()).thenReturn(List.of("m","m").toString());
//
//		
//		when(t4mock.getSource()).thenReturn(s3mock);
//		when(t4mock.getTarget()).thenReturn(s4mock);
//		when(t4mock.getLabel()).thenReturn(lab2);
//		when(t4mock.getRank()).thenReturn(2);
		

		prop = new Automaton<>(Set.of(t1mock,t2mock));
//		prop2 = new Automaton<>(Set.of(t4mock));
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
	public void testStringRank2() {
		when(t1mock.getRank()).thenReturn(2);
		prop = new Automaton<>(Set.of(t1mock));
		String test = "Rank: 2"+System.lineSeparator() + 
				"Initial state: 0"+System.lineSeparator() + 
				"Final states: [[1][1]]"+System.lineSeparator() + 
				"Transitions: "+System.lineSeparator() + 
				"(0,m,1)"+System.lineSeparator();
		System.out.println(prop.toString());
		assertEquals(prop.toString(),test);
	}
	
	@Test
	public void testGetForwardStar() {
		prop = new Automaton<>(Set.of(t1mock,t2mock,t3mock));
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
		Assert.assertThrows("Null argument",
				NullPointerException.class,
				()->new Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>>(null));

	}

	@Test
	public void constructorTest_Exception_emptyTransitions() {
		Set<Transition<String,String, BasicState<String>,Label<String>>> s = new HashSet<>();
		Assert.assertThrows("No transitions",
				IllegalArgumentException.class ,
				() -> new Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>>(s));

	}

	@Test
	public void constructor_Exception_nullArgument() throws Exception {
		Set<Transition<String,String, BasicState<String>,Label<String>>> s = new HashSet<>();
		s.add(null);
		Assert.assertThrows("Null element", 
				IllegalArgumentException.class,
				() -> new Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>>(s));
	}

	@Test
	public void constructor_Exception_differentRank() throws Exception {
		when(t2mock.getRank()).thenReturn(2);
		Assert.assertThrows("Transitions with different rank", 
				IllegalArgumentException.class,
				() -> new Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>>(Set.of(t1mock,t2mock)));
	}

	@Test
	public void noInitialState_exception() throws Exception
	{
		when(s1mock.isInitial()).thenReturn(true);
		Assert.assertThrows("Not Exactly one Initial State found!", 
				IllegalArgumentException.class,
				() -> new Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>>(Set.of(t1mock,t2mock)));
	}

	@Test
	public void noFinalStatesInTransitions_exception() throws Exception
	{
		when(s1mock.isFinalstate()).thenReturn(false);
		when(s2mock.isFinalstate()).thenReturn(false);
		Assert.assertThrows("No Final States!", 
				IllegalArgumentException.class, 
				()->new Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>>(Set.of(t1mock,t2mock)));
	}

}
