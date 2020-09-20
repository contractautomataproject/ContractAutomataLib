package CA;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import FMCA.FMCATransition;

public class CAState {
	private int[] state; //TODO this should be a set of CAState?
	
	private boolean initial;
	private boolean finalstate;
	
	private float x;
	private float y;
	private boolean isReachable=false;
	private boolean isSuccessful=false;
	
	public CAState(int[] state)
	{
		this.setState(state);
	}
	
	public CAState(int[] state, boolean initial, boolean finalstate)
	{
		this(state);
		this.setInitial(initial);
		this.setFinalstate(finalstate);
	}
	
	public CAState(int[] state, float x, float y, boolean initial, boolean finalstate)
	{
		this(state,initial,finalstate);
		this.setX(x);
		this.setY(y);
	}
	
	public CAState(List<CAState> states)//TODO test
	{
		this(states.stream()
				.map(CAState::getState)
        		.flatMapToInt(Arrays::stream)
        		.toArray(),
		states.parallelStream().allMatch(CAState::isInitial),
		states.parallelStream().allMatch(CAState::isFinalstate));
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
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (finalstate ? 1231 : 1237);
		result = prime * result + (initial ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(state);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CAState other = (CAState) obj;
		if (finalstate != other.finalstate)
			return false;
		if (initial != other.initial)
			return false;
		if (!Arrays.equals(state, other.state))
			return false;
		return true;
		
		//&& this.x==c.getX()							
		//&& this.y==c.getY()
		//&& this.isReachable == c.isReachable()		//reachable and successful are updated when computing the dangling states
		//&& this.isSuccessfull == c.isSuccessfull()	//thus two equal states may become different if I check this variables
	}
	
	public boolean isSuccessful() {
		return isSuccessful;
	}
	
	public void setSuccessful(boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}
	
	public static CAState getCAStateWithValue(int[] value, Set<CAState> states)
	{
		return states.parallelStream()
		.filter(x->Arrays.equals(x.getState(),value))
		.findAny()
		.orElseThrow(IllegalArgumentException::new);
	}
	
	/**
	 * @param tr
	 * @return an array of CAStates containing all states of transitions tr
	 */
	public static Set<CAState> extractCAStatesFromTransitions(Set<FMCATransition> tr)
	{
		Set<CAState> s = new HashSet<CAState>();
		for (FMCATransition t : tr)
		{
			s.add(t.getSource()); //no duplicates in set
			s.add(t.getTarget());
		}
		
		return s;
	}
	
	
	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (this.isInitial())
			sb.append(" Initial ");
		if (this.isFinalstate())
			sb.append(" Final ");
		if (this.isReachable)
			sb.append(" Reachable ");
		if (this.isSuccessful)
			sb.append(" Successful ");
			
		sb.append(Arrays.toString(this.getState()));
		
		return sb.toString();
	}
}
