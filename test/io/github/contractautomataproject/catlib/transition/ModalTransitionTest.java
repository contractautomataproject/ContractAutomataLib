package io.github.contractautomataproject.catlib.transition;


import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.transition.ModalTransition.Modality;

@RunWith(MockitoJUnitRunner.class)
public class ModalTransitionTest {
	
	@Mock BasicState<String> bs0;
	@Mock Label<String> lab;
	@Mock BasicState<String> bs1;


	@Mock State<String> cs0;
	@Mock State<String> cs1;
	@Mock State<String> cs2;
	@Mock CALabel calab;
	
	ModalTransition<String,String,State<String>,Label<String>> tu;
	ModalTransition<String,String,State<String>,Label<String>> tl;
	ModalTransition<String,String,State<String>,Label<String>> tp;
	
	ModalTransition<String,String,State<String>,CALabel> catl;
	
	Set<ModalTransition<String,String,State<String>,CALabel>> setTr;
	Set<State<String>> badStates;
	BiPredicate<ModalTransition<String,String,State<String>,CALabel>,ModalTransition<String,String,State<String>,Label<String>>> controllabilityPred;
	
	@Before
	public void setup()
	{	
		when(cs0.getRank()).thenReturn(1);
		when(cs0.toString()).thenReturn("[0]");
		when(cs0.print()).thenReturn(List.of("0"));
		when(cs1.getRank()).thenReturn(1);
		when(cs1.toString()).thenReturn("[1]");
		when(cs1.print()).thenReturn(List.of("1"));
		when(lab.getRank()).thenReturn(1);
		when(lab.toString()).thenReturn("[!test]");
		when(calab.getRank()).thenReturn(1);
		when(calab.isMatch()).thenReturn(false);
		
		tu = new ModalTransition<>(cs0,lab,cs1,Modality.URGENT);
		tl = new ModalTransition<>(cs0,lab,cs1,Modality.LAZY);
		tp = new ModalTransition<>(cs0,lab,cs1,Modality.PERMITTED);
	
		catl = new ModalTransition<>(cs1,calab,cs1,Modality.LAZY);
		badStates = new HashSet<>();
		setTr = Set.of(catl);
	}
		
	
	@Test
	public void testConstructorNullMod() {	
		Assert.assertThrows(IllegalArgumentException.class, 
				() -> new ModalTransition<String,String,State<String>,Label<String>>(cs0,lab,cs1,null));
	}
	
	@Test
	public void testIsUrgentTrue() {
		Assert.assertTrue(tu.isUrgent());
	}
	
	@Test
	public void testIsUrgentFalse() {
		Assert.assertFalse(tp.isUrgent());
	}
	
	@Test
	public void testIsLazyTrue() {
		Assert.assertTrue(tl.isLazy());
	}
	
	@Test
	public void testIsLazyFalse() {
		Assert.assertFalse(tu.isLazy());
	}
	
	@Test
	public void testIsPermittedTrue() {
		Assert.assertTrue(tp.isPermitted());
	}
	
	@Test
	public void testIsPermittedFalse() {
		Assert.assertFalse(tl.isPermitted());
	}
	
	@Test
	public void testIsNecessaryUrgentTrue() {
		Assert.assertTrue(tu.isNecessary());
	}
	
	@Test
	public void testIsNecessaryLazyTrue() {
		Assert.assertTrue(tl.isNecessary());
	}
	
	@Test
	public void testIsNecessaryFalse() {
		Assert.assertFalse(tp.isNecessary());
	}
	
	@Test
	public void testGetModality() {
		assertEquals(Modality.URGENT,tu.getModality());
	}
	
	@Test
	public void testToStringUrgent() {
		assertEquals("!U([0],[!test],[1])",tu.toString());
	}
	
	@Test
	public void testToStringLazy() {
		assertEquals("!L([0],[!test],[1])",tl.toString());
	}

	@Test
	public void testToStringPermitted() {
		assertEquals("([0],[!test],[1])",tp.toString());
	}
	
	@Test
	public void testPrintUrgent() {
		assertEquals("!U([0],[!test],[1])",tu.print());
	}
	
	@Test
	public void testPrintLazy() {
		assertEquals("!L([0],[!test],[1])",tl.print());
	}

	@Test
	public void testPrintPermitted() {
		assertEquals("([0],[!test],[1])",tp.print());
	}
	
	@Test
	public void testHashCodeEquals() {
		assertEquals(tu.hashCode(),  new ModalTransition<String,String,State<String>,Label<String>>(cs0,lab,cs1,Modality.URGENT).hashCode());
	}
	
	@Test
	public void testHashCodeNotEquals() {
		Assert.assertNotEquals(tu.hashCode(),  tl.hashCode());
	}
	

	@Test
	public void testEquals() {
		assertEquals(tu,  new ModalTransition<String,String,State<String>,Label<String>>(cs0,lab,cs1,Modality.URGENT));
	}
	
	@Test
	public void testNotEqualsModality() {
		Assert.assertNotEquals(tu,  tl);
	}
	
	@Test
	public void testNotEqualsSuper() {
		Assert.assertNotEquals(tu, new ModalTransition<String,String,State<String>,Label<String>>(cs0,lab,cs0,Modality.URGENT));
	}
	
	@Test
	public void testIsUncontrollableUrgentTrue() {
		Assert.assertTrue(tu.isUncontrollable(setTr, badStates, controllabilityPred));
	}
	

	@Test
	public void testIsUncontrollablePermittedFalse() {
		Assert.assertFalse(tp.isUncontrollable(setTr, badStates, controllabilityPred));
	}
	
	@Test
	public void testIsUncontrollableLazyTrueNoMatch() {
		Assert.assertTrue(tl.isUncontrollable(setTr, badStates, controllabilityPred));
	}
	
	
	@Test
	public void testIsUncontrollableLazyTrueNoBadStateSource() {
		when(calab.isMatch()).thenReturn(true);
		badStates = Set.of(cs1);
		Assert.assertTrue(tl.isUncontrollable(setTr, badStates, controllabilityPred));
	}
	
	@Test
	public void testIsUncontrollableLazyTrueFalsePred() {
		when(calab.isMatch()).thenReturn(true);
		controllabilityPred = (a1,a2)->false;
		Assert.assertTrue(tl.isUncontrollable(setTr, badStates, controllabilityPred));
	}
	
	@Test
	public void testIsUncontrollableLazyFalseTruePred() {
		when(calab.isMatch()).thenReturn(true);

		ModalTransition<String,String,State<String>,CALabel> catl2 = 
				new ModalTransition<>(cs1,calab,cs0,Modality.LAZY);
		setTr = Set.of(catl,catl2);
		controllabilityPred = (a1,a2)->a1.getTarget().equals(cs0);
		Assert.assertFalse(tl.isUncontrollable(setTr, badStates, controllabilityPred));
	}
	
	
	@Test
	public void testIsUncontrollableLazyFalseNoneMatchWithTwoTransitions() {
		when(calab.isMatch()).thenReturn(true);
		ModalTransition<String,String,State<String>,CALabel> catl2 = 
				new ModalTransition<>(cs1,calab,cs0,Modality.LAZY);
		setTr = Set.of(catl,catl2);
		controllabilityPred = (a1,a2)->a1.getSource().equals(cs0);
		Assert.assertTrue(tl.isUncontrollable(setTr, badStates, controllabilityPred));
	}	
	
}
