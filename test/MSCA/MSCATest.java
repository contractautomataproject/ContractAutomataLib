package MSCA;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import contractAutomata.MSCA;
import contractAutomata.MSCAIO;

public class MSCATest {
	
	@Test
	public void compositionTest() throws ParserConfigurationException, SAXException, IOException {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/Hotel.mxe.data"));
	    MSCA comp=MSCA.composition(aut, t->t.getLabel().isRequest(),100);
	    MSCAIO.convertMSCAintoXML(dir+"/test/BusinessClientxHotel.mxe",comp);
	    MSCAIO.parseXMLintoMSCA(dir+"/test/BusinessClientxHotel.mxe");
	}
	
	@Test
	public void loadTest() throws ParserConfigurationException, SAXException, IOException {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/EconomyClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/Hotel.mxe.data"));
	}
	
	@Test
	public void unionTest() throws ParserConfigurationException, SAXException, IOException {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/EconomyClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/Hotel.mxe.data"));
		
		MSCA.union(aut).toString();
	}

	@Test
	public void cloneTest() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/provaurgent.mxe");
		aut.clone();
	}
	
	@Test
	public void mpcTest() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/provaurgent.mxe");
		aut.mpc();
	}
	
	@Test
	public void chorTest() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/provaLazy2.mxe");
		aut.choreography();
		assert(aut==null);
	}
}
