package io.github.contractautomata.catlib.family;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.OfferAction;
import io.github.contractautomata.catlib.automaton.label.action.RequestAction;
import io.github.contractautomata.catlib.automaton.state.BasicState;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.Strict.class)
public class FMCATest {

    public static final Function<Set<Product>,Set<String>> sorting =  s -> s.stream()
            .map(p->"R:" + p.getRequired().stream()
                    .sorted(Comparator.comparing(Feature::toString))
                    .collect(Collectors.toList())  +
                    "; F:" +
                    p.getForbidden().stream()
                            .sorted(Comparator.comparing(Feature::toString))
                            .collect(Collectors.toList())
                    +";")
            .collect(Collectors.toSet());

    @Mock Feature f1;
    @Mock Feature f2;
    @Mock Feature f3;

    @Mock Product p1;
    @Mock Product p2;
    @Mock Product p3;
    @Mock Product p4;
    @Mock Family family;

    Set<Product> set;

    FMCA aut;

    @Mock BasicState<String> bs0;
    @Mock BasicState<String> bs1;
    @Mock BasicState<String> bs2;
    @Mock State<String> cs1;
    @Mock State<String> cs2;
    @Mock State<String> cs3;
    @Mock Action act;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t1;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t2;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t3;
    @Mock ModalTransition<String,Action,State<String>,CALabel> t4;
    @Mock
    Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> a;


