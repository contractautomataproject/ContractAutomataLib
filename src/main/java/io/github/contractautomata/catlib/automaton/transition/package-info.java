/**
 * The transition package groups the transitions of an automaton.<br>
 * <code>Transition</code> is the super class, it has a source and target states and a label.<br>
 * <code>ModalTransition</code> extends <code>Transition</code> to include modalities.<br>
 * Modalities of Contract Automata are permitted and necessary.<br>
 * A necessary transition has a label that must be match in a composition whilst a permitted transition
 * can be withdrawn.<br>
 * Necessary transitions can be further distinguished between urgent and lazy, where urgent is the classic<br>
 * notion of uncontrollability, whereas lazy is a novel notion introduced in contract automata.<br>
 * Lazy transitions can be either controllable or uncontrollable, according to a given predicate evaluated  <br>
 * on the whole automaton to which this transition belongs to.<br>
 *
 * Class diagram of this package:<br>
 *
 * <img src="https://github.com/ContractAutomataProject/ContractAutomataLib/blob/gh-pages/doc/transition%20diagram.png?raw=true" alt="the class diagram">
 */
package io.github.contractautomata.catlib.automaton.transition;