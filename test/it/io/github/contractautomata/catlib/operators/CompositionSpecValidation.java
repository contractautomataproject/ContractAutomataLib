package it.io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;

import java.util.List;
import java.util.Set;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Given a list of operands automata and a resulting automaton, this class validates whether
 * the resulting automaton satisfies the conditions for being considered a composition of the operands.
 * The specification of the composition is expressed in first-order logic (expressed through Java Streams).
 * 
 * @author Davide Basile
 *
 */
public class CompositionSpecValidation<S1> implements BooleanSupplier {


	private final List<Automaton<S1, Action, State<S1>, ModalTransition<S1,Action,State<S1>, CALabel>>> aut;
	private final Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>> comp;


	public CompositionSpecValidation(List<Automaton<S1, Action, State<S1>, ModalTransition<S1, Action, State<S1>, CALabel>>> aut,
									 Automaton<S1, Action, State<S1>, ModalTransition<S1, Action, State<S1>, CALabel>> comp) {
		this.aut = aut;
		this.comp = comp;
	}

	@Override
	public boolean getAsBoolean() {
		//check if the composed automaton comp satisfies the spec
		return rank()&&initialState()&&states()&&finalStates()&&transitions();
	}

	private boolean rank()
	{
		return comp.getRank()==aut.stream()
				.mapToInt(Automaton::getRank)
				.sum();
	}

	private boolean states()
	{
		return compareStates(x->true);
	}


	private boolean finalStates()
	{
		return compareStates(State::isFinalState);
	}

	private Boolean compareStates(Predicate<State<S1>> pred) {
		return IntStream.range(0,aut.size()).allMatch(j->//for all indexes j of operands automata aut.get(j)
		aut.get(j).getStates().parallelStream().filter(pred).allMatch(cs2-> //for all states cs2 of aut.get(j) satisfying pred
		comp.getStates().parallelStream().filter(pred).anyMatch(cs-> //there exists a state cs of the composition satisfying pred s.t.
		cs.getState().size()==comp.getRank() &&						//cs has the rank of comp and
		IntStream.range(0, cs2.getState().size()).allMatch(iBs->	//for all  indexes iBs of basic states of cs2 cs
		cs2.getState().get(iBs).equals(cs.getState().get(iBs+shift(aut,j))  //the basic state of cs2 at index bs_i is equal to the basic state of cs at index iBs+shift
				)))));
	}

	private boolean initialState()
	{
		return comp.getInitial().getState().size()==comp.getRank() && //the rank of the initial state is equal to the rank of comp and
				IntStream.range(0,aut.size()).allMatch(j->  //forall indexes j of operands
				IntStream.range(0, aut.get(j).getInitial().getState().size()).allMatch(iBs->  //forall indexes iBs of basicstates of the initial state of operand at index j
				aut.get(j).getInitial().getState().get(iBs).equals(comp.getInitial().getState().get(iBs+shift(aut,j))) //the basic state of the initial state of operand j at index bs_i is equal to the basic state of the initial state of comp at index iBs+shift
						)); 
	}

	private boolean transitions() {

		List<Set<ModalTransition<S1,Action,State<S1>,CALabel>>> autTr = aut.stream()
				.map(Automaton::getTransition)
				.collect(Collectors.toList());

		//each transition of the composition is either a match or an interleaving transition
		return comp.getTransition().parallelStream().allMatch(t-> match(autTr,t) || interleaving(autTr,t));
	}

	private boolean sourceState(ModalTransition<S1,Action,State<S1>,CALabel> t,Integer ind, State<S1> s){
		//true if the source of transition t (of the operand at index ind)  is a component of composite state s
		return s.getState().size()==comp.getRank()&&
				IntStream.range(0, t.getSource().getState().size()).allMatch(bi->
						t.getSource().getState().get(bi).equals(s.getState().get(bi+shift(aut,ind))
						));
	}

	/**
	 * predicates for match transitions
	 * 
	 */
	private boolean match(List<Set<ModalTransition<S1,Action,State<S1>,CALabel>>> autTr,
						  ModalTransition<S1,Action,State<S1>,CALabel> t){
		
		//t transition of composition, ti and tj transitions of operands, i and j index of operands
		return IntStream.range(0, aut.size()).anyMatch(i-> 		//exists i in [0,aut.size]
		IntStream.range(i+1, aut.size()).anyMatch(j->		//exists j in [i+1,aut.size]
		autTr.get(i).parallelStream().filter(ti->sourceState(ti, i, t.getSource())).anyMatch(ti->
		autTr.get(j).parallelStream().filter(tj->sourceState(tj, j, t.getSource())).anyMatch(tj->
		ti.getLabel().match(tj.getLabel()) && labelMatch(t, ti, tj, i, j) && targetMatch(t, ti, tj, i, j) && modalityMatch(t, ti, tj)
				))));
	}

