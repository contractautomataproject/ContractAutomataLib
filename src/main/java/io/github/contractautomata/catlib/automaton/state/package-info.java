/**
 * The state package  groups the classes implementing states of automata.<br>
 * <tt>AbstractState</tt> is the (abstract) super class, where a state can be initial or final and has a label.<br>
 * A <tt>BasicState</tt> implements an <tt>AbstractState</tt> of a single participant, it has rank 1 and the label of the<br>
 * state cannot have further inner components.<br>
 * A <tt>State</tt> implements an <tt>AbstractState</tt> with a rank: it is a list of basic states.<br>
 */
package io.github.contractautomata.catlib.automaton.state;