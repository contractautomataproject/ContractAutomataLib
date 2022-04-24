/**
 *  The label package groups classes related to labels of automata.<br>
 *  <code>Label</code> is the super class having a content that is a tuple of a generic type. <br>
 *  Labels have a rank (size of the tuple) and implements<br>
 *  the <code>Matchable</code> interface, to check if two actions match.<br>
 *  <code>CALabel</code> extends Label to implement labels of Contract Automata.<br>
 *  In this case labels are list of actions, with specific constraints.<br>
 *
 * Class diagram of this package:<br>
 *
 * <img src="https://github.com/ContractAutomataProject/ContractAutomataLib/blob/gh-pages/doc/label%20diagram.png?raw=true" alt="the class diagram">
 */
package io.github.contractautomata.catlib.automaton.label;