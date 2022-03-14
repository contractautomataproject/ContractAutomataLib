package io.github.contractautomataproject.catlib.converters;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.xml.sax.SAXException;

import io.github.contractautomataproject.catlib.automaton.Automaton;

/**
 * The interface used to import/export MSCA
 * @author Davide Basile
 *
 */
public interface AutConverter<A1 extends Automaton<?,?,?,?>,A2 extends Automaton<?,?,?,?>> {
	public  A1 importMSCA(String filename) throws IOException, ParserConfigurationException, SAXException;
	public void  exportMSCA(String filename, A2  aut) throws IOException,ParserConfigurationException, TransformerException;
}
