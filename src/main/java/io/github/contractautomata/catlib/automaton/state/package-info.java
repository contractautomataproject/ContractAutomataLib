/**
 * The state package  groups the classes implementing states of automata.<br>
 * <code>AbstractState</code> is the (abstract) super class, where a state can be initial or final and has a label.<br>
 * A <code>BasicState</code> implements an <code>AbstractState</code> of a single participant, it has rank 1 and the label of the<br>
 * state cannot have further inner components.<br>
 * A <code>State</code> implements an <code>AbstractState</code> with a rank: it is a list of basic states.<br>
 *
 * Class diagram of this package:<br>
 *
 * <img src="https://github.com/ContractAutomataProject/ContractAutomataLib/blob/gh-pages/doc/state%20diagram.png?raw=true" alt="the class diagram">
 *
 */
package io.github.contractautomata.catlib.automaton.state;