    @Before
    public void setUp() throws Exception {
        f1 = mock(Feature.class);
        f2 = mock(Feature.class);
        f3 = mock(Feature.class);

        when(f1.getName()).thenReturn("f1");
        when(f2.getName()).thenReturn("f2");
        when(f3.getName()).thenReturn("f3");
        when(f1.toString()).thenReturn("f1");
        when(f2.toString()).thenReturn("f2");
        when(f3.toString()).thenReturn("f3");

        when(p1.getRequired()).thenReturn(Set.of(f1));
        when(p1.getForbidden()).thenReturn(Set.of(f2, f3));
//        when(p1.getForbiddenAndRequiredNumber()).thenReturn(3);

        when(p2.getRequired()).thenReturn(Collections.singleton(f1));
        when(p2.getForbidden()).thenReturn(Collections.singleton(f3));
        when(family.getSubProductsNotClosedTransitively(p2)).thenReturn(Collections.singleton(p1));
        //       when(p2.getForbiddenAndRequiredNumber()).thenReturn(2);

        when(p3.getRequired()).thenReturn(Collections.singleton(f1));
        when(p3.getForbidden()).thenReturn(Collections.singleton(f2));
//        when(p3.getForbiddenAndRequiredNumber()).thenReturn(2);
        when(family.getSubProductsNotClosedTransitively(p3)).thenReturn(Collections.singleton(p1));

        when(p4.getRequired()).thenReturn(Set.of(f3));
        when(p4.getForbidden()).thenReturn(Collections.emptySet());
        when(family.getSubProductsNotClosedTransitively(p4)).thenReturn(Collections.emptySet());
        //       when(p4.getForbiddenAndRequiredNumber()).thenReturn(1);


        set = new HashSet<>(Arrays.asList(p1,p2,p3,p4));
//        when(family.getProducts()).thenReturn(set);
        when(family.getMaximalProducts()).thenReturn(Set.of(p2,p3,p4));

        //       when(bs0.isInitial()).thenReturn(true);
        when(bs0.isFinalState()).thenReturn(false);
        when(bs0.getState()).thenReturn("0");
        when(bs1.isFinalState()).thenReturn(true);
        when(bs2.isFinalState()).thenReturn(true);
        when(bs1.getState()).thenReturn("1");
        when(bs2.getState()).thenReturn("2");

        when(cs1.isInitial()).thenReturn(true);
        when(cs1.toString()).thenReturn(List.of("0","0").toString());
        when(cs3.isFinalState()).thenReturn(true);

        when(cs1.getState()).thenReturn(asList(bs0,bs0));
        when(cs2.getState()).thenReturn(asList(bs1,bs0));
        when(cs3.getState()).thenReturn(asList(bs1,bs2));

        ////
        act = mock(OfferAction.class);
        RequestAction ract = mock(RequestAction.class);
        CALabel lab = mock(CALabel.class);
        when(t1.getLabel()).thenReturn(lab);
        when(lab.getAction()).thenReturn(act);
        when(lab.getContent()).thenReturn(List.of(act,ract));
        when(act.getLabel()).thenReturn("f1");
        when(act.toString()).thenReturn("!f1");
        //       when(ract.getLabel()).thenReturn("f1");
        when(ract.toString()).thenReturn("?f1");

        Action act2 = mock(OfferAction.class);
        RequestAction ract2 = mock(RequestAction.class);

        CALabel lab2 = mock(CALabel.class);
        when(t2.getLabel()).thenReturn(lab2);
        when(lab2.getAction()).thenReturn(act2);
        when(lab2.getContent()).thenReturn(List.of(act2,ract2));
        when(act2.getLabel()).thenReturn("f2");
        when(act2.toString()).thenReturn("!f2");
        //       when(ract2.getLabel()).thenReturn("f2");
        when(ract2.toString()).thenReturn("?f2");

        CALabel lab3 = mock(CALabel.class);
        Action act3 = mock(OfferAction.class);
        RequestAction ract3 = mock(RequestAction.class);

        when(t3.getLabel()).thenReturn(lab3);
        when(lab3.getAction()).thenReturn(act3);
        when(lab3.getContent()).thenReturn(List.of(act3,ract3));
        when(act3.getLabel()).thenReturn("f3");
        when(act3.toString()).thenReturn("!f3");
//        when(ract3.getLabel()).thenReturn("f3");
        when(ract3.toString()).thenReturn("?f3");

        ///

        when(t1.getSource()).thenReturn(cs1);
        when(t1.getLabel()).thenReturn(lab);
        when(t1.getTarget()).thenReturn(cs2);
        when(t1.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);
        when(t1.getRank()).thenReturn(2);
        when(t1.toString()).thenReturn("([0, 0],[!f1,?f1],[1, 0])");

        when(t2.getSource()).thenReturn(cs2);
        when(t2.getLabel()).thenReturn(lab2);
        when(t2.getTarget()).thenReturn(cs3);
        when(t2.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);
        when(t2.getRank()).thenReturn(2);
        when(t2.toString()).thenReturn("([1, 0],[!f2,?f2],[1, 2])");

        when(t3.getSource()).thenReturn(cs3);
        when(t3.getLabel()).thenReturn(lab3);
        when(t3.getTarget()).thenReturn(cs1);
        when(t3.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);
        when(t3.getRank()).thenReturn(2);
        when(t3.toString()).thenReturn("([1, 2],[!f3,?f3],[0, 0])");


        when(p1.isForbidden(lab2)).thenReturn(true);
        when(p3.isForbidden(lab2)).thenReturn(true);
        when(p1.isForbidden(lab3)).thenReturn(true);
        when(p2.isForbidden(lab3)).thenReturn(true);


        when(a.getTransition()).then(args -> new HashSet<>(Arrays.asList(t1,t2,t3)));
        when(a.getStates()).thenReturn(Set.of(cs1,cs2,cs3));
//        when(a.getBasicStates()).thenReturn(Map.of(1,Set.of(bs0,bs2),2,Set.of(bs0,bs1)));
        when(a.getForwardStar(cs1)).thenReturn(Set.of(t1));
        when(a.getInitial()).thenReturn(cs1);

//        when(p1.checkRequired(anySet())).thenReturn(true);
        when(p2.checkRequired(anySet())).thenReturn(true);
        //       when(p3.checkRequired(anySet())).thenReturn(true);
        when(p4.checkRequired(anySet())).thenReturn(true);
        when(p4.isValid(any())).thenReturn(true);


        Map<Product, Map<Boolean,Set<Product>>> po =
                Map.of(p1, Map.of(false, Collections.emptySet(),true,Set.of(p2,p3)),
                        p2, Map.of(false, Set.of(p1),true, Collections.emptySet()),
                        p3, Map.of(false, Set.of(p1),true, Collections.emptySet()),
                        p4, Map.of(false, Collections.emptySet(),true, Collections.emptySet()));

        when(family.getPo()).thenReturn(po);

        aut =  new FMCA(a,family);
    }

    @Test
    public void testGetAut() {
        Assert.assertEquals(a,aut.getAut());
    }

    @Test
    public void testGetFamily() {
        assertEquals(family,aut.getFamily());
    }


    @Test
    public void testGetFamilyConstructor2() {
        FMCA aut2 = new FMCA(a,set);
        assertEquals(sorting.apply(set),sorting.apply(aut2.getFamily().getProducts()));
    }

