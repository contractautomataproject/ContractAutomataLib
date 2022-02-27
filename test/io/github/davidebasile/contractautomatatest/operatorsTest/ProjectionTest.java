package io.github.davidebasile.contractautomatatest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.CMLabel;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.converters.DataConverter;
import io.github.davidebasile.contractautomata.converters.MSCAConverter;
import io.github.davidebasile.contractautomata.operators.ChoreographySynthesisOperator;
import io.github.davidebasile.contractautomata.operators.MSCACompositionFunction;
import io.github.davidebasile.contractautomata.operators.ProjectionFunction;
import io.github.davidebasile.contractautomata.requirements.StrongAgreement;
import io.github.davidebasile.contractautomatatest.MSCATest;

public class ProjectionTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	//private final MSCAConverter bmc = new MxeConverter();
	private final MSCAConverter bdc = new DataConverter();
	
	@Test
	public void projectionTestSCP2020_BusinessClient() throws Exception{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		ModalAutomaton<CALabel> test= bdc.importMSCA(dir+"BusinessClient.data");
		aut=new ProjectionFunction(new Label<List<String>>(List.of("dum"))).apply(aut,0, t->t.getLabel().getRequester());
		//		System.out.println(aut);
		//		System.out.println(test);
		assertEquals(MSCATest.checkTransitions(aut,test),true);

	}

	@Test
	public void choreoConcur2021projectAndComposeTest() throws Exception {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");
		List<ModalAutomaton<CALabel>> principals = IntStream.range(0,aut.getRank())
				.mapToObj(i->new ProjectionFunction(new Label<List<String>>(List.of("dumb"))).apply(aut,i, t->t.getLabel().getOfferer()))
				.collect(Collectors.toList());

		ModalAutomaton<CALabel> closed_aut = new MSCACompositionFunction(principals).apply(new StrongAgreement().negate(), 100);

		bdc.exportMSCA(dir+"testcor_concur21_Example34_closureCA.data", closed_aut);

		boolean bc = closed_aut.getTransition().stream()
				.allMatch(t->new ChoreographySynthesisOperator(new StrongAgreement())
						.satisfiesBranchingCondition(t,aut.getTransition(), 
								new HashSet<CAState>()));
		assertEquals(bc,false);	
	}
	

	@Test
	public void choreoConcur2021projectAndComposeTestCM() throws Exception {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");

		
		List<ModalAutomaton<CALabel>> principals = IntStream.range(0,aut.getRank())
				.mapToObj(i->new ProjectionFunction(new CMLabel(1+"",2+"","!dummy")).apply(aut,i, t->t.getLabel().getOfferer()))
				.collect(Collectors.toList());
		ModalAutomaton<CALabel> closed_aut = new MSCACompositionFunction(principals).apply(new StrongAgreement().negate(), 100);
//		bdc.exportMSCA(dir+"testcor_concur21_Example34_closureCM.data", closed_aut);
	
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"testcor_concur21_Example34_closureCM.data");

//		boolean bc = closed_aut.getTransition().stream()
//				.allMatch(t->new ChoreographySynthesisOperator(new StrongAgreement())
//						.satisfiesBranchingCondition(t,aut.getTransition(), 
//								new HashSet<CAState>()));
//		assertEquals(bc,false);	
		
		assertTrue(MSCATest.checkTransitions(closed_aut, test));
	}
	
	@Test
	public void projectOnMachineAndImport() throws Exception {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");
		ModalAutomaton<CALabel> cm = new ProjectionFunction(new CMLabel("1","2","!dummy")).apply(aut,0, t->t.getLabel().getOfferer());
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"cm_concur21.data");
		
		assertTrue(MSCATest.checkTransitions(cm, test));

	}

	
	//************************************exceptions*********************************************

	@Test
	public void projectionException1() throws Exception {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"BusinessClient.data");
		assertThatThrownBy(() -> new ProjectionFunction(new Label<List<String>>(List.of("dumb"))).apply(aut,-1, null))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Index out of rank");
	}

	@Test
	public void projectionException2() throws Exception {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"BusinessClient.data");
		assertThatThrownBy(() -> new ProjectionFunction(new Label<List<String>>(List.of("dumb"))).apply(aut,2, null))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Index out of rank");
	}

	@Test
	public void projectionException3() throws Exception {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"BusinessClient.data");
		assertThatThrownBy(() -> new ProjectionFunction(new CMLabel(1+"",2+"","!dum")).apply(aut,0, t->t.getLabel().getOfferer()))
		.isInstanceOf(UnsupportedOperationException.class);
	}

}
