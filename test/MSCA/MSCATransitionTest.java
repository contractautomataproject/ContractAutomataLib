package MSCA;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import contractAutomata.CAState;
import contractAutomata.MSCA;
import contractAutomata.MSCAIO;
import contractAutomata.MSCATransition;

public class MSCATransitionTest {
	
	@Test
	public void extractRequestFromMatchTest() {
		
	}
	
	@Test
	public void branchingCondition() throws NumberFormatException, IOException {
	
		String dir = System.getProperty("user.dir");
		MSCA aut = MSCAIO.load(dir+"/CAtest/violatingbranchingcondition.mxe.data");

		final Set<MSCATransition> trf = aut.getTransition();
		Set<MSCATransition> violatingBC = aut.getTransition().stream()
		.filter(x->!x.satisfiesBranchingCondition(trf, new HashSet<CAState>()))
		.collect(Collectors.toSet());
	
		
		assert(violatingBC.size()==6);
	}
}
