package familyTest.converterTest;

import static org.junit.Assert.assertTrue;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import contractAutomata.MSCA;
import contractAutomata.converters.MSCAConverter;
import contractAutomata.converters.MxeConverter;
import family.FMCA;
import family.Product;
import family.converters.DimacFamilyConverter;
import family.converters.FamilyConverter;
import family.converters.FeatureIDEfamilyConverter;

public class DimacConverterTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final FamilyConverter dfc = new DimacFamilyConverter();
	private final FamilyConverter ffc = new FeatureIDEfamilyConverter();
	private final MSCAConverter bmc = new MxeConverter();
	
	@Test
	public void testImport() throws Exception
	{
		Set<Product> prod= dfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs");
		MSCA aut = bmc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).mxe");
		FMCA fa = new FMCA(aut,prod);
		FMCA fa2 = new FMCA(aut, ffc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.xml"));
		assertTrue(fa.getFamily().equals(fa2.getFamily()));
	}
	
	@Test
	public void testUnsat() throws Exception
	{
		Set<Product> prod= dfc.importProducts(dir+"unsat.dimacs");
		assertTrue(prod.isEmpty());
	}
	
	@Test
	public void testExport() throws IOException
	{
		assertThatThrownBy(()->dfc.exportFamily(null, null))
		.isInstanceOf(UnsupportedOperationException.class);
		//just for coverage
	}
}

