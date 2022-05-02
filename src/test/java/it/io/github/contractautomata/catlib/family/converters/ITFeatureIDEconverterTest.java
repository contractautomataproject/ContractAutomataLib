package it.io.github.contractautomata.catlib.family.converters;

import it.io.github.contractautomata.catlib.automaton.ITAutomatonTest;
import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.PartialProductGenerator;
import io.github.contractautomata.catlib.family.Product;
import io.github.contractautomata.catlib.family.converters.FamilyConverter;
import io.github.contractautomata.catlib.family.converters.FeatureIDEfamilyConverter;
import io.github.contractautomata.catlib.family.converters.ProdFamilyConverter;
import org.junit.Test;

import java.io.File;
import java.util.Set;
import java.util.function.UnaryOperator;


import static org.junit.Assert.assertEquals;


public class ITFeatureIDEconverterTest {
	private final FamilyConverter ffc = new FeatureIDEfamilyConverter();
	private final FamilyConverter pfc = new ProdFamilyConverter();
	
	@Test
	public void testImportFamily() throws Exception {
		UnaryOperator<Set<Product>> spg = new PartialProductGenerator();
		Family f1= new Family(spg.apply(ffc.importProducts(ITAutomatonTest.dir+ "FeatureIDEmodel" +File.separator+"model.xml")));
		Family f2= new Family(pfc.importProducts(ITAutomatonTest.dir + "ValidProducts.prod"));
		assertEquals(f1.getProducts(),f2.getProducts());
	}
}
