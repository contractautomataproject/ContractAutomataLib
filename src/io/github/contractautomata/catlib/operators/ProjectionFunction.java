package io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.*;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.operators.interfaces.TriFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class implementing the projection function
 * 
 * @author Davide Basile
 *
 */
public class ProjectionFunction<S1> implements TriFunction<Automaton<S1, Action, State<S1>, ModalTransition<S1, Action,State<S1>, CALabel>>,Integer,ToIntFunction<ModalTransition<S1,Action,State<S1>,CALabel>>,Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>>> {
	private final  boolean createAddress;

	public ProjectionFunction(boolean createAddress)
	{
		this.createAddress=createAddress;
	}

	public ProjectionFunction()
	{
		this.createAddress=false;
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
	public Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>> apply(Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>> aut, Integer indexprincipal,
																							 ToIntFunction<ModalTransition<S1,Action,State<S1>,CALabel>> getNecessaryPrincipal)
	{
		if ((indexprincipal<0)||(indexprincipal>aut.getRank())) 
			throw new IllegalArgumentException("Index out of rank");

		//extracting the basicstates of the principal and creating the castates of the projection
		Map<BasicState<S1>,State<S1>> bs2cs = aut.getTransition().parallelStream()
				.flatMap(t->Stream.of(t.getSource(), t.getTarget()))
				.map(s->s.getState().get(indexprincipal))
				.distinct()
				.collect(Collectors.toMap(Function.identity(), bs-> new State<>(new ArrayList<>(List.of(bs)))));

		//associating each castate of the composition with the castate of the principal
		Map<State<S1>,State<S1>> map2princst =
				aut.getTransition().parallelStream()
				.flatMap(t->Stream.of(t.getSource(), t.getTarget()))
				.distinct()
				.collect(Collectors.toMap(Function.identity(), s->bs2cs.get(s.getState().get(indexprincipal))));


		return new Automaton<>(aut.getTransition().parallelStream()
				.filter(t-> t.getLabel().isMatch()
						?(t.getLabel().getOfferer().equals(indexprincipal) || t.getLabel().getRequester().equals(indexprincipal))
								:t.getLabel().getOffererOrRequester().equals(indexprincipal))
				.map(t-> new ModalTransition<>(map2princst.get(t.getSource()),
						createLabel(t, indexprincipal),
						map2princst.get(t.getTarget()),
						(t.isPermitted() || (t.getLabel().isMatch() && getNecessaryPrincipal.applyAsInt(t) != indexprincipal))
								? ModalTransition.Modality.PERMITTED
								: t.isLazy() ? ModalTransition.Modality.LAZY : ModalTransition.Modality.URGENT))
				.collect(Collectors.toSet()));
	}

	private CALabel createLabel(ModalTransition<S1,Action,State<S1>,CALabel> t,Integer indexprincipal) {
		if (!createAddress)
			return (!t.getLabel().isRequest()&&t.getLabel().getOfferer().equals(indexprincipal))?
					new CALabel(1,0,t.getLabel().getAction())
					:new CALabel(1,0,t.getLabel().isRequest()?t.getLabel().getAction()
					:t.getLabel().getCoAction());
		else
		{
			if (!t.getLabel().isMatch() || t.getLabel().getAction() instanceof AddressedAction)
				throw new UnsupportedOperationException();

			if (t.getLabel().getOfferer().equals(indexprincipal))
				return new CALabel(1,0,new AddressedOfferAction(t.getLabel().getAction().getLabel(),
						new Address(indexprincipal+"",t.getLabel().getRequester()+"")));
			else
				return new CALabel(1,0,new AddressedRequestAction(t.getLabel().getAction().getLabel(),
						new Address(t.getLabel().getOfferer()+"",indexprincipal+"")));
		}
	}
}
