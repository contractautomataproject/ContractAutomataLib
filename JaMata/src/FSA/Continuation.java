package FSA;



import java.util.Vector;

/**
 * This class represents a freeze state of the simulator, it is used for backtracking 
 * 
 * @author Davide Basile
 *
 */
public class Continuation {
	protected int pointer_state; //the actual state
	protected int pointer_string; //pointer to the symbol in input
	private Vector<Transition> tr; //the possible transitions to be executed
	
	/**
	 * Create a new Continuation
	 * @param pe	the actual state
	 * @param pg	pointer to the symbol in input
	 * @param tr	the possible transitions to be executed
	 */
	public Continuation(int pe, int pg, Vector<Transition> tr)
	{
		this.pointer_state = pe;
		this.pointer_string = pg;
		this.tr=tr;
	}
	
	/**
	 * 
	 * @return	the transitions
	 */
	public Vector<Transition> getTransition()
	{
		return tr;
	}
	
	/**
	 * Update the transitions
	 * @param v		the new transitions
	 */
	public void setTransition(Vector<Transition> v)
	{
		this.tr=v;
	}
	
	/**
	 * 
	 * @return	a pointer to a state of the automaton
	 */
	public int getPointerState()
	{
		return pointer_state;
	}
	
	/**
	 * 
	 * @return	a pointer to the symbol in input
	 */
	public int getPointerString()
	{
		return pointer_string;
	}
	
	/**
	 * Clone this continuation, without specifying the transitions
	 * @return	a cloned continuation without transitions
	 */
	public Continuation cloneWithoutTransition()
	{
		return new Continuation(this.pointer_state,this.pointer_string, new Vector<Transition>());
	}
	
	/**
	 * Check if the continuation c has the same pointer state and pointer string of this continuation
	 * @param c		a continuation to be checked
	 * @return		true if this continuation and c are equals without checking the transitions
	 */
	public boolean equalWithoutTransition(Continuation c)
	{
		return this.getPointerState()==c.getPointerState()&&this.getPointerString()==c.getPointerString();
	}
	
	/**
	 * Check if the continuation c is equal without transition to this continuation, and if the transition t
	 * is contained in this continuation
	 * @param c		the continuation to be searched
	 * @param t		the transition to be checked
	 * @return		true if c and this continuation are equals without transition and if this continuation has a transition t
	 */
	public boolean containTransition(Continuation c,Transition t)
	{
		if (this.equalWithoutTransition(c))
		{
			for(int i=0;i<tr.size();i++)
				if (tr.elementAt(i).equals(t))
					return true;
		}
		return false;
	}
	
}
