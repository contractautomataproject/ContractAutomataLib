package contractAutomataTest.operatorsTest;

import static contractAutomataTest.MSCATest.checkTransitions;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import contractAutomata.automaton.MSCA;
import contractAutomata.converters.DataConverter;
import contractAutomata.converters.MxeConverter;
import contractAutomata.operators.UnionFunction;

public class UnionTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final MxeConverter bmc = new MxeConverter();
	private final DataConverter bdc = new DataConverter();
	
	@Test
	public void unionTest() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));
		MSCA union = new UnionFunction().apply(aut);
		MSCA test = bmc.importMSCA(dir+"union_BusinessClient_EconomyClient_Hotel.mxe");
		assertEquals(checkTransitions(union,test),true);
	}
	
	@Test
	public void union_statelabelsnotnumbers() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"testgraph.data"));
		aut.add(bdc.importMSCA(dir+"testgraph.data"));

		MSCA union = new UnionFunction().apply(aut);
		//		MSCAIO.convertMSCAintoXML(dir+"union_testgraph_testgraph.mxe", union);

		MSCA test = bmc.importMSCA(dir+"union_testgraph_testgraph.mxe");
		assertEquals(checkTransitions(union,test),true);
	}


	@Test
	public void union_empty() 
	{
		assertThatThrownBy(()->new UnionFunction().apply(new ArrayList<MSCA>()))
		.isInstanceOf(IllegalArgumentException.class);
	}
	

	@Test
	public void union_differentrank_exception() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bmc.importMSCA(dir+"BusinessClientxHotel_open.mxe"));

		assertThatThrownBy(() -> new UnionFunction().apply(aut))
		.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void union_illegalcharacter() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);

		MSCA test = bmc.importMSCA(dir+"union_testgraph_testgraph.mxe");
		
		aut.add(test);
		aut.add(test);

		assertThatThrownBy(() -> new UnionFunction().apply(aut))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Illegal label containing _ in some basic state");
	}

	@Test
	public void union_nullElement() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);	
		aut.add(null);
		aut.add(null);

		assertThatThrownBy(() -> new UnionFunction().apply(aut))
		.isInstanceOf(IllegalArgumentException.class);
	}

}
