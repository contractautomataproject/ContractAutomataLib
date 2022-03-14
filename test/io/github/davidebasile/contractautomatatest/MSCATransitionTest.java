package io.github.davidebasile.contractautomatatest;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

public class MSCATransitionTest {
//	private final String dir = System.getProperty("user.dir");
	
	private final BasicState<String> bs0 = new BasicState<String>("0",true,false);
	private final BasicState<String> bs1 = new BasicState<String>("1",true,false);
	private final BasicState<String> bs2 = new BasicState<String>("2",true,false);
	private final BasicState<String> bs3 = new BasicState<String>("3",false,false);
	private final List<CAState> l = new ArrayList<>();
	private final List<String> lab = new ArrayList<>();
	private CALabel calab;
	private CAState source;
	private CAState target;
	private ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel> t1;
	
	@Before
	public void setup()
	{
		l.add(new CAState(Arrays.asList(bs0,bs1,bs2)//,0,0
				)); 
		l.add(new CAState(Arrays.asList(bs0,bs1,bs3)//,0,0
				));
		
		lab.add(CALabel.IDLE);
		lab.add(CALabel.OFFER+"a");
		lab.add(CALabel.REQUEST+"a");
		calab= new CALabel(lab);

		source = new CAState(Arrays.asList(bs0,bs1,bs2)//,0,0
				);
		target = new CAState(Arrays.asList(bs0,bs1,bs3)//,0,0
				);
		t1 = new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(source,calab,target,Modality.PERMITTED);

	}
	
	@Test
	public void coverbranchingConditionException() {
		//check if it is brcond involved	
		CAState source = l.get(0);
		CAState target = l.get(1);
		assertThatThrownBy(() -> new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(source,calab,target,null))
        .isInstanceOf(RuntimeException.class);
	}
	
	@Test
	public void coverConstructorException() {
		assertThatThrownBy(() -> new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(null,null,null,null))
        .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void coverModNullException() {	
		assertThatThrownBy(() -> new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(source,calab,target,null))
        .isInstanceOf(RuntimeException.class);
	}
	
	@Test
	public void constructorRankException() {
		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.IDLE);
		lab2.add(CALabel.IDLE);
		lab2.add(CALabel.OFFER+"a");
		lab2.add(CALabel.REQUEST+"a");
		CALabel calab2= new CALabel(lab2);

		assertThatThrownBy(() -> new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(source,calab2,target,Modality.PERMITTED))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("source, label or target with different ranks");
	}
	
//	
//	
//	@Test
//	public void toStringException() {
//		CAState source = new CAState(new int[] {0,1,2},true,false);
//		CAState target = new CAState(new int[] {0,1,2},false,false);
//		List<String> lab = new ArrayList<>();
//		lab.add(CALabel.idle);
//		lab.add(CALabel.offer+"a");
//		lab.add(CALabel.idle);
//		CALabel calab= new CALabel(lab);
//		ModalTransition<List<State<String>>,List<String>,CAState,CALabel> t = new ModalTransition<List<State<String>>,List<String>,CAState,CALabel>(source,calab,target,Modality.PERMITTED);
//		assertThatThrownBy(() -> t.)
//        .isInstanceOf(RuntimeException.class)
//        .hasMessageContaining("this transition is not a match");
//	}
	
	@Test
	public void testEquals() {
		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.IDLE);
		lab2.add(CALabel.OFFER+"a");
		lab2.add(CALabel.REQUEST+"a");
		CALabel calab2= new CALabel(lab2);
		
		ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel> t2 = new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(source,calab2,target,Modality.PERMITTED);

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
		
		ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel> t2 = new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(target,calab2,target,Modality.PERMITTED);

		Assert.assertNotEquals(t1,t2);
	}

	@Test
	public void testEquals4() {
		Transition<List<BasicState<String>>,List<String>,CAState,CALabel> t2 = new Transition<>(source,calab,target);
		
		Assert.assertNotEquals(t1,t2);
	}	
	
	
	@Test
	public void testEquals5() {
		ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel> t2 = new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(source,calab,target,Modality.URGENT);
		
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
		
		ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel> t2 = new ModalTransition<List<BasicState<String>>,List<String>,CAState,CALabel>(source,calab2,target,Modality.PERMITTED);

		Assert.assertNotEquals(t1,t2);
	}
}
