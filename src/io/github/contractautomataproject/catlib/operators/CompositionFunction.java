package io.github.contractautomataproject.catlib.operators;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.Ranked;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.label.action.Action;
import io.github.contractautomataproject.catlib.automaton.label.action.IdleAction;
import io.github.contractautomataproject.catlib.automaton.state.AbstractState;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;

/**
 * Class implementing the generic composition
 *
 * @author Davide Basile
 */


public class CompositionFunction<S1,S extends State<S1>,L extends Label<Action>,T extends ModalTransition<S1,Action,S,L>,A extends Automaton<S1,Action,S,T>>  implements IntFunction<A>{

	private final BiPredicate<L,L> match;
	private final Function<List<BasicState<S1>>,S> createState;
	private final TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition;
	private final Function<List<Action>,L> createLabel;
	private final Function<Set<T>,A> createAutomaton;


	//each transition of each MSCA in aut is associated with the corresponding index in aut
	final class TIndex {//more readable than Entry
		final T tra;
		final Integer ind;
		public TIndex(T tr, Integer i) {
			this.tra=tr; //different principals may have equal transitions
			this.ind=i;
		}
	}

	private final List<? extends Automaton<S1,Action,S,T>> aut;
	private final int rank;
	private final S initialState;
	private final Queue<Entry<List<S>,Integer>> toVisit;
	private final Queue<Entry<List<S>,Integer>> frontier;
	private final ConcurrentMap<List<S>, S> operandstat2compstat;
	private final Set<T> tr;
	private final Set<List<S>> visited;
	private final Queue<S> dontvisit;
	private final Predicate<L> pruningPred;

	/**
	 *
	 * @param aut the list of the automata to compose
	 * @param match a function taking two operands labels L and returning true if there is a match
	 * @param createState	a function with argument the list of operands state, and as result the composed state
	 * @param createTransition	a function taking as arguments the composed source state, composed label, composed target state and composed modality, and returns the created transition 
	 * @param createLabel a function taking as arguments two operands transitions (with corresponding indexes of the operands), the rank of the composed automaton, and returns the composed label
	 * @param createAutomaton a function taking as argument the set of transitions of the composition, and returns the composed automaton
	 *
	 */
	public CompositionFunction(List<A> aut,
							   BiPredicate<L,L> match,
							   Function<List<BasicState<S1>>,S> createState,
							   TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition,
							   Function<List<Action>,L> createLabel,
							   Function<Set<T>,A> createAutomaton,
							   Predicate<L> pruningPred)
	{
		this.aut= new ArrayList<>(aut);
		this.rank= aut.stream()
				.map(Ranked::getRank)
				.mapToInt(Integer::intValue).sum();

		List<S> initial = aut.stream()
				.flatMap(a -> a.getStates().stream())
				.filter(AbstractState::isInitial)
				.collect(Collectors.toList());

		initialState = createState.apply(flattenState(initial));
		this.toVisit = new ConcurrentLinkedQueue<>(List.of(new AbstractMap.SimpleEntry<>(initial, 0)));
		this.frontier = new ConcurrentLinkedQueue<>();
		this.operandstat2compstat = new ConcurrentHashMap<>();
		this.operandstat2compstat.put(initial, initialState);//used to avoid duplicate target states
		this.tr = new HashSet<>();//transitions of the composed automaton to build
		this.visited = new HashSet<>();
		this.dontvisit = new ConcurrentLinkedQueue<>();
		this.match=match;
		this.createState=createState;
		this.createLabel=createLabel;
		this.createTransition=createTransition;
		this.createAutomaton=createAutomaton;
		this.pruningPred=pruningPred;
	}

