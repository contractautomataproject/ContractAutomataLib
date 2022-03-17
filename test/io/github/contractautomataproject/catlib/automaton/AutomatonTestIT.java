package io.github.contractautomataproject.catlib.automaton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;
import io.github.contractautomataproject.catlib.transition.ModalTransition.Modality;
import io.github.contractautomataproject.catlib.transition.Transition;

public class AutomatonTestIT {

	@Test
	public void testString() {
		BasicState<String> s0 = new BasicState<String>("0",true,false);
		BasicState<String> s1 = new BasicState<String>("1",false,true);
		BasicState<String> s2 = new BasicState<String>("2",false,true);
		Transition<String,String,BasicState<String>,Label<String>> t1 = new Transition<>(s0, new Label<String>("m"), s1);
		Transition<String,String,BasicState<String>,Label<String>> t2 = new Transition<>(s0, new Label<String>("m"), s2);
		
		Automaton<String,String, BasicState<String>,Transition<String,String, BasicState<String>,Label<String>>> prop = new Automaton<>(Set.of(t1,t2));
		
		String test = "Rank: 1"+System.lineSeparator() + 
				"Initial state: 0"+System.lineSeparator() + 
				"Final states: [[1, 2]]"+System.lineSeparator() + 
				"Transitions: "+System.lineSeparator() + 
				"(0,m,1)"+System.lineSeparator() + 
				"(0,m,2)"+System.lineSeparator();
		Assert.assertEquals(prop.toString(),test);
	}
	@Test
	public void constructor_Exception_differentRank() throws Exception {
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.IDLE);
		lab.add(CALabel.OFFER+"a");
		lab.add(CALabel.REQUEST+"a");

		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.IDLE);
		lab2.add(CALabel.IDLE);
		lab2.add(CALabel.OFFER+"a");
		lab2.add(CALabel.REQUEST+"a");


		BasicState<String> bs0 = new BasicState<String>("0",true,false);
		BasicState<String> bs1 = new BasicState<String>("1",true,false);
		BasicState<String> bs2 = new BasicState<String>("2",true,false);
		BasicState<String> bs3 = new BasicState<String>("3",true,false);

		Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>> tr = new HashSet<>();
		tr.add(new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(new CAState(Arrays.asList(bs0,bs1,bs2)//,0,0
				),
				new CALabel(lab),
				new CAState(Arrays.asList(bs0,bs1,bs3)),
				Modality.PERMITTED));
		CAState cs = new CAState(Arrays.asList(bs0,bs1,bs2,bs3)//,0,0
				);
		tr.add(new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(cs,
				new CALabel(lab2),
				cs,
				Modality.PERMITTED));

		Assert.assertThrows("Transitions with different rank", IllegalArgumentException.class, () -> new ModalAutomaton<CALabel>(tr));
	}


	@Test
	public void noInitialState_exception() throws Exception
	{
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.OFFER+"a");

		BasicState<String> bs0 = new BasicState<String>("0",false,true);
		BasicState<String> bs1 = new BasicState<String>("1",false,true);


		Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>> tr = new HashSet<>();
		tr.add(new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(new CAState(Arrays.asList(bs0)//,0,0
				),
				new CALabel(lab),
				new CAState(Arrays.asList(bs1)),
				Modality.PERMITTED));

		Assert.assertThrows("Not Exactly one Initial State found!", 
				IllegalArgumentException.class,
				() -> new ModalAutomaton<CALabel>(tr));
	}

	@Test
	public void noFinalStatesInTransitions_exception() throws Exception
	{
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.OFFER+"a");

		BasicState<String> bs0 = new BasicState<String>("0",true,false);
		BasicState<String> bs1 = new BasicState<String>("1",false,false);


		Set<ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>> tr = new HashSet<>();
		tr.add(new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(new CAState(Arrays.asList(bs0)//,0,0
				),
				new CALabel(lab),
				new CAState(Arrays.asList(bs1)),
				Modality.PERMITTED));

		Assert.assertThrows("No Final States!", 
				IllegalArgumentException.class,
				() -> new ModalAutomaton<CALabel>(tr));
	}

}
