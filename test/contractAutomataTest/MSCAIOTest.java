package contractAutomataTest;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.junit.Test;
import org.xml.sax.SAXException;

import contractAutomata.BasicState;
import contractAutomata.MSCA;
import contractAutomata.MSCAIO;

public class MSCAIOTest {

	@Test
	public void dummyConstructorForCoverage() {		
		MSCAIO dummy = new MSCAIO();
		assertNotEquals(dummy,null);
	}
	

	@Test
	public void loadVSparseSCP2020_BusinessClient() throws Exception {		
		//check that loading .data and parsing .mxe are equals 
		String dir = System.getProperty("user.dir");
		MSCA comp= MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data");
		MSCA comp2= MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClient.mxe");
		assertEquals(MSCATest.checkTransitions(comp,comp2),true);
	}

	@Test
	public void loadAndPrintTest_SCP2020_BusinessClientxHotelxEconomyClient() throws Exception {		
		//check if by loading and printing the automaton does not change
		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.load(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe.data");
		MSCAIO.printToFile(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe.data",aut);
		MSCA test = MSCAIO.load(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe.data");
		assertEquals(MSCATest.checkTransitions(aut,test),true);
	}

	@Test
	public void loadAndCheckBasicStatesTest_SCP2020_BusinessClientxHotelxEconomyClient() throws Exception {		
		//check if there are different objects for the same basic state
		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.load(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe.data");

		assertEquals(aut.getStates().stream()
		.flatMap(cs->cs.getState().stream()
				.map(bs->new AbstractMap.SimpleEntry<Integer,BasicState>(cs.getState().indexOf(bs),bs)))
		.anyMatch(e1->aut.getStates()
				.stream()
				.map(cs->cs.getState().get(e1.getKey()))
				.filter(bs->bs!=e1.getValue()&&bs.getLabel().equals(e1.getValue().getLabel()))
				.count()>0),false);
	}
	
	@Test
	public void parseAndCheckBasicStatesTest_SCP2020_BusinessClientxHotelxEconomyClient() throws Exception {		
		//check if there are different objects for the same basic state
		String dir = System.getProperty("user.dir");
//		MSCA aut = MSCAIO.parseXMLintoMSCAnew(dir+"/CAtest/testnew.mxe");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe");

		assertEquals(aut.getStates().stream()
		.flatMap(cs->cs.getState().stream()
				.map(bs->new AbstractMap.SimpleEntry<Integer,BasicState>(cs.getState().indexOf(bs),bs)))
		.anyMatch(e1->aut.getStates()
				.stream()
				.map(cs->cs.getState().get(e1.getKey()))
				.filter(bs->bs!=e1.getValue()&&bs.getLabel().equals(e1.getValue().getLabel()))
				.count()>0),false);
	}

//	@Test
	public void parseAndConvertAllMxe() {
		String dir = System.getProperty("user.dir");

		try {
			Files.list(Paths.get(dir+"/Catest/"))
					.map(Path::toFile)
					.filter(f->f.getName().endsWith("data")&&!f.getName().startsWith("ill"))
					.map(f->{
						try {
							System.out.println(f.getAbsolutePath());
							return new AbstractMap.SimpleEntry<String,MSCA>(f.getAbsolutePath(),
//									MSCAIO.parseXMLintoMSCA(f.getAbsolutePath()));
									MSCAIO.load(f.getAbsolutePath()));

						} catch (Exception e) {
							throw new RuntimeException();
						}
					})
					.forEach(e->{
						try {
	//						MSCAIO.convertMSCAintoXMLnew(e.getKey(),e.getValue());
						} catch (Exception ex) {
							throw new RuntimeException();
						}
					});
		} catch (IOException e1) {
			System.out.println(e1.toString());
			e1.printStackTrace();
			return;
		}
	}
//	
//	@Test
//	public void conversionXMLNew() throws Exception, TransformerException {
//		//check if by converting and parsing the automaton does not change
//		String dir = System.getProperty("user.dir");
////		MSCA comp= MSCAIO.load(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe.data");		
//		MSCA comp= MSCAIO.parseXMLintoMSCAnew(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe");			
//		MSCAIO.convertMSCAintoXMLnew(dir+"/CAtest/testnew.mxe",comp);
//		MSCA test=MSCAIO.parseXMLintoMSCAnew(dir+"/CAtest/testnew.mxe");
//
//		assertEquals(MSCATest.checkTransitions(comp,test),true);
//	}
	
	@Test
	public void conversionXMLtestSCP2020_BusinessClientxHotel() throws Exception, TransformerException {
		//check if by converting and parsing the automaton does not change
		String dir = System.getProperty("user.dir");
		MSCA comp= MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe");			
		MSCAIO.convertMSCAintoXML(dir+"/CAtest/test.mxe",comp);
		MSCA test=MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test.mxe");

		assertEquals(MSCATest.checkTransitions(comp,test),true);
	}

	@Test
	public void parsePrintLoadTest_SCP2020_BusinessClientxHotelxEconomyClient() throws Exception {		
		//check if by parsing and printing the automaton does not change

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe");
		MSCAIO.printToFile(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe",aut);
		MSCA test = MSCAIO.load(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe.data");

		assertEquals(MSCATest.checkTransitions(aut,test),true);

	}
	
	@Test
	public void parse_noxy() throws Exception, TransformerException {		
		//check if by parsing and printing the automaton does not change

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_parse_noxy.mxe");
		MSCAIO.convertMSCAintoXML(dir+"/CAtest/test_parse_withxy.mxe",aut);

		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_parse_withxy.mxe");
		assertEquals(MSCATest.checkTransitions(aut,test),true);

	}


	@Test
	public void loadConvertSCP2020_BusinessClientxHotel() throws Exception, TransformerException {
		//check if by loading and converting the automaton does not change
		String dir = System.getProperty("user.dir");
		MSCA comp= MSCAIO.load(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe.data");			
		MSCAIO.convertMSCAintoXML(dir+"/CAtest/test.mxe",comp);
		MSCA test=MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test.mxe");

		assertEquals(MSCATest.checkTransitions(comp,test),true);
	}
	
	
	//****************************Exceptions**********************************
	@Test
	public void wrongFormatData_exception() throws IOException
	{
		//
		String dir = System.getProperty("user.dir");
		assertThatThrownBy(() -> MSCAIO.load(dir+"/CAtest//BusinessClient.mxe"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("Not a .data format");
	}
	
	@Test
	public void emptyFileName_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		//
		assertThatThrownBy(() -> MSCAIO.printToFile("",null))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("Empty file name");
	}
	
	
	@Test
	public void loadIllActions_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		assertThatThrownBy(() -> MSCAIO.load(dir+"/CAtest//illformed.data"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("The label is not well-formed");
	}
	
	@Test
	public void loadIllRankStatesHigher_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		assertThatThrownBy(() -> MSCAIO.load(dir+"/CAtest//illformed2.data"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Ill-formed transitions, different ranks");
	}
	
	@Test
	public void loadIllRankStatesLower_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		assertThatThrownBy(() -> MSCAIO.load(dir+"/CAtest//illformed3.data"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Ill-formed transitions, different ranks");
	}
	
	@Test
	public void loadIllRankInitialStatesLower_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		assertThatThrownBy(() -> MSCAIO.load(dir+"/CAtest//illformed4.data"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Initial state with different rank");
	}
	
	@Test
	public void loadIllRankFinalStatesLower_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		assertThatThrownBy(() -> MSCAIO.load(dir+"/CAtest//illformed5.data"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Final states with different rank");
	}
	
	
	@Test
	public void parseDuplicateStates_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		assertThatThrownBy(() -> MSCAIO.parseXMLintoMSCA(dir+"/CAtest//illformed.mxe"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Duplicate states!");
	}

	@Test
	public void parseIllActions_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		assertThatThrownBy(() -> MSCAIO.parseXMLintoMSCA(dir+"/CAtest//illformed2.mxe"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Ill-formed action");
	}
	
	@Test
	public void parseNoFinalStates_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		assertThatThrownBy(() -> MSCAIO.parseXMLintoMSCA(dir+"/CAtest//illformed3.mxe"))
	    .isInstanceOf(IllegalArgumentException.class) //IOException.class)
	    .hasMessageContaining("No Final States!");
	}
	
	@Test
	public void parseEmptyElements_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		assertThatThrownBy(() -> MSCAIO.parseXMLintoMSCA(dir+"/CAtest//illformed4.mxe"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("No states!");
	}
	
	@Test
	public void parseWrongFinalStates_exception() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		assertThatThrownBy(() -> MSCAIO.parseXMLintoMSCA(dir+"/CAtest//illformed5.mxe"))
	    .isInstanceOf(IOException.class)
	    .hasMessageContaining("Problems with final states in .mxe");
	}

}
