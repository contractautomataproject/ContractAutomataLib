package CA;

import java.util.Arrays;

import FMCA.FMCAUtil;

public class CAState {
	private int[] state;
	private float x;
	private float y;
	private boolean initial;
	private boolean finalstate;
	private boolean isReachable=false;
	private boolean isSuccessfull=false;
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
	public boolean isReachable() {
		return isReachable;
	}
	public void setReachable(boolean setReachable) {
		this.isReachable = setReachable;
	}
	public CAState clone()
	{
		return new CAState(Arrays.copyOf(state,state.length),x,y,initial,finalstate);
	}
	
	public boolean equals(CAState c)
	{
		return  (Arrays.equals(state,c.getState())
				&& this.x==c.getX()
				&& this.y==c.getY()
				&& this.initial == c.isInitial()
				&& this.finalstate == c.isFinalstate()
				&& this.isReachable == c.isReachable()
				&& this.isSuccessfull == c.isSuccessfull());
	}
	
	
	public boolean isSuccessfull() {
		return isSuccessfull;
	}
	public void setSuccessfull(boolean isSuccessfull) {
		this.isSuccessfull = isSuccessfull;
	}
	
	public static CAState getCAStateWithValue(int[] value, CAState[] states)
	{
		for (int i=0;i<states.length;i++)
		{
			if (Arrays.equals(states[i].getState(), value))
				return states[i];
		}
		return null;
	}
	
	/**
	 * 
	 * @param tr
	 * @return an array of CAStates containing all states of transitions tr
	 */
	public static CAState[] extractCAStatesFromTransitions(CATransition[] tr)
	{
		CAState[] states = new CAState[tr.length*2];//upperbound
		int count=0;
		for (int i=0;i<tr.length;i++)
		{
			CAState source=tr[i].getSourceP();
			CAState target=tr[i].getTargetP();
			if (!FMCAUtil.contains(source,states,count))
			{
				states[count]=source;
				count++;
			}
			if (!FMCAUtil.contains(target,states,count))
			{
				states[count]=target;
				count++;
			}
		}
		return states;
	}
}