package CA;


import java.util.Arrays;


import FSA.Transition;



/**
 * A tuple representing a Transition
 * 
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class CATransition extends Transition implements java.io.Serializable{ 
	private int[] initial;
	private int[] fina;
	private int[] label;
	
	/**
	 * 
	 * @param initial		initial state
	 * @param label2			label
	 * @param fina			final state
	 */
	public CATransition(int[] initial, int[] label2, int[] fina){
		super(0,0,0);
		this.initial=initial;
		this.fina=fina;
		this.label =label2;
	}
	
	
	/**
	 * Take in input a transition
	 * @param i			the index of the transition to be showed as a message to the user
	 */
	public CATransition(int i)
	{
		super(i,true);
		this.initial = new int[1];
		this.fina = new int[1];
		this.label = new int[1];
		initial[0]=super.getInitial();
		fina[0]=super.getFinal();
		label[0]=super.getLabel();
	}
	
	/**
	 * 
	 * @return		the initial state of the transition
	 */
	public int[] getInitialP()
	{
		return initial;
	}
	
	
	/**
	 * 
	 * @return		the final state of the transition
	 */
	public int[] getFinalP()
	{
		return fina;
	}
	
	/**
	 * 
	 * @return the label of the transition
	 */
	public int[] getLabelP()
	{
		return label;
	}
	
	/**
	 * 
	 * @return true if the transition is a match
	 */
	public boolean match()
	{
		int c=0;
		for (int i=0;i<label.length;i++)
		{
			if(label[i]!=0)
				c++;
		}
		return (c==2);
	}
	
	/**
	 * 
	 * @return true if the transition is an offer
	 */
	public boolean offer()
	{
		int c=0;
		int l=0;
		for (int i=0;i<label.length;i++)
		{
			if(label[i]!=0)
			{
				c++;
				l=label[i];
			}
		}
		return (c==1)&&(l>0);
	}
	
	/**
	 * 
	 * @return true if the transition is a request
	 */
	public boolean request()
	{
		int c=0;
		int l=0;
		for (int i=0;i<label.length;i++)
		{
			if(label[i]!=0)
			{
				c++;
				l=label[i];
			}
		}
		return (c==1)&&(l<0);
	}
	
	/**
	 * override of toString
	 */
	public String toString()
	{
		return "("+Arrays.toString(initial)+","+Arrays.toString(label)+","+Arrays.toString(fina)+")";
	}

	public boolean equals(Object t)
	{
		CATransition tr=(CATransition) t;
		int[] ip =tr.getInitialP();
		int[] lp=tr.getLabelP();
		int[] dp=tr.getFinalP();
		return ( Arrays.equals(ip,initial))&&(Arrays.equals(lp,label))&&(Arrays.equals(dp,fina));
	}	
}