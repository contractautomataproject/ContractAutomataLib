package contractAutomataTest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import contractAutomata.CAState;
import contractAutomata.MSCA;
import contractAutomata.converters.DataConverter;
import contractAutomata.converters.MxeConverter;
import contractAutomata.operators.ChoreographySynthesisOperator;
import contractAutomata.operators.CompositionFunction;
import contractAutomata.operators.ProjectionFunction;
import contractAutomata.requirements.StrongAgreement;
import contractAutomataTest.MSCATest;

public class ProjectionTest {
	

	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final MxeConverter bmc = new MxeConverter();
	private final DataConverter bdc = new DataConverter();
	
	@Test
	public void projectionTestSCP2020_BusinessClient() throws Exception{
		MSCA aut = bmc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).mxe");
		MSCA test= bmc.importMSCA(dir+"BusinessClient.mxe");
		aut=new ProjectionFunction().apply(aut,0, t->t.getLabel().getRequester());
		//		System.out.println(aut);
		//		System.out.println(test);
		assertEquals(MSCATest.checkTransitions(aut,test),true);

	}

	@Test
	public void choreoConcur2021projectAndComposeTest() throws Exception {
		MSCA aut = bmc.importMSCA(dir+"testcor_concur21_Example34.mxe");
		List<MSCA> principals = IntStream.range(0,aut.getRank())
				.mapToObj(i->new ProjectionFunction().apply(aut,i, t->t.getLabel().getOfferer()))
				.collect(Collectors.toList());
		//	System.out.println(principals);
		MSCA closed_aut = new CompositionFunction().apply(principals, t->!t.getLabel().isMatch(), 100);
		//	MSCAIO.convertMSCAintoXML(dir+"testcor_concur21_Example34_closed_composition.mxe", closed_aut);

		boolean bc = closed_aut.getTransition().stream()
				.allMatch(t->new ChoreographySynthesisOperator(new StrongAgreement()).satisfiesBranchingCondition(t,aut.getTransition(), 
						new HashSet<CAState>()));
		assertEquals(bc,false);	
	}
	
	//************************************exceptions*********************************************

	@Test
	public void projectionException1() throws IOException {

		MSCA aut = bdc.importMSCA(dir+"BusinessClient.mxe.data");
		assertThatThrownBy(() -> new ProjectionFunction().apply(aut,-1, null))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Index out of rank");

	}

	@Test
	public void projectionException2() throws IOException {

		MSCA aut = bdc.importMSCA(dir+"BusinessClient.mxe.data");
		assertThatThrownBy(() -> new ProjectionFunction().apply(aut,2, null))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("Index out of rank");

	}


}
