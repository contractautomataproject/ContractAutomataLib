package io.github.davidebasile.contractautomata.operators;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.ModalTransition;

/**
 * Class implementing the relabeling operator
 * @author Davide Basile
 *
 */
public class RelabelingOperator<L extends Label<List<String>>> implements UnaryOperator<ModalAutomaton<L>> {
	private UnaryOperator<String> relabel;

	private Function<List<String>,L> createLabel;

	public RelabelingOperator(Function<List<String>,L> createLabel) {
		this.createLabel=createLabel;
		this.relabel = s->s;
	}

	/**
	 * 
	 * @param relabel the relabeling operator to apply to each basicstate
	 */
	public RelabelingOperator(Function<List<String>,L> createLabel, UnaryOperator<String> relabel) {
		this.createLabel=createLabel;
		this.relabel=relabel;
	}

	@Override
	public ModalAutomaton<L> apply(ModalAutomaton<L> aut)
	{	
		if (aut.getTransition().isEmpty())
			throw new IllegalArgumentException();

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
								)));


		return new ModalAutomaton<L>(aut.getTransition().stream()
				.map(t->new ModalTransition<List<BasicState>,List<String>,CAState,L>(clonedcastates.get(t.getSource()),
						createLabel.apply(t.getLabel().getAction()),
						clonedcastates.get(t.getTarget()),
						t.getModality()))
				.collect(Collectors.toSet()));
	}
}


//if (createLabel==null) {
//	L lab=aut.getTransition().iterator().next().getLabel();
//	createLabel = arg -> {
//		try {
//			Constructor<? extends Label> con= lab.getClass().getConstructor(lab.getAction().getClass());
//			return (L)con.newInstance(arg);
//		} catch (Exception e) {
//			RuntimeException re = new RuntimeException();
//			re.addSuppressed(e);
//			throw re;
//		} 
//	};
//}



//	private CALabel getCopy(CALabel la) {
//		if (la.isMatch())
//			return new CALabel(la.getRank(),la.getOfferer(),la.getRequester(),la.getTheAction(),la.getCoAction());
//			//TODO check I removed this constructor call return new CALabel(la.getRank(),la.getOfferer(),la.getRequester(),la.getAction());
//		else 
//			return new CALabel(la.getRank(),(la.isOffer())?la.getOfferer():la.getRequester(),la.getTheAction());
//	}
