package it.io.github.contractautomata.catlib.operators;

import it.io.github.contractautomata.catlib.automaton.ITAutomatonTest;
import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.AutomatonTest;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.operations.UnionFunction;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertThrows;

public class ITUnionTest {

	final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	@Test
	public void unionTest() throws Exception {
		List<Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);
		aut.add(bdc.importMSCA(ITAutomatonTest.dir+ "BusinessClient.data"));
		aut.add(bdc.importMSCA(ITAutomatonTest.dir+ "EconomyClient.data"));
		aut.add(bdc.importMSCA(ITAutomatonTest.dir+ "Hotel.data"));
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> union = new UnionFunction().apply(aut);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(ITAutomatonTest.dir+ "union_BusinessClient_EconomyClient_Hotel.data");
		Assert.assertTrue(AutomatonTest.autEquals(union,test));
	}
	
	@Test
	public void union_statelabelsnotnumbers() throws Exception {
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);
		aut.add(bdc.importMSCA(ITAutomatonTest.dir+ "testgraph_testunion.data"));
		aut.add(bdc.importMSCA(ITAutomatonTest.dir+ "testgraph_testunion.data"));
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> union = new UnionFunction().apply(aut);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(ITAutomatonTest.dir+ "union_testgraph_testgraph.data");
		Assert.assertTrue(AutomatonTest.autEquals(union,test));
	}



	

	@Test
	public void union_differentrank_exception() throws Exception {
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);
		aut.add(bdc.importMSCA(ITAutomatonTest.dir+ "BusinessClient.data"));
		aut.add(bdc.importMSCA(ITAutomatonTest.dir+ "BusinessClientxHotel_open.data"));

		UnionFunction uf = new UnionFunction();
		assertThrows(IllegalArgumentException.class, ()->uf.apply(aut));
	}

	@Test
	public void union_illegalcharacter() throws Exception {
		List<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> aut = new ArrayList<>(2);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(ITAutomatonTest.dir+ "union_testgraph_testgraph.data");
		aut.add(test);
		aut.add(test);

		UnionFunction uf = new UnionFunction();
		assertThrows("Illegal label containing _ in some basic state",IllegalArgumentException.class, ()->uf.apply(aut));

	}


}
