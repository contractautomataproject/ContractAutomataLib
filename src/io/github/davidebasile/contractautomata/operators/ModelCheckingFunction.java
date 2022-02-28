package io.github.davidebasile.contractautomata.operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.BasicState;
import io.github.davidebasile.contractautomata.automaton.state.CAState;
import io.github.davidebasile.contractautomata.automaton.transition.ModalTransition;

/**
 * Class implementing the model checking function. 
 * This is implemented by instantiating and applying the composition function.
 * 
 * @author Davide Basile
 *
 */
public class ModelCheckingFunction implements 
BiFunction<ModalAutomaton<CALabel>,
Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>>,
ModalAutomaton<CALabel>>{
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
	public ModalAutomaton<CALabel> apply(ModalAutomaton<CALabel> aut, 
			Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>> prop)
	{
		//converting
		ModalAutomaton<Label<List<String>>> convaut = aut.revertToModalAutomaton();
		ModalAutomaton<Label<List<String>>> convprop = ModelCheckingFunction.convert(prop, Label::new, ModalTransition::new, ModalAutomaton::new);

		//instantiating the composition function
		CompositionFunction<List<BasicState>,List<String>,CAState,Label<List<String>>,ModalTransition<List<BasicState>,List<String>,CAState,Label<List<String>>>> cf = 
				new CompositionFunction<List<BasicState>,List<String>,CAState,Label<List<String>>,ModalTransition<List<BasicState>,List<String>,CAState,Label<List<String>>>> (
						Arrays.asList(convaut,convprop), 
						MSCACompositionFunction::computeRank,
						(l1,l2)->new CALabel(l1.getAction()).getUnsignedAction().equals(l2.getAction().get(0)), 
						CAState::new, 
						ModalTransition::new, 
						(e, ee,rank) -> new Label<List<String>>(Stream.concat(e.tra.getLabel().getAction().stream(), 
								ee.tra.getLabel().getAction().stream())
								.collect(Collectors.toList())), 
						(lab, rank, shift) ->{ 
							List<String> l = new ArrayList<String>(rank);
							l.addAll(Stream.generate(()->CALabel.idle).limit(shift).collect(Collectors.toList()));
							l.addAll(lab.getAction());
							if (rank-l.size()>0)
								l.addAll(Stream.generate(()->CALabel.idle).limit(rank-l.size()).collect(Collectors.toList()));
							return new Label<List<String>>(l);
						}, ModalAutomaton::new);

		//apply the instantiation
		ModalAutomaton<Label<List<String>>> comp = 
				(ModalAutomaton<Label<List<String>>>) cf.apply(
						l->l.getAction().get(l.getRank()-1).equals(CALabel.idle)||
						IntStream.range(0, l.getRank()-1)
						.allMatch(i->l.getAction().get(i).equals(CALabel.idle))
						//only transitions where both aut and prop moves together are allowed
				,bound);

		if (comp==null)
			return null;
		
		//removing the prop automaton from the composition before reverting to an MSCA
		
		//first renaming states of aut that are unfolded by prop
		comp.getTransition().parallelStream()
				.filter(t->t.getSource().getState().subList(0, t.getSource().getState().size()-1).equals(
						t.getTarget().getState().subList(0, t.getTarget().getState().size()-1))&&
						!t.getSource().getState().get(t.getSource().getState().size()-1).equals(
								t.getTarget().getState().get(t.getTarget().getState().size()-1)))//only prop has moved
				.forEach(t->{
					List<BasicState> state = t.getTarget().getState();
					IntStream.range(0,t.getLabel().getRank()-1)
					.filter(i->!t.getLabel().getAction().get(i).equals(CALabel.idle))
					.forEach(i->state.set(i, new BasicState(state.get(i).getState()+"_"+state.get(state.size()-1).getState()						
							,false,state.get(i).isFinalstate())));//cannot duplicate initial states!
				});
		
		//transitions may share a castate
		Map<List<BasicState>,CAState> cs2cs = comp.getStates().parallelStream()
		.map(s->s.getState().subList(0, s.getState().size()-1))
		.distinct()
		.collect(Collectors.toMap(Function.identity(), CAState::new));
		
		return new ModalAutomaton<CALabel>(comp.getTransition().parallelStream()
				.map(t->new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(
							cs2cs.get(t.getSource().getState().subList(0, t.getSource().getRank()-1)), 
							new CALabel(t.getLabel().getAction().subList(0, t.getLabel().getRank()-1)),
							cs2cs.get(t.getTarget().getState().subList(0, t.getTarget().getRank()-1)),
							t.getModality()))
				.collect(Collectors.toSet()));

	}


	private static <L extends Label<List<String>>, T extends  ModalTransition<List<BasicState>,List<String>,CAState,L>, 
	A extends ModalAutomaton<L>>	A convert(
			Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>>  aut,
			Function<List<String>,L> createLabel, 
			TetraFunction<CAState,L,CAState,ModalTransition.Modality,T> createTransition, 
			Function<Set<T>,A> createAut)
	{
		Map<BasicState,CAState> bs2cs = aut.getStates().stream()
				.collect(Collectors.toMap(Function.identity(), s->new CAState(Arrays.asList(s))));

		A conv = createAut.apply(aut.getTransition().parallelStream()
				.map(t->createTransition.apply(bs2cs.get(t.getSource()),
						createLabel.apply(List.of(t.getLabel().getAction())),
						bs2cs.get(t.getTarget()), 
						t.getModality()))
				.collect(Collectors.toSet()));

		return conv;
	}
}



//this method does both the convertions  but requires to do casts
//	public static <L extends Label<List<String>>, T extends  ModalTransition<List<BasicState>,List<String>,CAState,L>, 
//	A extends Automaton<List<BasicState>,List<String>,CAState,T>> 	A convertAll(
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
//			Map<BasicState,CAState> bs2cs = aut.getStates().stream()
//					.map(s->(BasicState) s)
//					.collect(Collectors.toMap(Function.identity(), s->new CAState(Arrays.asList(s))));
//
//			A conv = createAut.apply(aut.getTransition().parallelStream()
//					.map(t->createTransition.apply(bs2cs.get((BasicState)t.getSource()),
//								createLabel.apply(List.of((String)t.getLabel().getAction())),
//								bs2cs.get((BasicState)t.getTarget()), 
//								t.getModality()))
//					.collect(Collectors.toSet()));
//
//			return conv;
//		}
//		else if (aut.getStates().iterator().next() instanceof BasicState && 
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
//	public Automaton<List<BasicState>,List<String>,CAState,ModalTransition<List<BasicState>,List<String>,CAState,Label<List<String>>>> apply(
//			Automaton<List<BasicState>,List<String>,CAState,ModalTransition<List<BasicState>,List<String>,CAState,Label<List<String>>>> aut, 
//			Automaton<String,String,BasicState,ModalTransition<String,String,BasicState,Label<String>>> prop)
//	{
//		//converting prop 
//		Map<BasicState,CAState> bs2cs = prop.getStates().stream()
//				.collect(Collectors.toMap(Function.identity(), s->new CAState(Arrays.asList(s))));
//
//		Automaton<List<BasicState>,List<String>,CAState,ModalTransition<List<BasicState>,List<String>,CAState,Label<List<String>>>> prop_aut = 
//				new Automaton<>(prop.getTransition().parallelStream()
//						.map(t->new ModalTransition<List<BasicState>,List<String>,CAState,Label<List<String>>>(bs2cs.get(t.getSource()),
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
//		Map<BasicState,CAState> bs2cs = prop.getStates().stream()
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