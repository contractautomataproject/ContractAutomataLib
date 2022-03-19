package io.github.contractautomataproject.catlib.automaton.state;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

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
	
	@Mock BasicState<Integer> bs3;
	
	@Before
	public void setup() {		
		when(bs0.isInitial()).thenReturn(true);
		when(bs0.getState()).thenReturn("0");
		when(bs1.isInitial()).thenReturn(true);
		when(bs1.getState()).thenReturn("1");
		when(bs2.getState()).thenReturn("2");
		
		test = new State<String>(List.of(bs0,bs1,bs2,bs2,bs0,bs1));
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
		when(bs0.isFinalstate()).thenReturn(true);
		when(bs1.isFinalstate()).thenReturn(true);
		when(bs2.isFinalstate()).thenReturn(true);
		assertTrue(test.isFinalstate());
	}
	
	
	@Test
	public void testIsFinalStateFalse() {
		assertFalse(test.isFinalstate());
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
		Assert.assertNotSame(list,new State<String>(list).getState());
	}
	
	
	@Test
	public void toStringInitialTest() {
		String test = "[label=0,initial=true, label=1,initial=true, label=2, label=2, label=0,initial=true, label=1,initial=true]";

		assertEquals(test, test.toString());
	}
	
	@Test
	public void toStringFinalTest() {
		when(bs4.toString()).thenReturn("label=5,final=true");
		List<BasicState<String>> l = List.of(bs4,bs4);
		
		State<String> test2=new State<String>(l);
		
		assertEquals("[label=5,final=true, label=5,final=true]", test2.toString());
	}
	
	

	@Test
	public void testPrint() {	
		assertEquals("[0, 1, 2, 2, 0, 1]",test.print().toString());
	}

	//********************** testing exceptions *********************
	
	@Test
	public void testConstructorException_empty() {
		List<BasicState<String>> list = List.of(bs1);
		Assert.assertThrows(IllegalArgumentException.class, () -> new State<String>(list));
	}
	
	@Test
	public void testConstructorException_null() {
		Assert.assertThrows(NullPointerException.class, () -> new State<String>(null));
	}
	
	@Test
	public void testConstructorException_nullelement() {
		List<BasicState<String>> list = List.of(bs0,null);
		Assert.assertThrows(NullPointerException.class, () -> new State<String>(list));
	}
	
//	@Test
//	public void testConstructorException_notStringState() {
//		Assert.assertThrows(IllegalArgumentException.class, () -> new State<String>(List.of(bs3)));
//	}
//	
//	@Test
//	public void testConstructorException_notBasicState() {
//			
//		State<String> temp = new State<String>("test") {
//			@Override
//			public boolean isFinalstate() {
//				throw new RuntimeException();
//			}
//
//			@Override
//			public boolean isInitial() {
//				throw new RuntimeException();
//			}
//
//			@Override
//			public Integer getRank() {
//				// Auto-generated method stub
//				return null;
//			}
//		};
//		Assert.assertThrows(IllegalArgumentException.class, () -> new State<String>(List.of(temp)));
//	}
}

