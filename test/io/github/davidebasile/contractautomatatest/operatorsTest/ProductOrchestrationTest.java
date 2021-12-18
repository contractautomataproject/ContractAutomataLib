package io.github.davidebasile.contractautomatatest.operatorsTest;


import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.converters.DataConverter;
import io.github.davidebasile.contractautomata.converters.MSCAConverter;
import io.github.davidebasile.contractautomata.family.Product;
import io.github.davidebasile.contractautomata.operators.ProductOrchestrationSynthesisOperator;
import io.github.davidebasile.contractautomata.requirements.Agreement;
import io.github.davidebasile.contractautomatatest.MSCATest;

public class ProductOrchestrationTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
//	private final MSCAConverter bmc = new MxeConverter();
	private final MSCAConverter bdc = new DataConverter();

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_product4858_transitions() throws Exception
	{		
		MSCA aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		MSCA test= bdc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient).data");
		Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"cash"});
		assertEquals(MSCATest.checkTransitions(
				new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut)
				,test),true);
	}


	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_empty() throws Exception
	{
		MSCA aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		Product p = new Product(new String[] {"dummy"}, new String[] {""});
		assertEquals(new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut),null);
	}

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_empty_transitions() throws Exception
	{
		MSCA aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");		
		Product p = new Product(new String[] {"card","sharedBathroom"}, new String[] {"singleRoom"});
		assertEquals(new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut),null);
	}	
	
	@Test
	public void testForte2021() throws Exception {
		
		MSCA aut= bdc.importMSCA(dir+"(AlicexBob)_forte2021.data");		
		Product p = new Product(new String[] {"cherry"}, new String[] {"blueberry"});
		assertEquals(null,new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut));
		
	}

}
