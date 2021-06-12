package contractAutomataTest;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.xml.transform.TransformerException;

import org.junit.Test;

import contractAutomata.BasicState;
import contractAutomata.CALabel;
import contractAutomata.CAState;
import contractAutomata.ChoreographySynthesisOperator;
import contractAutomata.CompositionFunction;
import contractAutomata.DataConverter;
import contractAutomata.MSCA;
import contractAutomata.MSCATransition;
import contractAutomata.MSCATransition.Modality;
import contractAutomata.MpcSynthesisOperator;
import contractAutomata.MxeConverter;
import contractAutomata.OrchestrationSynthesisOperator;
import contractAutomata.ProjectionFunction;
import contractAutomata.SynthesisFunction;
import contractAutomata.UnionFunction;

public class MSCATest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final MxeConverter bmc = new MxeConverter();
	private final DataConverter bdc = new DataConverter();

	//**********************************SCICO2020 case study*******************************************************************

	@Test
	public void compositionTestSCP2020_nonassociative() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);
		
		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bmc.importMSCA(dir+"BusinessClientxHotel_open.mxe"));

		MSCA comp=new CompositionFunction().apply(aut, null,100);
		assertEquals(new OrchestrationSynthesisOperator().apply(comp),null);
	}

	@Test
	public void compositionTestSCP2020_BusinessClientxHotel_closed() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);
		
		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));

		MSCA comp=new CompositionFunction().apply(aut, t->t.getLabel().isRequest(),100);
		MSCA test = bmc.importMSCA(dir+"BusinessClientxHotel_closed.mxe");
		assertEquals(checkTransitions(comp,test),true);
	}

	@Test
	public void compositionTestSCP2020_BusinessClientxHotel_open() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);
		
		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));

		MSCA comp=new CompositionFunction().apply(aut, null,100);
		MSCA test = bmc.importMSCA(dir+"BusinessClientxHotel_open.mxe");
		assertEquals(checkTransitions(comp,test),true);
	}

	@Test
	public void compositionTestSCP2020_BusinessClientxHotelxEconomyClient_open_transitions() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);
		
		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.mxe.data"));
		MSCA comp = new CompositionFunction().apply(aut, null,100);
		MSCA test= bmc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe");
		assertEquals(checkTransitions(comp,test),true);	
	}

	@Test
	public void compAndOrcTestSCP2020_BusinessClientxHotelxEconomyClient() throws Exception
	{
		List<MSCA> aut = new ArrayList<>(2);
		
		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.mxe.data"));
		MSCA comp=new CompositionFunction().apply(aut, t->t.getLabel().isRequest(),100);
		
		MSCA test= bmc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient)_test.mxe");
		assertEquals(checkTransitions(new OrchestrationSynthesisOperator().apply(comp),test),true);

//		assertEquals(comp.orchestration().getNumStates(),14);
	}	

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_transitions() throws Exception
	{

		
		MSCA aut = bmc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).mxe");
		MSCA test= bmc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient)_test.mxe");
		assertEquals(checkTransitions(new OrchestrationSynthesisOperator().apply(aut),test),true);
	}	


	@Test
	public void unionTest() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);
		
		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));
		MSCA union = new UnionFunction().apply(aut);
		MSCA test = bmc.importMSCA(dir+"union_BusinessClient_EconomyClient_Hotel.mxe");
		assertEquals(checkTransitions(union,test),true);
	}
	
	@Test
	public void projectionTestSCP2020_BusinessClient() throws Exception{
		MSCA aut = bmc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).mxe");
		MSCA test= bmc.importMSCA(dir+"BusinessClient.mxe");
		aut=new ProjectionFunction().apply(aut,0, t->t.getLabel().getRequester());
