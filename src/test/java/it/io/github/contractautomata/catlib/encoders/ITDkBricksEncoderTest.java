package it.io.github.contractautomata.catlib.encoders;

import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.converters.AutDataConverter;


public class ITDkBricksEncoderTest {
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
//
//	@Test
//	public void test1() throws IOException {
//		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = bdc.importMSCA(ITAutomatonTest.dir + "NewOrc_(DealerxPlayerxPlayer).data");
//		DkBricsEncoder dbe = new DkBricsEncoder(aut);
//		dk.brics.automaton.Automaton a = dbe.getDkAut();
//		System.out.println(a.getNumberOfTransitions() + " " + a.getNumberOfStates());
//
//		boolean isDeterministic = aut.getTransition().parallelStream()
//						.noneMatch(t->aut.getTransition().parallelStream()
//								.filter(tt->tt!=t)
//								.noneMatch(tt->t.getSource().equals(tt.getSource()) && t.getLabel().equals(tt.getLabel())));
//
//		boolean isDkDeterministic = a.getStates().parallelStream()
//						.anyMatch(s->s.getTransitions()
//								.stream().map(Transition::getMin)
//								.count()>1);
//
//		System.out.println(isDkDeterministic + " " + isDeterministic);
//		a.determinize();
//
//		System.out.println(a.getNumberOfTransitions() + " " + a.getNumberOfStates());
//		//System.out.println(a);
//		//System.out.println(dbe.getCA());
//		assertTrue(AutomatonTest.autEquals(aut, dbe.getCA()));
//
//	}
//
//
//	@Test
//	public void test2() throws IOException {
//		BasicState<String> bs0 = new BasicState<>("0",true,false);
//		BasicState<String> bs1 = new BasicState<>("1",false,true);
//		BasicState<String> bs2 = new BasicState<>("2",false,true);
//		State<String> cs0 = new State<>(List.of(bs0));
//		State<String> cs1 = new State<>(List.of(bs1));
//		State<String> cs2 = new State<>(List.of(bs2));
//		CALabel ca = new CALabel(List.of(new OfferAction("a")));
//		ModalTransition<String,Action,State<String>,CALabel> tr1 = new ModalTransition<>(cs0,ca,cs1, ModalTransition.Modality.PERMITTED);
//		ModalTransition<String,Action,State<String>,CALabel> tr2 = new ModalTransition<>(cs0,ca,cs2, ModalTransition.Modality.PERMITTED);
//		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut = new Automaton<>(Set.of(tr1,tr2));
//
//		DkBricsEncoder dbe = new DkBricsEncoder(aut);
//		dk.brics.automaton.Automaton a = dbe.getDkAut();
//		System.out.println(a.toDot());
//
//		System.out.println("---------------------------------");
//
//
////		dbe.getCA();
//
//		a.setDeterministic(false);
//		a.determinize();
//		System.out.println(a.toDot());
//		System.out.println("---------------------------------");
//
//		assertTrue(AutomatonTest.autEquals(aut, dbe.getCA()));
//	}
}
