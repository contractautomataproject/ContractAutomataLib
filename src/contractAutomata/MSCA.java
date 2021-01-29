package contractAutomata;
//import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.groupingByConcurrent;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/** 
 * Class representing a Modal Service Contract Automaton
 * 
 * @author Davide Basile
 *
 */
public class MSCA
{ 
	/**
	 * the rank of the automaton
	 */
	private final Integer rank;
	
	/**
	 * identifiers of the final states of the principal in the contract automaton
	 */
	private int[][] finalstates;
	//TODO there is loss of information in mxGraph XML and projection
	//this is the only information of the CAState needed but not currently stored in XML

	/**
	 * transitions of the automaton
	 */
	private Set<MSCATransition> tra;
	
	/**
	 * all the states of the automaton
	 */
	//private Set<CAState> states;

	private Map<CAState,Boolean> reachable;
	private Map<CAState,Boolean> successful;

	public MSCA(Integer rank, CAState initial,  int[][] finalstates, Set<MSCATransition> tr)
	{
		if (rank==null || initial == null || finalstates == null || tr == null)
			throw new IllegalArgumentException("Null argument "+rank+" "+initial+" "+finalstates+" "+tr+" ");

		this.rank=rank;
		setTransition(tr);
		//setStates(states);
		//setInitialCA(initial);
		setFinalStatesofPrincipals(finalstates);
		this.checkInitFin();//useful for development
	}


	public void setTransition(Set<MSCATransition> tr)
	{
		if (tr.parallelStream()
				.anyMatch(Objects::isNull))
			throw new IllegalArgumentException("Null element");
		this.tra=tr;
	}


	public  Set<MSCATransition> getTransition()
	{
		return tra;
	}

//	public void setStates(Set<CAState> s)
//	{
//		if (s.parallelStream()
//				.anyMatch(Objects::isNull))
//			throw new IllegalArgumentException("Null element");
//		this.states=s;
//	}

//	public Set<CAState> getStates()
//	{
//		return states;
//	}

	public int[][] getFinalStatesofPrincipals()
	{
		return this.finalstates;

	}

	public void setFinalStatesofPrincipals(int[][] finalstates)
	{
		for (int[] a : finalstates)
			if (a==null)
				throw new IllegalArgumentException("Final states contain a null array element");

		this.finalstates = finalstates;
	}

	/**
	 * @return	an array containing for each principal its number of states
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
     * the only initial state in the set of states is set to be the one equal to argument initial
     * @param initial the state to be set
	 */
	public void setInitialCA(CAState initial)
	{
		
		Set<CAState> states=this.getStates();
		
		states.parallelStream()
		.filter(CAState::isInitial)
		.forEach(x->x.setInitial(false));

		CAState init = states.parallelStream()
				.filter(x->Arrays.equals(initial.getState(),x.getState()))
				.findAny().orElseThrow(IllegalArgumentException::new);

		init.setInitial(true);
	}
	
	public int getRank()
	{
		return rank;
	}

	/**
	 * invokes the synthesis method for synthesising the mpc in agreement
	 * @return the synthesised most permissive controller in agreement
	 */
	public MSCA mpc()
	{
		if (this.getTransition().parallelStream()
				.anyMatch(t-> t.isSemiControllable()))
			throw new UnsupportedOperationException("The automaton contains semi-controllable transitions");

		
		return synthesis((x,t,bad) -> bad.contains(x.getTarget())|| x.getLabel().isRequest(), 
				(x,t,bad) -> bad.contains(x.getTarget()));
	}


	/**
	 * invokes the synthesis method for synthesising the orchestration in agreement
	 * @return the synthesised orchestration in agreement
	 */
	public MSCA orchestration()
	{
		if (this.getTransition().parallelStream()
				.anyMatch(t-> !t.isPermitted()&&t.getLabel().isOffer()))
			throw new UnsupportedOperationException("The automaton contains necessary offers that are not allowed in the orchestration synthesis");

		return synthesis((x,t,bad) -> bad.contains(x.getTarget())|| x.getLabel().isRequest(), 
				(x,t,bad) -> bad.contains(x.getTarget())&&x.isUncontrollableOrchestration(t, bad));
	}

