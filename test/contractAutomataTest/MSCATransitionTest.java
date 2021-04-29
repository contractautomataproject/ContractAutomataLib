package contractAutomataTest;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import contractAutomata.CALabel;
import contractAutomata.CAState;
import contractAutomata.MSCA;
import contractAutomata.MSCAIO;
import contractAutomata.MSCATransition;
import contractAutomata.MSCATransition.Modality;

public class MSCATransitionTest {
	
	@Test
	public void branchingCondition() throws NumberFormatException, IOException {
	
		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.load(dir+"/CAtest/violatingbranchingcondition.mxe.data");

		final Set<MSCATransition> trf = aut.getTransition();
		Set<MSCATransition> violatingBC = aut.getTransition().stream()
		.filter(x->!x.satisfiesBranchingCondition(trf, new HashSet<CAState>()))
		.collect(Collectors.toSet());
	
		
		assertEquals(violatingBC.size(),6);
	}
	
	@Test
	public void coverbranchingConditionException() {
		CAState source = new CAState(new int[] {0,1,2},true,false);
		CAState target = new CAState(new int[] {0,1,2},false,false);
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.idle);
		lab.add(CALabel.offer+"a");
		lab.add(CALabel.request+"a");
		CALabel calab= new CALabel(lab);

		assertThatThrownBy(() -> new MSCATransition(source,calab,target,null))
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
		CAState source = new CAState(new int[] {0,1,2},true,false);
		CAState target = new CAState(new int[] {0,1,2},false,false);
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.idle);
		lab.add(CALabel.offer+"a");
		lab.add(CALabel.request+"a");
		CALabel calab= new CALabel(lab);

		assertThatThrownBy(() -> new MSCATransition(source,calab,target,null))
        .isInstanceOf(RuntimeException.class)
        .hasMessageContaining("Ill-formed transition");
	}
	
	@Test
	public void constructorRankException() {
		CAState source = new CAState(new int[] {0,1,2},true,false);
		CAState target = new CAState(new int[] {0,1,2},false,false);
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.idle);
		lab.add(CALabel.idle);
		lab.add(CALabel.offer+"a");
		lab.add(CALabel.request+"a");
		CALabel calab= new CALabel(lab);

		assertThatThrownBy(() -> new MSCATransition(source,calab,target,Modality.PERMITTED))
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
	
	
}
