/**
 * The transition package groups the transitions of an automaton.<br>
 * <tt>Transition</tt> is the super class, it has a source and target states and a label.<br>
 * <tt>ModalTransition</tt> extends <tt>Transition</tt> to include modalities.<br>
 * Modalities of Contract Automata are permitted and necessary.<br>
 * A necessary transition has a label that must be match in a composition whilst a permitted transition
 * can be withdrawn.<br>
 * Necessary transitions can be further distinguished between urgent and lazy, where urgent is the classic<br>
 * notion of uncontrollability, whereas lazy is a novel notion introduced in contract automata.<br>
 * Lazy transitions can be either controllable or uncontrollable, according to a given predicate evaluated  <br>
 * on the whole automaton to which this transition belongs to.<br>
 */
package io.github.contractautomata.catlib.automaton.transition;