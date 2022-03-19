package io.github.contractautomataproject.catlib.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.CMLabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;

/**
 * Class implementing the projection function
 * 
 * @author Davide Basile
 *
 */
public class ProjectionFunction implements TriFunction<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>,Integer,ToIntFunction<ModalTransition<String,String,State<String>,CALabel>>,Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> {
	final BiFunction<ModalTransition<String,String,State<String>,CALabel>,Integer,CALabel> createLabel;

	/**
	 * 
	 * @param lab the label indicates whether CMs or CAs are to be projected
	 */
	public ProjectionFunction(Label<String> lab)
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
	public Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> apply(Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> aut, Integer indexprincipal, ToIntFunction<ModalTransition<String,String,State<String>,CALabel>> getNecessaryPrincipal)
	{
		if ((indexprincipal<0)||(indexprincipal>aut.getRank())) 
			throw new IllegalArgumentException("Index out of rank");

		//extracting the basicstates of the principal and creating the castates of the projection
		Map<BasicState<String>,State<String>> bs2cs = aut.getTransition().parallelStream()
				.flatMap(t->Stream.of(t.getSource(), t.getTarget()))
				.map(s->s.getState().get(indexprincipal))
				.distinct()
				.collect(Collectors.toMap(Function.identity(), bs-> new State<>(new ArrayList<>(List.of(bs)))));

		//associating each castate of the composition with the castate of the principal
		Map<State<String>,State<String>> map2princst = 
				aut.getTransition().parallelStream()
				.flatMap(t->Stream.of(t.getSource(), t.getTarget()))
				.distinct()
				.collect(Collectors.toMap(Function.identity(), s->bs2cs.get(s.getState().get(indexprincipal))));


		return new Automaton<>(aut.getTransition().parallelStream()
				.filter(t-> t.getLabel().isMatch()
						?(t.getLabel().getOfferer().equals(indexprincipal) || t.getLabel().getRequester().equals(indexprincipal))
								:t.getLabel().getOffererOrRequester().equals(indexprincipal))
				.map(t-> new ModalTransition<>(map2princst.get(t.getSource()),
						createLabel.apply(t, indexprincipal),
						map2princst.get(t.getTarget()),
						(t.isPermitted() || (t.getLabel().isMatch() && getNecessaryPrincipal.applyAsInt(t) != indexprincipal))
								? ModalTransition.Modality.PERMITTED
								: t.isLazy() ? ModalTransition.Modality.LAZY : ModalTransition.Modality.URGENT))
				.collect(Collectors.toSet()));
	}

	private CALabel createLabelCA(ModalTransition<String,String,State<String>,CALabel> t,Integer indexprincipal) {
		return (!t.getLabel().isRequest()&&t.getLabel().getOfferer().equals(indexprincipal))?
				new CALabel(1,0,t.getLabel().getPrincipalAction())
				:new CALabel(1,0,t.getLabel().isRequest()?t.getLabel().getPrincipalAction()
						:t.getLabel().getCoAction());

	}

	private CMLabel createLabelCM(ModalTransition<String,String,State<String>,CALabel> t,Integer indexprincipal) {
		if (!t.getLabel().isMatch())
			throw new UnsupportedOperationException();
		
		return new CMLabel(t.getLabel().getOfferer()+"",t.getLabel().getRequester()+"",
				((t.getLabel().getOfferer().equals(indexprincipal))?
						t.getLabel().getPrincipalAction():t.getLabel().getCoAction()));

	}
}
