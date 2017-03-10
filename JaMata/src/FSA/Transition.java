package FSA;
import java.io.BufferedReader;
import java.io.InputStreamReader;



/**
 * A tuple representing a Transition
 * 
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class Transition implements java.io.Serializable {
	private int source;
	private int label;
	private int target;
	
	/**
	 * 
	 * @param initial		initial state
	 * @param label			label
	 * @param fina			final state
	 */
	public Transition(int initial, int label, int fina){
		this.source=initial;
		this.label=label;
		this.target=fina;
	}
	
	
	/**
	 * Take in input a transition
	 * @param i			the index of the transition to be showed as a message to the user
	 * @param label		true if the label must be specified, otherwise false
	 */
	public Transition(int i,boolean label)
	{
		InputStreamReader reader = new InputStreamReader (System.in);
        BufferedReader myInput = new BufferedReader (reader);
		try {
			System.out.println("Insert start state of the transition "+i);
			this.source = Integer.parseInt(myInput.readLine());
			if (label)
			{
				System.out.println("Insert label of the transition "+i);
				this.label = Integer.parseInt(myInput.readLine());
			}
			System.out.println("Insert arrival state of the transition "+i);
			this.target = Integer.parseInt(myInput.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		};
	}
	
	/**
	 * 
	 * @return		the initial state of the transition
	 */
	public int getSource()
	{
		return source;
	}
	
	/**
	 * 
	 * @return		the label state of the transition
	 */
	public int getLabel()
	{
		return label;
	}
	
	/**
	 * 
	 * @return		the final state of the transition
	 */
	public int getTarget()
	{
		return target;
	}
	
	/**
	 * override of toString
	 */
	public String toString()
	{
		return "("+source+","+label+","+target+")";
	}

	/**
	 * Returns all the transitions starting from a state n
	 * @param n		the initial state
	 * @param tr	the array of transitions
	 * @return the transitions of tr starting from state n
	 */
	public static Transition[] getTransitionFrom(int n, Transition[] tr)
	{
		if (tr==null)
			return null;
		Transition[] t = new Transition[tr.length];
		int j=0;
		for (int i=0;i<tr.length;i++)
		{
			if (tr[i].getSource()==n)
			{
				t[j]=tr[i];
				j++;
			}
		}
		if (j==0)
			return null;
		Transition[] tt = new Transition[j];
		for (int i=0;i<j;i++)
		{
			tt[i]=t[i];
		}
		return tt;
	}
	
	/**
	 * Return all the transitions with label l
	 * @param l		the label
	 * @param tr	an array of transitions
	 * @return the transitions of tr with the label equals to l
	 */
	public static Transition[] getTransitionByLabel(int l, Transition[] tr)
	{
		if (tr==null)
			return null;
		Transition[] t = new Transition[tr.length];
		int j=0;
		for (int i=0;i<tr.length;i++)
		{
			if (tr[i].getLabel()==l)
			{
				t[j]=tr[i];
				j++;
			}
		}

		if (j==0)
			return null;
		Transition[] tt = new Transition[j];
		for (int i=0;i<j;i++)
		{
			tt[i]=t[i];
		}
		return tt;
	}	
}