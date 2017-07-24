package FMCA;

public class FMCAState {
	private int[] state;
	private float x;
	private float y;
	private boolean initial;
	private boolean finalstate;
	public FMCAState(int[] state)
	{
		setInitial(false);
		setFinalstate(false);
		this.setState(state);
	}
	public FMCAState(int[] state, float x, float y)
	{
		setInitial(false);
		setFinalstate(false);
		this.setState(state);
		this.setX(x);
		this.setY(y);		
	}
	public FMCAState(int[] state, float x, float y,boolean initial, boolean finalstate)
	{
		this.setState(state);
		this.setX(x);
		this.setY(y);
		this.setInitial(initial);
		this.setFinalstate(finalstate);
	}
	public int[] getState() {
		return state;
	}
	public void setState(int[] state) {
		this.state = state;
	}
	public float getX() {
		return x;
	}
	public void setX(float x) {
		this.x = x;
	}
	public float getY() {
		return y;
	}
	public void setY(float y) {
		this.y = y;
	}
	public boolean isInitial() {
		return initial;
	}
	public void setInitial(boolean initial) {
		this.initial = initial;
	}
	public boolean isFinalstate() {
		return finalstate;
	}
	public void setFinalstate(boolean finalstate) {
		this.finalstate = finalstate;
	}
}