//		System.out.println(aut);
//		System.out.println(test);
		assertEquals(checkTransitions(aut,test),true);
		
	}

	//*******************************************LMCS2020 case study********************************************************


	@Test
	public void chorTestLMCS2020Transitions() throws Exception, TransformerException
	{
		
		MSCA aut = bmc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		MSCA test1 = bmc.importMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		MSCA test2 = bmc.importMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_1.mxe");
		MSCA test3 = bmc.importMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_2.mxe");
		MSCA test4 = bmc.importMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_3.mxe");
		MSCA test5 = bmc.importMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_4.mxe");
		MSCA test6 = bmc.importMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_5.mxe");
		MSCA test7 = bmc.importMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_6.mxe");
		MSCA test8 = bmc.importMSCA(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_7.mxe");

		//aut = aut.choreography();
		aut = new ChoreographySynthesisOperator().apply(aut);
		boolean check = checkTransitions(aut,test1)||checkTransitions(aut,test2)||checkTransitions(aut,test3)
				||checkTransitions(aut,test4)||checkTransitions(aut,test5)
				||checkTransitions(aut,test6)||checkTransitions(aut,test7)
				||checkTransitions(aut,test8);//||checkTransitions(aut,test7); 

//		do {
//			aut = MSCAIO.parseXMLintoMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
//			aut = aut.choreography();
//			check = checkTransitions(aut,test1)||checkTransitions(aut,test2)||checkTransitions(aut,test3)
//					||checkTransitions(aut,test4)||checkTransitions(aut,test5)
//					||checkTransitions(aut,test6)||checkTransitions(aut,test7)
//					||checkTransitions(aut,test8);//||checkTransitions(aut,test7); 
//		} while (check);
//		if (!check)
//			MSCAIO.convertMSCAintoXML(dir+"Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_8.mxe", aut);
		
		assertTrue(check);
	}

	@Test
	public void chorSmallerTestTransitions() throws Exception
	{

		MSCA aut = bmc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		MSCA cor=new SynthesisFunction().apply(aut,(x,t,bad) -> 	!x.satisfiesBranchingCondition(t, bad)||!x.getLabel().isMatch()||bad.contains(x.getTarget()),
				(x,t,bad) -> bad.contains(x.getTarget()) && x.isUncontrollableChoreography(t, bad));

		assertEquals(cor,null);
	}



	@Test
	public void orcTestLMCS2020Transitions() throws Exception
	{

		
		MSCA aut = bmc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");
		MSCA test = bmc.importMSCA(dir+"Orc_(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");

		assertEquals(checkTransitions(new OrchestrationSynthesisOperator().apply(aut),test),true);
	}

	@Test
	public void orcTestLMCS2020Transitions_new() throws Exception
	{

		
		MSCA aut = bmc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");
		MSCA test = bmc.importMSCA(dir+"Orc_(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");

		assertEquals(checkTransitions(new OrchestrationSynthesisOperator().apply(aut),test),true);
	}

	
	@Test
	public void mpcEmptyTestLMCS2020() throws Exception
	{

		
		MSCA aut = bmc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedUrgentHotel).mxe");
		MSCA mpc=new MpcSynthesisOperator().apply(aut);

		assertEquals(mpc,null);
	}
	
	@Test 
	public void mpcEmptyTestLMCS20202() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedUrgentHotel).mxe");
		new MpcSynthesisOperator().apply(aut);
		assertEquals(new MpcSynthesisOperator().apply(aut),null);	
	}


	@Test
	public void mpcEmptyTestNoDangling() throws Exception
	{

		
		MSCA aut = bmc.importMSCA(dir+"test_empty_mpc_nodangling.mxe");
		MSCA mpc=new MpcSynthesisOperator().apply(aut);

		assertEquals(mpc,null);
	}
	
	@Test
	public void orcEmptyTestNoDangling() throws Exception
	{

		
		MSCA aut = bmc.importMSCA(dir+"test_empty_orc_nodangling.mxe");
		assertEquals(new OrchestrationSynthesisOperator().apply(aut),null);
	}
	
	//**********************************************************************************************


	@Test
	public void chorTestControllableLazyOfferTransitions() throws Exception
	{

		
		MSCA aut = bmc.importMSCA(dir+"test_chor_controllablelazyoffer.mxe");
		MSCA test = bmc.importMSCA(dir+"Chor_(test_chor_controllablelazyoffer).mxe");
		assertEquals(checkTransitions(new ChoreographySynthesisOperator().apply(aut),test),true);
	}

