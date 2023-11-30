package io.github.contractautomata.catlib.converters;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.action.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.util.Objects;
import java.util.function.BiPredicate;

/**
 *
 * The interface used to import/export automata.
 * Each converter must implement this interface.
 *
 * @param <A1>  the type of the automaton to import
 * @param <A2>  the type of the automaton to export
 *
 *
 * @author Davide Basile
 */
public interface AutConverter<A1 extends Automaton<?,?,?,?>,A2 extends Automaton<?,?,?,?>> {

	/**
	 * This method is used to import an automaton of type A1 stored in a file
	 *
	 * @param filename   the name of the file containing the automaton
	 * @return  an automaton of type A1 loaded from the file
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 */
	A1 importMSCA(String filename) throws IOException,ParserConfigurationException, SAXException;

	/**
	 * This method is used to store an automaton into a file
	 *
	 * @param filename  the name of the file to store
	 * @param aut  the automaton to store
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws TransformerException
	 */
	void  exportMSCA(String filename, A2 aut) throws ParserConfigurationException, IOException, TransformerException;

	/**
	 * This method provides facilities for parsing a string encoding a textual representation of an action
	 * into an object Action.
	 * If the string is not parsable a run-time exception is thrown.
	 *
	 * @param action  the string representing an action (must be non-null)
	 * @return  the object Action encoded in the parameter
	 */
	default Action parseAction(String action) {
		Objects.requireNonNull(action);

		BiPredicate<String,String> isAction = (s,a)->s.startsWith(a) && s.length()>1;

		if (action.equals(IdleAction.IDLE))
			return new IdleAction();
		if (isAction.test(action, OfferAction.OFFER))
			return new OfferAction(action.substring(1));
		if (isAction.test(action, RequestAction.REQUEST))
			return new RequestAction(action.substring(1));
		if (isAction.test(action, TauAction.TAU))
			return new TauAction(action.substring(4));

		String[] f = action.split(Address.ACTION_SEPARATOR);
		String[] p = f[0].split(Address.ID_SEPARATOR);

		if (p.length==2 && f.length>=2) {
			String subAct = action.substring(action.indexOf(Address.ACTION_SEPARATOR)+1);
			if (isAction.test(subAct,OfferAction.OFFER))
				return new AddressedOfferAction(subAct.substring(1), new Address(p[0],p[1]));
			else if (isAction.test(subAct,RequestAction.REQUEST))
				return new AddressedRequestAction(subAct.substring(1), new Address(p[0],p[1]));
		}

		throw new IllegalArgumentException();
	}
}
