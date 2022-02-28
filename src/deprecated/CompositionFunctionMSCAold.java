//package deprecated;
//
//import java.util.AbstractMap;
//import java.util.AbstractMap.SimpleEntry;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Map.Entry;
//import java.util.Queue;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.ConcurrentLinkedQueue;
//import java.util.concurrent.ConcurrentMap;
//import java.util.function.BiFunction;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//import java.util.stream.Stream;
//
//import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
//import io.github.davidebasile.contractautomata.automaton.label.CALabel;
//import io.github.davidebasile.contractautomata.automaton.state.BasicState;
//import io.github.davidebasile.contractautomata.automaton.state.CAState;
//import io.github.davidebasile.contractautomata.automaton.transition.ModalTransition;
//
///**
// * Class implementing the composition
// * 
// * @author Davide Basile
// */
//public class CompositionFunctionMSCAold implements BiFunction<Predicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>,Integer,ModalAutomaton<CALabel>>{
//
//	//each transition of each ModalAutomaton<CALabel> in aut is associated with the corresponding index in aut
//	static final class ModalTransitionIndex {//more readable than Entry
//		ModalTransition<List<BasicState>,List<String>,CAState,CALabel> tra;
//		Integer ind;
//		public ModalTransitionIndex(ModalTransition<List<BasicState>,List<String>,CAState,CALabel> tr, Integer i) {
//			this.tra=tr; //different principals may have equal transitions
//			this.ind=i;
//		}
//	}
//
//	private List<ModalAutomaton<CALabel>> aut;
//	private int rank;
//	private List<CAState> initial;
//	private CAState initialstate;
//	private Queue<Entry<List<CAState>,Integer>> toVisit;
//	private Queue<Entry<List<CAState>,Integer>> frontier;
//	private ConcurrentMap<List<CAState>, CAState> operandstat2compstat;
//	private Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> tr;
//	private Set<List<CAState>> visited;
//	private Queue<CAState> dontvisit;
//	private BiFunction<CALabel,CALabel,Boolean> match;
//	private BiFunction<ModalTransitionIndex,ModalTransitionIndex,CALabel> createLabel;
//
//	/**
//	 * 
//	 * @param aut the list of the automata to compose
//	 */
//	public CompositionFunctionMSCAold(List<ModalAutomaton<CALabel>> aut)
//	{
//		this.aut=aut;
//		this.rank=aut.stream()
//				.map(ModalAutomaton<CALabel>::getRank)
//				.collect(Collectors.summingInt(Integer::intValue));
//		this.initial = aut.stream()  
//				.flatMap(a -> a.getStates().stream())
//				.filter(CAState::isInitial)
//				.collect(Collectors.toList());
//
//		this.initialstate = new CAState(initial);
//		this.toVisit = new ConcurrentLinkedQueue<Entry<List<CAState>,Integer>>(Arrays.asList(new AbstractMap.SimpleEntry<>(initial, 0)));//List.of(Map.entry(initial,0)));
//		this.frontier = new ConcurrentLinkedQueue<Entry<List<CAState>,Integer>>();
//		this.operandstat2compstat = new ConcurrentHashMap<List<CAState>, CAState>();//);Map.of(initial, initialstate));
//		this.operandstat2compstat.put(initial, initialstate);//used to avoid duplicate target states 
//		this.tr = new HashSet<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>();//transitions of the composed automaton to build
//		this.visited = new HashSet<List<CAState>>();
//		this.dontvisit = new ConcurrentLinkedQueue<CAState>();
//		this.match= (t1,t2) -> t1.match(t2);
//		this.createLabel = (e, ee) -> new CALabel(rank,computeSumPrincipal(e.tra,e.ind,aut),//index of principal in e
//				computeSumPrincipal(ee.tra,ee.ind,aut),	//index of principal in ee										
//				e.tra.getLabel().getTheAction(),ee.tra.getLabel().getTheAction());
//	}
//
//	//	creating new labels may break the well-formedness of ca labels, i.e. calabels must be requests, offers or matches
//	//
//	//	public CompositionFunction(List<ModalAutomaton<CALabel>> aut, BiFunction<CALabel,CALabel,Boolean> match, BiFunction<ModalTransitionIndex,ModalTransitionIndex,CALabel> createLabel)
//	//	{
//	//		this(aut);
//	//		this.match=match;
//	//		this.createLabel=createLabel;
//	//	}
//
//	/**
//	 * This is the most important method of the tool, it computes the non-associative composition of contract automata.
//	 * 
//	 * @param pruningPred  the invariant that all transitions must satisfy
//	 * @param bound  the bound on the depth of the visit
//	 * @return  the composed automaton
//	 */
//	@Override
//	public ModalAutomaton<CALabel> apply(Predicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> pruningPred, Integer bound)
//	{
//		//TODO study non-associative composition but all-at-once
//		//TODO study remotion of requests on-credit for a closed composition
//
//		if (!frontier.isEmpty())//in case this method is called more than once, a potential frontier can be restored
//		{
//			toVisit.addAll(frontier);
//			frontier.clear();
//		}
//
//		do {
//			Entry<List<CAState>,Integer> sourceEntry=toVisit.remove(); //pop state to visit
//			if (sourceEntry.getValue()>=bound)  //if bound is reached store the frontier for a next call
//				frontier.add(sourceEntry);
//			else if (visited.add(sourceEntry.getKey())) //if the state has not been visited so far and it is within bound
//			{
//				List<CAState> source =sourceEntry.getKey();
//				CAState sourcestate= operandstat2compstat.get(source);
//
//				if (dontvisit.remove(sourcestate))
//					continue;//was target of a semicontrollable bad transition
//
//				List<ModalTransitionIndex> trans2index = IntStream.range(0,aut.size())
//						.mapToObj(i->aut.get(i)
//								.getForwardStar(source.get(i))
//								.parallelStream()
//								.map(t->new ModalTransitionIndex(t,i)))
//						.flatMap(Function.identity())
//						.collect(Collectors.toList()); //indexing outgoing transitions of each operand, used for target states and labels
//
//				//				assert(trans2index.parallelStream()
//				//						.filter(e -> e.tra.getRank() != aut.get(e.ind).rank)
//				//						.count()==0);
//
//				//firstly match transitions are generated
//				Map<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, List<SimpleEntry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>>> matchtransitions=
//						trans2index.parallelStream()
//						.flatMap(e -> trans2index.parallelStream()
//								.filter(ee->(e.ind<ee.ind) && match.apply(e.tra.getLabel(), ee.tra.getLabel()))
//								.flatMap(ee->{ 
//									List<CAState> targetlist =  new ArrayList<CAState>(source);
//									targetlist.set(e.ind, e.tra.getTarget());
//									targetlist.set(ee.ind, ee.tra.getTarget());
//
//									ModalTransition<List<BasicState>,List<String>,CAState,CALabel> tradd=new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(sourcestate,
//											this.createLabel.apply(e, ee),
//											operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)), 
//											e.tra.isNecessary()?e.tra.getModality():ee.tra.getModality());
//
//									return Stream.of((SimpleEntry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, SimpleEntry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>>) 
//											new AbstractMap.SimpleEntry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, SimpleEntry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>>(e.tra, 
//													new AbstractMap.SimpleEntry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>(tradd,targetlist)),
//											(SimpleEntry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, SimpleEntry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>>)//dummy, ee.tra is matched
//											new AbstractMap.SimpleEntry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, SimpleEntry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>>(ee.tra, 
//													new AbstractMap.SimpleEntry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>(tradd, (List<CAState>)new ArrayList<CAState>())));
//								}))
//						.collect( 
//								Collectors.groupingByConcurrent(Entry::getKey, 
//										Collectors.mapping(Entry::getValue,Collectors.toList()))//each principal transition can have more matches
//								);
//
//				//collecting match transitions and adding unmatched transitions
//				Set<SimpleEntry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>> trmap=
//						trans2index.parallelStream()
//						.filter(e->!matchtransitions.containsKey(e.tra))//transitions not matched
//						.collect(Collectors.mapping(e->{List<CAState> targetlist = new ArrayList<CAState>(source);
//						targetlist.set(e.ind, e.tra.getTarget());
//						return 	new AbstractMap.SimpleEntry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>
//						(new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(sourcestate,
//								new CALabel(e.tra.getLabel(),rank, //TODO change if you would like to preserve the CM constraints
//										IntStream.range(0, e.ind)
//										.map(i->aut.get(i).getRank())
//										.sum()),//shifting positions of label
//								operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)),
//								e.tra.getModality()),
//								targetlist);},
//								Collectors.toSet()));
//				trmap.addAll(matchtransitions.values().parallelStream()//matched transitions
//						.flatMap(List::parallelStream)
//						.filter(e->(!e.getValue().isEmpty())) //no duplicates
//						.collect(Collectors.toSet()));
//
//				if (trmap.parallelStream()
//						.anyMatch(x->pruningPred!=null&&pruningPred.test(x.getKey())&&x.getKey().isUrgent()))
//				{
//					if (sourcestate.equals(initialstate))
//						return null;
//					continue;//source state is bad in this case don't visit target states
//				}
//				else 
//				{//adding transitions, updating states
//					Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> trans=trmap.parallelStream()
//							.filter(x->pruningPred==null||x.getKey().isNecessary()||pruningPred.negate().test(x.getKey()))//semicontrollable are not pruned
//							.collect(Collectors.mapping((Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, List<CAState>> e)-> e.getKey(),Collectors.toSet()));
//					tr.addAll(trans);
//
//					if (pruningPred!=null)//avoid visiting targets of semicontrollable bad transitions
//						dontvisit.addAll(trans.parallelStream()
//								.filter(x->x.isLazy()&&pruningPred.test(x))
//								.map(ModalTransition<List<BasicState>,List<String>,CAState,CALabel>::getTarget)
//								.collect(Collectors.toList()));
//
//					toVisit.addAll(trmap.parallelStream()
//							.filter(x->pruningPred==null||x.getKey().isNecessary()||pruningPred.negate().test(x.getKey()))//semicontrollable are not pruned
//							.collect(Collectors.mapping((Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, List<CAState>> e)-> e.getValue(),Collectors.toSet()))
//							.parallelStream()
//							.map(s->new AbstractMap.SimpleEntry<List<CAState>,Integer>(s,sourceEntry.getValue()+1))
//							.collect(Collectors.toSet()));
//				}
//			}
//		} while (!toVisit.isEmpty());
//
//		//if (pruningPred==null) assert(new CompositionSpecCheck().test(aut, new ModalAutomaton<CALabel>(tr)));   post-condition
//
//		//in case of pruning if no final states are reachable return null
//		if (pruningPred!=null&& !tr.parallelStream()
//				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
//				.distinct().anyMatch(s->s.isFinalstate()))
//			return null;
//		else
//			return new ModalAutomaton<CALabel>(tr);
//	}
//
//	public boolean isFrontierEmpty() {
//		return this.frontier.isEmpty();
//	}
//
//	private static Integer computeSumPrincipal(ModalTransition<List<BasicState>,List<String>,CAState,CALabel> etra, Integer eind, List<ModalAutomaton<CALabel>> aut)
//	{
//		return IntStream.range(0, eind)
//				.map(i->aut.get(i).getRank())
//				.sum()+etra.getLabel().getOffererOrRequester();
//	}
//
//}
//
//
////END OF CLASS
//
//
//
//
//
//
////
////  **this is the composition method using Java 15, I translated back to Java 8 to use JML**
////
////	public static ModalAutomaton<CALabel> composition(List<ModalAutomaton<CALabel>> aut, Predicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> pruningPred, Integer bound)
////	{
////		//each transition of each ModalAutomaton<CALabel> in aut is associated with the corresponding index in aut
////		final class FMCATransitionIndex {//more readable than Entry
////			ModalTransition<List<BasicState>,List<String>,CAState,CALabel> tra;
////			Integer ind;
////			public FMCATransitionIndex(ModalTransition<List<BasicState>,List<String>,CAState,CALabel> tr, Integer i) {
////				this.tra=tr; //different principals may have equal transitions
////				this.ind=i;
////			}
////		}
////		
////		int rank=aut.stream()
////				.map(ModalAutomaton<CALabel>::getRank)
////				.collect(Collectors.summingInt(Integer::intValue));
////
////		List<CAState> initial = aut.stream()  
////				.flatMap(a -> a.getStates().stream())
////				.filter(CAState::isInitial)
////				.collect(Collectors.toList());
////		CAState initialstate = new CAState(initial);
////
////		Queue<Entry<List<CAState>,Integer>> toVisit = new ConcurrentLinkedQueue<Entry<List<CAState>,Integer>>(List.of(Map.entry(initial,0)));
////		ConcurrentMap<List<CAState>, CAState> operandstat2compstat = new ConcurrentHashMap<List<CAState>, CAState>(Map.of(initial, initialstate));//used to avoid duplicate target states 
////		Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> tr = new HashSet<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>();//transitions of the composed automaton to build
////		Set<List<CAState>> visited = new HashSet<List<CAState>>();
////		Queue<CAState> dontvisit = new ConcurrentLinkedQueue<CAState>();
////
////		do {
////			Entry<List<CAState>,Integer> sourceEntry=toVisit.remove(); //pop state to visit
////			if (visited.add(sourceEntry.getKey())&&sourceEntry.getValue()<bound) //if states has not been visited so far
////			{
////				List<CAState> source =sourceEntry.getKey();
////				CAState sourcestate= operandstat2compstat.get(source);
////				if (dontvisit.remove(sourcestate))
////					continue;//was target of a semicontrollable bad transition
////
////				List<FMCATransitionIndex> trans2index = IntStream.range(0,aut.size())
////						.mapToObj(i->aut.get(i)
////								.getForwardStar(source.get(i))
////								.parallelStream()
////								.map(t->new FMCATransitionIndex(t,i)))
////						.flatMap(Function.identity())
////						.collect(toList()); //indexing outgoing transitions of each operand, used for target states and labels
////
////				//firstly match transitions are generated
////				Map<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, List<Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>>> matchtransitions=
////						trans2index.parallelStream()
////						.collect(flatMapping(e -> trans2index.parallelStream()
////								.filter(ee->(e.ind<ee.ind) && CALabel.match(e.tra.getLabel(), ee.tra.getLabel()))
////								.flatMap(ee->{ 
////									List<CAState> targetlist =  new ArrayList<CAState>(source);
////									targetlist.set(e.ind, e.tra.getTarget());
////									targetlist.set(ee.ind, ee.tra.getTarget());
////									
////									ModalTransition<List<BasicState>,List<String>,CAState,CALabel> tradd=new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(sourcestate,
////											e.tra.getLabel().isOffer(), //since e.ind<ee.ind true if e is offer
////											(e.tra.getLabel().isOffer())?e.tra.getLabel().getAction():ee.tra.getLabel().getAction(),//offer action
////											operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)), 
////											e.tra.isNecessary()?e.tra.getModality():ee.tra.getModality());
////
////									return Stream.of((Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>>) 
////											Map.entry(e.tra, Map.entry(tradd,targetlist)),
////											(Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>>)//dummy, ee.tra is matched
////											Map.entry(ee.tra, Map.entry(tradd, (List<CAState>)new ArrayList<CAState>())));
////								}), 
////								groupingByConcurrent(Entry::getKey, 
////										mapping(Entry::getValue,toList()))//each principal transition can have more matches
////								));
////
////				//collecting match transitions and adding unmatched transitions
////				Set<Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>> trmap=
////						trans2index.parallelStream()
////						.filter(e->!matchtransitions.containsKey(e.tra))//transitions not matched
////						.collect(Collectors.collectingAndThen(
////								mapping(e->{List<CAState> targetlist = new ArrayList<CAState>(source);
////								targetlist.set(e.ind, e.tra.getTarget());
////								return 
////										Map.entry((!e.tra.getLabel().isMatch())?
////												new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(sourcestate,  
////														e.tra.getLabel().getAction(),
////														operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)),
////														e.tra.getModality())
////												:new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(sourcestate, 
////														e.tra.getLabel().getOfferer()<e.tra.getLabel().getRequester(), 
////														e.tra.getLabel().getAction(),
////														operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)),
////													 	e.tra.getModality()),
////												targetlist);},
////										toSet()),
////								trm->{trm.addAll(matchtransitions.values().parallelStream()//matched transitions
////										.flatMap(List::parallelStream)
////										.filter(e->(!e.getValue().isEmpty())) //no duplicates
////										.collect(toSet()));
////								return trm;}));
////
////				if (trmap.parallelStream()//don't visit target states if they are bad
////						.anyMatch(x->pruningPred!=null&&pruningPred.test(x.getKey())&&x.getKey().isUrgent()))
////				{
////					if (sourcestate.equals(initialstate))
////						return null;
////					continue;
////				}
////				else {//adding transitions, updating states
////					tr.addAll(trmap.parallelStream()
////							.filter(x->pruningPred==null||x.getKey().isNecessary()||pruningPred.negate().test(x.getKey()))//semicontrollable are not pruned
////							.collect(Collectors.teeing(
////									mapping((Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, List<CAState>> e)-> e.getKey(),toSet()), 
////									mapping((Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, List<CAState>> e)-> e.getValue(),toSet()), 
////									(trans,toVis)->{
////										toVisit.addAll(toVis.parallelStream()
////												.map(s->Map.entry(s,sourceEntry.getValue()+1))
////												.collect(toSet()));
////										if (pruningPred!=null)//avoid visiting targets of semicontrollable bad transitions
////											dontvisit.addAll(trans.parallelStream()
////													.filter(x->x.isSemiControllable()&&pruningPred.test(x))
////													.map(ModalTransition<List<BasicState>,List<String>,CAState,CALabel>::getTarget)
////													.collect(toList()));
////										return trans;
////									})));
////				}
////			}
////		} while (!toVisit.isEmpty());
////
////		int[][] finalstates = new int[rank][];
////		int pointer=0;
////		for (ModalAutomaton<CALabel> a : aut){
////			System.arraycopy(a.getFinalStatesofPrincipals(), 0, finalstates, pointer,a.getRank());
////			pointer+=a.getRank();
////		}
////		Set<CAState> states =visited.parallelStream()
////				.map(l->operandstat2compstat.get(l))
////				.collect(Collectors.toSet());
////		return new ModalAutomaton<CALabel>(rank, initialstate, finalstates, tr, states);
////	}
//
