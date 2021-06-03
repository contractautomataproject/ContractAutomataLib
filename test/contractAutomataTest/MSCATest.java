package contractAutomataTest;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

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
import contractAutomata.MSCA;
import contractAutomata.MSCAIO;
import contractAutomata.MSCATransition;
import contractAutomata.MSCATransition.Modality;

public class MSCATest {

	//**********************************SCICO2020 case study*******************************************************************

	@Test
	public void compositionTestSCP2020_nonassociative() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotel_open.mxe"));

		MSCA comp=MSCA.composition(aut, null,100);
		assertEquals(comp.orchestration(),null);
	}

	@Test
	public void compositionTestSCP2020_BusinessClientxHotel_closed() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/Hotel.mxe.data"));

		MSCA comp=MSCA.composition(aut, t->t.getLabel().isRequest(),100);
		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotel_closed.mxe");
		assertEquals(checkTransitions(comp,test),true);
	}

	@Test
	public void compositionTestSCP2020_BusinessClientxHotel_open() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/Hotel.mxe.data"));

		MSCA comp=MSCA.composition(aut, null,100);
		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotel_open.mxe");
		assertEquals(checkTransitions(comp,test),true);
	}

	@Test
	public void compositionTestSCP2020_BusinessClientxHotelxEconomyClient_open_transitions() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/Hotel.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/EconomyClient.mxe.data"));
		MSCA comp = MSCA.composition(aut, null,100);
		MSCA test= MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe");
		assertEquals(checkTransitions(comp,test),true);	
	}

	@Test
	public void compAndOrcTestSCP2020_BusinessClientxHotelxEconomyClient() throws Exception
	{
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/Hotel.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/EconomyClient.mxe.data"));
		MSCA comp=MSCA.composition(aut, t->t.getLabel().isRequest(),100);
		
		MSCA test= MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Orc_(BusinessClientxHotelxEconomyClient)_test.mxe");
		assertEquals(checkTransitions(comp.orchestration(),test),true);

//		assertEquals(comp.orchestration().getNumStates(),14);
	}	

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_transitions() throws Exception
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(BusinessClientxHotelxEconomyClient).mxe");
		MSCA test= MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Orc_(BusinessClientxHotelxEconomyClient)_test.mxe");
		assertEquals(checkTransitions(aut.orchestration(),test),true);
	}	


	@Test
	public void unionTest() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/EconomyClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/Hotel.mxe.data"));
		MSCA union = MSCA.union(aut);
		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/union_BusinessClient_EconomyClient_Hotel.mxe");
		assertEquals(checkTransitions(union,test),true);
	}

	//*******************************************LMCS2020 case study********************************************************


	@Test
	public void chorTestLMCS2020Transitions() throws Exception, TransformerException
	{
		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		MSCA test1 = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		MSCA test2 = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_1.mxe");
		MSCA test3 = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_2.mxe");
		MSCA test4 = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_3.mxe");
		MSCA test5 = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_4.mxe");
		MSCA test6 = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_5.mxe");
		MSCA test7 = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_6.mxe");
		MSCA test8 = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_7.mxe");

		aut = aut.choreography();
		boolean check = checkTransitions(aut,test1)||checkTransitions(aut,test2)||checkTransitions(aut,test3)
				||checkTransitions(aut,test4)||checkTransitions(aut,test5)
				||checkTransitions(aut,test6)||checkTransitions(aut,test7)
				||checkTransitions(aut,test8);//||checkTransitions(aut,test7); 

//		do {
//			aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
//			aut = aut.choreography();
//			check = checkTransitions(aut,test1)||checkTransitions(aut,test2)||checkTransitions(aut,test3)
//					||checkTransitions(aut,test4)||checkTransitions(aut,test5)
//					||checkTransitions(aut,test6)||checkTransitions(aut,test7)
//					||checkTransitions(aut,test8);//||checkTransitions(aut,test7); 
//		} while (check);
//		if (!check)
//			MSCAIO.convertMSCAintoXML(dir+"/CAtest/Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel)_8.mxe", aut);
		
		assertEquals(check,true);
	}

	@Test
	public void chorSmallerTestTransitions() throws Exception
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		MSCA cor=aut.clone();
		cor=cor.synthesis((x,t,bad) -> 	!x.satisfiesBranchingCondition(t, bad)||!x.getLabel().isMatch()||bad.contains(x.getTarget()),
				(x,t,bad) -> bad.contains(x.getTarget()) && x.isUncontrollableChoreography(t, bad));

		assertEquals(cor,null);
	}



	@Test
	public void orcTestLMCS2020Transitions() throws Exception
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");
		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Orc_(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");

		assertEquals(checkTransitions(aut.orchestration(),test),true);
	}

	@Test
	public void mpcEmptyTestLMCS2020() throws Exception
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(ClientxClientxBrokerxHotelxPriviledgedUrgentHotel).mxe");
		MSCA mpc=aut.mpc();

		assertEquals(mpc,null);
	}

	@Test
	public void mpcEmptyTestNoDangling() throws Exception
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_empty_mpc_nodangling.mxe");
		MSCA mpc=aut.mpc();

		assertEquals(mpc,null);
	}
	
	@Test
	public void orcEmptyTestNoDangling() throws Exception
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_empty_orc_nodangling.mxe");
		assertEquals(aut.orchestration(),null);
	}
	
	//**********************************************************************************************


	@Test
	public void chorTestControllableLazyOfferTransitions() throws Exception
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_chor_controllablelazyoffer.mxe");
		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Chor_(test_chor_controllablelazyoffer).mxe");
		assertEquals(checkTransitions(aut.choreography(),test),true);
	}

	@Test
	public void cloneTest() throws Exception
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_urgent.mxe");
		assertEquals(checkTransitions(aut,aut.clone()),true);
	}

	@Test
	public void compTestSimple() throws Exception
	{
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.parseXMLintoMSCA(dir+"/CAtest/A.mxe"));
		aut.add(MSCAIO.parseXMLintoMSCA(dir+"/CAtest/B.mxe"));

		MSCA comp=MSCA.composition(aut,null,100);
		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(AxB).mxe");

		assertEquals(checkTransitions(comp,test),true);
	}


	@Test
	public void compTestEmptySimple() throws Exception
	{
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.parseXMLintoMSCA(dir+"/CAtest/forNullClosedAgreementComposition.mxe"));
		aut.add(MSCAIO.parseXMLintoMSCA(dir+"/CAtest/forNullClosedAgreementComposition.mxe"));

		MSCA comp=MSCA.composition(aut,t->t.getLabel().isRequest(),100);

		assertEquals(comp,null);
	}

	@Test
	public void mpcTest_nonempty() throws Exception
	{
		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_urgent.mxe");
		assertEquals(aut.mpc().getNumStates(),2);
	}

	@Test
	public void orcTest_empty() throws Exception
	{
		String dir = System.getProperty("user.dir");
		MSCA orc = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_empty_orc.mxe");
		assertEquals(orc.orchestration(),null);
	}

	@Test
	public void orcTest_empty_lazy() throws Exception
	{
		String dir = System.getProperty("user.dir");
		MSCA orc = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_empty_orc_lazy.mxe");
		assertEquals(orc.orchestration(),null);
	}

	@Test
	public void orcTest_nonempty() throws Exception
	{
		String dir = System.getProperty("user.dir");
		MSCA orc = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_empty_orc_lazy.mxe");
		assertEquals(orc.orchestration(),null);
	}

	@Test
	public void chorTest_empty() throws Exception
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_lazy_empty_cor.mxe");
		assertEquals(aut.choreography(),null);
	}

	@Test
	public void chorTest_urgent_empty() throws Exception
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_chor_urgentoffer.mxe");
		assertEquals(aut.choreography(),null);
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

	@Test
	public void setInitialCATest() throws Exception {
		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data");

		CAState newInitial = aut.getStates().parallelStream()
				.filter(s->s!=aut.getInitial())
				.findFirst()
				.orElse(null);

		aut.setInitialCA(newInitial);

		assertEquals(aut.getInitial(),newInitial);
	}

	@Test
	public void union_null() 
	{
		assertEquals(MSCA.union(null),null);
	}

	@Test
	public void union_statelabelsnotnumbers() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/testgraph.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/testgraph.data"));
		
		MSCA union = MSCA.union(aut);
