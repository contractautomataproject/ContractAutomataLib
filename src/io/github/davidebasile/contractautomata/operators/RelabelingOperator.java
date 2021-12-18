package io.github.davidebasile.contractautomata.operators;

import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.MSCATransition;

/**
 * Class implementing the relabeling operator
 * @author Davide Basile
 *
 */
public class RelabelingOperator implements UnaryOperator<MSCA> {
	UnaryOperator<String> relabel;
	
	public RelabelingOperator() {
		relabel = s->s;
	}
	
	/**
	 * 
	 * @param relabel the relabeling operator to apply to each action in a label
	 */
	public RelabelingOperator(UnaryOperator<String> relabel) {
		this.relabel=relabel;
	}
	
	@Override
	public MSCA apply(MSCA aut)
	{	
		Map<BasicState,BasicState> clonedstate = aut.getStates().stream()
				.flatMap(x->x.getState().stream())
				.distinct()
				.collect(Collectors.toMap(Function.identity(), 
						s->new BasicState(relabel.apply(s.getState()),
								s.isInitial(),s.isFinalstate())));
		
		Map<CAState,CAState> clonedcastates  = aut.getStates().stream()
				.collect(Collectors.toMap(Function.identity(), 
						x->new CAState(x.getState().stream()
								.map(s->clonedstate.get(s))
								.collect(Collectors.toList())
								//,x.getX(),x.getY()
								)));

		return new MSCA(aut.getTransition().stream()
				.map(t->new MSCATransition(clonedcastates.get(t.getSource()),
						getCopy(t.getLabel()),
						clonedcastates.get(t.getTarget()),
						t.getModality()))
				.collect(Collectors.toSet()));
	}
	

	private CALabel getCopy(CALabel la) {
		if (la.isMatch())
			return new CALabel(la.getRank(),la.getOfferer(),la.getRequester(),la.getAction());
		else 
			return new CALabel(la.getRank(),(la.isOffer())?la.getOfferer():la.getRequester(),la.getAction());
	}


}
