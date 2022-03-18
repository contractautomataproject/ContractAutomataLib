package io.github.contractautomataproject.catlib.operators;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.AutomatonTestIT;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.requirements.Agreement;
import io.github.contractautomataproject.catlib.spec.CompositionSpecCheck;
import io.github.contractautomataproject.catlib.transition.ModalTransition;

public class MSCACompositionTest {

	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);

	//***********************************testing impl against spec on scenarios **********************************************
	
	@Test
	public void scico2020Test() throws Exception{
		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.data"));
		assertTrue(new CompositionSpecCheck().test(aut,new MSCACompositionFunction(aut,null).apply(100)));
	}

	@Test
	public void lmcs2020Test() throws Exception{
		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"Client.data"));
		aut.add(bdc.importMSCA(dir+"Client.data"));
		aut.add(bdc.importMSCA(dir+"Broker.data"));
		aut.add(bdc.importMSCA(dir+"HotelLMCS.data"));
		aut.add(bdc.importMSCA(dir+"PriviledgedHotel.data"));
		
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp =new MSCACompositionFunction(aut,null).apply(100);
		
		assertTrue(new CompositionSpecCheck().test(aut,comp));
	}



	@Test
	public void lmcs2020Test2() throws Exception{
		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"Client.data"));
		aut.add(bdc.importMSCA(dir+"PriviledgedClient.data"));
		aut.add(bdc.importMSCA(dir+"Broker.data"));
		aut.add(bdc.importMSCA(dir+"HotelLMCS.data"));
		aut.add(bdc.importMSCA(dir+"HotelLMCS.data"));
		
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp =new MSCACompositionFunction(aut,null).apply(100);
		
		assertTrue(new CompositionSpecCheck().test(aut,comp));
	}
	
	//**********************************SCICO2020 case study*******************************************************************


	@Test
	public void compositionTestSCP2020_BusinessClientxHotel_open() throws Exception {
		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.data"));

		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp=new MSCACompositionFunction(aut,null).apply(100);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> test = bdc.importMSCA(dir+"BusinessClientxHotel_open.data");
		assertTrue(AutomatonTestIT.autEquals(comp,test));
	}

	@Test
	public void compositionTestSCP2020_nonassociative() throws Exception {
		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"BusinessClientxHotel_open.data"));

		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp=new MSCACompositionFunction(aut,null).apply(100);
		Assert.assertNull(new OrchestrationSynthesisOperator(new Agreement()).apply(comp));
	}

	@Test
	public void compositionTestSCP2020_BusinessClientxHotel_closed() throws Exception {
		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.data"));

		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp=new MSCACompositionFunction(aut,l->l.isRequest()).apply(100);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> test = bdc.importMSCA(dir+"BusinessClientxHotel_closed.data");
		assertTrue(AutomatonTestIT.autEquals(comp,test));
	}



	@Test
	public void compositionTestSCP2020_BusinessClientxHotelxEconomyClient_open_transitions() throws Exception {
		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.data"));
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp = new MSCACompositionFunction(aut,null).apply(100);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> test= bdc.importMSCA(dir+"BusinessClientxHotelxEconomyClient.data");
		assertTrue(AutomatonTestIT.autEquals(comp,test));	
	}

	@Test
	public void compAndOrcTestSCP2020_BusinessClientxHotelxEconomyClient() throws Exception
	{
		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"BusinessClient.data"));
		aut.add(bdc.importMSCA(dir+"Hotel.data"));
		aut.add(bdc.importMSCA(dir+"EconomyClient.data"));
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp=new MSCACompositionFunction(aut,l->l.isRequest()).apply(100);

		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> test= bdc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient).data");
		assertTrue(AutomatonTestIT.autEquals(new OrchestrationSynthesisOperator(new Agreement()).apply(comp),test));
	}	

	///////////////

	@Test
	public void compTestSimple() throws Exception
	{
		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"A.data"));
		aut.add(bdc.importMSCA(dir+"B.data"));

		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp=new MSCACompositionFunction(aut,null).apply(100);
		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> test = bdc.importMSCA(dir+"(AxB).data");

		assertTrue(AutomatonTestIT.autEquals(comp,test));
	}


	@Test
	public void compTestEmptySimple() throws Exception
	{
		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"forNullClosedAgreementComposition.data"));
		aut.add(bdc.importMSCA(dir+"forNullClosedAgreementComposition.data"));

		Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>> comp=new MSCACompositionFunction(aut,l->l.isRequest()).apply(100);

		Assert.assertNull(comp);
	}

	@Test
	public void compTestBound_noTransitions() throws Exception
	{
		List<Automaton<String,String,State<String>,ModalTransition<String,String,State<String>,CALabel>>> aut = new ArrayList<>(2);

		aut.add(bdc.importMSCA(dir+"forNullClosedAgreementComposition.data"));
		aut.add(bdc.importMSCA(dir+"forNullClosedAgreementComposition.data"));

		MSCACompositionFunction mcf = new MSCACompositionFunction(aut,null);
		assertThatThrownBy(() -> mcf.apply(0))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("No transitions");
	}

}


