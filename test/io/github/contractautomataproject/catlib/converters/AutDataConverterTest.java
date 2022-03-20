package io.github.contractautomataproject.catlib.converters;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;

import io.github.contractautomataproject.catlib.automaton.label.action.Action;
import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ITAutomatonTest;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;

import javax.xml.parsers.ParserConfigurationException;

public class AutDataConverterTest {
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;

	@Test
	public void loadAndPrintTest_SCP2020_BusinessClientxHotelxEconomyClient() throws Exception {		
		//check if by loading and printing the automaton does not change
		Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.data");
		bdc.exportMSCA(dir+"BusinessClientxHotelxEconomyClient.data",aut);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.data");
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
	public void wrongFormatData_exception() {
		assertThatThrownBy(() -> bdc.importMSCA(dir+"BusinessClient.mxe"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("Not a .data format");
	}
	
	@Test
	public void emptyFileName_exception() throws NumberFormatException {
		assertThatThrownBy(() -> {
			try {
				bdc.exportMSCA("", null);
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
		})
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("Empty file name");
	}
	
	
	@Test
	public void loadIllActions_exception() throws NumberFormatException {
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed.data"))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void loadIllRankStatesHigher_exception() throws NumberFormatException {
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed2.data"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Ill-formed transitions, different ranks");
	}
	
	@Test
	public void loadIllRankStatesLower_exception() throws NumberFormatException {
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed3.data"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Ill-formed transitions, different ranks");
	}
	
	@Test
	public void loadIllRankInitialStatesLower_exception() throws NumberFormatException {
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed4.data"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("Initial state with different rank");
	}
	
	@Test
	public void loadIllRankFinalStatesLower_exception() throws NumberFormatException {
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed5.data"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("Final states with different rank");
	}
	
	//TODO add load CM

}
