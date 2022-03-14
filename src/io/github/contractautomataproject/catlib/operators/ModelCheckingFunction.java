package io.github.contractautomataproject.catlib.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

/**
 * Class implementing the model checking function. 
 * This is implemented by instantiating and applying the composition function, 
 * composing the model with the property.
 * 
 * @author Davide Basile
 *
 */
public class ModelCheckingFunction extends CompositionFunction<List<BasicState<String>>,List<String>,CAState,Label<List<String>>,ModalTransition<List<BasicState<String>>,List<String>,CAState,Label<List<String>>>,ModalAutomaton<Label<List<String>>>> 
{

	public ModelCheckingFunction(ModalAutomaton<CALabel> aut, 
			Automaton<String,String,BasicState<String>,ModalTransition<String,String,BasicState<String>,Label<String>>> prop, 
			Predicate<Label<List<String>>> pruningPred) {
		super(Arrays.asList(aut.convertLabelsToLabelsListString(),
				ModelCheckingFunction.convert(prop, Label::new, ModalTransition::new, ModalAutomaton::new)), 
				MSCACompositionFunction::computeRank,
				(l1,l2)->new CALabel(l1.getAction()).getUnsignedAction().equals(l2.getAction().get(0)), //match
				CAState::new, 
				ModalTransition::new, 
				(e, ee,rank) -> new Label<List<String>>(Stream.concat(e.tra.getLabel().getAction().stream(), 
						ee.tra.getLabel().getAction().stream())
						.collect(Collectors.toList())), 
				(lab, rank, shift) ->{ 
					List<String> l = new ArrayList<>(rank);
					l.addAll(Stream.generate(()->CALabel.IDLE).limit(shift).collect(Collectors.toList()));
					l.addAll(lab.getAction());
					if (rank-l.size()>0)
						l.addAll(Stream.generate(()->CALabel.IDLE).limit(rank.longValue()-l.size()).collect(Collectors.toList()));
					return new Label<List<String>>(l);
				}, 
				ModalAutomaton::new,
				pruningPred);

	}

	/**
	 *
	 * @param aut the automaton to convert
	 * @param createLabel	the constructor of a label
	 * @param createTransition	the constructor of a transition
	 * @param createAut	the constructor of the automaton
	 * @return	aut converted into an extension of ModalAutomaton
	 */
	private static <L extends Label<List<String>>, T extends  ModalTransition<List<BasicState<String>>,List<String>,CAState,L>, 
	A extends ModalAutomaton<L>> A convert(
			Automaton<String,String,BasicState<String>,ModalTransition<String,String,BasicState<String>,Label<String>>>  aut,
			Function<List<String>,L> createLabel, 
			TetraFunction<CAState,L,CAState,ModalTransition.Modality,T> createTransition, 
			Function<Set<T>,A> createAut)
	{
		Map<BasicState<String>,CAState> bs2cs = aut.getStates().stream()
				.collect(Collectors.toMap(Function.identity(), s->new CAState(Arrays.asList(s))));

		return createAut.apply(aut.getTransition().parallelStream()
				.map(t->createTransition.apply(bs2cs.get(t.getSource()),
						createLabel.apply(List.of(t.getLabel().getAction())),
						bs2cs.get(t.getTarget()), 
						t.getModality()))
				.collect(Collectors.toSet()));
	}

}

interface PentaFunction<A,B,C,D,E,F>{
	public F apply(A arg1, B arg2, C arg3, D arg4, E arg5);
}

//this method does both the convertions  but requires to do casts
//	public static <L extends Label<List<String>>, T extends  ModalTransition<List<State<String>>,List<String>,CAState,L>, 
//	A extends Automaton<List<State<String>>,List<String>,CAState,T>> 	A convertAll(
//					Automaton<?,?,?,ModalTransition<?,?,?,Label<?>>>  aut,
//					Function<List<String>,L> createLabel, 
//					TetraFunction<CAState,L,CAState,ModalTransition.Modality,T> createTransition, 
//					Function<Set<T>,A> createAut)
//	{
//		if (aut.getStates().isEmpty() || aut.getTransition().isEmpty())
//			throw new IllegalArgumentException();
//
//		if (aut.getStates().iterator().next() instanceof CAState && 
//				aut.getTransition().iterator().next().getLabel().getAction() instanceof String){ 
//
//			Map<State<String>,CAState> bs2cs = aut.getStates().stream()
//					.map(s->(State<String>) s)
//					.collect(Collectors.toMap(Function.identity(), s->new CAState(Arrays.asList(s))));
//
//			A conv = createAut.apply(aut.getTransition().parallelStream()
//					.map(t->createTransition.apply(bs2cs.get((State<String>)t.getSource()),
//								createLabel.apply(List.of((String)t.getLabel().getAction())),
//								bs2cs.get((State<String>)t.getTarget()), 
//								t.getModality()))
//					.collect(Collectors.toSet()));
//
//			return conv;
//		}
//		else if (aut.getStates().iterator().next() instanceof State<String> && 
//				aut.getTransition().iterator().next().getLabel() instanceof CALabel) {
//			A conv = createAut.apply(aut.getTransition().parallelStream()
//					.map(t->createTransition.apply((CAState)t.getSource(),
//							createLabel.apply(((CALabel) t.getLabel()).getAction()),
//							(CAState)t.getTarget(), 
//							t.getModality()))
//					.collect(Collectors.toSet()));
//
//			return conv;
//		}
//		else throw new IllegalArgumentException();
//	}





