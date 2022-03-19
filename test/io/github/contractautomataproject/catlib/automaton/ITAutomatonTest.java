package io.github.contractautomataproject.catlib.automaton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.contractautomataproject.catlib.automaton.transition.Transition;
import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition.Modality;

public class ITAutomatonTest {

	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	
//	@Test
//	public void testString() {
//		BasicState<String> s0 = new BasicState<String>("0",true,false);
//		BasicState<String> s1 = new BasicState<String>("1",false,true);
//		BasicState<String> s2 = new BasicState<String>("2",false,true);
//		Transition<String,String,BasicState<String>,Label<String>> t1 = new Transition<>(s0, new Label<String>("m"), s1);
//		Transition<String,String,BasicState<String>,Label<String>> t2 = new Transition<>(s0, new Label<String>("m"), s2);
//		
//		Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>> prop = new Automaton<>(Set.of(t1,t2));
//		
//		String test = "Rank: 1"+System.lineSeparator() + 
//				"Initial state: 0"+System.lineSeparator() + 
//				"Final states: [[1, 2]]"+System.lineSeparator() + 
//				"Transitions: "+System.lineSeparator() + 
//				"(0,m,1)"+System.lineSeparator() + 
//				"(0,m,2)"+System.lineSeparator();
//		Assert.assertEquals(prop.toString(),test);
//	}
	
