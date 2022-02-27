package io.github.davidebasile.contractautomata.converters;

import io.github.davidebasile.contractautomata.automaton.ModalAutomaton;
import io.github.davidebasile.contractautomata.automaton.label.CALabel;

/**
 * The interface used to import/export MSCA
 * @author Davide Basile
 *
 */
public interface MSCAConverter {
	public ModalAutomaton<CALabel> importMSCA(String filename) throws Exception;
	public void exportMSCA(String filename, ModalAutomaton<CALabel> aut) throws Exception;
}
