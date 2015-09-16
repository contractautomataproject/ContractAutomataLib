package FMA;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import FSA.FSA;
import FSA.Simulator;



/**
 * Extends the FSA by including a function rho for each state and a set of registers.
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class FMA  extends FSA implements java.io.Serializable
{
	private int[] rho;
	private int[] register;
	private static String message = "*** FMA simulator ***\n" +
			"The infinite alphabet is represented by the set of natural numbers\n" +
			"-1 is used as a special character for the empty register\n" +
			"Pay attention: all the indexes start from zero";
	
	/**
	 * Invoke the super constructor and take in input the added new parameters of the automaton
	 */
	public FMA()
	{
		super(message);
		try{
			System.out.println(); 
			
			InputStreamReader reader = new InputStreamReader (System.in);
	        BufferedReader myInput = new BufferedReader (reader);
	        
			System.out.println("Insert the length of register");
			int length = Integer.parseInt(myInput.readLine());
			register = new int[length];
			for (int i=0;i<length;i++){
				System.out.println("Insert the value of the register at position "+i+" (use -1 for empty register)");	
				register[i]=Integer.parseInt(myInput.readLine());
			}
			if (this.checkRegister())
			{
				System.out.println("The automaton is not an FMA");
				register=null;
				return;
			}
	        rho= new int[this.getStates()];
	        for(int i=0;i<this.getStates();i++){
	        	System.out.println("Insert the value for Rho("+i+"), use -1 for non-defined");
	        	rho[i]=Integer.parseInt(myInput.readLine());
	        }
			super.write(this);
		}
		catch (Exception e){e.printStackTrace();}
	}
	
	/**
	 * 
	 * @return the registers of the automaton
	 */
	public int[] getRegister()
	{
		return register;
	}
	
	/**
	 * 
	 * @return the function rho of the automaton
	 */
	public int[] getRho()
	{
		return rho;
	}
	
	/**
	 * Change the actual register with those in input
	 * @param r the registers to be saved
	 */
	public void updateRegister(int[] r)
	{
		this.register=r;
	}
	
	/**
	 * Print in output a description of the automaton
	 */
	public void print()
	{
		super.print();
		System.out.println("Rho: "+ Arrays.toString(rho));
		System.out.println("Register: "+ Arrays.toString(register));
	}
	
	/**
	 * 
	 * @return true if the automaton is not an FMA
	 */
	private boolean checkRegister()
	{
	  for(int i=0;i<register.length;i++)
	  {
		  for (int j=i+1;j<register.length;j++)
		  {
			  if ((register[i]==register[j])&&(register[i]!=-1))
			  {
				  return true;
			  }
		  }
	  }
	  return false;
	}
	
	/**
	 * Create an instance of the simulator for an FMA
	 */
	protected Simulator createSim()
	{
		return new FMASimulator(this);
	}
	
}
