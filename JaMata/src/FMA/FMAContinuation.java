package FMA;
import java.util.Arrays;
import java.util.Vector;

import FSA.Continuation;
import FSA.Transition;


/**
 * Extends the Continuation by adding the register
 * 
 * @author Davide	Basile
 *
 */
public class FMAContinuation extends Continuation{
	private int[] register; //the register of the automaton
	
	/**
	 * Create a new FMA Continuation
	 * @param pe	a pointer to a state of the automaton
	 * @param pg	a pointer to the string in input
	 * @param r		the registers
	 * @param tr	the allowed transitions
	 */
	public FMAContinuation(int pe, int pg, int[] r, Vector<Transition> tr)
	{
		super(pe,pg,tr);
		this.register = r;
	}
	
	/**
	 * 
	 * @return	the register
	 */
	public int[] getRegister()
	{
		return register;
	}
	
	/**
	 * Clone this continuation without the transitions
	 */
	public Continuation cloneWithoutTransition()
	{
		Continuation c = super.cloneWithoutTransition();
		return new FMAContinuation(c.getPointerState(),c.getPointerString(),this.copyRegister(),c.getTransition());
	}
	
	/**
	 * 
	 * @return  a new array initialized with the registers of this continuation
	 */
	public int[] copyRegister()
	{
		int[] r=new int[register.length];
		System.arraycopy(register, 0, r, 0, register.length);
		return r;
	}
	
	/**
	 * Check if this transition is equal to c without checking the transitions
	 */
	public boolean equalWithoutTransition(Continuation c)
	{
		return Arrays.equals(this.getRegister(), ((FMAContinuation)c).getRegister())&&super.equalWithoutTransition(c);
	}
	
}
