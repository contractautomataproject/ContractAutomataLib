package io.github.davidebasile.contractautomatatest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.converters.MSCADataConverter;
import io.github.davidebasile.contractautomata.operators.OrchestrationSynthesisOperator;
import io.github.davidebasile.contractautomata.requirements.Agreement;
import io.github.davidebasile.contractautomatatest.MSCATest;

public class OrchestrationTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
//	private final MSCAConverter bmc = new MxeConverter();
	private final MSCADataConverter bdc = new MSCADataConverter();

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_transitions() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		ModalAutomaton<CALabel> test= bdc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient).data");
		assertEquals(MSCATest.checkTransitions(new OrchestrationSynthesisOperator(new Agreement()).apply(aut),test),true);
	}	
	
	@Test
	public void orcTestLMCS2020Transitions() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedHotel).data");
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"Orc_(ClientxClientxBrokerxHotelxPriviledgedHotel).data");
		assertEquals(MSCATest.checkTransitions(new OrchestrationSynthesisOperator(new Agreement()).apply(aut),test),true);
	}

//	@Test
//	public void orcTestLMCS2020Transitions_new() throws Exception
//	{
//		MSCA aut = bmc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");
//		MSCA test = bmc.importMSCA(dir+"Orc_(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");
//		assertEquals(MSCATest.checkTransitions(new OrchestrationSynthesisOperator(new Agreement()).apply(aut),test),true);
//	}
//	
	
	@Test
	public void orcEmptyTestNoDangling() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_empty_orc_nodangling.data");
		assertEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(aut),null);
	}
		
	@Test
	public void orcTest_empty() throws Exception
	{
		ModalAutomaton<CALabel> orc = bdc.importMSCA(dir+"test_empty_orc.data");
		assertEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(orc),null);
	}

	@Test
	public void orcTest_empty_lazy() throws Exception
	{

		ModalAutomaton<CALabel> orc = bdc.importMSCA(dir+"test_empty_orc_lazy.data");
		assertEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(orc),null);
	}
	
	@Test
	public void orcTest_lazyloop() throws Exception 
	{
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"test_lazy_loop.data");
		ModalAutomaton<CALabel> orc =  new OrchestrationSynthesisOperator(new Agreement()).apply(test);
		assertTrue(MSCATest.checkTransitions(orc, bdc.importMSCA(dir+"test_lazy_loop_orc.data")));
	}

//	@Test
//	public void orcTest_nonempty() throws Exception
//	{
//
//		MSCA orc = bdc.importMSCA(dir+"test_empty_orc_lazy.data");
//		assertEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(orc),null);
//	}

	@Test
	public void orc_necessaryoffer_exception() throws Exception
	{
		ModalAutomaton<CALabel> orc = bdc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).data");
		assertThatThrownBy(() -> new OrchestrationSynthesisOperator(new Agreement()).apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}
}
