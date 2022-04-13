package io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.AutomatonTest;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.requirements.Agreement;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class OrchestrationTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test"+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	private Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,Label<Action>>> prop ;
	
	@Before
	public void setup() {
		BasicState<String> s0 = new BasicState<>("0", true, false);
		BasicState<String> s1 = new BasicState<>("1", false, false);
		BasicState<String> s2 = new BasicState<>("2", false, true);
		State<String> cs0 = new State<>(List.of(s0));
		State<String> cs1 = new State<>(List.of(s1));
		State<String> cs2 = new State<>(List.of(s2));
		ModalTransition<String,Action,State<String>,Label<Action>> t1 = new ModalTransition<>(cs0, new Label<>(List.of(new Action("blueberry"))), cs1, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,Action,State<String>,Label<Action>> t2 = new ModalTransition<>(cs1, new Label<>(List.of(new Action("ananas"))), cs2, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,Action,State<String>,Label<Action>> t3 = new ModalTransition<>(cs0, new Label<>(List.of(new Action("cherry"))), cs2, ModalTransition.Modality.PERMITTED);
		prop = new Automaton<>(Set.of(t1,t2,t3));
	}


	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_transitions() throws Exception
	{
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test= bdc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient).data");
		assertTrue(AutomatonTest.autEquals(new OrchestrationSynthesisOperator<String>(new Agreement()).apply(aut),test));
	}	
	
	@Test
	public void orcTestSCP2020_BusinessClientxHotel_transitions() throws IOException {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp = bdc.importMSCA(dir+"BusinessClientxHotel_open.data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = new OrchestrationSynthesisOperator<String>(new Agreement()).apply(comp);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir+"Orc_BusinessClientxHotel.data");
		assertTrue(AutomatonTest.autEquals(orc,test));
	}
	
	@Test
	public void orcTestLMCS2020Transitions() throws Exception
	{
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedHotel).data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir+"Orc_(ClientxClientxBrokerxHotelxPriviledgedHotel).data");
		assertTrue(AutomatonTest.autEquals(new OrchestrationSynthesisOperator<String>(new Agreement()).apply(aut),test));
	}
	
	@Test
	public void orcEmptyTestNoDangling() throws Exception
	{
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir+"test_empty_orc_nodangling.data");
		Assert.assertNull(new OrchestrationSynthesisOperator<String>(new Agreement()).apply(aut));
	}
		
	@Test
	public void orcTest_empty() throws Exception
	{
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = bdc.importMSCA(dir+"test_empty_orc.data");
		Assert.assertNull(new OrchestrationSynthesisOperator<String>(new Agreement()).apply(orc));
	}

	@Test
	public void orcTest_empty_lazy() throws Exception
	{

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = bdc.importMSCA(dir+"test_empty_orc_lazy.data");
		Assert.assertNull(new OrchestrationSynthesisOperator<String>(new Agreement()).apply(orc));
	}
	
	@Test
	public void orcTest_lazyloop() throws Exception 
	{
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir+"test_lazy_loop.data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc =  new OrchestrationSynthesisOperator<String>(new Agreement()).apply(test);
		assertTrue(AutomatonTest.autEquals(orc, bdc.importMSCA(dir+"test_lazy_loop_orc.data")));
	}

	//----with MC
	
	@Test
	public void testForte2021synth() throws IOException {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> synth = new OrchestrationSynthesisOperator<>(new Agreement(),prop).apply(aut);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir + "(AlicexBob)_forte2021_synth.data");
		assertTrue(AutomatonTest.autEquals(synth, test));
	}
	
	@Test
	public void testLazyLoopSynth() throws IOException {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir + "test_lazy_loop_prop.data");		
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> synth = new OrchestrationSynthesisOperator<>(new Agreement(),prop).apply(aut);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir + "test_lazy_loop_prop_synth.data");

		assertTrue(AutomatonTest.autEquals(synth, test));
	}

	@Test
	public void testModelCheckingLoopSynt() throws IOException {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir + "modelchecking_loop.data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = new OrchestrationSynthesisOperator<>(new Agreement(),prop).apply(aut);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir + "modelchecking_loop_synth.data");
		assertTrue(AutomatonTest.autEquals(orc, test));

	}
	
	@Test
	public void testOrcSynthesis2021() throws IOException {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.data");	
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = new OrchestrationSynthesisOperator<>(new Agreement(),prop).apply(aut);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(dir+"Orc_(AlicexBob)_forte2021.data");
		assertTrue(AutomatonTest.autEquals(orc, test));
	}
	
	//---------------------------

	@Test
	public void orc_necessaryoffer_exception() throws Exception
	{
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = bdc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).data");
		OrchestrationSynthesisOperator<String> os = new OrchestrationSynthesisOperator<>(new Agreement());
		assertThrows(UnsupportedOperationException.class, () -> os.apply(orc));
	}
}




//@Test
//public void orcTest_nonempty() throws Exception
//{
//
//	MSCA orc = bdc.importMSCA(dir+"test_empty_orc_lazy.data");
//	assertEquals(new OrchestrationSynthesisOperator<>(new Agreement()).apply(orc),null);
//}

//@Test
//public void orcTestLMCS2020Transitions_new() throws Exception
//{
//	MSCA aut = bmc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");
//	MSCA test = bmc.importMSCA(dir+"Orc_(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");
//	assertEquals(MSCATest.checkTransitions(new OrchestrationSynthesisOperator<>(new Agreement()).apply(aut),test),true);
//}
//
