package io.github.contractautomataproject.catlib.operators;

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
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.Ranked;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.AbstractState;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

/**
 * Class implementing the generic composition
 * 
 * @author Davide Basile
 */


public class CompositionFunction<S1,L1,S extends State<S1>,L extends Label<L1>,T extends ModalTransition<S1,L1,S,L>,A extends Automaton<S1,L1,S,T>>  implements IntFunction<A>{

	private final BiPredicate<L,L> match;
	private final Function<List<BasicState<S1>>,S> createState;
	private final TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition;
	private final TriFunction<TIndex,TIndex,Integer,L> createLabel;
	private final TriFunction<L,Integer,Integer, L> shiftLabel;
	private final Function<Set<T>,A> createAutomaton;


	//each transition of each MSCA in aut is associated with the corresponding index in aut
	final class TIndex {//more readable than Entry
		T tra;
		Integer ind;
		public TIndex(T tr, Integer i) {
			this.tra=tr; //different principals may have equal transitions
			this.ind=i;
		}
	}

	private final List<? extends Automaton<S1,L1,S,T>> aut;
	private int rank;
	private List<S> initial;
	private S initialstate;
	private Queue<Entry<List<S>,Integer>> toVisit;
	private Queue<Entry<List<S>,Integer>> frontier;
	private ConcurrentMap<List<S>, S> operandstat2compstat;
	private Set<T> tr;
	private Set<List<S>> visited;
	private Queue<S> dontvisit;
	private Predicate<L> pruningPred;

	/**
	 * 
	 * @param aut the list of the automata to compose
	 * @param computeRank a function taking a list of ranked elements (operands) and returning the rank of the composition
	 * @param match a function taking two operands labels L and returning true if there is a match
	 * @param createState	a function with argument the list of operands state, and as result the composed state
	 * @param createTransition	a function taking as arguments the composed source state, composed label, composed target state and composed modality, and returns the created transition 
	 * @param createLabel a function taking as arguments two operands transitions (with corresponding indexes of the operands), the rank of the composed automaton, and returns the composed label
	 * @param shiftLabel when interleaving a transition of an operand, it could be necessary in the composed label to shift the position of such interleaved label. shiftLabel is 
	 *        a function taking as arguments the label to shift of one operand, the rank of the composed automaton, the positions to shift (positive is to the right), and returns the shifted label
	 * @param createAutomaton a function taking as argument the set of transitions of the composition, and returns the composed automaton
	 * 
	 */
	public CompositionFunction(List<A> aut,  
			ToIntFunction<List<? extends Ranked>> computeRank,
			BiPredicate<L,L> match, 
			Function<List<BasicState<S1>>,S> createState, 
			TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition, 
			TriFunction<TIndex,TIndex,Integer,L> createLabel,
			TriFunction<L,Integer,Integer, L> shiftLabel, 
			Function<Set<T>,A> createAutomaton,
			Predicate<L> pruningPred)
	{
		this.aut= new ArrayList<>(aut);
		this.rank=computeRank.applyAsInt(aut.stream()
				.map(Ranked.class::cast)
				.collect(Collectors.toList()));

		this.initial = aut.stream()  
				.flatMap(a -> a.getStates().stream())
				.filter(AbstractState::isInitial)
				.collect(Collectors.toList());

		this.initialstate = createState.apply(flattenState(initial));
		this.toVisit = new ConcurrentLinkedQueue<>(Arrays.asList(new AbstractMap.SimpleEntry<>(initial, 0)));
		this.frontier = new ConcurrentLinkedQueue<>();
		this.operandstat2compstat = new ConcurrentHashMap<>();
		this.operandstat2compstat.put(initial, initialstate);//used to avoid duplicate target states 
		this.tr = new HashSet<>();//transitions of the composed automaton to build
		this.visited = new HashSet<>();
		this.dontvisit = new ConcurrentLinkedQueue<>();
		this.match=match;
		this.createState=createState;
		this.createLabel=createLabel;
		this.createTransition=createTransition;
		this.shiftLabel=shiftLabel;
		this.createAutomaton=createAutomaton;
		this.pruningPred=pruningPred;
	}

