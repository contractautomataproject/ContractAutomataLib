package io.github.contractautomataproject.catlib.operators;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.automaton.transition.ModalTransition;

/**
 * 
 * @author Davide Basile
 *
 */
public class ModelCheckingSynthesisOperator extends 
SynthesisOperator<String,String,State<String>,CALabel,ModalTransition<String,String,State<String>,CALabel>,Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> {

	private final Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>>  prop;
	private final Function<CALabel,CALabel> changeLabel;
	private final Predicate<Label<String>> reqmc;

	/**
	 * 
	 * @param pruningPredicate the pruning predicate
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param prop another property to enforce expressed by an automaton
	 */
	public ModelCheckingSynthesisOperator(
			TriPredicate<ModalTransition<String,String,State<String>,CALabel>, 
			Set<ModalTransition<String,String,State<String>,CALabel>>, 
			Set<State<String>>> pruningPredicate,
			TriPredicate<ModalTransition<String,String,State<String>,CALabel>, 
			Set<ModalTransition<String,String,State<String>,CALabel>>, 
			Set<State<String>>> forbiddenPredicate,
			Predicate<CALabel> req,
			Predicate<Label<String>> reqmc,
			Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>>  prop,
			UnaryOperator<CALabel> changeLabel) 
	{
		super(pruningPredicate,forbiddenPredicate,req,Automaton::new);
		this.prop=prop;
		this.changeLabel=changeLabel;
		this.reqmc=reqmc;
	}

	/**
	 * This constructor does not use any pruning predicate
	 *   
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param prop another property to enforce expressed by an automaton
	 */
	public ModelCheckingSynthesisOperator(
			TriPredicate<ModalTransition<String,String,State<String>,CALabel>, 
			Set<ModalTransition<String,String,State<String>,CALabel>>, 
			Set<State<String>>> forbiddenPredicate,
			Predicate<CALabel> req,
			Predicate<Label<String>> reqmc,
			Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>> prop,
			UnaryOperator<CALabel> changeLabel) 
	{
		this((x,t,bad) -> false, forbiddenPredicate,req,reqmc,prop,changeLabel);
	}

	@Override
	public Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> apply(Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> arg1) {
		if (prop==null)
			return super.apply(arg1);
		else
		{
			ModelCheckingFunction mcf = new ModelCheckingFunction(new Automaton<>(arg1.getTransition()),prop,reqmc);
			Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>> comp=mcf.apply(Integer.MAX_VALUE);	

			if (comp==null)
				return null;

			//the following steps are necessary to reuse the synthesis of MSCA
			//firstly silencing the prop action and treat lazy transitions satisfying the pruningPredicate: 
			//they must be detectable as "bad" also after reverting to an MSCA  
			Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> deletingPropAction = new Automaton<>(comp.getTransition()
					.parallelStream().map(t->{
						List<String> li = new ArrayList<>(t.getLabel().getAction());
						li.set(t.getRank()-1, CALabel.IDLE); //removing the move of prop to have a CALabel
						CALabel lab = new CALabel(li);
						if (mcf.getPruningPred().test(t.getLabel())&&t.isLazy()&&this.getReq().test(lab)) //the transition was bad lazy, but after removing 
							//the prop move is lazy good: it must be changed.
							lab=changeLabel.apply(lab); //change either to request or offer
						return new ModalTransition<>(t.getSource(),lab,t.getTarget(),t.getModality());})
					.collect(Collectors.toSet()));

			//computing the synthesis lazy transitions are quantified existentially on the states: the states must not be modified
			return  super.apply(deletingPropAction);
		}
	}
}