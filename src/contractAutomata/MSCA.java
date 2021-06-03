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
 * 
 * @author Davide Basile
 *
 */
public class MSCA
{ 

	/**
	 * transitions of the automaton
	 */
	private final Set<MSCATransition> tra;

	private Map<CAState,Boolean> reachable;
	private Map<CAState,Boolean> successful;

	public MSCA(Set<MSCATransition> tr) 
	{
		if (tr == null)
			throw new IllegalArgumentException("Null argument");

		if (tr.size()==0)
			throw new IllegalArgumentException("No transitions");

		if (tr.parallelStream()
				.anyMatch(Objects::isNull))
			throw new IllegalArgumentException("Null element");

		MSCATransition tt = tr.stream().findFirst().orElse(null);
		if (tr.parallelStream()
				.anyMatch(t->t.getRank()!=tt.getRank()))
			throw new IllegalArgumentException("Transitions with different rank");

		this.tra=tr;

		Set<CAState> states = this.getStates();

		if(states.stream()
				.anyMatch(x-> states.stream()
						.filter(y->x!=y && x.getState().equals(y.getState()))
						.count()!=0))
			throw new IllegalArgumentException("Transitions have ambiguous states (different objects for the same state).");

		if (states.parallelStream()
				.filter(CAState::isInitial)
				.count()!=1)
			throw new IllegalArgumentException("Not Exactly one Initial State found! ");

		if (!states.parallelStream()
				.filter(CAState::isFinalstate)
				.findAny().isPresent())
			throw new IllegalArgumentException("No Final States!");

	}

	public  Set<MSCATransition> getTransition()
	{
		return tra;
	}

	/**
	 * @return all  states that appear in at least one transition
	 */
	public Set<CAState> getStates()
	{
		Set<CAState> cs= this.getTransition().stream()
				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
				.collect(Collectors.toSet()); //CAState without equals, duplicates objects are detected

		return cs;

	}

	/**
	 * 
	 * @return a map where key is the index of principal, and value is its set of basic states
	 */
	public Map<Integer,Set<BasicState>> getBasicStates()
	{
		return this.getStates().stream()
				.flatMap(cs->cs.getState().stream()
						.map(bs->new AbstractMap.SimpleEntry<Integer,BasicState>(cs.getState().indexOf(bs),bs)))
				.collect(Collectors.groupingBy(Entry::getKey, Collectors.mapping(Entry::getValue, Collectors.toSet())));

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
				.filter(x->x==initial)
				.findAny().orElseThrow(IllegalArgumentException::new);

		init.setInitial(true);
	}

	public int getRank()
	{
		return this.getTransition().iterator().next().getRank();
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
				(x,t,bad) -> x.isUrgent()&&!t.contains(x));
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
				(x,t,bad) -> (!x.isUrgent()&&x.isUncontrollableOrchestration(t, bad))
								||(x.isUrgent()&&!t.contains(x)));
	}

