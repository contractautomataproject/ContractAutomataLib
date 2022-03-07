package io.github.davidebasile.contractautomatatest.operatorsTest;


import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.converters.MSCADataConverter;
import io.github.contractautomataproject.catlib.family.Product;
import io.github.contractautomataproject.catlib.operators.ProductOrchestrationSynthesisOperator;
import io.github.contractautomataproject.catlib.requirements.Agreement;
import io.github.davidebasile.contractautomatatest.MSCATest;

public class ProductOrchestrationTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
//	private final MSCAConverter bmc = new MxeConverter();
	private final MSCADataConverter bdc = new MSCADataConverter();

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_product4858_transitions() throws Exception
	{		
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		ModalAutomaton<CALabel> test= bdc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient).data");
		Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"cash"});
		assertEquals(MSCATest.checkTransitions(
				new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut)
				,test),true);
	}


	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_empty() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		Product p = new Product(new String[] {"dummy"}, new String[] {""});
		assertEquals(new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut),null);
	}

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_empty_transitions() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");		
		Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"singleRoom"});
		assertEquals(new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut),null);
	}	
	
	@Test
	public void testForte2021() throws Exception {
		
		ModalAutomaton<CALabel> aut= bdc.importMSCA(dir+"(AlicexBob)_forte2021.data");		
		Product p = new Product(new String[] {"cherry"}, new String[] {"blueberry"});
		assertEquals(null,new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut));
		
	}

}
