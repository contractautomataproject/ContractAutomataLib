package contractAutomataTest;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import contractAutomata.BasicState;
import contractAutomata.CALabel;
import contractAutomata.CAState;
import contractAutomata.CATransition;
import contractAutomata.MSCA;
import contractAutomata.MSCATransition;
import contractAutomata.MSCATransition.Modality;
import contractAutomata.converters.DataConverter;
import contractAutomata.operators.ChoreographySynthesisOperator;
import contractAutomata.requirements.StrongAgreement;

public class MSCATransitionTest {
	private final String dir = System.getProperty("user.dir");
	
	private final BasicState bs0 = new BasicState("0",true,false);
	private final BasicState bs1 = new BasicState("1",true,false);
	private final BasicState bs2 = new BasicState("2",true,false);
	private final BasicState bs3 = new BasicState("3",false,false);
	private final List<CAState> l = new ArrayList<>();
	private final List<String> lab = new ArrayList<>();
	private CALabel calab;
	private CAState source;
	private CAState target;
	private MSCATransition t1;
	
	@Before
	public void setup()
	{
		l.add(new CAState(Arrays.asList(bs0,bs1,bs2),0,0)); 
		l.add(new CAState(Arrays.asList(bs0,bs1,bs3),0,0));
		
		lab.add(CALabel.idle);
		lab.add(CALabel.offer+"a");
		lab.add(CALabel.request+"a");
		calab= new CALabel(lab);

		source = new CAState(Arrays.asList(bs0,bs1,bs2),0,0);
		target = new CAState(Arrays.asList(bs0,bs1,bs3),0,0);
		t1 = new MSCATransition(source,calab,target,Modality.PERMITTED);

	}
	
	@Test
	public void branchingCondition() throws NumberFormatException, IOException {
	
		MSCA aut = new DataConverter().importMSCA(dir+"/CAtest/violatingbranchingcondition.mxe.data");

		final Set<MSCATransition> trf = aut.getTransition();
		Set<MSCATransition> violatingBC = aut.getTransition().stream()
		.filter(x->!new ChoreographySynthesisOperator(new StrongAgreement()).satisfiesBranchingCondition(x,trf, new HashSet<CAState>()))
		.collect(Collectors.toSet());
	
		
		assertEquals(violatingBC.size(),6);
	}
	
	@Test
	public void coverbranchingConditionException() {
		//check if it is brcond involved	
		assertThatThrownBy(() -> new MSCATransition(l.get(0),calab,l.get(1),null))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Ill-formed transition");
	}
	
	@Test
	public void coverConstructorException() {
		assertThatThrownBy(() -> new MSCATransition(null,null,null,null))
        .isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void coverModNullException() {	
		assertThatThrownBy(() -> new MSCATransition(source,calab,target,null))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Ill-formed transition");
	}
	
	@Test
	public void constructorRankException() {
		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.idle);
		lab2.add(CALabel.idle);
		lab2.add(CALabel.offer+"a");
		lab2.add(CALabel.request+"a");
		CALabel calab2= new CALabel(lab2);

		assertThatThrownBy(() -> new MSCATransition(source,calab2,target,Modality.PERMITTED))
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
//		MSCATransition t = new MSCATransition(source,calab,target,Modality.PERMITTED);
//		assertThatThrownBy(() -> t.)
//        .isInstanceOf(RuntimeException.class)
//        .hasMessageContaining("this transition is not a match");
//	}
	
	@Test
	public void testEquals() {
		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.idle);
		lab2.add(CALabel.offer+"a");
		lab2.add(CALabel.request+"a");
		CALabel calab2= new CALabel(lab2);
		
		MSCATransition t2 = new MSCATransition(source,calab2,target,Modality.PERMITTED);

		assertEquals(t1.equals(t2),true);
	}
	
	@Test
	public void testEquals2() {

		assertEquals(t1.equals(t1),true);
	}	
	
	@Test
	public void testEquals3() {
		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.idle);
		lab2.add(CALabel.offer+"a");
		lab2.add(CALabel.request+"a");
		CALabel calab2= new CALabel(lab2);
		
		MSCATransition t2 = new MSCATransition(target,calab2,target,Modality.PERMITTED);

		assertEquals(t1.equals(t2),false);
	}

	@Test
	public void testEquals4() {
		CATransition t2 = new CATransition(source,calab,target);
		
		assertEquals(t1.equals(t2),false);
	}	
	
	
	@Test
	public void testEquals5() {
		MSCATransition t2 = new MSCATransition(source,calab,target,Modality.URGENT);
		
		assertEquals(t1.equals(t2),false);
	}
	
	@Test
	public void testEquals6() {
		assertFalse(t1.equals(null));
	}	
	
	@Test
	public void testEquals7() {
		List<String> lab2 = new ArrayList<>();
		lab2.add(CALabel.idle);
		lab2.add(CALabel.offer+"b");
		lab2.add(CALabel.request+"b");
		CALabel calab2= new CALabel(lab2);
		
		MSCATransition t2 = new MSCATransition(source,calab2,target,Modality.PERMITTED);

		assertEquals(t1.equals(t2),false);
	}
}
