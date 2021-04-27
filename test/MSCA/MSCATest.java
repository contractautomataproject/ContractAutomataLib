package MSCA;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import contractAutomata.MSCA;
import contractAutomata.MSCAIO;

public class MSCATest {

	//**********************************SCICO2020 case study*******************************************************************

	@Test
	public void compositionTestSCP2020_nonassociative() throws ParserConfigurationException, SAXException, IOException {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotel_open.mxe"));

		MSCA comp=MSCA.composition(aut, null,100);

		assert(comp.orchestration()==null);
	}

	@Test
	public void compositionTestSCP2020_BusinessClientxHotel_closed() throws ParserConfigurationException, SAXException, IOException {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/Hotel.mxe.data"));

		MSCA comp=MSCA.composition(aut, t->t.getLabel().isRequest(),100);
		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotel_closed.mxe");

		assert(checkTransitions(comp,test));
	}

	@Test
	public void compositionTestSCP2020_BusinessClientxHotel_open() throws ParserConfigurationException, SAXException, IOException {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/Hotel.mxe.data"));

		MSCA comp=MSCA.composition(aut, null,100);
		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotel_open.mxe");

		assert(checkTransitions(comp,test));
	}

	@Test
	public void compositionTestSCP2020_BusinessClientxHotelxEconomyClient_open_transitions() throws ParserConfigurationException, SAXException, IOException {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/Hotel.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/EconomyClient.mxe.data"));
		MSCA comp = MSCA.composition(aut, null,100);
		MSCA test= MSCAIO.parseXMLintoMSCA(dir+"/CAtest/BusinessClientxHotelxEconomyClient.mxe");

		assert(checkTransitions(comp,test));	
	}

	@Test
	public void compositionTestSCP2020_BusinessClientxHotelxEconomyClient_open_numStates() throws ParserConfigurationException, SAXException, IOException {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/Hotel.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/EconomyClient.mxe.data"));
		MSCA comp = MSCA.composition(aut, null,100);

		assert(comp.getNumStates()==343);	
	}

	@Test
	public void compAndOrcTestSCP2020_BusinessClientxHotelxEconomyClient_numStates() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/Hotel.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/EconomyClient.mxe.data"));
		MSCA comp=MSCA.composition(aut, t->t.getLabel().isRequest(),100);
		assert(comp.orchestration().getNumStates()==14);
	}	

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_transitions() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(BusinessClientxHotelxEconomyClient).mxe");
		MSCA test= MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Orc_(BusinessClientxHotelxEconomyClient).mxe");
		assert(checkTransitions(aut.orchestration(),test));
	}	

	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_numStates() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(BusinessClientxHotelxEconomyClient).mxe");
		assert(aut.orchestration().getNumStates()==14);
	}	

	@Test
	public void unionTest() throws ParserConfigurationException, SAXException, IOException {
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.load(dir+"/CAtest/BusinessClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/EconomyClient.mxe.data"));
		aut.add(MSCAIO.load(dir+"/CAtest/Hotel.mxe.data"));
		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/union_BusinessClient_EconomyClient_Hotel.mxe");
		assert(checkTransitions(MSCA.union(aut),test));
	}

	//*******************************************LMCS2020 case study********************************************************

	@Test
	public void chorTestLMCS2020numStates() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		assert(aut.choreography().getNumStates()==13);
	}

	@Test
	public void chorTestLMCS2020Transitions() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Chor_(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		MSCA cor=aut.clone();
		cor=cor.choreography();
		int attempts=3;
		for (int i=0;i<attempts;i++)
		{
			if (checkTransitions(cor,test))
				return;
			cor=aut.clone();
			cor=cor.choreography();//non-deterministic
		}
		assert(checkTransitions(cor,test));
	}

	@Test
	public void chorSmallerTestTransitions() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(ClientxPriviledgedClientxBrokerxHotelxHotel).mxe");
		MSCA cor=aut.clone();
		cor=cor.synthesis((x,t,bad) -> 	!x.satisfiesBranchingCondition(t, bad)||!x.getLabel().isMatch()||bad.contains(x.getTarget()),
		(x,t,bad) -> bad.contains(x.getTarget()) && x.isUncontrollableChoreography(t, bad));

		assert(cor==null);
	}



	@Test
	public void orcTestLMCS2020numStates() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");
		assert(aut.orchestration().getNumStates()==37);
	}

	@Test
	public void orcTestLMCS2020Transitions() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");
		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Orc_(ClientxClientxBrokerxHotelxPriviledgedHotel).mxe");

		assert(checkTransitions(aut.orchestration(),test));
	}

	@Test
	public void mpcEmptyTestLMCS2020() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(ClientxClientxBrokerxHotelxPriviledgedUrgentHotel).mxe");
		MSCA mpc=aut.mpc();

		assert(mpc==null);
	}

	//**********************************************************************************************


	@Test
	public void chorTestControllableLazyOffernumStates() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_chor_controllablelazyoffer.mxe");
		assert(aut.choreography().getNumStates()==3);
	}

	@Test
	public void chorTestControllableLazyOfferTransitions() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_chor_controllablelazyoffer.mxe");
		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Chor_(test_chor_controllablelazyoffer).mxe");
		MSCA cor=aut.clone();
		cor=cor.choreography();
		int attempts=3;
		for (int i=0;i<attempts;i++)
		{
			if (checkTransitions(cor,test))
				return;
			cor=aut.clone();
			cor=cor.choreography();//non-deterministic
		}
		assert(checkTransitions(cor,test));
	}

	@Test
	public void cloneTest() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_urgent.mxe");
		assert(checkTransitions(aut,aut.clone()));
	}

	@Test
	public void compTestSimple() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		List<MSCA> aut = new ArrayList<>(2);
		String dir = System.getProperty("user.dir");
		aut.add(MSCAIO.parseXMLintoMSCA(dir+"/CAtest/A.mxe"));
		aut.add(MSCAIO.parseXMLintoMSCA(dir+"/CAtest/B.mxe"));

		MSCA comp=MSCA.composition(aut,null,100);
		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(AxB).mxe");

		assert(checkTransitions(comp,test));
	}

	@Test
	public void mpcTest_nonempty() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_urgent.mxe");
		assert(aut.mpc().getNumStates()==2);
	}

	@Test
	public void orcTest_empty() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		MSCA orc = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_empty_orc.mxe");
		assert(orc.orchestration()==null);
	}

	@Test
	public void orcTest_empty_lazy() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		MSCA orc = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_empty_orc_lazy.mxe");
		assert(orc.orchestration()==null);
	}

	@Test
	public void orcTest_nonempty() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		MSCA orc = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_empty_orc_lazy.mxe");
		assert(orc.orchestration()==null);
	}

	@Test
	public void chorTest_empty() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_lazy_empty_cor.mxe");
		assert(aut.choreography()==null);
	}

	@Test
	public void chorTest_urgent_empty() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/test_chor_urgentoffer.mxe");
		assert(aut.choreography()==null);
	}
	
	static boolean checkTransitions(MSCA aut, MSCA test) {
		Set<String> autTr=aut.getTransition().parallelStream()
				.map(t->t.toString())
				.collect(Collectors.toSet());
		Set<String> testTr=test.getTransition().parallelStream()
				.map(t->t.toString())
				.collect(Collectors.toSet());
		return autTr.parallelStream()
				.allMatch(t->testTr.contains(t))
				&&
				testTr.parallelStream()
				.allMatch(t->autTr.contains(t));
	}
}
