package CA;

import java.util.Arrays;

import javax.swing.text.html.HTMLDocument.HTMLReader.SpecialAction;

public class CAState {
	private int[] state;
	private float x;
	private float y;
	private boolean initial;
	private boolean finalstate;
	private boolean setReachable=false;
	public enum type {
		INITIAL, FINAL, BOTH
	}
	public CAState(int[] state)
	{
		setInitial(false);
		setFinalstate(false);
		this.setState(state);
	}
	public CAState(int[] state, type t)
	{
		if (t == type.INITIAL)
		{
			setInitial(true);
			setFinalstate(false);
		}
		else if (t == type.FINAL)
		{
			setInitial(false);
			setFinalstate(true);
		} else if (t == type.BOTH)
		{
			setInitial(true);
			setFinalstate(true);
		}
		this.setState(state);
	}
	public CAState(int[] state, float x, float y)
	{
		setInitial(false);
		setFinalstate(false);
		this.setState(state);
		this.setX(x);
		this.setY(y);		
	}
	public CAState(int[] state, float x, float y,boolean initial, boolean finalstate)
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
	public boolean isSetReachable() {
		return setReachable;
	}
	public void setSetReachable(boolean setReachable) {
		this.setReachable = setReachable;
	}
	public CAState clone()
	{
		return new CAState(Arrays.copyOf(state,state.length),x,y,initial,finalstate);
	}
	
	public static CAState getFrom(CAState[] s, int[] value)
	{
		for (int i=0;i<s.length;i++)
			if (Arrays.equals(s[i].getState(),value))
				return s[i];
		return null;
	}
}
