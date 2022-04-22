package io.github.contractautomata.catlib.family.converters;

import io.github.contractautomata.catlib.family.Family;
import io.github.contractautomata.catlib.family.Product;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.File;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.Strict.class)
public class ProdFamilyConverterTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test"+File.separator+"test_resources"+File.separator;
	private  FamilyConverter dfc;
	@Mock Family fam;

	@Before
	public void setUp(){
		dfc = new ProdFamilyConverter();
	}

	@Test
	public void testImportProducts() throws Exception
	{
		String test = "[R:[card, invoice];"+System.lineSeparator()+
				"F:[sharedRoom, cash];"+System.lineSeparator()+
				", R:[card, sharedBathroom];"+System.lineSeparator()+
				"F:[cash];"+System.lineSeparator()+
				", R:[card];"+System.lineSeparator()+
				"F:[invoice, sharedRoom, cash];"+System.lineSeparator()+
				", R:[card];"+System.lineSeparator()+
				"F:[sharedRoom, cash];"+System.lineSeparator()+
				", R:[invoice, cash];"+System.lineSeparator()+
				"F:[card, sharedRoom];"+System.lineSeparator()+
				"]";
		assertEquals(test,dfc.importProducts(dir +"SomeProducts.prod").toString());
	}

	@Test
	public void testImportExport() throws Exception
	{
		Set<Product> set = dfc.importProducts(dir +"SomeProducts.prod");
		when(fam.getProducts()).thenReturn(set);
		dfc.exportFamily(dir +"SomeProductsTest", fam);
		assertEquals(set,dfc.importProducts(dir +"SomeProductsTest.prod"));
	}

	@Test
	public void testImportExportWithSuffix() throws Exception
	{
		Set<Product> set = dfc.importProducts(dir +"SomeProducts.prod");
		when(fam.getProducts()).thenReturn(set);
		dfc.exportFamily(dir +"SomeProductsTest.prod", fam);
		assertEquals(set,dfc.importProducts(dir +"SomeProductsTest.prod"));
	}
	
	@Test
	public void testExportExceptionEmpty()
	{
		assertThrows(IllegalArgumentException.class, () -> dfc.exportFamily("", fam));
	}


	@Test
	public void testExportExceptionNull()
	{
		assertThrows(IllegalArgumentException.class, () -> dfc.exportFamily(null, fam));
	}

}
