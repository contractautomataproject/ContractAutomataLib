package io.github.contractautomataproject.catlib.operators;

import static io.github.contractautomataproject.catlib.automaton.ModalAutomatonTest.autEquals;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.transform.TransformerException;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.ModalAutomatonTest;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.operators.ChoreographySynthesisOperator;
import io.github.contractautomataproject.catlib.requirements.StrongAgreement;
import io.github.contractautomataproject.catlib.requirements.StrongAgreementModelChecking;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

public class ChoreographyTest {

	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);

	@Test
	public void chorTestLMCS2020Transitions() throws Exception, TransformerException
	{
		boolean check=false;
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).data");
		List<ModalAutomaton<CALabel>> tests = new ArrayList<>();
		tests.add(bdc.importMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel).data"));
		int max=8;
		//	while(true){
		max+=1;
		//			System.out.println(max);
		for (int i=1;i<max;i++)
			tests.add(bdc.importMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_"+i+".data"));

		//			MSCA corsave;
		//			do {
		ModalAutomaton<CALabel> cor = new ChoreographySynthesisOperator(new StrongAgreement()).apply(aut);
		check = tests.stream()
				.anyMatch(a->autEquals(cor,a));
		//				corsave=cor;

		//			} while (check);
		//			bmc.exportMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_"+max+".mxe", corsave);
		//}

		assertTrue(check);
	}

	@Test
	public void chorTestLMCS2020TransitionsConstructorTwoArguments() throws Exception, TransformerException
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).data");
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_5.data");

		Function<Stream<ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>>,Optional<ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>>> choice = 
				s -> s.sorted((t1,t2)->t1.toString().compareTo(t2.toString())).findFirst();
				
		ModalAutomaton<CALabel> cor = new ChoreographySynthesisOperator(new StrongAgreement(),choice).apply(aut);
	

		assertTrue(autEquals(cor,test));
	}

	@Test
	public void chorTestControllableLazyOfferTransitions() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_chor_controllablelazyoffer.data");
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"Chor_(test_chor_controllablelazyoffer).data");
		assertTrue(autEquals(new ChoreographySynthesisOperator(new StrongAgreement()).apply(aut),test));
	}


	@Test
	public void chorTest_empty() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_lazy_empty_cor.data");
		Assert.assertNull(new ChoreographySynthesisOperator(new StrongAgreement()).apply(aut));
	}

	@Test
	public void chorTest_urgent_empty() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_chor_urgentoffer.data");
		Assert.assertNull( new ChoreographySynthesisOperator(new StrongAgreement()).apply(aut));
	}

	@Test
	public void chor_lazy_exception() throws Exception
	{
		ModalAutomaton<CALabel> orc = bdc.importMSCA(dir+"test_empty_orc_lazy.data");
		ChoreographySynthesisOperator cso = new ChoreographySynthesisOperator(new StrongAgreement());
		assertThatThrownBy(() -> cso.apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	public void choreoConcur2021ex25() throws Exception {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"testcor_concur21_Example25.data");
		boolean bc = aut.getTransition().stream()
				.allMatch(t->new ChoreographySynthesisOperator(new StrongAgreement()).satisfiesBranchingCondition(t,aut.getTransition(), 
						new HashSet<CAState<String>>()));
		Assert.assertFalse(bc);	
	}

	@Test
	public void choreoConcur2021ex34() throws Exception {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"testcor_concur21_Example34.data");
		boolean bc = aut.getTransition().stream()
				.allMatch(t->new ChoreographySynthesisOperator(new StrongAgreement()).satisfiesBranchingCondition(t,aut.getTransition(), 
						new HashSet<CAState<String>>()));
		Assert.assertFalse(bc);	
	}


	@Test
	public void branchingCondition() throws NumberFormatException, IOException {
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"violatingbranchingcondition.data");
		final Set<ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>> trf = aut.getTransition();
		Set<ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>> violatingBC = aut.getTransition().stream()
				.filter(x->!new ChoreographySynthesisOperator(new StrongAgreement())
						.satisfiesBranchingCondition(x,trf, new HashSet<CAState<String>>()))
				.collect(Collectors.toSet());

		assertEquals(6,violatingBC.size());
	}


	///////// MC 

	@Test
	public void testCorSynthesis2021() throws IOException {
		BasicState<String> s0 = new BasicState<String>("0",true,false);
		BasicState<String> s1 = new BasicState<String>("1",false,true);
		BasicState<String> s2 = new BasicState<String>("2",false,true);
		ModalTransition<String,String,BasicState<String>,Label<String>> t1 = new ModalTransition<>(s0, new Label<String>("m"), s1, ModalTransition.Modality.PERMITTED);
		ModalTransition<String,String,BasicState<String>,Label<String>> t2 = new ModalTransition<>(s0, new Label<String>("n"), s2, ModalTransition.Modality.PERMITTED);
		Automaton<String,String,BasicState<String>,ModalTransition<String,String,BasicState<String>,Label<String>>> prop  = new Automaton<>(Set.of(t1,t2));
		
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir + "testcor_concur21_Example34.data");
		ModalAutomaton<CALabel> cor = new ChoreographySynthesisOperator(new StrongAgreement(),new StrongAgreementModelChecking<Label<List<String>>>(),prop).apply(aut);
	
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir+"Cor_(testcor_concur21_Example34)_prop.data");		
		
		assertTrue(ModalAutomatonTest.autEquals(cor, test));
	}

}




//	@Test
//	public void chorSmallerTestTransitions() throws Exception
//	{
//
//		MSCA aut = bdc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).data");
//		MSCA cor=new SynthesisOperator((x,t,bad) -> !x.satisfiesBranchingCondition(t, bad)
//				||!x.getLabel().isMatch()||bad.contains(x.getTarget()),
//				(x,t,bad) -> bad.contains(x.getTarget()) && x.isUncontrollableChoreography(t, bad))
//				.apply(aut);
//
//		Assert.assertNull(cor);
//	}
