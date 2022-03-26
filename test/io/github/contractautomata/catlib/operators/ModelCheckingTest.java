package io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.ITAutomatonTest;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.requirements.StrongAgreementModelChecking;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertTrue;

public class ModelCheckingTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	private final AutDataConverter<Label<Action>> adc = new AutDataConverter<>(Label::new);
	public static Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,Label<Action>>> prop ;
	
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
	public void testModelCheckingLoopMc() throws IOException {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir + "modelchecking_loop.data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,Label<Action>>> comp = new ModelCheckingFunction<>(aut, prop, new StrongAgreementModelChecking<>().negate()).apply(Integer.MAX_VALUE);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,Label<Action>>> test = adc.importMSCA(dir + "modelchecking_loop_mc.data");
		assertTrue(ITAutomatonTest.autEquals(comp, test));
	}
	
	@Test
	public void testForte2021mc() throws IOException {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.data");
		ModelCheckingFunction<String> mcf = new ModelCheckingFunction<>(aut, prop, new StrongAgreementModelChecking<>().negate());
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,Label<Action>>> comp = mcf.apply(Integer.MAX_VALUE);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,Label<Action>>> test = adc.importMSCA(dir+"(AlicexBob)_forte2021_mc.data");

		assertTrue(ITAutomatonTest.autEquals(comp, test));
	}

	
	@Test
	public void testLazyLoopMc() throws IOException {
		Automaton<String,Action,State<String>,ModalTransition<String, Action,State<String>,CALabel>> aut = bdc.importMSCA(dir + "test_lazy_loop_prop.data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,Label<Action>>> comp = new ModelCheckingFunction<>(aut, prop, new StrongAgreementModelChecking<>().negate()).apply(Integer.MAX_VALUE);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,Label<Action>>> test = adc.importMSCA(dir + "test_lazy_loop_prop_mc.data");
		
		assertTrue(ITAutomatonTest.autEquals(comp, test));
		
	}
}
