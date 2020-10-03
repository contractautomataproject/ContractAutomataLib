package MSCA;
import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.groupingByConcurrent;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import CA.CAState;
import CA.CATransition;


/** 
 * Class implementing a Modal Service Contract Automaton and its functionalities
 * The class is under construction, some functionalities are not yet updated
 * 
 * 
 * @author Davide Basile
 *
 */
public class MSCA
{ 
	private int rank;
	
	private int[][] finalstates; //these are the final states of the principal in the contract automaton
	//TODO there is loss of information in mxgraph XML and projection
	//this is the only information of the CAState we need
	//this cannot be retrieved from CAState[] states because of conjunction and the usage of int[] for 
	//composed state
	

	private Set<? extends MSCATransition> tra;
	private Set<CAState> states; //all the states of the automaton
	

	public MSCA(int rank, CAState initial,  int[][] finalstates, Set<? extends MSCATransition> tr, Set<CAState> states)
	{
		this.rank=rank;
		setTransition(tr);
		setStates(states);
		setInitialCA(initial);
		setFinalStatesofPrincipals(finalstates);
	}


	public void setTransition(Set<? extends MSCATransition> tr)
	{
		this.tra=tr;
	}


	public  Set<? extends MSCATransition> getTransition()
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
						.allMatch(i -> MSCAUtils.contains(x.getState()[i], 
								IntStream.of(finalstates[i]).boxed().toArray())))))
				.collect(Collectors.toSet()));
	}

	/**
	 * 
	 * @return	the array of number of states of each principal
	 */
	public int[] getNumStatesPrinc()
	{	
		return IntStream.range(0, rank)
				.map(i -> (int)this.getStates().parallelStream()
						.map(x-> x.getState()[i])
						.distinct()
						.count()
						)
				.toArray();
	}

	public CAState getInitial()
	{
		return this.getStates().parallelStream()
				.filter(CAState::isInitial)
				.findFirst().orElseThrow(NullPointerException::new);
	}

	/**
	 * set the initial state in this.getState
	 * @param initial the state to be set
	 */
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

	/**
	 * @return the synthesised orchestration/mpc in agreement
	 */
	public MSCA orchestration()
	{
		return synthesis(x-> {return (t,bad) -> bad.contains(x.getTarget())|| x.isRequest();}, 
				x -> {return (t,bad) -> bad.contains(x.getTarget())&&x.isUncontrollableOrchestration(t, bad);});
	}

	/** 
	 * @return the synthesised choreography in strong agreement, removing only one transition violating the branching condition 
	 * each time no further updates are possible. The transition to remove is chosen nondeterministically with findAny().
	 * 
	 */
	public MSCA choreographyLarger()
	{
		MSCA aut = this;
		MSCATransition toRemove=null;
		do 
		{ aut = aut.synthesis(x-> {return (t,bad) -> 
		!x.isMatch()||bad.contains(x.getTarget());},
				x -> {return (t,bad) -> bad.contains(x.getTarget())&&x.isUncontrollableChoreography(t, bad);});;
		final Set<? extends MSCATransition> trf = aut.getTransition();
		toRemove=(aut.getTransition().parallelStream()
				.filter(x->!x.satisfiesBranchingCondition(trf, new HashSet<CAState>()))
				.findAny() 
				.orElse(null));
		} while (aut.getTransition().remove(toRemove));
		return aut;
	}

	/**
	 * this is the synthesis algorithm
	 * @param pruningPred
	 * @param forbiddenPred
	 * @return
	 */
	protected MSCA synthesis(Function<MSCATransition, BiPredicate<Set<MSCATransition>, Set<CAState>>> pruningPred, 
			Function<MSCATransition, BiPredicate<Set<MSCATransition>, Set<CAState>>> forbiddenPred) 
	{
		Set<MSCATransition> trbackup = new HashSet<MSCATransition>(this.getTransition());
		Set<CAState> R = new HashSet<CAState>(this.getDanglingStates());//R0
		boolean update=false;
		do{
			final Set<CAState> Rf = new HashSet<CAState>(R); 
			final Set<MSCATransition> trf= new HashSet<MSCATransition>(this.getTransition());

			if (this.getTransition().removeAll(this.getTransition().parallelStream()
					.filter(x->pruningPred.apply(x).test(trf, Rf))
					.collect(Collectors.toSet()))) //Ki
				R.addAll(this.getDanglingStates());

			R.addAll(trbackup.parallelStream() 
					.filter(x->forbiddenPred.apply(x).test(trbackup, Rf))
					.map(MSCATransition::getSource)
					.collect(Collectors.toSet())); //Ri

			update=Rf.size()!=R.size()|| trf.size()!=this.getTransition().size();
		} while(update);

		this.removeDanglingTransitions();

		if (R.contains(this.getInitial()))
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

	public int getNumStates()
	{
		if (states==null)
			throw new NullPointerException("The array of number of states of principals has not been initialised");
		return this.states.size();
	}

	/**
	 * @return all actions present in the automaton
	 */
	public Set<String> getActions()
	{
		return this.getTransition().parallelStream()
				.map(x->CATransition.getUnsignedAction(x.getAction()))
				.collect(Collectors.toSet());
	}


	/**
	 * @return a message on the expressiveness of lazy transitions
	 */
	public String infoExpressivenessLazyTransitions()
	{
		long l=this.getTransition()
				.parallelStream()
				.filter(MSCATransition::isLazy)
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

	private void setReachableAndSuccessfulStates()
	{
		//all states' flags are reset
		this.getStates().forEach(s->{s.setReachable(false);	
		s.setSuccessful(false);});

		forwardVisit(this.getInitial()); 

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


	public Set<MSCATransition> getForwardStar(CAState source)
	{
		return this.getTransition().parallelStream()
				.filter(x->x.getSource().equals(source))
				.collect(Collectors.toSet());
	}

	private Set<MSCATransition> getBackwardStar(CAState target)
	{
		return this.getTransition().parallelStream()
				.filter(x->x.getTarget().equals(target))
				.collect(Collectors.toSet());
	}

	
	/**
	 * This is the most important method of the tool, computing the composition.
	 * 
	 * @param aut  the automata to compose
	 * @param pruningPred  the invariant on transitions
	 * @param bound  the bound on the depth of the visit
	 * @return  the composed automaton
	 */
	public static MSCA composition(List<MSCA> aut, Predicate<MSCATransition> pruningPred, Integer bound)
	{
		final class MSCATransitionIndex {//more readable than Entry
			MSCATransition tra;
			Integer ind;
			public MSCATransitionIndex(MSCATransition tr, Integer i) {
				this.tra=tr; //different principals may have equals transitions
				this.ind=i;
			}
		}
		
		List<CAState> initial = aut.stream()  
				.flatMap(a -> a.getStates().stream())
				.filter(CAState::isInitial)
				.collect(Collectors.toList());
		CAState initialstate = new CAState(initial);
		
		Queue<Entry<List<CAState>,Integer>> toVisit = new ConcurrentLinkedQueue<Entry<List<CAState>,Integer>>(List.of(Map.entry(initial,0)));
		ConcurrentMap<List<CAState>, CAState> operandstat2compstat = new ConcurrentHashMap<List<CAState>, CAState>(Map.of(initial, initialstate));//used to avoid duplicate target states 
		Set<MSCATransition> tr = new HashSet<MSCATransition>();//transitions of the composed automaton to build
		Set<List<CAState>> visited = new HashSet<List<CAState>>();
		Queue<CAState> dontvisit = new ConcurrentLinkedQueue<CAState>();
		
		do {
			Entry<List<CAState>,Integer> sourceEntry=toVisit.remove(); //pop state to visit
			if (visited.add(sourceEntry.getKey())&&sourceEntry.getValue()<bound) //if states has not been visited so far
			{
				List<CAState> source =sourceEntry.getKey();
				CAState sourcestate= operandstat2compstat.get(source);
				if (dontvisit.remove(sourcestate))
					continue;//was target of a semicontrollable bad transition
				
				List<MSCATransitionIndex> trans2index = IntStream.range(0,aut.size())
						.mapToObj(i->aut.get(i)
								.getForwardStar(source.get(i))
								.parallelStream()
								.map(t->new MSCATransitionIndex(t,i)))
						.flatMap(Function.identity())
						.collect(toList()); //indexing outgoing transitions of each operand, used for target states and labels

				//firstly match transitions are generated
				Map<MSCATransition, List<Entry<MSCATransition,List<CAState>>>> matchtransitions=
						trans2index.parallelStream()
						.collect(flatMapping(e -> trans2index.parallelStream()
								.filter(ee->CATransition.match(e.tra.getLabel(), ee.tra.getLabel()))
								.map(ee->{ 
									if (e.ind<ee.ind)	{
										List<CAState> targetlist =  new ArrayList<CAState>(source);
										targetlist.set(e.ind, e.tra.getTarget());
										targetlist.set(ee.ind, ee.tra.getTarget());
										MSCATransition tradd=new MSCATransition(sourcestate,e.ind,e.tra.getFirstAction(),ee.ind,
												operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)), 
												e.tra.isNecessary()?e.tra.getType():ee.tra.getType());
										return (Entry<MSCATransition, Entry<MSCATransition,List<CAState>>>) 
														Map.entry(e.tra, Map.entry(tradd,targetlist));//match 
									}//targetlist is used for toVisit
									else
										return (Entry<MSCATransition, Entry<MSCATransition,List<CAState>>>)//dummy, ee.tra is matched
												Map.entry(ee.tra, Map.entry(new MSCATransition(null,null,null,null,null,null),source));
								}), 
								groupingByConcurrent(Entry::getKey, 
										mapping(Entry::getValue,toList()))//each principal transition can have more matches
								));
				
				//collecting match transitions and adding unmatched transitions
				Set<Entry<MSCATransition,List<CAState>>> trmap=
						trans2index.parallelStream()
						.filter(e->!matchtransitions.containsKey(e.tra))//transitions not matched
						.collect(Collectors.collectingAndThen(
								mapping(e->{List<CAState> targetlist = new ArrayList<CAState>(source);
								targetlist.set(e.ind, e.tra.getTarget());
								return Map.entry(new MSCATransition(sourcestate, e.ind, e.tra.getFirstAction(),null,
										operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)),
										e.tra.getType()),targetlist);},
										toSet()),
								trm->{trm.addAll(matchtransitions.values().parallelStream()//matched transitions
										.flatMap(List::parallelStream)
										.filter(e->(e.getKey().getSource()!=null)) //no duplicates
										.collect(toSet()));
								return trm;}));

				if (trmap.parallelStream()//don't visit target states if they are bad
						.anyMatch(x->pruningPred!=null&&pruningPred.test(x.getKey())&&x.getKey().isUrgent()))
					continue;
				else {//adding transitions, updating states
					tr.addAll(trmap.parallelStream()
					.filter(x->pruningPred==null||x.getKey().isNecessary()||pruningPred.negate().test(x.getKey()))//semicontrollable are not pruned
					.collect(Collectors.teeing(
							mapping((Entry<MSCATransition, List<CAState>> e)-> e.getKey(),toSet()), 
							mapping((Entry<MSCATransition, List<CAState>> e)-> e.getValue(),toSet()), 
							(trans,toVis)->{
								toVisit.addAll(toVis.parallelStream()
										.map(s->Map.entry(s,sourceEntry.getValue()+1))
										.collect(toSet()));
								if (pruningPred!=null)//avoid visiting targets of semicontrollable bad transitions
									dontvisit.addAll(trans.parallelStream()
												.filter(x->x.isSemiControllable()&&pruningPred.test(x))
												.map(MSCATransition::getTarget)
												.collect(toList()));
								return trans;
							})));
				}
			}
		} while (!toVisit.isEmpty());

		int rank=aut.stream()
				.map(MSCA::getRank)
				.collect(Collectors.summingInt(Integer::intValue));
		int[][] finalstates = new int[rank][];
		int pointer=0;
		for (MSCA a : aut){
			System.arraycopy(a.getFinalStatesofPrincipals(), 0, finalstates, pointer,a.getRank());
			pointer+=a.getRank();
		}
		Set<CAState> states =visited.parallelStream()
				.map(l->operandstat2compstat.get(l))
				.collect(Collectors.toSet()) ;
		return new MSCA(rank, initialstate, finalstates, tr, states);
	}


	/**
	 * compute the projection on the i-th principal
	 * @param indexprincipal		index of the FMCA
	 * @return		the ith principal
	 * 
	 * @deprecated the XML representation must be fixed to store final states of principals for projection to work
	 */
