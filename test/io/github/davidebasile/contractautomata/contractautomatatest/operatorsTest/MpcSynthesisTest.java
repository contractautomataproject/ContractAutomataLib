package io.github.davidebasile.contractautomata.contractautomatatest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Set;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.transition.Transition;
import io.github.davidebasile.contractautomata.converters.MxeConverter;
import io.github.davidebasile.contractautomata.operators.MpcSynthesisOperator;
import io.github.davidebasile.contractautomata.requirements.Agreement;
import io.github.davidebasile.contractautomatatest.MSCATest;


public class MpcSynthesisTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final MxeConverter bmc = new MxeConverter();
//	private final DataConverter bdc = new DataConverter();

	@Test
	public void mpcEmptyTestLMCS2020() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedUrgentHotel).mxe");
		MSCA mpc=new MpcSynthesisOperator(new Agreement()).apply(aut);

		assertEquals(mpc,null);
	}

	@Test
	public void mpcEmptyTestNoDangling() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"test_empty_mpc_nodangling.mxe");
		MSCA mpc=new MpcSynthesisOperator(new Agreement()).apply(aut);

		assertEquals(mpc,null);
	}

	@Test
	public void mpcEmptyTestNoDanglingWithProperty() throws Exception
	{
		BasicState s0 = new BasicState("0",true,false);
		BasicState s1 = new BasicState("1",false,false);
		BasicState s2 = new BasicState("2",false,true);
		Transition<String, BasicState,Label> t1 = new Transition<>(s0, new Label("blueberry"), s1);
		Transition<String, BasicState,Label> t2 = new Transition<>(s1, new Label("ananas"), s2);
		Transition<String, BasicState,Label> t3 = new Transition<>(s0, new Label("cherry"), s2);
		Automaton<String, BasicState,Transition<String, BasicState,Label>> prop = new Automaton<>(Set.of(t1,t2,t3));

		MSCA aut = bmc.importMSCA(dir+"test_empty_mpc_nodangling.mxe");
		MSCA mpc=new MpcSynthesisOperator(new Agreement(),prop).apply(aut);

		assertEquals(mpc,null);
	}
	@Test
	public void mpcTest_nonempty() throws Exception
	{

		MSCA aut = bmc.importMSCA(dir+"test_urgent.mxe");
		assertEquals(new MpcSynthesisOperator(new Agreement()).apply(aut).getNumStates(),2);
	}

	@Test 
	public void mpcTest2() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"test_urgent.mxe");
		//bmc.exportMSCA(dir+ File.separator + "test_urgent_mpc_agreement", new MpcSynthesisOperator(new Agreement()).apply(aut));
		
		MSCA test = bmc.importMSCA(dir + File.separator + "test_urgent_mpc_agreement.mxe");
		assertTrue(MSCATest.checkTransitions(new MpcSynthesisOperator(new Agreement()).apply(aut),
				test));	
	}
	
	@Test
	public void mpc_lazy_exception() throws Exception
	{

		MSCA orc = bmc.importMSCA(dir+"test_empty_orc_lazy.mxe");
		assertThatThrownBy(() -> new MpcSynthesisOperator(new Agreement()).apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}


}
