package contractAutomata;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
	private final List<BasicState> state;
	
	private final float x;
	private final float y;


	public CAState(List<BasicState> lstate, float x, float y)
	{
		if (lstate==null||lstate.isEmpty())
			throw new IllegalArgumentException();
		this.state = lstate;
		this.x=x;
		this.y=y;
	}

	public CAState(List<CAState> states)
	{
		if (states==null)
			throw new IllegalArgumentException();
		
		this.state = states.stream()
		.map(CAState::getState)
		.reduce(new ArrayList<BasicState>(), (x,y)->{x.addAll(y); return x;});
		
		this.x=0;
		this.y=0;
	}

	public List<BasicState> getState(){
		return state;
	}

	public int getRank() {
		return state.size();
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public boolean isInitial() {
		return state.stream().allMatch(BasicState::isInit);
	}
	
	public void setInitial(boolean initial) {
		this.state.forEach(s->s.setInit(initial));
	}

	public boolean isFinalstate() {
		return state.stream().allMatch(BasicState::isFin);
	}
	
	public String toCSV()
	{
		return "[state="+getState().stream()
		.map(bs->bs.toCSV())
		.collect(Collectors.joining())+"]";
	}


	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		if (this.isInitial())
			sb.append(" Initial ");
		if (this.isFinalstate())
			sb.append(" Final ");

		sb.append(this.getState().toString());

		return sb.toString();
	}
	
//	public boolean hasSameBasicStateLabelsOf(CAState s) {
//		if (s.getState().size()!=this.state.size())
//				return false;
//		return IntStream.range(0, this.state.size())
//		.allMatch(i->state.get(i).getLabel().equals(s.getState().get(i).getLabel()));
//	}
	
	// equals could cause errors of duplication of states in transitions to go undetected. 
}
