package io.github.contractautomataproject.catlib.operators;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.ModalAutomatonTest;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.requirements.Agreement;
import io.github.contractautomataproject.catlib.requirements.StrongAgreementModelChecking;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

public class OrchestrationTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	private Automaton<String,String,BasicState<String>,ModalTransition<String,String,BasicState<String>,Label<String>>> prop ;
	
	@Before
	public void setup() {
		BasicState<String> s0 = new BasicState<String>("0",true,false);
		BasicState<String> s1 = new BasicState<String>("1",false,false);
		BasicState<String> s2 = new BasicState<String>("2",false,true);
		ModalTransition<String,String,BasicState<String>,Label<String>> t1 = new ModalTransition<String,String,BasicState<String>,Label<String>>(s0, new Label<String>("blueberry"), s1, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,BasicState<String>,Label<String>> t2 = new ModalTransition<>(s1, new Label<String>("ananas"), s2, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,BasicState<String>,Label<String>> t3 = new ModalTransition<>(s0, new Label<String>("cherry"), s2, ModalTransition.Modality.PERMITTED);
		prop = new Automaton<>(Set.of(t1,t2,t3));
	}


	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_transitions() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		ModalAutomaton<CALabel> test= bdc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient).data");
		assertTrue(ModalAutomatonTest.autEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(aut),test));
	}	
	
	@Test
	public void orcTestSCP2020_BusinessClientxHotel_transitions() throws IOException {
		ModalAutomaton<CALabel> comp = bdc.importMSCA(dir+"BusinessClientxHotel_open.data");
		ModalAutomaton<CALabel> orc = new OrchestrationSynthesisOperator(new Agreement()).apply(comp);
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"Orc_BusinessClientxHotel.data");
		assertTrue(ModalAutomatonTest.autEquals(orc,test));
	}
	
	@Test
	public void orcTestLMCS2020Transitions() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedHotel).data");
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"Orc_(ClientxClientxBrokerxHotelxPriviledgedHotel).data");
		assertTrue(ModalAutomatonTest.autEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(aut),test));
	}
	
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
		assertTrue(ModalAutomatonTest.autEquals(orc, bdc.importMSCA(dir+"test_lazy_loop_orc.data")));
	}

	//----with MC
	
	@Test
	public void testForte2021synth() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.data");
		ModalAutomaton<CALabel> synth = new OrchestrationSynthesisOperator(new Agreement(),new StrongAgreementModelChecking<Label<List<String>>>(),prop).apply(aut);
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir + "(AlicexBob)_forte2021_synth.data");
		assertTrue(ModalAutomatonTest.autEquals(synth, test));
	}
	
	@Test
	public void testLazyLoopSynth() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "test_lazy_loop_prop.data");		
		ModalAutomaton<CALabel> synth = new OrchestrationSynthesisOperator(new Agreement(),new StrongAgreementModelChecking<Label<List<String>>>(),prop).apply(aut);	
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir + "test_lazy_loop_prop_synth.data");
		assertTrue(ModalAutomatonTest.autEquals(synth, test));	
	}

	@Test
	public void testModelCheckingLoopSynt() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "modelchecking_loop.data");
		ModalAutomaton<CALabel> orc = new OrchestrationSynthesisOperator(new Agreement(),new StrongAgreementModelChecking<Label<List<String>>>(),prop).apply(aut);
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir + "modelchecking_loop_synth.data");
		assertTrue(ModalAutomatonTest.autEquals(orc, test));

	}
	
	@Test
	public void testOrcSynthesis2021() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.data");	
		ModalAutomaton<CALabel> orc = new OrchestrationSynthesisOperator(new Agreement(),new StrongAgreementModelChecking<Label<List<String>>>(),prop).apply(aut);
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"Orc_(AlicexBob)_forte2021.data");
		assertTrue(ModalAutomatonTest.autEquals(orc, test));
	}
	
	//---------------------------

	@Test
	public void orc_necessaryoffer_exception() throws Exception
	{
		ModalAutomaton<CALabel> orc = bdc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).data");
		OrchestrationSynthesisOperator os = new OrchestrationSynthesisOperator(new Agreement());
		assertThatThrownBy(() -> os.apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}
}




//@Test
//public void orcTest_nonempty() throws Exception
//{
//
//	MSCA orc = bdc.importMSCA(dir+"test_empty_orc_lazy.data");
//	assertEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(orc),null);
//}

//@Test
//public void orcTestLMCS2020Transitions_new() throws Exception
//{
//	MSCA aut = bmc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");
//	MSCA test = bmc.importMSCA(dir+"Orc_(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");
//	assertEquals(MSCATest.checkTransitions(new OrchestrationSynthesisOperator(new Agreement()).apply(aut),test),true);
//}
//
