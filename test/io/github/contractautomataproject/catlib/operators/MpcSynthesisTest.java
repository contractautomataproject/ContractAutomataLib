package io.github.contractautomataproject.catlib.operators;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ITAutomatonTest;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.requirements.Agreement;
import io.github.contractautomataproject.catlib.requirements.StrongAgreementModelChecking;
import io.github.contractautomataproject.catlib.transition.ModalTransition;


public class MpcSynthesisTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);

	@Test
	public void mpcEmptyTestLMCS2020() throws Exception
	{
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedUrgentHotel).data");
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> mpc=new MpcSynthesisOperator(new Agreement()).apply(aut);
		Assert.assertNull(mpc);
	}

	@Test
	public void mpcEmptyTestNoDangling() throws Exception
	{
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"test_empty_mpc_nodangling.data");
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> mpc=new MpcSynthesisOperator(new Agreement()).apply(aut);
		Assert.assertNull(mpc);
	}
	
	@Test
	public void mpcTest_nonempty() throws Exception
	{
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"test_urgent.data");
		assertEquals(2, new MpcSynthesisOperator(new Agreement()).apply(aut).getNumStates());
	}

	@Test 
	public void mpcTest2() throws Exception
	{
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"test_urgent.data");		
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> test = bdc.importMSCA(dir + File.separator + "test_urgent_mpc_agreement.data");
		assertTrue(ITAutomatonTest.autEquals(new MpcSynthesisOperator(new Agreement()).apply(aut),test));
	}
	
	@Test
	public void mpc_lazy_exception() throws Exception
	{

		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> orc = bdc.importMSCA(dir+"test_empty_orc_lazy.data");
		MpcSynthesisOperator m = new MpcSynthesisOperator(new Agreement());
		assertThatThrownBy(() -> m.apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}

	/////mpc synthesis
	
	
	@Test
	public void mpcEmptyTestNoDanglingWithProperty() throws Exception
	{
		BasicState<String> s0 = new BasicState<>("0", true, false);
		BasicState<String> s1 = new BasicState<>("1", false, false);
		BasicState<String> s2 = new BasicState<>("2", false, true);
		State<String> cs0 = new State<>(List.of(s0));
		State<String> cs1 = new State<>(List.of(s1));
		State<String> cs2 = new State<>(List.of(s2));
		ModalTransition<String,String,State<String>,Label<String>> t1 = new ModalTransition<>(cs0, new Label<>(List.of("blueberry")), cs1, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,State<String>,Label<String>> t2 = new ModalTransition<>(cs1, new Label<>(List.of("ananas")), cs2, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,State<String>,Label<String>> t3 = new ModalTransition<>(cs0, new Label<>(List.of("cherry")), cs2, ModalTransition.Modality.PERMITTED);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>> prop = new Automaton<>(Set.of(t1,t2,t3));
		
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"test_empty_mpc_nodangling.data");
		
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> mpc=new MpcSynthesisOperator(new Agreement(), new StrongAgreementModelChecking<>(),prop).apply(aut);
		Assert.assertNull(mpc);
	}

}
