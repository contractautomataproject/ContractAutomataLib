package PFSA;
import java.util.Vector;

import FSA.FSA;
import FSA.Continuation;
import FSA.Simulator;
import FSA.Transition;



/**
 * Extends the Simulator by changing the notion of validity of a transition, also handling fresh names.
 * Overrides various methods 
 * @author Davide Basile
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class PFSASimulator extends Simulator{
	Vector<Integer> fresh = new Vector<Integer>(20);
	
	/**
	 * 
	 * @param automa the automaton to simulate
	 */
	public PFSASimulator(FSA automa)
	{
		super((PFSA) automa);
	}
	
	/**
	 * Change the automaton
	 */
	public void changeAutoma(FSA automa)
	{
		super.changeAutoma((PFSA) automa);
	}
	
	/**
	 * As a side conditions in the final state when the string in input is consumed the automaton must have
	 * the empty stack
	 */
	protected boolean sideConditions()
	{
		return ((PFSA)automa).getStreg()[0].isEmpty();
	}
	
	/**
	 * A transition tr with symbol in input is valid if :
	 * <ul>
	 * 			<li>sigma=T,	stack not empty,		top fresh or top equals to symbol in input</li>
	 * 		    <li>sigma=i	,	register i not empty and available,	top fresh or top equals to symbol in input</li>
	 * 			<li>sigma=square </li>
	 * </ul>
	 * <ul>
	 * 			<li>Z=i,		stack and register i not empty,		top stack = top register i</li>
	 * 			<li>Z=?,		stack not empty</li>
	 * 			<li>Z=square
	 * </ul>
	 * <ul>
	 * 			<li>zeta=i,		register i not empty,	top register i available</li>
	 * 			<li>zeta=square</li>
	 * <ul>
	 * 			<li>delta=-i	</li>
	 * 			<li>delta=+i	</li>
	 * 			<li>delta=square</li>
	 * </ul>
	 * 		
	 */
	protected boolean isValid(Transition tr,int symbol)
	{
		Vector[] streg= ((PFSA) automa).getStreg(); 
		int sigma = ((PFSATransition)tr).getSigma();
		//int delta = ((PFSATransition)tr).getDelta();
		int Z = ((PFSATransition)tr).getZ();
		int zeta = ((PFSATransition)tr).getZeta();
		// this boolean is true if the transition is allowed
		boolean valid= 	( 
				          (
				        	(sigma==-1)&&(!streg[0].isEmpty())&&
								( 
										( (int)streg[0].lastElement()<=-2 && !checkFreshness(symbol) ) || // top stack fresh
										( (int)streg[0].lastElement()>=0 &&(int)streg[0].lastElement()== symbol ) // top stack equals to symbol
							    )
						  ) 
						  || 
						  (
						     (sigma>=0)	&&(!streg[sigma+1].isEmpty())&&((int)streg[sigma+1].lastElement()!=-1)&& //top register not unavailable
									(
											(int)streg[sigma+1].lastElement()<=-2 && !checkFreshness(symbol)  || //top register fresh symbol
											((int)streg[sigma+1].lastElement()>=0 && (int)streg[sigma+1].lastElement() == symbol)//top register equals to symbol
								    )
		                  ) 
		                  || sigma == -2 //nothing
						) 
						/*&& 
						(
							(
								(delta<0)	&& 
									( (streg[Math.abs(delta)].size()==1) && ((int) streg[Math.abs(delta)].lastElement() != -1) //pop register
									  || (streg[Math.abs(delta)].size()>1)
									 )
							)
						 || delta>=0 //push or nothing
						) */
						&& 
						(
							(
								(Z>=0) 	&&(!streg[Z+1].isEmpty())&&(!streg[0].isEmpty()) && ((int)streg[Z+1].lastElement()== (int)streg[0].lastElement()) //check available register
							) 
						  || Z==-2 //nothing
						  || (Z==-3 && !streg[0].isEmpty()) //pop
						) 
						&& 
						(
							((zeta>=0) 	&&(!streg[zeta+1].isEmpty()) && ((int)streg[zeta+1].lastElement()!= -1))//check available register 
							|| zeta<0  //nothing
					    );
		return valid;
	}

	/**
	 *  Update the state of the simulator by performing the transition tr 
	 */
	protected void updateState(Transition t, int symbol)
	{
		Vector[] streg= ((PFSA) automa).getStreg(); 
		int sigma =  ((PFSATransition)t).getSigma();
		int delta = ((PFSATransition)t).getDelta();
		int Z = ((PFSATransition)t).getZ();
		int zeta = ((PFSATransition)t).getZeta();
		if (sigma != -2)
		{
			 //substitute fresh symbol with symbol in input
			if ((sigma == -1)&&((int)streg[0].elementAt(streg[0].size()-1)<=-2)) //top stack fresh symbol
				substitute(symbol,(int)streg[0].elementAt(streg[0].size()-1));
			if ((sigma >= 0)&&((int)streg[sigma+1].elementAt(streg[sigma+1].size()-1)<=-2))
				substitute(symbol,(int)streg[sigma+1].elementAt(streg[sigma+1].size()-1));
			pointer_string++; // read input
		}
		if ((Z!=-2)&& ((Z==-3)||(streg[Z+1].lastElement().equals(streg[0].lastElement()))))
				streg[0].removeElementAt(streg[0].size()-1); //pop stack
		if (zeta!=-2)
			streg[0].add(streg[zeta+1].lastElement()); //push stack
		if (delta < 0)
		{
			 boolean pop=false;
			 if (!streg[Math.abs(delta)].isEmpty()&&(int) streg[Math.abs(delta)].lastElement() == -1)
			 {
				 streg[Math.abs(delta)].removeElementAt(streg[Math.abs(delta)].size()-1); //pop -1
				 pop=true;
			 }
			 if (!streg[Math.abs(delta)].isEmpty())
			 {
				 if ((int) streg[Math.abs(delta)].lastElement()<-1)
					 fresh.set( ((int) streg[Math.abs(delta)].lastElement()+2)*-1,new Integer(-1)); //dispose fresh symbol
				 streg[Math.abs(delta)].removeElementAt(streg[Math.abs(delta)].size()-1); //pop 
				 pop=true;
			 }
			 if (pop)
				 streg[Math.abs(delta)].add(-1);  //add unavailable
		}
		else if (delta > 0)
		{
			/**
			 * fresh id starts from -2, check the first fresh id available, if all fresh id are not instantiated
			 * creates a new one. This corresponds to a sort of alpha conversion of fresh names.
			 */
			int freshid=0;
			for (int i=0;i<fresh.size();i++)
			{
				if (fresh.elementAt(i).intValue() == -1)
				{
					freshid =  (i+2)*-1;
					fresh.set(i,new Integer(1));
					break;
				}
			}
			if (freshid==0)
			{
				fresh.add(new Integer(1));
				freshid= (fresh.size()+1)*-1;
			}
			if (!streg[delta].isEmpty()&&(int)streg[delta].lastElement()==-1)	
				 streg[Math.abs(delta)].removeElementAt(streg[Math.abs(delta)].size()-1); //pop -1
			streg[delta].add(freshid); //fresh name
		}
		pointer_state=t.getFinal();
	
	}
	
	/**
	 * Substitute the freshid with the symbol in input
	 * @param symbol	symbol to be instantiated
	 * @param freshid	the freshid to be disposed
	 */
	private void substitute(int symbol, int freshid)
	{
		fresh.set((freshid+2)*-1,new Integer(-1)); //dispose the fresh symbol
		Vector[] streg= ((PFSA) automa).getStreg(); 
		for (int i=0;i<streg.length;i++)
		{
			for (int j=0; j<streg[i].size();j++)
			{
				if ( (int)streg[i].elementAt(j)==freshid)
					{
						streg[i].set(j, symbol);
					}
			}
		}
	}
	/**
	 * Check if the symbol is fresh (not already stored in the registers)
	 * @param symbol
	 * @return true if the symbol is NOT fresh
	 */
	private boolean checkFreshness(int symbol)
	{
		Vector[] streg= ((PFSA) automa).getStreg(); 
		for (int i=0;i<streg.length;i++)
		{
			if (Simulator.contains(symbol, streg[i])>=0)
				return true;
		}
		return false;
	}
	
	/**
	 * restore the state of the simulator with the parameters in the continuation C. First invoke the method of the
	 * super class and then update the register of the automaton with a copy of the register stored in the continuation.
	 */
	protected void restoreStateContinuation(Continuation c)
	{
		super.restoreStateContinuation(c);
		((PFSA) automa).updateStreg(((PFSAContinuation) c).copyStReg());
	}
	
	/**
	 * Create a new Continuation with the freezed state of the automaton
	 */
	protected Continuation createContinuation()
	{
		return new PFSAContinuation(pointer_state,pointer_string, null, ((PFSA)automa).getStreg());
	}
}
