package io.github.davidebasile.contractautomatatest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.transition.ModalTransition;
import io.github.davidebasile.contractautomata.converters.DataConverter;
import io.github.davidebasile.contractautomata.operators.ChoreographySynthesisOperator;
import io.github.davidebasile.contractautomata.operators.ModelCheckingFunction;
import io.github.davidebasile.contractautomata.operators.OrchestrationSynthesisOperator;
import io.github.davidebasile.contractautomata.requirements.Agreement;
import io.github.davidebasile.contractautomata.requirements.StrongAgreement;
import io.github.davidebasile.contractautomatatest.MSCATest;

public class ModelCheckingTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final DataConverter bdc = new DataConverter();
	private Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>> prop ;
	
	@Before
	public void setup() {
		BasicState s0 = new BasicState("0",true,false);
		BasicState s1 = new BasicState("1",false,false);
		BasicState s2 = new BasicState("2",false,true);
		ModalTransition<String,String,BasicState,Label<String>> t1 = new ModalTransition<>(s0, new Label<String>("blueberry"), s1, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,BasicState,Label<String>> t2 = new ModalTransition<>(s1, new Label<String>("ananas"), s2, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,BasicState,Label<String>> t3 = new ModalTransition<>(s0, new Label<String>("cherry"), s2, ModalTransition.Modality.PERMITTED);
		prop = new Automaton<>(Set.of(t1,t2,t3));
	}
	
	@Test
	public void testForte2021() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.data");

		ModalAutomaton<Label<List<String>>> comp = new ModelCheckingFunction(aut, prop).apply(Integer.MAX_VALUE);
		assertThatThrownBy(() -> ModelCheckingFunction.revertToMSCA(comp))
		.isInstanceOf(UnsupportedOperationException.class);
		
//		ModalAutomaton<CALabel> synth = ModelCheckingFunction.revertToMSCA(comp);
//		ModalAutomaton<CALabel> test = bdc.importMSCA(dir + "(AlicexBob)_forte2021_synth.data");
//		assertTrue(MSCATest.checkTransitions(synth, test));
	}
	
	@Test
	public void testModelCheckingLoop() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "modelchecking_loop.data");
		ModalAutomaton<Label<List<String>>> comp = new ModelCheckingFunction(aut, prop).apply(Integer.MAX_VALUE);
		assertThatThrownBy(() -> ModelCheckingFunction.revertToMSCA(comp))
		.isInstanceOf(UnsupportedOperationException.class);

//		ModalAutomaton<CALabel> synth = ModelCheckingFunction.revertToMSCA(comp);
//		ModalAutomaton<CALabel> test = bdc.importMSCA(dir + "modelchecking_loop_synth.data");
//		assertTrue(MSCATest.checkTransitions(synth, test));
	}
	
	@Test
	public void testOrcSynthesis2021() throws IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.data");
	
		assertThatThrownBy(() -> new OrchestrationSynthesisOperator(new Agreement(),prop).apply(aut))
		.isInstanceOf(UnsupportedOperationException.class);
		
//		ModalAutomaton<CALabel> orc = new OrchestrationSynthesisOperator(new Agreement(),prop).apply(aut);
			
//		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"Orc_(AlicexBob)_forte2021.data");
//		
//		assertTrue(MSCATest.checkTransitions(orc, test));
	}
	
	@Test
	public void testCorSynthesis2021() throws IOException {
		BasicState s0 = new BasicState("0",true,false);
		BasicState s1 = new BasicState("1",false,true);
		BasicState s2 = new BasicState("2",false,true);
		ModalTransition<String,String,BasicState,Label<String>> t1 = new ModalTransition<>(s0, new Label<String>("m"), s1, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,BasicState,Label<String>> t2 = new ModalTransition<>(s0, new Label<String>("m"), s2, ModalTransition.Modality.PERMITTED);
		Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>> prop  = new Automaton<>(Set.of(t1,t2));
		
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "testcor_concur21_Example34.data");
	
		ModalAutomaton<CALabel> cor = new ChoreographySynthesisOperator(new StrongAgreement(),prop).apply(aut);
		
		
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"Cor_(testcor_concur21_Example34)_prop.data");		
		assertTrue(MSCATest.checkTransitions(cor, test));
	}
}
