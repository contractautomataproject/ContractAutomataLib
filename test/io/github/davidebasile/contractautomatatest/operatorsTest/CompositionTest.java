package io.github.davidebasile.contractautomatatest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.MSCATransition;
import io.github.davidebasile.contractautomata.converters.DataConverter;
import io.github.davidebasile.contractautomata.converters.MxeConverter;
import io.github.davidebasile.contractautomata.operators.CompositionFunction;
import io.github.davidebasile.contractautomata.operators.OrchestrationSynthesisOperator;
import io.github.davidebasile.contractautomata.operators.TriPredicate;
import io.github.davidebasile.contractautomata.requirements.Agreement;
import io.github.davidebasile.contractautomatatest.MSCATest;

public class CompositionTest {

	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final MxeConverter bmc = new MxeConverter();
	private final DataConverter bdc = new DataConverter();

    //**********************  SPEC   ***********************//
	
	public boolean rank(List<MSCA> aut, MSCA comp) throws IOException 
	{
		return comp.getRank()==aut.stream()
				.mapToInt(a->a.getRank())
				.sum();
	}

	public boolean states(List<MSCA> aut, MSCA comp) throws Exception
	{
		return compareStatesPred(x->true,comp,aut);
	}


	public boolean finalStates(List<MSCA> aut, MSCA comp) throws Exception
	{
		return compareStatesPred(CAState::isFinalstate,comp,aut);
	}

	private boolean compareStatesPred(Predicate<CAState> pred, MSCA comp, List<MSCA> aut) {
		return IntStream.range(0,aut.size()).allMatch(j->//for all indexes j of operands automata aut.get(j)
			aut.get(j).getStates().parallelStream().filter(pred).allMatch(cs2-> //for all states cs2 of aut.get(j) satisfying pred
			comp.getStates().parallelStream().filter(pred).anyMatch(cs-> //there exists a state cs of the composition satisfying pred s.t.
			cs.getState().size()==comp.getRank() &&
			IntStream.range(0, cs2.getState().size()).allMatch(i_bs->	//for all  indexes i_bs of basic states of cs2 cs
				cs2.getState().get(i_bs).equals(cs.getState().get(i_bs+shift(aut,j))  //the basic state of cs2 at index bs_i is equal to the basic state of cs at index i_bs+shift
						)))));
	}
	
	public boolean initialState(List<MSCA> aut, MSCA comp) throws Exception
	{

		return 
		comp.getInitial().getState().size()==comp.getRank() &&
		IntStream.range(0,aut.size()).allMatch(j->  //forall indexes j of operands
		IntStream.range(0, aut.get(j).getInitial().getState().size()).allMatch(i_bs->  //forall indexes i_bs of basicstates of the initial state of operand at index j
		aut.get(j).getInitial().getState().get(i_bs).equals(comp.getInitial().getState().get(i_bs+shift(aut,j))) //the basic state of the initial state of operand j at index bs_i is equal to the basic state of the initial state of comp at index i_bs+shift
						)); 
	}

