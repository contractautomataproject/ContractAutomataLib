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
	private String[] label;
	public static  String idle="-";
	public static  String offer="!";
	public static  String request="?";
	/**
	 * 
	 */
	public CATransition(int[] source, String[] label, int[] target){
		super(0,0,0);
		this.source=source;
		this.target=target;
		this.label =label;
	}
	
	
	/**
	 * Take as input a transition
	 * @param i			the index of the transition to be showed as a message to the user
	 */
/*	public CATransition(int i)
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
	*/
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
	public String[] getLabelP()
	{
		return label;
	}
	
	/**
	 * 
	 * @return the action of the transition, in case of a match it returns the offer (positive)
	 */
	public String getAction()
	{
		if (this.isRequest())
		{
			for (int i=0;i<label.length;i++)
			{
				if(!isIdle(label,i))
					return label[i];
			}
		}
		else
		{
			for (int i=0;i<label.length;i++)
			{
				if(isOffer(label, i))
					return label[i];
			}
		}
		return null;
	}
	

	/**
	 * 
	 * @return true if the transition is a match
	 */
	public boolean isMatch()
	{
		int c=0;
		String[] s = new String[2];
		for (int i=0;i<label.length;i++)
		{
			if(!isIdle(label,i))
			{
				s[c]=label[i];
				c++;
			}
		}
		return (c==2)&&isMatch(s[0],s[1]);
	}
	
	private static boolean isMatch(String l1, String l2)
	{
		//TODO: check
		return l1.substring(1).equals(l2.substring(1));
	}
	
	public String getUnsignedAction()
	{
		String act=this.getAction();
		return act.substring(1);
	}
	
	public static String getUnsignedAction(String label)
	{
		return label.substring(1);
	}
	
	/**
	 * 
	 * @return true if the transition is an offer
	 */
	public boolean isOffer()
	{
		int c=0;
		int index=-1;
		for (int i=0;i<label.length;i++)
		{
			if(!isIdle(label,i))
			{
				c++;
				index=i;
			}
		}
		return (c==1)&&isOffer(label, index);
	}
	
	/**
	 * 
	 * @return true if the transition is a request
	 */
	public boolean isRequest()
	{
		int c=0;
		int index=-1;
		for (int i=0;i<label.length;i++)
		{
			if(!isIdle(label,i))
			{
				c++;
				index=i;
			}
		}
		return (c==1)&&isRequest(label, index);
	}
	
	/**
	 * 
	 * @param label
	 * @param i
	 * @return 	true if principal i is idle in the label l
	 */
	private static boolean isIdle(String[] l, int i)
	{
		return l[i].contains(idle);
	}
	
	private static boolean isRequest(String[] l, int i)
	{
		return l[i].substring(0,1).equals(request);
	}
	
	private static boolean isOffer(String[] l, int i)
	{
		return  l[i].substring(0,1).equals(offer);
	}
	
	/**
	 * 
	 * @return the index of the sender or -1 
	 */
	public int getSender()
	{
		for (int i=0;i<label.length;i++)
		{
			if (isOffer(label,i))
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
			if (isRequest(label,i))
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
		String[] lp=tr.getLabelP();
		int[] dp=tr.getTargetP();
		return ( Arrays.equals(ip,source))&&(Arrays.equals(lp,label))&&(Arrays.equals(dp,target));
	}	
	
	/**
	 * check if labels l and ll are in match
	 * @param l
	 * @param ll
	 * @return true if there is a match, false otherwise
	 */
	public static boolean match(String[] l,String[] ll)
	{
		int[] dummy=null;
		CATransition t1=new CATransition(dummy,l,dummy);
		CATransition t2=new CATransition(dummy,ll,dummy);
		if (t1.isMatch()||t2.isMatch())	//both transitions must not be match (non-associative)
			return false;
		if (t1.isOffer()&&t2.isOffer())
			return false;
		if (t1.isRequest()&&t2.isRequest())
			return false;
		return isMatch(t1.getAction(),t2.getAction());
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
			String[] l=((CATransition) t).getLabelP();
			int[] d=((CATransition) t).getTargetP();
			int[] ss = ((CATransition) tt).getSourceP();
			String[] ll=((CATransition) tt).getLabelP();
			int[] dd =((CATransition) tt).getTargetP();
			int[] initial = new int[insert.length+s.length+ss.length];
			int[] dest = new int[insert.length+s.length+ss.length];
			String[] label = new String[insert.length+s.length+ss.length];
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
						label[i+counter]=idle;
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
			String[] l=((CATransition) t).getLabelP();
			int[] d=((CATransition) t).getTargetP();
			int[] initial = new int[insert.length+s.length];
			int[] dest = new int[insert.length+s.length];
			String[] label = new String[insert.length+s.length];
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
					label[i+counter]=idle;
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