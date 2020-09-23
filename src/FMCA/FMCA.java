package FMCA;
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
 * Class implementing a Featured Modal Service Contract Automaton and its functionalities
 * The class is under construction, some functionalities are not yet updated
 * 
 * 
 * @author Davide Basile
 *
 */
public class FMCA  //TODO bring back MSCA class and remove Family from it
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
						.allMatch(i -> FMCAUtil.contains(x.getState()[i], 
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
				.map(i -> (int)this.getStates().parallelStream()
						.map(x-> x.getState()[i])
						.distinct()
						.count()
						)
				.toArray();
	}

	public CAState getInitialCA()
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

	public void setFamily(Family f)
	{
		this.family=f;
	}

	public Family getFamily()
	{
		return family;
	}

	/**
	 * @return the synthesised orchestration/mpc of product p in agreement
	 */
	public FMCA orchestration(Product p)
	{
		FMCA a = synthesis(x-> {return (t,bad) -> 
		x.isRequest()||x.isForbidden(p)||bad.contains(x.getTarget());}, 
				x -> {return (t,bad) -> bad.contains(x.getTarget())&&x.isUncontrollableOrchestration(t, bad);});

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
				x -> {return (t,bad) -> bad.contains(x.getTarget())&&x.isUncontrollableOrchestration(t, bad);});
	}

	/** 
	 * @return the synthesised choreography in strong agreement, removing only one transition violating the branching condition 
	 * each time no further updates are possible. The transition to remove is chosen nondeterministically with findAny().
	 * 
	 */
	public FMCA choreographyLarger()
	{
		FMCA aut = this;
		FMCATransition toRemove=null;
		do 
		{ aut = aut.synthesis(x-> {return (t,bad) -> 
		!x.isMatch()||bad.contains(x.getTarget());},
				x -> {return (t,bad) -> bad.contains(x.getTarget())&&x.isUncontrollableChoreography(t, bad);});;
		final Set<FMCATransition> trf = aut.getTransition();
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
					.filter(x->forbiddenPred.apply(x).test(trbackup, Rf))
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

	public int getNumStates()
	{
		if (states==null)
			throw new NullPointerException("The array of number of states of principals has not been initialised");
		return this.states.size();
	}

	/**
	 * @return all actions present in the automaton
	 */
	Set<String> getActions()
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

	
	/**
	 * This is the most important method of the tool, computing the composition.
	 * 
	 * @param aut  the automata to compose
	 * @param pruningPred  the invariant on transitions
	 * @param bound  the bound on the depth of the visit
	 * @return  the composed automaton
	 */
	public static FMCA composition(List<FMCA> aut, Predicate<FMCATransition> pruningPred, Integer bound)
	{
		final class FMCATransitionIndex {//more readable than Entry
			FMCATransition tra;
			Integer ind;
			public FMCATransitionIndex(FMCATransition tr, Integer i) {
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
		Set<FMCATransition> tr = new HashSet<FMCATransition>();//transitions of the composed automaton to build
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
				
				List<FMCATransitionIndex> trans2index = IntStream.range(0,aut.size())
						.mapToObj(i->aut.get(i)
								.getForwardStar(source.get(i))
								.parallelStream()
								.map(t->new FMCATransitionIndex(t,i)))
						.flatMap(Function.identity())
						.collect(toList()); //indexing outgoing transitions of each operand, used for target states and labels

				//firstly match transitions are generated
				Map<FMCATransition, List<Entry<FMCATransition,List<CAState>>>> matchtransitions=
						trans2index.parallelStream()
						.collect(flatMapping(e -> trans2index.parallelStream()
								.filter(ee->CATransition.match(e.tra.getLabel(), ee.tra.getLabel()))
								.map(ee->{ 
									if (e.ind<ee.ind)	{
										List<CAState> targetlist =  new ArrayList<CAState>(source);
										targetlist.set(e.ind, e.tra.getTarget());
										targetlist.set(ee.ind, ee.tra.getTarget());
										FMCATransition tradd=new FMCATransition(sourcestate,e.ind,e.tra.getFirstAction(),ee.ind,
												operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)), 
												e.tra.isNecessary()?e.tra.getType():ee.tra.getType());
										return (Entry<FMCATransition, Entry<FMCATransition,List<CAState>>>) 
														Map.entry(e.tra, Map.entry(tradd,targetlist));//match 
									}//TODO check if targetlist is still needed in the entry
									else
										return (Entry<FMCATransition, Entry<FMCATransition,List<CAState>>>)//dummy, ee.tra is matched
												Map.entry(ee.tra, Map.entry(new FMCATransition(null,null,null,null,null,null),source));
								}), 
								groupingByConcurrent(Entry::getKey, 
										mapping(Entry::getValue,toList()))//each principal transition can have more matches
								));
				
				//collecting match transitions and adding unmatched transitions
				Set<Entry<FMCATransition,List<CAState>>> trmap=
						trans2index.parallelStream()
						.filter(e->!matchtransitions.containsKey(e.tra))//transitions not matched
						.collect(Collectors.collectingAndThen(
								mapping(e->{List<CAState> targetlist = new ArrayList<CAState>(source);
								targetlist.set(e.ind, e.tra.getTarget());
								return Map.entry(new FMCATransition(sourcestate, e.ind, e.tra.getFirstAction(),null,
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
							mapping((Entry<FMCATransition, List<CAState>> e)-> e.getKey(),toSet()), 
							mapping((Entry<FMCATransition, List<CAState>> e)-> e.getValue(),toSet()), 
							(trans,toVis)->{
								toVisit.addAll(toVis.parallelStream()
										.map(s->Map.entry(s,sourceEntry.getValue()+1))
										.collect(toSet()));
								if (pruningPred!=null)//avoid visiting targets of semicontrollable bad transitions
									dontvisit.addAll(trans.parallelStream()
												.filter(x->x.isSemiControllable()&&pruningPred.test(x))
												.map(FMCATransition::getTarget)
												.collect(toList()));
								return trans;
							})));
				}
			}
		} while (!toVisit.isEmpty());

		int rank=aut.stream()
				.map(FMCA::getRank)
				.collect(Collectors.summingInt(Integer::intValue));
		int[][] finalstates = new int[rank][];
		int pointer=0;
		for (FMCA a : aut){
			System.arraycopy(a.getFinalStatesofPrincipals(), 0, finalstates, pointer,a.getRank());
			pointer+=a.getRank();
		}
		Set<CAState> states =visited.parallelStream()
				.map(l->operandstat2compstat.get(l))
				.collect(Collectors.toSet()) ;
		return new FMCA(rank, initialstate, finalstates, tr, states);
	}


	/**
	 * compute the projection on the i-th principal
	 * @param indexprincipal		index of the FMCA
	 * @return		the ith principal
	 * 
	 * @deprecated the XML representation must be fixed to store final states of principals for projection to work
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
	 * TODO this method needs to be tested again
	 * 
	 * @param aut
	 * @return compute the union of the FMCA in aut
	 */
	public static FMCA union(FMCA[] aut)
	{
		if (aut.length==0)
			return null;
		int upperbound=100; //TODO upperbound check
		int rank=aut[0].getRank(); //the aut must have all the same rank

		float fur= (float)Arrays.stream(aut)
				.mapToDouble(x ->  x.getStates().parallelStream()
						.mapToDouble(CAState::getX)
						.max()
						.getAsDouble()
						)
				.max()
				.getAsDouble(); //furthestnode

		for (int i=0;i<aut.length;i++)
		{
			int[][] fs=aut[i].getFinalStatesofPrincipals();
			int[][] newfs=new int[fs.length][];
			for (int j=0;j<newfs.length;j++)
			{
				newfs[j]=Arrays.copyOf(fs[j], fs[j].length);
			}
			for (int j=0;j<newfs.length;j++)
			{
				for (int z=0;z<newfs[j].length;z++)
					newfs[j][z]+=upperbound*(i+1);
			}
			aut[i].setFinalStatesofPrincipals(newfs); //TODO I modified this method, not tested
		}
		for (int i=0;i<aut.length;i++)
		{
			if (aut[i].getRank()!=rank)
				return null;

			//renaming states of operands
			//			CAState initial=aut[i].getInitialCA().clone();
			//			for (int z=0;z<initial.getState().length;z++)
			//				initial.getState()[z]=initial.getState()[z]+upperbound*(i+1);
			//			aut[i].setInitialCA(initial); 
			//TODO check I changed the setInitial method, now there is no more initial state instance variable
			Set<FMCATransition> tr=aut[i].getTransition();
			for (FMCATransition t : tr)
			{
				CAState source=t.getSource(); 
				//with the states of the FMCA
				CAState target=t.getTarget();
				for (int z=0;z<source.getState().length;z++)
				{
					source.getState()[z] = source.getState()[z] + upperbound*(i+1);
					target.getState()[z] = target.getState()[z] + upperbound*(i+1);
				}
				//t.setSource(source);
				//t.setTarget(target);
			}

			//repositioning states and renaming
			CAState[] fst=aut[i].getStates().toArray(new CAState[] {});
			CAState[] newfst=new CAState[fst.length];
			for (int j=0;j<fst.length;j++)
			{
				int[] value=Arrays.copyOf(fst[j].getState(),fst[j].getState().length);
				for (int z=0;z<value.length;z++)
					value[z]=value[z] + upperbound*(i+1); //rename state TODO this is already done if not cloned
				newfst[j]=new CAState(value, fst[j].getX()+fur*(i)+25*i, fst[j].getY()+50, //repositioning
						fst[j].isInitial(),fst[j].isFinalstate()); //TODO not clear why I instantiate a new state	
			}
			aut[i].setStates(new HashSet<CAState>(Arrays.asList(newfst)));
		}

		int[] initial = new int[rank]; //special initial state
		String[] label = new String[rank];
		label[0]="!dummy";				
		for (int i=0;i<rank;i++)
		{
			initial[i]=0;
			if (i!=0)
				label[i]="-";
		}
		CAState finitial = new CAState(initial,(float)((aut.length)*fur)/2,0,true,false);
		//dummy transitions to initial states
		FMCATransition[] t=new FMCATransition[aut.length];
		for (int i=0;i<t.length;i++)
		{
			t[i]=new FMCATransition(finitial,label,aut[i].getInitialCA(),FMCATransition.action.PERMITTED); 
		}
		int trlength=t.length;
		FMCATransition[][] tr=new FMCATransition[aut.length][];
		for (int i=0;i<aut.length;i++)
		{
			tr[i]=aut[i].getTransition().toArray(new FMCATransition[] {});
			trlength+=tr[i].length;
		}
		FMCATransition[] uniontr=new FMCATransition[trlength];//union of all transitions
		int count=0;
		for (int i=0;i<t.length;i++)
		{
			uniontr[count]=t[i];
			count++;
		}
		for (int i=0;i<aut.length;i++)
		{
			for (int j=0;j<tr[i].length;j++)
			{
				uniontr[count]=tr[i][j];
				count++;
			}
		}

		int[] states = new int[rank]; //TODO remove
		int[] finalstateslength = new int[rank];
		for (int i=0;i<rank;i++)
		{
			states[i]=0;
			finalstateslength[i]=0; //initialise
		}
		int numoffstate=0; //the overall sum of fmcastates of all operands
		for (int i=0;i<aut.length;i++)
		{
			numoffstate+=aut[i].getStates().size();
			int[][] fs = aut[i].getFinalStatesofPrincipals();
			for (int j=0;j<rank;j++)
			{
				states[j]+= aut[i].getNumStatesPrinc()[j]; //sum of states		
				finalstateslength[j] += fs[j].length; //number of final states of operands
			}
		}

		int[][] finalstates = new int[rank][];
		int[] finalstatescount= new int[rank];
		for (int i=0;i<finalstates.length;i++)
		{
			finalstatescount[i]=0;
			finalstates[i]=new int[finalstateslength[i]];
		}
		for (int i=0;i<aut.length;i++)
		{
			int[][] fs = aut[i].getFinalStatesofPrincipals();
			for (int j=0;j<rank;j++)
			{		
				for (int z=0;z<fs[j].length;z++)
				{
					finalstates[j][finalstatescount[j]]=fs[j][z];//TODO check
					finalstatescount[j]++;
				}
			}
		}

		// copying states of operands
		CAState[] ufst = new CAState[numoffstate+1];
		int countfs=0;
		for (int i=0;i<aut.length;i++)
		{
			CAState[] so = aut[i].getStates().toArray(new CAState[] {});
			for (int j=0;j<so.length;j++)
			{
				ufst[countfs]=so[j];
				countfs++;
			}
		}
		ufst[countfs]=finitial;
		/*int[][] finalstates = new int[rank][];
		for (int i=0;i<rank;i++)
		{
			int[][] fs=aut[i].getFinalStatesCA();

		}*/

		return new FMCA(rank, finitial, //states, 
				finalstates, 
				new HashSet<FMCATransition>(Arrays.asList(uniontr)), 
				new HashSet<CAState>(Arrays.asList(ufst)));
	}

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