	/**
	 * This is the most important method of the tool, it computes the non-associative composition (of contract automata).
	 *
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

				if (badsourcestate && sourcestate.equals(initialState))
					return null;
				else if (!badsourcestate) {//adding transitions, updating states
					Set<T> trans= trmap.parallelStream()
							.filter(x -> pruningPred == null || x.getKey().isNecessary() || pruningPred.negate().test(x.getKey().getLabel()))
							.map(Entry::getKey).collect(toSet());
					tr.addAll(trans);

					if (pruningPred!=null)//avoid visiting targets of semicontrollable bad transitions
						dontvisit.addAll(trans.parallelStream()
								.filter(x->x.isLazy()&&pruningPred.test(x.getLabel()))
								.map(T::getTarget)
								.collect(toList()));

					toVisit.addAll(trmap.parallelStream()
							.filter(x -> pruningPred == null || x.getKey().isNecessary() || pruningPred.negate().test(x.getKey().getLabel()))
							.map(Entry::getValue).collect(toSet())
							.parallelStream()
							.map(s->new AbstractMap.SimpleEntry<>(s,sourceEntry.getValue()+1))
							.collect(toSet()));
				}
			}

		} while (!toVisit.isEmpty());

		//if (pruningPred==null) assert(new CompositionSpecCheck().test(aut, new MSCA(tr)));   post-condition

		//in case of pruning if no final states are reachable return null
		if (pruningPred!=null&& tr.parallelStream()
				.flatMap(t->Stream.of(t.getSource(),t.getTarget()))
				.distinct().noneMatch(AbstractState::isFinalState))
			return null;
		else
			return this.createAutomaton.apply(tr);
	}

	private Set<SimpleEntry<T,List<S>>> computeComposedForwardStar(List<TIndex> trans2index,List<S> source, S sourcestate){
		List<S> emptyList = new ArrayList<>();

		//firstly match transitions are generated
		Map<T, List<SimpleEntry<T,List<S>>>> matchTransitions=
				trans2index.parallelStream()
						.flatMap(e -> trans2index.parallelStream()
								.filter(ee->(e.ind<ee.ind) && match.test(e.tra.getLabel(), ee.tra.getLabel()))
								.flatMap(ee->{
									List<S> targetlist =  new ArrayList<>(source);
									targetlist.set(e.ind, e.tra.getTarget());
									targetlist.set(ee.ind, ee.tra.getTarget());

									T tradd=createTransition.apply(sourcestate,
											this.createLabel(e, ee),
											operandstat2compstat.computeIfAbsent(targetlist, s->createState.apply(flattenState(s))),
											e.tra.isNecessary()?e.tra.getModality():ee.tra.getModality());

									return Stream.of(new AbstractMap.SimpleEntry<>(e.tra,
													new AbstractMap.SimpleEntry<>(tradd,targetlist)),
											new AbstractMap.SimpleEntry<>(ee.tra, //dummy, the match transition is already stored by e.tra
													new AbstractMap.SimpleEntry<>(tradd, emptyList)));
								}))
						.collect(groupingByConcurrent(Entry::getKey,
								mapping(Entry::getValue,toList())));//each principal transition can have more matches


		//collecting match transitions and adding unmatched transitions
		Set<SimpleEntry<T,List<S>>> trmap = trans2index.parallelStream()
				.filter(e -> !matchTransitions.containsKey(e.tra))
				.map(e -> {
					List<S> targetlist = new ArrayList<>(source);
					targetlist.set(e.ind, e.tra.getTarget());
					return new SimpleEntry<>
							(createTransition.apply(sourcestate,
									this.shiftLabel(e.tra.getLabel(), rank,
											IntStream.range(0, e.ind)
													.map(i -> aut.get(i).getRank())
													.sum()),//shifting positions of label
									operandstat2compstat.computeIfAbsent(targetlist, s -> createState.apply(flattenState(s))),
									e.tra.getModality()),
									targetlist);
				}).collect(toSet());
		trmap.addAll(matchTransitions.values().parallelStream()//matched transitions
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

	private List<BasicState<S1>> flattenState(List<S> lstate){
		return lstate.stream()
				.map(State::getState)
				.reduce(new ArrayList<>(), (x,y)->{x.addAll(y); return x;});
	}

	private L createLabel(TIndex e1, TIndex e2){
		List<Action> li = IntStream.range(0, aut.size())
				.mapToObj(i->{
					if (i==e1.ind) return e1.tra.getLabel().getLabel();
					else if (i==e2.ind) return e2.tra.getLabel().getLabel();
					else return Collections.nCopies(aut.get(i).getRank(),new IdleAction());
				})
				.flatMap(List::stream)
				.collect(toList());
		return createLabel.apply(li);
	}

	private L shiftLabel(L lab, Integer rank, Integer shift){
		List<Action> l = new ArrayList<>(rank);
		l.addAll(Collections.nCopies(shift,new IdleAction()));
		l.addAll(lab.getLabel());
		if (rank-l.size()>0)
			l.addAll(Collections.nCopies((int)rank.longValue()-l.size(), new IdleAction()));
		return createLabel.apply(l);
	}

}

interface TetraFunction<T,U,V,W,Z> {
	Z apply(T arg1, U arg2, V arg3,W arg4);
}
