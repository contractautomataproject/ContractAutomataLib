package io.github.contractautomataproject.catlib.operators;

import static io.github.contractautomataproject.catlib.automaton.ITAutomatonTest.autEquals;
import static io.github.contractautomataproject.catlib.automaton.ITAutomatonTest.counterExample;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.github.contractautomataproject.catlib.automaton.label.action.Action;
import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;

public class UnionTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	@Test
	public void unionTest() throws Exception {
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);
		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.data"));
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> union = new UnionFunction().apply(aut);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir+"union_BusinessClient_EconomyClient_Hotel.data");
		Assert.assertTrue(autEquals(union,test));
	}
	
	@Test
	public void union_statelabelsnotnumbers() throws Exception {
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);
		aut.add(bdc.importMSCA(dir+"testgraph_testunion.data"));
		aut.add(bdc.importMSCA(dir+"testgraph_testunion.data"));
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> union = new UnionFunction().apply(aut);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir+"union_testgraph_testgraph.data");
		Assert.assertTrue(autEquals(union,test));
	}


	@Test
	public void union_empty() 
	{
		UnionFunction uf = new UnionFunction();
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> arg = new ArrayList<>();
		assertThatThrownBy(()->uf.apply(arg))
		.isInstanceOf(IllegalArgumentException.class);
	}
	

	@Test
	public void union_differentrank_exception() throws Exception {
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);
		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"BusinessClientxHotel_open.data"));

		UnionFunction uf = new UnionFunction();
		assertThatThrownBy(() -> uf.apply(aut))
		.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void union_illegalcharacter() throws Exception {
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir+"union_testgraph_testgraph.data");		
		aut.add(test);
		aut.add(test);

		UnionFunction uf = new UnionFunction();
		assertThatThrownBy(() -> uf.apply(aut))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessage("Illegal label containing _ in some basic state");
	}

	@Test
	public void union_nullElement() {
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);	
		aut.add(null);
		aut.add(null);

		UnionFunction uf = new UnionFunction();
		assertThatThrownBy(() -> uf.apply(aut))
		.isInstanceOf(IllegalArgumentException.class);
	}

}
