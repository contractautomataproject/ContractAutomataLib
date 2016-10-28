package MSCA;


import java.util.Arrays;



import FSA.Transition;



/**
 * Transition of a contract automaton
 * 
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class MSCATransition extends Transition implements java.io.Serializable{ 
	private int[] source;
	private int[] target;
	private int[] label;
	private boolean must;  
	
	/**
	 * 
	 * @param initial		source state
	 * @param label2			label
	 * @param fina			arrival state
	 */
	public MSCATransition(int[] initial, int[] label2, int[] fina,boolean must){
		super(0,0,0);
		this.source=initial;
		this.target=fina;
		this.label =label2;
		this.must=must;
	}
	
	
	/**
	 * Take in input a transition
	 * @param i			the index of the transition to be showed as a message to the user
	 *
	public MSCATransition(int i)
	{
		super(i,true);
		this.source = new int[1];
		this.target = new int[1];
		this.label = new int[1];
		source[0]=super.getInitial();
		target[0]=super.getFinal();
		if (super.getLabel()==0)
		{
			System.out.println("Error, principals do not have silent transitions, the label is automatically set to 1");
			label[0]=1;
		}
		else
			label[0]=super.getLabel();
	}*/
	
	/**
	 * 
	 * @return		the source state of the transition
	 */
	public int[] getSource()
	{
		return source;
	}
	
	
	/**
	 * 
	 * @return		the arrival state of the transition
	 */
	public int[] getArrival()
	{
		return target;
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
	 * 
	 * @return the index of the sender or -1 
	 */
	public int sender()
	{
		for (int i=0;i<label.length;i++)
		{
			if (label[i]>0)
				return i;
		}
		return -1;
	}
	
	/**
	 * 
	 * @return the index of the receiver or -1 
	 */
	public int receiver()
	{
		for (int i=0;i<label.length;i++)
		{
			if (label[i]<0)
				return i;
		}
		return -1;
	}
	
	public boolean isMust()
	{
		return this.must;
	}
	
	/**
	 * override of toString
	 */
	public String toString()
	{
		if (this.must)
			return "!("+Arrays.toString(source)+","+Arrays.toString(label)+","+Arrays.toString(target)+")";
		else
			return "("+Arrays.toString(source)+","+Arrays.toString(label)+","+Arrays.toString(target)+")";
	}

	public boolean equals(MSCATransition t)
	{
		MSCATransition tr=(MSCATransition) t;
		int[] ip =tr.getSource();
		int[] lp=tr.getLabelP();
		int[] dp=tr.getArrival();
		boolean must=tr.isMust();
		return ( Arrays.equals(ip,source))&&(Arrays.equals(lp,label))&&(Arrays.equals(dp,target)&&(this.must==must));
	}	
	
	/**
	 * check if labels l and ll are in match
	 * @param l
	 * @param ll
	 * @return true if there is a match, false otherwise
	 */
	public static boolean match(int[] l,int[] ll)
	{
		int m=-1000; int mm=-1000;
		for (int i=0;i<l.length;i++)
		{
			if (l[i]!=0)
			{
				if (m==-1000)
					m=l[i];
				else
					return false; //l is a match
			}
		}
		for (int i=0;i<ll.length;i++)
			if (ll[i]!=0)
			{
				if(mm==-1000)
					mm=ll[i];
				else
					return false; // ll is a match
			}
		return ((m+mm) == 0)&&(m!=-1000)&&(mm!=-1000); 
	}
	/**
	 * 
	 * @return	true if the  must transition request is matched 
	 */
	protected  boolean isMatched(MSCA aut)
	{
		MSCATransition[] tr = aut.getTransition();
		int[][] fs=aut.allFinalStates();
		int[][] R=aut.getRedundantStates();
		//MSCATransition[] unmatch = new MSCATransition[tr.length];
		if ((this.request())
			&&((this.isMust())
			&&(!MSCAUtil.contains(this.getSource(), fs)))) // if source state is not final
		{
			for (int j=0;j<tr.length;j++)	
			{
				if ((tr[j].match())
					&&(tr[j].isMust())
					&&(tr[j].receiver()==this.receiver())	//the same principal
					&&(tr[j].getSource()[tr[j].receiver()]==this.getSource()[this.receiver()]) //the same source state					
					&&(tr[j].getLabelP()[tr[j].receiver()]==this.getLabelP()[this.receiver()]) //the same request
					&&(!MSCAUtil.contains(this.getSource(), R))) //source state is not redundant
					{
						return true;
					}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param t
	 * @param aut
	 * @return   source states of transitions in t that are unmatched in aut
	 */
	protected static int[][] sourcesUnmatched(MSCATransition[] t, MSCA aut)
	{
		int[][] s= new int[t.length][];
		int pointer=0;
		for (int i=0;i<t.length;i++)
		{
			if (!t[i].isMatched(aut))
			{
				if (!MSCAUtil.contains(t[i].getSource(), s))
				{
					s[pointer]=t[i].getSource();
					pointer++;
				}
			}
		}
		MSCAUtil.removeTailsNull(s, pointer);
		return s;
	}
}