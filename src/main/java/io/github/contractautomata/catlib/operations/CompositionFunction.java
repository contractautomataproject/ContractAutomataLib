package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.Ranked;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.state.AbstractState;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.operations.interfaces.TetraFunction;

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
 * Class implementing the Composition Function. <br>
 * The composition function supports an on-the-fly, bounded composition. <br>
 * It is possible to invoke a composition stopping at a given depth, and invoking again at a greater depth. <br>
 * In this case when reapplying the function, the previous states are stored and will not be generated again. <br>

 *
 *  This composition is a special type of synchronous product where synchronizations (called matches) are not broadcast, i.e., they only involve two principals. <br>
 *  The arguments of the composition may be automata having rank greater than 1, i.e., representing an ensemble of composed automata. <br>
 *  In this case, pre-existing matches inside the operands automata are preserved and are not rearranged. <br>
 *  By changing the order in which principal automata are composed, different results can be obtained, in other words, this composition is non-associative. <br>
 *  The associative composition is a special case where all operands are of rank 1.<br>
 *   * The formal definition of this composition is specified in Definition 5 of
 *       <ul>
 *       <li>
 *         Basile, D. et al., 2020.
 *         Controller synthesis of service contracts with variability. Science of Computer Programming, vol. 187, pp. 102344.
 *          (<a href="https://doi.org/10.1016/j.scico.2019.102344">https://doi.org/10.1016/j.scico.2019.102344</a>)
 *          </li>
 *          </ul>
 *
 *     @param <S1> the generic type of the content of states
 *     @param <S> the generic type of states, must be a subtype of <tt>State&lt;S1&gt;</tt>
 *     @param <L> the generic type of the labels, must be a subtype of <tt>Label&lt;Action&gt;</tt>
 *     @param <T> the generic type of a transitions, must be a subtype of <tt>ModalTransition&lt;S1,Action,S,L&gt;</tt>
 *     @param <A> the generic type of the automata, must be a subtype of <tt>Automaton&lt;S1,Action,S,T &gt;</tt>
 *
 * @author Davide Basile
 */

public class CompositionFunction<S1,S extends State<S1>,L extends Label<Action>,T extends ModalTransition<S1,Action,S,L>,A extends Automaton<S1,Action,S,T>>  implements IntFunction<A>{

	private final BiPredicate<L,L> match;
	private final Function<List<BasicState<S1>>,S> createState;
	private final TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition;
	private final Function<List<Action>,L> createLabel;
	private final Function<Set<T>,A> createAutomaton;

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

	//each transition of each MSCA in aut is associated with the corresponding index in aut
	final class TIndex {//more readable than Entry
		final T tra;
		final Integer ind;
		public TIndex(T tr, Integer i) {
			this.tra=tr; //different principals may have equal transitions
			this.ind=i;
		}
	}

	/**
	 * Constructor for a composition function.
	 *
	 * @param aut the list of automata to compose
	 * @param match a function taking two operands labels L and returning true if there is a match
	 * @param createState	a function with argument the list of operands state, and as result the composed state
	 * @param createTransition	a function taking as arguments the composed source state, composed label, composed target state and composed modality, and returns the created transition 
	 * @param createLabel a function taking as arguments a list of actions, and returns the composed label
	 * @param createAutomaton a function taking as argument the set of transitions of the composition, and returns the composed automaton
	 * @param pruningPred a predicate on labels useful for pruning unwanted transitions not to be explored during the computation of the composition
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
	 * This is one of the main functionalities of the library.
	 * It applies the composition function to compute the non-associative composition.
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
								.filter(x->x.isLazy() && pruningPred.test(x.getLabel()))
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
				.distinct().noneMatch(State::isFinalState))
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
											e.tra.isNecessary()?
													e.tra.getModality()
													:ee.tra.getModality());

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

	/**
	 * Returns true if no states are left to be generated, i.e., the whole depth of the composition has been generated. <br>
	 * If it returns false, this composition can be reapplied with a major depth to produce a composition with the frontier
	 * further pushed onwards. <br>
	 * When invoking again the composition, the previous information is stored to avoid recomputing the previously generated states. <br>
	 *
	 * @return true if no states are left to be generated, i.e., the whole depth of the composition has been generated.
	 */
	public boolean isFrontierEmpty() {
		return this.frontier.isEmpty();
	}

	/**
	 * Getter of the pruning predicate.
	 * @return the pruning predicate.
	 */
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
					if (i==e1.ind) return e1.tra.getLabel().getContent();
					else if (i==e2.ind) return e2.tra.getLabel().getContent();
					else return Collections.nCopies(aut.get(i).getRank(),new IdleAction());
				})
				.flatMap(List::stream)
				.collect(toList());
		return createLabel.apply(li);
	}

	private L shiftLabel(L lab, Integer rank, Integer shift){
		List<Action> l = new ArrayList<>(rank);
		l.addAll(Collections.nCopies(shift,new IdleAction()));
		l.addAll(lab.getContent());
		//it always hold that rank-l.size() is non-negative
		l.addAll(Collections.nCopies((int)rank.longValue()-l.size(), new IdleAction()));
		return createLabel.apply(l);
	}

}