	@Test
	public void constructor_Exception_differentRank() {
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.IDLE);
		lab.add(CALabel.OFFER+"a");
		lab.add(CALabel.REQUEST+"a");

		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.IDLE);
		lab2.add(CALabel.IDLE);
		lab2.add(CALabel.OFFER+"a");
		lab2.add(CALabel.REQUEST+"a");


		BasicState<String> bs0 = new BasicState<>("0", true, false);
		BasicState<String> bs1 = new BasicState<>("1", true, false);
		BasicState<String> bs2 = new BasicState<>("2", true, false);
		BasicState<String> bs3 = new BasicState<>("3", true, false);

		Set<ModalTransition<String,String,State<String>,CALabel>> tr = new HashSet<>();
		tr.add(new ModalTransition<>(new State<>(Arrays.asList(bs0, bs1, bs2)//,0,0
		),
				new CALabel(lab),
				new State<>(Arrays.asList(bs0, bs1, bs3)),
				Modality.PERMITTED));
		State<String> cs = new State<>(Arrays.asList(bs0, bs1, bs2, bs3)//,0,0
		);
		tr.add(new ModalTransition<>(cs,
				new CALabel(lab2),
				cs,
				Modality.PERMITTED));

		Assert.assertThrows("Transitions with different rank", IllegalArgumentException.class, () -> new Automaton<>(tr));
	}


	@Test
	public void noInitialState_exception() {
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.OFFER+"a");

		BasicState<String> bs0 = new BasicState<>("0", false, true);
		BasicState<String> bs1 = new BasicState<>("1", false, true);


		Set<ModalTransition<String,String,State<String>,CALabel>> tr = new HashSet<>();
		tr.add(new ModalTransition<>(new State<>(List.of(bs0)//,0,0
		),
				new CALabel(lab),
				new State<>(List.of(bs1)),
				Modality.PERMITTED));

		Assert.assertThrows("Not Exactly one Initial State found!", 
				IllegalArgumentException.class,
				() -> new Automaton<>(tr));
	}

	@Test
	public void noFinalStatesInTransitions_exception() {
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.OFFER+"a");

		BasicState<String> bs0 = new BasicState<>("0", true, false);
		BasicState<String> bs1 = new BasicState<>("1", false, false);


		Set<ModalTransition<String,String,State<String>,CALabel>> tr = new HashSet<>();
		tr.add(new ModalTransition<>(new State<>(List.of(bs0)//,0,0
		),
				new CALabel(lab),
				new State<>(List.of(bs1)),
				Modality.PERMITTED));

		Assert.assertThrows("No Final States!", 
				IllegalArgumentException.class,
				() -> new Automaton<>(tr));
	}
	
	@Test
	public void ambiguousStates_exception() {
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.OFFER+"a");

		BasicState<String> bs1 = new BasicState<>("0", true, false);
		BasicState<String> bs2 = new BasicState<>("0", false, true);

		Set<ModalTransition<String,String,State<String>,CALabel>> tr = new HashSet<>();
		tr.add(new ModalTransition<>(new State<>(List.of(bs1)),
				new CALabel(lab),
				new State<>(List.of(bs2)),
				Modality.PERMITTED));

		tr.add(new ModalTransition<>(new State<>(List.of(bs2)),
				new CALabel(lab),
				new State<>(List.of(bs2)),
				Modality.PERMITTED));
		Assert.assertThrows("Transitions have ambiguous states (different objects for the same state).", 
				IllegalArgumentException.class,
				() -> new Automaton<>(tr));
	}

	
	@Test
	public void testToString() throws IOException {
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut= bdc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient).data");
		String test ="Rank: 3"+System.lineSeparator()+
				"Initial state: [0, 0, 0]"+System.lineSeparator()+
						"Final states: [[3][3][3]]"+System.lineSeparator()+
						"Transitions: "+System.lineSeparator()+
						"!L([2, 9, 0],[?invoice, !invoice, -],[3, 3, 0])"+System.lineSeparator()+
						"!L([3, 3, 0],[-, !singleRoom, ?singleRoom],[3, 6, 7])"+System.lineSeparator()+
						"!L([3, 9, 2],[-, !invoice, ?invoice],[3, 3, 3])"+System.lineSeparator()+
						"!U([0, 0, 0],[?singleRoom, !singleRoom, -],[6, 6, 0])"+System.lineSeparator()+
						"([1, 1, 0],[!card, ?card, -],[2, 2, 0])"+System.lineSeparator()+
						"([2, 2, 0],[-, !freebrk, -],[2, 9, 0])"+System.lineSeparator()+
						"([2, 2, 0],[?receipt, !receipt, -],[3, 3, 0])"+System.lineSeparator()+
						"([3, 1, 1],[-, ?card, !card],[3, 2, 2])"+System.lineSeparator()+
						"([3, 2, 2],[-, !freebrk, -],[3, 9, 2])"+System.lineSeparator()+
						"([3, 2, 2],[-, !receipt, ?receipt],[3, 3, 3])"+System.lineSeparator()+
						"([3, 3, 0],[-, !sharedRoom, ?sharedRoom],[3, 8, 8])"+System.lineSeparator()+
						"([3, 5, 5],[-, !sharedBathroom, ?sharedBathroom],[3, 1, 1])"+System.lineSeparator()+
						"([3, 6, 7],[-, !noFreeCancellation, ?noFreeCancellation],[3, 5, 5])"+System.lineSeparator()+
						"([3, 8, 8],[-, !noFreeCancellation, ?noFreeCancellation],[3, 5, 5])"+System.lineSeparator()+
						"([6, 6, 0],[?noFreeCancellation, !noFreeCancellation, -],[9, 5, 0])"+System.lineSeparator()+
						"([9, 5, 0],[?privateBathroom, !privateBathroom, -],[1, 1, 0])"+System.lineSeparator()+
						"";
		Assert.assertEquals(test, aut.toString());
	}

	public static boolean autEquals(Automaton<?,?,?,?> aut, Automaton<?,?,?,?>  test) {
		Set<String> autTr=aut.getTransition().parallelStream()
				.map(Transition::toString)
				.collect(Collectors.toSet());
		Set<String> testTr=test.getTransition().parallelStream()
				.map(Transition::toString)
				.collect(Collectors.toSet());

		return autTr.parallelStream()
				.allMatch(testTr::contains)
				&&
				testTr.parallelStream()
				.allMatch(autTr::contains);
	}
}
