package contractAutomata;

import java.util.List;
import java.util.Objects;

/**
 * Transition of a contract automaton
 * 
 * @author Davide Basile
 *
 */
public class CATransition { 
	final private CAState source;
	final private CAState target;
	final private CALabel label;

	public CATransition(CAState source, CALabel label, CAState target){
		if (source==null || label==null || target==null)
			throw new IllegalArgumentException("source, label or target null");
		if (!(source.getRank()==target.getRank()&&label.getRank()==source.getRank()))
			throw new IllegalArgumentException("source, label or target with different ranks");
		this.source=source;
		this.target=target;
		this.label=label;
	}

	public CAState getSource()
	{
		return this.source;
	}

	public CAState getTarget()
	{
		return target;
	}

	public List<String> getLabelAsList()
	{
		return label.getLabelAsList();
	}

	public CALabel getLabel()
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
		CATransition other = (CATransition) obj;
		return label.equals(other.label)&&source.equals(other.source)&&target.equals(other.target);
	}
}
