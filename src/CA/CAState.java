package CA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 
 * @author Davide Basile
 *
 */
public class CAState {
//	private int[] arrstate; 
//	private boolean initial;
//	private boolean finalstate;

	private List<State> lstate;
	
	private float x;
	private float y;

	//TODO this constructor is called by load method. To exploit the more precise information 
	//	   about whether each state in lstate is initial or final, one should firstly see how 
	//	   such information is stored, loaded, and then the correct constructor should be called.
	public CAState(int[] state, boolean initial, boolean finalstate)
	{
//		this.setState(state);
//		this.setInitial(initial);
//		this.setFinalstate(finalstate);
		if (state==null)
			throw new IllegalArgumentException();

		this.setStateL(IntStream.range(0,state.length)
		.mapToObj(i->new State(state[i]+"",initial,finalstate))//loss of information using lstate
		.collect(Collectors.toList()));
	}

	public CAState(int[] state, float x, float y, boolean initial, boolean finalstate)
	{
		this(state,initial,finalstate);
		this.x=x;
		this.y=y;
	}
	
	public CAState(List<State> lstate,int i)//TODO remove i, is here because of type erasure
	{
		if (lstate==null)
			throw new IllegalArgumentException();
		this.setStateL(lstate);
	}


	public CAState(List<State> lstate, float x, float y)
	{
		this(lstate,0);
		this.x=x;
		this.y=y;
	}

	public CAState(List<CAState> states)
	{
//		this(states.stream()
//				.map(CAState::getState)
//				.flatMapToInt(Arrays::stream)
//				.toArray(),
//				states.parallelStream().allMatch(CAState::isInitial),
//				states.parallelStream().allMatch(CAState::isFinalstate));
		if (states==null)
			throw new IllegalArgumentException();
		
		
		this.setStateL(states.stream()
		.map(CAState::getStateL)
		.reduce(new ArrayList<State>(), (x,y)->{x.addAll(y); return x;}));
	}

	public int[] getState() {
		return lstate.stream()
		.mapToInt(s->Integer.parseInt(s.getLabel()))
		.toArray();
		//return arrstate;
	}
	
	public List<State> getStateL(){
		return lstate;
	}

	public int getRank() {
		return lstate.size();
	}
	
	public void setState(int[] state) {
		//this.arrstate = state;
		if (state==null)
			throw new IllegalArgumentException();

		lstate=IntStream.range(0,state.length)
				.mapToObj(i->new State(state[i]+"",lstate.get(i).isInit(),lstate.get(i).isFin()))
				.collect(Collectors.toList());

	}

	public void setStateL(List<State> state) {
		if (state==null)
			throw new IllegalArgumentException();

		this.lstate = state;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public boolean isInitial() {
		//return initial;
		return lstate.stream().allMatch(State::isInit);
	}
	
	public void setInitial(boolean initial) {
	//	this.initial = initial;
		this.lstate.forEach(s->s.setInit(initial));
	}

	public boolean isFinalstate() {
		return lstate.stream().allMatch(State::isFin);
//		return finalstate;
	}

	public void setFinalstate(boolean finalstate) {
	//	this.finalstate = finalstate;

		this.lstate.forEach(s->s.setFin(finalstate));
	}


//	@Override
//	public int hashCode() {
//		final int prime = 31;
//		int result = 1;
//		result = prime * result + (finalstate ? 1231 : 1237);
//		result = prime * result + (initial ? 1231 : 1237);
//		result = prime * result + Arrays.hashCode(arrstate);
//		return result;
//	}

	
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
			throw new IllegalArgumentException("Bug: Ambiguous states: there is more than one state with value "+Arrays.toString(value));

		return states.parallelStream()
				.filter(x->Arrays.equals(x.getState(),value))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}
	
	public static CAState getCAStateWithLValue(List<State> value, Set<CAState> states)
	{
		if (states.parallelStream()
				.filter(x->x.getStateL().equals(value))
				.count()>1)
			throw new IllegalArgumentException("Bug: Ambiguous states: there is more than one state with value "+value);

		return states.parallelStream()
				.filter(x->x.getStateL().equals(value))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}

	/**
	 * @param tr
	 * @return an array of CAStates containing all states of transitions tr
	 */
//	public static Set<CAState> extractCAStatesFromTransitions(Set<? extends CATransition> tr)
//	{
//
//		Set<CAState> cs= tr.stream()
//				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
//				.collect(Collectors.toSet()); //CAState without equals, duplicates objects are detected
//
//		if (cs.stream()
//				.anyMatch(x-> cs.stream()
//						.filter(y->x!=y && Arrays.equals(x.getState(), y.getState()))
//						.count()>0))
//			throw new IllegalArgumentException("Transitions have ambiguous states (different objects for the same state).");
//
//		return cs;
//	}
	
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
				.filter(y->x!=y && x.getStateL().equals(y.getStateL()))
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


}