//	public MSCA choreography_()
//	{
//		if (this.getTransition().parallelStream()
//				.anyMatch(t-> !t.isPermitted()&&t.getLabel().isRequest()))
//			throw new UnsupportedOperationException("The automaton contains necessary requests that are not allowed in the choreography synthesis");
//
//		MSCA aut = this;
//		MSCATransition toRemove=null;
//		do 
//		{ aut = aut.synthesis((x,t,bad) -> !x.getLabel().isMatch()||bad.contains(x.getTarget()),
//				(x,t,bad) -> (bad.contains(x.getTarget())||!t.contains(x)) && x.isUncontrollableChoreography(t, bad));
//		if (aut==null)
//			break;
//		final Set<MSCATransition> trf = aut.getTransition();
//		toRemove=(aut.getTransition().parallelStream()
//				.filter(x->!x.satisfiesBranchingCondition(trf, new HashSet<CAState>()))
//				.findAny() 
//				.orElse(null));
//		} while (aut.getTransition().remove(toRemove));
//		return aut;
//	}

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

		MSCATransition toRemove=null; 
		Set<String> violatingbc = new HashSet<>();
		MSCA aut;
		do 
		{ 
			aut = this.clone().synthesis((x,t,bad) -> !x.getLabel().isMatch()||bad.contains(x.getTarget())||violatingbc.contains(x.toCSV()),
					(x,t,bad) -> (!x.isUrgent()&&x.isUncontrollableChoreography(t, bad))||(x.isUrgent()&&!t.contains(x)));
			if (aut==null)
				break;
			final Set<MSCATransition> trf = aut.getTransition();
			toRemove=(aut.getTransition().parallelStream()
					.filter(x->!x.satisfiesBranchingCondition(trf, new HashSet<CAState>()))
					.findAny() 
					.orElse(null));
		} while (toRemove!=null && violatingbc.add(toRemove.toCSV()));
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
					.filter(x->forbiddenPred.test(x,trf, Rf))
					.map(MSCATransition::getSource)
					.collect(Collectors.toSet())); //Ri

			update=Rf.size()!=R.size()|| trf.size()!=this.getTransition().size();
		} while(update);

	
		if (R.contains(init)||this.getTransition().size()==0)
			return null;
		
		//remove dangling transitions
		this.getTransition().removeAll(this.getTransition().parallelStream()
				.filter(x->!this.reachable.get(x.getSource())||!this.successful.get(x.getTarget()))
				.collect(Collectors.toSet()));

		return this;
	}



	/**
	 * 
	 * @return the number of states
	 */
	public int getNumStates()
	{
		return this.getStates().size();
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
	 * 
	 * @param source   the source state
	 * @return  the transitions outgoing the source state
	 */
	public Set<MSCATransition> getForwardStar(CAState source) {
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
		//TODO study remotion of requests on-credit for a closed composition

		//each transition of each MSCA in aut is associated with the corresponding index in aut
		final class MSCATransitionIndex {//more readable than Entry
			MSCATransition tra;
			Integer ind;
			public MSCATransitionIndex(MSCATransition tr, Integer i) {
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

				List<MSCATransitionIndex> trans2index = IntStream.range(0,aut.size())
						.mapToObj(i->aut.get(i)
								.getForwardStar(source.get(i))
								.parallelStream()
								.map(t->new MSCATransitionIndex(t,i)))
						.flatMap(Function.identity())
						.collect(toList()); //indexing outgoing transitions of each operand, used for target states and labels

				//				assert(trans2index.parallelStream()
				//						.filter(e -> e.tra.getRank() != aut.get(e.ind).rank)
				//						.count()==0);

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

		return new MSCA(tr);
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
		if (aut==null||aut.size()==0)
			return null;

		int rank=aut.get(0).getRank(); 
		if (aut.stream()
				.map(MSCA::getRank)
				.anyMatch(x->x!=rank))
			throw new IllegalArgumentException("Automata with different ranks!"); 

		//		final int upperbound=aut.parallelStream()
		//				.flatMap(x->x.getStates().parallelStream())
		//				.mapToInt(x->
		//				x.getState().stream()
		//				.mapToInt(bs->Integer.parseInt(bs.getLabel()))
		//				.max().orElse(0))
		//				.max().orElse(0)+1; //for renaming states

		//relabeling
		IntStream.range(0, aut.size())
		.forEach(id ->{
			aut.get(id).getStates().forEach(x->{
				x.getState().forEach(s->s.setLabel(id+"_"+s.getLabel()));
			});
		}); 

		//new initial state
		CAState newinitial = new CAState(IntStream.range(0,rank)
				.mapToObj(i->new BasicState("0",true,false))
				.collect(Collectors.toList()),0,0);

		Set<MSCATransition> uniontr= new HashSet<>(aut.stream()
				.map(x->x.getTransition().size())
				.reduce(Integer::sum)
				.orElse(0)+aut.size());  //Initialized to the total number of transitions

		uniontr.addAll(IntStream.range(0, aut.size())
				.mapToObj(i->new MSCATransition(newinitial,new CALabel(rank, 0, "!dummy"),aut.get(i).getInitial(),MSCATransition.Modality.PERMITTED))
				.collect(Collectors.toSet())); //adding transition from new initial state to previous initial states

		//remove old initial states, I need to do this now
		aut.parallelStream()
		.flatMap(a->a.getStates().stream())
		.filter(CAState::isInitial)
		.forEach(x->x.setInitial(false));

		uniontr.addAll(IntStream.range(0, aut.size())
				.mapToObj(i->aut.get(i).getTransition())
				.flatMap(Set::stream)
				.collect(Collectors.toSet())); //adding all other transitions

		return new MSCA(uniontr);
	}

	@Override
	public MSCA clone()
	{	
		Map<BasicState,BasicState> clonedstate = this.getStates().stream()
				.flatMap(x->x.getState().stream())
				.distinct()
				.collect(Collectors.toMap(Function.identity(), s->new BasicState(s.getLabel(),s.isInit(),s.isFin())));

		Map<CAState,CAState> clonedcastates  = this.getStates().stream()
				.collect(Collectors.toMap(Function.identity(), 
						x->new CAState(x.getState().stream()
								.map(s->clonedstate.get(s))
								.collect(Collectors.toList()),
								x.getX(),x.getY())));

		return new MSCA(this.getTransition().stream()
				.map(t->new MSCATransition(clonedcastates.get(t.getSource()),
						t.getLabel().getClone(),
						clonedcastates.get(t.getTarget()),
						t.getModality()))
				.collect(Collectors.toSet()));
	}


	public String toString() {
		StringBuilder pr = new StringBuilder();
		int rank = this.getRank();
		pr.append("Rank: "+rank+"\n");

		pr.append("Initial state: " +this.getInitial().getState().toString()+"\n");
		pr.append("Final states: [");
		for (int i=0;i<rank;i++) {
			pr.append(Arrays.toString(
					this.getBasicStates().get(i).stream()
					.filter(BasicState::isFin)
					.map(BasicState::getLabel)
					//.mapToInt(Integer::parseInt)
					.toArray()));
		}
		pr.append("]\n");
		pr.append("Transitions: \n");
		for (MSCATransition t : this.getTransition())
			pr.append(t.toString()+"\n");
		return pr.toString();
	}



	/**
	 * compute the projection on the i-th principal
	 * @param indexprincipal index of the MSCA
	 * @param function returning the index of the necessary principal in a transition, if any
	 * @return	the ith principal
	 * 
	 */
	public MSCA projection(int indexprincipal, Function<MSCATransition, Integer> getNecessaryPrincipal)
	{
		if ((indexprincipal<0)||(indexprincipal>this.getRank())) 
			throw new IllegalArgumentException("Index out of rank");

		//extracting the basicstates of the principal and creating the castates of the projection
		Map<BasicState,CAState> bs2cs = this.getTransition().parallelStream()
				.flatMap(t->Stream.of(t.getSource(), t.getTarget()))
				.map(s->s.getState().get(indexprincipal))
				.distinct()
				.collect(Collectors.toMap(Function.identity(), bs->new CAState(new ArrayList<BasicState>(Arrays.asList(bs)),0,0)));

		//associating each castate of the composition with the castate of the principal
		Map<CAState,CAState> map2princst = 
				this.getTransition().parallelStream()
				.flatMap(t->Stream.of(t.getSource(), t.getTarget()))
				.distinct()
				.collect(Collectors.toMap(Function.identity(), s->bs2cs.get(s.getState().get(indexprincipal))));


		return new MSCA(this.getTransition().parallelStream()
				.filter(t-> t.getLabel().getOfferer().equals(indexprincipal) || t.getLabel().getRequester().equals(indexprincipal))
				.map(t-> new MSCATransition(map2princst.get(t.getSource()),
						t.getLabel().getOfferer().equals(indexprincipal)?
								new CALabel(1,0,t.getLabel().getAction())
								:new CALabel(1,0,t.getLabel().isRequest()?t.getLabel().getAction()
										:t.getLabel().getCoAction()),
								map2princst.get(t.getTarget()),
								t.isPermitted()||!getNecessaryPrincipal.apply(t).equals(indexprincipal)?MSCATransition.Modality.PERMITTED
										:t.isLazy()?MSCATransition.Modality.LAZY:MSCATransition.Modality.URGENT
						))
				.collect(Collectors.toSet())); 
	}
}


//END OF THE CLASS










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
