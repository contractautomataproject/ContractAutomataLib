package io.github.contractautomataproject.catlib.operators;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.ModalAutomatonTest;
import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.operators.MpcSynthesisOperator;
import io.github.contractautomataproject.catlib.requirements.Agreement;
import io.github.contractautomataproject.catlib.requirements.StrongAgreementModelChecking;
import io.github.contractautomataproject.catlib.transition.ModalTransition;


public class MpcSynthesisTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);

	@Test
	public void mpcEmptyTestLMCS2020() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedUrgentHotel).data");
		ModalAutomaton<CALabel> mpc=new MpcSynthesisOperator(new Agreement()).apply(aut);
		Assert.assertNull(mpc);
	}

	@Test
	public void mpcEmptyTestNoDangling() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_empty_mpc_nodangling.data");
		ModalAutomaton<CALabel> mpc=new MpcSynthesisOperator(new Agreement()).apply(aut);
		Assert.assertNull(mpc);
	}
	
	@Test
	public void mpcTest_nonempty() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_urgent.data");
		assertEquals(2, new MpcSynthesisOperator(new Agreement()).apply(aut).getNumStates());
	}

	@Test 
	public void mpcTest2() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_urgent.data");		
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir + File.separator + "test_urgent_mpc_agreement.data");
		assertTrue(ModalAutomatonTest.autEquals(new MpcSynthesisOperator(new Agreement()).apply(aut),test));	
	}
	
	@Test
	public void mpc_lazy_exception() throws Exception
	{

		ModalAutomaton<CALabel> orc = bdc.importMSCA(dir+"test_empty_orc_lazy.data");
		MpcSynthesisOperator m = new MpcSynthesisOperator(new Agreement());
		assertThatThrownBy(() -> m.apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}

	/////mpc synthesis
	
	
	@Test
	public void mpcEmptyTestNoDanglingWithProperty() throws Exception
	{
		BasicState<String> s0 = new BasicState<String>("0",true,false);
		BasicState<String> s1 = new BasicState<String>("1",false,false);
		BasicState<String> s2 = new BasicState<String>("2",false,true);
		ModalTransition<String,String,BasicState<String>,Label<String>> t1 = new ModalTransition<>(s0, new Label<String>("blueberry"), s1, ModalTransition.Modality.URGENT);
		ModalTransition<String,String,BasicState<String>,Label<String>> t2 = new ModalTransition<>(s1, new Label<String>("ananas"), s2, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,BasicState<String>,Label<String>> t3 = new ModalTransition<>(s0, new Label<String>("cherry"), s2, ModalTransition.Modality.PERMITTED);
		
		Automaton<String,String,BasicState<String>,ModalTransition<String,String,BasicState<String>,Label<String>>> prop = new Automaton<>(Set.of(t1,t2,t3));;

		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_empty_mpc_nodangling.data");
		
		ModalAutomaton<CALabel> mpc=new MpcSynthesisOperator(new Agreement(),new StrongAgreementModelChecking<Label<List<String>>>(),prop).apply(aut);
		Assert.assertNull(mpc);
	}

}
