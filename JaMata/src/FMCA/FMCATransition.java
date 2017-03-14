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
	//private boolean must;  
	public enum action{
		PERMITTED,URGENT,GREEDY,LAZY
	}
	private action type;
	/**
	 * 
	 * @param initial		source state
	 * @param label2			label
	 * @param fina			arrival state
	 */
	public FMCATransition(int[] initial, int[] label2, int[] fina, action type){
		super(initial,label2,fina);
		this.type=type;
//		if (type!=action.PERMITTED)
//			this.must=true;
	}
	
	
	public boolean isUrgent()
	{
		return (this.type==action.URGENT);
	}
	
	public boolean isGreedy()
	{
		return (this.type==action.GREEDY);
	}
	
	
	public boolean isLazy()
	{
		return (this.type==action.LAZY);
	}
	
	
	public boolean isMust()
	{
		return (this.type!=action.PERMITTED);
	}
	
	public action getType()
	{
		return this.type;
	}
	
	/**
	 * override of toString
	 */
	public String toString()
	{
		switch (this.type) 
		{
			case PERMITTED: return "("+Arrays.toString(getSourceP())+","+Arrays.toString(getLabelP())+","+Arrays.toString(getTargetP())+")";
			case URGENT:return "!U("+Arrays.toString(getSourceP())+","+Arrays.toString(getLabelP())+","+Arrays.toString(getTargetP())+")";
			case GREEDY:return "!G("+Arrays.toString(getSourceP())+","+Arrays.toString(getLabelP())+","+Arrays.toString(getTargetP())+")";
			case LAZY:return "!L("+Arrays.toString(getSourceP())+","+Arrays.toString(getLabelP())+","+Arrays.toString(getTargetP())+")";		
		}
		return null;
	}

	public boolean equals(FMCATransition t)
	{
		FMCATransition tr=(FMCATransition) t;
		int[] ip =tr.getSourceP();
		int[] lp=tr.getLabelP();
		int[] dp=tr.getTargetP();
		action type=tr.getType();
		return ( Arrays.equals(ip,getSourceP()))&&(Arrays.equals(lp,getLabelP()))&&(Arrays.equals(dp,this.getTargetP())&&(this.type==type));
	}	
	
	/**
	 * 
	 * @return	true if the  greedy/lazy transition request is matched 
	 */
	protected  boolean isMatched(FMCA aut)
	{
		FMCATransition[] tr = aut.getTransition();
//		int[][] fs=aut.allFinalStates();
		//MSCATransition[] unmatch = new MSCATransition[tr.length];
		if ((this.isRequest())
			&&(this.isGreedy()||this.isLazy()))
		{
			int[][] R=aut.getDanglingStates();
//			if (!MSCAUtil.contains(this.getSource(), fs)) // if source state is not final
//				return true;
			for (int j=0;j<tr.length;j++)	
			{
				if ((tr[j].isMatch())
					&&((tr[j].isGreedy()&&this.isGreedy())||(tr[j].isUrgent()&&this.isUrgent()))//the same type (greedy or lazy)
					&&(tr[j].getReceiver()==this.getReceiver())	//the same principal
					&&(tr[j].getSourceP()[tr[j].getReceiver()]==this.getSourceP()[this.getReceiver()]) //the same source state					
					&&(tr[j].getLabelP()[tr[j].getReceiver()]==this.getLabelP()[this.getReceiver()]) //the same request
					&&(!FMCAUtil.contains(this.getSourceP(), R))) //source state is not redundant
					{
						return true;
					}
			}
		}
		return false;
	}



	/**
	 * 
	 * @return a new request transition where the sender of the match is idle
	 */
	public FMCATransition extractRequestFromMatch()
	{
		if (!this.isMatch())
			return null;
		int length=this.getSourceP().length;
		int sender=this.getSender();
		int[] source=Arrays.copyOf(this.getSourceP(), length);
		int[] target=Arrays.copyOf(this.getTargetP(), length);
		int[] request=Arrays.copyOf(this.getLabelP(), length);
		target[sender]=source[sender];  //the sender is now idle
		request[sender]=0;  //swapping offer to idle
		return new FMCATransition(source,request,target,this.type); //returning the request transition
		
	}
	
	/**
	 * 
	 * @return	true if the  lazy match transition is lazy unmatchable in aut
	 */
	protected  boolean isLazyUnmatchable(FMCA aut)
	{
		FMCATransition[] tr = aut.getTransition();
//		int[][] fs=aut.allFinalStates();
		//MSCATransition[] unmatch = new MSCATransition[tr.length];
		if ((this.isMatch())
			&&(this.isLazy()))
		{
			for (int j=0;j<tr.length;j++)	
			{
				if (this.equals(tr[j]))
					return false; //the transition must not be in aut
			}
			FMCATransition t= this.extractRequestFromMatch(); //extract the request transition from this
			return t.isMatched(aut); 
		}
		else
			return false;
	}
	
	/**
	 * 
	 * @param aut
	 * @return	true if the transition is uncontrollable in aut
	 */
	protected boolean isUncontrollable(FMCA aut)
	{
		return this.isUrgent()||(this.isMatch()&&this.isGreedy())||this.isMatched(aut)||this.isLazyUnmatchable(aut);
		
	}
	
	protected boolean isForbidden(Product p)
	{
		return (FMCAUtil.getIndex(p.getForbidden(),Math.abs(this.getAction()))>=0);
	}
	
	protected boolean isRequired(Product p)
	{
		return (FMCAUtil.getIndex(p.getRequired(),this.getAction())>=0);		
	}
	
	/**
	 *
	 * @param t
	 * @return   source states of transitions in t 
	 */
	protected static int[][] getSources(FMCATransition[] t)
	{
		int[][] s= new int[t.length][];
		int pointer=0;
		for (int i=0;i<t.length;i++)
		{
			if (!FMCAUtil.contains(t[i].getSourceP(), s)) //if the source state was not already inserted previously
			{
				s[pointer]=t[i].getSourceP();
				pointer++;
			}
		}
		s=FMCAUtil.removeTailsNull(s, pointer);
		return s;
	}

	/**
	 * @param t
	 * @param aut
	 * @return   source states of transitions in t that are unmatched or lazy unmatchable in aut
	 */
	protected static int[][] areMatchedOrLazyUnmatchable(FMCATransition[] t, FMCA aut)
	{
		int[][] s= new int[t.length][];
		int pointer=0;
		for (int i=0;i<t.length;i++)
		{
			if ((!t[i].isMatched(aut))||(t[i].isLazyUnmatchable(aut)))
			{
				if (!FMCAUtil.contains(t[i].getSourceP(), s)) //if the source state was not already inserted previously
				{
					s[pointer]=t[i].getSourceP();
					pointer++;
				}
			}
		}
		s=FMCAUtil.removeTailsNull(s, pointer);
		return s;
	}
	
	/**
	 * This method is different from the corresponding one in CATransition class because it deals with necessary actions
	 * 
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
			int[] s=((FMCATransition) t).getSourceP();
			int[] l=((FMCATransition) t).getLabelP();
			int[] d=((FMCATransition) t).getTargetP();
			int[] ss = ((FMCATransition) tt).getSourceP();
			int[] ll=((FMCATransition) tt).getLabelP();
			int[] dd =((FMCATransition) tt).getTargetP();
			int[] initial = new int[insert.length+s.length+ss.length];
			int[] dest = new int[insert.length+s.length+ss.length];
			int[] label = new int[insert.length+s.length+ss.length];
			action type;
			if (((FMCATransition) t).isRequest())
				type=((FMCATransition) t).getType();
			else
				type=((FMCATransition) tt).getType();
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
			return new FMCATransition(initial,label,dest,type);	
		}
		else	//is not a match
		{
			int[] s=((FMCATransition) t).getSourceP();
			int[] l=((FMCATransition) t).getLabelP();
			int[] d=((FMCATransition) t).getTargetP();
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
			return new FMCATransition(initial,label,dest,((FMCATransition) t).getType());	
		}
	}
}