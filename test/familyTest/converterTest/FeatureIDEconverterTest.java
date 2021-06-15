package familyTest.converterTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Set;
import java.util.function.UnaryOperator;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import family.Family;
import family.Product;
import family.PartialProductGenerator;
import family.converters.FamilyConverter;
import family.converters.FeatureIDEfamilyConverter;
import family.converters.ProdFamilyConverter;

public class FeatureIDEconverterTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;	
	private final FamilyConverter ffc = new FeatureIDEfamilyConverter();
	private final FamilyConverter pfc = new ProdFamilyConverter();
	
	@Test
	public void testImportFamily() throws Exception, ParserConfigurationException, SAXException
	{
		UnaryOperator<Set<Product>> spg = new PartialProductGenerator();
		Family f1= new Family(spg.apply(ffc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.xml")));
		Family f2= new Family(pfc.importProducts(dir +"ValidProducts.prod"));
		assertTrue(f1.getProducts().equals(f2.getProducts()));
	}
	
	@Test
	public void testExportException() throws Exception
	{
		Family fam = new Family(pfc.importProducts(dir +"maximalProductsTest.prod"));
		assertThatThrownBy(() -> ffc.exportFamily("", fam))
		.isInstanceOf(UnsupportedOperationException.class);
	}
}