//	@Test
//	public void cloneTest() throws Exception
//	{	
//		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"test_urgent.mxe");
//		assertEquals(checkTransitions(aut,aut.clone()),true);
//	}

	@Test
	public void compTestSimple() throws Exception
	{
		List<MSCA> aut = new ArrayList<>(2);
		
		aut.add(bmc.importMSCA(dir+"A.mxe"));
		aut.add(bmc.importMSCA(dir+"B.mxe"));

		MSCA comp=new CompositionFunction().apply(aut,null,100);
		MSCA test = bmc.importMSCA(dir+"(AxB).mxe");

		assertEquals(checkTransitions(comp,test),true);
	}


	@Test
	public void compTestEmptySimple() throws Exception
	{
		List<MSCA> aut = new ArrayList<>(2);
		
		aut.add(bmc.importMSCA(dir+"forNullClosedAgreementComposition.mxe"));
		aut.add(bmc.importMSCA(dir+"forNullClosedAgreementComposition.mxe"));

		MSCA comp=new CompositionFunction().apply(aut,t->t.getLabel().isRequest(),100);

		assertEquals(comp,null);
	}

	@Test
	public void mpcTest_nonempty() throws Exception
	{
		
		MSCA aut = bmc.importMSCA(dir+"test_urgent.mxe");
		assertEquals(new MpcSynthesisOperator().apply(aut).getNumStates(),2);
	}

	@Test 
	public void mpcTest2() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"test_urgent.mxe");
		new MpcSynthesisOperator().apply(aut);
		assertTrue(checkTransitions(new MpcSynthesisOperator().apply(aut),new MpcSynthesisOperator().apply(aut)));	
	}
	
	@Test
	public void orcTest_empty() throws Exception
	{
		
		MSCA orc = bmc.importMSCA(dir+"test_empty_orc.mxe");
		assertEquals(new OrchestrationSynthesisOperator().apply(orc),null);
	}

	@Test
	public void orcTest_empty_lazy() throws Exception
	{
		
		MSCA orc = bmc.importMSCA(dir+"test_empty_orc_lazy.mxe");
		assertEquals(new OrchestrationSynthesisOperator().apply(orc),null);
	}

	@Test
	public void orcTest_nonempty() throws Exception
	{
		
		MSCA orc = bmc.importMSCA(dir+"test_empty_orc_lazy.mxe");
		assertEquals(new OrchestrationSynthesisOperator().apply(orc),null);
	}

	@Test
	public void chorTest_empty() throws Exception
	{

		
		MSCA aut = bmc.importMSCA(dir+"test_lazy_empty_cor.mxe");
		assertEquals(new ChoreographySynthesisOperator().apply(aut),null);
	}

	@Test
	public void chorTest_urgent_empty() throws Exception
	{

		
		MSCA aut = bmc.importMSCA(dir+"test_chor_urgentoffer.mxe");
		assertEquals(new ChoreographySynthesisOperator().apply(aut),null);
	}

	public static boolean checkTransitions(MSCA aut, MSCA test) {
		Set<String> autTr=aut.getTransition().parallelStream()
				.map(t->t.toCSV())
				.collect(Collectors.toSet());
		Set<String> testTr=test.getTransition().parallelStream()
				.map(t->t.toCSV())
				.collect(Collectors.toSet());
		return autTr.parallelStream()
				.allMatch(t->testTr.contains(t))
				&&
				testTr.parallelStream()
				.allMatch(t->autTr.contains(t));
	}

//	@Test
//	public void setInitialCATest() throws Exception {
//		
//		MSCA aut = MSCAIO.load(dir+"BusinessClient.mxe.data");
//
//		CAState newInitial = aut.getStates().parallelStream()
//				.filter(s->s!=aut.getInitial())
//				.findFirst()
//				.orElse(null);
//
//		aut.setInitialCA(newInitial);
//
//		assertEquals(aut.getInitial(),newInitial);
//	}


	@Test
	public void union_statelabelsnotnumbers() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);
		
		aut.add(bdc.importMSCA(dir+"testgraph.data"));
		aut.add(bdc.importMSCA(dir+"testgraph.data"));
		
		MSCA union = new UnionFunction().apply(aut);
