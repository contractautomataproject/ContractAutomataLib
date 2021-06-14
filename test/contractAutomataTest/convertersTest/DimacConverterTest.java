package contractAutomataTest.convertersTest;

import java.io.File;

import org.junit.Test;

import family.converters.DimacFamilyConverter;
import family.converters.FamilyConverter;

public class DimacConverterTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final FamilyConverter dfc = new DimacFamilyConverter();
	
	@Test
	public void testImport() throws Exception
	{
		dfc.importFamily(dir+"FeatureIDEmodel"+File.separator+"model.dimacs");
	}
}
