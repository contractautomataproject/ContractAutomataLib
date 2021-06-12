package contractAutomata;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
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

	public MSCA(Set<MSCATransition> tr) 
	{
		if (tr == null)
			throw new IllegalArgumentException("Null argument");

		if (tr.isEmpty())
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
	public final Set<CAState> getStates()
	{
		return this.getTransition().stream()
				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
				.collect(Collectors.toSet()); //CAState without equals, duplicates objects are detected
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

	public int getRank()
	{
		return this.getTransition().iterator().next().getRank();
	}
	
	public int getNumStates()
	{
		return this.getStates().size();
	}

	public Set<MSCATransition> getForwardStar(CAState source) {
		return this.getTransition().parallelStream()
				.filter(x->x.getSource().equals(source))
				.collect(Collectors.toSet());
	}


	@Override
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

}


//END OF THE CLASS







///**
//* the only initial state in the set of states is set to be the one equal to argument initial
//* @param initial the state to be set
//*/
//private void setInitialCA(CAState initial)
//{
//	Set<CAState> states=this.getStates();
//
//	states.parallelStream()
//	.filter(CAState::isInitial)
//	.forEach(x->x.setInitial(false));
//
//	CAState init = states.parallelStream()
//			.filter(x->x==initial)
//			.findAny().orElseThrow(IllegalArgumentException::new);
//
//	init.setInitial(true);
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