    @Test
    public void testGetFamilyConstructor3() {
        Feature f4 = mock(Feature.class);//not present in aut (and orc)
        Feature f5 = mock(Feature.class);//not present in orc

        when(f4.getName()).thenReturn("f4");
        when(f5.getName()).thenReturn("f5");

        when(p4.getRequired()).thenReturn(Set.of(f3,f5));
//        when(p4.getForbidden()).thenReturn(Collections.emptySet());


        CALabel lab4 = mock(CALabel.class);
        RequestAction ract4 = mock(RequestAction.class);
     //   IdleAction iact = mock(IdleAction.class);

        when(t4.getLabel()).thenReturn(lab4);
        when(lab4.getAction()).thenReturn(ract4);
 //       when(lab4.getLabel()).thenReturn(List.of(iact,ract4));
        when(lab4.isRequest()).thenReturn(true);
        when(ract4.getLabel()).thenReturn("f5");
  //      when(ract4.toString()).thenReturn("?f5");

        when(t4.getSource()).thenReturn(cs3);
        when(t4.getLabel()).thenReturn(lab4);
        when(t4.getTarget()).thenReturn(cs3);
   //     when(t4.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);
 //       when(t4.getRank()).thenReturn(2);
   //     when(t4.toString()).thenReturn("([1,2],[-,?f5],[1, 2])");

        when(a.getTransition()).then(args -> new HashSet<>(Arrays.asList(t1,t2,t3,t4)));

        Product p3before = mock(Product.class);
        when(p3before.getRequired()).thenReturn(Set.of(f1,f4));
        when(p3before.getForbidden()).thenReturn(Set.of(f2,f4));

        set = new HashSet<>(Arrays.asList(p1,p2,p3before,p4));

        FMCA aut2 = new FMCA(a,set);

        Set<String> autProducts = sorting.apply(aut2.getFamily().getProducts());
        Set<String> testSet = sorting.apply(new HashSet<>(Arrays.asList(p1,p2,p3)));

        assertEquals(testSet, autProducts);

    }

    @Test
    public void testGetCanonicalProducts() {
        Feature f4 = mock(Feature.class);
        when(f4.getName()).thenReturn("f4");

        Product p5 = mock(Product.class);
        when(p5.toString()).thenReturn("p5");
        assertEquals("p5",p5.toString()); //just to avoid unnecessary mocking exception

        when(p5.checkRequired(any())).thenReturn(true);
        when(p5.getForbidden()).thenReturn(Set.of(f4));

        set = new HashSet<>(Arrays.asList(p1,p2,p3,p4,p5));
        when(family.getMaximalProducts()).thenReturn(Set.of(p2,p3,p4,p5));

        String test1 = "[p2=Rank: 2" + System.lineSeparator() +
                "Initial state: [0, 0]" + System.lineSeparator() +
                "Final states: [[1][2]]" + System.lineSeparator() +
                "Committed states: [[][]]" + System.lineSeparator() +
                "Transitions: " + System.lineSeparator() +
                "([0, 0],[!f1,?f1],[1, 0])" + System.lineSeparator() +
                "([1, 0],[!f2,?f2],[1, 2])" + System.lineSeparator() +
                ", ";
        String test2 = "=Rank: 2" + System.lineSeparator() +
                "Initial state: [0, 0]" + System.lineSeparator() +
                "Final states: [[1][2]]" + System.lineSeparator() +
                "Committed states: [[][]]" + System.lineSeparator() +
                "Transitions: " + System.lineSeparator() +
                "([0, 0],[!f1,?f1],[1, 0])" + System.lineSeparator() +
                "([1, 0],[!f2,?f2],[1, 2])" + System.lineSeparator() +
                "([1, 2],[!f3,?f3],[0, 0])" + System.lineSeparator() +
                "]";

        String cp = aut.getCanonicalProducts().entrySet()
                .stream().sorted(Comparator.comparing(e->e.getKey().toString()))
                .collect(Collectors.toList()).toString();

        assertTrue(cp.equals(test1+"p4"+test2) || cp.equals(test1+"p5"+test2));
    }

    @Test
    public void getCanonicalProductsException() {
        when(act.getLabel()).thenReturn("dummy");
        when(a.getForwardStar(cs1)).thenReturn(Set.of(t1));
        assertThrows(UnsupportedOperationException.class,()->aut.getCanonicalProducts());
    }

    @Test
    public void testGetOrchestrationOfFamilyEnumerative() {
        String test = "Rank: 2" + System.lineSeparator() +
                "Initial state: [0, 0]" + System.lineSeparator() +
                "Final states: [[0_1][0_2]]" + System.lineSeparator() +
                "Committed states: [[][]]" + System.lineSeparator() +
                "Transitions: " + System.lineSeparator() +
                "([0, 0],[!dummy, -],[0_0, 0_0])" + System.lineSeparator() +
                "([0_0, 0_0],[!f1, ?f1],[0_1, 0_0])" + System.lineSeparator() +
                "([0_1, 0_0],[!f2, ?f2],[0_1, 0_2])" + System.lineSeparator() +
                "([0_1, 0_2],[!f3, ?f3],[0_0, 0_0])" + System.lineSeparator();

        String orc =  aut.getOrchestrationOfFamilyEnumerative().toString();
        assertEquals(test, orc);
    }

