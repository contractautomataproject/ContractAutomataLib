package io.github.contractautomataproject.catlib.automaton.transition;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.State;


@RunWith(MockitoJUnitRunner.class)
public class TransitionTest {
	
	@Mock State<String> bs0;
	@Mock Label<String> lab;
	@Mock State<String> bs1;
	@Mock Label<String> lab2;
	
	Transition<String,String,State<String>,Label<String>> t1;
	
	@Before
	public void setup()
	{	
		when(bs0.toString()).thenReturn("[0]");
		when(bs1.toString()).thenReturn("[1]");
		when(lab.toString()).thenReturn("[!test]");
		
		when(bs0.print()).thenReturn(List.of("0"));
		when(bs1.print()).thenReturn(List.of("1"));
		
		
		
		when(bs0.getRank()).thenReturn(1);
		when(bs1.getRank()).thenReturn(1);
		when(lab.getRank()).thenReturn(1);
		when(lab2.getRank()).thenReturn(1);
		
		t1 = new Transition<>(bs0,lab,bs1);
	}
	
	@Test
	public void testGetSource() {
		assertEquals(bs0,t1.getSource());
	}
	
	@Test
	public void testGetLabel() {
		assertEquals(lab,t1.getLabel());
	}
	
	@Test
	public void testGetTarget() {
		assertEquals(bs1,t1.getTarget());
	}
	
	@Test
	public void testGetRank() {
		assertEquals(1,t1.getRank().intValue());
	}
	
	@Test
	public void testToString() {
		assertEquals("([0],[!test],[1])",t1.toString());
	}
	
	@Test
	public void testPrint() {
		assertEquals("([0],[!test],[1])",t1.print());
	}
	
	
	@Test
	public void testHashCodeEquals() {
		assertEquals(t1.hashCode(), new Transition<>(bs0, lab, bs1).hashCode());
	}
	
	@Test
	public void testHashCodeNotEquals() {
		Assert.assertNotEquals(t1.hashCode(), new Transition<>(bs1, lab, bs0).hashCode());
	}
	
	@Test
	public void testEquals() {
		assertEquals(t1, new Transition<>(bs0, lab, bs1));
	}
	
	@Test
	public void testNotEqualsSource() {
		Assert.assertNotEquals(t1, new Transition<>(bs1, lab, bs1));
	}
	
	@Test
	public void testNotEqualsLabel() {
		Assert.assertNotEquals(t1, new Transition<>(bs0, lab2, bs1));
	}
	
	@Test
	public void testNotEqualsTarget() {
		Assert.assertNotEquals(t1, new Transition<>(bs0, lab, bs0));
	}
	
	@Test
	public void testEqualsReflexive() {
		assertEquals(t1,t1);
	}
	
	@Test
	public void testNotEqualsNull() {
		Assert.assertNotEquals(t1,null);
	}
	
	@Test
	public void testNotEqualsClass() {
		Assert.assertNotEquals(t1,"test");
	}
	
	/////////
	
	@Test
	public void testConstructorExceptionTargetNull() {
		assertThrows("source, label or target null", IllegalArgumentException.class, ()-> new Transition<>(bs0, lab, null));
		
	}
	
	@Test
	public void testConstructorExceptionLabelNull() {
		assertThrows("source, label or target null", IllegalArgumentException.class, ()->new Transition<String,String,State<String>,Label<String>>(bs0,null,bs0));
	}
	
	@Test
	public void testConstructorExceptionSourceNull() {
		assertThrows("source, label or target null", IllegalArgumentException.class, ()-> new Transition<>(null, lab, bs0));
	}
	
	@Test
	public void testConstructorExceptionSourceRank() {
		when(bs0.getRank()).thenReturn(3);
		assertThrows("source, label or target with different ranks", IllegalArgumentException.class, ()-> new Transition<>(bs0, lab, bs1));
	}
	
	@Test
	public void testConstructorExceptionLabelRank() {
		when(lab.getRank()).thenReturn(3);
		assertThrows("source, label or target with different ranks", IllegalArgumentException.class, ()-> new Transition<>(bs0, lab, bs1));
	}
	
	@Test
	public void testConstructorExceptionTargetRank() {
		when(bs1.getRank()).thenReturn(3);
		assertThrows("source, label or target with different ranks", IllegalArgumentException.class, ()-> new Transition<>(bs0, lab, bs1));
	}
}
