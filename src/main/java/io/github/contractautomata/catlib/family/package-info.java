/**
 * The family package groups together the functionalities that extend <br>
 * contract automata to product lines.<br>
 * Featured Modal Contract Automata (FMCA) is the name of this extension. <br>
 * The class <code>FMCA</code> implements this type of automata. <br>
 * The family of products is implemented by the class <code>Family</code>. <br>
 * Each product is implemented by the class <code>Product</code>. <br>
 * Each feature of a product is implemented by the class <code>Feature</code>. <br>
 * FMCA exploits the possibility of having partial products, i.e., products where the
 * assignment of features is not completely known. <br>
 * The class <code>PartialProductGenerator</code> is used for generating all partial products  <br>
 * starting from the set of total products, i.e., products where all features are either  <br>
 * assigned or not. <br>
 *
 * The extension of Contract Automata to product lines is fully specified in:
 * <ul>
 *     <li>Basile, D. et al., 2020.
 *     Controller synthesis of service contracts with variability. Science of Computer Programming, vol. 187, pp. 102344.
 *      (<a href="https://doi.org/10.1016/j.scico.2019.102344">https://doi.org/10.1016/j.scico.2019.102344</a>)</li>
 *  </ul>
 *
 *
 *
 * Class diagram of this package:<br>
 *
 * <img src="https://github.com/ContractAutomataProject/ContractAutomataLib/blob/gh-pages/doc/family%20diagram.png?raw=true" alt="the class diagram">
 */
package io.github.contractautomata.catlib.family;