package io.github.contractautomataproject.catlib.automaton.state;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Class representing a state of a Contract Automaton
 * 
 * @author Davide Basile
 * 
 */
public class CAState extends State<List<BasicState<String>>> {

	/**
	 * Construct a new CAState from
	 * - a list of BasicStates
	 * - a list of CAStates by flattening them into  a list of basic states
	 * @param lstate states the list of castates or basicstates
	 */
	@SuppressWarnings("unchecked")
	public <T extends State<?>> CAState(List<T> lstate)
	{
		super((lstate.get(0) instanceof CAState)?
				lstate.stream()
				.map(CAState.class::cast)
				.map(CAState::getState)
				.reduce(new ArrayList<>(), (x,y)->{x.addAll(y); return x;})
				:(lstate.get(0) instanceof BasicState<?>) && (lstate.get(0).getState() instanceof String)?
						lstate.stream()
						.map(s-> (BasicState<String>)s)
						.collect(Collectors.toList())
						:null);
	}

	@Override
	public Integer getRank() {
		return this.getState().size();
	}

	@Override
	public boolean isInitial() {
		return this.getState().stream().allMatch(BasicState<String>::isInitial);
	}

	@Override
	public boolean isFinalstate() {
		return this.getState().stream().allMatch(BasicState<String>::isFinalstate);
	}

	@Override
	public  List<BasicState<String>> getState() {
		return new ArrayList<>(super.getState());
	}

	@Override
	public String toString()
	{
		return this.getState().toString();
	}

	// equals could cause errors of duplication of states in transitions to go undetected. 	

}