//	MSCA proj(int indexprincipal)
//	{
//		if ((indexprincipal<0)||(indexprincipal>this.getRank())) //TODO check if the parameter i is in the rank of the FMCA
//			return null;
//		if (this.getRank()==1)
//			return this;
//		MSCATransition[] tra = this.getTransition().toArray(new MSCATransition[] {});
//		//int[] numberofstatesprincipal= new int[1];
//		//numberofstatesprincipal[0]= this.getNumStatesPrinc()[indexprincipal];
//		MSCATransition[] transitionsprincipal = new MSCATransition[tra.length];
//		int pointer=0;
//		for (int ind=0;ind<tra.length;ind++)
//		{
//			MSCATransition tt= ((MSCATransition)tra[ind]);
//			String label = tt.getLabel()[indexprincipal];
//			if(label!=CATransition.idle)
//			{
//				int source =  tt.getSource().getState()[indexprincipal];
//				int dest = tt.getTarget().getState()[indexprincipal];
//				int[] sou = new int[1];
//				sou[0]=source;
//				int[] des = new int[1];
//				des[0]=dest;
//				String[] lab = new String[1];
//				lab[0]=label;
//				MSCATransition selected = null;
//				if (label.substring(0,1).equals(CATransition.offer))
//				{
//					selected = new MSCATransition(new CAState(sou),lab, new CAState(des),MSCATransition.action.PERMITTED);
//				}
//				else {
//					selected = new MSCATransition(new CAState(sou),lab, new CAState(des),tt.getType());
//				}
//
//				if (!CAUtil.contains(selected, transitionsprincipal, pointer))
//				{
//					transitionsprincipal[pointer]=selected;
//					pointer++;
//				}
//			}
//		}
//
//		transitionsprincipal = CAUtil.removeTailsNull(transitionsprincipal, pointer, new MSCATransition[] {});
//		Set<MSCATransition> transitionprincipalset = new HashSet<MSCATransition>(Arrays.asList(transitionsprincipal));
//		Set<CAState> fstates = CAState.extractCAStatesFromTransitions(transitionprincipalset);
//		int[] init=new int[1]; init[0]=0;
//		CAState initialstateprincipal = CAState.getCAStateWithValue(init, fstates);
//		initialstateprincipal.setInitial(true);  //if is dangling will throw exception
//		int[][] finalstatesprincipal = new int[1][];
//		finalstatesprincipal[0]=this.getFinalStatesofPrincipals()[indexprincipal];
//		for (int ind=0;ind<finalstatesprincipal[0].length;ind++)
//		{
//			int[] value=new int[1]; value[0]=finalstatesprincipal[0][ind];
//			CAState.getCAStateWithValue(value, fstates).setFinalstate(true); //if is dangling will throw exception
//		}
//
//		return new MSCA(1,initialstateprincipal,
//				finalstatesprincipal,transitionprincipalset,fstates); 
//	}

	
}


// END OF THE CLASS



//	/**
//	 * @return the synthesised choreography in strong agreement, 
//	 * removing at each iteration all transitions violating branching condition.
//	 */
//	public FMCA choreographySmaller()
//	{
//		return synthesis(x-> {return (t,bad) -> 
//					!x.isMatch()||bad.contains(x.getTarget())||!x.satisfiesBranchingCondition(t, bad);},
//				x -> {return (t,bad) -> bad.contains(x.getTarget())&&x.isUncontrollableChoreography(t, bad);});
//	}
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
//**
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

