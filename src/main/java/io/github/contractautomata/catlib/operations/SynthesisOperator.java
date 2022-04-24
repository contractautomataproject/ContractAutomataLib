package io.github.contractautomata.catlib.operations;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.transition.Transition;
import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.operations.interfaces.TriPredicate;

/**
 * Class implementing the abstract synthesis operator.<br>
 * The synthesis operation is an automatic refinement of an automaton to a refined one where <br>
 * given conditions hold. <br>
 * Contract automata are equipped with two specific conditions: agreement and strong agreement, <br>
 * detailed in the package requirements. <br>
 * The synthesis must also take into account when an action is controllable or uncontrollable. <br>
 *
 * <p>
 * The synthesis is an iterative procedure that at each step i   <br>
 * updates incrementally a set of states Ri containing the bad states, i.e. those states that  <br>
 * cannot prevent a forbidden state to be eventually reached, and refines an automaton Ki. <br>
 * The algorithm starts with an automaton K0 equal to A and a set R0 containing all <br>
 * dangling states in A, where a state is dangling if it cannot be reached from the initial state <br>
 * or cannot reach a final state. At each step i, the algorithm prunes from Ki−1 in a backwards  <br>
 * fashion transitions with target state in Ri−1 or forbidden source state. The set Ri is obtained  <br>
 * by adding to Ri−1 dangling states in Ki and source states of uncontrollable transitions of A   <br>
 * with target state in Ri−1. When no more updates are possible, the algorithm terminates.  <br>
 * Termination is ensured since A is finite-state and has a finite set of transitions, and at each  <br>
 * step the subsets of its states Ri cannot decrease while the set of its transitions TKi  cannot <br>
 * increase. At its termination the algorithm returns the pair (Ks, Rs). <br>
 * We have that the result is empty, if the initial state of A is in Rs; otherwise, the result is <br>
 * obtained from Ks by removing the states Rs.
 * </p>
 *
 * <p>
 * The abstract synthesis operations generalises the  <br>
 * other synthesis operations by abstracting away the conditions under which a transition is   <br>
 * pruned or a state is deemed bad, thus encapsulating and extrapolating the notion of controllability and  <br>
 * safety. These two conditions, called pruning predicate (φp) and forbidden predicate (φf )   <br>
 * are parameters to be instantiated by the corresponding instance of the synthesis algorithm   <br>
 * (e.g. orchestration or choreography). Predicate φp is used for selecting the transitions to be  <br>
 * pruned. Depending on the specific instance, non-local information about the automaton or  <br>
 * the set of bad states is needed by φp. Therefore, φp takes as input the current transition  <br>
 * to be checked, the automaton, and the set of bad states. If φp evaluates to true, then the  <br>
 * corresponding transition will be pruned. Predicate φf is used for deciding whether a state  <br>
 * becomes bad. The input parameters are the same as φp. However, φf only inspects necessary  <br>
 * transitions. If φf evaluates to true, then the source state is deemed bad and added to    <br>
 * the set of bad states.  <br>
 *</p>
 *
 * The formal definition is given in Definition 5.1 of:
 * <ul>
 *      <li>Basile, D., et al., 2020.
 *      Synthesis of Orchestrations and Choreographies: Bridging the Gap between Supervisory Control and Coordination of Services. Logical Methods in Computer Science, vol. 16(2), pp. 9:1 - 9:29.
 *       (<a href="https://doi.org/10.23638/LMCS-16(2:9)2020">https://doi.org/10.23638/LMCS-16(2:9)2020</a>)</li>
 * </ul>
 *
 * @author Davide Basile
 *
 */
