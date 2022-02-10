package io.github.davidebasile.contractautomata.operators;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.MSCATransition;
import io.github.davidebasile.contractautomata.automaton.transition.Transition;
import io.github.davidebasile.contractautomata.requirements.StrongAgreement;
import io.github.davidebasile.contractautomata.operators.CompositionFunction;

/**
 * Class implementing the model checking function
 * 
 * @author Davide Basile
 *
 */
public class ModelCheckingFunction implements BiFunction<MSCA,Automaton<String,String,BasicState,Transition<String,String,BasicState,Label<String>>>,Set<CAState>>{
	private final int bound;

	public ModelCheckingFunction() {
		this.bound=Integer.MAX_VALUE;
	}

	/**
	 * 
	 * @param bound the  bound of bounded model checking
	 */
	public ModelCheckingFunction(Integer bound) {
		this.bound=bound;
	}

	/**
	 * @param aut the plant automaton to verify
	 * @param prop the automaton expressing the property to verify
	 * @return the set of states violating prop
	 */
	@Override
	public Set<CAState> apply(MSCA aut, Automaton<String,String,BasicState,Transition<String,String,BasicState,Label<String>>> prop)
	{
		//build from aut an auxiliary msca ignoring modalities with all offers
		MSCA aut_all_permitted = new MSCA(aut.getTransition()
				.parallelStream()
				.map(t->new MSCATransition(t.getSource(),
						//t.getLabel(),
						new CALabel(t.getRank(),0,CALabel.offer+t.getLabel().getUnsignedAction()),
						t.getTarget(),MSCATransition.Modality.PERMITTED))
				.collect(Collectors.toSet()));

		//build from prop an auxiliary msca with all requests
		Map<BasicState,CAState> bs2cs = prop.getStates().stream()
				.collect(Collectors.toMap(Function.identity(), s->new CAState(Arrays.asList(s))));

		MSCA prop_all_reqs = new MSCA(prop.getTransition().parallelStream()
				.map(t->new MSCATransition(bs2cs.get(t.getSource()),
						new CALabel(1,0,CALabel.request+t.getLabel().getAction()),
						bs2cs.get(t.getTarget()), 
						MSCATransition.Modality.PERMITTED))
				.collect(Collectors.toSet()));

		//compute synchronous composition 
		CompositionFunction cf = new CompositionFunction(Arrays.asList(aut_all_permitted,prop_all_reqs)); 
		//				(l1,l2)->l1.getUnsignedAction().equals(l2.getUnsignedAction()), 
		//				(e,ee)-> { 
		//				return new CALabel(e.tra.getRank()+1,0,e.tra.getRank(),CALabel.offer+e.tra.getLabel().getUnsignedAction(),
		//						CALabel.request+ee.tra.getLabel().getUnsignedAction());});
		MSCA comp = cf.apply(new StrongAgreement().negate(),
				//t->t.getLabel().getLabelAsList().get(t.getRank()-1).equals(CALabel.idle), 
				this.bound);


		//return state of aut not in the synchronous composition
		if (comp==null)
			return aut.getStates();
		else
			return aut.getStates().parallelStream()
					.filter(s->!comp.getStates().parallelStream()
							.map(CAState::getState)
							.map(l->l.subList(0, l.size()-1))
							.anyMatch(l->s.getState().equals(l)))
					.collect(Collectors.toSet());

	}
}



//public Set<CAState> applyOld(MSCA aut, Automaton<String,BasicState,Transition<String,BasicState,Label>> prop)
//{
//
//	Queue<SimpleEntry<SimpleEntry<CAState, BasicState>,Integer>> toVisit = new ConcurrentLinkedQueue<SimpleEntry<SimpleEntry<CAState, BasicState>,Integer>>(Arrays.asList(
//			new AbstractMap.SimpleEntry<>(
//					new AbstractMap.SimpleEntry<>(aut.getInitial(), prop.getInitial()),0)));
//	Set<CAState> visited = new HashSet<CAState>();
//	Set<CAState> dontvisit = new HashSet<>();
//
//	do {
//		SimpleEntry<SimpleEntry<CAState, BasicState>,Integer> sourceEntry=toVisit.remove(); //pop state to visit
//		CAState sourceAut =sourceEntry.getKey().getKey();
//		if (!dontvisit.contains(sourceAut)&&visited.add(sourceAut)&&sourceEntry.getValue()<bound) //if states has not been visited so far
//		{
//			State<?> sourceProp =sourceEntry.getKey().getValue();
//
//			Map<MSCATransition, Set<Transition<String,BasicState,Label>>> matches = 
//					aut.getForwardStar(sourceAut).stream()
//					.map(t1 ->	new AbstractMap.SimpleEntry<>(t1, prop.getForwardStar(sourceProp).stream()
//							.filter(t2->t2.getLabel().match(t1.getLabel()))
//							.collect(Collectors.toSet())))
//					.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
//
//			toVisit.addAll(matches.entrySet().stream()
//					.filter(e->!e.getValue().isEmpty())
//					.flatMap(e-> e.getValue().stream().map(tr2->
//					new AbstractMap.SimpleEntry<>(e.getKey().getTarget(),tr2.getTarget())))				
//					.map(e-> new AbstractMap.SimpleEntry<>(e,sourceEntry.getValue()+1))
//					.collect(Collectors.toSet()));
//			
//			dontvisit.addAll(matches.entrySet().stream()
//					.filter(e->e.getValue().isEmpty())
//					.map(e->e.getKey().getTarget())
//					.collect(Collectors.toSet()));
//		}
//	} while (!toVisit.isEmpty());
//
//	return dontvisit;
//}