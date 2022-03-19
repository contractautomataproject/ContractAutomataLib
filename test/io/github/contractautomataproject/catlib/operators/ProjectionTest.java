package io.github.contractautomataproject.catlib.operators;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ITAutomatonTest;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.CMLabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.requirements.StrongAgreement;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

public class ProjectionTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	
	@Test
	public void projectionTestSCP2020_BusinessClient() throws Exception{
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> test= bdc.importMSCA(dir+"BusinessClient.data");
		aut=new ProjectionFunction(new Label<>(List.of("dum"))).apply(aut,0, t->t.getLabel().getRequester());
		Assert.assertTrue(ITAutomatonTest.autEquals(aut,test));

	}

	@Test
	public void choreoConcur2021projectAndComposeTest() throws Exception {
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");
		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> principals = IntStream.range(0,aut.getRank())
				.mapToObj(i->new ProjectionFunction(new Label<>(List.of("dumb"))).apply(aut,i, t->t.getLabel().getOfferer()))
				.collect(Collectors.toList());

		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> closed_aut = new MSCACompositionFunction(principals,new StrongAgreement().negate()).apply(100);

		bdc.exportMSCA(dir+"testcor_concur21_Example34_closureCA.data", closed_aut);

		boolean bc = closed_aut.getTransition().stream()
				.allMatch(t->new ChoreographySynthesisOperator(new StrongAgreement())
						.satisfiesBranchingCondition(t,aut.getTransition(),
                                new HashSet<>()));
		Assert.assertFalse(bc);	
	}
	

	@Test
	public void choreoConcur2021projectAndComposeTestCM() throws Exception {
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");

		
		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> principals = IntStream.range(0,aut.getRank())
				.mapToObj(i->new ProjectionFunction(new CMLabel(1+"",2+"","!dummy")).apply(aut,i, t->t.getLabel().getOfferer()))
				.collect(Collectors.toList());
				
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> closed_aut = new MSCACompositionFunction(principals,new StrongAgreement().negate()).apply(100);
	
		//bdc.exportMSCA(dir+"testcor_concur21_Example34_closureCM_new.data", closed_aut);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> test = bdc.importMSCA(dir+"testcor_concur21_Example34_closureCM.data");

		assertTrue(ITAutomatonTest.autEquals(closed_aut, test));
	}
	
	@Test
	public void projectOnMachineAndImport() throws Exception {
		AutDataConverter<CMLabel> cmdc = new AutDataConverter<>(CMLabel::new);
		
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> cm = new ProjectionFunction(new CMLabel("1","2","!dummy")).apply(aut,0, t->t.getLabel().getOfferer());
		
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CMLabel>> test = cmdc.importMSCA(dir+"cm_concur21.data");
		
		assertTrue(ITAutomatonTest.autEquals(cm, test));

	}

	
	//************************************exceptions*********************************************

	@Test
	public void projectionException1() throws Exception {
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"BusinessClient.data");
		ProjectionFunction pj = new ProjectionFunction(new Label<>(List.of("dumb")));
		assertThatThrownBy(() -> pj.apply(aut,-1, null))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Index out of rank");
	}

	@Test
	public void projectionException2() throws Exception {
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"BusinessClient.data");
		ProjectionFunction pj = new ProjectionFunction(new Label<>(List.of("dumb")));
		assertThatThrownBy(() -> pj.apply(aut,2, null))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Index out of rank");
	}

	@Test
	public void projectionException3() throws Exception {
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut = bdc.importMSCA(dir+"BusinessClient.data");
		ProjectionFunction pj = new ProjectionFunction(new CMLabel(1+"",2+"","!dum"));
		assertThatThrownBy(() -> pj.apply(aut,0, t->t.getLabel().getOfferer()))
		.isInstanceOf(UnsupportedOperationException.class);
	}

}
