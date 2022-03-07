package io.github.contractautomataproject.catlib.operators;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.IntStream;

import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

/**
 * The specification of the composition in first-order logic (expressed through Java Streams)
 * 
 * @author Davide Basile
 *
 */
public class CompositionSpecCheck implements BiPredicate<List<ModalAutomaton<CALabel>>,ModalAutomaton<CALabel>>{
	
	@Override
	public boolean test(List<ModalAutomaton<CALabel>> aut, ModalAutomaton<CALabel> comp) {
		//check if the composed automaton comp satisfies the spec
		return rank(aut,comp)&&initialState(aut,comp)&&states(aut,comp)&&finalStates(aut,comp)&&transitions(aut,comp);
	}
	
	private boolean rank(List<ModalAutomaton<CALabel>> aut, ModalAutomaton<CALabel> comp) 
	{
		return comp.getRank()==aut.stream()
				.mapToInt(a->a.getRank())
				.sum();
	}

	private boolean states(List<ModalAutomaton<CALabel>> aut, ModalAutomaton<CALabel> comp) 
	{
		return compareStatesPred(x->true,comp,aut);
	}


	private boolean finalStates(List<ModalAutomaton<CALabel>> aut, ModalAutomaton<CALabel> comp) 
	{
		return compareStatesPred(CAState::isFinalstate,comp,aut);
	}

	private boolean compareStatesPred(Predicate<CAState> pred, ModalAutomaton<CALabel> comp, List<ModalAutomaton<CALabel>> aut) {
		return IntStream.range(0,aut.size()).allMatch(j->//for all indexes j of operands automata aut.get(j)
			aut.get(j).getStates().parallelStream().filter(pred).allMatch(cs2-> //for all states cs2 of aut.get(j) satisfying pred
			comp.getStates().parallelStream().filter(pred).anyMatch(cs-> //there exists a state cs of the composition satisfying pred s.t.
			cs.getState().size()==comp.getRank() &&
			IntStream.range(0, cs2.getState().size()).allMatch(i_bs->	//for all  indexes i_bs of basic states of cs2 cs
				cs2.getState().get(i_bs).equals(cs.getState().get(i_bs+shift(aut,j))  //the basic state of cs2 at index bs_i is equal to the basic state of cs at index i_bs+shift
						)))));
	}
	
	private boolean initialState(List<ModalAutomaton<CALabel>> aut, ModalAutomaton<CALabel> comp) 
	{

		return 
		comp.getInitial().getState().size()==comp.getRank() &&
		IntStream.range(0,aut.size()).allMatch(j->  //forall indexes j of operands
		IntStream.range(0, aut.get(j).getInitial().getState().size()).allMatch(i_bs->  //forall indexes i_bs of basicstates of the initial state of operand at index j
		aut.get(j).getInitial().getState().get(i_bs).equals(comp.getInitial().getState().get(i_bs+shift(aut,j))) //the basic state of the initial state of operand j at index bs_i is equal to the basic state of the initial state of comp at index i_bs+shift
						)); 
	}
	
//	public CAState getInitialState(List<ModalAutomaton<CALabel>> aut) {
//		List<BasicState> ic = new ArrayList<>(aut.stream()
//				.mapToInt(a->a.getRank())
//				.sum());
//		
//		IntStream.range(0,aut.size())
//		.forEach(j->{IntStream.range(0, aut.get(j).getInitial().getState().size())
//				.forEach(i_bs->ic.set(i_bs+shift(aut,j), aut.get(j).getInitial().getState().get(i_bs)));}
//				);
//		
//		return new CAState(ic,0,0);
//	}

