package it.io.github.contractautomata.catlib.automaton.transition;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.label.action.OfferAction;
import io.github.contractautomata.catlib.automaton.label.action.RequestAction;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.transition.Transition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ITModalTransitionTest {
	
	private final BasicState<String> bs0 = new BasicState<>("0", true, false);
	private final BasicState<String> bs1 = new BasicState<>("1", true, false);
	private final BasicState<String> bs2 = new BasicState<>("2", true, false);
	private final BasicState<String> bs3 = new BasicState<>("3", false, false);
	private final List<State<String>> l = new ArrayList<>();
	private final List<Action> lab = new ArrayList<>();
	private CALabel calab;
	private State<String> source;
	private State<String> target;
	private ModalTransition<String, Action,State<String>,CALabel> t1;
	
	@Before
	public void setup()
	{
		l.add(new State<>(Arrays.asList(bs0, bs1, bs2)));
		l.add(new State<>(Arrays.asList(bs0, bs1, bs3)));
		
		lab.add(new IdleAction());
		lab.add(new OfferAction("a"));
		lab.add(new RequestAction("a"));
		calab= new CALabel(lab);

		source = new State<>(Arrays.asList(bs0, bs1, bs2));
		target = new State<>(Arrays.asList(bs0, bs1, bs3));
		t1 = new ModalTransition<>(source, calab, target, ModalTransition.Modality.PERMITTED);

	}
	
	@Test
	public void coverbranchingConditionException() {
		State<String> source = l.get(0);
		State<String> target = l.get(1);
		Assert.assertThrows(RuntimeException.class, () -> new ModalTransition<>(source, calab, target, null));
	}
	
	@Test
	public void coverConstructorException() {
		Assert.assertThrows(IllegalArgumentException.class, () -> new ModalTransition<String,Action,State<String>,CALabel>(null,null,null,null));
	}
	
	@Test
	public void coverModNullException() {	
		Assert.assertThrows(RuntimeException.class, () -> new ModalTransition<>(source, calab, target, null));
	}
	
	@Test
	public void constructorRankException() {
		List<Action> lab2 = new ArrayList<>();
		lab2.add(new IdleAction());
		lab2.add(new IdleAction());
		lab2.add(new OfferAction("a"));
		lab2.add(new RequestAction("a"));
		CALabel calab2= new CALabel(lab2);

		Assert.assertThrows("source, label or target with different ranks", 
				IllegalArgumentException.class,
				() -> new ModalTransition<>(source, calab2, target, ModalTransition.Modality.PERMITTED));
	}
	
	@Test
	public void testEquals() {
		List<Action> lab2 = new ArrayList<>();
		lab2.add(new IdleAction());
		lab2.add(new OfferAction("a"));
		lab2.add(new RequestAction("a"));
		CALabel calab2= new CALabel(lab2);
		
		ModalTransition<String,Action,State<String>,CALabel> t2 = new ModalTransition<>(source, calab2, target, ModalTransition.Modality.PERMITTED);

		assertEquals(t1,t2);
	}
	
	@Test
	public void testEquals2() {

		assertEquals(t1,t1);
	}	
	
	@Test
	public void testEquals3() {
		List<Action> lab2 = new ArrayList<>();
		lab2.add(new IdleAction());
		lab2.add(new OfferAction("a"));
		lab2.add(new RequestAction("a"));
		CALabel calab2= new CALabel(lab2);
		
		ModalTransition<String,Action,State<String>,CALabel> t2 = new ModalTransition<>(target, calab2, target, ModalTransition.Modality.PERMITTED);

		Assert.assertNotEquals(t1,t2);
	}

	@Test
	public void testEquals4() {
		Transition<String,Action,State<String>,CALabel> t2 = new Transition<>(source,calab,target);
		
		Assert.assertNotEquals(t1,t2);
	}	
	
	
	@Test
	public void testEquals5() {
		ModalTransition<String,Action,State<String>,CALabel> t2 = new ModalTransition<>(source, calab, target, ModalTransition.Modality.URGENT);
		
		Assert.assertNotEquals(t1,t2);
	}
	
	@Test
	public void testEquals6() {
		Assert.assertNotNull(t1);
	}	
	
	@Test
	public void testEquals7() {
		List<Action> lab2 = new ArrayList<>();
		lab2.add(new IdleAction());
		lab2.add(new OfferAction("b"));
		lab2.add(new RequestAction("b"));
		CALabel calab2= new CALabel(lab2);

		ModalTransition<String,Action,State<String>,CALabel> t2 = new ModalTransition<>(source, calab2, target, ModalTransition.Modality.PERMITTED);

		Assert.assertNotEquals(t1,t2);
	}
}
