package io.github.davidebasile.contractautomatatest.operatorsTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.operators.MpcSynthesisOperator;
import io.github.contractautomataproject.catlib.requirements.Agreement;
import io.github.davidebasile.contractautomatatest.MSCATest;


public class MpcSynthesisTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);

	@Test
	public void mpcEmptyTestLMCS2020() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(ClientxClientxBrokerxHotelxPriviledgedUrgentHotel).data");
		ModalAutomaton<CALabel> mpc=new MpcSynthesisOperator(new Agreement()).apply(aut);
		Assert.assertNull(mpc);
	}

	@Test
	public void mpcEmptyTestNoDangling() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_empty_mpc_nodangling.data");
		ModalAutomaton<CALabel> mpc=new MpcSynthesisOperator(new Agreement()).apply(aut);
		Assert.assertNull(mpc);
	}
	
	@Test
	public void mpcTest_nonempty() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_urgent.data");
		assertEquals(new MpcSynthesisOperator(new Agreement()).apply(aut).getNumStates(),2);
	}

	@Test 
	public void mpcTest2() throws Exception
	{
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"test_urgent.data");		
		ModalAutomaton<CALabel> test = bdc.importMSCA(dir + File.separator + "test_urgent_mpc_agreement.data");
		assertTrue(MSCATest.autEquals(new MpcSynthesisOperator(new Agreement()).apply(aut),test));	
	}
	
	@Test
	public void mpc_lazy_exception() throws Exception
	{

		ModalAutomaton<CALabel> orc = bdc.importMSCA(dir+"test_empty_orc_lazy.data");
		MpcSynthesisOperator m = new MpcSynthesisOperator(new Agreement());
		assertThatThrownBy(() -> m.apply(orc))
		.isInstanceOf(UnsupportedOperationException.class);
	}


}
