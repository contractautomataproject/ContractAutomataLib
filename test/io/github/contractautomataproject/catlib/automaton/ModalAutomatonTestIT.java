package io.github.contractautomataproject.catlib.automaton;


import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;
import io.github.contractautomataproject.catlib.transition.ModalTransition.Modality;

public class ModalAutomatonTestIT {

	@Test
	public void ambiguousStates_exception() throws Exception
	{
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.OFFER+"a");

		BasicState<String> bs1 = new BasicState<String>("0",true,false);
		BasicState<String> bs2 = new BasicState<String>("0",false,true);

		Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>> tr = new HashSet<>();
		tr.add(new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(new CAState(Arrays.asList(bs1)),
				new CALabel(lab),
				new CAState(Arrays.asList(bs2)),
				Modality.PERMITTED));

		tr.add(new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(new CAState(Arrays.asList(bs2)),
				new CALabel(lab),
				new CAState(Arrays.asList(bs2)),
				Modality.PERMITTED));
		assertThatThrownBy(() -> new ModalAutomaton<CALabel>(tr))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Transitions have ambiguous states (different objects for the same state).");
	}
}