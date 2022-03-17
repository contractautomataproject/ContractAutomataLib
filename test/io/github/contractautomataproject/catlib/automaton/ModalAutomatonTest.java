package io.github.contractautomataproject.catlib.automaton;

import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;
import io.github.contractautomataproject.catlib.transition.ModalTransition.Modality;

@RunWith(MockitoJUnitRunner.class)
public class ModalAutomatonTest {

	@Mock BasicState<String> bs0;
	@Mock BasicState<String> bs1;
	@Mock BasicState<String> bs2;
	@Mock CAState cs1;
	@Mock CAState cs2;	
	@Mock CAState cs3;	
	@Mock ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel> t1;
	@Mock ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel> t2;
	@Mock ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel> t3;
	@Mock CALabel lab;

	ModalAutomaton<CALabel> aut;
	
	Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>> st;
	
	Map<Integer,Set<BasicState<String>>> map;

	@Before
	public void setup() {
		when(bs1.isFinalstate()).thenReturn(true);
		when(bs2.isFinalstate()).thenReturn(true);

		when(bs0.getState()).thenReturn("0");
		when(bs1.getState()).thenReturn("1");
		when(bs2.getState()).thenReturn("2");
		
		
		when(cs1.isInitial()).thenReturn(true);
		when(cs3.isFinalstate()).thenReturn(true);

		when(cs1.getState()).thenReturn(Arrays.asList(bs0,bs0));
		when(cs2.getState()).thenReturn(Arrays.asList(bs1,bs0));
		when(cs3.getState()).thenReturn(Arrays.asList(bs1,bs2));

		when(t1.getSource()).thenReturn(cs1);
		when(t1.getLabel()).thenReturn(lab);
		when(t1.getTarget()).thenReturn(cs2);
		when(t1.getRank()).thenReturn(2);
		when(t1.getModality()).thenReturn(Modality.URGENT);
		
		when(t2.getSource()).thenReturn(cs2);
		when(t2.getLabel()).thenReturn(lab);
		when(t2.getTarget()).thenReturn(cs3);
		when(t2.getRank()).thenReturn(2);
		when(t2.getModality()).thenReturn(Modality.PERMITTED);
		

		when(t3.getSource()).thenReturn(cs3);
		when(t3.getLabel()).thenReturn(lab);
		when(t3.getTarget()).thenReturn(cs1);
		when(t3.getRank()).thenReturn(2);
		when(t3.getModality()).thenReturn(Modality.LAZY);

		
		when(lab.toString()).thenReturn("[!test,?test]");

		st = new HashSet<>(Set.of(t1,t2,t3));
		aut = new ModalAutomaton<CALabel>(st);
		
		map = Map.of(0,Set.of(bs0,bs1),1, Set.of(bs0,bs2));		
	}

	@After
	public void teardown() {
		st = null;
		aut = null;
	}

	@Test
	public void testGetBasicStates() {
		Assert.assertEquals(map, aut.getBasicStates());
	}
	

	@Test
	public void testPrintFinalStates() {
//		Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>> spyst = Mockito.spy(st);
//		Mockito.doReturn(Set.of(t1).iterator()).when(spyst).iterator();	
//		aut = new ModalAutomaton<>(spyst);
		
		String test = "Rank: 2" + System.lineSeparator()+
				"Initial state: [0, 0]" + System.lineSeparator()+ 
				"Final states: [[1][2]]" + System.lineSeparator()+ 
				"Transitions: " + System.lineSeparator()+
				"!U([0, 0],[!test,?test],[1, 0])" + System.lineSeparator()+
				"([1, 0],[!test,?test],[1, 2])"  + System.lineSeparator()+ 
				"!L([1, 2],[!test,?test],[0, 0])" + System.lineSeparator();
		Assert.assertEquals(test, aut.toString());
	}

	@Test
	public void testAmbiguousStates_exception() throws Exception
	{	
		when(cs1.getState()).thenReturn(Arrays.asList(bs0));
		when(cs2.getState()).thenReturn(Arrays.asList(bs1));
		when(cs3.getState()).thenReturn(Arrays.asList(bs1));


		st = Set.of(t1,t2);
		Assert.assertThrows("Transitions have ambiguous states (different objects for the same state).", 
				IllegalArgumentException.class,
				() -> new ModalAutomaton<CALabel>(st));
	}

	public static boolean autEquals(Automaton<?,?,?,?> aut, Automaton<?,?,?,?>  test) {
		Set<String> autTr=aut.getTransition().parallelStream()
				.map(t->t.toString())
				.collect(Collectors.toSet());
		Set<String> testTr=test.getTransition().parallelStream()
				.map(t->t.toString())
				.collect(Collectors.toSet());

		return autTr.parallelStream()
				.allMatch(t->testTr.contains(t))
				&&
				testTr.parallelStream()
				.allMatch(t->autTr.contains(t));
	}
}
//	private static <T extends State<?>> String csvState(T state) {
//		if (state instanceof CAState)
//		{
//			CAState castate = (CAState) state;
//			return castate.getState().stream()
//					.map(BasicState::toCSV)
//					.collect(Collectors.joining());
//		}
//		else
//		{
//			String finalstate= (state.isFinalstate())?",final=true":"";
//			String initial= (state.isInitial())?",initial=true":"";
//			return "label="+state.getState()+finalstate+initial;
//		}
//	}
//
//	private static <T extends Label<?>> String csvLabel(T label) {
//		if (label instanceof CALabel) {
//			CALabel cal = (CALabel) label;
//			return "[rank=" + cal.getRank() + ", offerer=" + cal.getOfferer()+ ", requester=" + cal.getRequester()
//			+ ", actiontype=" + cal.getActiontype()+ "]";
//		}
//		else
//			return "[action=" +label.getAction()+"]";
//	}
//
//	private static <T extends Transition<?,?,?,?>> String csvTransition(T transition) {
//		if (transition instanceof ModalTransition<?,?,?,?>) {
//			ModalTransition<?,?,?,?> mt = (ModalTransition<?,?,?,?>) transition;
//			return "[mod="+mt.getModality()+",source="+csvState(mt.getSource())
//			+",label="+csvLabel(mt.getLabel())
//			+",target="+csvState(mt.getTarget())+"]";
//		}
//		else return "[source="+csvState(transition.getSource())
//				+",label="+csvLabel(transition.getLabel())
//				+",target="+csvState(transition.getTarget())+"]";
//	}