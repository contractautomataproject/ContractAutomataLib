package familyTest.converterTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Set;

import org.junit.Test;

import family.Family;
import family.Product;
import family.converters.FamilyConverter;
import family.converters.ProdFamilyConverter;

public class ProdFamilyConverterTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final FamilyConverter dfc = new ProdFamilyConverter();
	
	@Test
	public void testReadProducts() throws Exception
	{
		
		String fileName =dir +"ValidProducts.prod";
		Family fam=dfc.importFamily(fileName);
		Set<Product> prod = fam.getProducts();
		Set<Product> test=dfc.importFamily(fileName).getProducts();
		assertTrue(prod.equals(test));
	}

	@Test
	public void testImportExport() throws Exception
	{
		Family fam = dfc.importFamily(dir +"maximalProductsTest.prod");
		dfc.exportFamily(dir +"write_test", fam);

		Set<Product> test = dfc.importFamily(dir +"write_test.prod").getProducts();

		assertTrue(fam.getProducts().equals(test));

	}
	
	@Test
	public void testExportException() throws Exception
	{
		Family fam = dfc.importFamily(dir +"maximalProductsTest.prod");
		assertThatThrownBy(() -> dfc.exportFamily("", fam))
		.isInstanceOf(IllegalArgumentException.class);
	}
}
