package CA;


/**
 * Transition of a contract automaton
 * 
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class CATransition //extends Transition
		implements java.io.Serializable{ 
	private CAState source;
	private CAState target;
	private String[] label;
	public static  String idle="-";
	public static  String offer="!";
	public static  String request="?";
	
	public CATransition(CAState source, String[] label, CAState target){
		this.source=source;
		this.target=target;
		this.label =label;
	}
	
	/**
	 * 
	 * @return		the source state of the transition
	 */
	public CAState getSourceP()
	{
		return this.source;
	}
	
	public void setSourceP(CAState s)
	{
		this.source=s;
	}
	
	
	/**
	 * 
	 * @return		the target state of the transition
	 */
	public CAState getTargetP()
	{
		return target;
	}
	
	public void setTargetP(CAState t)
	{
		this.target=t;
	}
	
	/**
	 * 
	 * @return the label of the transition
	 */
	public String[] getLabelP()
	{
		return label;
	}
	
	public void setLabelP(String[] l)
	{
		this.label=l;
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
		String label=this.getAction();
		if (label.startsWith("!")||label.startsWith("?"))
			return label.substring(1);
		else
			return label;
	}
	
	public static String getUnsignedAction(String label)
	{
		if (label.startsWith("!")||label.startsWith("?"))
			return label.substring(1);
		else
			return label;
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
	 * check if labels l and ll are in match
	 * @param l
	 * @param ll
	 * @return true if there is a match, false otherwise
	 */
	public static boolean match(String[] l,String[] ll)
	{
		int[] dummy=null;
		CATransition t1=new CATransition(new CAState(dummy),l,new CAState(dummy));
		CATransition t2=new CATransition(new CAState(dummy),ll,new CAState(dummy));
		if (t1.isMatch()||t2.isMatch())	//both transitions must not be match (non-associative)
			return false;
		if (t1.isOffer()&&t2.isOffer())
			return false;
		if (t1.isRequest()&&t2.isRequest())
			return false;
		return isMatch(t1.getAction(),t2.getAction());
	}
}