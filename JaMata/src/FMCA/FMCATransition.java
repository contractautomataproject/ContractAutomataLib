package FMCA;


import java.util.Arrays;





import CA.CATransition;
import FSA.Transition;



/**
 * Transition of a contract automaton
 * 
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class FMCATransition extends CATransition implements java.io.Serializable{ 
	private boolean must;  
	
	/**
	 * 
	 * @param initial		source state
	 * @param label2			label
	 * @param fina			arrival state
	 */
	public FMCATransition(int[] initial, int[] label2, int[] fina,boolean must){
		super(initial,label2,fina);
		this.must=must;
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
			return "!("+Arrays.toString(getSource())+","+Arrays.toString(getLabelP())+","+Arrays.toString(getArrival())+")";
		else
			return "("+Arrays.toString(getSource())+","+Arrays.toString(getLabelP())+","+Arrays.toString(getArrival())+")";
	}

	public boolean equals(FMCATransition t)
	{
		FMCATransition tr=(FMCATransition) t;
		int[] ip =tr.getSource();
		int[] lp=tr.getLabelP();
		int[] dp=tr.getArrival();
		boolean must=tr.isMust();
		return ( Arrays.equals(ip,getSource()))&&(Arrays.equals(lp,getLabelP()))&&(Arrays.equals(dp,this.getArrival())&&(this.must==must));
	}	
	
	/**
	 * 
	 * @return	true if the  must transition request is matched 
	 */
	protected  boolean isMatched(FMCA aut)
	{
		FMCATransition[] tr = aut.getTransition();
//		int[][] fs=aut.allFinalStates();
		int[][] R=aut.getDanglingStates();
		//MSCATransition[] unmatch = new MSCATransition[tr.length];
		if ((this.request())
			&&(this.isMust()))
		{

//			if (!MSCAUtil.contains(this.getSource(), fs)) // if source state is not final
//				return true;
			for (int j=0;j<tr.length;j++)	
			{
				if ((tr[j].match())
					&&(tr[j].isMust())
					&&(tr[j].receiver()==this.receiver())	//the same principal
					&&(tr[j].getSource()[tr[j].receiver()]==this.getSource()[this.receiver()]) //the same source state					
					&&(tr[j].getLabelP()[tr[j].receiver()]==this.getLabelP()[this.receiver()]) //the same request
					&&(!FMCAUtil.contains(this.getSource(), R))) //source state is not redundant
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
	protected static int[][] sourcesUnmatched(FMCATransition[] t, FMCA aut)
	{
		int[][] s= new int[t.length][];
		int pointer=0;
		for (int i=0;i<t.length;i++)
		{
			if (!t[i].isMatched(aut))
			{
				if (!FMCAUtil.contains(t[i].getSource(), s))
				{
					s[pointer]=t[i].getSource();
					pointer++;
				}
			}
		}
		s=FMCAUtil.removeTailsNull(s, pointer);
		return s;
	}
	
	/**
	 * This method is different from the corresponding one in CATransition class because it deals with must transitions
	 * 
	 * TODO fix CAUtil to call this method
	 * 
	 * @param t				first transition to move
	 * @param tt			second transition to move only in case of match
	 * @param firstprinci  the index to start to copy the principals in t
	 * @param firstprincii the index to start to copy the principals in tt
	 * @param insert		the states of all other principals who stays idle
	 * @return				a new transition where only principals in t (and tt) moves while the other stays idle in their state given in insert[]
	 */
	public FMCATransition generateATransition(Transition t, Transition tt, int firstprinci, int firstprincii,int[] insert)
	{
		if (tt!=null) //if it is a match
		{
			int[] s=((FMCATransition) t).getSource();
			int[] l=((FMCATransition) t).getLabelP();
			int[] d=((FMCATransition) t).getArrival();
			int[] ss = ((FMCATransition) tt).getSource();
			int[] ll=((FMCATransition) tt).getLabelP();
			int[] dd =((FMCATransition) tt).getArrival();
			int[] initial = new int[insert.length+s.length+ss.length];
			int[] dest = new int[insert.length+s.length+ss.length];
			int[] label = new int[insert.length+s.length+ss.length];
			boolean must = ((FMCATransition) t).isMust() || ((FMCATransition) tt).isMust();
			int counter=0;
			for (int i=0;i<insert.length;i++)
			{
				if (i==firstprinci)
				{
					for (int j=0;j<s.length;j++)
					{
						initial[i+j]=s[j];
						label[i+j]=l[j];
						dest[i+j]=d[j];
					}
					counter+=s.length; //record the shift due to the first CA 
					i--;
					firstprinci=-1;
				}
				else 
				{
					if (i==firstprincii)
					{
						for (int j=0;j<ss.length;j++)
						{
							initial[i+counter+j]=ss[j];
							label[i+counter+j]=ll[j];
							dest[i+counter+j]=dd[j];
						}
						counter+=ss.length;//record the shift due to the second CA 
						i--;
						firstprincii=-1;
					}	
					else 
					{
						initial[i+counter]=insert[i];
						dest[i+counter]=insert[i];
						label[i+counter]=0;
					}
				}
			}
			if (firstprinci==insert.length)//case limit, the first CA was the last of aut
			{
				for (int j=0;j<s.length;j++)
				{
					initial[insert.length+j]=s[j];
					label[insert.length+j]=l[j];
					dest[insert.length+j]=d[j];
				}
				counter+=s.length; //record the shift due to the first CA 
			}
			if (firstprincii==insert.length) //case limit, the second CA was the last of aut
			{
				for (int j=0;j<ss.length;j++)
				{
					initial[insert.length+counter+j]=ss[j];
					label[insert.length+counter+j]=ll[j];
					dest[insert.length+counter+j]=dd[j];
				}
			}
			return new FMCATransition(initial,label,dest,must);	
		}
		else
		{
			int[] s=((FMCATransition) t).getSource();
			int[] l=((FMCATransition) t).getLabelP();
			int[] d=((FMCATransition) t).getArrival();
			int[] initial = new int[insert.length+s.length];
			int[] dest = new int[insert.length+s.length];
			int[] label = new int[insert.length+s.length];
			int counter=0;
			for (int i=0;i<insert.length;i++)
			{
				if (i==firstprinci)
				{
					for (int j=0;j<s.length;j++)
					{
						initial[i+j]=s[j];
						label[i+j]=l[j];
						dest[i+j]=d[j];
					}
					counter+=s.length; //record the shift due to the first CA 
					i--;
					firstprinci=-1;
				}
				else
				{
					initial[i+counter]=insert[i];
					dest[i+counter]=insert[i];
					label[i+counter]=0;
				}
			}
			if (firstprinci==insert.length)//case limit, the first CA was the last of aut
			{
				for (int j=0;j<s.length;j++)
				{
					initial[insert.length+j]=s[j];
					label[insert.length+j]=l[j];
					dest[insert.length+j]=d[j];
				}
				counter+=s.length; //record the shift due to the first CA 
			}
			return new FMCATransition(initial,label,dest,((FMCATransition) t).isMust());	
		}
	}
}