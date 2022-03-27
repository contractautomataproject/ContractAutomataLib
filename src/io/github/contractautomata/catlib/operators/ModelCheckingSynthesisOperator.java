package io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.operators.interfaces.TetraFunction;
import io.github.contractautomata.catlib.operators.interfaces.TriPredicate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * 
 * @author Davide Basile
 *
 */
public class ModelCheckingSynthesisOperator<S1,
		S extends State<S1>,
		L extends L2,
		T extends ModalTransition<S1,Action,S,L>,
		A extends Automaton<S1,Action,S,T>,
		L2 extends Label<Action>,
		T2 extends ModalTransition<S1,Action,S,L2>,
		A2 extends Automaton<S1,Action,S,T2>>  extends SynthesisOperator<S1,Action,S,L,T,A> {

	private final A2 prop;
	private final Function<L,L> changeLabel;
	private final Function<List<Action>,L> createLabel;
	private final TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition;
	private final Function<List<BasicState<S1>>,S> createState;
	private final Function<List<Action>,L2> createLabelProp;
	private final TetraFunction<S,L2,S,ModalTransition.Modality, T2> createTransitionProp;
	private final Function<Set<T2>,A2> createAutomatonProp;

	/**
	 *
	 * @param pruningPredicate the pruning predicate
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param prop another property to enforce expressed by an automaton
	 */
	public ModelCheckingSynthesisOperator(
			TriPredicate<T, Set<T>, Set<S>> pruningPredicate,
			TriPredicate<T, Set<T>, Set<S>> forbiddenPredicate,
			Predicate<L> req,
			A2 prop,
			UnaryOperator<L> changeLabel,
			Function<Set<T>,A> createAutomaton,
			Function<List<Action>,L> createLabel,
			TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition,
			Function<List<BasicState<S1>>,S> createState,
			Function<List<Action>,L2> createLabelProp,
			TetraFunction<S,L2,S,ModalTransition.Modality, T2> createTransitionProp,
			Function<Set<T2>,A2> createAutomatonProp)
	{
		super(pruningPredicate,forbiddenPredicate,req,createAutomaton);
		this.prop=prop;
		this.changeLabel=changeLabel;
		this.createLabel=createLabel;
		this.createTransition=createTransition;
		this.createState=createState;
		this.createLabelProp=createLabelProp;
		this.createTransitionProp=createTransitionProp;
		this.createAutomatonProp=createAutomatonProp;
	}

	/**
	 * This constructor does not use any pruning predicate
	 *
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param prop another property to enforce expressed by an automaton
	 */
	public ModelCheckingSynthesisOperator(
			TriPredicate<T, Set<T>, Set<S>> forbiddenPredicate,
			Predicate<L> req,
			A2 prop,
			UnaryOperator<L> changeLabel,
			Function<Set<T>,A> createAutomaton,
			Function<List<Action>,L> createLabel,
			TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition,
			Function<List<BasicState<S1>>,S> createState,
			Function<List<Action>,L2> createLabelProp,
			TetraFunction<S,L2,S,ModalTransition.Modality, T2> createTransitionProp,
			Function<Set<T2>,A2> createAutomatonProp)
	{
		this((x,t,bad) -> false,forbiddenPredicate,req,prop,changeLabel,createAutomaton,
				createLabel,createTransition,createState,createLabelProp,createTransitionProp,createAutomatonProp);
	}

	public ModelCheckingSynthesisOperator(
			TriPredicate<T, Set<T>, Set<S>> forbiddenPredicate,
			Predicate<L> req,
			Function<Set<T>,A> createAutomaton,
			Function<List<Action>,L> createLabel,
			TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition,
			Function<List<BasicState<S1>>,S> createState)
	{
		this((x,t,bad) -> false,forbiddenPredicate,req,null,null,createAutomaton,
				createLabel,createTransition,createState,null,null,null);
	}

	@Override
	public A apply(A arg1) {
		if (prop==null)
			return super.apply(arg1);
		else
		{
			//model checking is performed on the type of the property (the resulting labels
			//could not be calabels), whilst the synthesis is performed using the type of the automaton
			//(generally requirements are expressed using calabels), thus conversions must be performed

			A2 convertAut = createAutomatonProp.apply(  //converting A to A2
						 arg1.getTransition()
									.parallelStream()
									.map(t -> createTransitionProp.apply(t.getSource(),
											t.getLabel(),
											t.getTarget(),
											t.getModality()))
									.collect(Collectors.toSet()));

			ModelCheckingFunction<S1,S,L2,T2,A2>
					mcf = new ModelCheckingFunction<>(convertAut,prop,
					createState, createTransitionProp, createLabelProp, createAutomatonProp);

			A2 comp = mcf.apply(Integer.MAX_VALUE);

			if (comp==null)
				return null;

			//the following steps are necessary to reuse the synthesis, reverting A2 to A,
			//silencing the prop action and treat lazy transitions satisfying the pruningPredicate:
			//they must be detectable as "bad" also after reverting to A,
			//lazy transitions are quantified existentially on the states: the states must not be modified
			A deletingPropAction = this.getCreateAut().apply(comp.getTransition()
					.parallelStream().map(t->{
						List<Action> li = new ArrayList<>(t.getLabel().getLabel());
						li.set(t.getRank()-1, new IdleAction()); //silencing the prop moves
						L lab = createLabel.apply(li);
						if (mcf.getPruningPred().test(t.getLabel())&&t.isLazy()&&this.getReq().test(lab)) //the transition was lazy and bad (satisfying pruning pred),
							// but after removing the prop move it satisfies getReq: it must be changed.
							lab=changeLabel.apply(lab); //change either to request or offer
						return createTransition.apply(t.getSource(),lab,t.getTarget(),t.getModality());})
					.collect(Collectors.toSet()));

			//computing the synthesis
			return  super.apply(deletingPropAction);
		}
	}
}