package it.io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.AutomatonTest;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.operations.MSCACompositionFunction;
import io.github.contractautomata.catlib.operations.SplittingOrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.requirements.Agreement;
import io.github.contractautomata.catlib.requirements.StrongAgreement;
import it.io.github.contractautomata.catlib.automaton.ITAutomatonTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class ITNewOrchestrationTest {
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	private Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,Label<Action>>> prop;
	@Before
	public void setup() {
		BasicState<String> s0 = new BasicState<>("WaitFirstCard", true, false, false);
		BasicState<String> s1 = new BasicState<>("WaitSecondCard", false, false, false);
		BasicState<String> s2 = new BasicState<>("Go", false, true, false);
		State<String> cs0 = new State<>(List.of(s0));
		State<String> cs1 = new State<>(List.of(s1));
		State<String> cs2 = new State<>(List.of(s2));

		Set<ModalTransition<String,Action,State<String>, Label<Action>>> setTr = new HashSet<>();
		setTr.add(new ModalTransition<>(cs0, new Label<>(List.of(new Action("pair1"))), cs1, ModalTransition.Modality.PERMITTED));
		setTr.add(new ModalTransition<>(cs0, new Label<>(List.of(new Action("pair2"))), cs1, ModalTransition.Modality.PERMITTED));
		setTr.add(new ModalTransition<>(cs0, new Label<>(List.of(new Action("pair3"))), cs1, ModalTransition.Modality.PERMITTED));

		setTr.add(new ModalTransition<>(cs1, new Label<>(List.of(new Action("pair1"))), cs2, ModalTransition.Modality.PERMITTED));
		setTr.add(new ModalTransition<>(cs1, new Label<>(List.of(new Action("pair2"))), cs2, ModalTransition.Modality.PERMITTED));
		setTr.add(new ModalTransition<>(cs1, new Label<>(List.of(new Action("pair3"))), cs2, ModalTransition.Modality.PERMITTED));


		setTr.add(new ModalTransition<>(cs2, new Label<>(List.of(new Action("1"))), cs2, ModalTransition.Modality.PERMITTED));
		setTr.add(new ModalTransition<>(cs2, new Label<>(List.of(new Action("2"))), cs2, ModalTransition.Modality.PERMITTED));
		setTr.add(new ModalTransition<>(cs2, new Label<>(List.of(new Action("3"))), cs2, ModalTransition.Modality.PERMITTED));
		setTr.add(new ModalTransition<>(cs2, new Label<>(List.of(new Action("4"))), cs2, ModalTransition.Modality.PERMITTED));

		prop = new Automaton<>(setTr);
	}

	@Test
	public void testOrcCardsGameICE2023modelcheck() throws IOException {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> dealer = bdc.importMSCA(ITAutomatonTest.dir + "DealerNoCommitted.data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> player = bdc.importMSCA(ITAutomatonTest.dir + "Player.data");

		SplittingOrchestrationSynthesisOperator os = new SplittingOrchestrationSynthesisOperator(new StrongAgreement(),prop);
		os.setIgnoreModality();
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = os.apply(List.of(dealer,player,player));

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(ITAutomatonTest.dir+ "NewOrc_(DealerxPlayerxPlayer)_modelcheck.data");

		assertTrue(AutomatonTest.autEquals(orc, test));
	}

	@Test
	public void testOrcCardsGameICE2023comp() throws IOException {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> dealer = bdc.importMSCA(ITAutomatonTest.dir + "Dealer.data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> player = bdc.importMSCA(ITAutomatonTest.dir + "Player.data");

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> comp =
			new MSCACompositionFunction<>(List.of(dealer,player,player), t->new StrongAgreement().negate().test(t.getLabel()) ).apply(Integer.MAX_VALUE);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = new SplittingOrchestrationSynthesisOperator(new StrongAgreement()).apply(comp);

		//bdc.exportMSCA(ITAutomatonTest.dir+ "NewOrc_(DealerxPlayerxPlayer).data",orc);
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(ITAutomatonTest.dir+ "NewOrc_(DealerxPlayerxPlayer).data");
		assertTrue(AutomatonTest.autEquals(orc, test));
	}


	@Test
	public void testOrcCardsGameICE2023() throws IOException {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> dealer = bdc.importMSCA(ITAutomatonTest.dir + "Dealer.data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> player = bdc.importMSCA(ITAutomatonTest.dir + "Player.data");

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = new SplittingOrchestrationSynthesisOperator(new StrongAgreement()).apply(List.of(dealer,player,player));
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(ITAutomatonTest.dir+ "NewOrc_(DealerxPlayerxPlayer).data");
		assertTrue(AutomatonTest.autEquals(orc, test));
	}


	//------------EXCEPTIONS--------------

	@Test
	public void test_necessaryoffer_exception() throws Exception
	{
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(ITAutomatonTest.dir+ "PriviledgedClient.data");
		assertThrows("Some automaton contains necessary offers that are not allowed in the orchestration synthesis or some action is labelled with tau_", IllegalArgumentException.class, () ->  new SplittingOrchestrationSynthesisOperator(new Agreement()).apply(List.of(aut)));
	}

	@Test
	public void test_onlyprincipals_exception() throws Exception
	{
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(ITAutomatonTest.dir+ "(AxB).data");
		assertThrows("Only principals are allowed", IllegalArgumentException.class, () ->  new SplittingOrchestrationSynthesisOperator(new Agreement()).apply(List.of(aut)));
	}


	@Test
	public void testExceptionTau() throws IOException {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> player = bdc.importMSCA(ITAutomatonTest.dir + "PlayerEncoded.data");
		assertThrows("Some automaton contains necessary offers that are not allowed in the orchestration synthesis or some action is labelled with tau_",IllegalArgumentException.class,() -> new SplittingOrchestrationSynthesisOperator(new Agreement()).apply(List.of(player)));

	}


}


//	@Test
//	public void testEncoding() throws IOException {
//
//		State<String> s0 = new State<>(List.of( new BasicState<>("0",true,false), new BasicState<>("0",true,false)));
//		State<String> s1 = new State<>(List.of(new BasicState<>("1",false,false),new BasicState<>("1",false,false)));
//		State<String> s2 = new State<>(List.of(new BasicState<>("2",false,false),new BasicState<>("2",false,false)));
//		State<String> s3 = new State<>(List.of(new BasicState<>("3",false,true),new BasicState<>("3",false,true)));
//
//		Set<ModalTransition<String,Action,State<String>, CALabel>> setTr = new HashSet<>();
//		setTr.add(new ModalTransition<>(s0, new CALabel(List.of(new RequestAction("a"), new OfferAction("a"))), s1, ModalTransition.Modality.LAZY));
//		setTr.add(new ModalTransition<>(s0, new CALabel(List.of(new RequestAction("b"), new OfferAction("b"))), s2, ModalTransition.Modality.LAZY));
//		setTr.add(new ModalTransition<>(s2, new CALabel(List.of(new OfferAction("e"), new RequestAction("e"))), s1, ModalTransition.Modality.LAZY));
//		setTr.add(new ModalTransition<>(s1, new CALabel(List.of(new RequestAction("c"), new OfferAction("c"))), s3, ModalTransition.Modality.LAZY));
//		setTr.add(new ModalTransition<>(s2, new CALabel(List.of(new RequestAction("d"), new OfferAction("d"))), s3, ModalTransition.Modality.LAZY));
//
//		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = new Automaton<>(setTr);
//
//	//	bdc.exportMSCA("test.data", new NewOrchestrationSynthesisOperator(new StrongAgreement()).apply(aut));
//
//	}