package contractAutomata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class representing a state of a Contract Automaton
 * 
 * @author Davide Basile
 * 
 */
public class CAState {
	
	/**
	 * The list of states of principal
	 */
	private List<BasicState> lstate;
	//TODO the users of this class still make use of array of int[] instead of List<BasicState>
	
	private float x;
	private float y;

	//TODO this constructor is called by load method. To exploit the more precise information 
	//	   about whether each state in lstate is initial or final, one should firstly see how 
	//	   such information is stored, loaded, and then the correct constructor should be called.
	public CAState(int[] state, boolean initial, boolean finalstate)
	{
		if (state==null)
			throw new IllegalArgumentException();

		this.setState(IntStream.range(0,state.length)
		.mapToObj(i->new BasicState(state[i]+"",initial,finalstate))//loss of information using lstate
		.collect(Collectors.toList()));
	}

	public CAState(int[] state, float x, float y, boolean initial, boolean finalstate)
	{
		this(state,initial,finalstate);
		this.x=x;
		this.y=y;
	}

	public CAState(List<BasicState> lstate, float x, float y)
	{
		if (lstate==null)
			throw new IllegalArgumentException();
		this.setState(lstate);
		this.x=x;
		this.y=y;
	}

	public CAState(List<CAState> states)
	{
		if (states==null)
			throw new IllegalArgumentException();
	
		this.setState(states.stream()
		.map(CAState::getStateL)
		.reduce(new ArrayList<BasicState>(), (x,y)->{x.addAll(y); return x;}));
	}

	public int[] getState() {
		return lstate.stream()
		.mapToInt(s->Integer.parseInt(s.getLabel()))
		.toArray();
	}
	
	public List<BasicState> getStateL(){
		return lstate;
	}

	public int getRank() {
		return lstate.size();
	}
	
	public void setState(int[] state) {
		if (state==null)
			throw new IllegalArgumentException();

		lstate=IntStream.range(0,state.length)
				.mapToObj(i->new BasicState(state[i]+"",lstate.get(i).isInit(),lstate.get(i).isFin()))
				.collect(Collectors.toList());
	}

	public void setState(List<BasicState> state) {
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
		return lstate.stream().allMatch(BasicState::isInit);
	}
	
	public void setInitial(boolean initial) {
		this.lstate.forEach(s->s.setInit(initial));
	}

	public boolean isFinalstate() {
		return lstate.stream().allMatch(BasicState::isFin);
	}

	public void setFinalstate(boolean finalstate) {

		this.lstate.forEach(s->s.setFin(finalstate));
	}


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
	
/*	public static CAState getCAStateWithLValue(List<State> value, Set<CAState> states)
	{
		if (states.parallelStream()
				.filter(x->x.getStateL().equals(value))
				.count()>1)
			throw new IllegalArgumentException("Bug: Ambiguous states: there is more than one state with value "+value);

		return states.parallelStream()
				.filter(x->x.getStateL().equals(value))
				.findFirst()
				.orElseThrow(IllegalArgumentException::new);
	}*/


	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (this.isInitial())
			sb.append(" Initial ");
		if (this.isFinalstate())
			sb.append(" Final ");

		sb.append(this.getStateL().toString());

		return sb.toString();
	}
	
	// equals could cause errors of duplication of states in transitions to go undetected. 
}
