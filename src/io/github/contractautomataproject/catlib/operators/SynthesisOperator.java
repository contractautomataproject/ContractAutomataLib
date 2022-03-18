package io.github.contractautomataproject.catlib.operators;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.transition.ModalTransition;
import io.github.contractautomataproject.catlib.transition.Transition;

/**
 * Class implementing the abstract synthesis operator
 * 
 * @author Davide Basile
 *
 */
public class SynthesisOperator<S1,L1,S extends State<S1>,
L extends Label<L1>,T extends ModalTransition<S1,L1,S,L>, A extends Automaton<S1,L1,S,T>> implements UnaryOperator<A>{

	private Map<S,Boolean> reachable;
	private Map<S,Boolean> successful;
	private TriPredicate<T, Set<T>, Set<S>> pruningPred;
	private final TriPredicate<T, Set<T>, Set<S>> forbiddenPred;
	private final Predicate<L> req;
	private final Function<Set<T>,A> createAut;

	/**
	 * 
	 * @param pruningPredicate  the pruning predicate 
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 */
	public SynthesisOperator(TriPredicate<T, Set<T>, Set<S>> pruningPredicate,
			TriPredicate<T, Set<T>, Set<S>> forbiddenPredicate, 
			Predicate<L> req, 
			Function<Set<T>,A> createAut) {
		super();
		this.pruningPred = (x,t,bad) -> bad.contains(x.getTarget())|| !req.test(x.getLabel()) || pruningPredicate.test(x, t, bad);
		this.forbiddenPred = (x,t,bad) -> !t.contains(x) && forbiddenPredicate.test(x, t, bad);
		this.req=req;
		this.createAut=createAut;
	}


	/**
	 * This constructor does not use any pruning predicate
	 * 
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
	 */
	public SynthesisOperator(TriPredicate<T, Set<T>, Set<S>> forbiddenPredicate, 
			Predicate<L> req, 
			Function<Set<T>,A> createAut) {
		this((x,t,bad) -> false, forbiddenPredicate,req, createAut);
	}


	public void setPruningPred(TriPredicate<T, Set<T>, Set<S>> pruningPredicate, Predicate<L> req) {
		this.pruningPred =  (x,t,bad) -> bad.contains(x.getTarget()) || !req.test(x.getLabel()) || pruningPredicate.test(x, t, bad);
	}

	public Predicate<L> getReq() {
		return req;
	}

	/** 
	 * invokes the synthesis
	 * @param arg1 the plant automaton to which the synthesis is performed
	 * @return the synthesised automaton
	 * 
	 */
	@Override
	public A apply(A aut) {
		
		class Pair{
			Set<T> tr; 
			Set<S> s;
			public Pair(Set<T> tr, Set<S> s) {
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

			return (pre.tr.size()!=pair.tr.size() || pre.s.size() != pair.s.size());//hasnext
		},p->p)
				.reduce((first,second)->new Pair(second.tr,second.s))
				.orElse(seed);

		if (fixpoint==null || fixpoint.s.contains(init)||fixpoint.tr.isEmpty())
			return null;

		//remove dangling transitions
		fixpoint.tr.removeAll(fixpoint.tr.parallelStream()
				.filter(x->!reachable.get(x.getSource())||!successful.get(x.getTarget()))
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
			if (x.isFinalstate()&& Boolean.TRUE.equals(this.reachable.get(x)))
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