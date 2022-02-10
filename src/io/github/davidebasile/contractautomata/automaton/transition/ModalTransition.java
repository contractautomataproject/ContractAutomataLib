package io.github.davidebasile.contractautomata.automaton.transition;

import java.util.Objects;

import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.State;

public class ModalTransition<U,V, S extends State<U>,L extends Label<V>>   extends Transition<U,V,S,L>  {
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

}
