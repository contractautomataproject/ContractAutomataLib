package contractAutomata;


import java.util.Set;
import java.util.stream.Collectors;



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

	private final Modality mod;

	public MSCATransition(CAState source, CALabel label, CAState target, Modality type)
	{
		super(source,label,target);
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

	@Override
	public String toString()
	{
		if (getSource()!=null&&getLabelAsList()!=null)
			switch (this.mod) 
			{
			case PERMITTED: return "("+getSource().getStateL().toString()+","+getLabelAsList()+","+getTarget().getStateL().toString()+")";
			case URGENT:return "!U("+getSource().getStateL().toString()+","+getLabelAsList()+","+getTarget().getStateL().toString()+")";
			case LAZY:return "!L("+getSource().getStateL().toString()+","+getLabelAsList()+","+getTarget().getStateL().toString()+")";		
			}
		return null;
	}

	/**
	 * 
	 * @return	true if the  greedy/lazy transition request is controllable 
	 */
	private boolean isControllableLazyRequest(Set<? extends MSCATransition> tr, Set<CAState> badStates)
	{
		//TODO the same source state is checked by comparing the label of the basic state, 
		//	   equals comparison should be performed, but equals is problematic on CAState
		if (this.getLabel().isRequest()&&this.isLazy())
		{
			for (MSCATransition t : tr)	
			{
				if ((t.getLabel().isMatch())
						&&(t.isLazy()&&this.isLazy())//the same type (lazy)
						&&(t.getLabel().getRequester().equals(this.getLabel().getRequester()))	//the same principal
						&&(t.getSource().getStateL().get(t.getLabel().getRequester()).getLabel().equals(this.getSource().getStateL().get(this.getLabel().getRequester()).getLabel())) //the same source state					
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
		//TODO the same source state is checked by comparing the label of the basic state, 
		//	   equals comparison should be performed, but equals is problematic on CAState

		if ((this.getLabel().isOffer()&&this.isLazy()))
		{
			for (MSCATransition t : tr)	
			{
				if ((t.getLabel().isMatch())
						&&(t.isLazy()&&this.isLazy())//the same type (lazy)
						&&(t.getLabel().getOfferer().equals(this.getLabel().getOfferer()))	//the same principal
						&&(t.getSource().getStateL().get(t.getLabel().getOfferer()).getLabel().equals(this.getSource().getStateL().get(this.getLabel().getOfferer()).getLabel())) //the same source state					
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
		//target.getState()[offerer]=source.getState()[offerer];  

		target.getStateL().set(offerer, source.getStateL().get(offerer));   //the offerer is now idle

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
		target.getStateL().set(requester, source.getStateL().get(requester));
		//	target.getState()[requester]=source.getState()[requester];  
		return new MSCATransition(source,
				new CALabel(label.getRank(),label.getOfferer(),label.getAction()),
				target,this.mod); 
		//returning the offer transition
	}

	/**
	 * 
	 * @return	true if the transition is uncontrollable for an orchestration
	 */
	public boolean isUncontrollableOrchestration(Set<? extends MSCATransition> tr, Set<CAState> badStates)
	{
		return this.isUrgent()
				||!this.isControllableLazyRequest(tr,badStates)
				||this.getLabel().isMatch()&&this.isLazy()&&!tr.contains(this)&& 
				!this.extractRequestFromMatch().isControllableLazyRequest(tr,badStates);
	}

	/**
	 * 
	 * @param aut
	 * @return	true if the transition is uncontrollable for a choreography
	 */
	public boolean  isUncontrollableChoreography(Set<? extends MSCATransition> tr, Set<CAState> badStates)
	{
		return !this.isControllableLazyOffer(tr, badStates)
				||this.getLabel().isMatch()&&this.isLazy()&&!tr.contains(this)&& 
				!this.extractOfferFromMatch().isControllableLazyOffer(tr,badStates);
	}

	/**
	 *
	 * @param t	set of transitions
	 * @return   source states of transitions in t 
	 */
	static Set<CAState> getSources(Set<? extends MSCATransition> t)
	{
		return t.parallelStream()
				.map(MSCATransition::getSource)
				.collect(Collectors.toSet());
	}

	/**
	 * 
	 * this method is used by the choreography synthesis 
	 * @param trans the set of transitions to check
	 * @param bad  the set of bad (dangling) states
	 * @return true if the set of transitions and bad states violate the branching condition
	 */
	public boolean satisfiesBranchingCondition(Set<MSCATransition> trans, Set<CAState> bad) 
	{
		if (!this.getLabel().isMatch()||bad.contains(this.getSource()) || bad.contains(this.getTarget()))
			return false;		//ignore this transition because it is going to be pruned in the synthesis

		final Set<MSCATransition> ftr = trans.parallelStream()
				.filter(x->x.getLabel().isMatch()&&!bad.contains(x.getSource())&&!bad.contains(x.getTarget()))
				.collect(Collectors.toSet()); //only valid candidates

//				return ftr.parallelStream()
//				.map(x->x.getSource().getState())
//				.filter(x->(!Arrays.equals(this.getSource().getState(),x))&&
//						this.getSource().getState()[this.getLabel().getOfferer()]==x[this.getLabel().getOfferer()]) 
//				//it's not the same state of this but sender is in the same state of this
//				.allMatch(s -> ftr.parallelStream()
//						.filter(x->Arrays.equals(x.getSource().getState(),s)
//								&& this.getLabel().equals(x.getLabel()))
//								//Arrays.equals(x.getLabelAsStringArr(), this.getLabelAsStringArr()))
//						.count()>0  //for all such states there exists an outgoing transition with the same label of this
//						);
		 		
		return ftr.parallelStream()
				.map(x->x.getSource())
				.filter(x->!x.hasSameBasicStateLabelsOf(this.getSource())&&
						this.getSource().getStateL().get(this.getLabel().getOfferer()).getLabel()
						.equals(x.getStateL().get(this.getLabel().getOfferer()).getLabel()))
				//TODO BasicState objects are not equal because of constructor
				// 		CAState(int[] state, boolean initial, boolean finalstate)
				//it's not the same state of this but sender is in the same state of this
				.allMatch(s -> ftr.parallelStream()
						.filter(x->x.getSource().hasSameBasicStateLabelsOf(s)
								&& this.getLabel().equals(x.getLabel()))
						.count()>0  //for all such states there exists an outgoing transition with the same label of this
						);
		
	}
}