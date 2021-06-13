package contractAutomataTest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import contractAutomata.MSCA;
import contractAutomata.converters.DataConverter;
import contractAutomata.converters.MxeConverter;
import contractAutomata.operators.ProjectionFunction;
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
