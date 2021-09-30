package io.github.davidebasile.contractautomatatest.familytest.converterTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Set;

import org.junit.Test;

import io.github.davidebasile.contractautomata.family.Family;
import io.github.davidebasile.contractautomata.family.Product;
import io.github.davidebasile.contractautomata.family.converters.FamilyConverter;
import io.github.davidebasile.contractautomata.family.converters.ProdFamilyConverter;

public class ProdFamilyConverterTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final FamilyConverter dfc = new ProdFamilyConverter();
	
	@Test
	public void testReadProducts() throws Exception
	{
		
		String fileName =dir +"ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		Set<Product> prod = fam.getProducts();
		Set<Product> test=dfc.importProducts(fileName);
		assertTrue(prod.equals(test));
	}

	@Test
	public void testImportExport() throws Exception
	{
		Family fam = new Family(dfc.importProducts(dir +"maximalProductsTest.prod"));
		dfc.exportFamily(dir +"write_test", fam);

		Set<Product> test = dfc.importProducts(dir +"write_test.prod");

		assertTrue(fam.getProducts().equals(test));

	}
	
	@Test
	public void testExportException() throws Exception
	{
		Family fam = new Family(dfc.importProducts(dir +"maximalProductsTest.prod"));
		assertThatThrownBy(() -> dfc.exportFamily("", fam))
		.isInstanceOf(IllegalArgumentException.class);
	}
}
