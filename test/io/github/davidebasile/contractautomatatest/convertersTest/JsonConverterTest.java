package io.github.davidebasile.contractautomatatest.convertersTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.converters.JsonConverter;
import io.github.davidebasile.contractautomata.converters.MSCAConverter;
import io.github.davidebasile.contractautomata.converters.MxeConverter;
import io.github.davidebasile.contractautomatatest.MSCATest;

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
