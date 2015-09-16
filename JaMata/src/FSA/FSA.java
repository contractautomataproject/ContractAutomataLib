package FSA;



import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;





/**
 * This class represents a FSA.
 * 
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class FSA implements java.io.Serializable{
	
	private int states;
	private int initial;
	private int[] finalstates;
	private Transition[] tra;
	private static String message = "*** FSA simulator ***\n" +
			"The alphabet is represented by the set of natural numbers\n" +
			"Pay attention: all the indexes start from zero";
	
	/**
	 * Create a FSA by taking in input the parameters
	 * @param message	a message of help
	 */
	public FSA(String message) 
	{
		System.out.println(message);
		try{
			InputStreamReader reader = new InputStreamReader (System.in);
	        BufferedReader myInput = new BufferedReader (reader);
			System.out.println("Insert the number of states:");
	        this.states = Integer.parseInt(myInput.readLine());
	        System.out.println("Insert Id of initial State (the id starts from 0)");
			initial = Integer.parseInt(myInput.readLine());
			System.out.println("Insert the number of final states");
			int finalstate = Integer.parseInt(myInput.readLine());
			finalstates = new int[finalstate];
			for(int i=0;i<finalstate;i++){
	        	System.out.println("Insert the Id of the final states number " + i);
	        	finalstates[i]=Integer.parseInt(myInput.readLine());
	        }
			System.out.println("Insert the number of transitions:");
			int num_trans= Integer.parseInt(myInput.readLine());
			tra = new Transition[num_trans];
			for (int i=0;i<num_trans;i++)
				tra[i] = createTransition(i);
		}
		catch (Exception e){System.out.println("Errore inserimento");}
	}
	
	/**
	 * Create the FSA by taking in input all the parameters and store the created Automaton.
	 */
	public FSA()
	{
		this(message);
		FSA.write(this);
	}
	
	/**
	 * initialize only the transitions
	 * @param t
	 */
	public FSA(Transition[] t)
	{
		this.tra=t;
	}
	
	/**
	 * Print in output a description of the automaton
	 */
	public void print()
	{
		System.out.println("This is the automaton:");
		System.out.println("Number of states: " + this.getStates());
		System.out.println("Initial state: " +this.getInitial());
		System.out.println("Final states: "+Arrays.toString(this.getFinalStates()));
		System.out.println("Transition: "+Arrays.toString(this.getTransition()));
	}
	
	/**
	 * Store in a file the automaton
	 * @param a		the automaton to be saved
	 */
	public static void write(FSA a)
	{
		String name=null;
		FileOutputStream f_out;
		InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader myInput = new BufferedReader(reader);
		try {
			System.out.println("Do you want to save this automaton? (write yes or no)");
			if (myInput.readLine().equals("yes"))
			{	
				System.out.println("Write the name of this automaton");
				name= myInput.readLine();
			}
			else return;
			f_out = new FileOutputStream(name+".data");
			ObjectOutputStream obj_out = new ObjectOutputStream(f_out);
			obj_out.writeObject(a);
			obj_out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Load a stored automaton
	 * 
	 * @param name	the name of the file
	 * @return	the automaton 
	 */
	public static FSA load(String name)
	{
		FileInputStream f_in;
		try {
			f_in = new FileInputStream(name+".data");
			ObjectInputStream obj_in =  new ObjectInputStream(f_in);
			Object obj = obj_in.readObject();
			obj_in.close();
			if (obj instanceof FSA)
			{
				FSA a = (FSA) obj;
				return a;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} 
		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Take in input a string to be recognized by the automaton, create an instance of the Simulator
	 * and run it with the string in input.
	 */
	public void run()
	{
		InputStreamReader reader = new InputStreamReader (System.in);
        BufferedReader myInput = new BufferedReader (reader);
		System.out.println("Insert length of the string to be recognized: ");
		int sleng;int[] srec=null;
		try {
			sleng = Integer.parseInt(myInput.readLine());
			srec = new int[sleng];
			for (int i=0; i<sleng; i++){
				System.out.println("Insert the character "+i+" of the string to be recognized");
				srec[i]= Integer.parseInt(myInput.readLine());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Simulator sim = createSim();
		if (sim.run(srec))
			System.out.println("The string "+Arrays.toString(srec)+" is accepted by the automaton");
		else
			System.out.println("The string "+Arrays.toString(srec)+" is not accepted by the automaton");
	}
	
	/**
	 * 
	 * @return the initial state
	 */
	public int getInitial()
	{
		return initial;
	}
	
	/**
	 * 
	 * @return	the array of final states
	 */
	public int[] getFinalStates()
	{
		return finalstates;
	}
	
	/**
	 * 
	 * @return	the array of transitions
	 */
	public Transition[] getTransition()
	{
		return tra;
	}
	
	/**
	 * 
	 * @param tra initialize the variable Transition
	 */
	public void setTransition(Transition[] tra)
	{
		this.tra=tra;
	}
	/**
	 * 
	 * @return	the number of states
	 */
	public int getStates()
	{
		return states;
	}
	
	/**
	 * 
	 * @return	an instance of the simulator istantiated with this automaton
	 */
	protected Simulator createSim()
	{
		return new Simulator(this);
	}
	
	/**
	 * 
	 * @param i		the index of the transition to be showed as a message to the user
	 * @return		a new Transition for this automaton
	 */
	protected Transition createTransition(int i)
	{
		return new Transition(i,true);
	}
}
