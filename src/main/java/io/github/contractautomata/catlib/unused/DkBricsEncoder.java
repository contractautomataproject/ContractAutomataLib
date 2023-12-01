package io.github.contractautomata.catlib.unused;

/**
 * THIS CLASS IS NOT WORKING.
 * THE CODE IS LEFT IN CASE IT MAY BE NEEDED FOR OTHER ENCODERS.
 *
 * I have encountered the following issues with the library dk.brics.automaton.Automaton.
 * It is hard to keep a connection between the states of the starting contract automaton,
 * and the states of the automaton encoded into the dk.brics library.
 * This is because at each operation of the dk.brics library, states are instantiated as
 * new objects (the method equals for state is the one from Object class).
 * Furthermore, there is no way of assigning additional information to a state, for example,
 * a label, in such a way that a state can be identified by its label, even if the object
 * is new.
 * Indeed, the dk.brics library assigns an integer to each state, which however varies
 * at every new operation (e.g., toString, toDot), so each number does not uniquely identify
 * a state through various operations.
 * Furthermore, although I can extend a State to include a label, each operation called
 * to the automaton will produce a new automaton such that states have no label.
 */
public class DkBricsEncoder {
//
//    private  Map<dk.brics.automaton.State, State<String>> dkstate2castate;
//    private  Map<State<String>, dk.brics.automaton.State> castate2dkstate;
//
//    private  Map<Transition, ModalTransition.Modality> transition2modality;
//
//    private Map<CALabel,Character> label2char;
//
//    private Map<Character, CALabel> char2label;
//
//    private  dk.brics.automaton.Automaton dkAut;
//
//    public DkBricsEncoder(io.github.contractautomata.catlib.automaton.Automaton<String, Action, State<String>, ModalTransition<String,Action, State<String>, CALabel>> aut) {
//        dk.brics.automaton.Automaton a = new dk.brics.automaton.Automaton();
//
//        List<CALabel> labels = aut.getTransition().parallelStream()
//                .map(io.github.contractautomata.catlib.automaton.transition.Transition::getLabel)
//                .distinct()
//                .collect(Collectors.toList());
//
//        if (labels.size()>127) //ASCII interval
//            throw new RuntimeException("Cannot encode all the labels into chars");
//
//        label2char = IntStream.range(0,labels.size())
//                .boxed()
//                .collect(Collectors.toMap(labels::get, i-> (char) i.intValue()));
//
//        char2label = label2char.entrySet()
//                .stream()
//                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
//
//        dkstate2castate = aut.getStates().parallelStream()
//                .collect(Collectors.toMap(s -> {
//                    dk.brics.automaton.State state = new dk.brics.automaton.State();
//                    if (s.isInitial()) a.setInitialState(state);
//                    if (s.isFinalState()) state.setAccept(true);
//                    return state;
//                }, s -> s));
//
//        castate2dkstate = dkstate2castate.entrySet()
//                        .stream()
//                        .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
//
//        transition2modality = new HashMap<>();
//
//        dkstate2castate.keySet().forEach(
//                state -> {
//                    aut.getForwardStar(dkstate2castate.get(state)).forEach(
//                            tr -> {
//                                dk.brics.automaton.State target = castate2dkstate.get(tr.getTarget());
//                                Character label = label2char.get(tr.getLabel());
//                                Transition t = new Transition(label, target);
//                                transition2modality.put(t,tr.getModality());
//                                state.addTransition(t);
//                            });
//                }
//
//        );
//
////        a.toDot(); //this is used to set the state numbers, which is done inside the method...
////        dkstatestring2castate = dkstate2castate.entrySet()
////                .stream()
////                //.peek(e->System.out.println(e.getKey()))
////                .collect(Collectors.toMap(e->e.getKey().toString().split(":")[0], Map.Entry::getValue));
//
//
//        //    System.out.println(transition2modality);
//        this.dkAut=a;
//    }
//
//    public Automaton getDkAut(){
//        return dkAut;
//    }
//
//
//    public io.github.contractautomata.catlib.automaton.Automaton<String, Action, State<String>, ModalTransition<String,Action, State<String>, CALabel>> getCA(){
//        Map<dk.brics.automaton.State,Boolean> map = dkAut.getStates().stream()
//                .collect(Collectors.toMap(x -> x, x -> false));
//        map.put(dkAut.getInitialState(), true);
//        Queue<dk.brics.automaton.State> toVisit = new LinkedList<>(List.of(dkAut.getInitialState()));
//        Set<ModalTransition<String, Action, State<String>, CALabel>> trans = new HashSet<>();
//        while(!toVisit.isEmpty()) {
//            dk.brics.automaton.State currentstate = toVisit.remove();
//            //System.out.println(currentstate.toString().split(":")[0]);
//        //    System.out.println(dkstatestring2castate);
//            trans.addAll(currentstate.getTransitions()
//                    .stream()
//           //         .peek(t-> { System.out.println(dkstatestring2castate.get(currentstate.toString().split(":")[0]) + " " + char2label.get(t.getMin()) + " " + dkstatestring2castate.get(t.getDest().toString().split(":")[0])); })
//                    .map(t->{
//                        return new ModalTransition<>(dkstate2castate.get(currentstate),char2label.get(t.getMin()),dkstate2castate.get(t.getDest()), transition2modality.get(t));
//                    })
//                    .collect(Collectors.toSet()));
//            Map<dk.brics.automaton.State, Boolean> toAdd =
//                    currentstate.getTransitions()
//                            .stream()
//                            .map(Transition::getDest)
//                            .filter(dest ->Boolean.FALSE.equals(map.get(dest)))
//                            .distinct()
//                            .collect(Collectors.toMap(x -> x, x -> true));
//            map.putAll(toAdd);
//            toVisit.addAll(toAdd.keySet());
//        }
//    }
}
