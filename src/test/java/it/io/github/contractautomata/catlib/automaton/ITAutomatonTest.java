package it.io.github.contractautomata.catlib.automaton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.automaton.transition.Transition;
import io.github.contractautomata.catlib.converters.AutDataConverter;
import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import io.github.contractautomata.catlib.automaton.label.action.OfferAction;
import io.github.contractautomata.catlib.automaton.label.action.RequestAction;

import static org.junit.Assert.assertThrows;

public class ITAutomatonTest {

    private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
    public static final String dir = System.getProperty("user.dir")+File.separator+"src"+File.separator+"test"+File.separator+"resources"+File.separator;

    @Test
    public void testString() {
        BasicState<String> s0 = new BasicState<>("0",true,false);
        BasicState<String> s1 = new BasicState<>("1",false,true);
        BasicState<String> s2 = new BasicState<>("2",false,true);
        State<String> cs0 = new State<>(List.of(s0));
        Transition<String,String,State<String>, Label<String>> t1 = new Transition<>(cs0, new Label<>(List.of("m")), new State<>(List.of(s1)));
        Transition<String,String,State<String>, Label<String>> t2 = new Transition<>(cs0, new Label<>(List.of("m")), new State<>(List.of(s2)));

        Automaton<String,String, State<String>,Transition<String,String, State<String>,Label<String>>> prop = new Automaton<>(Set.of(t1,t2));

        String test = "Rank: 1"+System.lineSeparator() +
                "Initial state: [0]"+System.lineSeparator() +
                "Final states: [[1, 2]]"+System.lineSeparator() +
                "Transitions: "+System.lineSeparator() +
                "([0],[m],[1])"+System.lineSeparator() +
                "([0],[m],[2])"+System.lineSeparator();
        Assert.assertEquals(prop.toString(),test);
    }

    @Test
    public void constructor_Exception_differentRank() {
        List<Action> lab = new ArrayList<>();
        lab.add(new IdleAction());
        lab.add(new OfferAction("a"));
        lab.add(new RequestAction("a"));

        List<Action> lab2 = new ArrayList<>();
        lab2.add(new IdleAction());
        lab2.add(new IdleAction());
        lab2.add(new OfferAction("a"));
        lab2.add(new RequestAction("a"));


        BasicState<String> bs0 = new BasicState<>("0", true, false);
        BasicState<String> bs1 = new BasicState<>("1", true, false);
        BasicState<String> bs2 = new BasicState<>("2", true, false);
        BasicState<String> bs3 = new BasicState<>("3", true, false);

        Set<ModalTransition<String,Action,State<String>,CALabel>> tr = new HashSet<>();
        tr.add(new ModalTransition<>(new State<>(Arrays.asList(bs0, bs1, bs2)//,0,0
        ),
                new CALabel(lab),
                new State<>(Arrays.asList(bs0, bs1, bs3)),
                ModalTransition.Modality.PERMITTED));
        State<String> cs = new State<>(Arrays.asList(bs0, bs1, bs2, bs3)//,0,0
        );
        tr.add(new ModalTransition<>(cs,
                new CALabel(lab2),
                cs,
                ModalTransition.Modality.PERMITTED));

        assertThrows("Transitions with different rank", IllegalArgumentException.class, () -> new Automaton<>(tr));
    }


    @Test
    public void noInitialState_exception() {
        List<Action> lab = new ArrayList<>();
        lab.add(new OfferAction("a"));

        BasicState<String> bs0 = new BasicState<>("0", false, true);
        BasicState<String> bs1 = new BasicState<>("1", false, true);


        Set<ModalTransition<String,Action,State<String>,CALabel>> tr = new HashSet<>();
        tr.add(new ModalTransition<>(new State<>(List.of(bs0)//,0,0
        ),
                new CALabel(lab),
                new State<>(List.of(bs1)),
                ModalTransition.Modality.PERMITTED));

        assertThrows("Not Exactly one Initial State found!",
                IllegalArgumentException.class,
                () -> new Automaton<>(tr));
    }

    @Test
    public void noFinalStatesInTransitions_exception() {
        List<Action> lab = new ArrayList<>();
        lab.add(new OfferAction("a"));

        BasicState<String> bs0 = new BasicState<>("0", true, false);
        BasicState<String> bs1 = new BasicState<>("1", false, false);


        Set<ModalTransition<String,Action,State<String>,CALabel>> tr = new HashSet<>();
        tr.add(new ModalTransition<>(new State<>(List.of(bs0)//,0,0
        ),
                new CALabel(lab),
                new State<>(List.of(bs1)),
                ModalTransition.Modality.PERMITTED));

        assertThrows("No Final States!",
                IllegalArgumentException.class,
                () -> new Automaton<>(tr));
    }

    @Test
    public void ambiguousStates_exception() {
        List<Action> lab = new ArrayList<>();
        lab.add(new OfferAction("a"));

        BasicState<String> bs1 = new BasicState<>("0", true, false);
        BasicState<String> bs2 = new BasicState<>("0", false, true);

        Set<ModalTransition<String,Action,State<String>,CALabel>> tr = new HashSet<>();
        tr.add(new ModalTransition<>(new State<>(List.of(bs1)),
                new CALabel(lab),
                new State<>(List.of(bs2)),
                ModalTransition.Modality.PERMITTED));

        tr.add(new ModalTransition<>(new State<>(List.of(bs2)),
                new CALabel(lab),
                new State<>(List.of(bs2)),
                ModalTransition.Modality.PERMITTED));
        assertThrows("Transitions have ambiguous states (different objects for the same state).",
                IllegalArgumentException.class,
                () -> new Automaton<>(tr));
    }


    @Test
    public void testToString() throws IOException {
        Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut= bdc.importMSCA(dir+ "Orc_(BusinessClientxHotelxEconomyClient).data");
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

    public static boolean autEquals(Automaton<?,?,?,?> aut, Automaton<?,?,?,?>  test) {
        Set<String> autTr=aut.getTransition().parallelStream()
                .map(Transition::toString)
                .collect(Collectors.toSet());
        Set<String> testTr=test.getTransition().parallelStream()
                .map(Transition::toString)
                .collect(Collectors.toSet());

        return autTr.parallelStream()
                .allMatch(testTr::contains)
                &&
                testTr.parallelStream()
                        .allMatch(autTr::contains);
    }

    public static String counterExample(Automaton<?,?,?,?> aut, Automaton<?,?,?,?>  test) {
        Set<String> autTr=aut.getTransition().parallelStream()
                .map(Transition::toString)
                .collect(Collectors.toSet());
        Set<String> testTr=test.getTransition().parallelStream()
                .map(Transition::toString)
                .collect(Collectors.toSet());

        String ctx1 = autTr.parallelStream()
                .filter(t->!testTr.contains(t))
                .collect(Collectors.joining(System.lineSeparator()));

        String ctx2 = testTr.parallelStream()
                .filter(t->!autTr.contains(t))
                .collect(Collectors.joining(System.lineSeparator()));

        return "Transitions that should not be in SUT: "+ctx1+System.lineSeparator()+
                "Transitions not contained in SUT:"+ctx2;

    }
}