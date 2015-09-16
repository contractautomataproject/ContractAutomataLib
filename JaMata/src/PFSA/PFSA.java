package PFSA;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Vector;
import FSA.FSA;
import FSA.Simulator;
import FSA.Transition;




/**
 * Extends FSA by adding the stack and registers
 * @author Davide Basile
 *
 */
@SuppressWarnings({"serial","rawtypes"})
public class PFSA  extends FSA implements java.io.Serializable{
	private Vector[] streg;
	private static String message = "*** PFSA simulator ***\n" +
			"The infinite alphabet is represented by the set of natural numbers\n" +
			"Pay attention: all the indexes start from zero";
	
	/**
	 * Invoke the super constructor and take in input the added new parameters of the automaton
	 */
	public PFSA()
	{
		super(message);
		try{
			System.out.println(); 
			InputStreamReader reader = new InputStreamReader (System.in);
	        BufferedReader myInput = new BufferedReader (reader);
			System.out.println("Insert the number of registers");
			int length = Integer.parseInt(myInput.readLine());
			streg = new Vector[length+1];
			for (int i=0;i<streg.length;i++)
				streg[i]=new Vector();
			super.write(this);
		}
		catch (Exception e){System.out.println("Errore inserimento");}
	}
	
	/**
	 * Print in output a description of the automaton
	 */
	public void print()
	{
		super.print();
		System.out.println("Number of Registers: "+ (streg.length-1));
	}
	
	/**
	 * 
	 * @return an array of Vector, the first element represents the stack, the other are the registers
	 */
	public Vector[] getStreg()
	{
		return this.streg;
	}
	
	/**
	 * Change streg with the parameter in input
	 * @param streg	an array of Vector
	 */
	public void updateStreg( Vector[] streg)
	{
		this.streg = streg;
	}
	
	/**
	 * Create a new Transition for this PFSA
	 */
	protected Transition createTransition(int i)
	{
		return new PFSATransition(i);
	}
	
	/**
	 * Create an instance of the simulator for the PFSA
	 */
	protected Simulator createSim()
	{
		return new PFSASimulator(this);
	}
}
