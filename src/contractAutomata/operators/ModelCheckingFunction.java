package contractAutomata.operators;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import contractAutomata.automaton.Automaton;
import contractAutomata.automaton.MSCA;
import contractAutomata.automaton.label.Label;
import contractAutomata.automaton.state.BasicState;
import contractAutomata.automaton.state.CAState;
import contractAutomata.automaton.state.State;
import contractAutomata.automaton.transition.MSCATransition;
import contractAutomata.automaton.transition.Transition;

/**
 * Class implementing the model checking function
 * 
 * @author Davide Basile
 *
 */
public class ModelCheckingFunction implements BiFunction<MSCA,Automaton<String,BasicState,Transition<String,BasicState,Label>>,Set<CAState>>{
	private final int bound;

	public ModelCheckingFunction() {
		this.bound=Integer.MAX_VALUE;
	}

	/**
	 * 
	 * @param bound the  bound of bounded model checking
	 */
	public ModelCheckingFunction(Integer bound) {
		this.bound=bound;
	}

	/**
	 * @param aut the plant automaton to verify
	 * @param prop the automaton expressing the property to verify
	 * @return the set of states violating prop
	 */
	@Override
	public Set<CAState> apply(MSCA aut, Automaton<String,BasicState,Transition<String,BasicState,Label>> prop)
	{
		prop.getInitial();

		Queue<SimpleEntry<SimpleEntry<CAState, BasicState>,Integer>> toVisit = new ConcurrentLinkedQueue<SimpleEntry<SimpleEntry<CAState, BasicState>,Integer>>(Arrays.asList(
				new AbstractMap.SimpleEntry<>(
						new AbstractMap.SimpleEntry<>(aut.getInitial(), prop.getInitial()),0)));
		Set<CAState> visited = new HashSet<CAState>();
		Set<CAState> dontvisit = new HashSet<>();

		do {
			SimpleEntry<SimpleEntry<CAState, BasicState>,Integer> sourceEntry=toVisit.remove(); //pop state to visit
			CAState sourceAut =sourceEntry.getKey().getKey();
			if (!dontvisit.contains(sourceAut)&&visited.add(sourceAut)&&sourceEntry.getValue()<bound) //if states has not been visited so far
			{
				State<?> sourceProp =sourceEntry.getKey().getValue();

				Map<MSCATransition, Set<Transition<String,BasicState,Label>>> matches = 
						aut.getForwardStar(sourceAut).stream()
						.map(t1 ->	new AbstractMap.SimpleEntry<>(t1, prop.getForwardStar(sourceProp).stream()
								.filter(t2->t2.getLabel().match(t1.getLabel()))
								.collect(Collectors.toSet())))
						.collect(Collectors.toMap(Entry::getKey, Entry::getValue));

				toVisit.addAll(matches.entrySet().stream()
						.filter(e->!e.getValue().isEmpty())
						.flatMap(e-> e.getValue().stream().map(tr2->
						new AbstractMap.SimpleEntry<>(e.getKey().getTarget(),tr2.getTarget())))				
						.map(e-> new AbstractMap.SimpleEntry<>(e,sourceEntry.getValue()+1))
						.collect(Collectors.toSet()));
				
				dontvisit.addAll(matches.entrySet().stream()
						.filter(e->e.getValue().isEmpty())
						.map(e->e.getKey().getTarget())
						.collect(Collectors.toSet()));
			}
		} while (!toVisit.isEmpty());

		return dontvisit;
	}
}