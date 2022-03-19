package io.github.contractautomataproject.catlib.operators;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ITAutomatonTest;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.requirements.StrongAgreementModelChecking;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;

public class ModelCheckingTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	private final AutDataConverter<Label<String>> adc = new AutDataConverter<>(Label::new);
	public static Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>> prop ;
	
	@Before
	public void setup() {
		BasicState<String> s0 = new BasicState<>("0", true, false);
		BasicState<String> s1 = new BasicState<>("1", false, false);
		BasicState<String> s2 = new BasicState<>("2", false, true);
		State<String> cs0 = new State<>(List.of(s0));
		State<String> cs1 = new State<>(List.of(s1));
		State<String> cs2 = new State<>(List.of(s2));
		ModalTransition<String,String,State<String>,Label<String>> t1 = new ModalTransition<>(cs0, new Label<>(List.of("blueberry")), cs1, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,State<String>,Label<String>> t2 = new ModalTransition<>(cs1, new Label<>(List.of("ananas")), cs2, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,State<String>,Label<String>> t3 = new ModalTransition<>(cs0, new Label<>(List.of("cherry")), cs2, ModalTransition.Modality.PERMITTED);
		prop = new Automaton<>(Set.of(t1,t2,t3));
	}
	
	@Test
	public void testModelCheckingLoopMc() throws IOException {
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir + "modelchecking_loop.data");
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>> comp = new ModelCheckingFunction(aut, prop, new StrongAgreementModelChecking<>()).apply(Integer.MAX_VALUE);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>> test = adc.importMSCA(dir + "modelchecking_loop_mc.data");
		
		assertTrue(ITAutomatonTest.autEquals(comp, test));
	}
	
	@Test
	public void testForte2021mc() throws IOException {
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.data");
		ModelCheckingFunction mcf = new ModelCheckingFunction(aut, prop, new StrongAgreementModelChecking<>());
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>> comp = mcf.apply(Integer.MAX_VALUE);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>> test = adc.importMSCA(dir+"(AlicexBob)_forte2021_mc.data");

		assertTrue(ITAutomatonTest.autEquals(comp, test));
	}

	
	@Test
	public void testLazyLoopMc() throws IOException {
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir + "test_lazy_loop_prop.data");		
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>> comp = new ModelCheckingFunction(aut, prop, new StrongAgreementModelChecking<>()).apply(Integer.MAX_VALUE);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>> test = adc.importMSCA(dir + "test_lazy_loop_prop_mc.data");
		
		assertTrue(ITAutomatonTest.autEquals(comp, test));
		
	}
}
