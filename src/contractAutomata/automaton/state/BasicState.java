package contractAutomata.automaton.state;

/**
 * Class representing a basic state, used by CAState
 * @author Davide
 *
 */
public class BasicState extends State<String> {

	private boolean init;
	private final boolean fin;
	
	public BasicState(String label, boolean init, boolean fin) {
		super(label);
		this.init = init;
		this.fin = fin;
	}
	
	@Override
	public boolean isFinalstate() {
		return fin;
	}
	
	@Override
	public boolean isInitial() {
		return init;
	}
	
	@Override
	public void setInitial(boolean init) {
		this.init = init;
	}
		
	
	@Override
	public String toString() {
		return this.getState().toString();//"[init=" + init + ", fin=" + fin + ", label=" + label + "]";
	}
	
	public String toCSV() {

		String fin= (this.isFinalstate())?",final=true":"";
		String init= (this.isInitial())?",initial=true":"";

		return "label="+this.getState()+fin+init;
	}
	
	public static BasicState readCSV(String s) {
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
		return new BasicState(label,init,fin);
		
	}
		
	//equals creates problems
	
}
