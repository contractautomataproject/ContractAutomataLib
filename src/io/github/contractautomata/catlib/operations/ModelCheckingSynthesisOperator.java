package io.github.contractautomata.catlib.operations;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.operations.interfaces.TriPredicate;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.operations.interfaces.TetraFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * This class implements a model checking operation followed by a synthesis operation. <br>
 * In case the property to model-check is not given, the synthesis operation is applied straightforward. <br>
 * Otherwise, the synthesis operation is applied on the result of the application of the model checking function. <br>
 *
 *     @param <S1> the generic type of the content of states
 *     @param <S> the generic type of states, must be a subtype of <tt>State&lt;S1&gt;</tt>
 *     @param <L> the generic type of the labels of the automaton to check, must be a subtype of <tt>L2</tt>
 *     @param <T> the generic type of the transitions of the automaton to check, must be a subtype of <tt>ModalTransition&lt;S1,Action,S,L&gt;</tt>
 *     @param <A> the generic type of the automaton to check, must be a subtype of <tt>Automaton&lt;S1,Action,S,T &gt;</tt>
  *    @param <L2> the generic type of the labels of the property, must be a subtype of <tt>Label&lt;Action&gt;</tt>
  *    @param <T2> the generic type of the transitions of the property, must be a subtype of <tt>ModalTransition&lt;S1,Action,S,L2&gt;</tt>
 *     @param <A2> the generic type of the automaton property, must be a subtype of <tt>Automaton&lt;S1,Action,S,T2 &gt;</tt>
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
	private final UnaryOperator<L> changeLabel;
	private final Function<List<Action>,L> createLabel;
	private final TetraFunction<S,L,S,ModalTransition.Modality, T> createTransition;
	private final Function<List<BasicState<S1>>,S> createState;
	private final Function<List<Action>,L2> createLabelProp;
	private final TetraFunction<S,L2,S,ModalTransition.Modality, T2> createTransitionProp;
	private final Function<Set<T2>,A2> createAutomatonProp;

	/**
	 * Constructor for a model checking synthesis operator, it requires also the constructors for the used generic types.
	 *
	 * @param pruningPredicate the pruning predicate
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param prop the automaton  property to perform model checking
	 * @param changeLabel constructor for a request action (e.g., orchestration/mpc) or offer action (e.g., choreography)
	 * @param createState	the constructor of states of the automaton
	 * @param createTransition the constructor of transitions of the automaton
	 * @param createLabel the constructor of labels of the automaton
	 * @param createAutomaton the constructor of the automaton
	 * @param createTransitionProp	the constructor of the transitions of the property
	 * @param createLabelProp the constructor of the labels of the property
	 * @param createAutomatonProp the constructor of the automaton property
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
	 * Constructor for a model checking synthesis operator, it requires also the constructors for the used generic types.<br>
	 * In this constructor the pruning predicate is set to always return false.
	 *
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param prop the automaton  property to perform model checking
	 * @param changeLabel constructor for a request action (e.g., orchestration/mpc) or offer action (e.g., choreography)
	 * @param createState	the constructor of states of the automaton
	 * @param createTransition the constructor of transitions of the automaton
	 * @param createLabel the constructor of labels of the automaton
	 * @param createAutomaton the constructor of the automaton
	 * @param createTransitionProp	the constructor of the transitions of the property
	 * @param createLabelProp the constructor of the labels of the property
	 * @param createAutomatonProp the constructor of the automaton property
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

	/**
	 * Constructor for a model checking synthesis operator. <br>
	 * This constructor sets to null the property and the related constructors. <br>
	 *
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param createState	the constructor of states of the automaton
	 * @param createTransition the constructor of transitions of the automaton
	 * @param createLabel the constructor of labels of the automaton
	 * @param createAutomaton the constructor of the automaton
	 */
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

	/**
	 * Applies the model checking and synthesis operator.
	 *
	 * @param arg1 the automaton to which model checking and synthesis are applied
	 * @return the automaton resulting from applying model checking and synthesis to arg
	 */
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
						List<Action> li = new ArrayList<>(t.getLabel().getContent());
						li.set(t.getRank()-1, new IdleAction()); //silencing the prop moves
						L lab = createLabel.apply(li);
						if (mcf.getPruningPred().test(t.getLabel())
								&& t.isLazy()
								&& this.getReq().test(lab)) //the transition was lazy and bad (satisfying pruning pred),
							// but after removing the prop move it satisfies getReq: it must be changed.
							lab=changeLabel.apply(lab); //change either to request or offer
						return createTransition.apply(
								t.getSource(),
								lab,
								t.getTarget(),
								t.getModality());})
					.collect(Collectors.toSet()));

			//computing the synthesis
			return  super.apply(deletingPropAction);
		}
	}

	/**
	 * Getter of the function changeLabel.
	 *
	 * @return the function changeLabel
	 */
	public UnaryOperator<L> getChangeLabel(){
		return this.changeLabel;
	}
}