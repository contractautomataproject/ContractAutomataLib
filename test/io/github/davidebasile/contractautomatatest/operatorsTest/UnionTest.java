package io.github.davidebasile.contractautomatatest.operatorsTest;

import static io.github.davidebasile.contractautomatatest.MSCATest.autEquals;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.operators.UnionFunction;

public class UnionTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);	
	@Test
	public void unionTest() throws Exception {
		List<ModalAutomaton<CALabel>> aut = new ArrayList<>(2);
		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.data"));
		ModalAutomaton<CALabel> union = new UnionFunction().apply(aut);
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"union_BusinessClient_EconomyClient_Hotel.data");
		Assert.assertTrue(autEquals(union,test));
	}
	
	@Test
	public void union_statelabelsnotnumbers() throws Exception {
		List<ModalAutomaton<CALabel>> aut = new ArrayList<>(2);
		aut.add(bdc.importMSCA(dir+"testgraph_testunion.data"));
		aut.add(bdc.importMSCA(dir+"testgraph_testunion.data"));
		ModalAutomaton<CALabel> union = new UnionFunction().apply(aut);
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"union_testgraph_testgraph.data");
		Assert.assertTrue(autEquals(union,test));
	}


	@Test
	public void union_empty() 
	{
		UnionFunction uf = new UnionFunction();
		assertThatThrownBy(()->uf.apply(new ArrayList<ModalAutomaton<CALabel>>()))
		.isInstanceOf(IllegalArgumentException.class);
	}
	

	@Test
	public void union_differentrank_exception() throws Exception {
		List<ModalAutomaton<CALabel>> aut = new ArrayList<>(2);
		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"BusinessClientxHotel_open.data"));

		UnionFunction uf = new UnionFunction();
		assertThatThrownBy(() -> uf.apply(aut))
		.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void union_illegalcharacter() throws Exception {
		List<ModalAutomaton<CALabel>> aut = new ArrayList<>(2);
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"union_testgraph_testgraph.data");		
		aut.add(test);
		aut.add(test);

		UnionFunction uf = new UnionFunction();
		assertThatThrownBy(() -> uf.apply(aut))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Illegal label containing _ in some basic state");
	}

	@Test
	public void union_nullElement() throws Exception {
		List<ModalAutomaton<CALabel>> aut = new ArrayList<>(2);	
		aut.add(null);
		aut.add(null);

		UnionFunction uf = new UnionFunction();
		assertThatThrownBy(() -> uf.apply(aut))
		.isInstanceOf(IllegalArgumentException.class);
	}

}
