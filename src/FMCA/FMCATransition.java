package FMCA;


import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import CA.CAState;
import CA.CATransition;



/**
 * Transition of a featured modal contract automaton
 * 
 * @author Davide Basile
 *
 */
public class FMCATransition extends CATransition { 
	public enum action{
		PERMITTED,URGENT,GREEDY,LAZY		//TODO remove greedy
	}
	private action type;


	public FMCATransition(CAState source, String[] label, CAState target, action type)//TODO remove String[]
	{
		super(source,label,target);
		this.type=type;
	}


	public FMCATransition(CAState source, Integer p1, String action, Integer p2, CAState target, action type)
	{
		super(source,p1,action,p2,target);
		this.type=type;
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


	public boolean isNecessary()
	{
		return (this.type!=action.PERMITTED);
	}

	public boolean isPermitted()
	{
		return (this.type==action.PERMITTED);
	}
	
	public boolean isSemiControllable()
	{
		return (this.type==action.LAZY)||(this.type==action.GREEDY);
	}

	public action getType()
	{
		return this.type;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		FMCATransition other = (FMCATransition) obj;
		if (type != other.type)
			return false;
		return true;
	}


	//@Override
	public String toString()
	{
		if (getSource()!=null&&getLabel()!=null)
			switch (this.type) 
			{
			case PERMITTED: return "("+Arrays.toString(getSource().getState())+","+Arrays.toString(getLabel())+","+Arrays.toString(getTarget().getState())+")";
			case URGENT:return "!U("+Arrays.toString(getSource().getState())+","+Arrays.toString(getLabel())+","+Arrays.toString(getTarget().getState())+")";
			case GREEDY:return "!G("+Arrays.toString(getSource().getState())+","+Arrays.toString(getLabel())+","+Arrays.toString(getTarget().getState())+")";
			case LAZY:return "!L("+Arrays.toString(getSource().getState())+","+Arrays.toString(getLabel())+","+Arrays.toString(getTarget().getState())+")";		
			}
		return null;
	}


	/**
	 * 
	 * @return	true if the  greedy/lazy transition request is controllable 
	 */
	boolean isControllableLazyRequest(Set<FMCATransition> tr, Set<CAState> badStates)
	{
		if (this.isRequest()&&this.isLazy())
		{
			for (FMCATransition t : tr)	
			{
				if ((t.isMatch())
						&&(t.isLazy()&&this.isLazy())//the same type (lazy)
						&&(t.getReceiver()==this.getReceiver())	//the same principal
						&&(t.getSource().getState()[t.getReceiver()]==this.getSource().getState()[this.getReceiver()]) //the same source state					
						&&(t.getLabel()[t.getReceiver()].equals(this.getLabel()[this.getReceiver()])) //the same request
						&&(!badStates.contains(this.getSource()))) //source state is not bad
				{
					return true;
				}
			}
			return false;
		}
		return true; // trivially matched, it is not a request or it is not lazy
	}


	/**
	 *  it checks that the transition that matches has the same source state of this
	 * @return	true if the  lazy transition request is matched 
	 */
	boolean isControllableLazyOffer(Set<FMCATransition> tr, Set<CAState> badStates)
	{
		if ((this.isOffer()&&this.isLazy()))
		{
			for (FMCATransition t : tr)	
			{
				if ((t.isMatch())
						&&(t.isLazy()&&this.isLazy())//the same type (lazy)
						&&(t.getSender()==this.getSender())	//the same principal
						&&(t.getSource().getState()[t.getSender()]==this.getSource().getState()[this.getSender()]) //the same source state					
						&&(t.getLabel()[t.getSender()].equals(this.getLabel()[this.getSender()])) //the same offer (different from orchestration!)
						&&(t.getSource().equals(this.getSource())) //the same source state because it is a semi-controllable for choreography
						&&(!badStates.contains(this.getSource())) //source state is not redundant
						&&(!badStates.contains(t.getTarget()))) //target state is not redundant
				{
					return true;
				}
			}
			return false;
		}
		return true; // trivially matched, it is not an offer or it is not lazy
	}

	/**
	 * 
	 * @return a new request transition where the sender of the match is idle
	 */
	public FMCATransition extractRequestFromMatch()
	{
		if (!this.isMatch())
			return null;
		int sender=this.getSender();
		CAState source= this.getSource(); 
		CAState target= this.getTarget();  
		String[] request=Arrays.copyOf(this.getLabel(), this.getLabel().length);
		target.getState()[sender]=source.getState()[sender];  //the sender is now idle
		request[sender]=CATransition.idle;  //swapping offer to idle
		return new FMCATransition(source,request,target,this.type); //returning the request transition

	}

	/**
	 * 
	 * @return a new request transition where the sender of the match is idle
	 */
	public FMCATransition extractOfferFromMatch()
	{
		if (!this.isMatch())
			return null;
		int receiver=this.getReceiver();
		CAState source= this.getSource();
		CAState target= this.getTarget();
		String[] offer=Arrays.copyOf(this.getLabel(), this.getLabel().length);
		target.getState()[receiver]=source.getState()[receiver];  
		offer[receiver]=CATransition.idle;  //swapping request to idle, and target state equal source state. The receiver is now idle
		return new FMCATransition(source,offer,target,this.type); //returning the request transition

	}

	/**
	 * 
	 * @return	true if the transition is uncontrollable
	 */
	boolean isUncontrollableOrchestration(Set<FMCATransition> tr, Set<CAState> badStates)
	{
		return this.isUrgent()//||(this.isMatch()&&this.isGreedy())
				||!this.isControllableLazyRequest(tr,badStates)
				||this.isMatch()&&this.isLazy()&&!tr.contains(this)&& 
				!this.extractRequestFromMatch().isControllableLazyRequest(tr,badStates);
	}

	/**
	 * Readapted for a choreography,
	 * 
	 * @param aut
	 * @return	true if the transition is uncontrollable
	 */
	boolean isUncontrollableChoreography(Set<FMCATransition> tr, Set<CAState> badStates)
	{
		return !this.isControllableLazyOffer(tr, badStates)
				||this.isMatch()&&this.isLazy()&&!tr.contains(this)&& 
				!this.extractOfferFromMatch().isControllableLazyOffer(tr,badStates);
	}

	boolean isForbidden(Product p)
	{
		return (FMCAUtil.getIndex(p.getForbidden(),this.getUnsignedAction())>=0);
	}

	boolean isRequired(Product p)
	{
		return (FMCAUtil.getIndex(p.getRequired(),this.getUnsignedAction())>=0);		
	}

	/**
	 *
	 * @param t
	 * @return   source states of transitions in t 
	 */
	static Set<CAState> getSources(Set<FMCATransition> t)
	{
		return t.parallelStream()
				.map(FMCATransition::getSource)
				.collect(Collectors.toSet());
	}



	/**
	 * used by choreography synthesis
	 * @return true if violates the branching condition
	 */
	boolean satisfiesBranchingCondition(Set<FMCATransition> trans, Set<CAState> bad) 
	{
		if (!this.isMatch()||bad.contains(this.getSource()) || bad.contains(this.getTarget()))
			return false;		//ignore this transition because it is going to be pruned in the synthesis

		final Set<FMCATransition> ftr= trans.parallelStream()
				.filter(x->x.isMatch()&&!bad.contains(x.getSource())&&!bad.contains(x.getTarget()))
				.collect(Collectors.toSet()); //only valid candidates

		return ftr.parallelStream()
				.map(x->x.getSource().getState())
				.filter(x->(!Arrays.equals(this.getSource().getState(),x))&&
						this.getSource().getState()[this.getSender()]==x[this.getSender()]) 
				//it's not the same state of this but sender is in the same state of this
				.allMatch(s -> ftr.parallelStream()
						.filter(x->Arrays.equals(x.getSource().getState(),s)
								&&Arrays.equals(x.getLabel(), this.getLabel()))
						.count()>0  //for all such states there exists an outgoing transition with the same label of this
						);
	}

	/**
	 * This method is different from the corresponding one in CATransition class because it deals with modal actions. 
	 * Moreover insert is changed.
	 * 
	 * 
	 * @param t				first transition to move
	 * @param tt			second transition to move only in case of match
	 * @param firstprinci  the index to start to copy the principals in t
	 * @param firstprincii the index to start to copy the principals in tt
	 * @param insert		the states of all other principals who stays idle
	 * @param aut		array of principals automata, used here to retrieve the states of idle principals using insert as pointer  
	 * 					 the field CAState[] states must be instantiated --> not modified
	 * @return				a new transition where only principals in t (and tt) moves while the other stays idle in their state given in insert[]
	 */
	public FMCATransition generateATransition(CATransition t, CATransition tt, int firstprinci, int firstprincii,int[] insert,FMCA[] aut)
	{

		if (tt!=null) //if it is a match
		{
			int[] s=((FMCATransition) t).getSource().getState();
			String[] l=((FMCATransition) t).getLabel();
			int[] d=((FMCATransition) t).getTarget().getState();
			int[] ss = ((FMCATransition) tt).getSource().getState();
			String[] ll=((FMCATransition) tt).getLabel();
			int[] dd =((FMCATransition) tt).getTarget().getState();
			int[] source = new int[insert.length+s.length+ss.length];
			int[] target = new int[insert.length+s.length+ss.length];
			String[] label = new String[insert.length+s.length+ss.length];
			action type;
			/*	
		    changed in case offers are necessary instead of requests


		    if (((FMCATransition) t).isRequest())
				type=((FMCATransition) t).getType();
			else
				type=((FMCATransition) tt).getType();
			 */		
			if (((FMCATransition) t).getType()== action.PERMITTED)
				type=((FMCATransition) tt).getType();
			else
				type=((FMCATransition) t).getType(); //TODO here I assume that it is not the case that both offers and requests are necessary!

			int counter=0;
			for (int i=0;i<insert.length;i++)
			{
				if (i==firstprinci)
				{
					for (int j=0;j<s.length;j++)
					{
						source[i+j]=s[j];
						label[i+j]=l[j];
						target[i+j]=d[j];
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
							source[i+counter+j]=ss[j];
							label[i+counter+j]=ll[j];
							target[i+counter+j]=dd[j];
						}
						counter+=ss.length;//record the shift due to the second CA 
						i--;
						firstprincii=-1;
					}	
					else 
					{
						source[i+counter]=  ((FMCA)aut[i+counter]).getStates().toArray(new CAState[] {})[insert[i]].getState()[0];//insert[i]; //TODO modify here! this should have been fixed
						target[i+counter]=  ((FMCA)aut[i+counter]).getStates().toArray(new CAState[] {})[insert[i]].getState()[0];//insert[i];	//TODO modify here! this should have been fixed
						label[i+counter]=CATransition.idle;
					}
				}
			}
			if (firstprinci==insert.length)//case limit, the first CA was the last of aut
			{
				for (int j=0;j<s.length;j++)
				{
					source[insert.length+j]=s[j];
					label[insert.length+j]=l[j];
					target[insert.length+j]=d[j];
				}
				counter+=s.length; //record the shift due to the first CA 
			}
			if (firstprincii==insert.length) //case limit, the second CA was the last of aut
			{
				for (int j=0;j<ss.length;j++)
				{
					source[insert.length+counter+j]=ss[j];
					label[insert.length+counter+j]=ll[j];
					if (ll[j]==null)
					{
						System.out.println("vai");
					}
					target[insert.length+counter+j]=dd[j];
				}
			}
			return new FMCATransition(new CAState(source),label,new CAState(target),type);	
		}
		else	//is not a match
		{
			int[] s=((FMCATransition) t).getSource().getState();
			String[] l=((FMCATransition) t).getLabel();
			int[] d=((FMCATransition) t).getTarget().getState();
			int[] source = new int[insert.length+s.length];
			int[] target = new int[insert.length+s.length];
			String[] label = new String[insert.length+s.length];
			int counter=0;
			for (int i=0;i<insert.length;i++)
			{
				if (i==firstprinci)
				{
					for (int j=0;j<s.length;j++)
					{
						source[i+j]=s[j];
						label[i+j]=l[j];
						target[i+j]=d[j];
					}
					counter+=s.length; //record the shift due to the first CA 
					i--;
					firstprinci=-1;
				}
				else
				{
					try{
						source[i+counter]=((FMCA)aut[i+counter]).getStates().toArray(new CAState[] {})[insert[i]].getState()[0]; //insert[i];//TODO modify here! this should have been fixed
						target[i+counter]=((FMCA)aut[i+counter]).getStates().toArray(new CAState[] {})[insert[i]].getState()[0]; //insert[i];//TODO modify here! this should have been fixed
						label[i+counter]=CATransition.idle;
					} catch (NullPointerException e) 
					{
						System.out.println("prova");
					}
				}
			}
			if (firstprinci==insert.length)//case limit, the first CA is the last of aut
			{
				for (int j=0;j<s.length;j++)
				{
					source[insert.length+j]=s[j];
					label[insert.length+j]=l[j];
					target[insert.length+j]=d[j];
				}
				counter+=s.length; //record the shift due to the first CA 
			}
			return new FMCATransition(new CAState(source),label,new CAState(target),((FMCATransition) t).getType());	
		}
	}
}