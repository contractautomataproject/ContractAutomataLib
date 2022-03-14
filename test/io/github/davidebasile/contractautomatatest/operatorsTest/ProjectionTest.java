package io.github.davidebasile.contractautomatatest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.CMLabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.operators.ChoreographySynthesisOperator;
import io.github.contractautomataproject.catlib.operators.MSCACompositionFunction;
import io.github.contractautomataproject.catlib.operators.ProjectionFunction;
import io.github.contractautomataproject.catlib.requirements.StrongAgreement;
import io.github.davidebasile.contractautomatatest.MSCATest;

public class ProjectionTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	
	@Test
	public void projectionTestSCP2020_BusinessClient() throws Exception{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		ModalAutomaton<CALabel> test= bdc.importMSCA(dir+"BusinessClient.data");
		aut=new ProjectionFunction(new Label<List<String>>(List.of("dum"))).apply(aut,0, t->t.getLabel().getRequester());
		//		System.out.println(aut);
		//		System.out.println(test);
		Assert.assertTrue(MSCATest.autEquals(aut,test));

	}

	@Test
	public void choreoConcur2021projectAndComposeTest() throws Exception {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");
		List<ModalAutomaton<CALabel>> principals = IntStream.range(0,aut.getRank())
				.mapToObj(i->new ProjectionFunction(new Label<List<String>>(List.of("dumb"))).apply(aut,i, t->t.getLabel().getOfferer()))
				.collect(Collectors.toList());

		ModalAutomaton<CALabel> closed_aut = new MSCACompositionFunction(principals,new StrongAgreement().negate()).apply(100);

		bdc.exportMSCA(dir+"testcor_concur21_Example34_closureCA.data", closed_aut);

		boolean bc = closed_aut.getTransition().stream()
				.allMatch(t->new ChoreographySynthesisOperator(new StrongAgreement())
						.satisfiesBranchingCondition(t,aut.getTransition(), 
								new HashSet<CAState>()));
		Assert.assertFalse(bc);	
	}
	

	@Test
	public void choreoConcur2021projectAndComposeTestCM() throws Exception {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");

		
		List<ModalAutomaton<CALabel>> principals = IntStream.range(0,aut.getRank())
				.mapToObj(i->new ProjectionFunction(new CMLabel(1+"",2+"","!dummy")).apply(aut,i, t->t.getLabel().getOfferer()))
				.collect(Collectors.toList());
		ModalAutomaton<CALabel> closed_aut = new MSCACompositionFunction(principals,new StrongAgreement().negate()).apply(100);
//		bdc.exportMSCA(dir+"testcor_concur21_Example34_closureCM.data", closed_aut);
	
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"testcor_concur21_Example34_closureCM.data");

//		boolean bc = closed_aut.getTransition().stream()
//				.allMatch(t->new ChoreographySynthesisOperator(new StrongAgreement())
//						.satisfiesBranchingCondition(t,aut.getTransition(), 
//								new HashSet<CAState>()));
//		assertEquals(bc,false);	
		
		assertTrue(MSCATest.autEquals(closed_aut, test));
	}
	
	@Test
	public void projectOnMachineAndImport() throws Exception {
		AutDataConverter<CMLabel> cmdc = new AutDataConverter<>(CMLabel::new);
		
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");
		ModalAutomaton<CALabel> cm = new ProjectionFunction(new CMLabel("1","2","!dummy")).apply(aut,0, t->t.getLabel().getOfferer());
		
		ModalAutomaton<CMLabel> test = cmdc.importMSCA(dir+"cm_concur21.data");
		
		assertTrue(MSCATest.autEquals(cm, test));

	}

	
	//************************************exceptions*********************************************

	@Test
	public void projectionException1() throws Exception {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"BusinessClient.data");
		ProjectionFunction pj = new ProjectionFunction(new Label<List<String>>(List.of("dumb")));
		assertThatThrownBy(() -> pj.apply(aut,-1, null))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Index out of rank");
	}

	@Test
	public void projectionException2() throws Exception {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"BusinessClient.data");
		ProjectionFunction pj = new ProjectionFunction(new Label<List<String>>(List.of("dumb")));
		assertThatThrownBy(() -> pj.apply(aut,2, null))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Index out of rank");
	}

	@Test
	public void projectionException3() throws Exception {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"BusinessClient.data");
		ProjectionFunction pj = new ProjectionFunction(new CMLabel(1+"",2+"","!dum"));
		assertThatThrownBy(() -> pj.apply(aut,0, t->t.getLabel().getOfferer()))
		.isInstanceOf(UnsupportedOperationException.class);
	}

}
