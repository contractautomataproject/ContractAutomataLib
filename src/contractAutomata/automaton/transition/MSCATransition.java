package contractAutomata.automaton.transition;


import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiPredicate;

import contractAutomata.automaton.label.CALabel;
import contractAutomata.automaton.state.BasicState;
import contractAutomata.automaton.state.CAState;



/**
 * Transition of a modal service contract automaton
 * 
 * @author Davide Basile
 *
 */
public class MSCATransition extends Transition<List<BasicState>,CAState,CALabel> {

	public enum Modality{
		PERMITTED,URGENT,LAZY
	}

	private final Modality mod;

	public MSCATransition(CAState source, CALabel label, CAState target, Modality type)
	{
		super(source,label,target);
		if (type==null)
			throw new RuntimeException("Ill-formed transition");
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
			return "!U("+getSource().getState().toString()+","+getLabel().getLabelAsList()+","+getTarget().getState().toString()+")";
		else if (this.mod==Modality.LAZY)	
			return "!L("+getSource().getState().toString()+","+getLabel().getLabelAsList()+","+getTarget().getState().toString()+")";
		else 
			return "("+getSource().getState().toString()+","+getLabel().getLabelAsList()+","+getTarget().getState().toString()+")";
	}

	public String toCSV()
	{
		return "[mod="+this.getModality()+",source="+this.getSource().toCSV()
				+",label="+this.getLabel().toCSV()
				+",target="+this.getTarget().toCSV()+"]";
	}


	public boolean isUncontrollable(Set<? extends MSCATransition> tr, Set<CAState> badStates, BiPredicate<MSCATransition,MSCATransition> controllabilityPred)
	{
		if (this.isUrgent())
			return true;
		if (this.isPermitted())//||(this.getLabel().isMatch()&&tr.contains(this))
			return false;
		return !tr.parallelStream()
				.filter(t->t.getLabel().isMatch()
						&& !badStates.contains(t.getSource()))
				//	&&!badStates.contains(t.getTarget())//guaranteed to hold if the pruning predicate has bad.contains(x.getTarget())
				.anyMatch(t->controllabilityPred.test(t,this));
	}



	/*	//**
	 *
	 * @param t	set of transitions
	 * @return   source states of transitions in t 
	 *//*
	static Set<CAState> getSources(Set<? extends MSCATransition> t)
	{
		return t.parallelStream()
				.map(MSCATransition::getSource)
				.collect(Collectors.toSet());
	}*/

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(),mod.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		MSCATransition other = (MSCATransition) obj;
		return mod==other.mod;
	}
}