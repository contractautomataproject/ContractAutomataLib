package io.github.contractautomataproject.catlib.converters;

import io.github.contractautomataproject.catlib.automaton.Automaton;
import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;
import io.github.contractautomataproject.catlib.automaton.state.State;
import io.github.contractautomataproject.catlib.transition.Transition;

public interface AutConverter<CS,CL,S extends State<CS>,T extends Transition<CS,CL,S,? extends Label<CL>>> {
	public Automaton<CS,CL,S,T> importMSCA(String filename) throws Exception;
	public void  exportMSCA(String filename, ModalAutomaton<CALabel>  aut) throws Exception;
}
