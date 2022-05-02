package it.io.github.contractautomata.catlib.operators;


import it.io.github.contractautomata.catlib.automaton.ITAutomatonTest;
import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.AutomatonTest;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.family.Product;
import io.github.contractautomata.catlib.operations.ProductOrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.requirements.Agreement;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import org.junit.Assert;
import org.junit.Test;

public class ITProductOrchestrationTest {
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_product4858_transitions() throws Exception
	{		
		Automaton<String,Action, State<String>, ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(ITAutomatonTest.dir+ "(BusinessClientxHotelxEconomyClient).data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test= bdc.importMSCA(ITAutomatonTest.dir+ "Orc_(BusinessClientxHotelxEconomyClient).data");
		Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"cash"});
		Assert.assertTrue(AutomatonTest.autEquals(new ProductOrchestrationSynthesisOperator<String>(new Agreement(),p).apply(aut),test));
	}


	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_empty() throws Exception
	{
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(ITAutomatonTest.dir+ "(BusinessClientxHotelxEconomyClient).data");
		Product p = new Product(new String[] {"dummy"}, new String[] {""});
		Assert.assertNull(new ProductOrchestrationSynthesisOperator<String>(new Agreement(),p).apply(aut));
	}

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_empty_transitions() throws Exception
	{
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(ITAutomatonTest.dir+ "(BusinessClientxHotelxEconomyClient).data");
		Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"singleRoom"});
		Assert.assertNull(new ProductOrchestrationSynthesisOperator<String>(new Agreement(),p).apply(aut));
	}	
	
	@Test
	public void testForte2021() throws Exception {
		
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut= bdc.importMSCA(ITAutomatonTest.dir+ "(AlicexBob)_forte2021.data");
		Product p = new Product(new String[] {"cherry"}, new String[] {"blueberry"});
		Assert.assertNull(new ProductOrchestrationSynthesisOperator<String>(new Agreement(),p).apply(aut));
		
	}

}
