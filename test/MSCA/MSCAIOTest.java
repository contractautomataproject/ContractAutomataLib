package MSCA;
import java.io.IOException;
import java.util.AbstractMap;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import contractAutomata.BasicState;
import contractAutomata.MSCA;
import contractAutomata.MSCAIO;

public class MSCAIOTest {

	@Test
	public void loadVSparseSCP2020_BusinessClient() throws ParserConfigurationException, SAXException, IOException {		
		//check that loading .data and parsing .mxe are equals 
		String dir = System.getProperty("user.dir");
		MSCA comp= MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data");
		MSCA comp2= MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClient.mxe");
		assert(MSCATest.checkTransitions(comp,comp2));
	}

	@Test
	public void loadAndPrintTest_SCP2020_BusinessClientxHotelxEconomyClient() throws ParserConfigurationException, SAXException, IOException {		
		//check if by loading and printing the automaton does not change
		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.load(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe.data");
		MSCAIO.printToFile(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe.data",aut);
		MSCA test = MSCAIO.load(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe.data");
		assert(MSCATest.checkTransitions(aut,test));
	}

	@Test
	public void loadAndCheckBasicStatesTest_SCP2020_BusinessClientxHotelxEconomyClient() throws ParserConfigurationException, SAXException, IOException {		
		//check if there are different objects for the same basic state

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.load(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe.data");

		assert(aut.getStates().stream()
		.flatMap(cs->cs.getState().stream()
				.map(bs->new AbstractMap.SimpleEntry<Integer,BasicState>(cs.getState().indexOf(bs),bs)))
		.anyMatch(e1->aut.getStates()
				.stream()
				.map(cs->cs.getState().get(e1.getKey()))
				.filter(bs->bs!=e1.getValue()&&bs.getLabel().equals(e1.getValue().getLabel()))
				.count()>0)==false);
	}
	
	@Test
	public void parseAndCheckBasicStatesTest_SCP2020_BusinessClientxHotelxEconomyClient() throws ParserConfigurationException, SAXException, IOException {		
		//check if there are different objects for the same basic state

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe");

		assert(aut.getStates().stream()
		.flatMap(cs->cs.getState().stream()
				.map(bs->new AbstractMap.SimpleEntry<Integer,BasicState>(cs.getState().indexOf(bs),bs)))
		.anyMatch(e1->aut.getStates()
				.stream()
				.map(cs->cs.getState().get(e1.getKey()))
				.filter(bs->bs!=e1.getValue()&&bs.getLabel().equals(e1.getValue().getLabel()))
				.count()>0)==false);
	}

	@Test
	public void conversionXMLtestSCP2020_BusinessClientxHotel() throws ParserConfigurationException, SAXException, IOException {
		//check if by converting and parsing the automaton does not change
		String dir = System.getProperty("user.dir");
		MSCA comp= MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe");			
		MSCAIO.convertMSCAintoXML(dir+"/CAtest/test.mxe",comp);
		MSCA test=MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test.mxe");

		assert(MSCATest.checkTransitions(comp,test));
	}

	@Test
	public void parsePrintLoadTest_SCP2020_BusinessClientxHotelxEconomyClient() throws ParserConfigurationException, SAXException, IOException {		
		//check if by parsing and printing the automaton does not change

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe");
		MSCAIO.printToFile(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe.data",aut);
		MSCA test = MSCAIO.load(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe.data");

		assert(MSCATest.checkTransitions(aut,test));

	}
	
	@Test
	public void parse_noxy() throws ParserConfigurationException, SAXException, IOException {		
		//check if by parsing and printing the automaton does not change

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_parse_noxy.mxe");
		MSCAIO.convertMSCAintoXML(dir+"/CAtest/test_parse_withxy.mxe",aut);

		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_parse_withxy.mxe");
		assert(MSCATest.checkTransitions(aut,test));

	}


	@Test
	public void loadConvertSCP2020_BusinessClientxHotel() throws ParserConfigurationException, SAXException, IOException {
		//check if by loading and converting the automaton does not change
		String dir = System.getProperty("user.dir");
		MSCA comp= MSCAIO.load(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe.data");			
		MSCAIO.convertMSCAintoXML(dir+"/CAtest/test.mxe",comp);
		MSCA test=MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test.mxe");

		assert(MSCATest.checkTransitions(comp,test));
	}

}
