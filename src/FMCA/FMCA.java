package FMCA;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import CA.CAState;
import CA.CATransition;


/** 
 * Class implementing a Featured Modal Service Contract Automaton and its functionalities
 * The class is under construction, some functionalities are not yet updated
 * 
 * 
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class FMCA  implements java.io.Serializable
{
	private int rank;
	private int[][] finalstates; //these are the final states of the principal in the contract automaton
								 //TODO there is loss of information in mxgraph XML and projection
								 //this is the only information of the CAState we need
								 //this cannot be retrieved from CAState[] states because of conjunction and the usage of int[] for 
								 //composed state
	
	private Set<FMCATransition> tra;
	private Set<CAState> states; //all the states of the automaton
	private Family family; 
	
	//TODO: add generation of products from a feature constraint, 
	//       the family now contains all valid products, they are generated and can be imported from a feature model in FeatureIDE


	public FMCA(int rank, CAState initial,  int[][] finalstates, Set<FMCATransition> tr, Set<CAState> states)
	{
		this.rank=rank;
		setTransition(tr);
		setStates(states);
		setInitialCA(initial);
		setFinalStatesofPrincipals(finalstates);
	}
	
	public void setTransition(Set<FMCATransition> tr)
	{
		this.tra=tr;
	}
	
	
	public  Set<FMCATransition> getTransition()
	{
		return tra;
	}
	
	public void setStates(Set<CAState> s)
	{
		this.states=s;
	}

	public Set<CAState> getStates()
	{
		return states;
	}

	public int[][] getFinalStatesofPrincipals()
	{
		return this.finalstates;
		
	}
	
	
	public void setFinalStatesofPrincipals(int[][] finalstates)
	{
		 this.finalstates = finalstates;
		 setFinalStatesCA();
	}
	
	/**
	 * set the final state flag to the states of the CA using the finalstates of each principal
	 * @param fs  the array of final states of each principal
	 */
	private void setFinalStatesCA()
	{
		this.setStates(this.getStates().stream()
		.peek(x -> x.setFinalstate((IntStream.range(0,x.getState().length)
									.allMatch(i -> FMCAUtil.contains(new Integer(x.getState()[i]), 
											IntStream.of(finalstates[i]).boxed().toArray())))))
		.collect(Collectors.toSet()));
	}
	
	/**
	 * 
	 * @return	the array of number of states of each principal
	 */
	int[] getNumStatesPrinc()
	{	
		return IntStream.range(0, rank)
		.map(i -> new Long(this.getStates().parallelStream()
						.map(x-> x.getState()[i])
						.distinct()
						.count()).intValue()
			)
		.toArray();
	}
	
	public CAState getInitialCA()
	{
		return this.getStates().parallelStream()
				.filter(CAState::isInitial)
				.findFirst().orElseThrow(NullPointerException::new);
	}
	
	public void setInitialCA(CAState initial)
	{
		this.getStates().parallelStream()
					.filter(CAState::isInitial)
					.findAny().ifPresent(x->x.setInitial(false));
		
		CAState init = this.getStates().parallelStream()
				.filter(x->Arrays.equals(initial.getState(),x.getState()))
				.findAny().orElseThrow(IllegalArgumentException::new);
		
		init.setInitial(true);
	}
	
	public int getRank()
	{
		return rank;
	}
	
	public void setFamily(Family f)
	{
		this.family=f;
	}

	public Family getFamily()
	{
		return family;
	}

	boolean containAction(String act)
	{
		return FMCAUtil.contains(act, this.getActions());
	}


	/**
	 * compute the projection on the i-th principal
	 * @param indexprincipal		index of the FMCA
	 * @return		the ith principal
	 * 
	 * TODO remove this method, in stack of calls is used by generateATransition
	 */
	FMCA proj(int indexprincipal)
	{
		if ((indexprincipal<0)||(indexprincipal>this.getRank())) //TODO check if the parameter i is in the rank of the FMCA
			return null;
		if (this.getRank()==1)
			return this;
		FMCATransition[] tra = this.getTransition().toArray(new FMCATransition[] {});
		//int[] numberofstatesprincipal= new int[1];
		//numberofstatesprincipal[0]= this.getNumStatesPrinc()[indexprincipal];
		FMCATransition[] transitionsprincipal = new FMCATransition[tra.length];
		int pointer=0;
		for (int ind=0;ind<tra.length;ind++)
		{
			FMCATransition tt= ((FMCATransition)tra[ind]);
			String label = tt.getLabel()[indexprincipal];
			if(label!=CATransition.idle)
			{
				int source =  tt.getSource().getState()[indexprincipal];
				int dest = tt.getTarget().getState()[indexprincipal];
				int[] sou = new int[1];
				sou[0]=source;
				int[] des = new int[1];
				des[0]=dest;
				String[] lab = new String[1];
				lab[0]=label;
				FMCATransition selected = null;
				if (label.substring(0,1).equals(CATransition.offer))
				{
					selected = new FMCATransition(new CAState(sou),lab, new CAState(des),FMCATransition.action.PERMITTED);
				}
				else {
					selected = new FMCATransition(new CAState(sou),lab, new CAState(des),tt.getType());
				}

				if (!FMCAUtil.contains(selected, transitionsprincipal, pointer))
				{
					transitionsprincipal[pointer]=selected;
					pointer++;
				}
			}
		}

		transitionsprincipal = FMCAUtil.removeTailsNull(transitionsprincipal, pointer, new FMCATransition[] {});
		Set<FMCATransition> transitionprincipalset = new HashSet<FMCATransition>(Arrays.asList(transitionsprincipal));
		Set<CAState> fstates = CAState.extractCAStatesFromTransitions(transitionprincipalset);
		int[] init=new int[1]; init[0]=0;
		CAState initialstateprincipal = CAState.getCAStateWithValue(init, fstates);
		initialstateprincipal.setInitial(true);  //if is dangling will throw exception
		int[][] finalstatesprincipal = new int[1][];
		finalstatesprincipal[0]=this.getFinalStatesofPrincipals()[indexprincipal];
		for (int ind=0;ind<finalstatesprincipal[0].length;ind++)
		{
			int[] value=new int[1]; value[0]=finalstatesprincipal[0][ind];
			CAState.getCAStateWithValue(value, fstates).setFinalstate(true); //if is dangling will throw exception
		}

		return new FMCA(1,initialstateprincipal,
				finalstatesprincipal,transitionprincipalset,fstates); 
	}



	/**
	 * @return the synthesised orchestration/mpc of product p in agreement
	 */
	public FMCA orchestration(Product p)
	{
		FMCA a = synthesis(x-> {return (t,bad) -> 
									x.isRequest()||x.isForbidden(p)||bad.contains(x.getTarget());}, 
				x -> x::isUncontrollableOrchestration);
		
		if (a!=null&&p.checkRequired(a.getTransition()))
			return a;
		else
			return null;
	}
	
	/**
	 * @return the synthesised orchestration/mpc in agreement
	 */
	public FMCA orchestration()
	{
		return synthesis(x-> {return (t,bad) -> bad.contains(x.getTarget())|| x.isRequest();}, 
				x -> x::isUncontrollableOrchestration);
	}

	
	/**
	 * @return the synthesised choreography in strong agreement, 
	 * removing at each iteration all transitions violating branching condition
	 */
	public FMCA choreographySmaller()
	{
		return synthesis(x-> {return (t,bad) -> 
					!x.isMatch()||bad.contains(x.getTarget())||!x.satisfiesBranchingCondition(t, bad);},
				x -> x::isUncontrollableChoreography);
	}
	
	/**
	 * @return the synthesised choreography in strong agreement, 
	 * removing only one transition violating the branching condition each time no further updates are possible
	 */
	public FMCA choreographyLarger()
	{
		FMCA aut = this;
		FMCATransition toRemove=null;
		do 
			{ aut = aut.synthesis(x-> {return (t,bad) -> 
					!x.isMatch()||bad.contains(x.getTarget());},
					x -> x::isUncontrollableChoreography);
			  final Set<FMCATransition> trf = aut.getTransition();
			  toRemove=(aut.getTransition().parallelStream()
					  .filter(x->!x.satisfiesBranchingCondition(trf, new HashSet<CAState>()))
					  .findAny()
					  .orElse(null));
			} while (aut.getTransition().remove(toRemove));
		return aut;
	}
	
	
	private FMCA synthesis(Function<FMCATransition, BiPredicate<Set<FMCATransition>, Set<CAState>>> pruningPred, 
						Function<FMCATransition, BiPredicate<Set<FMCATransition>, Set<CAState>>> forbiddenPred) 
	{
		Set<FMCATransition> trbackup = new HashSet<FMCATransition>(this.getTransition());
		Set<CAState> R = new HashSet<CAState>(this.getDanglingStates());//R0
		boolean update=false;
		do{
			final Set<CAState> Rf = new HashSet<CAState>(R); 
			final Set<FMCATransition> trf= new HashSet<FMCATransition>(this.getTransition());
			
			if (this.getTransition().removeAll(this.getTransition().parallelStream()
						.filter(x->pruningPred.apply(x).test(trf, Rf))
						.collect(Collectors.toSet()))) //Ki
				R.addAll(this.getDanglingStates());
			
			R.addAll(trbackup.parallelStream() 
					.filter(x->forbiddenPred.apply(x).test(trbackup, Rf)) //x.isUncontrollable(trbackup, Rf)
								//&&(Rf.contains(x.getTarget()))) //||invariant.test(x)))
					.map(FMCATransition::getSource)
					.collect(Collectors.toSet())); //Ri
	
			update=Rf.size()!=R.size()|| trf.size()!=this.getTransition().size();
		} while(update);

		this.removeDanglingTransitions();
		
		if (R.contains(this.getInitialCA()))
			return null;
		
		this.setStates(this.extractAllStatesFromTransitions());
		return this;
	}

	/**
	 * @return all  states that appear in at least one transition
	 */
	private Set<CAState> extractAllStatesFromTransitions()
	{
		return CAState.extractCAStatesFromTransitions(this.getTransition());
	}

	/**
	 * this method is used when importing from XML, where no description of  principal final states is given 
	 * and it is reconstructed
	 * 
	 * //TODO remove this in the future, when importing from XML there is loss of information anyway
	 * 
	 * @param all final states of the composed automaton
	 * @return the final states of each principal
	 */
	static int[][] principalsFinalStates(int[][] states)
	{
		if (states.length<=0)
			return null;
		int rank=states[0].length;
		int[] count=new int[rank];
		int[][] pfs=new int[rank][states.length];

		for (int ind=0; ind<pfs.length;ind++)
			for (int ind2=0; ind2<pfs[ind].length;ind2++)
				pfs[ind][ind2] = -1;    //the check FMCAUtil.getIndex(pfs[j], states[i][j])==-1  will not work otherwise because 0 is the initialization value but can also be a state

		for (int j=0;j<rank;j++)
		{
			pfs[j][0]=states[0][j];
			count[j]=1;		//initialising count[j] and doing first iteration (I guess..)
		}
		for (int i=1;i<states.length;i++)
		{
			for (int j=0;j<rank;j++)
			{
				if (FMCAUtil.getIndex(pfs[j], states[i][j])==-1 )  // if states[i][j] is not in pfs[j]
				{
					pfs[j][count[j]]=states[i][j];
					count[j]++;
				}
			}
		}
		for (int j=0;j<rank;j++)
			pfs[j]=FMCAUtil.removeTailsNull(pfs[j], count[j]);
		return pfs;
	}

	public int getNumStates()
	{
		if (states==null)
			throw new NullPointerException("The array of number of states of principals has not been initialised");
		return this.states.size();
	}

	/**
	 * @return all actions present in the automaton
	 */
	String[] getActions()
	{
		return this.getTransition().parallelStream()
		.map(x->CATransition.getUnsignedAction(x.getAction()))
		.collect(Collectors.toSet())
		.toArray(new String[] {});
	}

	
	/**
	 * @return a message on the expressiveness of lazy transitions
	 */
	public String infoExpressivenessLazyTransitions()
	{
		long l=this.getTransition()
		.parallelStream()
		.filter(FMCATransition::isLazy)
		.count();

		long ns = this.getNumStates()+1;
		return "The automaton contains the following number of lazy transitions : "+l+" \n"
				+"The resulting automaton with only urgent transitions will have the following number of states ("+ns+") * (2^"+l+"-1)";
	}

	/**
	 * @return	states who do not reach a final state or are unreachable
	 */
	private Set<CAState> getDanglingStates()
	{
		this.setReachableAndSuccessfulStates();
		return this.getStates().parallelStream()
		.filter(x->!(x.isReachable()&&x.isSuccessful()))
		.collect(Collectors.toSet());
	}

	void setReachableAndSuccessfulStates() //TODO set to private if not used by composition
	{
		//all states' flags are reset
		this.getStates().forEach(s->{s.setReachable(false);	
									s.setSuccessful(false);});
		
		forwardVisit(this.getInitialCA()); 
			
		this.getStates().forEach(
				x-> {if (x.isFinalstate()&&x.isReachable())
					this.backwardVisit(x);});
	}

	private void forwardVisit(CAState currentstate)
	{ 
		currentstate.setReachable(true);
		this.getForwardStar(currentstate).forEach(x->{
			if (!x.getTarget().isReachable())
				this.forwardVisit(x.getTarget());
		});
	}

	private void backwardVisit(CAState currentstate)
	{ 
		currentstate.setSuccessful(true);
		this.getBackwardStar(currentstate).forEach(x->{
			if (!x.getSource().isSuccessful())
				this.backwardVisit(x.getSource());
		});
	}

	
	/**
	 * remove the unreachable transitions, needs to compute reachable and successful first
	 */
	void removeDanglingTransitions()
	{
		this.setTransition(this.getTransition().parallelStream()
				.filter(x->x.getSource().isReachable()&&x.getTarget().isSuccessful())
				.collect(Collectors.toSet()));
	}
	
	
	Set<FMCATransition> getForwardStar(CAState source)
	{
		return this.getTransition().parallelStream()
					.filter(x->x.getSource().equals(source))
					.collect(Collectors.toSet());
	}
	
	private Set<FMCATransition> getBackwardStar(CAState target)
	{
		return this.getTransition().parallelStream()
				.filter(x->x.getTarget().equals(target))
				.collect(Collectors.toSet());
	}
}

