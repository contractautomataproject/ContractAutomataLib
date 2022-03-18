package io.github.contractautomataproject.catlib.automaton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.state.BasicState;
import io.github.contractautomataproject.catlib.automaton.state.CAState;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.transition.ModalTransition;
import io.github.contractautomataproject.catlib.transition.ModalTransition.Modality;

public class ModalAutomatonTestIT {
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	
	@Test
	public void ambiguousStates_exception() throws Exception
	{
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.OFFER+"a");

		BasicState<String> bs1 = new BasicState<String>("0",true,false);
		BasicState<String> bs2 = new BasicState<String>("0",false,true);

		Set<ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>> tr = new HashSet<>();
		tr.add(new ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>(new CAState<String>(Arrays.asList(bs1)),
				new CALabel(lab),
				new CAState<String>(Arrays.asList(bs2)),
				Modality.PERMITTED));

		tr.add(new ModalTransition<List<BasicState<String>>,List<String>,CAState<String>,CALabel>(new CAState<String>(Arrays.asList(bs2)),
				new CALabel(lab),
				new CAState<String>(Arrays.asList(bs2)),
				Modality.PERMITTED));
		Assert.assertThrows("Transitions have ambiguous states (different objects for the same state).", 
				IllegalArgumentException.class,
				() -> new ModalAutomaton<CALabel>(tr));
	}
	
	@Test
	public void testToString() throws IOException {
		ModalAutomaton<CALabel> aut= bdc.importMSCA(dir+"Orc_(BusinessClientxHotelxEconomyClient).data");
		String test ="Rank: 3"+System.lineSeparator()+
				"Initial state: [0, 0, 0]"+System.lineSeparator()+
						"Final states: [[3][3][3]]"+System.lineSeparator()+
						"Transitions: "+System.lineSeparator()+
						"!L([2, 9, 0],[?invoice, !invoice, -],[3, 3, 0])"+System.lineSeparator()+
						"!L([3, 3, 0],[-, !singleRoom, ?singleRoom],[3, 6, 7])"+System.lineSeparator()+
						"!L([3, 9, 2],[-, !invoice, ?invoice],[3, 3, 3])"+System.lineSeparator()+
						"!U([0, 0, 0],[?singleRoom, !singleRoom, -],[6, 6, 0])"+System.lineSeparator()+
						"([1, 1, 0],[!card, ?card, -],[2, 2, 0])"+System.lineSeparator()+
						"([2, 2, 0],[-, !freebrk, -],[2, 9, 0])"+System.lineSeparator()+
						"([2, 2, 0],[?receipt, !receipt, -],[3, 3, 0])"+System.lineSeparator()+
						"([3, 1, 1],[-, ?card, !card],[3, 2, 2])"+System.lineSeparator()+
						"([3, 2, 2],[-, !freebrk, -],[3, 9, 2])"+System.lineSeparator()+
						"([3, 2, 2],[-, !receipt, ?receipt],[3, 3, 3])"+System.lineSeparator()+
						"([3, 3, 0],[-, !sharedRoom, ?sharedRoom],[3, 8, 8])"+System.lineSeparator()+
						"([3, 5, 5],[-, !sharedBathroom, ?sharedBathroom],[3, 1, 1])"+System.lineSeparator()+
						"([3, 6, 7],[-, !noFreeCancellation, ?noFreeCancellation],[3, 5, 5])"+System.lineSeparator()+
						"([3, 8, 8],[-, !noFreeCancellation, ?noFreeCancellation],[3, 5, 5])"+System.lineSeparator()+
						"([6, 6, 0],[?noFreeCancellation, !noFreeCancellation, -],[9, 5, 0])"+System.lineSeparator()+
						"([9, 5, 0],[?privateBathroom, !privateBathroom, -],[1, 1, 0])"+System.lineSeparator()+
						"";
		Assert.assertEquals(test, aut.toString());
	}

}