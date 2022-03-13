package io.github.contractautomataproject.catlib.transition;

import java.util.Objects;

import io.github.contractautomataproject.catlib.automaton.Ranked;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.State;

/**
 * Transition of a Contract Automaton
 * 
 * @author Davide Basile
 *
 * @param <U> generic type of the instance variable of S
 * @param <S> generic type of the state
 * @param <L> generic type of the label 
 */
public class Transition<U,V, S extends State<U>,L extends Label<V>> { 
	final private S source;
	final private S target;
	final private L label;
//	final private TriFunction<U,Boolean,Boolean,S> createState;
	
	
	public Transition(S source, L label, S target){
		check(source,label,target);
		this.source=source;
		this.label=label;
		this.target=target;
	}

	private void check(S source, L label, S target) {
		if (source==null || label==null || target==null)
			throw new IllegalArgumentException("source, label or target null");
		if (!(source.getRank().equals(target.getRank())&&label.getRank().equals(source.getRank()))) {
//			System.out.println("error in "+source.toString()+label.toString()+target.toString()+
//					source.getRank()+" "+label.getRank());
			throw new IllegalArgumentException("source, label or target with different ranks");
		}		
	}
	public S getSource()
	{
		return source;
//		return createState.apply(source.getState(),source.isInitial(),source.isFinalstate());
	}

	public S getTarget()
	{
		return target;
//		return createState.apply(target.getState(),source.isInitial(),source.isFinalstate());
	}

	public L getLabel()
	{
		return label;
	}

	public Integer getRank()
	{
		return label.getRank();
	}

	@Override
	public int hashCode() {
		return Objects.hash(source.hashCode(),label.hashCode(),target.hashCode());
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Transition<?,?,? extends Ranked, ? extends Ranked> other = (Transition<?,?,?, ?>) obj;
		return label.equals(other.getLabel())&&source.equals(other.getSource())&&target.equals(other.getTarget());
	}

	@Override
	public String toString() {
		return "("+source+","+label+","+target+")";
	}
	
	/**
	 * 
	 * @return encoding of the object into comma separated values
	 */
	public String toCSV()
	{
		return "[source="+this.getSource().toCSV()
				+",label="+this.getLabel().toCSV()
				+",target="+this.getTarget().toCSV()+"]";
	}
	
}