//		MSCAIO.convertMSCAintoXML(dir+"/CAtest/union_testgraph_testgraph.mxe", union);
		
		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/union_testgraph_testgraph.mxe");
		assertEquals(checkTransitions(union,test),true);
	}
	
	@Test
	public void choreoConcur2021ex25() throws Exception {
		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/testcor_concur21_Example25.mxe");
		boolean bc = aut.getTransition().stream()
				.allMatch(t->t.satisfiesBranchingCondition(aut.getTransition(), 
						new HashSet<CAState>()));
		assertEquals(bc,false);	
	}
	
	@Test
	public void choreoConcur2021ex34() throws Exception {
		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/testcor_concur21_Example34.mxe");
		boolean bc = aut.getTransition().stream()
				.allMatch(t->t.satisfiesBranchingCondition(aut.getTransition(), 
						new HashSet<CAState>()));
		assertEquals(bc,false);	
	}
	

	@Test
	public void choreoConcur2021projectAndComposeTest() throws Exception {
		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/testcor_concur21_Example34.mxe");
		List<MSCA> principals = IntStream.range(0,aut.getRank())
		.mapToObj(i->aut.projection(i, t->t.getLabel().getOfferer()))
		.collect(Collectors.toList());
	//	System.out.println(principals);
		MSCA closed_aut = MSCA.composition(principals, t->!t.getLabel().isMatch(), 100);
	//	MSCAIO.convertMSCAintoXML(dir+"/CAtest/testcor_concur21_Example34_closed_composition.mxe", closed_aut);
		
		boolean bc = closed_aut.getTransition().stream()
				.allMatch(t->t.satisfiesBranchingCondition(aut.getTransition(), 
						new HashSet<CAState>()));
		assertEquals(bc,false);	
	}

