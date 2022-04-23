/**
 * This package groups the various operations that can be performed on automata. <br>
 * <tt>Projection</tt> is used to extract a principal automaton from a composed automaton. <br>
 * <tt>Relabeling</tt> is used to relabel the states of an automaton. <br>
 * <tt>Union</tt> is used to compute the union of different contract automata. <br>
 * The main operations are <tt>Composition</tt>, to compose automata, and <tt>Synthesis</tt>  <br>
 * to refine an automaton to satisfy given predicates.<br>
 * These two classes are generics. <br>
 * <tt>MSCACompositionFunction</tt> instantiates the generic types to those used by a modal contract automaton. <br>
 * <tt>ModelCheckingFunction</tt> extends <tt>CompositionFunction</tt> to compose an automaton with a property. <br>
 * <tt>ModelCheckingSynthesisOperator</tt> is used to synthesise an automaton enforcing a given property, using <br>
 * both model checking and synthesis. <br>
 * From this last class the <tt>MpcSynthesisOperator</tt>, <tt>OrchestrationSynthesisOperator</tt>, and <br>
 * <tt>ChoreographySynthesisOperator</tt> are derived. <br>
 * <tt>ProductOrchestrationSynthesisOperator</tt> further specialises the orchestration synthesis for a given configuration. <br>
 *
 * These operations are formally specified in:
 * <ul>
 *     <li>Basile, D. et al., 2020.
 *     Controller synthesis of service contracts with variability. Science of Computer Programming, vol. 187, pp. 102344.
 *      (<a href="https://doi.org/10.1016/j.scico.2019.102344">https://doi.org/10.1016/j.scico.2019.102344</a>)</li>
 *     <li>Basile, D., et al., 2020.
 *     Synthesis of Orchestrations and Choreographies: Bridging the Gap between Supervisory Control and Coordination of Services. Logical Methods in Computer Science, vol. 16(2), pp. 9:1 - 9:29.
 *      (<a href="https://doi.org/10.23638/LMCS-16(2:9)2020">https://doi.org/10.23638/LMCS-16(2:9)2020</a>)</li>
 * </ul>
 *
 */
package io.github.contractautomata.catlib.operations;