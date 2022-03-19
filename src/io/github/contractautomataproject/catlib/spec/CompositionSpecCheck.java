package io.github.contractautomataproject.catlib.spec;

import java.util.List;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.operators.TriPredicate;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;

/**
 * The specification of the composition in first-order logic (expressed through Java Streams)
 * 
 * @author Davide Basile
 *
 */
public class CompositionSpecCheck implements BiPredicate<List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>>,Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>>{

	@Override
	public boolean test(List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut, Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp) {
		//check if the composed automaton comp satisfies the spec
		return rank(aut,comp)&&initialState(aut,comp)&&states(aut,comp)&&finalStates(aut,comp)&&transitions(aut,comp);
	}

	private boolean rank(List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut, Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp) 
	{
		return comp.getRank()==aut.stream()
				.mapToInt(Automaton::getRank)
				.sum();
	}

	private boolean states(List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut, Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp) 
	{
		return compareStatesPred(x->true,comp,aut);
	}


	private boolean finalStates(List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut, Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp) 
	{
		return compareStatesPred(State::isFinalState,comp,aut);
	}

	private boolean compareStatesPred(Predicate<State<String>> pred, Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp, List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut) {
		return IntStream.range(0,aut.size()).allMatch(j->//for all indexes j of operands automata aut.get(j)
		aut.get(j).getStates().parallelStream().filter(pred).allMatch(cs2-> //for all states cs2 of aut.get(j) satisfying pred
		comp.getStates().parallelStream().filter(pred).anyMatch(cs-> //there exists a state cs of the composition satisfying pred s.t.
		cs.getState().size()==comp.getRank() &&
		IntStream.range(0, cs2.getState().size()).allMatch(iBs->	//for all  indexes iBs of basic states of cs2 cs
		cs2.getState().get(iBs).equals(cs.getState().get(iBs+shift(aut,j))  //the basic state of cs2 at index bs_i is equal to the basic state of cs at index iBs+shift
				)))));
	}

	private boolean initialState(List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut, Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp) 
	{
		return comp.getInitial().getState().size()==comp.getRank() &&
				IntStream.range(0,aut.size()).allMatch(j->  //forall indexes j of operands
				IntStream.range(0, aut.get(j).getInitial().getState().size()).allMatch(iBs->  //forall indexes iBs of basicstates of the initial state of operand at index j
				aut.get(j).getInitial().getState().get(iBs).equals(comp.getInitial().getState().get(iBs+shift(aut,j))) //the basic state of the initial state of operand j at index bs_i is equal to the basic state of the initial state of comp at index iBs+shift
						)); 
	}

	private boolean transitions(List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut, Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp) {

		//true if the source of transition t (of the operand at index ind)  is a component of composite state s
		TriPredicate<ModalTransition<String,String,State<String>,CALabel>,Integer,State<String>> sourcestatepred= (t,ind,s)-> 
		s.getState().size()==comp.getRank()&&
		IntStream.range(0, t.getSource().getState().size()).allMatch(bi->
		t.getSource().getState().get(bi).equals(s.getState().get(bi+shift(aut,ind))
				));

		List<Set<ModalTransition<String,String,State<String>,CALabel>>> autTr = aut.stream()
				.map(Automaton::getTransition)
				.collect(Collectors.toList());


		return comp.getTransition().parallelStream().allMatch(t->predMatch(aut,comp,sourcestatepred,autTr).test(t)||predIntrleav(aut,comp,sourcestatepred,autTr).test(t));
	}


