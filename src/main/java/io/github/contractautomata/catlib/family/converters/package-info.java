/**
 * The family.converters package groups the I/O operations of import/export <br>
 * of a product line. <br>
 * Each of these converters must implement the interface <code>FamilyConverter</code>, <br>
 * with methods for importing/exporting.<br>
 * <code>ProdFamilyConverter</code> converts a family to a textual representation, with extension <code>.prod</code>. <br>
 * <code>FeatureIDEfamilyConverter</code> imports the products generated using the tool FeatureIDE. <br>
 * <code>DimacFamilyConverter</code> imports all products that are models of a formula expressed in DIMAC format,  <br>
 * in a file with extension <code>.dimac</code>
 *
 * Class diagram of this package:<br>
 *
 * <img src="https://github.com/ContractAutomataProject/ContractAutomataLib/blob/gh-pages/doc/family%20converters%20diagram.png?raw=true" alt="the class diagram">
 */
package io.github.contractautomata.catlib.family.converters;