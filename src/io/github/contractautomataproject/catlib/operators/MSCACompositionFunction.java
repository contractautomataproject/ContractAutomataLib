package io.github.contractautomataproject.catlib.operators;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.Ranked;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

/**
 * Class implementing the composition of ModalAutomaton<CALabel>
 * 
 * @author Davide Basile
 */

public class MSCACompositionFunction extends CompositionFunction<List<BasicState>,List<String>,CAState,CALabel,ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> {

	public MSCACompositionFunction(List<ModalAutomaton<CALabel>> aut,Predicate<CALabel> pruningPred)
	{
		super(aut, MSCACompositionFunction::computeRank,(l1,l2)->l1.match(l2),
				CAState::new, ModalTransition<List<BasicState>,List<String>,CAState,CALabel>::new, 
				(e, ee,rank) -> MSCACompositionFunction.createLabel(e, ee, rank, aut), 
				CALabel::new, ModalAutomaton<CALabel>::new, pruningPred);
	}

	private static Integer computeSumPrincipal(ModalTransition<List<BasicState>,List<String>,CAState,CALabel> etra, Integer eind, List<ModalAutomaton<CALabel>> aut)
	{
		return IntStream.range(0, eind)
				.map(i->aut.get(i).getRank())
				.sum()+etra.getLabel().getOffererOrRequester();
	}
	
	public static Integer computeRank(List<? extends Ranked> aut) {
		return aut.stream()
				.map(Ranked::getRank)
				.collect(Collectors.summingInt(Integer::intValue));
	}
	
	public static CALabel createLabel(TIndex e, TIndex ee, Integer rank,List<ModalAutomaton<CALabel>> aut) {
		return new CALabel(rank,
				computeSumPrincipal(e.tra,e.ind,aut),//index of principal in e
				computeSumPrincipal(ee.tra,ee.ind,aut),	//index of principal in ee										
				e.tra.getLabel().getTheAction(),ee.tra.getLabel().getTheAction());
	}
	
	@Override
	public ModalAutomaton<CALabel> apply(Integer bound)
	{
		return (ModalAutomaton<CALabel>) super.apply(bound);
	}


}


//END OF CLASS






