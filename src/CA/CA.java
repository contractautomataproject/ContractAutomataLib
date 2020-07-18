package CA;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Scanner;

//import FSA.FSA;


/** 
 * Class implementing a Contract Automaton and its functionalities
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class CA  implements java.io.Serializable
{
	private int rank;
	private CAState initial;
	private int[] states;
	private int[][] finalstates; 
	private CATransition[] tra;
	
	/**
	 * Invoke the super constructor and take in input the added new parameters of the automaton
	 */
	public CA()
	{
		try{
			System.out.println();
	        this.rank = 1;
	        this.states = new int[1];
	      //  this.states[0] = super.getStates();
	        int[] ini= new int[1]; ini[0]=0;
	        this.initial = new CAState(ini);
	        this.initial.setInitial(true);
	       
	        finalstates = new int[1][1];//super.getFinalStates().length];
	        finalstates[0]= new int[1];//super.getFinalStates();
	       // this.tra=(CATransition[])super.getTransition();
	        //super.write(this);
	        this.printToFile("");
		}
		catch (Exception e){System.out.println("Errore inserimento");}
	}
	
	public CA(int rank, CAState initial, int[] states, int[][] finalstates,CATransition[] tra)
	{
		//super(tra);
		this.tra=tra;
		this.rank=rank;
		this.initial=initial;
		this.states=states;
		this.finalstates=finalstates;
	}
	
	/**
	 * 
	 * @param tra initialize the variable Transition
	 */
	public void setTransition(CATransition[] tra)
	{
		this.tra=tra;
	}
	
	/**
	 * print the description of the CA to a file
	 */
	public void printToFile(String filename)
	{
		String name=null;
		InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader myInput = new BufferedReader(reader);
		try {
			if (filename=="")
			{
				System.out.println("Do you want to save this automaton? (write yes or no, default yes)");
				if (!myInput.readLine().equals("no"))
				{	
					System.out.println("Write the name of this automaton");
					name= myInput.readLine();
				}
				else return;
			}
			else
				name=filename;
			 
			PrintWriter pr = new PrintWriter(name+".data"); 
			 pr.println("Rank: "+this.rank);
			 pr.println("Number of states: "+Arrays.toString(this.getStatesCA()));
			 pr.println("Initial state: " +Arrays.toString(this.getInitialCA().getState()));
			 pr.print("Final states: [");
			 for (int i=0;i<finalstates.length;i++)
				 pr.print(Arrays.toString(finalstates[i]));
			 pr.print("]\n");
			 pr.println("Transitions: \n");
			 CATransition[] t = this.getTransition();
			 if (t!=null)
			 {
				 for (int i=0;i<t.length;i++)
					 pr.println(t[i].toString());
			 }
			 pr.close();
		}catch(Exception e){e.printStackTrace();}
	}
	
	protected static CATransition loadTransition(String str, int rank)
	{
		  int what=0;
		  String[] ss=str.split("]");
		  int[][] store=new int[1][];
		  String[] label = new String[rank];
		  for (int i=0;i<ss.length;i++)
			  //TODO check
		  {
			  int[] statestransition = new int[rank];
			  Scanner s = new Scanner(ss[i]);
			  s.useDelimiter(",|\\[| ");
			  int j=0;
			  while (s.hasNext())
			  {
				  if (what==0||what==2)//source or target
				  {
					  if (s.hasNextInt())
					  {
						  statestransition[j]=s.nextInt();
						 j++;
					  }
					  else {
						   s.next();
					  }
				  }
				  else
				  {
					  if (s.hasNext())
					  {
						  String action=s.next();
						  if (action.contains(CATransition.idle))
							  label[j]=CATransition.idle;
						  else if (action.contains(CATransition.offer))
							  label[j]=action.substring(action.indexOf(CATransition.offer));
						  else if (action.contains(CATransition.request))
							  label[j]=action.substring(action.indexOf(CATransition.request));
						  else
							  j--; //trick for not increasing the counter j
						  
						  j++;
					  }
					  else {
						   s.next();
					  }
				  }
			  }
			  s.close();
			  if (what==2)
			  {
				  return new CATransition(new CAState(store[0]),label,new CAState(statestransition));
			  }
			  else
			  {
				  if (what==0)
					  store[what]=statestransition; //the source state
			  }
			  what++;
		  }
		  return null;
	}
	
	/**
	 * 
	 * @return	the array of final states
	 */
	public int[][] getFinalStatesCA()
	{
		return finalstates;
	}
	
	public void setFinalStatesCA(int[][] fs)
	{
		this.finalstates=fs;
	}
	
	/**
	 * 
	 * @return	the array of states
	 */
	public int[] getStatesCA()
	{
		return states;
	}
	
	/**
	 * 
	 * @return	the array of initial states
	 */
	public CAState getInitialCA()
	{
		return initial;
	}
	
	public void setInitialCA(CAState i)
	{
		this.initial=i;
	}
	
	/**
	 * 
	 * @return the rank of the Contract Automaton
	 */
	public int getRank()
	{
		return rank;
	}
	
	/**
	 * 
	 * @return	the array of transitions
	 */
	public CATransition[] getTransition()
	{
		/*Transition[] temp = super.getTransition();
		CATransition[] t = new CATransition[temp.length];
		for (int i=0;i<temp.length;i++)
				t[i]=(CATransition)temp[i];
		return t;*/
		return tra;
	}
	
	/**
	 * The sum all states of all principals
	 * @return The sum all states of all principals
	 */
	public int sumStates()
	{
		int numstates=0;
		for (int i=0;i<states.length;i++)
		{
			numstates+=states[i];
		}
		return numstates;
	}
	
	/**
	 * The product of the states of all principals
	 * @return The product of the states of all principals
	 */
	public int prodStates()
	{
		int prodstates=1;
		for (int i=0;i<states.length;i++)
		{
			prodstates*=states[i];
		}
		return prodstates;
	}
	
	/**
	 * 
	 * @return the maximum number of states
	 */
	public int numberOfStates()
	{
		int n=1;
		int[] states=this.getStatesCA();
		for (int i=0;i<states.length;i++)
		{
			n*=(states[i]+1);
		}
		return n;
	}
	
}