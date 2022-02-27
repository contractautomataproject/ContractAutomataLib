package io.github.davidebasile.contractautomatatest.operatorsTest;

import static io.github.davidebasile.contractautomatatest.MSCATest.checkTransitions;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.converters.DataConverter;
import io.github.davidebasile.contractautomata.converters.MSCAConverter;
import io.github.davidebasile.contractautomata.operators.UnionFunction;

public class UnionTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
//	private final MSCAConverter bmc = new MxeConverter();
	private final MSCAConverter bdc = new DataConverter();
	
	@Test
	public void unionTest() throws Exception {
		List<ModalAutomaton<CALabel>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.data"));
		ModalAutomaton<CALabel> union = new UnionFunction().apply(aut);
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"union_BusinessClient_EconomyClient_Hotel.data");
		assertEquals(checkTransitions(union,test),true);
	}
	
	@Test
	public void union_statelabelsnotnumbers() throws Exception {
		List<ModalAutomaton<CALabel>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"testgraph_testunion.data"));
		aut.add(bdc.importMSCA(dir+"testgraph_testunion.data"));

		ModalAutomaton<CALabel> union = new UnionFunction().apply(aut);
		//		MSCAIO.convertMSCAintoXML(dir+"union_testgraph_testgraph.mxe", union);

		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"union_testgraph_testgraph.data");
		assertEquals(checkTransitions(union,test),true);
	}


	@Test
	public void union_empty() 
	{
		assertThatThrownBy(()->new UnionFunction().apply(new ArrayList<ModalAutomaton<CALabel>>()))
		.isInstanceOf(IllegalArgumentException.class);
	}
	

	@Test
	public void union_differentrank_exception() throws Exception {
		List<ModalAutomaton<CALabel>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"BusinessClientxHotel_open.data"));

		assertThatThrownBy(() -> new UnionFunction().apply(aut))
		.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void union_illegalcharacter() throws Exception {
		List<ModalAutomaton<CALabel>> aut = new ArrayList<>(2);

		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"union_testgraph_testgraph.data");
		
		aut.add(test);
		aut.add(test);

		assertThatThrownBy(() -> new UnionFunction().apply(aut))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Illegal label containing _ in some basic state");
	}

	@Test
	public void union_nullElement() throws Exception {
		List<ModalAutomaton<CALabel>> aut = new ArrayList<>(2);	
		aut.add(null);
		aut.add(null);

		assertThatThrownBy(() -> new UnionFunction().apply(aut))
		.isInstanceOf(IllegalArgumentException.class);
	}

}
