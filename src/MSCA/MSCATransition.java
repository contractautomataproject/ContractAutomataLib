package MSCA;


import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import CA.CALabel;
import CA.CAState;
import CA.CATransition;



/**
 * Transition of a modal service contract automaton
 * 
 * @author Davide Basile
 *
 */
public class MSCATransition extends CATransition {
	
	public enum Modality{
		PERMITTED,URGENT,LAZY
	}
	
	private Modality mod;
	
	public MSCATransition(CAState source, CALabel label, CAState target, Modality type)
	{
		super(source,label,target);
		this.mod=type;
	}

	public MSCATransition(CAState source, boolean offererMinorRequester, String offeraction, CAState target, Modality type)
	{
		super(source,offererMinorRequester,offeraction,target);
		this.mod=type;
	}

	public MSCATransition(CAState source, String action, CAState target, Modality type)
	{
		super(source,action,target);
		this.mod=type;
	}
	
	public boolean isUrgent()
	{
		return (this.mod==Modality.URGENT);
	}

	public boolean isLazy()
	{
		return (this.mod==Modality.LAZY);
	}

	public boolean isNecessary()
	{
		return (this.mod!=Modality.PERMITTED);
	}

	public boolean isPermitted()
	{
		return (this.mod==Modality.PERMITTED);
	}
	
	public boolean isSemiControllable()
	{
		return (this.mod==Modality.LAZY);
	}

	public Modality getModality()
	{
		return this.mod;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((mod == null) ? 0 : mod.hashCode());
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
		MSCATransition other = (MSCATransition) obj;
		if (mod != other.mod)
			return false;
		return true;
	}

	//@Override
	public String toString()
	{
		if (getSource()!=null&&getLabelAsList()!=null)
			switch (this.mod) 
			{
			case PERMITTED: return "("+Arrays.toString(getSource().getState())+","+getLabelAsList()+","+Arrays.toString(getTarget().getState())+")";
			case URGENT:return "!U("+Arrays.toString(getSource().getState())+","+getLabelAsList()+","+Arrays.toString(getTarget().getState())+")";
			case LAZY:return "!L("+Arrays.toString(getSource().getState())+","+getLabelAsList()+","+Arrays.toString(getTarget().getState())+")";		
			}
		return null;
	}

	/**
	 * 
	 * @return	true if the  greedy/lazy transition request is controllable 
	 */
	private boolean isControllableLazyRequest(Set<? extends MSCATransition> tr, Set<CAState> badStates)
	{
		if (this.getLabel().isRequest()&&this.isLazy())
		{
			for (MSCATransition t : tr)	
			{
				if ((t.getLabel().isMatch())
						&&(t.isLazy()&&this.isLazy())//the same type (lazy)
						&&(t.getLabel().getRequester().equals(this.getLabel().getRequester()))	//the same principal
						&&(t.getSource().getState()[t.getLabel().getRequester()]==this.getSource().getState()[this.getLabel().getRequester()]) //the same source state					
						&&(t.getLabel().getCoAction().equals(this.getLabel().getAction())) //the same request
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
	private boolean isControllableLazyOffer(Set<? extends MSCATransition> tr, Set<CAState> badStates)
	{
		if ((this.getLabel().isOffer()&&this.isLazy()))
		{
			for (MSCATransition t : tr)	
			{
				if ((t.getLabel().isMatch())
						&&(t.isLazy()&&this.isLazy())//the same type (lazy)
						&&(t.getLabel().getOfferer().equals(this.getLabel().getOfferer()))	//the same principal
						&&(t.getSource().getState()[t.getLabel().getOfferer()]==this.getSource().getState()[this.getLabel().getOfferer()]) //the same source state					
						&&(t.getLabel().getAction().equals(this.getLabel().getAction())) //the same offer (different from orchestration!)
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
	private MSCATransition extractRequestFromMatch()
	{
		if (!this.getLabel().isMatch())
			throw new RuntimeException("this transition is not a match");
		CAState source= this.getSource(); 
		CAState target= this.getTarget();  
		CALabel label = this.getLabel();
		
		int offerer=this.getLabel().getOfferer();
		target.getState()[offerer]=source.getState()[offerer];  //the offerer is now idle
		return new MSCATransition(source,
				new CALabel(label.getRank(),label.getRequester(),label.getCoAction()),
				target,this.mod); //returning the request transition
	}

	/**
	 * 
	 * @return a new request transition where the sender of the match is idle
	 */
	private MSCATransition extractOfferFromMatch()
	{
		if (!this.getLabel().isMatch())
			throw new RuntimeException("this transition is not a match");
		CAState source= this.getSource();
		CAState target= this.getTarget();
		CALabel label=this.getLabel();
		
		int requester=this.getLabel().getRequester();
		target.getState()[requester]=source.getState()[requester];  
		return new MSCATransition(source,
				new CALabel(label.getRank(),label.getOfferer(),label.getAction()),
				target,this.mod); 
		//returning the offer transition
	}

	/**
	 * 
	 * @return	true if the transition is uncontrollable
	 */
	public boolean isUncontrollableOrchestration(Set<? extends MSCATransition> tr, Set<CAState> badStates)
	{
		return this.isUrgent()
				||!this.isControllableLazyRequest(tr,badStates)
				||this.getLabel().isMatch()&&this.isLazy()&&!tr.contains(this)&& 
				!this.extractRequestFromMatch().isControllableLazyRequest(tr,badStates);
	}

	/**
	 * Readapted for a choreography,
	 * 
	 * @param aut
	 * @return	true if the transition is uncontrollable
	 */
	public boolean  isUncontrollableChoreography(Set<? extends MSCATransition> tr, Set<CAState> badStates)
	{
		return !this.isControllableLazyOffer(tr, badStates)
				||this.getLabel().isMatch()&&this.isLazy()&&!tr.contains(this)&& 
				!this.extractOfferFromMatch().isControllableLazyOffer(tr,badStates);
	}

	/**
	 *
	 * @param t
	 * @return   source states of transitions in t 
	 */
	static Set<CAState> getSources(Set<? extends MSCATransition> t)
	{
		return t.parallelStream()
				.map(MSCATransition::getSource)
				.collect(Collectors.toSet());
	}

	/**
	 * used by choreography synthesis
	 * @return true if violates the branching condition
	 */
	public boolean satisfiesBranchingCondition(Set<? extends MSCATransition> trans, Set<CAState> bad) 
	{
		if (!this.getLabel().isMatch()||bad.contains(this.getSource()) || bad.contains(this.getTarget()))
			return false;		//ignore this transition because it is going to be pruned in the synthesis

		final Set<MSCATransition> ftr = trans.parallelStream()
				.filter(x->x.getLabel().isMatch()&&!bad.contains(x.getSource())&&!bad.contains(x.getTarget()))
				.collect(Collectors.toSet()); //only valid candidates

		return ftr.parallelStream()
				.map(x->x.getSource().getState())
				.filter(x->(!Arrays.equals(this.getSource().getState(),x))&&
						this.getSource().getState()[this.getLabel().getOfferer()]==x[this.getLabel().getOfferer()]) 
				//it's not the same state of this but sender is in the same state of this
				.allMatch(s -> ftr.parallelStream()
						.filter(x->Arrays.equals(x.getSource().getState(),s)
								&& this.getLabel().equals(x.getLabel()))
								//Arrays.equals(x.getLabelAsStringArr(), this.getLabelAsStringArr())) //TODO check
						.count()>0  //for all such states there exists an outgoing transition with the same label of this
						);
	}
}