	/**
	 * This is the most important method of the tool, it computes the non-associative composition (of contract automata).
	 * 
	 * @param pruningPred  the invariant that all transitions must satisfy
	 * @param bound  the bound on the depth of the visit
	 * @return  the composed automaton
	 */
	@Override
	public A apply(int bound)
	{
		if (!frontier.isEmpty())//in case this method is called more than once, a potential frontier can be restored
		{
			toVisit.addAll(frontier);
			frontier.clear();
		}

		do {
			Entry<List<S>,Integer> sourceEntry=toVisit.remove(); //pop state to visit
			if (sourceEntry.getValue()>=bound)  //if bound is reached store the frontier for a next call
				frontier.add(sourceEntry);
			else if (visited.add(sourceEntry.getKey())) //if the state has not been visited so far and it is within bound
			{
				List<S> source =sourceEntry.getKey();
				S sourcestate= operandstat2compstat.get(source);

				if (dontvisit.remove(sourcestate))
					continue;//was target of a semicontrollable bad transition

				List<TIndex> trans2index = IntStream.range(0,aut.size())
						.mapToObj(i->aut.get(i)
								.getForwardStar(source.get(i))
								.parallelStream()
								.map(t->new TIndex(t,i)))
						.flatMap(Function.identity())
						.collect(toList()); //indexing outgoing transitions of each operand, used for target states and labels

				//				assert(trans2index.parallelStream()
				//						.filter(e -> e.tra.getRank() != aut.get(e.ind).rank)
				//						.count()==0);

				Set<SimpleEntry<T,List<S>>> trmap = computeComposedForwardStar(trans2index,source,sourcestate);

				//if source state is bad then don't visit target states
				boolean badsourcestate = pruningPred!=null && trmap.parallelStream()
						.anyMatch(x->pruningPred.test(x.getKey().getLabel())&&x.getKey().isUrgent());

				if (badsourcestate && sourcestate.equals(initialstate))
					return null;
				else if (!badsourcestate) {//adding transitions, updating states
					Set<T> trans=trmap.parallelStream()
							.filter(x->pruningPred==null||x.getKey().isNecessary()||pruningPred.negate().test(x.getKey().getLabel()))//semicontrollable are not pruned
							.collect(mapping(Entry::getKey,toSet()));
					tr.addAll(trans);

					if (pruningPred!=null)//avoid visiting targets of semicontrollable bad transitions
						dontvisit.addAll(trans.parallelStream()
								.filter(x->x.isLazy()&&pruningPred.test(x.getLabel()))
								.map(T::getTarget)
								.collect(toList()));

					toVisit.addAll(trmap.parallelStream()
							.filter(x->pruningPred==null||x.getKey().isNecessary()||pruningPred.negate().test(x.getKey().getLabel()))//semicontrollable are not pruned
							.collect(mapping(Entry::getValue,toSet()))
							.parallelStream()
							.map(s->new AbstractMap.SimpleEntry<List<S>,Integer>(s,sourceEntry.getValue()+1))
							.collect(toSet()));
				}
			}

		} while (!toVisit.isEmpty());

		//if (pruningPred==null) assert(new CompositionSpecCheck().test(aut, new MSCA(tr)));   post-condition

		//in case of pruning if no final states are reachable return null
		if (pruningPred!=null&& tr.parallelStream()
				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
				.distinct().noneMatch(AbstractState::isFinalstate))
			return null;
		else
			return this.createAutomaton.apply(tr);
	}

	private Set<SimpleEntry<T,List<S>>> computeComposedForwardStar(List<TIndex> trans2index,List<S> source, S sourcestate){
		List<S> emptyList = new ArrayList<>();

		//firstly match transitions are generated
		Map<T, List<SimpleEntry<T,List<S>>>> matchtransitions=
				trans2index.parallelStream()
				.flatMap(e -> trans2index.parallelStream()
						.filter(ee->(e.ind<ee.ind) && match.test(e.tra.getLabel(), ee.tra.getLabel()))
						.flatMap(ee->{ 
							List<S> targetlist =  new ArrayList<>(source);
							targetlist.set(e.ind, e.tra.getTarget());
							targetlist.set(ee.ind, ee.tra.getTarget());

							T tradd=createTransition.apply(sourcestate,	
									this.createLabel.apply(e, ee, rank),
									operandstat2compstat.computeIfAbsent(targetlist, s->createState.apply(flattenState(s))), 
									e.tra.isNecessary()?e.tra.getModality():ee.tra.getModality());

							return Stream.of(new AbstractMap.SimpleEntry<>(e.tra, 
									new AbstractMap.SimpleEntry<>(tradd,targetlist)),
									new AbstractMap.SimpleEntry<>(ee.tra, //dummy, ee.tra is matched
											new AbstractMap.SimpleEntry<>(tradd, emptyList)));
						}))
				.collect(groupingByConcurrent(Entry::getKey, 
						mapping(Entry::getValue,toList())));//each principal transition can have more matches


		//collecting match transitions and adding unmatched transitions
		Set<SimpleEntry<T,List<S>>> trmap = trans2index.parallelStream()
				.filter(e->!matchtransitions.containsKey(e.tra))//transitions not matched
				.collect(mapping(e->{List<S> targetlist = new ArrayList<>(source);
				targetlist.set(e.ind, e.tra.getTarget());
				return 	new AbstractMap.SimpleEntry<>
				(createTransition.apply(sourcestate,
						shiftLabel.apply(e.tra.getLabel(),rank, //change here if you would like to preserve the CM constraints
								IntStream.range(0, e.ind)
								.map(i->aut.get(i).getRank())
								.sum()),//shifting positions of label
						operandstat2compstat.computeIfAbsent(targetlist, s->createState.apply(flattenState(s))),
						e.tra.getModality()),
						targetlist);},
						toSet()));
		trmap.addAll(matchtransitions.values().parallelStream()//matched transitions
				.flatMap(List::parallelStream)
				.filter(e->(!e.getValue().isEmpty())) //no duplicates
				.collect(toSet()));

		return trmap;
	}

	public boolean isFrontierEmpty() {
		return this.frontier.isEmpty();
	}

	public Predicate<L> getPruningPred() {
		return pruningPred;
	}

	public List<BasicState<S1>> flattenState(List<S> lstate){
		return lstate.stream()
				.map(State::getState)
				.reduce(new ArrayList<>(), (x,y)->{x.addAll(y); return x;});
	}


}

interface TetraFunction<T,U,V,W,Z> {
	public Z apply(T arg1, U arg2, V arg3,W arg4);
}