	private boolean labelMatch(ModalTransition<S1,Action,State<S1>,CALabel> t,
							   ModalTransition<S1,Action,State<S1>,CALabel> ti,
							   ModalTransition<S1,Action,State<S1>,CALabel> tj,
							   Integer i,
							   Integer j){
		return t.getLabel().getLabel().size()==comp.getRank() &&
				IntStream.range(0,t.getLabel().getLabel().size()).allMatch(li->
						(li<shift(aut,i))?t.getLabel().getLabel().get(li) instanceof IdleAction
								:(li<shift(aut,i+1))?t.getLabel().getLabel().get(li).equals(ti.getLabel().getLabel().get(li-shift(aut,i)))
								:(li<shift(aut,j))?t.getLabel().getLabel().get(li) instanceof IdleAction
								:(li<shift(aut,j+1))?t.getLabel().getLabel().get(li).equals(tj.getLabel().getLabel().get(li-shift(aut,j)))
								:t.getLabel().getLabel().get(li) instanceof IdleAction);
	}

	private boolean targetMatch(ModalTransition<S1,Action,State<S1>,CALabel> t,
							   ModalTransition<S1,Action,State<S1>,CALabel> ti,
							   ModalTransition<S1,Action,State<S1>,CALabel> tj,
							   Integer i, Integer j){
		return 	t.getTarget().getState().size()==comp.getRank() &&
				IntStream.range(0,t.getTarget().getState().size()).allMatch(bsti->
						(bsti<shift(aut,i))?t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti))
								:(bsti<shift(aut,i+1))?t.getTarget().getState().get(bsti).equals(ti.getTarget().getState().get(bsti-shift(aut,i)))
								:(bsti<shift(aut,j))?t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti))
								:(bsti<shift(aut,j+1))?t.getTarget().getState().get(bsti).equals(tj.getTarget().getState().get(bsti-shift(aut,j)))
								:t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti)));
	}

	private boolean modalityMatch(ModalTransition<S1,Action,State<S1>,CALabel> t,
								ModalTransition<S1,Action,State<S1>,CALabel> ti,
								ModalTransition<S1,Action,State<S1>,CALabel> tj){
		return ((t.getModality().equals(ModalTransition.Modality.PERMITTED) && ti.getModality().equals(ModalTransition.Modality.PERMITTED) &&
				tj.getModality().equals(ModalTransition.Modality.PERMITTED))||
				(!t.getModality().equals(ModalTransition.Modality.PERMITTED) && (!ti.getModality().equals(ModalTransition.Modality.PERMITTED)||
						!tj.getModality().equals(ModalTransition.Modality.PERMITTED))));
	}

	/**
	 * predicates for interleaving transitions 
	 *
	 */
	private boolean interleaving(List<Set<ModalTransition<S1,Action,State<S1>,CALabel>>> autTr,
								 ModalTransition<S1,Action,State<S1>,CALabel> t){

		return IntStream.range(0, aut.size()).anyMatch(i->
		autTr.get(i).parallelStream().filter(ti->sourceState(ti, i, t.getSource())).anyMatch(ti->
		IntStream.range(0, aut.size()).filter(j->j!=i).allMatch(j->		
		autTr.get(j).parallelStream().filter(tj->sourceState(tj, j, t.getSource())).allMatch(tj->
		!ti.getLabel().match(tj.getLabel()) && labelInterleaving(t,ti,i) && targetStateInterleaving(t,ti,i) && t.getModality().equals(ti.getModality())
				))));		

	}

	private boolean labelInterleaving(ModalTransition<S1,Action,State<S1>,CALabel> t, ModalTransition<S1,Action,State<S1>,CALabel> ti, Integer i) {
		return t.getLabel().getLabel().size()==comp.getRank() &&
				IntStream.range(0,t.getLabel().getLabel().size()).allMatch(li->
						(li<shift(aut,i))?t.getLabel().getLabel().get(li) instanceof IdleAction
								:(li<shift(aut,i+1))?t.getLabel().getLabel().get(li).equals(ti.getLabel().getLabel().get(li-shift(aut,i)))
								:t.getLabel().getLabel().get(li) instanceof IdleAction);
	}

	private boolean targetStateInterleaving(ModalTransition<S1,Action,State<S1>,CALabel> t, ModalTransition<S1,Action,State<S1>,CALabel> ti, Integer i) {
		return t.getTarget().getState().size()==comp.getRank() &&
				IntStream.range(0,t.getTarget().getState().size()).allMatch(bsti->
						(bsti<shift(aut,i))?t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti))
								:(bsti<shift(aut,i+1))?t.getTarget().getState().get(bsti).equals(ti.getTarget().getState().get(bsti-shift(aut,i)))
								:t.getTarget().getState().get(bsti).equals(t.getSource().getState().get(bsti)));
	}


	private int shift(List<Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>>> aut, int j) {
		return IntStream.range(0, j).map(i->aut.get(i).getRank()).sum();
	}

}
