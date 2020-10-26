package CA;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CAState {
	private int[] state; //TODO this should be a set of CAState? I would have many unused variables, still these are states identifiers, maybe an empty state class

	private boolean initial;
	private boolean finalstate; //TODO this should be a list telling which one is final, instead of the instance variable in FMCA class

	private float x;		//TODO check if these two variables should be removed and put into GUI and I/O methods as maps
	private float y;

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

	public CAState(List<CAState> states)
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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (finalstate ? 1231 : 1237);
		result = prime * result + (initial ? 1231 : 1237);
		result = prime * result + Arrays.hashCode(state);
		return result;
	}

	
// equals could cause errors of duplication of states in transitions to go undetected. 
//
//	@Override
//	public boolean equals(Object obj) {
//		if (this == obj)
//			return true;
//		if (obj == null)
//			return false;
//		if (getClass() != obj.getClass())
//			return false;
//		CAState other = (CAState) obj;
//		if (finalstate != other.finalstate)
//			return false;
//		if (initial != other.initial)
//			return false;
//		if (!Arrays.equals(state, other.state))
//			return false;
//		return true;
//
//		//&& this.x==c.getX()							
//		//&& this.y==c.getY()
//	}


	public static CAState getCAStateWithValue(int[] value, Set<CAState> states)
	{
		if (states.parallelStream()
				.filter(x->Arrays.equals(x.getState(),value))
				.count()>1)
			throw new IllegalArgumentException("Ambiguous states: there is more than one state with value "+Arrays.toString(value));

		return states.parallelStream()
				.filter(x->Arrays.equals(x.getState(),value))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

	/**
	 * @param tr
	 * @return an array of CAStates containing all states of transitions tr
	 */
	public static Set<CAState> extractCAStatesFromTransitions(Set<? extends CATransition> tr)
	{

		Set<CAState> cs= tr.stream()
				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
				.collect(Collectors.toSet()); //CAState without equals, duplicates objects are detected

		if (cs.stream()
				.anyMatch(x-> cs.stream()
						.filter(y->x!=y && Arrays.equals(x.getState(), y.getState()))
						.count()>0))
			throw new IllegalArgumentException("Transitions have ambiguous states (different objects for the same state).");

		return cs;
	}


	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (this.isInitial())
			sb.append(" Initial ");
		if (this.isFinalstate())
			sb.append(" Final ");

		sb.append(Arrays.toString(this.getState()));

		return sb.toString();
	}

	@Override
	public CAState clone()
	{
		return new CAState(Arrays.copyOf(state, state.length),x,y,initial,finalstate);
	}

}
