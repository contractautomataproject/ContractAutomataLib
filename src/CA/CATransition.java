package CA;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Transition of a contract automaton
 * 
 * @author Davide Basile
 *
 */
public class CATransition { 
	final private CAState source;
	final private CAState target;
	final private String[] label; //TODO only two integers+an action are needed
	final public static  String idle="-";
	final public static  String offer="!";
	final public static  String request="?";
	
	public CATransition(CAState source, String[] label, CAState target){
		this.source=source;
		this.target=target;
		this.label =label;
	}
	
	/**
	 * instantiate a transition by reconstructing the label
	 * @param source
	 * @param firstaction the first (i.e. with lower index) action occurring in label
	 * @param target
	 */
	public CATransition(CAState source, String firstaction, CAState target){
		this.source=source;
		this.target=target;
		this.label = new String[source.getState().length];
		Arrays.fill(label, idle);
		if (target!=null)//composition can initialise temporarily with null target
		{
			int[] index = IntStream.range(0, source.getState().length)
									.filter(i->source.getState()[i]!=target.getState()[i])
									.toArray();
			label[index[0]]=firstaction; //request or offer
			if (index.length>1)
				label[index[1]]=getCoAction(firstaction); //match
		}
	}
	
	
	/**
	 * 
	 * @return		the source state of the transition
	 */
	public CAState getSource()
	{
		return this.source;
	}
	
//	public void setSource(CAState s)
//	{
//		this.source=s;
//	}
	
	
	/**
	 * 
	 * @return		the target state of the transition
	 */
	public CAState getTarget()
	{
		return target;
	}
	
//	public void setTarget(CAState t)
//	{
//		this.target=t;
//	}
	
	/**
	 * 
	 * @return the label of the transition
	 */
	public String[] getLabel()
	{
		return label;
	}
	
//	public void setLabel(String[] l)
//	{
//		this.label=l;
//	}

	/**
	 * @return the action of the transition, in case of a match it returns the offer
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
	 * @return the action with lower index
	 */ 
	public String getFirstAction()
	{
		return Arrays.stream(label)
		.filter(l->l!=idle)
		.findFirst()
		.orElseThrow(IllegalArgumentException::new);
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
	
	public static String getCoAction(String label)
	{
		if (label.startsWith("!"))
			return "?"+label.substring(1);
		else if (label.startsWith("?"))
			return "!"+label.substring(1);
		else
			throw new IllegalArgumentException("The label is not an action");
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
	
	public int getSenderOrReceiver()
	{
		if (this.isMatch())
			throw new RuntimeException();
		for (int i=0;i<label.length;i++)
		{
			if (!isIdle(label,i))
				return i;
		}
		throw new RuntimeException();
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


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(label);
		result = prime * result + ((source == null) ? 0 : source.hashCode());
		result = prime * result + ((target == null) ? 0 : target.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CATransition other = (CATransition) obj;
		if (!Arrays.equals(label, other.label))
			return false;
		if (source == null) {
			if (other.source != null)
				return false;
		} else if (!source.equals(other.source))
			return false;
		if (target == null) {
			if (other.target != null)
				return false;
		} else if (!target.equals(other.target))
			return false;
		return true;
	}

}