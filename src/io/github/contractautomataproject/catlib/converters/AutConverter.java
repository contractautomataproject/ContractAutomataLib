package io.github.contractautomataproject.catlib.converters;

import java.io.IOException;


import io.github.contractautomataproject.catlib.automaton.Automaton;

/**
 * The interface used to import/export MSCA
 * @author Davide Basile
 *
 */
public interface AutConverter<A1 extends Automaton<?,?,?,?>,A2 extends Automaton<?,?,?,?>> {
	A1 importMSCA(String filename) throws IOException;
	void  exportMSCA(String filename, A2 aut) throws IOException;
}