    @Test
    public void testGetOrchestrationOfFamily() {
        String test1 = "Rank: 2" + System.lineSeparator() +
                "Initial state: [0, 0]" + System.lineSeparator() +
                "Final states: [[0_1, 1_1][0_2, 1_2]]" + System.lineSeparator() +
                "Committed states: [[][]]" + System.lineSeparator() +
                "Transitions: " + System.lineSeparator() +
                "([0, 0],[!dummy, -],[0_0, 0_0])" + System.lineSeparator() +
                "([0, 0],[!dummy, -],[1_0, 1_0])" + System.lineSeparator() +
                "([0_0, 0_0],[!f1, ?f1],[0_1, 0_0])" + System.lineSeparator() +
                "([0_1, 0_0],[!f2, ?f2],[0_1, 0_2])" + System.lineSeparator() +
                "([0_1, 0_2],[!f3, ?f3],[0_0, 0_0])" + System.lineSeparator() +
                "([1_0, 1_0],[!f1, ?f1],[1_1, 1_0])" + System.lineSeparator() +
                "([1_1, 1_0],[!f2, ?f2],[1_1, 1_2])"+ System.lineSeparator();

        String test2 = "Rank: 2" + System.lineSeparator() +
                "Initial state: [0, 0]" + System.lineSeparator() +
                "Final states: [[0_1, 1_1][0_2, 1_2]]" + System.lineSeparator() +
                "Committed states: [[][]]" + System.lineSeparator() +
                "Transitions: " + System.lineSeparator() +
                "([0, 0],[!dummy, -],[0_0, 0_0])" + System.lineSeparator() +
                "([0, 0],[!dummy, -],[1_0, 1_0])" + System.lineSeparator() +
                "([0_0, 0_0],[!f1, ?f1],[0_1, 0_0])" + System.lineSeparator() +
                "([0_1, 0_0],[!f2, ?f2],[0_1, 0_2])" + System.lineSeparator() +
                "([1_0, 1_0],[!f1, ?f1],[1_1, 1_0])" + System.lineSeparator() +
                "([1_1, 1_0],[!f2, ?f2],[1_1, 1_2])" + System.lineSeparator() +
                "([1_1, 1_2],[!f3, ?f3],[1_0, 1_0])" + System.lineSeparator();

        String orc = aut.getOrchestrationOfFamily().toString();

        assertTrue(orc.equals(test1) || orc.equals(test2));
    }

    @Test
    public void testGetTotalProductsWithNonemptyOrchestration() {
        String test = "{p4=Rank: 2" + System.lineSeparator() +
                "Initial state: [0, 0]" + System.lineSeparator() +
                "Final states: [[1][2]]" + System.lineSeparator() +
                "Committed states: [[][]]" + System.lineSeparator() +
                "Transitions: " + System.lineSeparator() +
                "([0, 0],[!f1,?f1],[1, 0])" + System.lineSeparator() +
                "([1, 0],[!f2,?f2],[1, 2])" + System.lineSeparator() +
                "([1, 2],[!f3,?f3],[0, 0])" + System.lineSeparator() +
                "}";

        assertEquals(test,aut.getTotalProductsWithNonemptyOrchestration().toString());
    }

    @Test
    public void testProductsRespectingValidityException() {
        when(act.getLabel()).thenReturn("dummy");
        assertThrows(UnsupportedOperationException.class,()->aut.productsRespectingValidity());
    }

    @Test
    public void testProductsRespectingValidity() {
        assertEquals(Set.of(p4),aut.productsRespectingValidity());
    }

