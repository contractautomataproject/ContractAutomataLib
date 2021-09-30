package io.github.davidebasile.contractautomata.converters;

import io.github.davidebasile.contractautomata.automaton.MSCA;

/**
 * The interface used to import/export MSCA
 * @author Davide Basile
 *
 */
public interface MSCAConverter {
	public MSCA importMSCA(String filename) throws Exception;
	public void exportMSCA(String filename, MSCA aut) throws Exception;
}
