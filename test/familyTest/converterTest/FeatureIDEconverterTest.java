package familyTest.converterTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import family.Family;
import family.converters.FeatureIDEfamilyConverter;
import family.converters.ProdFamilyConverter;

public class FeatureIDEconverterTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;	
	private final FeatureIDEfamilyConverter ffc = new FeatureIDEfamilyConverter();
	
	@Test
	public void testImportFamily() throws Exception, ParserConfigurationException, SAXException
	{
		
		Family f1= new FeatureIDEfamilyConverter().importFamily(dir+"FeatureIDEmodel"+File.separator+"model.xml");
		Family f2= new ProdFamilyConverter().importFamily(dir +"ValidProducts.prod");
		assertTrue(f1.getProducts().equals(f2.getProducts()));
	}
	
	@Test
	public void testExportException() throws Exception
	{
		Family fam = new ProdFamilyConverter().importFamily(dir +"maximalProductsTest.prod");
		assertThatThrownBy(() -> ffc.exportFamily("", fam))
		.isInstanceOf(UnsupportedOperationException.class);
	}
}
