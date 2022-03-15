package io.github.contractautomataproject.catlib.operators;


import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.ModalAutomatonTest;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.family.Product;
import io.github.contractautomataproject.catlib.operators.ProductOrchestrationSynthesisOperator;
import io.github.contractautomataproject.catlib.requirements.Agreement;

public class ProductOrchestrationTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_product4858_transitions() throws Exception
	{		
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		ModalAutomaton<CALabel> test= bdc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient).data");
		Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"cash"});
		Assert.assertTrue(ModalAutomatonTest.autEquals(new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut),test));
	}


	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_empty() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		Product p = new Product(new String[] {"dummy"}, new String[] {""});
		Assert.assertNull(new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut));
	}

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_empty_transitions() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");		
		Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"singleRoom"});
		Assert.assertNull(new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut));
	}	
	
	@Test
	public void testForte2021() throws Exception {
		
		ModalAutomaton<CALabel> aut= bdc.importMSCA(dir+"(AlicexBob)_forte2021.data");		
		Product p = new Product(new String[] {"cherry"}, new String[] {"blueberry"});
		Assert.assertNull(new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut));
		
	}

}
