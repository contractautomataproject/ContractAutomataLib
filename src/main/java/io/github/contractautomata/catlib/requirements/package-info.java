/**
 * This package groups the invariant requirements that can be enforced in a contract automaton. <br>
 * The <tt>Agreement</tt> requirement is an invariant requiring that each transition <br>
 * must not be a request: only offers and matches are allowed. This means that all <br>
 * requests actions are matched, and an agreement is reached. <br>
 * The <tt>StrongAgreement</tt> requirement is an invariant allowing only matches. <br>
 * This means that all requests and offers actions of principals are matched.<br>
 */
package io.github.contractautomata.catlib.requirements;