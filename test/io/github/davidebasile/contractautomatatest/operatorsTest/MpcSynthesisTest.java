package io.github.davidebasile.contractautomatatest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Set;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.transition.ModalTransition;
import io.github.davidebasile.contractautomata.converters.DataConverter;
import io.github.davidebasile.contractautomata.converters.MSCAConverter;
import io.github.davidebasile.contractautomata.operators.MpcSynthesisOperator;
import io.github.davidebasile.contractautomata.requirements.Agreement;
import io.github.davidebasile.contractautomatatest.MSCATest;


public class MpcSynthesisTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final MSCAConverter bdc = new DataConverter();

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
	public void mpcEmptyTestNoDanglingWithProperty() throws Exception
	{
		BasicState s0 = new BasicState("0",true,false);
		BasicState s1 = new BasicState("1",false,false);
		BasicState s2 = new BasicState("2",false,true);
		ModalTransition<String,String,BasicState,Label<String>> t1 = new ModalTransition<>(s0, new Label<String>("blueberry"), s1, ModalTransition.Modality.URGENT);
		ModalTransition<String,String,BasicState,Label<String>> t2 = new ModalTransition<>(s1, new Label<String>("ananas"), s2, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,BasicState,Label<String>> t3 = new ModalTransition<>(s0, new Label<String>("cherry"), s2, ModalTransition.Modality.PERMITTED);
		
		Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>> prop = new Automaton<>(Set.of(t1,t2,t3));;

		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_empty_mpc_nodangling.data");
		
		ModalAutomaton<CALabel> mpc=new MpcSynthesisOperator(new Agreement(),prop).apply(aut);
		assertTrue(mpc==null);
		
//		assertThatThrownBy(() -> new MpcSynthesisOperator(new Agreement(),prop).apply(aut))
//		.isInstanceOf(IllegalArgumentException.class);
//		.hasMessage("No transitions");

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
