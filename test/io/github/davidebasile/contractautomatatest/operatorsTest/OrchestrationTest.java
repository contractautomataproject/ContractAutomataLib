package io.github.davidebasile.contractautomatatest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.operators.OrchestrationSynthesisOperator;
import io.github.contractautomataproject.catlib.requirements.Agreement;
import io.github.davidebasile.contractautomatatest.MSCATest;

public class OrchestrationTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_transitions() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		ModalAutomaton<CALabel> test= bdc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient).data");
		assertTrue(MSCATest.autEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(aut),test));
	}	
	
	@Test
	public void orcTestSCP2020_BusinessClientxHotel_transitions() throws IOException {
		ModalAutomaton<CALabel> comp = bdc.importMSCA(dir+"BusinessClientxHotel_open.data");
		ModalAutomaton<CALabel> orc = new OrchestrationSynthesisOperator(new Agreement()).apply(comp);
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"Orc_BusinessClientxHotel.data");
		assertTrue(MSCATest.autEquals(orc,test));
	}
	
	@Test
	public void orcTestLMCS2020Transitions() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedHotel).data");
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"Orc_(ClientxClientxBrokerxHotelxPriviledgedHotel).data");
		assertTrue(MSCATest.autEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(aut),test));
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
		Assert.assertNull(new OrchestrationSynthesisOperator(new Agreement()).apply(aut));
	}
		
	@Test
	public void orcTest_empty() throws Exception
	{
		ModalAutomaton<CALabel> orc = bdc.importMSCA(dir+"test_empty_orc.data");
		Assert.assertNull(new OrchestrationSynthesisOperator(new Agreement()).apply(orc));
	}

	@Test
	public void orcTest_empty_lazy() throws Exception
	{

		ModalAutomaton<CALabel> orc = bdc.importMSCA(dir+"test_empty_orc_lazy.data");
		Assert.assertNull(new OrchestrationSynthesisOperator(new Agreement()).apply(orc));
	}
	
	@Test
	public void orcTest_lazyloop() throws Exception 
	{
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"test_lazy_loop.data");
		ModalAutomaton<CALabel> orc =  new OrchestrationSynthesisOperator(new Agreement()).apply(test);
		assertTrue(MSCATest.autEquals(orc, bdc.importMSCA(dir+"test_lazy_loop_orc.data")));
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
		OrchestrationSynthesisOperator os = new OrchestrationSynthesisOperator(new Agreement());
		assertThatThrownBy(() -> os.apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}
}
