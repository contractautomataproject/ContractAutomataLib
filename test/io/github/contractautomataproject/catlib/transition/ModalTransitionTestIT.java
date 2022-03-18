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
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.transition.ModalTransition;
import io.github.contractautomataproject.catlib.transition.ModalTransition.Modality;
import io.github.contractautomataproject.catlib.transition.Transition;

public class ModalTransitionTestIT {
	
	private final BasicState<String> bs0 = new BasicState<String>("0",true,false);
	private final BasicState<String> bs1 = new BasicState<String>("1",true,false);
	private final BasicState<String> bs2 = new BasicState<String>("2",true,false);
	private final BasicState<String> bs3 = new BasicState<String>("3",false,false);
	private final List<CAState<String>> l = new ArrayList<>();
	private final List<String> lab = new ArrayList<>();
	private CALabel calab;
	private CAState<String> source;
	private CAState<String> target;
	private ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel> t1;
	
	@Before
	public void setup()
	{
		l.add(new CAState<String>(Arrays.asList(bs0,bs1,bs2))); 
		l.add(new CAState<String>(Arrays.asList(bs0,bs1,bs3)));
		
		lab.add(CALabel.IDLE);
		lab.add(CALabel.OFFER+"a");
		lab.add(CALabel.REQUEST+"a");
		calab= new CALabel(lab);

		source = new CAState<String>(Arrays.asList(bs0,bs1,bs2));
		target = new CAState<String>(Arrays.asList(bs0,bs1,bs3));
		t1 = new ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>(source,calab,target,Modality.PERMITTED);

	}
	
	@Test
	public void coverbranchingConditionException() {
		CAState<String> source = l.get(0);
		CAState<String> target = l.get(1);
		Assert.assertThrows(RuntimeException.class, () -> new ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>(source,calab,target,null));
	}
	
	@Test
	public void coverConstructorException() {
		Assert.assertThrows(IllegalArgumentException.class, () -> new ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>(null,null,null,null));
	}
	
	@Test
	public void coverModNullException() {	
		Assert.assertThrows(RuntimeException.class, () -> new ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>(source,calab,target,null));
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
				() -> new ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>(source,calab2,target,Modality.PERMITTED));
	}
	
	@Test
	public void testEquals() {
		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.IDLE);
		lab2.add(CALabel.OFFER+"a");
		lab2.add(CALabel.REQUEST+"a");
		CALabel calab2= new CALabel(lab2);
		
		ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel> t2 = new ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>(source,calab2,target,Modality.PERMITTED);

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
		
		ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel> t2 = new ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>(target,calab2,target,Modality.PERMITTED);

		Assert.assertNotEquals(t1,t2);
	}

	@Test
	public void testEquals4() {
		Transition<List<BasicState<String>>,List<String>,CAState<String>,CALabel> t2 = new Transition<>(source,calab,target);
		
		Assert.assertNotEquals(t1,t2);
	}	
	
	
	@Test
	public void testEquals5() {
		ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel> t2 = new ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>(source,calab,target,Modality.URGENT);
		
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
		
		ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel> t2 = new ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>(source,calab2,target,Modality.PERMITTED);

		Assert.assertNotEquals(t1,t2);
	}
}