//
//  **this is the composition method using Java 15, I translated back to Java 8 to use JML**
//
//	public static ModalAutomaton<CALabel> composition(List<ModalAutomaton<CALabel>> aut, Predicate<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> pruningPred, Integer bound)
//	{
//		//each transition of each ModalAutomaton<CALabel> in aut is associated with the corresponding index in aut
//		final class FMCATransitionIndex {//more readable than Entry
//			ModalTransition<List<BasicState>,List<String>,CAState,CALabel> tra;
//			Integer ind;
//			public FMCATransitionIndex(ModalTransition<List<BasicState>,List<String>,CAState,CALabel> tr, Integer i) {
//				this.tra=tr; //different principals may have equal transitions
//				this.ind=i;
//			}
//		}
//		
//		int rank=aut.stream()
//				.map(ModalAutomaton<CALabel>::getRank)
//				.collect(Collectors.summingInt(Integer::intValue));
//
//		List<CAState> initial = aut.stream()  
//				.flatMap(a -> a.getStates().stream())
//				.filter(CAState::isInitial)
//				.collect(Collectors.toList());
//		CAState initialstate = new CAState(initial);
//
//		Queue<Entry<List<CAState>,Integer>> toVisit = new ConcurrentLinkedQueue<Entry<List<CAState>,Integer>>(List.of(Map.entry(initial,0)));
//		ConcurrentMap<List<CAState>, CAState> operandstat2compstat = new ConcurrentHashMap<List<CAState>, CAState>(Map.of(initial, initialstate));//used to avoid duplicate target states 
//		Set<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>> tr = new HashSet<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>>();//transitions of the composed automaton to build
//		Set<List<CAState>> visited = new HashSet<List<CAState>>();
//		Queue<CAState> dontvisit = new ConcurrentLinkedQueue<CAState>();
//
//		do {
//			Entry<List<CAState>,Integer> sourceEntry=toVisit.remove(); //pop state to visit
//			if (visited.add(sourceEntry.getKey())&&sourceEntry.getValue()<bound) //if states has not been visited so far
//			{
//				List<CAState> source =sourceEntry.getKey();
//				CAState sourcestate= operandstat2compstat.get(source);
//				if (dontvisit.remove(sourcestate))
//					continue;//was target of a semicontrollable bad transition
//
//				List<FMCATransitionIndex> trans2index = IntStream.range(0,aut.size())
//						.mapToObj(i->aut.get(i)
//								.getForwardStar(source.get(i))
//								.parallelStream()
//								.map(t->new FMCATransitionIndex(t,i)))
//						.flatMap(Function.identity())
//						.collect(toList()); //indexing outgoing transitions of each operand, used for target states and labels
//
//				//firstly match transitions are generated
//				Map<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, List<Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>>> matchtransitions=
//						trans2index.parallelStream()
//						.collect(flatMapping(e -> trans2index.parallelStream()
//								.filter(ee->(e.ind<ee.ind) && CALabel.match(e.tra.getLabel(), ee.tra.getLabel()))
//								.flatMap(ee->{ 
//									List<CAState> targetlist =  new ArrayList<CAState>(source);
//									targetlist.set(e.ind, e.tra.getTarget());
//									targetlist.set(ee.ind, ee.tra.getTarget());
//									
//									ModalTransition<List<BasicState>,List<String>,CAState,CALabel> tradd=new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(sourcestate,
//											e.tra.getLabel().isOffer(), //since e.ind<ee.ind true if e is offer
//											(e.tra.getLabel().isOffer())?e.tra.getLabel().getAction():ee.tra.getLabel().getAction(),//offer action
//											operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)), 
//											e.tra.isNecessary()?e.tra.getModality():ee.tra.getModality());
//
//									return Stream.of((Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>>) 
//											Map.entry(e.tra, Map.entry(tradd,targetlist)),
//											(Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>>)//dummy, ee.tra is matched
//											Map.entry(ee.tra, Map.entry(tradd, (List<CAState>)new ArrayList<CAState>())));
//								}), 
//								groupingByConcurrent(Entry::getKey, 
//										mapping(Entry::getValue,toList()))//each principal transition can have more matches
//								));
//
//				//collecting match transitions and adding unmatched transitions
//				Set<Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>,List<CAState>>> trmap=
//						trans2index.parallelStream()
//						.filter(e->!matchtransitions.containsKey(e.tra))//transitions not matched
//						.collect(Collectors.collectingAndThen(
//								mapping(e->{List<CAState> targetlist = new ArrayList<CAState>(source);
//								targetlist.set(e.ind, e.tra.getTarget());
//								return 
//										Map.entry((!e.tra.getLabel().isMatch())?
//												new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(sourcestate,  
//														e.tra.getLabel().getAction(),
//														operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)),
//														e.tra.getModality())
//												:new ModalTransition<List<BasicState>,List<String>,CAState,CALabel>(sourcestate, 
//														e.tra.getLabel().getOfferer()<e.tra.getLabel().getRequester(), 
//														e.tra.getLabel().getAction(),
//														operandstat2compstat.computeIfAbsent(targetlist, v->new CAState(v)),
//													 	e.tra.getModality()),
//												targetlist);},
//										toSet()),
//								trm->{trm.addAll(matchtransitions.values().parallelStream()//matched transitions
//										.flatMap(List::parallelStream)
//										.filter(e->(!e.getValue().isEmpty())) //no duplicates
//										.collect(toSet()));
//								return trm;}));
//
//				if (trmap.parallelStream()//don't visit target states if they are bad
//						.anyMatch(x->pruningPred!=null&&pruningPred.test(x.getKey())&&x.getKey().isUrgent()))
//				{
//					if (sourcestate.equals(initialstate))
//						return null;
//					continue;
//				}
//				else {//adding transitions, updating states
//					tr.addAll(trmap.parallelStream()
//							.filter(x->pruningPred==null||x.getKey().isNecessary()||pruningPred.negate().test(x.getKey()))//semicontrollable are not pruned
//							.collect(Collectors.teeing(
//									mapping((Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, List<CAState>> e)-> e.getKey(),toSet()), 
//									mapping((Entry<ModalTransition<List<BasicState>,List<String>,CAState,CALabel>, List<CAState>> e)-> e.getValue(),toSet()), 
//									(trans,toVis)->{
//										toVisit.addAll(toVis.parallelStream()
//												.map(s->Map.entry(s,sourceEntry.getValue()+1))
//												.collect(toSet()));
//										if (pruningPred!=null)//avoid visiting targets of semicontrollable bad transitions
//											dontvisit.addAll(trans.parallelStream()
//													.filter(x->x.isSemiControllable()&&pruningPred.test(x))
//													.map(ModalTransition<List<BasicState>,List<String>,CAState,CALabel>::getTarget)
//													.collect(toList()));
//										return trans;
//									})));
//				}
//			}
//		} while (!toVisit.isEmpty());
//
//		int[][] finalstates = new int[rank][];
//		int pointer=0;
//		for (ModalAutomaton<CALabel> a : aut){
//			System.arraycopy(a.getFinalStatesofPrincipals(), 0, finalstates, pointer,a.getRank());
//			pointer+=a.getRank();
//		}
//		Set<CAState> states =visited.parallelStream()
//				.map(l->operandstat2compstat.get(l))
//				.collect(Collectors.toSet());
//		return new ModalAutomaton<CALabel>(rank, initialstate, finalstates, tr, states);
//	}

