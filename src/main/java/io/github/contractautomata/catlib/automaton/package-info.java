/**
 * The automaton package contains the class implementing an automaton.<br>
 * Each <code>Automaton</code> has a set of transitions, a set of states, an initial state and a set of final states.<br>
 * To be composable, an Automaton implements the interface <code>Ranked</code>. The rank is the number of components contained in the automaton.<br>
 * Contract Automata have special labels, implemented inside the package labels.
 *
 * Contract Automata have been introduced (and formalised) in :
 * <ul>
 *     <li>Basile, D., et al. 2016. Automata for specifying and orchestrating service contracts. Logical methods in computer science, 12.
 *     <a href="https://doi.org/10.2168/LMCS-12(4:6)2016">https://doi.org/10.2168/LMCS-12(4:6)2016</a>
 *     </li>
 * </ul>
 *
 * Class diagram of this package:<br>
 *
 * <img src="https://raw.githubusercontent.com/ContractAutomataProject/ContractAutomataLib/gh-pages/doc/automaton%20diagram.png" alt="class diagram">
 */
package io.github.contractautomata.catlib.automaton;