package contractAutomataTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import contractAutomata.BasicMxeConverter;
import contractAutomata.JsonConverter;
import contractAutomata.MSCA;
import contractAutomata.VoxLogicaJsonConverter;

public class JsonConverterTest {
	private final BasicMxeConverter bmc = new BasicMxeConverter();
	private String dir = System.getProperty("user.dir");
	
	@Test
	public void importTest() throws Exception {
		JsonConverter jc = new VoxLogicaJsonConverter();
		MSCA aut = jc.importJSON(dir+"/CAtest/testgraph.json");
	    bmc.exportMxe(dir+"/CAtest/testgraph.mxe", aut);
	    MSCA test= bmc.importMxe(dir+"/CAtest/voxlogicajsonimporttest.mxe");
	    assertEquals(MSCATest.checkTransitions(aut, test),true);
	}

	@Test
	public void exportTest() throws Exception {
		JsonConverter jc = new VoxLogicaJsonConverter();
		jc.exportJSON(null, dir);//only for coverage
	}

}