// END OF THE CLASS

	
//no one is using this one
//public CAState[] getFinalStates()
//{
//	return Arrays.asList(states).parallelStream()
//			.filter(CAState::isFinalstate)
//			.collect(Collectors.toList())
//			.toArray(new CAState[] {});
//}
	
//	/**
//	 * 
//	 * @param state
//	 * @return true if the successful value of state has changed
//	 */
//	private boolean forwardNeighbourVisit(CAState state)
//	{	
//		boolean b = state.isSuccessful();
//		state.setSuccessful(Arrays.stream(this.getTransitionsWithSource(state))
//				.map(FMCATransition::getTarget)
//				.anyMatch(CAState::isSuccessful));
//
//		return b!=state.isSuccessful();
//		
//	}
//	
//	/**
//	 * 
//	 * @param state
//	 * @return true if the reachable value of state has changed
//	 */
//	private boolean backwardNeighbourVisit(CAState state)
//	{	
//		boolean b = state.isReachable();
//		
//		state.setReachable(Arrays.stream(this.getTransitionsWithTarget(state))
//				.map(FMCATransition::getSource)
//				.anyMatch(CAState::isReachable));
//		return b!=state.isReachable();
//		
//	}	
	/**
//	 * 
//	 * @param states  all the states of the CA enumerated
//	 * @return an array containing the number of  states of each principal
//	 */
//	public static int[] numberOfPrincipalsStates(int[][] states)
//	{
//		int[] rank = new int[states[0].length];
//		for (int i=0;i<rank.length;i++)
//		{
//			int[] principalstates=new int[states.length];//upperbound
//			for (int ind=0; ind<principalstates.length;ind++)
//				principalstates[ind] = -1;    //the next loop will not work otherwise because 0 is the initialization value but can also be a state
//
//			int count=0;
//			for (int j=0;j<principalstates.length;j++)
//			{
//				if (!FMCAUtil.contains(states[j][i],principalstates,count))
//				{
//					principalstates[count]=states[j][i];
//					count++;
//				}
//			}
//			rank[i]=count;
//		}
//	
//		return rank;
//	}

