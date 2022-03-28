package io.github.contractautomata.catlib.converters;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.ITAutomatonTest;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.transition.Transition;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class ITAutDataConverterTest {
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;


	@Test
	public void testTransition() throws IOException {
		Automaton<String, Action, State<String>, ModalTransition<String, Action, State<String>, CALabel>> axb =
				bdc.importMSCA(dir+"(AxB).data");

		String test = "[([0, 0],[!pippo, ?pippo],[1, 1]), ([1, 1],[!pluto, -],[2, 1]), ([1, 1],[-, !pluto],[1, 2]), ([1, 2],[!pluto, -],[2, 2]), ([2, 1],[-, !pluto],[2, 2])]";

		assertEquals(test,axb.getTransition()
				.stream()
				.sorted(Comparator.comparing(Transition::toString))
				.collect(Collectors.toList()).toString());
	}


	@Test
	public void loadAndPrintTest_SCP2020_BusinessClientxHotelxEconomyClient() throws Exception {		
		//check if by loading and printing the automaton does not change
		Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.data");
		bdc.exportMSCA(dir+"BusinessClientxHotelxEconomyClient_export.data",aut);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient_export.data");
		Assert.assertTrue(ITAutomatonTest.autEquals(aut,test));
	}
	
	@Test
	public void loadAndCheckBasicStatesTest_SCP2020_BusinessClientxHotelxEconomyClient() throws Exception {		
		//check if there are different objects for the same basic state
		
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.data");

		Assert.assertFalse(aut.getStates().stream()
		.flatMap(cs->cs.getState().stream()
				.map(bs-> new AbstractMap.SimpleEntry<>(cs.getState().indexOf(bs), bs)))
		.anyMatch(e1-> aut.getStates()
				.stream()
				.map(cs -> cs.getState().get(e1.getKey())).anyMatch(bs -> bs != e1.getValue() && bs.getState().equals(e1.getValue().getState()))));
	}

	@Test
	public void testImportProp() throws IOException {
		AutDataConverter<Label<Action>> adc = new AutDataConverter<>(Label::new);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,Label<Action>>>
		prop = adc.importMSCA(dir+"prop.data");


		BasicState<String> s0 = new BasicState<>("0", true, false);
		BasicState<String> s1 = new BasicState<>("1", false, false);
		BasicState<String> s2 = new BasicState<>("2", false, true);
		State<String> cs0 = new State<>(List.of(s0));
		State<String> cs1 = new State<>(List.of(s1));
		State<String> cs2 = new State<>(List.of(s2));
		ModalTransition<String,Action,State<String>, Label<Action>> t1 = new ModalTransition<>(cs0, new Label<>(List.of(new Action("blueberry"))), cs1, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,Action,State<String>,Label<Action>> t2 = new ModalTransition<>(cs1, new Label<>(List.of(new Action("ananas"))), cs2, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,Action,State<String>,Label<Action>> t3 = new ModalTransition<>(cs0, new Label<>(List.of(new Action("cherry"))), cs2, ModalTransition.Modality.PERMITTED);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,Label<Action>>> test = new Automaton<>(Set.of(t1,t2,t3));

		Assert.assertTrue(ITAutomatonTest.autEquals(prop,test));
	}

	
	//TODO add load CM

}
