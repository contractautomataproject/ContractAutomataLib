package io.github.contractautomata.catlib.operators;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.transition.Transition;
import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.state.State;

/**
 * Class implementing the Choreography Synthesis
 * 
 * @author Davide Basile
 *
 */
public class ChoreographySynthesisOperator<S1>  extends ModelCheckingSynthesisOperator<S1,State<S1>,CALabel,
        ModalTransition<S1,Action,State<S1>,CALabel>,
        Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>>,
        Label<Action>,
        ModalTransition<S1,Action,State<S1>,Label<Action>>,
        Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,Label<Action>>>>
{

	private final Predicate<CALabel> req;
	private Function<Stream<ModalTransition<S1, Action,State<S1>,CALabel>>,Optional<ModalTransition<S1,Action,State<S1>,CALabel>>> choice=Stream::findAny;

	
	public ChoreographySynthesisOperator(Predicate<CALabel> req,
			Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,Label<Action>>>  prop){
		super(ChoreographySynthesisOperator::isUncontrollableChoreography,req, prop,
				lab->new CALabel(lab.getRank(),lab.getOfferer(),lab.getAction()), //offers are necessary
                Automaton::new,CALabel::new,ModalTransition::new,State::new,Label::new,ModalTransition::new,Automaton::new);

        this.req=req;
	}
	

	public ChoreographySynthesisOperator(Predicate<CALabel> req){
		super(ChoreographySynthesisOperator::isUncontrollableChoreography,req,
                Automaton::new,CALabel::new,ModalTransition::new,State::new);

        this.req=req;
	}
	
	public ChoreographySynthesisOperator(Predicate<CALabel> req, 
			Function<Stream<ModalTransition<S1,Action,State<S1>,CALabel>>,
				Optional<ModalTransition<S1,Action,State<S1>,CALabel>>> choice){
		super(ChoreographySynthesisOperator::isUncontrollableChoreography,req, null,null,
                Automaton::new,CALabel::new,ModalTransition::new,State::new,null,null,null);

        this.req=req;
		this.choice=choice;
	}

	/** 
	 * invokes the synthesis method for synthesising the choreography
	 * @param aut the plant automaton to which the synthesis is performed
	 * @return the synthesised choreography, removing only one transition violating the branching condition 
	 * each time no further updates are possible. The transition to remove is chosen nondeterministically.
	 * 
	 */
	@Override
	public Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>> apply(Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>> aut)
	{
		if (aut.getTransition().parallelStream()
				.anyMatch(t-> !t.isPermitted()&&t.getLabel().isRequest()))
			throw new UnsupportedOperationException("The automaton contains necessary requests that are not allowed in the choreography synthesis");

		final Set<String> violatingbc=new HashSet<>();
		//this.setPruningPred((x,t,bad) -> violatingbc.contains(x.toString()),req);
		
		ModalTransition<S1,Action,State<S1>,CALabel> toRemove=null;
		Automaton<S1,Action,State<S1>,ModalTransition<S1,Action,State<S1>,CALabel>> chor;
		do 
		{
			if (toRemove!=null) {
				final String toRemoveString = toRemove.toString();
				aut = new Automaton<>(aut.getTransition().parallelStream()
						.filter(t -> !t.toString().equals(toRemoveString))
						.collect(Collectors.toSet()));
			}

			chor = super.apply(aut);
			if (chor==null)
				break;
			final Set<ModalTransition<S1,Action,State<S1>,CALabel>> trf = chor.getTransition();
			toRemove=choice.apply(chor.getTransition().parallelStream()
					.filter(x->!satisfiesBranchingCondition(x,trf, new HashSet<>())))
					.orElse(null);
		} while (toRemove!=null && violatingbc.add(toRemove.toString()));
		return chor;
	}

	private static <S1> boolean isUncontrollableChoreography(ModalTransition<S1,Action,State<S1>,CALabel> tra, Set<? extends ModalTransition<S1,Action,State<S1>,CALabel>> str, Set<State<S1>> badStates)
	{
		return 	tra.isUncontrollable(str,badStates, 
				(t,tt) -> t.getLabel().getOfferer().equals(tt.getLabel().getOfferer())//the same offerer
				&&t.getLabel().getAction().equals(tt.getLabel().getAction()) //the same offer
				&&t.getSource().equals(tt.getSource()));//the same global source state
	}

	/**
	 * @param tra the transition to check
	 * @param trans the set of transitions to check against tra
	 * @param bad  the set of bad (dangling) states to check
	 * @return true if the set of transitions and bad states violate the branching condition
	 */
	public boolean satisfiesBranchingCondition(ModalTransition<S1,Action,State<S1>,CALabel> tra, Set<ModalTransition<S1,Action,State<S1>,CALabel>> trans, Set<State<S1>> bad)
	{
		final Set<ModalTransition<S1,Action,State<S1>,CALabel>> ftr = trans.parallelStream()
				.filter(x->req.test(x.getLabel())&&!bad.contains(x.getSource())&&!bad.contains(x.getTarget()))
				.collect(Collectors.toSet()); //only valid candidates

		return ftr.parallelStream()
				.map(Transition::getSource)
				.filter(x->x!=tra.getSource()&&
				tra.getSource().getState().get(tra.getLabel().getOfferer()).getState()
				.equals(x.getState().get(tra.getLabel().getOfferer()).getState()))
				//it's not the same state of tra but sender is in the same state of this

				.allMatch(s -> ftr.parallelStream()
						.anyMatch(x->x.getSource()==s && tra.getLabel().equals(x.getLabel()))
						//for all such states there exists an outgoing transition with the same label of tra
						);
	}
}