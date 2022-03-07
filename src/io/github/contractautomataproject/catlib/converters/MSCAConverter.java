package io.github.contractautomataproject.catlib.converters;

import io.github.contractautomataproject.catlib.automaton.Automaton;

/**
 * The interface used to import/export MSCA
 * @author Davide Basile
 *
 */
public interface MSCAConverter<A1 extends Automaton<?,?,?,?>,A2 extends Automaton<?,?,?,?>> {
	public  A1 importMSCA(String filename) throws Exception;
	public void  exportMSCA(String filename, A2  aut) throws Exception;
}
