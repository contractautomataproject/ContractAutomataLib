package io.github.contractautomataproject.catlib.automaton;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;


/** 
 * Class representing a Modal  Automaton
 * 
 * @author Davide Basile
 *
 */
public class ModalAutomaton<L extends Label<List<String>>> extends Automaton<List<BasicState<String>>,List<String>, CAState, 
ModalTransition<List<BasicState<String>>,List<String>,CAState,L>>
{ 
	public ModalAutomaton(Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,L>> tr) 
	{
		super(tr);
		Set<CAState> states = this.getStates();
		if(states.stream()
				.anyMatch(x-> states.stream()
						.filter(y->x!=y && x.getState().equals(y.getState()))
						.count()!=0))
			throw new IllegalArgumentException("Transitions have ambiguous states (different objects for the same state).");
	}

	/**
	 * 
	 * @return a map where for each entry the key is the index of principal, and the value is its set of basic states
	 */
	public Map<Integer,Set<BasicState<String>>> getBasicStates()
	{

		return this.getStates().stream()
				.flatMap(cs->cs.getState().stream()
						.map(bs->new AbstractMap.SimpleEntry<Integer,BasicState<String>>(cs.getState().indexOf(bs),bs)))
				.collect(Collectors.groupingBy(Entry::getKey, Collectors.mapping(Entry::getValue, Collectors.toSet())));

	}

	@Override
	public String printFinalStates() {
		StringBuilder pr = new StringBuilder();
		for (int i=0;i<this.getRank();i++) {
			pr.append(Arrays.toString(
					this.getBasicStates().get(i).stream()
					.filter(BasicState<String>::isFinalstate)
					.map(BasicState<String>::getState)
					.toArray()));
		}
		return pr.toString();
	}
}