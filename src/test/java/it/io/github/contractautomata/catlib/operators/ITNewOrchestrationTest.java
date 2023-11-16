package it.io.github.contractautomata.catlib.operators;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.AutomatonTest;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import io.github.contractautomata.catlib.operations.NewOrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.requirements.Agreement;
import it.io.github.contractautomata.catlib.automaton.ITAutomatonTest;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;

import static org.junit.Assert.assertTrue;

public class ITNewOrchestrationTest {
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	
	@Before
	public void setup() {
	}


	@Test
	public void testOrcCardsGameICE2023() throws IOException {
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> dealer = bdc.importMSCA(ITAutomatonTest.dir + "Dealer.data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> player = bdc.importMSCA(ITAutomatonTest.dir + "Player.data");
		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc = new NewOrchestrationSynthesisOperator(new Agreement()).apply(Arrays.asList(dealer,player,player));

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> test = bdc.importMSCA(ITAutomatonTest.dir+ "NewOrc_(DealerxPlayerxPlayer).data");
		assertTrue(AutomatonTest.autEquals(orc, test));
	}

}