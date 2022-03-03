package io.github.davidebasile.contractautomata.converters;

import io.github.davidebasile.contractautomata.automaton.Automaton;
import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;
import io.github.davidebasile.contractautomata.automaton.label.Label;
import io.github.davidebasile.contractautomata.automaton.state.State;
import io.github.davidebasile.contractautomata.automaton.transition.Transition;

public interface AutConverter<CS,CL,S extends State<CS>,T extends Transition<CS,CL,S,? extends Label<CL>>> {
	public Automaton<CS,CL,S,T> importMSCA(String filename) throws Exception;
	public void  exportMSCA(String filename, ModalAutomaton<CALabel>  aut) throws Exception;
}
