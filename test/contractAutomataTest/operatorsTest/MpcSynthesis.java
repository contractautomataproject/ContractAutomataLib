package contractAutomataTest.operatorsTest;

import static contractAutomataTest.MSCATest.checkTransitions;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import contractAutomata.MSCA;
import contractAutomata.converters.MxeConverter;
import contractAutomata.operators.MpcSynthesisOperator;
import contractAutomata.requirements.Agreement;


public class MpcSynthesis {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final MxeConverter bmc = new MxeConverter();
//	private final DataConverter bdc = new DataConverter();

	@Test
	public void mpcEmptyTestLMCS2020() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedUrgentHotel).mxe");
		MSCA mpc=new MpcSynthesisOperator(new Agreement()).apply(aut);

		assertEquals(mpc,null);
	}

	@Test 
	public void mpcEmptyTestLMCS20202() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedUrgentHotel).mxe");
		new MpcSynthesisOperator(new Agreement()).apply(aut);
		assertEquals(new MpcSynthesisOperator(new Agreement()).apply(aut),null);	
	}


	@Test
	public void mpcEmptyTestNoDangling() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"test_empty_mpc_nodangling.mxe");
		MSCA mpc=new MpcSynthesisOperator(new Agreement()).apply(aut);

		assertEquals(mpc,null);
	}
	
	@Test
	public void mpcTest_nonempty() throws Exception
	{

		MSCA aut = bmc.importMSCA(dir+"test_urgent.mxe");
		assertEquals(new MpcSynthesisOperator(new Agreement()).apply(aut).getNumStates(),2);
	}

	@Test 
	public void mpcTest2() throws Exception
	{
		MSCA aut = bmc.importMSCA(dir+"test_urgent.mxe");
		new MpcSynthesisOperator(new Agreement()).apply(aut);
		assertTrue(checkTransitions(new MpcSynthesisOperator(new Agreement()).apply(aut),new MpcSynthesisOperator(new Agreement()).apply(aut)));	
	}
	
	@Test
	public void mpc_lazy_exception() throws Exception
	{

		MSCA orc = bmc.importMSCA(dir+"test_empty_orc_lazy.mxe");
		assertThatThrownBy(() -> new MpcSynthesisOperator(new Agreement()).apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}


}