	/** 
	 * invokes the synthesis method for synthesising the choreography in strong agreement
	 * @return the synthesised choreography in strong agreement, removing only one transition violating the branching condition 
	 * each time no further updates are possible. The transition to remove is chosen nondeterministically with findAny().
	 * 
	 */
	public MSCA choreography()
	{

		if (this.getTransition().parallelStream()
				.anyMatch(t-> !t.isPermitted()&&t.getLabel().isRequest()))
			throw new UnsupportedOperationException("The automaton contains necessary requests that are not allowed in the choreography synthesis");
		
		MSCA aut = this;
		MSCATransition toRemove=null;
		do 
		{ aut = aut.synthesis((x,t,bad) -> 
		!x.getLabel().isMatch()||bad.contains(x.getTarget()),
		(x,t,bad) -> bad.contains(x.getTarget())&&x.isUncontrollableChoreography(t, bad));
		if (aut==null)
			break;
		final Set<MSCATransition> trf = aut.getTransition();
		toRemove=(aut.getTransition().parallelStream()
				.filter(x->!x.satisfiesBranchingCondition(trf, new HashSet<CAState>()))
				.findAny() 
				.orElse(null));
		} while (aut.getTransition().remove(toRemove));
		return aut;
	}

	/**
	 * The generic synthesis algorithm
	 * 
	 * @param pruningPred  predicate for pruning transitions
	 * @param forbiddenPred   predicate for forbidden states
	 * @return  the synthesis automaton according to the predicates
	 */
	public MSCA synthesis(TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> pruningPred, 
			TriPredicate<MSCATransition, Set<MSCATransition>, Set<CAState>> forbiddenPred) 
	{
		Set<MSCATransition> trbackup = new HashSet<MSCATransition>(this.getTransition());
		Set<CAState> statesbackup= this.getStates(); 
		CAState init = this.getInitial();
		Set<CAState> R = new HashSet<CAState>(this.getDanglingStates(statesbackup,init));//R0
		boolean update=false;
		do{
			final Set<CAState> Rf = new HashSet<CAState>(R); 
			final Set<MSCATransition> trf= new HashSet<MSCATransition>(this.getTransition());

			if (this.getTransition().removeAll(this.getTransition().parallelStream()
					.filter(x->pruningPred.test(x,trf, Rf))
					.collect(Collectors.toSet()))) //Ki
				R.addAll(this.getDanglingStates(statesbackup,init));

			R.addAll(trbackup.parallelStream() 
					.filter(x->forbiddenPred.test(x,trbackup, Rf))
					.map(MSCATransition::getSource)
					.collect(Collectors.toSet())); //Ri

			update=Rf.size()!=R.size()|| trf.size()!=this.getTransition().size();
		} while(update);

		this.removeDanglingTransitions();

		if (R.contains(init))
			return null;

		//this.setStates(this.extractAllStatesFromTransitions());
		return this;
	}
	

	/**
	 * @return all  states that appear in at least one transition
	 */
	public Set<CAState> getStates()
	{
		Set<CAState> cs= this.getTransition().stream()
				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
				.collect(Collectors.toSet()); //CAState without equals, duplicates objects are detected

		if (cs.stream()
				.anyMatch(x-> cs.stream()
				.filter(y->x!=y && x.getStateL().equals(y.getStateL()))
				.count()>0))
			throw new IllegalArgumentException("Transitions have ambiguous states (different objects for the same state).");

		return cs;

	}

	/**
	 * 
	 * @return the number of states
	 */
	public int getNumStates()
	{
//		if (states==null)
//			throw new NullPointerException("The array of number of states of principals has not been initialised");
		return this.getStates().size();
	}

	/**
	 * @return all actions present in the automaton
	 */
	public Set<String> getUnsignedActions()
	{
		return this.getTransition().parallelStream()
				.map(x->CALabel.getUnsignedAction(x.getLabel().getAction()))
				.collect(Collectors.toSet());
	}


