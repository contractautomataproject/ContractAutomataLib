package io.github.contractautomata.catlib.converters;

import java.io.IOException;
import java.util.Objects;
import java.util.function.BiPredicate;


import io.github.contractautomata.catlib.automaton.label.action.*;
import io.github.contractautomata.catlib.automaton.Automaton;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

/**
 * The interface used to import/export MSCA
 * @author Davide Basile
 *
 */
public interface AutConverter<A1 extends Automaton<?,?,?,?>,A2 extends Automaton<?,?,?,?>> {
	A1 importMSCA(String filename) throws IOException,ParserConfigurationException, SAXException;
	void  exportMSCA(String filename, A2 aut) throws ParserConfigurationException, IOException, TransformerException;


	default Action parseAction(String action) {
		Objects.requireNonNull(action);

		BiPredicate<String,String> isAction = (s,a)->s.startsWith(a) && s.length()>1;

		if (action.equals(IdleAction.IDLE))
			return new IdleAction();
		if (isAction.test(action, OfferAction.OFFER))
			return new OfferAction(action.substring(1));
		if (isAction.test(action, RequestAction.REQUEST))
			return new RequestAction(action.substring(1));

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
