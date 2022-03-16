package io.github.contractautomataproject.catlib.automaton;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.HashMap;
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

	ModalAutomaton<CALabel> aut;
	Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>> st;

	@Before
	public void setup() {
		when(cs1.isInitial()).thenReturn(true);
		when(cs2.isInitial()).thenReturn(false);
		when(cs3.isInitial()).thenReturn(false);
		when(cs3.isFinalstate()).thenReturn(true);

		when(t1.getSource()).thenReturn(cs1);
		when(t1.getTarget()).thenReturn(cs2);
		when(t2.getSource()).thenReturn(cs2);
		when(t2.getTarget()).thenReturn(cs3);

		when(cs1.getState()).thenReturn(Arrays.asList(bs0,bs0));
		when(cs2.getState()).thenReturn(Arrays.asList(bs1,bs0));
		when(cs3.getState()).thenReturn(Arrays.asList(bs1,bs2));

		st = Set.of(t1,t2);
		aut = new ModalAutomaton<CALabel>(st);
	}

	@After
	public void teardown() {
		st = null;
		aut = null;
	}

	@Test
	public void testGetBasicStates() {
		Map<Integer,Set<BasicState<String>>> map = new HashMap<>();
		map.put(0, Set.of(bs0,bs1));
		map.put(1, Set.of(bs0,bs2));
		Assert.assertEquals(map, aut.getBasicStates());
	}

	@Test
	public void testPrintFinalStates() {
		when(bs1.isFinalstate()).thenReturn(true);
		when(bs2.isFinalstate()).thenReturn(true);
		when(bs1.getState()).thenReturn("1");
		when(bs2.getState()).thenReturn("2");
		when(t2.getRank()).thenReturn(2);
		String test = "[1][2]";

		Assert.assertEquals(test, aut.printFinalStates());
	}

	@Test
	public void testAmbiguousStates_exception() throws Exception
	{	
		when(cs1.getState()).thenReturn(Arrays.asList(bs0));
		when(cs2.getState()).thenReturn(Arrays.asList(bs1));
		when(cs3.getState()).thenReturn(Arrays.asList(bs1));


		st = Set.of(t1,t2);
		assertThatThrownBy(() -> new ModalAutomaton<CALabel>(st))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Transitions have ambiguous states (different objects for the same state).");
	}

	public static boolean autEquals(Automaton<?,?,?,?> aut, Automaton<?,?,?,?>  test) {
		Set<String> autTr=aut.getTransition().parallelStream()
				.map(t->t.toCSV())
				.collect(Collectors.toSet());
		Set<String> testTr=test.getTransition().parallelStream()
				.map(t->t.toCSV())
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