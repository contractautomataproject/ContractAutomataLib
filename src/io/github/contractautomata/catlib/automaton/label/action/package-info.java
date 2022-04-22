/**
 * The action package groups the classes implementing actions of labels.<br>
 * <tt>Action</tt> is the super class from which the other actions are inheriting.<br>
 * In Contract Automata, an action can be either an <tt>OfferAction</tt>, a <tt>RequestAction</tt> or an <tt>IdleAction</tt> (i.e., nil action).<br>
 * Actions are matchable and a request action matches an offer action (and vice-versa) if both have the same label.<br>
 * Actions can have an <tt>Address</tt>, in this case implementing the interface <tt>AddressedAction</tt>. <br>
 * Actions with addresses are <tt>AddressedOfferAction</tt> and <tt>AddressedRequestActions</tt>.<br>
 * These actions are equipped with an address storing senders and receivers of actions.<br>
 * For two addressed actions to match also their sender and receiver must be equal.<br>
 * Addressed actions are used to implement Communicating Machines, in which each participant in the composition <br>
 * is aware of the other participants. Communicating Machines are a model for choreographies.<br>
 * Actions not having an address are used in Contract Automata: in this case the participants are oblivious of <br>
 * the other partners and the model assume the presence of an orchestrator in charge of pairing offers and requests. <br>
 */
package io.github.contractautomata.catlib.automaton.label.action;