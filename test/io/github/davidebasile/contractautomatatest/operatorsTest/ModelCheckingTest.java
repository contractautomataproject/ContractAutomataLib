package io.github.davidebasile.contractautomatatest.operatorsTest;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.Transition;
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
	private Automaton<String,String,BasicState,Transition<String,String,BasicState,Label<String>>> prop ;
	
	@Before
	public void setup() {
		BasicState s0 = new BasicState("0",true,false);
		BasicState s1 = new BasicState("1",false,false);
		BasicState s2 = new BasicState("2",false,true);
		Transition<String,String,BasicState,Label<String>> t1 = new Transition<>(s0, new Label<String>("blueberry"), s1);
		Transition<String,String,BasicState,Label<String>> t2 = new Transition<>(s1, new Label<String>("ananas"), s2);
		Transition<String,String,BasicState,Label<String>> t3 = new Transition<>(s0, new Label<String>("cherry"), s2);
		prop = new Automaton<>(Set.of(t1,t2,t3));
	}
	
	@Test
	public void testForte2021() throws IOException {
		MSCA aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.data");
//		System.out.println(aut);
//		System.out.println(prop);
		Set<CAState> states = new ModelCheckingFunction(100).apply(aut, prop);
		
		Set<CAState> test = aut.getStates().stream()
		.filter(s->s.toString().equals("[1, 1]")||s.toString().equals("[1, 2]"))
		.collect(Collectors.toSet());
		
//		System.out.println(states);
		assertTrue(states.equals(test));
	}
	
	@Test
	public void testOrcSynthesis2021() throws IOException {
		MSCA aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.data");
	
		MSCA orc = new OrchestrationSynthesisOperator(new Agreement(),prop).apply(aut);
			
		MSCA test = bdc.importMSCA(dir+"Orc_(AlicexBob)_forte2021.data");
		
		assertTrue(MSCATest.checkTransitions(orc, test));
	}
	
	@Test
	public void testCorSynthesis2021() throws IOException {
		BasicState s0 = new BasicState("0",true,false);
		BasicState s1 = new BasicState("1",false,true);
		BasicState s2 = new BasicState("2",false,true);
		Transition<String,String,BasicState,Label<String>> t1 = new Transition<>(s0, new Label<String>("m"), s1);
		Transition<String,String,BasicState,Label<String>> t2 = new Transition<>(s0, new Label<String>("m"), s2);
		Automaton<String,String,BasicState,Transition<String,String,BasicState,Label<String>>> prop  = new Automaton<>(Set.of(t1,t2));
		
		MSCA aut = bdc.importMSCA(dir + "testcor_concur21_Example34.data");
	
		MSCA cor = new ChoreographySynthesisOperator(new StrongAgreement(),prop).apply(aut);
		MSCA test = bdc.importMSCA(dir+"Cor_(testcor_concur21_Example34)_prop.data");
		
		assertTrue(MSCATest.checkTransitions(cor, test));
	}
}
