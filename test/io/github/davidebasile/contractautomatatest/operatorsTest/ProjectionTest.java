package io.github.davidebasile.contractautomatatest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.label.CMLabel;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.converters.DataConverter;
import io.github.davidebasile.contractautomata.converters.MxeConverter;
import io.github.davidebasile.contractautomata.operators.ChoreographySynthesisOperator;
import io.github.davidebasile.contractautomata.operators.CompositionFunction;
import io.github.davidebasile.contractautomata.operators.ProjectionFunction;
import io.github.davidebasile.contractautomata.requirements.StrongAgreement;
import io.github.davidebasile.contractautomatatest.MSCATest;

public class ProjectionTest {
	

	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final MxeConverter bmc = new MxeConverter();
	private final DataConverter bdc = new DataConverter();
	
	@Test
	public void projectionTestSCP2020_BusinessClient() throws Exception{
		MSCA aut = bmc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).mxe");
		MSCA test= bmc.importMSCA(dir+"BusinessClient.mxe");
		aut=new ProjectionFunction(new Label("dum")).apply(aut,0, t->t.getLabel().getRequester());
		//		System.out.println(aut);
		//		System.out.println(test);
		assertEquals(MSCATest.checkTransitions(aut,test),true);

	}

	@Test
	public void choreoConcur2021projectAndComposeTest() throws Exception {
		MSCA aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");
		List<MSCA> principals = IntStream.range(0,aut.getRank())
				.mapToObj(i->new ProjectionFunction(new Label("dumb")).apply(aut,i, t->t.getLabel().getOfferer()))
				.collect(Collectors.toList());

		MSCA closed_aut = new CompositionFunction().apply(principals, new StrongAgreement().negate(), 100);

		bdc.exportMSCA(dir+"testcor_concur21_Example34_closureCA.data", closed_aut);

		boolean bc = closed_aut.getTransition().stream()
				.allMatch(t->new ChoreographySynthesisOperator(new StrongAgreement())
						.satisfiesBranchingCondition(t,aut.getTransition(), 
								new HashSet<CAState>()));
		assertEquals(bc,false);	
	}
	

	@Test
	public void choreoConcur2021projectAndComposeTestCM() throws Exception {
		MSCA aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");

		
		List<MSCA> principals = IntStream.range(0,aut.getRank())
				.mapToObj(i->new ProjectionFunction(new CMLabel(1+"",2+"","!dummy")).apply(aut,i, t->t.getLabel().getOfferer()))
				.collect(Collectors.toList());
		MSCA closed_aut = new CompositionFunction().apply(principals, new StrongAgreement().negate(), 100);
//		bdc.exportMSCA(dir+"testcor_concur21_Example34_closureCM.data", closed_aut);
	
		MSCA test = bdc.importMSCA(dir+"testcor_concur21_Example34_closureCM.data");

//		boolean bc = closed_aut.getTransition().stream()
//				.allMatch(t->new ChoreographySynthesisOperator(new StrongAgreement())
//						.satisfiesBranchingCondition(t,aut.getTransition(), 
//								new HashSet<CAState>()));
//		assertEquals(bc,false);	
		
		assertTrue(MSCATest.checkTransitions(closed_aut, test));
	}
	
	@Test
	public void projectOnMachineAndImport() throws IOException {
		MSCA aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");
		MSCA cm = new ProjectionFunction(new CMLabel("1","2","!dummy")).apply(aut,0, t->t.getLabel().getOfferer());
		MSCA test = bdc.importMSCA(dir+"cm_concur21.data");
		
		assertTrue(MSCATest.checkTransitions(cm, test));

	}

	
	//************************************exceptions*********************************************

	@Test
	public void projectionException1() throws IOException {
		MSCA aut = bdc.importMSCA(dir+"BusinessClient.mxe.data");
		assertThatThrownBy(() -> new ProjectionFunction(new Label("dum")).apply(aut,-1, null))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Index out of rank");
	}

	@Test
	public void projectionException2() throws IOException {
		MSCA aut = bdc.importMSCA(dir+"BusinessClient.mxe.data");
		assertThatThrownBy(() -> new ProjectionFunction(new Label("dum")).apply(aut,2, null))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Index out of rank");
	}

	@Test
	public void projectionException3() throws IOException {
		MSCA aut = bdc.importMSCA(dir+"BusinessClient.mxe.data");
		assertThatThrownBy(() -> new ProjectionFunction(new CMLabel(1+"",2+"","!dum")).apply(aut,0, t->t.getLabel().getOfferer()))
		.isInstanceOf(UnsupportedOperationException.class);
	}

}
