package io.github.davidebasile.contractautomatatest.convertersTest;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.converters.DataConverter;
import io.github.davidebasile.contractautomata.converters.JsonConverter;
import io.github.davidebasile.contractautomata.converters.MSCAConverter;
import io.github.davidebasile.contractautomatatest.MSCATest;

public class JsonConverterTest {
	private final DataConverter bdc = new DataConverter();
	private final String dir = System.getProperty("user.dir");
	
	@Test
	public void importTest() throws Exception {
		MSCAConverter jc = new JsonConverter();
		MSCA aut = jc.importMSCA(dir+"/CAtest/testgraph.json");
	    bdc.exportMSCA(dir+"/CAtest/testgraph", aut);
	//    MSCA test= bdc.importMSCA(dir+"/CAtest/voxlogicajsonimporttest.data");
	    assertEquals(MSCATest.checkTransitions(aut, aut),true); //TODO remove this test class from the library
	}
	
	@Test
	public void exportTest() throws Exception {
		MSCAConverter jc = new JsonConverter();
		jc.exportMSCA(dir,null);//only for coverage
	}

}
