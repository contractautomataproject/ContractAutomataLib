package PFSA;

import java.util.Vector;

import FSA.Continuation;
import FSA.Transition;

/**
 * Extends Continuation by adding stack and registers.
 * 
 * @author Davide Basile
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class PFSAContinuation extends Continuation {

	private Vector[] streg;
	/**
	 * 
	 * Create a new PFSA Continuation
	 * @param pe	a pointer to a state of the automaton
	 * @param pg	a pointer to the string in input
	 * @param tr	the allowed transitions
	 * @param streg	the stack and registers
	 */
	public PFSAContinuation(int pe, int pg, Vector<Transition> tr, Vector[] streg)
	{
		super(pe, pg ,tr);
		this.streg= streg;
	}
	
	/**
	 * 
	 * @return stack and registers
	 */
	public Vector[] getStReg()
	{
		return streg;
	}
	
	/**
	 * 
	 * @return  a new Vector array initialized with the registers and the stack of this continuation
	 */
	public Vector[] copyStReg()
	{
		Vector[] r= new Vector[streg.length];
		for (int i=0;i<r.length;i++)
		{
			r[i]= new Vector();
			for (int j=0;j<streg[i].size();j++)
				r[i].add(streg[i].elementAt(j));
		}
		return r;
	}
	
	/**
	 * Clone this continuation without the transitions
	 */
	public Continuation cloneWithoutTransition()
	{
		Continuation c=super.cloneWithoutTransition();
		return new PFSAContinuation(c.getPointerState(),c.getPointerString(),c.getTransition(),this.copyStReg());
	}
	
	/**
	 * Check if this transition is equal to c without checking the transitions
	 */
	public boolean equalWithoutTransition(Continuation c)
	{
		if (!super.equalWithoutTransition(c))
			return false;
		Vector[] c1 = this.getStReg();
		Vector[] c2 = ((PFSAContinuation)c).getStReg();
		for (int i=0;i<c1.length;i++)
		{
			Vector v1 = c1[i];
			Vector v2 = c2[i];
			if (v1.size()!=v2.size())
				return false;
			for (int j=0;j<v1.size();j++)
			{
				if ((int)v1.elementAt(j)!=(int)v2.elementAt(j))
						return false;
			}
		}
		return true;
	}
}
