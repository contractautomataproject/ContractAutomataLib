/**
 * The family.converters package groups the I/O operations of import/export <br>
 * of a product line. <br>
 * Each of these converters must implement the interface <tt>FamilyConverter</tt>, <br>
 * with methods for importing/exporting.<br>
 * <tt>ProdFamilyConverter</tt> converts a family to a textual representation, with extension <tt>.prod</tt>. <br>
 * <tt>FeatureIDEfamilyConverter</tt> imports the products generated using the tool FeatureIDE. <br>
 * <tt>DimacFamilyConverter</tt> imports all products that are models of a formula expressed in DIMAC format,  <br>
 * in a file with extension <tt>.dimac</tt>
 */
package io.github.contractautomata.catlib.family.converters;