//	@Test
//	public void getRankZero() throws Exception {
//		String dir = System.getProperty("user.dir");
//		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_chor_controllablelazyoffer.mxe");
//		aut.setTransition(new HashSet<MSCATransition>());
//		assertEquals(aut.getRank(),0);
//	}
	
	//************************************exceptions*********************************************

	
	
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
	//	MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_chor_controllablelazyoffer.mxe");
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
	//		String dir = System.getProperty("user.dir");
	//		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_chor_controllablelazyoffer.mxe");
	//		assertThatThrownBy(() -> aut.setFinalStatesofPrincipals(new int[][] { {1,2},null}))
	//	    .isInstanceOf(IllegalArgumentException.class)
	//	    .hasMessageContaining("Final states contain a null array element or are empty");
	//	}

	@Test
	public void mpc_lazy_exception() throws Exception
	{
		String dir = System.getProperty("user.dir");
		MSCA orc = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_empty_orc_lazy.mxe");
		assertThatThrownBy(() -> orc.mpc())
		.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	public void chor_lazy_exception() throws Exception
	{
		String dir = System.getProperty("user.dir");
		MSCA orc = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_empty_orc_lazy.mxe");
		assertThatThrownBy(() -> orc.choreography())
		.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	public void orc_necessaryoffer_exception() throws Exception
	{
		//
		String dir = System.getProperty("user.dir");
		MSCA orc = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		assertThatThrownBy(() -> orc.orchestration())
		.isInstanceOf(UnsupportedOperationException.class);
	}

	@Test
	public void union_differentrank_exception() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotel_open.mxe"));

		assertThatThrownBy(() -> MSCA.union(aut))
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
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.parseXMLintoMSCA(dir+"/CAtest/forNullClosedAgreementComposition.mxe"));
		aut.add(MSCAIO.parseXMLintoMSCA(dir+"/CAtest/forNullClosedAgreementComposition.mxe"));

		assertThatThrownBy(() -> MSCA.composition(aut,null,0))
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
