package io.github.davidebasile.contractautomatatest.familytest.converterTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Set;

import org.junit.Test;

import io.github.contractautomataproject.catlib.family.Family;
import io.github.contractautomataproject.catlib.family.Product;
import io.github.contractautomataproject.catlib.family.converters.FamilyConverter;
import io.github.contractautomataproject.catlib.family.converters.ProdFamilyConverter;

public class ProdFamilyConverterTest {
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
	
	@Test
	public void testExportException() throws Exception
	{
		Family fam = new Family(dfc.importProducts(dir +"maximalProductsTest.prod"));
		assertThatThrownBy(() -> dfc.exportFamily("", fam))
		.isInstanceOf(IllegalArgumentException.class);
	}
}
