package io.github.contractautomataproject.catlib.automaton.state;

/**
 * class encoding a state
 * 
 * @author Davide Basile
 *
 * @param <T> generic type of the instance variable of the state
 */
public class BasicState<T> extends State<T>{
	
	private boolean init;
	private boolean fin;

	
	public BasicState(T label, Boolean init, Boolean fin) {
		super(label);
		this.init=init;
		this.fin=fin;
	}
	
	
	@Override
	public boolean isFinalstate() {
		return fin;
	}
	
	@Override
	public boolean isInitial() {
		return init;
	}
	
	public void setInitial(boolean init) {
		this.init = init;
	}
		
	public void setFinalstate(boolean fin) {
		this.fin=fin;
	}
	
	
	
	@Override
	public String toString() {
		return this.getState().toString();//"[init=" + init + ", fin=" + fin + ", label=" + label + "]";
	}
	
	/**
	 * 
	 * @return a string encoding the object as comma separated values
	 */
	public String toCSV() {

		String fin= (this.isFinalstate())?",final=true":"";
		String init= (this.isInitial())?",initial=true":"";

		return "label="+this.getState()+fin+init;
	}
	
	/**
	 * 
	 * @param s the encoding of the object as comma separated values
	 * @return a new State<String> object constructed from the parameter s
	 */
	public static BasicState<String> readCSV(String s) {
		boolean init=false, fin=false;
		String label="";
		String[] cs = s.split(",");
		for (String keyval : cs)
		{
			String[] kv = keyval.split("=");
			if(kv[0].equals("label"))
				label=kv[1];
			else if (kv[0].equals("initial"))
				init=true;
			else if (kv[0].equals("final"))
				fin=true;
		}
		return new BasicState<String>(label,init,fin);
		
	}


//	/**
//	 * 
//	 * @return an encoding of the object as comma separated values
//	 */
//	public String toCSV()
//	{
//		return "[state="+state+"]";
//	}
	
//	public abstract <U extends State<T>> U getCopy();
}
