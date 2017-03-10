package CA;


import java.util.Arrays;
import FSA.Transition;



/**
 * Transition of a contract automaton
 * 
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class CATransition extends Transition implements java.io.Serializable{ 
	private int[] source;
	private int[] target;
	private int[] label;
	
	/**
	 * 
	 * @param initial		source state
	 * @param label2			label
	 * @param fina			target state
	 */
	public CATransition(int[] initial, int[] label2, int[] fina){
		super(0,0,0);
		this.source=initial;
		this.target=fina;
		this.label =label2;
	}
	
	
	/**
	 * Take in input a transition
	 * @param i			the index of the transition to be showed as a message to the user
	 */
	public CATransition(int i)
	{
		super(i,true);
		this.source = new int[1];
		this.target = new int[1];
		this.label = new int[1];
		source[0]=super.getSource();
		target[0]=super.getTarget();
		if (super.getLabel()==0)
		{
			System.out.println("Error, principals do not have silent transitions, the label is automatically set to 1");
			label[0]=1;
		}
		else
			label[0]=super.getLabel();
	}
	
	/**
	 * 
	 * @return		the source state of the transition
	 */
	public int[] getSourceP()
	{
		return this.source;
	}
	
	
	/**
	 * 
	 * @return		the target state of the transition
	 */
	public int[] getTargetP()
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
	 * @return the action of the transition, in case of a match it returns the offer (positive)
	 */
	public int getAction()
	{
		if (!this.isRequest())
		{
			for (int i=0;i<label.length;i++)
			{
				if(label[i]>0)
					return label[i];
			}
		}
		else
		{
			for (int i=0;i<label.length;i++)
			{
				if(label[i]<0)
					return label[i];
			}
		}
		return 0;
	}
	
	/**
	 * 
	 * @return true if the transition is a match
	 */
	public boolean isMatch()
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
	public boolean isOffer()
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
	public boolean isRequest()
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
	public int getSender()
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
	public int getReceiver()
	{
		for (int i=0;i<label.length;i++)
		{
			if (label[i]<0)
				return i;
		}
		return -1;
	}
	
	/**
	 * override of toString
	 */
	public String toString()
	{
		return "("+Arrays.toString(source)+","+Arrays.toString(label)+","+Arrays.toString(target)+")";
	}

	public boolean equals(Object t)
	{
		CATransition tr=(CATransition) t;
		int[] ip =tr.getSourceP();
		int[] lp=tr.getLabelP();
		int[] dp=tr.getTargetP();
		return ( Arrays.equals(ip,source))&&(Arrays.equals(lp,label))&&(Arrays.equals(dp,target));
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
	 * @param t				first transition to move
	 * @param tt			second transition to move only in case of match
	 * @param firstprinci  the index to start to copy the principals in t
	 * @param firstprincii the index to start to copy the principals in tt
	 * @param insert		the states of all other principals who stays idle
	 * @return				a new transition where only principals in t (and tt) moves while the other stays idle in their state given in insert[]
	 */
	public CATransition generateATransition(Transition t, Transition tt, int firstprinci, int firstprincii,int[] insert)
	{
		if (tt!=null)
		{
			int[] s=((CATransition) t).getSourceP();
			int[] l=((CATransition) t).getLabelP();
			int[] d=((CATransition) t).getTargetP();
			int[] ss = ((CATransition) tt).getSourceP();
			int[] ll=((CATransition) tt).getLabelP();
			int[] dd =((CATransition) tt).getTargetP();
			int[] initial = new int[insert.length+s.length+ss.length];
			int[] dest = new int[insert.length+s.length+ss.length];
			int[] label = new int[insert.length+s.length+ss.length];
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
			return new CATransition(initial,label,dest);	
		}
		else
		{
			int[] s=((CATransition) t).getSourceP();
			int[] l=((CATransition) t).getLabelP();
			int[] d=((CATransition) t).getTargetP();
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
			return new CATransition(initial,label,dest);	
		}
	}
}