package contractAutomataTest.convertersTest;
import static org.junit.Assert.assertEquals;

import java.io.File;

import javax.xml.transform.TransformerException;

import org.junit.Test;

import contractAutomata.automaton.MSCA;
import contractAutomata.converters.DataConverter;
import contractAutomata.converters.MxeConverter;
import contractAutomataTest.MSCATest;

/**
 * used for cross validation of different converters
 * 
 * @author Davide Basile
 *
 */
public class ConverterTest {
	private final MxeConverter bmc = new MxeConverter();
	private final DataConverter bdc = new DataConverter();
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;

	@Test
	public void loadVSparseSCP2020_BusinessClient() throws Exception {		
		//check that loading .data and parsing .mxe are equals 
		MSCA comp= bdc.importMSCA(dir+"BusinessClient.mxe.data");
		MSCA comp2= bmc.importMSCA(dir+"BusinessClient.mxe");
		assertEquals(MSCATest.checkTransitions(comp,comp2),true);
	}

	@Test
	public void parsePrintLoadTest_SCP2020_BusinessClientxHotelxEconomyClient() throws Exception {		
		//check if by parsing and printing the automaton does not change
	
		MSCA aut = bmc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe");
		bdc.exportMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe",aut);
		MSCA test = bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe.data");

		assertEquals(MSCATest.checkTransitions(aut,test),true);

	}
	
	@Test
	public void loadConvertSCP2020_BusinessClientxHotel() throws Exception, TransformerException {
		//check if by loading and converting the automaton does not change
		
		MSCA comp= bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe.data");			
		bmc.exportMSCA(dir+"test.mxe",comp);
		MSCA test=bmc.importMSCA(dir+"test.mxe");

		assertEquals(MSCATest.checkTransitions(comp,test),true);
	}
}





////@Test
//public void parseAndConvertAllMxe() {
//
//
//try {
//	Files.list(Paths.get(dir+"Catest/"))
//			.map(Path::toFile)
//			.filter(f->f.getName().endsWith("data")&&!f.getName().startsWith("ill"))
//			.map(f->{
//				try {
//					System.out.println(f.getAbsolutePath());
//					return new AbstractMap.SimpleEntry<String,MSCA>(f.getAbsolutePath(),
////							MSCAIO.parseXMLintoMSCA(f.getAbsolutePath()));
//							MSCAIO.load(f.getAbsolutePath()));
//
//				} catch (Exception e) {
//					throw new RuntimeException();
//				}
//			})
//			.forEach(e->{
//				try {
////						MSCAIO.convertMSCAintoXMLnew(e.getKey(),e.getValue());
//				} catch (Exception ex) {
//					throw new RuntimeException();
//				}
//			});
//} catch (IOException e1) {
//	System.out.println(e1.toString());
//	e1.printStackTrace();
//	return;
//}
//}
//
//@Test
//public void conversionXMLNew() throws Exception, TransformerException {
////check if by converting and parsing the automaton does not change
//
////MSCA comp= MSCAIO.load(dir+"BusinessClientxHotelxEconomyClient.mxe.data");		
//MSCA comp= MSCAIO.parseXMLintoMSCAnew(dir+"BusinessClientxHotelxEconomyClient.mxe");			
//MSCAIO.convertMSCAintoXMLnew(dir+"testnew.mxe",comp);
//MSCA test=MSCAIO.parseXMLintoMSCAnew(dir+"testnew.mxe");
//
//assertEquals(MSCATest.checkTransitions(comp,test),true);
//}

