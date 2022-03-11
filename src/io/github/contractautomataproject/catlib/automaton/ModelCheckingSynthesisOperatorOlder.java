package io.github.contractautomataproject.catlib.automaton;
//package io.github.contractautomataproject.catlib.operators;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//import java.util.function.Function;
//import java.util.function.Predicate;
//import java.util.stream.Collectors;
//import java.util.stream.IntStream;
//
//import io.github.contractautomataproject.catlib.automaton.Automaton;
//import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
//import io.github.contractautomataproject.catlib.automaton.ModalAutomatonSynthesisOperator;
//import io.github.contractautomataproject.catlib.automaton.label.CALabel;
//import io.github.contractautomataproject.catlib.automaton.label.Label;
//import io.github.contractautomataproject.catlib.automaton.state.BasicState;
//import io.github.contractautomataproject.catlib.automaton.state.CAState;
//import io.github.contractautomataproject.catlib.transition.ModalTransition;
//
///**
// * 
// * @author Davide Basile
// *
// */
//public class ModelCheckingSynthesisOperatorOlder extends ModalAutomatonSynthesisOperator<CALabel> {
//
//	private final Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>>  prop;
//	private final Function<CALabel,CALabel> changeLabel;
//	private final Predicate<Label<List<String>>> reqmc;
//
//	/**
//	 * 
//	 * @param pruningPredicate the pruning predicate
//	 * @param forbiddenPredicate the forbidden predicate
//	 * @param req the invariant requirement to enforce (e.g. agreement, strong agreement)
//	 * @param prop another property to enforce expressed by an automaton
//	 */
//	public ModelCheckingSynthesisOperatorOlder(
//			TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, 
//			Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>, 
//			Set<CAState>> pruningPredicate,
//			TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, 
//			Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>, 
//			Set<CAState>> forbiddenPredicate,
//			Predicate<CALabel> req,
//			Predicate<Label<List<String>>> reqmc,
//			Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>>  prop,
//			Function<CALabel,CALabel> changeLabel) 
//	{
//		super(pruningPredicate,forbiddenPredicate,req);
//		this.prop=prop;
//		this.changeLabel=changeLabel;
//		this.reqmc=reqmc;
//	}
//
//	/**
//	 * This constructor does not use any pruning predicate
//	 *   
//	 * @param forbiddenPredicate the forbidden predicate
//	 * @param req  the invariant requirement to enforce (e.g. agreement, strong agreement)
//	 * @param prop another property to enforce expressed by an automaton
//	 */
//	public ModelCheckingSynthesisOperatorOlder(
//			TriPredicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, 
//			Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>, 
//			Set<CAState>> forbiddenPredicate,
//			Predicate<CALabel> req,
//			Predicate<Label<List<String>>> reqmc,
//			Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>>  prop,
//			Function<CALabel,CALabel> changeLabel) 
//	{
//		this((x,t,bad) -> false, forbiddenPredicate,req,reqmc,prop,changeLabel);
//	}
//
//	@Override
//	public ModalAutomaton<CALabel> apply(ModalAutomaton<CALabel> arg1) {
//		if (prop==null)
//			return super.apply(arg1);
//		else
//		{
//			ModelCheckingFunction mcf = new ModelCheckingFunction(new ModalAutomaton<CALabel>(arg1.getTransition()),prop, reqmc);
//			ModalAutomaton<Label<List<String>>> comp=mcf.apply(Integer.MAX_VALUE);	
//			
//			if (comp==null)
//					return null;
//			
//			//the following steps are necessary to reuse the synthesis of MSCA
//			//firstly silencing the prop action and treat lazy transitions satisfying the pruningPredicate: 
//			//they must be detectable as "bad" also after reverting to an MSCA  
//			ModalAutomaton<CALabel> deletingPropAction = new ModalAutomaton<CALabel>(comp.getTransition()
//			.parallelStream().map(t->{
//				List<String> li = new ArrayList<>(t.getLabel().getAction());
//				li.set(t.getRank()-1, CALabel.idle); //removing the move of prop to have a CALabel
//				CALabel lab = new CALabel(li);
//				if (mcf.getPruningPred().test(t.getLabel())&&t.isLazy()&&this.getReq().test(lab)) 
//						lab=changeLabel.apply(lab);
//				return new ModalTransition<>(t.getSource(),lab,t.getTarget(),t.getModality());})
//			.collect(Collectors.toSet()));
//			
//			//computing the synthesis before modifying the states, this is important for the lazy transitions
//			ModalAutomaton<CALabel>  aut = super.apply(deletingPropAction);
//			
//			//the remaining prop states are removed, and unfolded states 
//			//are renamed to keep the unfolding
//			
//			//first renaming states of aut that are unfolded by prop
//			aut.getTransition().parallelStream()
//			.filter(t->t.getSource().getState().subList(0, t.getSource().getState().size()-1).equals(
//					t.getTarget().getState().subList(0, t.getTarget().getState().size()-1))&&
//					!t.getSource().getState().get(t.getSource().getState().size()-1).equals(
//							t.getTarget().getState().get(t.getTarget().getState().size()-1)))//only prop has moved
//			.forEach(t->{
//				List<BasicState> state = t.getTarget().getState();
//				IntStream.range(0,t.getLabel().getRank()-1)
//				.filter(i->!t.getLabel().getAction().get(i).equals(CALabel.idle))
//				.forEach(i->state.set(i, new BasicState(state.get(i).getState()+"_"+state.get(state.size()-1).getState(),		
//						false,state.get(i).isFinalstate())));//cannot duplicate initial states!
//			});
//
//			//transitions may share a castate
//			Map<List<BasicState>,CAState> cs2cs = aut.getStates().parallelStream()
//					.map(s->s.getState().subList(0, s.getState().size()-1))
//					.distinct()
//					.collect(Collectors.toMap(Function.identity(), CAState::new));
//
//			return new ModalAutomaton<CALabel>(aut.getTransition().parallelStream()
//					.map(t->new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(
//							cs2cs.get(t.getSource().getState().subList(0, t.getSource().getRank()-1)), 
//							new CALabel(t.getLabel().getAction().subList(0,t.getLabel().getRank()-1)),
//							cs2cs.get(t.getTarget().getState().subList(0, t.getTarget().getRank()-1)),
//							t.getModality()))
//					.collect(Collectors.toSet()));
//		}
//	}
//	
//
//}
//
//
