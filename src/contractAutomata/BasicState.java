package contractAutomata;

/**
 * Class representing a basic state, used by CAState
 * @author Davide
 *
 */
public class BasicState {

	private boolean init;
	private final boolean fin;
	private String label;
	
	public BasicState(String label, boolean init, boolean fin) {
		super();
		this.label=label;
		this.init = init;
		this.fin = fin;
	}
	public boolean isFin() {
		return fin;
	}
/*	public void setFin(boolean fin) {
		this.fin = fin;
	}*/
	public boolean isInit() {
		return init;
	}
	public void setInit(boolean init) {
		this.init = init;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label=label;
	}
	
	@Override
	public String toString() {
		return label;//"[init=" + init + ", fin=" + fin + ", label=" + label + "]";
	}
	
	public String toCSV() {

		String fin= (this.isFin())?",final=true":"";
		String init= (this.isInit())?",initial=true":"";

		return "label="+this.getLabel()+fin+init;
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
