package it.io.github.contractautomata.catlib.family.converters;

import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.Product;
import io.github.contractautomata.catlib.family.converters.FamilyConverter;
import io.github.contractautomata.catlib.family.converters.ProdFamilyConverter;
import org.junit.Test;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ITProdFamilyConverterTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;
	private final FamilyConverter dfc = new ProdFamilyConverter();
	
	@Test
	public void testReadProducts() throws Exception
	{
		
		String fileName =dir +"ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		Set<Product> prod = fam.getProducts();
		Set<Product> test=dfc.importProducts(fileName);
		assertEquals(prod,test);
	}

	@Test
	public void testImportExport() throws Exception
	{
		Family fam = new Family(dfc.importProducts(dir +"maximalProductsTest.prod"));
		dfc.exportFamily(dir +"write_test", fam);

		Set<Product> test = dfc.importProducts(dir +"write_test.prod");

		assertEquals(fam.getProducts(),test);

	}
}