//	public FMCA[] allPrincipals()
//	{
//
//		FMCA[] principals = new FMCA[this.getRank()];
//		for (int i=0;i<principals.length;i++)
//		{
//			principals[i] = this.proj(i);
//		}
//		return principals;
//	}

//	/**
//	 * Starting from the final states of each principal, it computes all their combinations to produce 
//	 * the final states of the contract automaton. Note that not all such combinations are reachable.
//	 * This method is only needed when loading a textual (.data) description of the automaton, where only 
//	 * the final states of the principals are identified.
//	 * 
//	 * 
//	 * @return all the final states of the CA
//	 */
//	private  int[][] generateAllCombinationsOfFinalStates()
//	{
//		int[][] finalstates = this.getFinalStatesofPrincipals(); //the final states of each principal
//		int[] states=new int[finalstates.length];
//		int comb=1;
//		int[] insert= new int[states.length];
//		for (int i=0;i<states.length;i++)
//		{
//			states[i]=finalstates[i].length;
//			comb*=states[i];
//			insert[i]=0;
//		}
//		int[][] modif = new int[comb][];
//		int[] indstates = new int[1];
//		indstates[0]= states.length-1;
//		int[] indmod = new int[1];
//		indmod[0]= 0; 
//
//		FMCAUtil.recGen(finalstates, modif,  states, indmod, indstates, insert);  
//		return modif;
//	}
//	/**
//	 * used by synthesis to remember transitions in case they are removed for checking semi-controllability
//	 * @return	copy Transitions
//	 */
//	private  FMCATransition[] copyTransition()
//	{
//		FMCATransition[] at = this.getTransition();
//		FMCATransition[] finalTr = new FMCATransition[at.length];
//		for(int i=0;i<finalTr.length;i++)
//		{
//			CAState in=at[i].getSourceP();
//			String[] l=at[i].getLabelP();
//			CAState out= at[i].getTargetP();
//		
//		
//			
//			//finalTr[i]=at[i]; probably this is enough
//			finalTr[i] = new FMCATransition(in,Arrays.copyOf(l,l.length),out,at[i].getType());
//		}
//		return finalTr;
//	}
//	/**
//	 * this method is not inherited from MSCA
//	 * @return	all the  must transitions request that are not matched 
//	 * 
//	 */
//	private  FMCATransition[] getUnmatch()
//	{
//		FMCATransition[] tr = this.getTransition();
//		int[][] fs=this.allFinalStates();
//		int pointer=0;
//		CAState[] R=this.getDanglingStates();
//		FMCATransition[] unmatch = new FMCATransition[tr.length];
//		for (int i=0;i<tr.length;i++)
//		{
//			if ((tr[i].isRequest())
//					&&((tr[i].isNecessary())
//							&&(!FMCAUtil.contains(tr[i].getSourceP().getState(), fs)))) // if source state is not final
//			{
//				boolean matched=false;
//				for (int j=0;j<tr.length;j++)	
//				{
//					if ((tr[j].isMatch())
//							&&(tr[j].isNecessary())
//							&&(tr[j].getReceiver()==tr[i].getReceiver())	//the same principal
//							&&(tr[j].getSourceP().getState()[tr[j].getReceiver()]==tr[i].getSourceP().getState()[tr[i].getReceiver()]) //the same source state					
//							&&(tr[j].getLabelP()[tr[j].getReceiver()]==tr[i].getLabelP()[tr[i].getReceiver()]) //the same request
//							&&(!FMCAUtil.contains(tr[i].getSourceP(), R))) //source state is not redundant
//					{
//						matched=true; // the request is matched
//					}
//				}
//				if (!matched)
//				{
//					unmatch[pointer]=tr[i];
//					pointer++;
//				}
//			}
//		}
//		if (pointer>0)
//		{
//			unmatch = FMCAUtil.removeTailsNull(unmatch, pointer, new FMCATransition[] {});
//			return unmatch;
//		}
//		else
//			return null;
//	}
//

	//
//	/**
//	 * 
//	 * @return key true contains final states
//	 * @TODO test this method
//	 */
//	public Map<Boolean, List<CAState>> partitionFinalAndNonFinalStates()
//	{
//		
//		Map<Boolean, List<CAState>> map = Arrays.asList(states)
//				.stream()
//				.collect(Collectors.partitioningBy(x -> x.isFinalstate()));
//		
////		CAState[] finalstates= Arrays.asList(states).stream()
////				.filter(x -> x.isFinalstate())
////				.collect(Collectors.toList())
////				.toArray(new CAState[] {});
////				
////				//FMCAUtil.setIntersection(states, this.generateAllCombinationsOfFinalStates());//only reachable final states
////		CAState[] nonfinalstates=FMCAUtil.setDifference(states, finalstates, new CAState[] {});
////
////		r[0]=nonfinalstates;
////		r[1]=finalstates;
//		
////		r[0]=map.get(false).toArray(new CAState[] {});
////		r[1]=map.get(true).toArray(new CAState[] {});
//		return map;
//	}

