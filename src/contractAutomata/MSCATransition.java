package contractAutomata;


import java.util.Set;
import java.util.function.BiPredicate;
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
		if (type!=null)
			this.mod=type;
		else		
			throw new RuntimeException("Ill-formed transition");

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
	public String toString()
	{
		if (this.mod==Modality.URGENT)
			return "!U("+getSource().getState().toString()+","+getLabelAsList()+","+getTarget().getState().toString()+")";
		else if (this.mod==Modality.LAZY)	
			return "!L("+getSource().getState().toString()+","+getLabelAsList()+","+getTarget().getState().toString()+")";
		else 
			return "("+getSource().getState().toString()+","+getLabelAsList()+","+getTarget().getState().toString()+")";
	}

	/**
	 * 
	 * @return	true if the transition is uncontrollable for an orchestration
	 */
	public boolean isUncontrollableOrchestration(Set<? extends MSCATransition> tr, Set<CAState> badStates)
	{
		return 	isUncontrollable(tr,badStates, 
				(t,tt) -> (t.getLabel().getRequester().equals(tt.getLabel().getRequester()))//the same requesting principal
				&&(t.getSource().getState().get(t.getLabel().getRequester())
						.equals(tt.getSource().getState().get(tt.getLabel().getRequester())))//in the same local source state					
				&&(tt.getLabel().isRequest()&&t.getLabel().getAction().equals(tt.getLabel().getCoAction())|| 
						tt.getLabel().isMatch()&&t.getLabel().getAction().equals(tt.getLabel().getAction())));//doing the same request
	}

	/**
	 * 
	 * @param aut
	 * @return	true if the transition is uncontrollable for a choreography
	 */
	public boolean  isUncontrollableChoreography(Set<? extends MSCATransition> tr, Set<CAState> badStates)
	{
		return 	isUncontrollable(tr,badStates, 
				(t,tt) -> t.getLabel().getOfferer().equals(tt.getLabel().getOfferer())//the same offerer
				&&t.getLabel().getAction().equals(tt.getLabel().getAction()) //the same offer 
				&&t.getSource().equals(tt.getSource()));//the same global source state
	}

	private boolean isUncontrollable(Set<? extends MSCATransition> tr, Set<CAState> badStates, BiPredicate<MSCATransition,MSCATransition> pred)
	{

		if (this.isUrgent())
			return true;
		if (this.isPermitted()||(this.getLabel().isMatch()&&tr.contains(this)))
			return false;
		return !tr.parallelStream()
				.anyMatch(t->t.getLabel().isMatch()
						&&(t.isLazy()&&this.isLazy())
						&&pred.test(t,this));
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

		return ftr.parallelStream()
				.map(x->x.getSource())
				.filter(x->x!=this.getSource()&& //!x.hasSameBasicStateLabelsOf(this.getSource())&&
						this.getSource().getState().get(this.getLabel().getOfferer()).getLabel()
						.equals(x.getState().get(this.getLabel().getOfferer()).getLabel()))
				//it's not the same state of this but sender is in the same state of this
				

				.allMatch(s -> ftr.parallelStream()
						.filter(x->x.getSource()==s //x.getSource().hasSameBasicStateLabelsOf(s)
								&& this.getLabel().equals(x.getLabel()))
						.count()>0  //for all such states there exists an outgoing transition with the same label of this
						);
	}


	/*	//**
	 *
	 * @param t	set of transitions
	 * @return   source states of transitions in t 
	 *//*
	static Set<CAState> getSources(Set<? extends MSCATransition> t)
	{
		return t.parallelStream()
				.map(MSCATransition::getSource)
				.collect(Collectors.toSet());
	}*/

	/*	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((mod == null) ? 0 : mod.hashCode());
		return result;
	}*/

	//	@Override
	//	public boolean equals(Object obj) {
	//		if (this == obj)
	//			return true;
	//		if (!super.equals(obj))
	//			return false;
	//		if (getClass() != obj.getClass())
	//			return false;
	//		MSCATransition other = (MSCATransition) obj;
	//		if (mod != other.mod)
	//			return false;
	//		return true;
	//	}
}