package FMA;

import FSA.FSA;
import FSA.Continuation;
import FSA.Simulator;
import FSA.Transition;


/**
 * Extends Simulator by adding the registers and change the condition of validity of transitions, and override various methods.
 * 
 * @author Davide Basile
 *
 */
public class FMASimulator extends Simulator {
	
	/**
	 * 
	 * @param automa the automaton to simulate
	 */
	public FMASimulator(FSA automa)
	{
		super((FMA) automa);
	}
	
	/**
	 * Change the automaton
	 */
	public void changeAutoma(FSA automa)
	{
		super.changeAutoma((FMA) automa);
	}

	/**
	 * A transition tr is valid if the symbol is contained in the registers and the label is equal to the index of the register
	 * containing the symbol in input; or if the symbol in input is not contained in the registers and the label is equal to
	 * the fuction rho in the actual state.
	 */
	protected boolean isValid(Transition tr,int symbol)
	{
		int[] rho=((FMA) automa).getRho();
		int index = contains(symbol,((FMA) automa).getRegister());  //check if the symbol in input is contained in the register
		if (index!=-1)
			return tr.getLabel()==index; // the label is equal to the index of the register that contains the symbol in input
		else if (rho[pointer_state]!=-1)
			return tr.getLabel()==rho[pointer_state];
		else
			return false;
		
	}
	
	/**
	 * Update the register of the automaton if the symbol in input is not contained in the register, invoke the method
	 * of the superclass for updating the other parameters. 
	 */
	protected void updateState(Transition tr,int symbol)
	{
		int index = contains(symbol,((FMA) automa).getRegister()); 
		if (index==-1)
			updateRegister(symbol); //update register if the symbol is not contained in the register
		super.updateState(tr, symbol);
	}
	
	
	/**
	 * Change the value of a register pointed by the function rho with s
	 * @param s the value to be stored
	 */
	private void updateRegister(int s)
	{
		int[] register= ((FMA) automa).getRegister();
		int[] rho=((FMA) automa).getRho();
		register[rho[pointer_state]]=s;
		((FMA) automa).updateRegister(register);
	}
	
	/**
	 * restore the state of the simulator with the parameters in the continuation C. First invoke the method of the
	 * super class and then update the register of the automaton with a copy of the register stored in the continuation.
	 */
	protected void restoreStateContinuation(Continuation c)
	{
		super.restoreStateContinuation(c);
		((FMA) automa).updateRegister(((FMAContinuation)c).copyRegister());
	}

	/**
	 * Create a new Continuation with the freezed state of the automaton
	 */
	protected Continuation createContinuation()
	{
		return new FMAContinuation(pointer_state,pointer_string, ((FMA) automa).getRegister(), null);
	}
	
}
