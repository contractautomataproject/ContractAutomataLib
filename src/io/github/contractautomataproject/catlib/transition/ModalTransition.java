package io.github.contractautomataproject.catlib.transition;

import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
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
			return "!U" + super.toString();
		else if (this.mod==Modality.LAZY)	
			return "!L"+super.toString();
		else 
			return super.toString();
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
	public boolean isUncontrollable(Set<? extends ModalTransition<S1,String,S,CALabel>> tr, Set<State<String>> badStates,
			BiPredicate<ModalTransition<S1,String,S,CALabel>,ModalTransition<S1,L1,S,L>> controllabilityPred)
	{
		if (this.isUrgent())
			return true;
		if (this.isPermitted())
			return false;
		return tr.parallelStream()
				.filter(t->t.getLabel().isMatch()
						&& !badStates.contains(t.getSource()))
				//	badStates does not contains target of t, 
				//  guaranteed to hold if the pruning predicate has bad.contains(x.getTarget())
				.noneMatch(t->controllabilityPred.test(t,this));
	}

	@Override
	public String print()
	{		
		if (this.getModality()==ModalTransition.Modality.URGENT)
			return "!U"+super.print();
		else if (this.getModality()==ModalTransition.Modality.LAZY)	
			return "!L"+super.print();
		else
			return super.print();
	}

}
