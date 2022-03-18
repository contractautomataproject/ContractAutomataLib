package io.github.contractautomataproject.catlib.automaton.state;

import java.util.List;

/**
 * class encoding a state
 * 
 * @author Davide Basile
 *
 * @param <T> generic type of the instance variable of the state
 */
public class BasicState<T> extends State<T>{
	
	private final boolean init;
	private final boolean fin;

	
	public BasicState(T label, Boolean init, Boolean fin) {
		super(label);
		if (label instanceof List<?>)
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
	
	/**
	 * 
	 * @param s the encoding of the object as comma separated values
	 * @return a new BasicState<String> object constructed from the parameter s
	 */
	public static BasicState<String> readCSV(String s) {
		boolean initial=false; 
		boolean	finalstate=false;
		String label="";
		String[] cs = s.split(",");
		for (String keyval : cs)
		{
			String[] kv = keyval.split("=");
			if(kv[0].equals("label"))
				label=kv[1];
			else if (kv[0].equals("initial"))
				initial=true;
			else finalstate=true;
		}
		return new BasicState<>(label,initial,finalstate);
	}
	
	// equals could cause errors of duplication of states in transitions to go undetected. 	
}