//		MSCAIO.convertMSCAintoXML(dir+"union_testgraph_testgraph.mxe", union);
		
		MSCA test = bmc.importMSCA(dir+"union_testgraph_testgraph.mxe");
		assertEquals(checkTransitions(union,test),true);
	}
	
	@Test
	public void choreoConcur2021ex25() throws Exception {
		
		MSCA aut = bmc.importMSCA(dir+"testcor_concur21_Example25.mxe");
		boolean bc = aut.getTransition().stream()
				.allMatch(t->t.satisfiesBranchingCondition(aut.getTransition(), 
						new HashSet<CAState>()));
		assertEquals(bc,false);	
	}
	
	@Test
	public void choreoConcur2021ex34() throws Exception {
		
		MSCA aut = bmc.importMSCA(dir+"testcor_concur21_Example34.mxe");
		boolean bc = aut.getTransition().stream()
				.allMatch(t->t.satisfiesBranchingCondition(aut.getTransition(), 
						new HashSet<CAState>()));
		assertEquals(bc,false);	
	}
	

	@Test
	public void choreoConcur2021projectAndComposeTest() throws Exception {
		MSCA aut = bmc.importMSCA(dir+"testcor_concur21_Example34.mxe");
		List<MSCA> principals = IntStream.range(0,aut.getRank())
		.mapToObj(i->new ProjectionFunction().apply(aut,i, t->t.getLabel().getOfferer()))
		.collect(Collectors.toList());
	//	System.out.println(principals);
		MSCA closed_aut = new CompositionFunction().apply(principals, t->!t.getLabel().isMatch(), 100);
	//	MSCAIO.convertMSCAintoXML(dir+"testcor_concur21_Example34_closed_composition.mxe", closed_aut);
		
		boolean bc = closed_aut.getTransition().stream()
				.allMatch(t->t.satisfiesBranchingCondition(aut.getTransition(), 
						new HashSet<CAState>()));
		assertEquals(bc,false);	
	}

