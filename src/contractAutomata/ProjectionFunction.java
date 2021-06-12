package contractAutomata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ProjectionFunction implements TriFunction<MSCA,Integer,Function<MSCATransition, Integer>,MSCA> {
	/**
	 * compute the projection on the i-th principal
	 * @param indexprincipal index of the MSCA
	 * @param function returning the index of the necessary principal in a match transition (either the offerer or the requester), if any
	 * @return	the ith principal
	 * 
	 */
	@Override
	public MSCA apply(MSCA aut, Integer indexprincipal, Function<MSCATransition, Integer> getNecessaryPrincipal)
	{
		if ((indexprincipal<0)||(indexprincipal>aut.getRank())) 
			throw new IllegalArgumentException("Index out of rank");

		//extracting the basicstates of the principal and creating the castates of the projection
		Map<BasicState,CAState> bs2cs = aut.getTransition().parallelStream()
				.flatMap(t->Stream.of(t.getSource(), t.getTarget()))
				.map(s->s.getState().get(indexprincipal))
				.distinct()
				.collect(Collectors.toMap(Function.identity(), bs->new CAState(new ArrayList<BasicState>(Arrays.asList(bs)),0,0)));

		//associating each castate of the composition with the castate of the principal
		Map<CAState,CAState> map2princst = 
				aut.getTransition().parallelStream()
				.flatMap(t->Stream.of(t.getSource(), t.getTarget()))
				.distinct()
				.collect(Collectors.toMap(Function.identity(), s->bs2cs.get(s.getState().get(indexprincipal))));


		return new MSCA(aut.getTransition().parallelStream()
				.filter(t-> t.getLabel().isMatch()
						?(t.getLabel().getOfferer().equals(indexprincipal) || t.getLabel().getRequester().equals(indexprincipal))
								:t.getLabel().getOffererOrRequester().equals(indexprincipal))
				.map(t-> new MSCATransition(map2princst.get(t.getSource()),
						(!t.getLabel().isRequest()&&t.getLabel().getOfferer().equals(indexprincipal))?
								new CALabel(1,0,t.getLabel().getAction())
								:new CALabel(1,0,t.getLabel().isRequest()?t.getLabel().getAction()
										:t.getLabel().getCoAction()),
								map2princst.get(t.getTarget()),
								(t.isPermitted()||(t.getLabel().isMatch()&&!getNecessaryPrincipal.apply(t).equals(indexprincipal)))
								?MSCATransition.Modality.PERMITTED
										:t.isLazy()?MSCATransition.Modality.LAZY:MSCATransition.Modality.URGENT
						))
				.collect(Collectors.toSet()));
	}



}
