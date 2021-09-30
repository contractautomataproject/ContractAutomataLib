package io.github.davidebasile.contractautomata.familytest.converterTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.converters.MSCAConverter;
import io.github.davidebasile.contractautomata.converters.MxeConverter;
import io.github.davidebasile.contractautomata.family.FMCA;
import io.github.davidebasile.contractautomata.family.Family;
import io.github.davidebasile.contractautomata.family.PartialProductGenerator;
import io.github.davidebasile.contractautomata.family.Product;
import io.github.davidebasile.contractautomata.family.converters.DimacFamilyConverter;
import io.github.davidebasile.contractautomata.family.converters.FamilyConverter;
import io.github.davidebasile.contractautomata.family.converters.FeatureIDEfamilyConverter;

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
		Set<Product> prod = dfc_pi.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs");	
		Set<Product> prodall = dfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs");
		PartialProductGenerator pg = new PartialProductGenerator();
		Family f2 = new Family(pg.apply(prodall));
		Set<Product> max = f2.getMaximalProducts();
		
		
		assertTrue(prod.equals(max));
	
	}
	
//	@Test
//	public void stressPrimeImplicant() throws Exception
//	{
//
//		Instant start = Instant.now();
//
//		Set<Product> prod = dfc_pi.importProducts(dir+"dimacs_benchmark"+File.separator+"uClinux.dimacs");
//		
//		Instant stop = Instant.now();
//		System.out.println(Duration.between(start, stop).toMillis());
//		
//		start = Instant.now();
//		
//		Set<Product> prodall = dfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"uClinux.dimacs");
//		PartialProductGenerator pg = new PartialProductGenerator();
//		Family f2 = new Family(pg.apply(prodall));
//		Set<Product> max = f2.getMaximalProducts();
//		
//		stop = Instant.now();
//		System.out.println(Duration.between(start, stop).toMillis());
//		
//		assertTrue(prod.equals(max));
//	
//	}
	
	
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

