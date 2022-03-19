package io.github.contractautomataproject.catlib.transition;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.transition.ModalTransition.Modality;

public class ITModalTransitionTest {
	
	private final BasicState<String> bs0 = new BasicState<>("0", true, false);
	private final BasicState<String> bs1 = new BasicState<>("1", true, false);
	private final BasicState<String> bs2 = new BasicState<>("2", true, false);
	private final BasicState<String> bs3 = new BasicState<>("3", false, false);
	private final List<State<String>> l = new ArrayList<>();
	private final List<String> lab = new ArrayList<>();
	private CALabel calab;
	private State<String> source;
	private State<String> target;
	private ModalTransition<String,String,State<String>,CALabel> t1;
	
	@Before
	public void setup()
	{
		l.add(new State<>(Arrays.asList(bs0, bs1, bs2)));
		l.add(new State<>(Arrays.asList(bs0, bs1, bs3)));
		
		lab.add(CALabel.IDLE);
		lab.add(CALabel.OFFER+"a");
		lab.add(CALabel.REQUEST+"a");
		calab= new CALabel(lab);

		source = new State<>(Arrays.asList(bs0, bs1, bs2));
		target = new State<>(Arrays.asList(bs0, bs1, bs3));
		t1 = new ModalTransition<>(source, calab, target, Modality.PERMITTED);

	}
	
	@Test
	public void coverbranchingConditionException() {
		State<String> source = l.get(0);
		State<String> target = l.get(1);
		Assert.assertThrows(RuntimeException.class, () -> new ModalTransition<>(source, calab, target, null));
	}
	
	@Test
	public void coverConstructorException() {
		Assert.assertThrows(IllegalArgumentException.class, () -> new ModalTransition<String,String,State<String>,CALabel>(null,null,null,null));
	}
	
	@Test
	public void coverModNullException() {	
		Assert.assertThrows(RuntimeException.class, () -> new ModalTransition<>(source, calab, target, null));
	}
	
	@Test
	public void constructorRankException() {
		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.IDLE);
		lab2.add(CALabel.IDLE);
		lab2.add(CALabel.OFFER+"a");
		lab2.add(CALabel.REQUEST+"a");
		CALabel calab2= new CALabel(lab2);

		Assert.assertThrows("source, label or target with different ranks", 
				IllegalArgumentException.class,
				() -> new ModalTransition<>(source, calab2, target, Modality.PERMITTED));
	}
	
	@Test
	public void testEquals() {
		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.IDLE);
		lab2.add(CALabel.OFFER+"a");
		lab2.add(CALabel.REQUEST+"a");
		CALabel calab2= new CALabel(lab2);
		
		ModalTransition<String,String,State<String>,CALabel> t2 = new ModalTransition<>(source, calab2, target, Modality.PERMITTED);

		assertEquals(t1,t2);
	}
	
	@Test
	public void testEquals2() {

		assertEquals(t1,t1);
	}	
	
	@Test
	public void testEquals3() {
		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.IDLE);
		lab2.add(CALabel.OFFER+"a");
		lab2.add(CALabel.REQUEST+"a");
		CALabel calab2= new CALabel(lab2);
		
		ModalTransition<String,String,State<String>,CALabel> t2 = new ModalTransition<>(target, calab2, target, Modality.PERMITTED);

		Assert.assertNotEquals(t1,t2);
	}

	@Test
	public void testEquals4() {
		Transition<String,String,State<String>,CALabel> t2 = new Transition<>(source,calab,target);
		
		Assert.assertNotEquals(t1,t2);
	}	
	
	
	@Test
	public void testEquals5() {
		ModalTransition<String,String,State<String>,CALabel> t2 = new ModalTransition<>(source, calab, target, Modality.URGENT);
		
		Assert.assertNotEquals(t1,t2);
	}
	
	@Test
	public void testEquals6() {
		Assert.assertNotNull(t1);
	}	
	
	@Test
	public void testEquals7() {
		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.IDLE);
		lab2.add(CALabel.OFFER+"b");
		lab2.add(CALabel.REQUEST+"b");
		CALabel calab2= new CALabel(lab2);
		
		ModalTransition<String,String,State<String>,CALabel> t2 = new ModalTransition<>(source, calab2, target, Modality.PERMITTED);

		Assert.assertNotEquals(t1,t2);
	}
}