	/**
	 * @return an informative message about the expressiveness of lazy transitions
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
	private Set<CAState> getDanglingStates(Set<CAState> states, CAState initial)
	{
		
		//all states' flags are reset
		this.reachable=states.parallelStream()   //this.getStates().forEach(s->{s.setReachable(false);	s.setSuccessful(false);});
				.collect(Collectors.toMap(x->x, x->false));
		this.successful=states.parallelStream()
				.collect(Collectors.toMap(x->x, x->false));

		//set reachable
		forwardVisit(initial);  

		//set successful
		states.forEach(
				x-> {if (x.isFinalstate()&&this.reachable.get(x))//x.isReachable())
					this.backwardVisit(x);});  
		
		return states.parallelStream()
				.filter(x->!(this.reachable.get(x)&&this.successful.get(x)))  //!(x.isReachable()&&x.isSuccessful()))
				.collect(Collectors.toSet());
	}


	private void forwardVisit(CAState currentstate)
	{ 
		this.reachable.put(currentstate, true);  //currentstate.setReachable(true);
		this.getForwardStar(currentstate).forEach(x->{
			if (!this.reachable.get(x.getTarget()))//!x.getTarget().isReachable())
				this.forwardVisit(x.getTarget());
		});
	}

	private void backwardVisit(CAState currentstate)
	{ 
		this.successful.put(currentstate, true); //currentstate.setSuccessful(true);
		this.getBackwardStar(currentstate).forEach(x->{
			if (!this.successful.get(x.getSource()))//!x.getSource().isSuccessful())
				this.backwardVisit(x.getSource());
		});
	}


	/**
	 * remove the unreachable transitions, needs to compute reachable and successful first
	 */
	void removeDanglingTransitions()
	{
		this.setTransition(this.getTransition().parallelStream()
				.filter(x->this.reachable.get(x.getSource())&&this.successful.get(x.getTarget()))//x.getSource().isReachable()&&x.getTarget().isSuccessful())
				.collect(Collectors.toSet()));
	}

