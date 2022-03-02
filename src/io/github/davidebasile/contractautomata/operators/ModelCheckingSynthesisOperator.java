package io.github.davidebasile.contractautomata.operators;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.ModalTransition;

/**
 * 
 * @author Davide Basile
 *
 */
public class ModelCheckingSynthesisOperator extends ModalAutomatonSynthesisOperator<CALabel> {

	private Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>>  prop;

	/**
	 * 
	 * @param pruningPredicate the pruning predicate
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param prop another property to enforce expressed by an automaton
	 */
	public ModelCheckingSynthesisOperator(
			TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, 
			Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>, 
			Set<CAState>> pruningPredicate,
			TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, 
			Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>, 
			Set<CAState>> forbiddenPredicate,
			Predicate<CALabel> req,
			Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>>  prop) 
	{
		super(pruningPredicate,forbiddenPredicate,req,CALabel::new);
		this.prop=prop;
	}

	/**
	 * This constructor does not use any pruning predicate
	 *   
	 * @param forbiddenPredicate the forbidden predicate
	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
	 * @param prop another property to enforce expressed by an automaton
	 */
	public ModelCheckingSynthesisOperator(
			TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, 
			Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>, 
			Set<CAState>> forbiddenPredicate,
			Predicate<CALabel> req,
			Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>>  prop) 
	{
		super((x,t,bad) -> false, forbiddenPredicate,req,CALabel::new);
		this.prop=prop;
	}

	@Override
	public ModalAutomaton<CALabel> apply(Automaton<List<BasicState>,List<String>,CAState,
			ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> arg1) {
		if (prop==null)
			return super.apply(arg1);
		else
		{
			ModalAutomaton<Label<List<String>>> comp=new ModelCheckingFunction(new ModalAutomaton<CALabel>(arg1.getTransition()),prop).apply(Integer.MAX_VALUE);			
			if (comp==null)
					return null;
			return super.apply(ModelCheckingFunction.revertToMSCA(comp));
		}
	}

}


