package CA;

public class State {

	private boolean init;
	private boolean fin;
	private String label;
	
	public State(String label, boolean init, boolean fin) {
		super();
		this.label=label;
		this.init = init;
		this.fin = fin;
	}
	public boolean isFin() {
		return fin;
	}
	public void setFin(boolean fin) {
		this.fin = fin;
	}
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
		this.label = label;
	}
	
}