	/**
	 * 
	 * @param source   the source state
	 * @return  the transitions outgoing the source state
	 */
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
	 * This is the most important method of the tool, it computes the non-associative composition of contract automata.
	 * 
	 * @param aut  the list of automata to compose
	 * @param pruningPred  the invariant that all transitions must satisfy
	 * @param bound  the bound on the depth of the visit
	 * @return  the composed automaton
	 */
	public static MSCA composition(List<MSCA> aut, Predicate<MSCATransition> pruningPred, Integer bound)
	{

		//TODO study non-associative composition but all-at-once
		
		//each transition of each MSCA in aut is associated with the corresponding index in aut
		final class FMCATransitionIndex {//more readable than Entry
			MSCATransition tra;
			Integer ind;
			public FMCATransitionIndex(MSCATransition tr, Integer i) {
				this.tra=tr; //different principals may have equal transitions
				this.ind=i;
			}
		}

		int rank=aut.stream()
				.map(MSCA::getRank)
				.collect(Collectors.summingInt(Integer::intValue));

		List<CAState> initial = aut.stream()  
				.flatMap(a -> a.getStates().stream())
				.filter(CAState::isInitial)
				.collect(Collectors.toList());

		CAState initialstate = new CAState(initial);

		Queue<Entry<List<CAState>,Integer>> toVisit = new ConcurrentLinkedQueue<Entry<List<CAState>,Integer>>(Arrays.asList(new AbstractMap.SimpleEntry<>(initial, 0)));//List.of(Map.entry(initial,0)));
		ConcurrentMap<List<CAState>, CAState> operandstat2compstat = new ConcurrentHashMap<List<CAState>, CAState>();//);Map.of(initial, initialstate));
		operandstat2compstat.put(initial, initialstate);//used to avoid duplicate target states 
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

				List<FMCATransitionIndex> trans2index = IntStream.range(0,aut.size())
						.mapToObj(i->aut.get(i)
								.getForwardStar(source.get(i))
								.parallelStream()
								.map(t->new FMCATransitionIndex(t,i)))
						.flatMap(Function.identity())
						.collect(toList()); //indexing outgoing transitions of each operand, used for target states and labels

				if (trans2index.parallelStream()
						.filter(e -> e.tra.getTarget().getStateL().size() != aut.get(e.ind).rank)
						.count()>0)
					throw new RuntimeException();

				//firstly match transitions are generated
				Map<MSCATransition, List<Entry<MSCATransition,List<CAState>>>> matchtransitions=
						trans2index.parallelStream()
						.flatMap(e -> trans2index.parallelStream()
								.filter(ee->(e.ind<ee.ind) && CALabel.match(e.tra.getLabel(), ee.tra.getLabel()))
								.flatMap(ee->{ 
									List<CAState> targetlist =  new ArrayList<CAState>(source);
									targetlist.set(e.ind, e.tra.getTarget());
									targetlist.set(ee.ind, ee.tra.getTarget());

									MSCATransition tradd=new MSCATransition(sourcestate,
											new CALabel(rank,computeSumPrincipal(e.tra,e.ind,aut),//index of principal in e
													computeSumPrincipal(ee.tra,ee.ind,aut),	//index of principal in ee										
													e.tra.getLabel().getAction(),ee.tra.getLabel().getAction()),
											operandstat2compstat.computeIfAbsent(targetlist, v->
											new CAState(v)), 
											e.tra.isNecessary()?e.tra.getModality():ee.tra.getModality());

									return Stream.of((Entry<MSCATransition, Entry<MSCATransition,List<CAState>>>) 
											new AbstractMap.SimpleEntry<MSCATransition, Entry<MSCATransition,List<CAState>>>(e.tra, 
													new AbstractMap.SimpleEntry<MSCATransition,List<CAState>>(tradd,targetlist)),
											(Entry<MSCATransition, Entry<MSCATransition,List<CAState>>>)//dummy, ee.tra is matched
											new AbstractMap.SimpleEntry<MSCATransition, Entry<MSCATransition,List<CAState>>>(ee.tra, 
													new AbstractMap.SimpleEntry<MSCATransition,List<CAState>>(tradd, (List<CAState>)new ArrayList<CAState>())));
								}))
						.collect( 
								groupingByConcurrent(Entry::getKey, 
										mapping(Entry::getValue,toList()))//each principal transition can have more matches
								);

				//collecting match transitions and adding unmatched transitions
				Set<Entry<MSCATransition,List<CAState>>> trmap=
						trans2index.parallelStream()
						.filter(e->!matchtransitions.containsKey(e.tra))//transitions not matched
						.collect(mapping(e->{List<CAState> targetlist = new ArrayList<CAState>(source);
						targetlist.set(e.ind, e.tra.getTarget());
						return 	new AbstractMap.SimpleEntry<MSCATransition,List<CAState>>
						(new MSCATransition(sourcestate,
								new CALabel(e.tra.getLabel(),rank,
										IntStream.range(0, e.ind)
										.map(i->aut.get(i).getRank())
										.sum()),//shifting positions of label
								operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)),
								e.tra.getModality()),
								targetlist);},
								toSet()));
				trmap.addAll(matchtransitions.values().parallelStream()//matched transitions
						.flatMap(List::parallelStream)
						.filter(e->(!e.getValue().isEmpty())) //no duplicates
						.collect(toSet()));

				if (trmap.parallelStream()//don't visit target states if they are bad
						.anyMatch(x->pruningPred!=null&&pruningPred.test(x.getKey())&&x.getKey().isUrgent()))
				{
					if (sourcestate.equals(initialstate))
						return null;
					continue;
				}
				else {//adding transitions, updating states
					Set<MSCATransition> trans=trmap.parallelStream()
							.filter(x->pruningPred==null||x.getKey().isNecessary()||pruningPred.negate().test(x.getKey()))//semicontrollable are not pruned
							.collect(mapping((Entry<MSCATransition, List<CAState>> e)-> e.getKey(),toSet()));
					tr.addAll(trans);

					if (pruningPred!=null)//avoid visiting targets of semicontrollable bad transitions
						dontvisit.addAll(trans.parallelStream()
								.filter(x->x.isSemiControllable()&&pruningPred.test(x))
								.map(MSCATransition::getTarget)
								.collect(toList()));

					toVisit.addAll(trmap.parallelStream()
							.filter(x->pruningPred==null||x.getKey().isNecessary()||pruningPred.negate().test(x.getKey()))//semicontrollable are not pruned
							.collect(mapping((Entry<MSCATransition, List<CAState>> e)-> e.getValue(),toSet()))
							.parallelStream()
							.map(s->new AbstractMap.SimpleEntry<List<CAState>,Integer>(s,sourceEntry.getValue()+1))
							.collect(toSet()));
				}
			}
		} while (!toVisit.isEmpty());

		int[][] finalstates = new int[rank][];
		int pointer=0;
		for (MSCA a : aut){
			System.arraycopy(a.getFinalStatesofPrincipals(), 0, finalstates, pointer,a.getRank());
			pointer+=a.getRank();
		}