    @Test
    public void testProductsRespectingValidity2() {
    //    when(p1.getForbidden()).thenReturn(Collections.emptySet());
   //     when(p1.getRequired()).thenReturn(Set.of(f1,f2,f3));
   //     when(p1.getForbiddenAndRequiredNumber()).thenReturn(3);
        when(p1.isValid(a)).thenReturn(true);

   //     when(p2.getForbidden()).thenReturn(Collections.emptySet());
   //     when(p2.getRequired()).thenReturn(Set.of(f2,f3));
   //     when(p2.getForbiddenAndRequiredNumber()).thenReturn(2);
        when(p2.isValid(a)).thenReturn(true);

    //    when(p3.getForbidden()).thenReturn(Collections.emptySet());
    //    when(p3.getRequired()).thenReturn(Set.of(f1,f3));
    //    when(p3.getForbiddenAndRequiredNumber()).thenReturn(2);
        when(p3.isValid(a)).thenReturn(true);

    //    when(p4.getForbidden()).thenReturn(Collections.emptySet());
    //    when(p4.getRequired()).thenReturn(Collections.singleton(f3));
   //     when(p4.getForbiddenAndRequiredNumber()).thenReturn(1);
        when(p4.isValid(a)).thenReturn(true);

        when(family.getMaximalProducts()).thenReturn(Collections.singleton(p4));
        when(family.getSubProductsNotClosedTransitively(p4)).thenReturn(Set.of(p2,p3));
        when(family.getSubProductsNotClosedTransitively(p3)).thenReturn(Collections.singleton(p1));
        when(family.getSubProductsNotClosedTransitively(p2)).thenReturn(Collections.singleton(p1));
        when(family.getSubProductsNotClosedTransitively(p1)).thenReturn(Collections.emptySet());

        assertEquals(Set.of(p1,p2,p3,p4),aut.productsRespectingValidity());
    }


    @Test
    public void testProductsWithNonEmptyOrchestration() {
        assertEquals("[p2, p4]",aut.productsWithNonEmptyOrchestration().stream()
                .sorted(Comparator.comparing(Product::toString))
                .collect(Collectors.toList())
                .toString());
    }

    @Test
    public void testProductsWithNonEmptyOrchestration2() {

        when(p1.getRequired()).thenReturn(Collections.emptySet());
        when(p1.getForbidden()).thenReturn(Set.of(f1,f2,f3));
//        when(p1.getForbiddenAndRequiredNumber()).thenReturn(3);

        when(p2.getRequired()).thenReturn(Collections.emptySet());
        when(p2.getForbidden()).thenReturn(Set.of(f2,f3));
//        when(p2.getForbiddenAndRequiredNumber()).thenReturn(2);

        when(p3.getRequired()).thenReturn(Collections.emptySet());
        when(p3.getForbidden()).thenReturn(Set.of(f1,f3));
//        when(p3.getForbiddenAndRequiredNumber()).thenReturn(2);

        when(p4.getRequired()).thenReturn(Collections.emptySet());
        when(p4.getForbidden()).thenReturn(Collections.singleton(f3));
 //       when(p4.getForbiddenAndRequiredNumber()).thenReturn(1);


        CALabel lab4 = mock(CALabel.class);
        Action act4 = mock(OfferAction.class);
      //  RequestAction ract4 = mock(RequestAction.class);
        when(lab4.getAction()).thenReturn(act4);
 //       when(lab4.getLabel()).thenReturn(List.of(act4,ract4));
        when(act4.getLabel()).thenReturn("f4");
  //      when(act4.toString()).thenReturn("!f4");
  //      when(ract4.toString()).thenReturn("?f4");

        ModalTransition<?,?,?,?> t4 = mock(ModalTransition.class);
        doReturn(cs1).when(t4).getSource();
        doReturn(lab4).when(t4).getLabel();
        doReturn(cs1).when(t4).getTarget();
  //      when(t4.getModality()).thenReturn(ModalTransition.Modality.PERMITTED);
        when(t4.getRank()).thenReturn(2);
 //       when(t4.toString()).thenReturn("([0, 0],[!f4,?f4],[0, 0])");


 //       when(p1.isForbidden(lab4)).thenReturn(false);
  //      when(p2.isForbidden(lab4)).thenReturn(false);
 //       when(p3.isForbidden(lab4)).thenReturn(false);
 //       when(p4.isForbidden(lab4)).thenReturn(false);

        when(a.getTransition()).then(args -> new HashSet<>(Arrays.asList(t1,t2,t3,t4)));
        doReturn(Set.of(t1,t4)).when(a).getForwardStar(cs1);


//        when(bs0.isFinalState()).thenReturn(true);
        when(cs1.isFinalState()).thenReturn(true);

        aut = new FMCA(a,Set.of(p1,p2,p3,p4));

        Set<String> test = Set.of("R:[]; F:[f1, f2, f3];",
               "R:[]; F:[f2, f3];", "R:[]; F:[f3];",
                "R:[]; F:[f1, f3];");

        assertEquals(test,sorting.apply(aut.productsWithNonEmptyOrchestration()));
    }


    @Test
    public void testConstructorNullArgument() {
        assertThrows(NullPointerException.class,()->new FMCA(null, (Family) null));
    }
}