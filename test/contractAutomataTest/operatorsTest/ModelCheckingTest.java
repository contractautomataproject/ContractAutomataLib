package contractAutomataTest.operatorsTest;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import contractAutomata.automaton.Automaton;
import contractAutomata.automaton.MSCA;
import contractAutomata.automaton.label.Label;
import contractAutomata.automaton.state.BasicState;
import contractAutomata.automaton.state.CAState;
import contractAutomata.automaton.transition.Transition;
import contractAutomata.converters.DataConverter;
import contractAutomata.operators.ChoreographySynthesisOperator;
import contractAutomata.operators.ModelCheckingFunction;
import contractAutomata.operators.OrchestrationSynthesisOperator;
import contractAutomata.requirements.Agreement;
import contractAutomata.requirements.StrongAgreement;
import contractAutomataTest.MSCATest;

public class ModelCheckingTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final DataConverter bdc = new DataConverter();
	private Automaton<String, BasicState,Transition<String, BasicState,Label>> prop ;
	
	@Before
	public void setup() {
		BasicState s0 = new BasicState("0",true,false);
		BasicState s1 = new BasicState("1",false,false);
		BasicState s2 = new BasicState("2",false,true);
		Transition<String, BasicState,Label> t1 = new Transition<>(s0, new Label("blueberry"), s1);
		Transition<String, BasicState,Label> t2 = new Transition<>(s1, new Label("ananas"), s2);
		Transition<String, BasicState,Label> t3 = new Transition<>(s0, new Label("cherry"), s2);
		prop = new Automaton<>(Set.of(t1,t2,t3));
	}
	
	@Test
	public void testForte2021() throws IOException {
		MSCA aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.mxe.data");
		Set<CAState> states = new ModelCheckingFunction().apply(aut, prop);
		Set<CAState> test = aut.getStates().stream()
		.filter(s->s.toString().equals("[1, 1]")||s.toString().equals("[1, 2]"))
		.collect(Collectors.toSet());
		assertTrue(states.equals(test));
	}
	
	@Test
	public void testOrcSynthesis2021() throws IOException {
		MSCA aut = bdc.importMSCA(dir + "(AlicexBob)_forte2021.mxe.data");
	
		MSCA orc = new OrchestrationSynthesisOperator(new Agreement(),prop).apply(aut);
		MSCA test = bdc.importMSCA(dir+"Orc_(AlicexBob)_forte2021.data");
		
		assertTrue(MSCATest.checkTransitions(orc, test));
	}
	
	@Test
	public void testCorSynthesis2021() throws IOException {
		BasicState s0 = new BasicState("0",true,false);
		BasicState s1 = new BasicState("1",false,true);
		BasicState s2 = new BasicState("2",false,true);
		Transition<String, BasicState,Label> t1 = new Transition<>(s0, new Label("m"), s1);
		Transition<String, BasicState,Label> t2 = new Transition<>(s0, new Label("m"), s2);
		Automaton<String, BasicState,Transition<String, BasicState,Label>> prop  = new Automaton<>(Set.of(t1,t2));
		
		MSCA aut = bdc.importMSCA(dir + "testcor_concur21_Example34.mxe.data");
	
		MSCA cor = new ChoreographySynthesisOperator(new StrongAgreement(),prop).apply(aut);
		MSCA test = bdc.importMSCA(dir+"Cor_(testcor_concur21_Example34)_prop.data");
		assertTrue(MSCATest.checkTransitions(cor, test));
	}
}
