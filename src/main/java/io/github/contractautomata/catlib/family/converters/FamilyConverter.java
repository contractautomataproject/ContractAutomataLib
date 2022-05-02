package io.github.contractautomata.catlib.family.converters;

import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.Product;
import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;
import org.xml.sax.SAXException;

/**
 * This is the interface to be implemented for importing/exporting a family.
 *
 * @author Davide Basile
 *
 */
public interface FamilyConverter {
	/**
	 * Returns a set of products loaded from filename, representing a family of products,
	 * imported from filename.
	 *
	 * @param filename  the name of the file to import
	 * @return a set of products loaded from filename, representing a family of products
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws ParseFormatException
	 * @throws ContradictionException
	 * @throws TimeoutException
	 */
	Set<Product> importProducts(String filename) throws IOException, ParserConfigurationException, SAXException, ParseFormatException, ContradictionException, TimeoutException;

	/**
	 * Stores the content of the family fam in the file filename.
	 *
	 * @param filename the name of the file to which the family of products is stored
	 * @param fam the family to be exported
	 * @throws IOException
	 */
	void exportFamily(String filename, Family fam) throws IOException;
}
