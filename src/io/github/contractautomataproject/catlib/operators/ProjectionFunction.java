package io.github.contractautomataproject.catlib.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.CMLabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

/**
 * Class implementing the projection function
 * 
 * @author Davide Basile
 *
 */
public class ProjectionFunction implements TriFunction<ModalAutomaton<CALabel>,Integer,Function<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>, Integer>,ModalAutomaton<CALabel>> {
	BiFunction<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>,Integer,CALabel> createLabel;

	/**
	 * 
	 * @param lab the label indicates whether CMs or CAs are to be projected
	 */
	public ProjectionFunction(Label<List<String>> lab)
	{
		//TODO only principals labels are CM
		if (lab instanceof CMLabel)
			this.createLabel = (t,i) -> createLabelCM(t,i);
		else
			this.createLabel = (t,i) -> createLabelCA(t,i);

	}

	/**
	 * compute the projection on the i-th principal
	 * @param aut the composed automaton
	 * @param indexprincipal index of the principal to project in the composition
	 * @param getNecessaryPrincipal function returning the index of the necessary principal in a match transition (it could be 
	 * 		  either the offerer or the requester), if any
	 * @return the projected i-th principal
	 * 
	 */
	@Override
	public ModalAutomaton<CALabel> apply(ModalAutomaton<CALabel> aut, Integer indexprincipal, Function<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>, Integer> getNecessaryPrincipal)
	{
		if ((indexprincipal<0)||(indexprincipal>aut.getRank())) 
			throw new IllegalArgumentException("Index out of rank");

		//extracting the basicstates of the principal and creating the castates of the projection
		Map<BasicState<String>,CAState> bs2cs = aut.getTransition().parallelStream()
				.flatMap(t->Stream.of(t.getSource(), t.getTarget()))
				.map(s->s.getState().get(indexprincipal))
				.distinct()
				.collect(Collectors.toMap(Function.identity(), bs->new CAState(new ArrayList<BasicState<String>>(Arrays.asList(bs))//,0,0
						)));

		//associating each castate of the composition with the castate of the principal
		Map<CAState,CAState> map2princst = 
				aut.getTransition().parallelStream()
				.flatMap(t->Stream.of(t.getSource(), t.getTarget()))
				.distinct()
				.collect(Collectors.toMap(Function.identity(), s->bs2cs.get(s.getState().get(indexprincipal))));


		return new ModalAutomaton<CALabel>(aut.getTransition().parallelStream()
				.filter(t-> t.getLabel().isMatch()
						?(t.getLabel().getOfferer().equals(indexprincipal) || t.getLabel().getRequester().equals(indexprincipal))
								:t.getLabel().getOffererOrRequester().equals(indexprincipal))
				.map(t-> new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(map2princst.get(t.getSource()),
						createLabel.apply(t,indexprincipal),
						map2princst.get(t.getTarget()),
						(t.isPermitted()||(t.getLabel().isMatch()&&!getNecessaryPrincipal.apply(t).equals(indexprincipal)))
						?ModalTransition.Modality.PERMITTED
								:t.isLazy()?ModalTransition.Modality.LAZY:ModalTransition.Modality.URGENT,CAState::new))
				.collect(Collectors.toSet()));
	}

	private CALabel createLabelCA(ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel> t,Integer indexprincipal) {
		return (!t.getLabel().isRequest()&&t.getLabel().getOfferer().equals(indexprincipal))?
				new CALabel(1,0,t.getLabel().getTheAction())
				:new CALabel(1,0,t.getLabel().isRequest()?t.getLabel().getTheAction()
						:t.getLabel().getCoAction());

	}

	private CALabel createLabelCM(ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel> t,Integer indexprincipal) {
		if (!t.getLabel().isMatch())
			throw new UnsupportedOperationException();
		
		return new CMLabel(t.getLabel().getOfferer()+"",t.getLabel().getRequester()+"",
				((t.getLabel().getOfferer().equals(indexprincipal))?
						t.getLabel().getTheAction():t.getLabel().getCoAction()));

	}
}
