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
	public String toString() {
		StringBuilder pr = new StringBuilder();
		int rank = this.getRank();
		pr.append("Rank: "+rank+System.lineSeparator());
		pr.append("Initial state: " +printState(this.getInitial())+System.lineSeparator());
		pr.append("Final states: ["+printFinalStates()+"]"+System.lineSeparator());
		pr.append("Transitions: "+System.lineSeparator());
		this.getTransition().stream()
		.sorted((t1,t2)->t1.toString().compareTo(t2.toString()))
		.forEach(t->pr.append(printTransition(t)+System.lineSeparator()));
		return pr.toString();
	}
	
	private List<String> printState(CAState s) {
		return s.getState().stream()
				.map(BasicState<String>::getState)
				.collect(Collectors.toList());
	}
	
	private String printFinalStates() {
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
	
	private String printTransition(ModalTransition<List<BasicState<String>>,List<String>,CAState,L> tr)
	{
		String str = "("+printState(tr.getSource())+","+tr.getLabel().toString()+","+printState(tr.getTarget())+")";
		if (tr.getModality()==ModalTransition.Modality.URGENT)
			return "!U"+str;
		else if (tr.getModality()==ModalTransition.Modality.LAZY)	
			return "!L"+str;
		else
			return str;
	}
}