	private boolean transitions(List<ModalAutomaton<CALabel>> aut, ModalAutomaton<CALabel> comp) {

		//true if the source of transition t (of the operand at index ind)  is a component of composite state s
		TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,Integer,CAState> sourcestatepred= (t,ind,s)-> 
		s.getState().size()==comp.getRank()&&
		IntStream.range(0, t.getSource().getState().size()).allMatch(bi->
				t.getSource().getState().get(bi).equals(s.getState().get(bi+shift(aut,ind))
				));
		
		//------------predicates for match transitions---------------
		//t transition of composition, ti and tj transitions of operands, i and j index of operands
		
		PentaPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,Integer,Integer> labelmatchpred = (t,ti,tj,i,j)->
		t.getLabel().getAction().size()==comp.getRank() &&
		IntStream.range(0,t.getLabel().getAction().size()).allMatch(li->
		(li<shift(aut,i))?t.getLabel().getAction().get(li).equals(CALabel.idle)
				:(li<shift(aut,i+1))?t.getLabel().getAction().get(li).equals(ti.getLabel().getAction().get(li-shift(aut,i)))
						:(li<shift(aut,j))?t.getLabel().getAction().get(li).equals(CALabel.idle)
								:(li<shift(aut,j+1))?t.getLabel().getAction().get(li).equals(tj.getLabel().getAction().get(li-shift(aut,j)))
										:t.getLabel().getAction().get(li).equals(CALabel.idle)) ;

		PentaPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,Integer,Integer> targetmatchpred = (t,ti,tj,i,j)->
		t.getTarget().getState().size()==comp.getRank() &&
		IntStream.range(0,t.getTarget().getState().size()).allMatch(bsti->
		(bsti<shift(aut,i))?t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti))
				:(bsti<shift(aut,i+1))?t.getTarget().getState().get(bsti).equals(ti.getTarget().getState().get(bsti-shift(aut,i)))
						:(bsti<shift(aut,j))?t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti))
								:(bsti<shift(aut,j+1))?t.getTarget().getState().get(bsti).equals(tj.getTarget().getState().get(bsti-shift(aut,j)))
										:t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti)));		
		
		TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> modalitymatchpred = (t,ti,tj) ->((t.getModality().equals(ModalTransition.Modality.PERMITTED) && ti.getModality().equals(ModalTransition.Modality.PERMITTED) &&
				tj.getModality().equals(ModalTransition.Modality.PERMITTED))||
				(!t.getModality().equals(ModalTransition.Modality.PERMITTED) && (!ti.getModality().equals(ModalTransition.Modality.PERMITTED)||
				!tj.getModality().equals(ModalTransition.Modality.PERMITTED))));
		
		Predicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> pred_match = t-> 
		IntStream.range(0, aut.size()).anyMatch(i-> 		//exists i in [0,aut.size]
		IntStream.range(i+1, aut.size()).anyMatch(j->		//exists j in [i+1,aut.size]
		aut.get(i).getTransition().parallelStream().filter(ti->sourcestatepred.test(ti, i, t.getSource())).anyMatch(ti->
		aut.get(j).getTransition().parallelStream().filter(tj->sourcestatepred.test(tj, j, t.getSource())).anyMatch(tj->
		ti.getLabel().match(tj.getLabel()) && labelmatchpred.test(t, ti, tj, i, j) && targetmatchpred.test(t, ti, tj, i, j) && modalitymatchpred.test(t, ti, tj)
		))));
		
		//---------------------------------------------------------------
		
		
		//--------------------predicates for interleaving transitions -------------------
		
		TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, Integer> labelintrleavpred = (t,ti,i)->
		t.getLabel().getAction().size()==comp.getRank() &&
		IntStream.range(0,t.getLabel().getAction().size()).allMatch(li->
		(li<shift(aut,i))?t.getLabel().getAction().get(li).equals(CALabel.idle)
				:(li<shift(aut,i+1))?t.getLabel().getAction().get(li).equals(ti.getLabel().getAction().get(li-shift(aut,i)))
							:t.getLabel().getAction().get(li).equals(CALabel.idle));
		
		TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, Integer> targetstateintrleavpred = (t,ti,i)-> 
		t.getTarget().getState().size()==comp.getRank() &&
		IntStream.range(0,t.getTarget().getState().size()).allMatch(bsti->
		(bsti<shift(aut,i))?t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti))
				:(bsti<shift(aut,i+1))?t.getTarget().getState().get(bsti).equals(ti.getTarget().getState().get(bsti-shift(aut,i)))
								:t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti)));
		
		
		Predicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> pred_intrleav = t->
		IntStream.range(0, aut.size()).anyMatch(i-> 	
		aut.get(i).getTransition().parallelStream().filter(ti->sourcestatepred.test(ti, i, t.getSource())).anyMatch(ti->
		IntStream.range(0, aut.size()).filter(j->j!=i).allMatch(j->		
		aut.get(j).getTransition().parallelStream().filter(tj->sourcestatepred.test(tj, j, t.getSource())).allMatch(tj->
		!ti.getLabel().match(tj.getLabel()) && labelintrleavpred.test(t,ti,i) && targetstateintrleavpred.test(t,ti,i) && t.getModality().equals(ti.getModality())
		))));		
		
		//-------------------------------------------------------------------------------------
		
		return comp.getTransition().parallelStream().allMatch(t-> pred_match.test(t) || pred_intrleav.test(t));
	}
	
	private int shift(List<ModalAutomaton<CALabel>> aut, int j) {
		return IntStream.range(0, j).map(i->aut.get(i).getRank()).sum();
	}
}


interface PentaPredicate<T,U,V,Z,Q> {
	public boolean test(T arg1, U arg2, V arg3, Z arg4, Q arg5);
}
