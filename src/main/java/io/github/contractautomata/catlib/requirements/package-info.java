/**
 * This package groups the invariant requirements that can be enforced in a contract automaton. <br>
 * The <code>Agreement</code> requirement is an invariant requiring that each transition <br>
 * must not be a request: only offers and matches are allowed. This means that all <br>
 * requests actions are matched, and an agreement is reached. <br>
 * The <code>StrongAgreement</code> requirement is an invariant allowing only matches. <br>
 * This means that all requests and offers actions of principals are matched.<br>
 *
 *
 * Class diagram of this package:<br>
 *
 * <img src="https://github.com/ContractAutomataProject/ContractAutomataLib/blob/gh-pages/doc/requirements%20diagram.png?raw=true" alt="the class diagram">
 */
package io.github.contractautomata.catlib.requirements;