//		Set<CAState> states =visited.parallelStream()
//				.map(l->operandstat2compstat.get(l))
//				.collect(Collectors.toSet());
		return new MSCA(rank, initialstate, finalstates, tr);
	}

	private static Integer computeSumPrincipal(MSCATransition etra, Integer eind, List<MSCA> aut)
	{
		return IntStream.range(0, eind)
				.map(i->aut.get(i).getRank())
				.sum()+etra.getLabel().getOffererOrRequester();
	}

	/**
	 * 
	 * @param aut	list of operands automata
	 * @return compute the union of the FMCA in aut
	 */
	public static MSCA union(List<MSCA> aut)
	{
		if (aut.size()==0)
			return null;
		int rank=aut.get(0).getRank(); 
		if (aut.stream()
				.map(MSCA::getRank)
				.anyMatch(x->x!=rank))
			throw new IllegalArgumentException("Automata with different ranks!"); 

		//		float fur= (float)aut.stream()
		//				.mapToDouble(x ->  x.getStates().parallelStream()
		//						.mapToDouble(CAState::getX)
		//						.max()
		//						.getAsDouble())
		//				.max()
		//				.getAsDouble(); //furthest node  

		final int upperbound=aut.parallelStream()
				.flatMap(x->x.getStates().parallelStream())
				.mapToInt(x->Arrays.stream(x.getState()).max().orElse(0))
				.max().orElse(0)+1; //for renaming states

		//repositioning states, renaming
		IntStream.range(0, aut.size())
		.forEach(id ->{
			aut.get(id).getStates().forEach(x->{
				//	x.setX(x.getX()+fur*(id)+25*id);
				//	x.setY(x.getY()+50);
				x.setState(Arrays.stream(x.getState())
						.map(s->s+upperbound*(id+1))
						.toArray());});
			aut.get(id).setFinalStatesofPrincipals(
					Arrays.stream(aut.get(id).getFinalStatesofPrincipals())
					.map(s->Arrays.stream(s).map(ar->ar+upperbound*(id+1))
							.toArray())
					.toArray(int[][]::new));
		}); 

		//gather all states
//		Set<CAState> statesunion=IntStream.range(0, aut.size())
//				.mapToObj(id ->aut.get(id).getStates())
//				.flatMap(Set::stream)
//				.collect(Collectors.toSet());

		//new initial state
		CAState newinitial = new CAState( new int[rank],//(float)((aut.size())*fur)/2,0,
				true,false);
//		statesunion.add(newinitial);		

		Set<MSCATransition> uniontr= new HashSet<>(aut.stream()
				.map(x->x.getTransition().size())
				.reduce(Integer::sum)
				.orElse(0)+aut.size());  //Initialized to the total number of transitions


		uniontr.addAll(IntStream.range(0, aut.size())
				.mapToObj(i->new MSCATransition(newinitial,new CALabel(rank, 0, "!dummy"),aut.get(i).getInitial(),MSCATransition.Modality.PERMITTED))
				.collect(Collectors.toSet())); //adding transition from new initial state to previous initial states

		uniontr.addAll(IntStream.range(0, aut.size())
				.mapToObj(i->aut.get(i).getTransition())
				.flatMap(Set::stream)
				.collect(Collectors.toSet())); //adding all other transitions

		int[][] finalstates = aut.stream()
				.map(MSCA::getFinalStatesofPrincipals)
				.reduce( (a,b)->{ 
					int[][] comb= new int[a.length][];
					IntStream.range(0,a.length).forEach(i->	{
						comb[i]= new int[a[i].length+b[i].length];
						System.arraycopy(a[i], 0, comb[i], 0, a[i].length);
						System.arraycopy(b[i], 0, comb[i], a[i].length, b[i].length);
					});
					return comb;
				}).orElseThrow(IllegalArgumentException::new);  //merging final states

		//MSCA.setInitialCA(newinitial, statesunion);
		
		//remove old initial states
		aut.parallelStream()
		.flatMap(a->a.getStates().stream())
		.filter(CAState::isInitial)
		.forEach(x->x.setInitial(false));

		return new MSCA(rank, newinitial, 
				finalstates, 
				uniontr);
	}

	@Override
	public MSCA clone()
	{	
		Map<BasicState,BasicState> clonedstate = this.getStates().stream()
				.flatMap(x->x.getStateL().stream())
				.distinct()
				.collect(Collectors.toMap(Function.identity(), s->new BasicState(s.getLabel(),s.isInit(),s.isFin())));

		Map<CAState,CAState> clonedcastates  = this.getStates().stream()
				.collect(Collectors.toMap(Function.identity(), 
						x->new CAState(x.getStateL().stream()
								.map(s->clonedstate.get(s))
								.collect(Collectors.toList()),
								x.getX(),x.getY())));

		return new MSCA(rank,
				clonedcastates.get(this.getInitial()),
				Arrays.stream(finalstates).map(int[]::clone).toArray(int[][]::new),
				this.getTransition().stream()
				.map(t->new MSCATransition(clonedcastates.get(t.getSource()),
						t.getLabel().getClone(),
						clonedcastates.get(t.getTarget()),
						t.getModality()))
				.collect(Collectors.toSet()));
//				clonedcastates.entrySet().stream()
//				.map(Entry::getValue)
//				.collect(Collectors.toSet()));
	}

	private void checkInitFin() 
	{
		Set<CAState> states = this.getStates();
		
		if (states.parallelStream()
		.filter(CAState::isInitial)
		.count()!=1)
			throw new IllegalArgumentException("No Exactly one Initial State found!");
		
		if (!states.parallelStream()
				.filter(CAState::isFinalstate)
				.findAny().isPresent())
					throw new IllegalArgumentException("No Final State found!");
				
//		Set<CAState> states_tr = this.getStates();
//		states_tr.stream()
//		.filter(s_tr->states.stream()
//				.allMatch(s->s!=s_tr))
//		.findAny()
//		.ifPresent(c->{throw new IllegalArgumentException("State "+c.toString()+ " is in a transition but not in states");});
//
//		states.stream()
//		.filter(s-> states_tr.stream()
//				.allMatch(s_tr->s_tr!=s))
//		.findAny()
//		.ifPresent(c -> {throw new IllegalArgumentException("State "+c.toString()+ " is in a state but not in any transition");});
	}

	public String toString() {
		StringBuilder pr = new StringBuilder();
		pr.append("Rank: "+rank+"\n");
		pr.append("Number of states: "+Arrays.toString(this.getNumStatesPrinc())+"\n");
		pr.append("Initial state: " +Arrays.toString(this.getInitial().getState())+"\n");
		pr.append("Final states: [");
		for (int i=0;i<finalstates.length;i++)
			pr.append(Arrays.toString(finalstates[i]));
		pr.append("]\n");
		pr.append("Transitions: \n");
		for (MSCATransition t : this.getTransition())
			pr.append(t.toString()+"\n");
		return pr.toString();
	}

	//	/**
	//	 * compute the projection on the i-th principal
	//	 * @param indexprincipal		index of the FMCA
	//	 * @return		the ith principal
	//	 * 
	//	 * @deprecated the XML representation must be fixed to store final states of principals for projection to work
	//	 */
	//	MSCA proj(int indexprincipal)
	//	{
	//		if ((indexprincipal<0)||(indexprincipal>this.getRank())) //check if the parameter i is in the rank of the FMCA
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
	//				if (!MSCAUtils.contains(selected, transitionsprincipal, pointer))
	//				{
	//					transitionsprincipal[pointer]=selected;
	//					pointer++;
	//				}
	//			}
	//		}
	//
	//		transitionsprincipal = MSCAUtils.removeTailsNull(transitionsprincipal, pointer, new MSCATransition[] {});
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

