package io.github.contractautomataproject.catlib.converters;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.util.AbstractMap;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import io.github.contractautomataproject.catlib.automaton.ModalAutomatonTest;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;

public class AutDataConverterTest {
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<CALabel>(CALabel::new);
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;

	@Test
	public void loadAndPrintTest_SCP2020_BusinessClientxHotelxEconomyClient() throws Exception {		
		//check if by loading and printing the automaton does not change
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.data");
		bdc.exportMSCA(dir+"BusinessClientxHotelxEconomyClient.data",aut);
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.data");
		Assert.assertTrue(ModalAutomatonTest.autEquals(aut,test));
	}
	
	@Test
	public void loadAndCheckBasicStatesTest_SCP2020_BusinessClientxHotelxEconomyClient() throws Exception {		
		//check if there are different objects for the same basic state
		
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.data");

		Assert.assertFalse(aut.getStates().stream()
		.flatMap(cs->cs.getState().stream()
				.map(bs->new AbstractMap.SimpleEntry<Integer,BasicState<String>>(cs.getState().indexOf(bs),bs)))
		.anyMatch(e1->aut.getStates()
				.stream()
				.map(cs->cs.getState().get(e1.getKey()))
				.filter(bs->bs!=e1.getValue()&&bs.getState().equals(e1.getValue().getState()))
				.count()>0));
	}
	
	@Test
	public void wrongFormatData_exception() throws IOException
	{
		assertThatThrownBy(() -> bdc.importMSCA(dir+"BusinessClient.mxe"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("Not a .data format");
	}
	
	@Test
	public void emptyFileName_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		assertThatThrownBy(() -> bdc.exportMSCA("",null))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("Empty file name");
	}
	
	
	@Test
	public void loadIllActions_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed.data"))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void loadIllRankStatesHigher_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed2.data"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Ill-formed transitions, different ranks");
	}
	
	@Test
	public void loadIllRankStatesLower_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed3.data"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Ill-formed transitions, different ranks");
	}
	
	@Test
	public void loadIllRankInitialStatesLower_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed4.data"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("Initial state with different rank");
	}
	
	@Test
	public void loadIllRankFinalStatesLower_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		assertThatThrownBy(() -> bdc.importMSCA(dir+"illformed5.data"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("Final states with different rank");
	}
	
	//TODO add load CM

}