	/**
	 * predicates for match transitions
	 * 
	 */
	private Predicate<ModalTransition<String,String,State<String>,CALabel>> predMatch(List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut, Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp, 
			TriPredicate<ModalTransition<String,String,State<String>,CALabel>,Integer,State<String>> sourcestatepred, 
			List<Set<ModalTransition<String,String,State<String>,CALabel>>> autTr){
		
		//t transition of composition, ti and tj transitions of operands, i and j index of operands

		PentaPredicate<ModalTransition<String,String,State<String>,CALabel>,ModalTransition<String,String,State<String>,CALabel>,
		ModalTransition<String,String,State<String>,CALabel>,Integer,Integer> labelmatchpred = (t,ti,tj,i,j)->
		t.getLabel().getAction().size()==comp.getRank() &&
		IntStream.range(0,t.getLabel().getAction().size()).allMatch(li->
		(li<shift(aut,i))?t.getLabel().getAction().get(li).equals(CALabel.IDLE)
				:(li<shift(aut,i+1))?t.getLabel().getAction().get(li).equals(ti.getLabel().getAction().get(li-shift(aut,i)))
						:(li<shift(aut,j))?t.getLabel().getAction().get(li).equals(CALabel.IDLE)
								:(li<shift(aut,j+1))?t.getLabel().getAction().get(li).equals(tj.getLabel().getAction().get(li-shift(aut,j)))
										:t.getLabel().getAction().get(li).equals(CALabel.IDLE)) ;

		PentaPredicate<ModalTransition<String,String,State<String>,CALabel>,ModalTransition<String,String,State<String>,CALabel>,
		ModalTransition<String,String,State<String>,CALabel>,Integer,Integer> targetmatchpred = (t,ti,tj,i,j)->
		t.getTarget().getState().size()==comp.getRank() &&
		IntStream.range(0,t.getTarget().getState().size()).allMatch(bsti->
		(bsti<shift(aut,i))?t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti))
				:(bsti<shift(aut,i+1))?t.getTarget().getState().get(bsti).equals(ti.getTarget().getState().get(bsti-shift(aut,i)))
						:(bsti<shift(aut,j))?t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti))
								:(bsti<shift(aut,j+1))?t.getTarget().getState().get(bsti).equals(tj.getTarget().getState().get(bsti-shift(aut,j)))
										:t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti)));		

		TriPredicate<ModalTransition<String,String,State<String>,CALabel>,ModalTransition<String,String,State<String>,CALabel>,
		ModalTransition<String,String,State<String>,CALabel>> modalitymatchpred = (t,ti,tj) ->((t.getModality().equals(ModalTransition.Modality.PERMITTED) && ti.getModality().equals(ModalTransition.Modality.PERMITTED) &&
				tj.getModality().equals(ModalTransition.Modality.PERMITTED))||
				(!t.getModality().equals(ModalTransition.Modality.PERMITTED) && (!ti.getModality().equals(ModalTransition.Modality.PERMITTED)||
						!tj.getModality().equals(ModalTransition.Modality.PERMITTED))));


		return t-> IntStream.range(0, aut.size()).anyMatch(i-> 		//exists i in [0,aut.size]
		IntStream.range(i+1, aut.size()).anyMatch(j->		//exists j in [i+1,aut.size]
		autTr.get(i).parallelStream().filter(ti->sourcestatepred.test(ti, i, t.getSource())).anyMatch(ti->
		autTr.get(j).parallelStream().filter(tj->sourcestatepred.test(tj, j, t.getSource())).anyMatch(tj->
		ti.getLabel().match(tj.getLabel()) && labelmatchpred.test(t, ti, tj, i, j) && targetmatchpred.test(t, ti, tj, i, j) && modalitymatchpred.test(t, ti, tj)
				))));
	}

	/**
	 * predicates for interleaving transitions 
	 *
	 */
	private Predicate<ModalTransition<String,String,State<String>,CALabel>> predIntrleav(List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut, Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp, 
			TriPredicate<ModalTransition<String,String,State<String>,CALabel>,Integer,State<String>> sourcestatepred, 
			List<Set<ModalTransition<String,String,State<String>,CALabel>>> autTr){
		TriPredicate<ModalTransition<String,String,State<String>,CALabel>, ModalTransition<String,String,State<String>,CALabel>, Integer> labelintrleavpred = (t,ti,i)->
		t.getLabel().getAction().size()==comp.getRank() &&
		IntStream.range(0,t.getLabel().getAction().size()).allMatch(li->
		(li<shift(aut,i))?t.getLabel().getAction().get(li).equals(CALabel.IDLE)
				:(li<shift(aut,i+1))?t.getLabel().getAction().get(li).equals(ti.getLabel().getAction().get(li-shift(aut,i)))
						:t.getLabel().getAction().get(li).equals(CALabel.IDLE));

		TriPredicate<ModalTransition<String,String,State<String>,CALabel>, ModalTransition<String,String,State<String>,CALabel>, Integer> targetstateintrleavpred = (t,ti,i)-> 
		t.getTarget().getState().size()==comp.getRank() &&
		IntStream.range(0,t.getTarget().getState().size()).allMatch(bsti->
		(bsti<shift(aut,i))?t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti))
				:(bsti<shift(aut,i+1))?t.getTarget().getState().get(bsti).equals(ti.getTarget().getState().get(bsti-shift(aut,i)))
						:t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti)));


		return t->IntStream.range(0, aut.size()).anyMatch(i-> 	
		autTr.get(i).parallelStream().filter(ti->sourcestatepred.test(ti, i, t.getSource())).anyMatch(ti->
		IntStream.range(0, aut.size()).filter(j->j!=i).allMatch(j->		
		autTr.get(j).parallelStream().filter(tj->sourcestatepred.test(tj, j, t.getSource())).allMatch(tj->
		!ti.getLabel().match(tj.getLabel()) && labelintrleavpred.test(t,ti,i) && targetstateintrleavpred.test(t,ti,i) && t.getModality().equals(ti.getModality())
				))));		

	}


	private int shift(List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut, int j) {
		return IntStream.range(0, j).map(i->aut.get(i).getRank()).sum();
	}
}


interface PentaPredicate<T,U,V,Z,Q> {
	boolean test(T arg1, U arg2, V arg3, Z arg4, Q arg5);
}
