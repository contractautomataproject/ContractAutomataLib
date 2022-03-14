package io.github.davidebasile.contractautomatatest.familytest.converterTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.ModalAutomaton;
import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;
import io.github.contractautomataproject.catlib.family.FMCA;
import io.github.contractautomataproject.catlib.family.Family;
import io.github.contractautomataproject.catlib.family.PartialProductGenerator;
import io.github.contractautomataproject.catlib.family.Product;
import io.github.contractautomataproject.catlib.family.converters.DimacFamilyConverter;
import io.github.contractautomataproject.catlib.family.converters.FamilyConverter;
import io.github.contractautomataproject.catlib.family.converters.FeatureIDEfamilyConverter;

public class DimacConverterTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"test_resources"+File.separator;

	private final FamilyConverter dfc = new DimacFamilyConverter(true);
	private final FamilyConverter dfc_pi = new DimacFamilyConverter(false);
	private final FamilyConverter ffc = new FeatureIDEfamilyConverter();
	private final AutDataConverter<CALabel> bdc = new AutDataConverter<>(CALabel::new);
	
	@Test
	public void testImport() throws Exception
	{
		Set<Product> prod= dfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs");
		ModalAutomaton<CALabel> aut = bdc.importMSCA(dir+"(BusinessClientxHotelxEconomyClient).data");

//		Set<Feature> actions = aut.getUnsignedActions().stream()
//				.map(Feature::new)
//				.collect(Collectors.toSet());
//		Set<Product> refinedProducts = prod.parallelStream()
//				.map(p->p.retainFeatures(actions))
//				.collect(Collectors.toSet());
				
		FMCA fmca = new FMCA(aut,prod);
		
		
		FMCA fmca_two = new FMCA(aut, ffc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.xml"));
		assertEquals(fmca.getFamily(), fmca_two.getFamily());
	}
	
	@Test
	public void testPrimeImplicant() throws Exception
	{
		Set<Product> prod = dfc_pi.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs");	
		Set<Product> prodall = dfc.importProducts(dir+"FeatureIDEmodel"+File.separator+"model.dimacs");
		PartialProductGenerator pg = new PartialProductGenerator();
		Family f2 = new Family(pg.apply(prodall));
		Set<Product> max = f2.getMaximalProducts();
		assertEquals(prod,max);
	
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

