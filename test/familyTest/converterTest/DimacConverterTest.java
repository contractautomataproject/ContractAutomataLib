package familyTest.converterTest;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;

import contractAutomata.MSCA;
import contractAutomata.converters.MSCAConverter;
import contractAutomata.converters.MxeConverter;
import family.Family;
import family.Feature;
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
		Family fam = new Family(dfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs"));
		MSCA aut = bmc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).mxe");
		Set<Feature> actions = aut.getUnsignedActions().stream()
				.map(Feature::new)
				.collect(Collectors.toSet());
		
		fam = new Family(fam.getProducts().parallelStream()
		.map(p->p.retainFeatures(actions))
		.collect(Collectors.toSet()));
		
		Family test=new Family(ffc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.xml"));
		
		assertTrue(fam.equals(test));
	}
}
