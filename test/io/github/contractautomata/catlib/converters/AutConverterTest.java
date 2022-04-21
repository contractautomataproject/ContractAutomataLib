package io.github.contractautomata.catlib.converters;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.action.*;
import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

import static org.junit.Assert.*;

public class AutConverterTest {

    final AutConverter ac = new AutConverter() {
        @Override
        public Automaton<?, ?, ?, ?> importMSCA(String filename) throws IOException, ParserConfigurationException, SAXException {
            return null;
        }

        @Override
        public void exportMSCA(String filename, Automaton aut) throws ParserConfigurationException, IOException, TransformerException {

        }
    };

    @Test
    public void testParseActionIdle() {
        assertEquals(ac.parseAction("-"),new IdleAction());
    }


    @Test
    public void testParseActionOffer() {
        assertEquals(ac.parseAction("!test"),new OfferAction("test"));
    }

    @Test
    public void testParseActionRequest() {
        assertEquals(ac.parseAction("?test"),new RequestAction("test"));
    }

    @Test
    public void testParseAddressedActionOffer() {
        assertEquals(ac.parseAction("1_2@!test"),new AddressedOfferAction("test",new Address("1","2")));
    }

    @Test
    public void testParseAddressedActionRequest() {
        assertEquals(ac.parseAction("1_2@?test"),new AddressedRequestAction("test",new Address("1","2")));
    }

    @Test
    public void testParseActionExceptionManyIDSeparator(){
        assertThrows(IllegalArgumentException.class, () -> ac.parseAction("1_2_3@!test"));
    }


    @Test
    public void testParseActionExceptionNoIDSeparator(){
        assertThrows(IllegalArgumentException.class, () -> ac.parseAction("@!test"));
    }

    @Test
    public void testParseActionExceptionBadActionSeparator(){
        assertThrows(IllegalArgumentException.class, () -> ac.parseAction("1_2_3@"));
    }


    @Test
    public void testParseActionExceptionNoActionSeparator(){
        assertThrows(IllegalArgumentException.class, () -> ac.parseAction("1_2!test"));
    }


    @Test
    public void testParseActionExceptionBadAction(){
        assertThrows(IllegalArgumentException.class, () -> ac.parseAction("1_2@test"));
    }



    @Test
    public void testParseActionExceptionBadAction2(){
        assertThrows(IllegalArgumentException.class, () -> ac.parseAction("1_2@!"));
    }

}