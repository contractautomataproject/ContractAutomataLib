package io.github.davidebasile.contractautomatatest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.converters.MxeConverter;
import io.github.davidebasile.contractautomata.operators.OrchestrationSynthesisOperator;
import io.github.davidebasile.contractautomata.requirements.Agreement;
import io.github.davidebasile.contractautomatatest.MSCATest;

public class OrchestrationTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final MxeConverter bmc = new MxeConverter();
//	private final DataConverter bdc = new DataConverter();

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_transitions() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).mxe");
		MSCA test= bmc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient)_test.mxe");
		assertEquals(MSCATest.checkTransitions(new OrchestrationSynthesisOperator(new Agreement()).apply(aut),test),true);
	}	
	
	@Test
	public void orcTestLMCS2020Transitions() throws Exception
	{


		MSCA aut = bmc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");
		MSCA test = bmc.importMSCA(dir+"Orc_(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");

		assertEquals(MSCATest.checkTransitions(new OrchestrationSynthesisOperator(new Agreement()).apply(aut),test),true);
	}

	@Test
	public void orcTestLMCS2020Transitions_new() throws Exception
	{


		MSCA aut = bmc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");
		MSCA test = bmc.importMSCA(dir+"Orc_(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");

		assertEquals(MSCATest.checkTransitions(new OrchestrationSynthesisOperator(new Agreement()).apply(aut),test),true);
	}
	
	
	@Test
	public void orcEmptyTestNoDangling() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"test_empty_orc_nodangling.mxe");
		assertEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(aut),null);
	}
	
	
	@Test
	public void orcTest_empty() throws Exception
	{

		MSCA orc = bmc.importMSCA(dir+"test_empty_orc.mxe");
		assertEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(orc),null);
	}

	@Test
	public void orcTest_empty_lazy() throws Exception
	{

		MSCA orc = bmc.importMSCA(dir+"test_empty_orc_lazy.mxe");
		assertEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(orc),null);
	}

	@Test
	public void orcTest_nonempty() throws Exception
	{

		MSCA orc = bmc.importMSCA(dir+"test_empty_orc_lazy.mxe");
		assertEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(orc),null);
	}

	@Test
	public void orc_necessaryoffer_exception() throws Exception
	{
		MSCA orc = bmc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		assertThatThrownBy(() -> new OrchestrationSynthesisOperator(new Agreement()).apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}



}