// previous conversion without generics
//	public Automaton<List<State<String>>,List<String>,CAState,ModalTransition<List<State<String>>,List<String>,CAState,Label<List<String>>>> apply(
//			Automaton<List<State<String>>,List<String>,CAState,ModalTransition<List<State<String>>,List<String>,CAState,Label<List<String>>>> aut, 
//			Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,Label<String>>> prop)
//	{
//		//converting prop 
//		Map<State<String>,CAState> bs2cs = prop.getStates().stream()
//				.collect(Collectors.toMap(Function.identity(), s->new CAState(Arrays.asList(s))));
//
//		Automaton<List<State<String>>,List<String>,CAState,ModalTransition<List<State<String>>,List<String>,CAState,Label<List<String>>>> prop_aut = 
//				new Automaton<>(prop.getTransition().parallelStream()
//						.map(t->new ModalTransition<List<State<String>>,List<String>,CAState,Label<List<String>>>(bs2cs.get(t.getSource()),
//								new Label<List<String>>(t.getLabel().getLabelAsList()),
//								bs2cs.get(t.getTarget()), 
//								t.getModality()))
//						.collect(Collectors.toSet()));
//	}



// previous modelchecking was reusing the composition of MSCA
//		//build from aut an auxiliary msca with all 
//		MSCA aut_all_permitted = new MSCA(aut.getTransition()
//				.parallelStream()
//				.map(t->new MSCATransition(t.getSource(),
//						//t.getLabel(),
//						new CALabel(t.getRank(),0,CALabel.offer+t.getLabel().getUnsignedAction()),
//						t.getTarget(),t.getModality()))
//				.collect(Collectors.toSet()));
//
//		//build from prop an auxiliary msca with all requests
//		Map<State<String>,CAState> bs2cs = prop.getStates().stream()
//				.collect(Collectors.toMap(Function.identity(), s->new CAState(Arrays.asList(s))));
//
//		MSCA prop_all_reqs = new MSCA(prop.getTransition().parallelStream()
//				.map(t->new MSCATransition(bs2cs.get(t.getSource()),
//						new CALabel(1,0,CALabel.request+t.getLabel().getAction()),
//						bs2cs.get(t.getTarget()), 
//						MSCATransition.Modality.PERMITTED))
//				.collect(Collectors.toSet()));
//
//		//compute synchronous composition 
//		MSCACompositionFunction mcf = new MSCACompositionFunction(Arrays.asList(aut_all_permitted,prop_all_reqs)); 
//		MSCA comp = mcf.apply(new StrongAgreement().negate(),this.bound);
//
//		return new MSCA(comp.getTransition()
//				.parallelStream()
//				.map(t->new MSCATransition(t.getSource(),
//						t.getLabel(),
//						t.getTarget(),
//						t.getModality()))
//				.collect(Collectors.toSet()));



// previous model checking was reimplementing the composition
//public Set<CAState> applyOld(MSCA aut, Automaton<String,State<String>,Transition<String,State<String>,Label>> prop)
//{
//
//	Queue<SimpleEntry<SimpleEntry<CAState, State<String>>,Integer>> toVisit = new ConcurrentLinkedQueue<SimpleEntry<SimpleEntry<CAState, State<String>>,Integer>>(Arrays.asList(
//			new AbstractMap.SimpleEntry<>(
//					new AbstractMap.SimpleEntry<>(aut.getInitial(), prop.getInitial()),0)));
//	Set<CAState> visited = new HashSet<CAState>();
//	Set<CAState> dontvisit = new HashSet<>();
//
//	do {
//		SimpleEntry<SimpleEntry<CAState, State<String>>,Integer> sourceEntry=toVisit.remove(); //pop state to visit
//		CAState sourceAut =sourceEntry.getKey().getKey();
//		if (!dontvisit.contains(sourceAut)&&visited.add(sourceAut)&&sourceEntry.getValue()<bound) //if states has not been visited so far
//		{
//			State<?> sourceProp =sourceEntry.getKey().getValue();
//
//			Map<MSCATransition, Set<Transition<String,State<String>,Label>>> matches = 
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