package it.io.github.contractautomata.catlib.family.converters;

import it.io.github.contractautomata.catlib.automaton.ITAutomatonTest;
import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.Product;
import io.github.contractautomata.catlib.family.converters.FamilyConverter;
import io.github.contractautomata.catlib.family.converters.ProdFamilyConverter;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ITProdFamilyConverterTest {
	private final FamilyConverter dfc = new ProdFamilyConverter();
	
	@Test
	public void testReadProducts() throws Exception
	{
		
		String fileName = ITAutomatonTest.dir + "ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		Set<Product> prod = fam.getProducts();
		Set<Product> test=dfc.importProducts(fileName);
		assertEquals(prod,test);
	}

	@Test
	public void testImportExport() throws Exception
	{
		Family fam = new Family(dfc.importProducts(ITAutomatonTest.dir + "maximalProductsTest.prod"));
		dfc.exportFamily(ITAutomatonTest.dir +"write_test", fam);

		Set<Product> test = dfc.importProducts(ITAutomatonTest.dir + "write_test.prod");

		assertEquals(fam.getProducts(),test);

	}
}
