package io.github.contractautomataproject.catlib.transition;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.automaton.state.State;

public class ModalTransition<S1,L1, S extends State<S1>,L extends Label<L1>> extends Transition<S1,L1,S,L>  {
	
	/**
	 * the modality of the transition
	 */
	public enum Modality{
		PERMITTED,URGENT,LAZY
	}

	private final Modality mod;

	public ModalTransition(S source, L label, S target, Modality type)
	{
		super(source,label,target);
		if (type==null)
			throw new IllegalArgumentException();
		else		
			this.mod=type;

	}
	
	public boolean isUrgent()
	{
		return (this.mod==Modality.URGENT);
	}

	public boolean isLazy()
	{
		return (this.mod==Modality.LAZY);
	}

	public boolean isNecessary()
	{
		return (this.mod!=Modality.PERMITTED);
	}

	public boolean isPermitted()
	{
		return (this.mod==Modality.PERMITTED);
	}

	public Modality getModality()
	{
		return this.mod;
	}

	@Override
	public String toString()
	{
		if (this.mod==Modality.URGENT)
			return "!U("+getSource().getState().toString()+","+getLabel().toString()+","+getTarget().getState().toString()+")";
		else if (this.mod==Modality.LAZY)	
			return "!L("+getSource().getState().toString()+","+getLabel().toString()+","+getTarget().getState().toString()+")";
		else 
			return "("+getSource().getState().toString()+","+getLabel().toString()+","+getTarget().getState().toString()+")";
	}

	/**
	 * 
	 * @return encoding of the object into comma separated values
	 */
	@Override
	public String toCSV()
	{
		return "[mod="+this.getModality()+",source="+this.getSource().toCSV()
				+",label="+this.getLabel().toCSV()
				+",target="+this.getTarget().toCSV()+"]";
	}


	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(),mod.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		ModalTransition<?,?,?,?> other = (ModalTransition<?,?,?,?>) obj;
		return mod==other.mod;
	}
	
	/**
	 * 
	 * @param tr the set of transitions to check
	 * @param badStates the set of badstates to check
	 * @param controllabilityPred the controllability predicate
	 * @return true if the transition is uncontrollable against the parameters
	 */
	public boolean isUncontrollable(Set<? extends ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>> tr, Set<CAState> badStates,
			BiPredicate<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>,ModalTransition<S1,L1,S,L>> controllabilityPred)
	{
		if (this.isUrgent())
			return true;
		if (this.isPermitted())
			return false;
		return tr.parallelStream()
				.filter(t->t.getLabel().isMatch()
						&& !badStates.contains(t.getSource()))
				//	&&!badStates.contains(t.getTarget())//guaranteed to hold if the pruning predicate has bad.contains(x.getTarget())
				.noneMatch(t->controllabilityPred.test(t,this));
	}

}
