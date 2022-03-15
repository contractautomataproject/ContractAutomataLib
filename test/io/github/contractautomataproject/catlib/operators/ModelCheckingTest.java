package io.github.contractautomataproject.catlib.operators;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.ModalAutomatonTest;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.requirements.StrongAgreementModelChecking;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

public class ModelCheckingTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	private final AutDataConverter<Label<List<String>>> adc = new AutDataConverter<>(Label::new);
	public static Automaton<String,String,BasicState<String>,ModalTransition<String,String,BasicState<String>,Label<String>>> prop ;
	
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
	public void testModelCheckingLoopMc() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "modelchecking_loop.data");
		ModalAutomaton<Label<List<String>>> comp = new ModelCheckingFunction(aut, prop,new StrongAgreementModelChecking<Label<List<String>>>()).apply(Integer.MAX_VALUE);
		ModalAutomaton<? extends Label<List<String>>> test = adc.importMSCA(dir + "modelchecking_loop_mc.data");
		
		assertTrue(ModalAutomatonTest.autEquals(comp, test));
	}
	
	@Test
	public void testForte2021mc() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.data");
		ModelCheckingFunction mcf = new ModelCheckingFunction(aut, prop,new StrongAgreementModelChecking<Label<List<String>>>());
		ModalAutomaton<Label<List<String>>> comp = mcf.apply(Integer.MAX_VALUE);
		ModalAutomaton<? extends Label<List<String>>> test = adc.importMSCA(dir+"(AlicexBob)_forte2021_mc.data");

		assertTrue(ModalAutomatonTest.autEquals(comp, test));
	}

	
	@Test
	public void testLazyLoopMc() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "test_lazy_loop_prop.data");		
		ModalAutomaton<Label<List<String>>> comp = new ModelCheckingFunction(aut, prop, new StrongAgreementModelChecking<Label<List<String>>>()).apply(Integer.MAX_VALUE);	
		ModalAutomaton<? extends Label<List<String>>> test = adc.importMSCA(dir + "test_lazy_loop_prop_mc.data");
		
		assertTrue(ModalAutomatonTest.autEquals(comp, test));
		
	}
}
