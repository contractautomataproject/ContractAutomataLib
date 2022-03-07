package io.github.davidebasile.contractautomatatest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.converters.MSCADataConverter;
import io.github.davidebasile.contractautomata.operators.MpcSynthesisOperator;
import io.github.davidebasile.contractautomata.requirements.Agreement;
import io.github.davidebasile.contractautomatatest.MSCATest;


public class MpcSynthesisTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final MSCADataConverter bdc = new MSCADataConverter();

	@Test
	public void mpcEmptyTestLMCS2020() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedUrgentHotel).data");
		ModalAutomaton<CALabel> mpc=new MpcSynthesisOperator(new Agreement()).apply(aut);

		assertEquals(mpc,null);
	}

	@Test
	public void mpcEmptyTestNoDangling() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_empty_mpc_nodangling.data");
		ModalAutomaton<CALabel> mpc=new MpcSynthesisOperator(new Agreement()).apply(aut);

		assertEquals(mpc,null);
	}
	
	@Test
	public void mpcTest_nonempty() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_urgent.data");
		assertEquals(new MpcSynthesisOperator(new Agreement()).apply(aut).getNumStates(),2);
	}

	@Test 
	public void mpcTest2() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_urgent.data");		
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir + File.separator + "test_urgent_mpc_agreement.data");
		assertTrue(MSCATest.checkTransitions(new MpcSynthesisOperator(new Agreement()).apply(aut),
				test));	
	}
	
	@Test
	public void mpc_lazy_exception() throws Exception
	{

		ModalAutomaton<CALabel> orc = bdc.importMSCA(dir+"test_empty_orc_lazy.data");
		assertThatThrownBy(() -> new MpcSynthesisOperator(new Agreement()).apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}


}
