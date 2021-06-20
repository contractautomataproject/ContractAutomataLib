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
import family.Family;
import family.PartialProductGenerator;
import family.Product;
import family.converters.DimacFamilyConverter;
import family.converters.FamilyConverter;
import family.converters.FeatureIDEfamilyConverter;

public class DimacConverterTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final FamilyConverter dfc = new DimacFamilyConverter(true);
	private final FamilyConverter dfc_pi = new DimacFamilyConverter(false);
	private final FamilyConverter ffc = new FeatureIDEfamilyConverter();
	private final MSCAConverter bmc = new MxeConverter();
	
	@Test
	public void testImport() throws Exception
	{
		Set<Product> prod= dfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs");

//		System.out.println(prod);
		
		MSCA aut = bmc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).mxe");

//		Set<Feature> actions = aut.getUnsignedActions().stream()
//				.map(Feature::new)
//				.collect(Collectors.toSet());
//		Set<Product> refinedProducts = prod.parallelStream()
//				.map(p->p.retainFeatures(actions))
//				.collect(Collectors.toSet());
				
		FMCA fmca = new FMCA(aut,prod);
		
		
		FMCA fmca_two = new FMCA(aut, ffc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.xml"));
		assertTrue(fmca.getFamily().equals(fmca_two.getFamily()));
	}
	
	@Test
	public void testPrimeImplicant() throws Exception
	{
		long start = System.currentTimeMillis();
		Set<Product> prod = dfc_pi.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs");
		long t1=System.currentTimeMillis()-start;
		
		start = System.currentTimeMillis();
		Set<Product> prodall = dfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs");
		PartialProductGenerator pg = new PartialProductGenerator();
		Family f2 = new Family(pg.apply(prodall));
		Set<Product> max = f2.getMaximalProducts();
		long t2=System.currentTimeMillis()-start;
		
		System.out.println(t1 + " " + t2);
		assertTrue(prod.equals(max));
	
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

