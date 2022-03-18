package io.github.contractautomataproject.catlib.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;
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
public class ProjectionFunction implements TriFunction<ModalAutomaton<CALabel>,Integer,ToIntFunction<ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>>,ModalAutomaton<CALabel>> {
	BiFunction<ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>,Integer,CALabel> createLabel;

	/**
	 * 
	 * @param lab the label indicates whether CMs or CAs are to be projected
	 */
	public ProjectionFunction(Label<List<String>> lab)
	{
		if (lab instanceof CMLabel)
			this.createLabel = this::createLabelCM;
		else
			this.createLabel = this::createLabelCA;

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
	public ModalAutomaton<CALabel> apply(ModalAutomaton<CALabel> aut, Integer indexprincipal, ToIntFunction<ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>> getNecessaryPrincipal)
	{
		if ((indexprincipal<0)||(indexprincipal>aut.getRank())) 
			throw new IllegalArgumentException("Index out of rank");

		//extracting the basicstates of the principal and creating the castates of the projection
		Map<BasicState<String>,CAState<String>> bs2cs = aut.getTransition().parallelStream()
				.flatMap(t->Stream.of(t.getSource(), t.getTarget()))
				.map(s->s.getState().get(indexprincipal))
				.distinct()
				.collect(Collectors.toMap(Function.identity(), bs->new CAState<String>(new ArrayList<BasicState<String>>(Arrays.asList(bs)))));

		//associating each castate of the composition with the castate of the principal
		Map<CAState<String>,CAState<String>> map2princst = 
				aut.getTransition().parallelStream()
				.flatMap(t->Stream.of(t.getSource(), t.getTarget()))
				.distinct()
				.collect(Collectors.toMap(Function.identity(), s->bs2cs.get(s.getState().get(indexprincipal))));


		return new ModalAutomaton<>(aut.getTransition().parallelStream()
				.filter(t-> t.getLabel().isMatch()
						?(t.getLabel().getOfferer().equals(indexprincipal) || t.getLabel().getRequester().equals(indexprincipal))
								:t.getLabel().getOffererOrRequester().equals(indexprincipal))
				.map(t-> new ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>(map2princst.get(t.getSource()),
						createLabel.apply(t,indexprincipal),
						map2princst.get(t.getTarget()),
						(t.isPermitted()||(t.getLabel().isMatch()&&getNecessaryPrincipal.applyAsInt(t)!=indexprincipal))
						?ModalTransition.Modality.PERMITTED
								:t.isLazy()?ModalTransition.Modality.LAZY:ModalTransition.Modality.URGENT))
				.collect(Collectors.toSet()));
	}

	private CALabel createLabelCA(ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel> t,Integer indexprincipal) {
		return (!t.getLabel().isRequest()&&t.getLabel().getOfferer().equals(indexprincipal))?
				new CALabel(1,0,t.getLabel().getPrincipalAction())
				:new CALabel(1,0,t.getLabel().isRequest()?t.getLabel().getPrincipalAction()
						:t.getLabel().getCoAction());

	}

	private CMLabel createLabelCM(ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel> t,Integer indexprincipal) {
		if (!t.getLabel().isMatch())
			throw new UnsupportedOperationException();
		
		return new CMLabel(t.getLabel().getOfferer()+"",t.getLabel().getRequester()+"",
				((t.getLabel().getOfferer().equals(indexprincipal))?
						t.getLabel().getPrincipalAction():t.getLabel().getCoAction()));

	}
}