////used in the synthesis for readability, instead of using a function taking one argument and returning a bipredicate where to apply such argument
//interface TriPredicate<T,U,V> {
//	public boolean test(T arg1, U arg2, V arg3);
//}

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


//
//  **this is the composition method using Java 15, I translated back to Java 8 to use JML**
//
//	public static MSCA composition(List<MSCA> aut, Predicate<MSCATransition> pruningPred, Integer bound)
//	{
//		//each transition of each MSCA in aut is associated with the corresponding index in aut
//		final class FMCATransitionIndex {//more readable than Entry
//			MSCATransition tra;
//			Integer ind;
//			public FMCATransitionIndex(MSCATransition tr, Integer i) {
//				this.tra=tr; //different principals may have equal transitions
//				this.ind=i;
//			}
//		}
//		
//		int rank=aut.stream()
//				.map(MSCA::getRank)
//				.collect(Collectors.summingInt(Integer::intValue));
//
//		List<CAState> initial = aut.stream()  
//				.flatMap(a -> a.getStates().stream())
//				.filter(CAState::isInitial)
//				.collect(Collectors.toList());
//		CAState initialstate = new CAState(initial);
//
//		Queue<Entry<List<CAState>,Integer>> toVisit = new ConcurrentLinkedQueue<Entry<List<CAState>,Integer>>(List.of(Map.entry(initial,0)));
//		ConcurrentMap<List<CAState>, CAState> operandstat2compstat = new ConcurrentHashMap<List<CAState>, CAState>(Map.of(initial, initialstate));//used to avoid duplicate target states 
//		Set<MSCATransition> tr = new HashSet<MSCATransition>();//transitions of the composed automaton to build
//		Set<List<CAState>> visited = new HashSet<List<CAState>>();
//		Queue<CAState> dontvisit = new ConcurrentLinkedQueue<CAState>();
//
//		do {
//			Entry<List<CAState>,Integer> sourceEntry=toVisit.remove(); //pop state to visit
//			if (visited.add(sourceEntry.getKey())&&sourceEntry.getValue()<bound) //if states has not been visited so far
//			{
//				List<CAState> source =sourceEntry.getKey();
//				CAState sourcestate= operandstat2compstat.get(source);
//				if (dontvisit.remove(sourcestate))
//					continue;//was target of a semicontrollable bad transition
//
//				List<FMCATransitionIndex> trans2index = IntStream.range(0,aut.size())
//						.mapToObj(i->aut.get(i)
//								.getForwardStar(source.get(i))
//								.parallelStream()
//								.map(t->new FMCATransitionIndex(t,i)))
//						.flatMap(Function.identity())
//						.collect(toList()); //indexing outgoing transitions of each operand, used for target states and labels
//
//				//firstly match transitions are generated
//				Map<MSCATransition, List<Entry<MSCATransition,List<CAState>>>> matchtransitions=
//						trans2index.parallelStream()
//						.collect(flatMapping(e -> trans2index.parallelStream()
//								.filter(ee->(e.ind<ee.ind) && CALabel.match(e.tra.getLabel(), ee.tra.getLabel()))
//								.flatMap(ee->{ 
//									List<CAState> targetlist =  new ArrayList<CAState>(source);
//									targetlist.set(e.ind, e.tra.getTarget());
//									targetlist.set(ee.ind, ee.tra.getTarget());
//									
//									MSCATransition tradd=new MSCATransition(sourcestate,
//											e.tra.getLabel().isOffer(), //since e.ind<ee.ind true if e is offer
//											(e.tra.getLabel().isOffer())?e.tra.getLabel().getAction():ee.tra.getLabel().getAction(),//offer action
//											operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)), 
//											e.tra.isNecessary()?e.tra.getModality():ee.tra.getModality());
//
//									return Stream.of((Entry<MSCATransition, Entry<MSCATransition,List<CAState>>>) 
//											Map.entry(e.tra, Map.entry(tradd,targetlist)),
//											(Entry<MSCATransition, Entry<MSCATransition,List<CAState>>>)//dummy, ee.tra is matched
//											Map.entry(ee.tra, Map.entry(tradd, (List<CAState>)new ArrayList<CAState>())));
//								}), 
//								groupingByConcurrent(Entry::getKey, 
//										mapping(Entry::getValue,toList()))//each principal transition can have more matches
//								));
//
//				//collecting match transitions and adding unmatched transitions
//				Set<Entry<MSCATransition,List<CAState>>> trmap=
//						trans2index.parallelStream()
//						.filter(e->!matchtransitions.containsKey(e.tra))//transitions not matched
//						.collect(Collectors.collectingAndThen(
//								mapping(e->{List<CAState> targetlist = new ArrayList<CAState>(source);
//								targetlist.set(e.ind, e.tra.getTarget());
//								return 
//										Map.entry((!e.tra.getLabel().isMatch())?
//												new MSCATransition(sourcestate,  
//														e.tra.getLabel().getAction(),
//														operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)),
//														e.tra.getModality())
//												:new MSCATransition(sourcestate, 
//														e.tra.getLabel().getOfferer()<e.tra.getLabel().getRequester(), 
//														e.tra.getLabel().getAction(),
//														operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)),
//													 	e.tra.getModality()),
//												targetlist);},
//										toSet()),
//								trm->{trm.addAll(matchtransitions.values().parallelStream()//matched transitions
//										.flatMap(List::parallelStream)
//										.filter(e->(!e.getValue().isEmpty())) //no duplicates
//										.collect(toSet()));
//								return trm;}));
//
//				if (trmap.parallelStream()//don't visit target states if they are bad
//						.anyMatch(x->pruningPred!=null&&pruningPred.test(x.getKey())&&x.getKey().isUrgent()))
//				{
//					if (sourcestate.equals(initialstate))
//						return null;
//					continue;
//				}
//				else {//adding transitions, updating states
//					tr.addAll(trmap.parallelStream()
//							.filter(x->pruningPred==null||x.getKey().isNecessary()||pruningPred.negate().test(x.getKey()))//semicontrollable are not pruned
//							.collect(Collectors.teeing(
//									mapping((Entry<MSCATransition, List<CAState>> e)-> e.getKey(),toSet()), 
//									mapping((Entry<MSCATransition, List<CAState>> e)-> e.getValue(),toSet()), 
//									(trans,toVis)->{
//										toVisit.addAll(toVis.parallelStream()
//												.map(s->Map.entry(s,sourceEntry.getValue()+1))
//												.collect(toSet()));
//										if (pruningPred!=null)//avoid visiting targets of semicontrollable bad transitions
//											dontvisit.addAll(trans.parallelStream()
//													.filter(x->x.isSemiControllable()&&pruningPred.test(x))
//													.map(MSCATransition::getTarget)
//													.collect(toList()));
//										return trans;
//									})));
//				}
//			}
//		} while (!toVisit.isEmpty());
//
//		int[][] finalstates = new int[rank][];
//		int pointer=0;
//		for (MSCA a : aut){
//			System.arraycopy(a.getFinalStatesofPrincipals(), 0, finalstates, pointer,a.getRank());
//			pointer+=a.getRank();
//		}
//		Set<CAState> states =visited.parallelStream()
//				.map(l->operandstat2compstat.get(l))
//				.collect(Collectors.toSet());
//		return new MSCA(rank, initialstate, finalstates, tr, states);
//	}
