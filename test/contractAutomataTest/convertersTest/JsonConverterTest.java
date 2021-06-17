package contractAutomataTest.convertersTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import contractAutomata.MSCA;
import contractAutomata.converters.JsonConverter;
import contractAutomata.converters.MSCAConverter;
import contractAutomata.converters.MxeConverter;
import contractAutomataTest.MSCATest;

public class JsonConverterTest {
	private final MxeConverter bmc = new MxeConverter();
	private String dir = System.getProperty("user.dir");
	
	@Test
	public void importTest() throws Exception {
		MSCAConverter jc = new JsonConverter();
		MSCA aut = jc.importMSCA(dir+"/CAtest/testgraph.json");
	    bmc.exportMSCA(dir+"/CAtest/testgraph", aut);
	    MSCA test= bmc.importMSCA(dir+"/CAtest/voxlogicajsonimporttest.mxe");
	    assertEquals(MSCATest.checkTransitions(aut, test),true);
	}

	@Test
	public void exportTest() throws Exception {
		MSCAConverter jc = new JsonConverter();
		jc.exportMSCA(dir,null);//only for coverage
	}

}
