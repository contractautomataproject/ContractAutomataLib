package contractAutomata.automaton;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import contractAutomata.automaton.state.BasicState;
import contractAutomata.automaton.state.CAState;
import contractAutomata.automaton.transition.MSCATransition;


/** 
 * Class representing a Modal Service Contract Automaton
 * 
 * 
 * @author Davide Basile
 *
 */
public class MSCA extends Automaton<List<BasicState>, CAState, MSCATransition>
{ 
	public MSCA(Set<MSCATransition> tr) 
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
	 * @return a map where key is the index of principal, and value is its set of basic states
	 */
	public Map<Integer,Set<BasicState>> getBasicStates()
	{

		return this.getStates().stream()
				.flatMap(cs->cs.getState().stream()
						.map(bs->new AbstractMap.SimpleEntry<Integer,BasicState>(cs.getState().indexOf(bs),bs)))
				.collect(Collectors.groupingBy(Entry::getKey, Collectors.mapping(Entry::getValue, Collectors.toSet())));

	}


//	@Override
//	public final Set<CAState> getStates()
//	{		
//		CAState dummy=null;
//		return getStates(dummy);
//	}

	public CAState getInitial()
	{
		return (CAState) super.getInitial();
		
	}
	
	public Set<String> getUnsignedActions(){
		return this.getTransition().parallelStream()
		.map(t->t.getLabel().getUnsignedAction())
		.collect(Collectors.toSet());
	}


	@Override
	public String toString() {
		StringBuilder pr = new StringBuilder();
		int rank = this.getRank();
		pr.append("Rank: "+rank+"\n");

		pr.append("Initial state: " +this.getInitial().getState().toString()+"\n");
		pr.append("Final states: [");
		for (int i=0;i<rank;i++) {
			pr.append(Arrays.toString(
					this.getBasicStates().get(i).stream()
					.filter(BasicState::isFinalstate)
					.map(BasicState::getState)
					//.mapToInt(Integer::parseInt)
					.toArray()));
		}
		pr.append("]\n");
		pr.append("Transitions: \n");
		for (MSCATransition t : this.getTransition())
			pr.append(t.toString()+"\n");
		return pr.toString();
	}

}


//END OF THE CLASS







///**
//* the only initial state in the set of states is set to be the one equal to argument initial
//* @param initial the state to be set
//*/
//private void setInitialCA(CAState initial)
//{
//	Set<CAState> states=this.getStates();
//
//	states.parallelStream()
//	.filter(CAState::isInitial)
//	.forEach(x->x.setInitial(false));
//
//	CAState init = states.parallelStream()
//			.filter(x->x==initial)
//			.findAny().orElseThrow(IllegalArgumentException::new);
//
//	init.setInitial(true);
//}