	public boolean transitions(List<MSCA> aut, MSCA comp) throws Exception {

		//true if the source of transition t (of the operand at index ind)  is a component of composite state s
		TriPredicate<MSCATransition,Integer,CAState> sourcestatepred= (t,ind,s)-> 
		s.getState().size()==comp.getRank()&&
		IntStream.range(0, t.getSource().getState().size()).allMatch(bi->
				t.getSource().getState().get(bi).equals(s.getState().get(bi+shift(aut,ind))
				));
		
		//------------predicates for match transitions---------------
		//t transition of composition, ti and tj transitions of operands, i and j index of operands
		
		PentaPredicate<MSCATransition,MSCATransition,MSCATransition,Integer,Integer> labelmatchpred = (t,ti,tj,i,j)->
		t.getLabel().getLabelAsList().size()==comp.getRank() &&
		IntStream.range(0,t.getLabel().getLabelAsList().size()).allMatch(li->
		(li<shift(aut,i))?t.getLabel().getLabelAsList().get(li).equals(CALabel.idle)
				:(li<shift(aut,i+1))?t.getLabel().getLabelAsList().get(li).equals(ti.getLabel().getLabelAsList().get(li-shift(aut,i)))
						:(li<shift(aut,j))?t.getLabel().getLabelAsList().get(li).equals(CALabel.idle)
								:(li<shift(aut,j+1))?t.getLabel().getLabelAsList().get(li).equals(tj.getLabel().getLabelAsList().get(li-shift(aut,j)))
										:t.getLabel().getLabelAsList().get(li).equals(CALabel.idle)) ;

		PentaPredicate<MSCATransition,MSCATransition,MSCATransition,Integer,Integer> targetmatchpred = (t,ti,tj,i,j)->
		t.getTarget().getState().size()==comp.getRank() &&
		IntStream.range(0,t.getTarget().getState().size()).allMatch(bsti->
		(bsti<shift(aut,i))?t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti))
				:(bsti<shift(aut,i+1))?t.getTarget().getState().get(bsti).equals(ti.getTarget().getState().get(bsti-shift(aut,i)))
						:(bsti<shift(aut,j))?t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti))
								:(bsti<shift(aut,j+1))?t.getTarget().getState().get(bsti).equals(tj.getTarget().getState().get(bsti-shift(aut,j)))
										:t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti)));		
		
		TriPredicate<MSCATransition,MSCATransition,MSCATransition> modalitymatchpred = (t,ti,tj) ->((t.getModality().equals(MSCATransition.Modality.PERMITTED) && ti.getModality().equals(MSCATransition.Modality.PERMITTED) &&
				tj.getModality().equals(MSCATransition.Modality.PERMITTED))||
				(!t.getModality().equals(MSCATransition.Modality.PERMITTED) && (!ti.getModality().equals(MSCATransition.Modality.PERMITTED)||
				!tj.getModality().equals(MSCATransition.Modality.PERMITTED))));
		
		Predicate<MSCATransition> pred_match = t-> 
		IntStream.range(0, aut.size()).anyMatch(i-> 		//exists i in [0,aut.size]
		IntStream.range(i+1, aut.size()).anyMatch(j->		//exists j in [i+1,aut.size]
		aut.get(i).getTransition().parallelStream().filter(ti->sourcestatepred.test(ti, i, t.getSource())).anyMatch(ti->
		aut.get(j).getTransition().parallelStream().filter(tj->sourcestatepred.test(tj, j, t.getSource())).anyMatch(tj->
		ti.getLabel().match(tj.getLabel()) && labelmatchpred.test(t, ti, tj, i, j) && targetmatchpred.test(t, ti, tj, i, j) && modalitymatchpred.test(t, ti, tj)
		))));
		
		//---------------------------------------------------------------
		
		
		//--------------------predicates for interleaving transitions -------------------
		
		TriPredicate<MSCATransition, MSCATransition, Integer> labelintrleavpred = (t,ti,i)->
		t.getLabel().getLabelAsList().size()==comp.getRank() &&
		IntStream.range(0,t.getLabel().getLabelAsList().size()).allMatch(li->
		(li<shift(aut,i))?t.getLabel().getLabelAsList().get(li).equals(CALabel.idle)
				:(li<shift(aut,i+1))?t.getLabel().getLabelAsList().get(li).equals(ti.getLabel().getLabelAsList().get(li-shift(aut,i)))
							:t.getLabel().getLabelAsList().get(li).equals(CALabel.idle));
		
		TriPredicate<MSCATransition, MSCATransition, Integer> targetstateintrleavpred = (t,ti,i)-> 
		t.getTarget().getState().size()==comp.getRank() &&
		IntStream.range(0,t.getTarget().getState().size()).allMatch(bsti->
		(bsti<shift(aut,i))?t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti))
				:(bsti<shift(aut,i+1))?t.getTarget().getState().get(bsti).equals(ti.getTarget().getState().get(bsti-shift(aut,i)))
								:t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti)));
		
		
		Predicate<MSCATransition> pred_intrleav = t->
		IntStream.range(0, aut.size()).anyMatch(i-> 	
		aut.get(i).getTransition().parallelStream().filter(ti->sourcestatepred.test(ti, i, t.getSource())).anyMatch(ti->
		IntStream.range(0, aut.size()).filter(j->j!=i).allMatch(j->		
		aut.get(j).getTransition().parallelStream().filter(tj->sourcestatepred.test(tj, j, t.getSource())).allMatch(tj->
		!ti.getLabel().match(tj.getLabel()) && labelintrleavpred.test(t,ti,i) && targetstateintrleavpred.test(t,ti,i) && t.getModality().equals(ti.getModality())
		))));		
		
		//-------------------------------------------------------------------------------------
		
		return comp.getTransition().parallelStream().allMatch(t-> pred_match.test(t) || pred_intrleav.test(t));
	}
	
	private int shift(List<MSCA> aut, int j) {
		return IntStream.range(0, j).map(i->aut.get(i).getRank()).sum();
	}
	
	public boolean testCompositionSpec(List<MSCA> aut) throws IOException, Exception {
		MSCA comp=new CompositionFunction().apply(aut, null,100);
		return rank(aut,comp)&&initialState(aut,comp)&&states(aut,comp)&&finalStates(aut,comp)&&transitions(aut,comp);
	}

	//***********************************testing impl against spec on scenarios **********************************************
	
	
	@Test
	public void scico2020Test() throws Exception{
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.mxe.data"));
		assertTrue(testCompositionSpec(aut));
	}

	@Test
	public void lmcs2020Test() throws Exception{
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bmc.importMSCA(dir+"Client.mxe"));
		aut.add(bmc.importMSCA(dir+"Client.mxe"));
		aut.add(bmc.importMSCA(dir+"Broker.mxe"));
		aut.add(bmc.importMSCA(dir+"HotelLMCS.mxe"));
		aut.add(bmc.importMSCA(dir+"PriviledgedHotel.mxe"));
		assertTrue(testCompositionSpec(aut));
	}



	@Test
	public void lmcs2020Test2() throws Exception{
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bmc.importMSCA(dir+"Client.mxe"));
		aut.add(bmc.importMSCA(dir+"PriviledgedClient.mxe"));
		aut.add(bmc.importMSCA(dir+"Broker.mxe"));
		aut.add(bmc.importMSCA(dir+"HotelLMCS.mxe"));
		aut.add(bmc.importMSCA(dir+"HotelLMCS.mxe"));
		assertTrue(testCompositionSpec(aut));
	}

	//**********************************SCICO2020 case study*******************************************************************


	@Test
	public void compositionTestSCP2020_BusinessClientxHotel_open() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));

		MSCA comp=new CompositionFunction().apply(aut, null,100);
		MSCA test = bmc.importMSCA(dir+"BusinessClientxHotel_open.mxe");
		assertEquals(MSCATest.checkTransitions(comp,test),true);
	}

	@Test
	public void compositionTestSCP2020_nonassociative() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bmc.importMSCA(dir+"BusinessClientxHotel_open.mxe"));

		MSCA comp=new CompositionFunction().apply(aut, null,100);
		assertEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(comp),null);
	}

	@Test
	public void compositionTestSCP2020_BusinessClientxHotel_closed() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));

		MSCA comp=new CompositionFunction().apply(aut, t->t.getLabel().isRequest(),100);
		MSCA test = bmc.importMSCA(dir+"BusinessClientxHotel_closed.mxe");
		assertEquals(MSCATest.checkTransitions(comp,test),true);
	}



	@Test
	public void compositionTestSCP2020_BusinessClientxHotelxEconomyClient_open_transitions() throws Exception {
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.mxe.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.mxe.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.mxe.data"));
		MSCA comp = new CompositionFunction().apply(aut, null,100);
		MSCA test= bmc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.mxe");
		assertEquals(MSCATest.checkTransitions(comp,test),true);	
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
		assertEquals(MSCATest.checkTransitions(new OrchestrationSynthesisOperator(new Agreement()).apply(comp),test),true);

		//		assertEquals(comp.orchestration().getNumStates(),14);
	}	

	///////////////

	@Test
	public void compTestSimple() throws Exception
	{
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bmc.importMSCA(dir+"A.mxe"));
		aut.add(bmc.importMSCA(dir+"B.mxe"));

		MSCA comp=new CompositionFunction().apply(aut,null,100);
		MSCA test = bmc.importMSCA(dir+"(AxB).mxe");

		assertEquals(MSCATest.checkTransitions(comp,test),true);
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
	public void compTestBound_noTransitions() throws Exception
	{
		List<MSCA> aut = new ArrayList<>(2);

		aut.add(bmc.importMSCA(dir+"forNullClosedAgreementComposition.mxe"));
		aut.add(bmc.importMSCA(dir+"forNullClosedAgreementComposition.mxe"));

		assertThatThrownBy(() -> new CompositionFunction().apply(aut,null,0))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("No transitions");
	}

}



interface PentaPredicate<T,U,V,Z,Q> {
	public boolean test(T arg1, U arg2, V arg3, Z arg4, Q arg5);
}