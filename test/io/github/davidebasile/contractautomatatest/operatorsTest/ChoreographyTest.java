package io.github.davidebasile.contractautomatatest.operatorsTest;

import static io.github.davidebasile.contractautomatatest.MSCATest.checkTransitions;
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

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.MSCATransition;
import io.github.davidebasile.contractautomata.converters.DataConverter;
import io.github.davidebasile.contractautomata.converters.MxeConverter;
import io.github.davidebasile.contractautomata.operators.ChoreographySynthesisOperator;
import io.github.davidebasile.contractautomata.requirements.StrongAgreement;

public class ChoreographyTest {

	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final MxeConverter bmc = new MxeConverter();
	//	private final DataConverter bdc = new DataConverter();

	@Test
	public void chorTestLMCS2020Transitions() throws Exception, TransformerException
	{
		boolean check=false;
		MSCA aut = bmc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		List<MSCA> tests = new ArrayList<>();
		tests.add(bmc.importMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe"));
		int max=8;
		//	while(true){
		max+=1;
		//			System.out.println(max);
		for (int i=1;i<max;i++)
			tests.add(bmc.importMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_"+i+".mxe"));

		//			MSCA corsave;
		//			do {
		MSCA cor = new ChoreographySynthesisOperator(new StrongAgreement()).apply(aut);
		check = tests.stream()
				.anyMatch(a->checkTransitions(cor,a));
		//				corsave=cor;

		//			} while (check);
		//			bmc.exportMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_"+max+".mxe", corsave);
		//}

		assertTrue(check);
	}

	@Test
	public void chorTestLMCS2020TransitionsConstructorTwoArguments() throws Exception, TransformerException
	{
		MSCA aut = bmc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		MSCA test = bmc.importMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_5.mxe");

		Function<Stream<MSCATransition>,Optional<MSCATransition>> choice = 
				s -> s.sorted((t1,t2)->t1.toCSV().compareTo(t2.toCSV())).findFirst();
				
		MSCA cor = new ChoreographySynthesisOperator(new StrongAgreement(),choice).apply(aut);
	

		assertTrue(checkTransitions(cor,test));
	}

	@Test
	public void chorTestControllableLazyOfferTransitions() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"test_chor_controllablelazyoffer.mxe");
		MSCA test = bmc.importMSCA(dir+"Chor_(test_chor_controllablelazyoffer).mxe");
		assertEquals(checkTransitions(new ChoreographySynthesisOperator(new StrongAgreement()).apply(aut),test),true);
	}


	@Test
	public void chorTest_empty() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"test_lazy_empty_cor.mxe");
		assertEquals(new ChoreographySynthesisOperator(new StrongAgreement()).apply(aut),null);
	}

	@Test
	public void chorTest_urgent_empty() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"test_chor_urgentoffer.mxe");
		assertEquals(new ChoreographySynthesisOperator(new StrongAgreement()).apply(aut),null);
	}

	@Test
	public void chor_lazy_exception() throws Exception
	{
		MSCA orc = bmc.importMSCA(dir+"test_empty_orc_lazy.mxe");
		assertThatThrownBy(() -> new ChoreographySynthesisOperator(new StrongAgreement()).apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	public void choreoConcur2021ex25() throws Exception {

		MSCA aut = bmc.importMSCA(dir+"testcor_concur21_Example25.mxe");
		boolean bc = aut.getTransition().stream()
				.allMatch(t->new ChoreographySynthesisOperator(new StrongAgreement()).satisfiesBranchingCondition(t,aut.getTransition(), 
						new HashSet<CAState>()));
		assertEquals(bc,false);	
	}

	@Test
	public void choreoConcur2021ex34() throws Exception {
		MSCA aut = bmc.importMSCA(dir+"testcor_concur21_Example34.mxe");
		boolean bc = aut.getTransition().stream()
				.allMatch(t->new ChoreographySynthesisOperator(new StrongAgreement()).satisfiesBranchingCondition(t,aut.getTransition(), 
						new HashSet<CAState>()));
		assertEquals(bc,false);	
	}


	@Test
	public void branchingCondition() throws NumberFormatException, IOException {

		MSCA aut = new DataConverter().importMSCA(dir+"violatingbranchingcondition.mxe.data");

		final Set<MSCATransition> trf = aut.getTransition();
		Set<MSCATransition> violatingBC = aut.getTransition().stream()
				.filter(x->!new ChoreographySynthesisOperator(new StrongAgreement())
						.satisfiesBranchingCondition(x,trf, new HashSet<CAState>()))
				.collect(Collectors.toSet());


		assertEquals(violatingBC.size(),6);
	}


	//	@Test
	//	public void chorSmallerTestTransitions() throws Exception
	//	{
	//
	//		MSCA aut = bmc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
	//		MSCA cor=new SynthesisOperator((x,t,bad) -> !x.satisfiesBranchingCondition(t, bad)
	//				||!x.getLabel().isMatch()||bad.contains(x.getTarget()),
	//				(x,t,bad) -> bad.contains(x.getTarget()) && x.isUncontrollableChoreography(t, bad))
	//				.apply(aut);
	//
	//		assertEquals(cor,null);
	//	}


}
