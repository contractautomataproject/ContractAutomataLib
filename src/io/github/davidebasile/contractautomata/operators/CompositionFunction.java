package io.github.davidebasile.contractautomata.operators;

import static java.util.stream.Collectors.groupingByConcurrent;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
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
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.MSCATransition;

/**
 * Class implementing the composition
 * 
 * @author Davide Basile
 *
 */
public class CompositionFunction implements BiFunction<Predicate<MSCATransition>,Integer,MSCA>{

	//each transition of each MSCA in aut is associated with the corresponding index in aut
	static final class MSCATransitionIndex {//more readable than Entry
		MSCATransition tra;
		Integer ind;
		public MSCATransitionIndex(MSCATransition tr, Integer i) {
			this.tra=tr; //different principals may have equal transitions
			this.ind=i;
		}
	}
	
	private List<MSCA> aut;
	private int rank;
	private List<CAState> initial;
	private CAState initialstate;
	private Queue<Entry<List<CAState>,Integer>> toVisit;
	private Queue<Entry<List<CAState>,Integer>> frontier;
	private ConcurrentMap<List<CAState>, CAState> operandstat2compstat;
	private Set<MSCATransition> tr;
	private Set<List<CAState>> visited;
	private Queue<CAState> dontvisit;
	
	/**
	 * 
	 * @param aut the list of the automata to compose
	 */
	public CompositionFunction(List<MSCA> aut)
	{
		this.aut=aut;
		this.rank=aut.stream()
				.map(MSCA::getRank)
				.collect(Collectors.summingInt(Integer::intValue));
		this.initial = aut.stream()  
				.flatMap(a -> a.getStates().stream())
				.filter(CAState::isInitial)
				.collect(Collectors.toList());

		this.initialstate = new CAState(initial);
		this.toVisit = new ConcurrentLinkedQueue<Entry<List<CAState>,Integer>>(Arrays.asList(new AbstractMap.SimpleEntry<>(initial, 0)));//List.of(Map.entry(initial,0)));
		this.frontier = new ConcurrentLinkedQueue<Entry<List<CAState>,Integer>>();
		this.operandstat2compstat = new ConcurrentHashMap<List<CAState>, CAState>();//);Map.of(initial, initialstate));
		this.operandstat2compstat.put(initial, initialstate);//used to avoid duplicate target states 
		this.tr = new HashSet<MSCATransition>();//transitions of the composed automaton to build
		this.visited = new HashSet<List<CAState>>();
		this.dontvisit = new ConcurrentLinkedQueue<CAState>();
	}

	/**
	 * This is the most important method of the tool, it computes the non-associative composition of contract automata.
	 * 
	 * @param pruningPred  the invariant that all transitions must satisfy
	 * @param bound  the bound on the depth of the visit
	 * @return  the composed automaton
	 */
	@Override
	public MSCA apply(Predicate<MSCATransition> pruningPred, Integer bound)
	{
		//TODO too long function
		//TODO study non-associative composition but all-at-once
		//TODO study remotion of requests on-credit for a closed composition
		
		if (!frontier.isEmpty())//in case this method is called more than once, a potential frontier can be restored
		{
			toVisit.addAll(frontier);
			frontier.clear();
		}

		do {
			Entry<List<CAState>,Integer> sourceEntry=toVisit.remove(); //pop state to visit
			if (sourceEntry.getValue()>=bound)  //if bound is reached store the frontier for a next call
				frontier.add(sourceEntry);
			else if (visited.add(sourceEntry.getKey())) //if the state has not been visited so far and it is within bound
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
				Map<MSCATransition, List<SimpleEntry<MSCATransition,List<CAState>>>> matchtransitions=
						trans2index.parallelStream()
						.flatMap(e -> trans2index.parallelStream()
								.filter(ee->(e.ind<ee.ind) && e.tra.getLabel().match(ee.tra.getLabel()))
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

									return Stream.of((SimpleEntry<MSCATransition, SimpleEntry<MSCATransition,List<CAState>>>) 
											new AbstractMap.SimpleEntry<MSCATransition, SimpleEntry<MSCATransition,List<CAState>>>(e.tra, 
													new AbstractMap.SimpleEntry<MSCATransition,List<CAState>>(tradd,targetlist)),
											(SimpleEntry<MSCATransition, SimpleEntry<MSCATransition,List<CAState>>>)//dummy, ee.tra is matched
											new AbstractMap.SimpleEntry<MSCATransition, SimpleEntry<MSCATransition,List<CAState>>>(ee.tra, 
													new AbstractMap.SimpleEntry<MSCATransition,List<CAState>>(tradd, (List<CAState>)new ArrayList<CAState>())));
								}))
						.collect( 
								groupingByConcurrent(Entry::getKey, 
										mapping(Entry::getValue,toList()))//each principal transition can have more matches
								);

				//collecting match transitions and adding unmatched transitions
				Set<SimpleEntry<MSCATransition,List<CAState>>> trmap=
						trans2index.parallelStream()
						.filter(e->!matchtransitions.containsKey(e.tra))//transitions not matched
						.collect(mapping(e->{List<CAState> targetlist = new ArrayList<CAState>(source);
						targetlist.set(e.ind, e.tra.getTarget());
						return 	new AbstractMap.SimpleEntry<MSCATransition,List<CAState>>
						(new MSCATransition(sourcestate,
								new CALabel(e.tra.getLabel(),rank, //TODO change if you would like to preserve the CM constraints
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

				if (trmap.parallelStream()
						.anyMatch(x->pruningPred!=null&&pruningPred.test(x.getKey())&&x.getKey().isUrgent()))
				{
					if (sourcestate.equals(initialstate))
						return null;
					continue;//source state is bad in this case don't visit target states
				}
				else 
				{//adding transitions, updating states
					Set<MSCATransition> trans=trmap.parallelStream()
							.filter(x->pruningPred==null||x.getKey().isNecessary()||pruningPred.negate().test(x.getKey()))//semicontrollable are not pruned
							.collect(mapping((Entry<MSCATransition, List<CAState>> e)-> e.getKey(),toSet()));
					tr.addAll(trans);

					if (pruningPred!=null)//avoid visiting targets of semicontrollable bad transitions
						dontvisit.addAll(trans.parallelStream()
								.filter(x->x.isLazy()&&pruningPred.test(x))
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
		
		//if (pruningPred==null) assert(new CompositionSpecCheck().test(aut, new MSCA(tr)));   post-condition
		
		//initialstate.getState().get(0).setFinalstate(true);
		return new MSCA(tr);
	}
	
	public boolean isFrontierEmpty() {
		return this.frontier.isEmpty();
	}

	private static Integer computeSumPrincipal(MSCATransition etra, Integer eind, List<MSCA> aut)
	{
		return IntStream.range(0, eind)
				.map(i->aut.get(i).getRank())
				.sum()+etra.getLabel().getOffererOrRequester();
	}

}


//END OF CLASS






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