//	@Test
//	public void getRankZero() throws Exception {
//		
//		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"test_chor_controllablelazyoffer.mxe");
//		aut.setTransition(new HashSet<MSCATransition>());
//		assertEquals(aut.getRank(),0);
//	}
	
	//************************************exceptions*********************************************

	@Test
	public void projectionException1() throws IOException {

		MSCA aut = bdc.importMSCA(dir+"BusinessClient.mxe.data");
		assertThatThrownBy(() -> new ProjectionFunction().apply(aut,-1, null))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Index out of rank");
		
	}

	@Test
	public void projectionException2() throws IOException {

		MSCA aut = bdc.importMSCA(dir+"BusinessClient.mxe.data");
		assertThatThrownBy(() -> new ProjectionFunction().apply(aut,2, null))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Index out of rank");
		
	}
	
	@Test
	public void constructorTest_Exception_nullArgument() {
		assertThatThrownBy(() -> new MSCA(null))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Null argument");
	}

	@Test
	public void constructorTest_Exception_emptyTransitions() {
		assertThatThrownBy(() -> new MSCA(new HashSet<MSCATransition>()))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("No transitions");

	}

	@Test
	public void constructor_Exception_nullArgument() throws Exception {
		Set<MSCATransition> tr = new HashSet<>();
		tr.add(null);
	//	MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"test_chor_controllablelazyoffer.mxe");
		assertThatThrownBy(() -> new MSCA(tr))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Null element");
	}

	@Test
	public void constructor_Exception_differentRank() throws Exception {
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.idle);
		lab.add(CALabel.offer+"a");
		lab.add(CALabel.request+"a");

		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.idle);
		lab2.add(CALabel.idle);
		lab2.add(CALabel.offer+"a");
		lab2.add(CALabel.request+"a");


		BasicState bs0 = new BasicState("0",true,false);
		BasicState bs1 = new BasicState("1",true,false);
		BasicState bs2 = new BasicState("2",true,false);
		BasicState bs3 = new BasicState("3",true,false);

		Set<MSCATransition> tr = new HashSet<>();
		tr.add(new MSCATransition(new CAState(Arrays.asList(bs0,bs1,bs2),0,0),
				new CALabel(lab),
				new CAState(Arrays.asList(bs0,bs1,bs3),0,0),
				Modality.PERMITTED));
		CAState cs = new CAState(Arrays.asList(bs0,bs1,bs2,bs3),0,0);
		tr.add(new MSCATransition(cs,
				new CALabel(lab2),
				cs,
				Modality.PERMITTED));

		assertThatThrownBy(() -> new MSCA(tr))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Transitions with different rank");
	}
	
	//	@Test
	//	public void setFinalStatesOfPrinc_Exception_nullArgument() throws Exception {
	//		
	//		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"test_chor_controllablelazyoffer.mxe");
	//		assertThatThrownBy(() -> aut.setFinalStatesofPrincipals(new int[][] { {1,2},null}))
	//	    .isInstanceOf(IllegalArgumentException.class)
	//	    .hasMessageContaining("Final states contain a null array element or are empty");
	//	}

	@Test
	public void mpc_lazy_exception() throws Exception
	{
		
		MSCA orc = bmc.importMSCA(dir+"test_empty_orc_lazy.mxe");
		assertThatThrownBy(() -> new MpcSynthesisOperator().apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	public void chor_lazy_exception() throws Exception
	{
		
		MSCA orc = bmc.importMSCA(dir+"test_empty_orc_lazy.mxe");
		assertThatThrownBy(() -> new ChoreographySynthesisOperator().apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	public void orc_necessaryoffer_exception() throws Exception
	{
		//
		
		MSCA orc = bmc.importMSCA(dir+"(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		assertThatThrownBy(() -> new OrchestrationSynthesisOperator().apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}
	

	@Test
	public void union_empty() 
	{
		assertThatThrownBy(()->new UnionFunction().apply(new ArrayList<MSCA>()))
		.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void union_differentrank_exception() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);
		
		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bmc.importMSCA(dir+"BusinessClientxHotel_open.mxe"));

		assertThatThrownBy(() -> new UnionFunction().apply(aut))
		.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void noInitialState_exception() throws Exception
	{
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.offer+"a");

		BasicState bs0 = new BasicState("0",false,true);
		BasicState bs1 = new BasicState("1",false,true);


		Set<MSCATransition> tr = new HashSet<>();
		tr.add(new MSCATransition(new CAState(Arrays.asList(bs0),0,0),
				new CALabel(lab),
				new CAState(Arrays.asList(bs1),0,0),
				Modality.PERMITTED));

		assertThatThrownBy(() -> new MSCA(tr))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Not Exactly one Initial State found!");
	}

	@Test
	public void noFinalStatesInTransitions_exception() throws Exception
	{
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.offer+"a");

		BasicState bs0 = new BasicState("0",true,false);
		BasicState bs1 = new BasicState("1",false,false);


		Set<MSCATransition> tr = new HashSet<>();
		tr.add(new MSCATransition(new CAState(Arrays.asList(bs0),0,0),
				new CALabel(lab),
				new CAState(Arrays.asList(bs1),0,0),
				Modality.PERMITTED));

		assertThatThrownBy(() -> new MSCA(tr))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("No Final States!");
	}

	@Test
	public void compTestBound_noTransitions() throws Exception
	{
		List<MSCA> aut = new ArrayList<>(2);
		
		aut.add(bmc.importMSCA(dir+"forNullClosedAgreementComposition.mxe"));
		aut.add(bmc.importMSCA(dir+"forNullClosedAgreementComposition.mxe"));

		assertThatThrownBy(() -> new CompositionFunction().apply(aut,null,0))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("No transitions");
	}

	@Test
	public void ambiguousStates_exception() throws Exception
	{
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.offer+"a");

		BasicState bs = new BasicState("0",true,true);

		Set<MSCATransition> tr = new HashSet<>();
		tr.add(new MSCATransition(new CAState(Arrays.asList(bs),0,0),
				new CALabel(lab),
				new CAState(Arrays.asList(bs),0,0),
				Modality.PERMITTED));

		assertThatThrownBy(() -> new MSCA(tr))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Transitions have ambiguous states (different objects for the same state).");
	}
}
