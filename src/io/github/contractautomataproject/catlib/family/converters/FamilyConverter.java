package io.github.contractautomataproject.catlib.family.converters;

import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.sat4j.reader.ParseFormatException;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.TimeoutException;
import org.xml.sax.SAXException;

import io.github.contractautomataproject.catlib.family.Family;
import io.github.contractautomataproject.catlib.family.Product;

/**
 * Interface for importing/exporting a family
 * @author Davide Basile
 *
 */
public interface FamilyConverter {
	public Set<Product> importProducts(String filename) throws IOException, ParserConfigurationException, SAXException, ParseFormatException, ContradictionException, TimeoutException;
	public void exportFamily(String filename, Family fam) throws IOException;
}