public class SynthesisOperator<S1,L1,S extends State<S1>,
		L extends Label<L1>,T extends ModalTransition<S1,L1,S,L>, A extends Automaton<S1,L1,S,T>> implements UnaryOperator<A>{

	private Map<S,Boolean> reachable;
	private Map<S,Boolean> successful;
	private final TriPredicate<T, Set<T>, Set<S>> pruningPred;
	private final TriPredicate<T, Set<T>, Set<S>> forbiddenPred;
	private final Predicate<L> req;
	private final Function<Set<T>,A> createAut;

	/**
	 * Constructor for the synthesis operator.
	 *
	 * @param pruningPredicate  the pruning predicate 
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param createAut the constructor of the automaton
	 */
	public SynthesisOperator(TriPredicate<T, Set<T>, Set<S>> pruningPredicate,
							 TriPredicate<T, Set<T>, Set<S>> forbiddenPredicate,
							 Predicate<L> req,
							 Function<Set<T>,A> createAut) {
		super();
		this.pruningPred = (x,t,bad) ->
				bad.contains(x.getTarget())
						|| !req.test(x.getLabel())
						|| pruningPredicate.test(x, t, bad);
		this.forbiddenPred = (x,t,bad) -> !t.contains(x)
				&&  forbiddenPredicate.test(x, t, bad);
		this.req=req;
		this.createAut=createAut;
	}

	/**
	 * Constructor for the synthesis operator. The pruning predicate is instantiated to always return false.
	 *
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param createAut  the constructor of the automaton
	 */
	public SynthesisOperator(TriPredicate<T, Set<T>, Set<S>> forbiddenPredicate,
							 Predicate<L> req,
							 Function<Set<T>,A> createAut) {
		this((x,t,bad) -> false, forbiddenPredicate,req, createAut);
	}

	/**
	 * Getter of the requirement.
	 * @return the requirement.
	 */
	public Predicate<L> getReq() {
		return req;
	}

	/**
	 * Getter of the function for creating an automaton.
	 * @return  the function for creating an automaton.
	 */
	public Function<Set<T>, A> getCreateAut() {
		return createAut;
	}

	/**
	 * This method applies the synthesis operator to aut.
	 * @param aut  the automaton to which the synthesis operation is applied.
	 * @return the synthesised automaton.
	 *
	 */
	@Override
	public A apply(A aut) {

		class Pair{
			final Set<T> tr;
			final Set<S> s;
			Pair(Set<T> tr, Set<S> s) {
				this.tr = tr; this.s = s;
			}
		}

		if (aut==null)
			throw new IllegalArgumentException();

		final Set<T> trbackup = aut.getTransition();
		final Set<S> statesbackup= aut.getStates();
		final S init = aut.getInitial();
		Pair seed = new Pair(aut.getTransition(), new HashSet<>(getDanglingStates(aut.getTransition(), statesbackup,init)));

		Pair fixpoint = Stream.iterate(seed, pair-> {
					Pair pre = new Pair(new HashSet<>(pair.tr),new HashSet<>(pair.s));

					//next function embedded into hasnext
					if (pair.tr.removeAll(pre.tr.parallelStream()
							.filter(x->pruningPred.test(x,pre.tr, pre.s))
							.collect(Collectors.toSet()))) //Ki
						pair.s.addAll(getDanglingStates(pair.tr, statesbackup,init));

					pair.s.addAll(trbackup.parallelStream()
							.filter(x->forbiddenPred.test(x,pre.tr, pre.s))
							.map(Transition::getSource)
							.collect(Collectors.toSet())); //Ri

					return (pre.tr.size()!=pair.tr.size()
							|| pre.s.size() != pair.s.size());//hasnext
				},p->p)
				.reduce((first,second)->new Pair(second.tr,second.s))
				.orElse(seed);

		if (fixpoint.s.contains(init)
				||fixpoint.tr.isEmpty())
			return null;

		//remove dangling transitions
		fixpoint.tr.removeAll(fixpoint.tr.parallelStream()
				.filter(x->!reachable.get(x.getSource()))
				.collect(Collectors.toSet()));

		return createAut.apply(fixpoint.tr);
	}

	/**
	 * @return	states who do not reach a final state or are unreachable
	 */
	private Set<S> getDanglingStates(Set<T> tr, Set<S> states, S initial)
	{
		//all states' flags are reset
		this.reachable=states.parallelStream()
				.collect(Collectors.toMap(x->x, x->false));
		this.successful=states.parallelStream()
				.collect(Collectors.toMap(x->x, x->false));

		//set reachable
		forwardVisit(tr, initial);

		//set successful
		states.forEach(x-> {
			if (x.isFinalState()&& Boolean.TRUE.equals(this.reachable.get(x)))
				backwardVisit(tr,x);});

		return states.parallelStream()
				.filter(x->!(reachable.get(x)&&this.successful.get(x)))
				.collect(Collectors.toSet());
	}

	private void forwardVisit(Set<T> tr, S currentstate)
	{
		this.reachable.put(currentstate, true);
		tr.parallelStream()
				.filter(x->x.getSource().equals(currentstate)) //forward star
				.forEach(x->{
					if (Boolean.FALSE.equals(this.reachable.get(x.getTarget())))
						forwardVisit(tr,x.getTarget());
				});
	}



	private void backwardVisit(Set<T> tr, S currentstate)
	{
		this.successful.put(currentstate, true);

		tr.stream()
				.filter(x->x.getTarget().equals(currentstate))// backward star
				.forEach(x->{
					if (Boolean.FALSE.equals(this.successful.get(x.getSource())))
						backwardVisit(tr, x.getSource());
				});
	}
}