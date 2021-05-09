package contractAutomataTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import contractAutomata.JsonConverter;
import contractAutomata.MSCA;
import contractAutomata.MSCAIO;
import contractAutomata.VoxLogicaJsonConverter;

public class JsonConverterTest {
	
	@Test
	public void importTest() throws Exception {
		String dir = System.getProperty("user.dir");
		JsonConverter jc = new VoxLogicaJsonConverter();
		MSCA aut = jc.importJSON(dir+"/CAtest/testgraph.json");
	    MSCAIO.convertMSCAintoXML(dir+"/CAtest/testgraph.mxe", aut);
	    MSCA test= MSCAIO.parseXMLintoMSCA(dir+"/CAtest/voxlogicajsonimporttest.mxe");
	    assertEquals(MSCATest.checkTransitions(aut, test),true);
	}

	@Test
	public void exportTest() throws Exception {
		String dir = System.getProperty("user.dir");
		JsonConverter jc = new VoxLogicaJsonConverter();
		jc.exportJSON(null, dir);//only for coverage
	}

}
