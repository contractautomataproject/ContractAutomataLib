package io.github.contractautomataproject.catlib.automaton.state;

import java.util.List;

/**
 * class encoding a state
 * 
 * @author Davide Basile
 *
 * @param <T> generic type of the instance variable of the state
 */
public class BasicState<T> extends AbstractState<T>{
	
	private final boolean init;
	private final boolean fin;

	
	public BasicState(T label, Boolean init, Boolean fin) {
		super(label);
		if (label instanceof List<?> && ((List<?>)label).get(0) instanceof AbstractState)
			throw new UnsupportedOperationException();
		this.init=init;
		this.fin=fin;
	}
	
	@Override
	public Integer getRank() {
		return 1;
	}
	
	@Override
	public boolean isFinalstate() {
		return fin;
	}
	
	@Override
	public boolean isInitial() {
		return init;
	}
	
	/**
	 * 
	 * @return a string encoding the object as comma separated values
	 */
	@Override
	public String toString() {
		String finalstate= (this.isFinalstate())?",final=true":"";
		String initial= (this.isInitial())?",initial=true":"";
		return "label="+this.getState()+finalstate+initial;
	}
	
	// equals could cause errors of duplication of states in transitions to go undetected. 	
}