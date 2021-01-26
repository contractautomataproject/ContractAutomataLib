package MSCA;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

public class MSCATest {
	
	@Test
	public void compositionTest() throws ParserConfigurationException, SAXException, IOException {
//		List<MSCA> aut = new ArrayList<>(2);
//		String dir = System.getProperty("user.dir");
//		aut.add(MSCAIO.parseXMLintoMSCA(dir+"/demoJSCP/BusinessClient.mxe"));
//		aut.add(MSCAIO.parseXMLintoMSCA(dir+"/demoJSCP/Hotel.mxe"));
//	    MSCA comp=MSCA.composition(aut, t->t.getLabel().isRequest(),100);
//	    MSCAIO.convertMSCAintoXML(dir+"/demoJSCP/BusinessClientxHotel.mxe",comp);
//	    MSCAIO.parseXMLintoMSCA(dir+"/demoJSCP/BusinessClientxHotel.mxe");
	}
	
	@Test
	public void loadTest() throws ParserConfigurationException, SAXException, IOException {
//		List<MSCA> aut = new ArrayList<>(2);
//		String dir = System.getProperty("user.dir");
//		aut.add(MSCAIO.load(dir+"/demoJSCP/BusinessClient.mxe.data"));
//		aut.add(MSCAIO.load(dir+"/demoJSCP/EconomyClient.mxe.data"));
//		aut.add(MSCAIO.load(dir+"/demoJSCP/Hotel.mxe.data"));
	}
	
	@Test
	public void unionTest() throws ParserConfigurationException, SAXException, IOException {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/demoJSCP/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/demoJSCP/EconomyClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/demoJSCP/Hotel.mxe.data"));
		
		MSCA.union(aut);
	}
}
