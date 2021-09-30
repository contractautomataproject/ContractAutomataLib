package io.github.davidebasile.contractautomatatest;


import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.MSCATransition;
import io.github.davidebasile.contractautomata.automaton.transition.MSCATransition.Modality;

public class MSCATest {
//	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
//	private final MxeConverter bmc = new MxeConverter();
//	private final DataConverter bdc = new DataConverter();


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
	public void ambiguousStates_exception() throws Exception
	{
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.offer+"a");

		BasicState bs1 = new BasicState("0",true,false);
		BasicState bs2 = new BasicState("0",false,true);

		Set<MSCATransition> tr = new HashSet<>();
		tr.add(new MSCATransition(new CAState(Arrays.asList(bs1),0,0),
				new CALabel(lab),
				new CAState(Arrays.asList(bs2),0,0),
				Modality.PERMITTED));

		tr.add(new MSCATransition(new CAState(Arrays.asList(bs2),0,0),
				new CALabel(lab),
				new CAState(Arrays.asList(bs2),0,0),
				Modality.PERMITTED));
		assertThatThrownBy(() -> new MSCA(tr))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Transitions have ambiguous states (different objects for the same state).");
	}
	
	//	@Test
	//	public void setFinalStatesOfPrinc_Exception_nullArgument() throws Exception {
	//		
	//		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"test_chor_controllablelazyoffer.mxe");
	//		assertThatThrownBy(() -> aut.setFinalStatesofPrincipals(new int[][] { {1,2},null}))
	//	    .isInstanceOf(IllegalArgumentException.class)
	//	    .hasMessageContaining("Final states contain a null array element or are empty");
	//	}

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



	//	@Test
	//	public void getRankZero() throws Exception {
	//		
	//		MSCA aut = MSCAIO.parseXMLintoMSCA(dir+"test_chor_controllablelazyoffer.mxe");
	//		aut.setTransition(new HashSet<MSCATransition>());
	//		assertEquals(aut.getRank(),0);
	//	}

}
