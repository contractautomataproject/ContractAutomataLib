package io.github.contractautomataproject.catlib.automaton.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class StateTest {
	private static State<String> test;

	@Mock BasicState<String> bs0; 
	@Mock BasicState<String> bs1;
	@Mock BasicState<String> bs2;
	@Mock BasicState<String> bs4;

	
	@Before
	public void setup() {		
		when(bs0.isInitial()).thenReturn(true);
		when(bs0.getState()).thenReturn("0");
		when(bs1.isInitial()).thenReturn(true);
		when(bs1.getState()).thenReturn("1");
		when(bs2.getState()).thenReturn("2");

		test = new State<>(List.of(bs0,bs1,bs2,bs2,bs0,bs1));
	}
	
	@Test
	public void testIsInitialTrue() {
		when(bs2.isInitial()).thenReturn(true);
		assertTrue(test.isInitial());
	}
	
	@Test
	public void testIsInitialFalse() {
		assertFalse(test.isInitial());
	}
	

	@Test
	public void testIsFinalStateTrue() {
		when(bs0.isFinalState()).thenReturn(true);
		when(bs1.isFinalState()).thenReturn(true);
		when(bs2.isFinalState()).thenReturn(true);

		assertTrue(test.isFinalState());
	}
	
	
	@Test
	public void testIsFinalStateFalse() {
		assertFalse(test.isFinalState());
	}
	
	@Test
	public void testGetRank() {
		assertEquals(6,test.getRank().intValue());
	}
	
	@Test
	public void testGetState() {
		assertEquals(List.of(bs0,bs1,bs2,bs2,bs0,bs1),test.getState());
	}
	
	@Test
	public void testGetStateNewList() {
		List<BasicState<String>> list = List.of(bs0,bs1);
		Assert.assertNotSame(list,new State<>(list).getState());
	}

	@Test
	public void toStringFinalTest() {
		when(bs4.getState()).thenReturn("5");
		List<BasicState<String>> l = List.of(bs4,bs4);
		
		State<String> test2=new State<>(l);
		
		assertEquals("[5, 5]", test2.toString());
	}
	
	

	@Test
	public void testToString() {
		assertEquals("[0, 1, 2, 2, 0, 1]",test.toString());
	}

	//********************** testing exceptions *********************
	
	@Test
	public void testConstructorException_empty() {
		List<BasicState<String>> list = List.of();
		Assert.assertThrows(IllegalArgumentException.class, () -> new State<>(list));
	}
	
	@Test
	public void testConstructorException_null() {
		Assert.assertThrows(IllegalArgumentException.class, () -> new State<>(null));
	}
	
	@Test
	public void testConstructorException_nullElement() {
		List<BasicState<String>> list = Arrays.asList(bs0, null);
		Assert.assertThrows(IllegalArgumentException.class, () -> new State<>(list));
	}
}

