/**
 *  The label package groups classes related to labels of automata.<br>
 *  <tt>Label</tt> is the super class having a content that is a tuple of a generic type. <br>
 *  Labels have a rank (size of the tuple) and implements<br>
 *  the <tt>Matchable</tt> interface, to check if two actions match.<br>
 *  <tt>CALabel</tt> extends Label to implement labels of Contract Automata.<br>
 *  In this case labels are list of actions, with specific constraints.<br>
 */
package io.github.contractautomata.catlib.automaton.label;