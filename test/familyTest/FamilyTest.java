package familyTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
/*
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.junit.Test;
 */
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import family.ProdFamilyConverter;
import family.Family;
import family.FamilyConverter;
import family.FeatureIDEfamilyConverter;
import family.Product;

public class FamilyTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final FamilyConverter dfc = new ProdFamilyConverter();
	
	@Test
	public void maximalProducts() throws Exception
	{
		
		String fileName =dir+"/CAtest/ValidProducts.prod";
		Family fam=  dfc.importFamily(fileName);
		//		Set<Product> mp = Arrays.stream(fam.getMaximalProducts())
		//				.mapToObj(i->fam.getElements()[i])
		//				.collect(Collectors.toSet());

		Set<Product> mp= fam.getMaximalProducts();

		Set<Product> test = dfc.importFamily(dir +"maximalProductsTest.prod").getProducts();

		assertTrue(mp.equals(test));
	}



	@Test
	public void testMaximumDepth() throws Exception
	{
		
		String fileName =dir +"ValidProducts.prod";
		Family fam=dfc.importFamily(fileName);
		assertTrue(fam.getMaximumDepth()==11);
	}
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
	public void getSuperProductsofProduct() throws Exception
	{
		
		String fileName =dir +"ValidProducts.prod";
		Family fam=dfc.importFamily(fileName);
		Set<Product> pr=fam.getProducts();
		List<Product> ar = new ArrayList<>(pr);

		int pindex=100;
		//		int[] subind = fam.getSuperProductsofProduct(pindex);
		//		Set<Product> products=Arrays.stream(subind)
		//				.mapToObj(i->pr[i])
		//				.collect(Collectors.toSet());

		Set<Product> products = fam.getSuperProductsofProduct(ar.get(pindex));
		//Family.writeFile(dir +"superProductsOfProduct_test", products);
		
		Set<Product> test = dfc.importFamily(dir +"superProductsOfProduct_test.prod").getProducts(); //)superProductsofProductTest.prod");	
		
		//System.out.println(test.toString()+"\n"+products.toString());
		
		assertTrue(products.equals(test));
	}

	@Test
	public void getSubProductsofProduct() throws Exception
	{
		
		String fileName =dir +"ValidProducts.prod";
		Family fam=dfc.importFamily(fileName);
		Set<Product> pr=fam.getProducts();
		List<Product> ar = new ArrayList<>(pr);

		int pindex=100;

		//		int[] subind = fam.getSubProductsofProduct(pindex);
		//		Set<Product> products=Arrays.stream(subind)
		//				.mapToObj(i->pr[i])
		//				.collect(Collectors.toSet());

		Set<Product> products = fam.getSubProductsofProduct(ar.get(pindex));


		//Family.writeFile(dir +"subProductsOfProduct_test", products);
		Set<Product> test = dfc.importFamily(dir +"subProductsOfProduct_test.prod").getProducts(); 
		// subProductsOfProductTest.prod");


		
		assertTrue(products.equals(test));
	}

	@Test
	public void writeTest() throws Exception
	{
		Family fam = dfc.importFamily(dir +"maximalProductsTest.prod");
		dfc.exportFamily(dir +"write_test", fam);

		Set<Product> test = dfc.importFamily(dir +"write_test.prod").getProducts();

		assertTrue(fam.getProducts().equals(test));

	}

	@Test
	public void constructorException()
	{
		Set<Product> pr = null;
		assertThatThrownBy(() -> new Family(pr))
		.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testImportFamily() throws Exception, ParserConfigurationException, SAXException
	{
		
		Family f1= new FeatureIDEfamilyConverter().importFamily(dir+"FeatureIDEmodel"+File.separator+"model.xml");
		Family f2=dfc.importFamily(dir +"ValidProducts.prod");
		assertTrue(f1.getProducts().equals(f2.getProducts()));
	}

	@Test
	public void testWriteException() throws Exception
	{
		Family fam = dfc.importFamily(dir +"maximalProductsTest.prod");
		assertThatThrownBy(() -> dfc.exportFamily("", fam))
		.isInstanceOf(IllegalArgumentException.class);

	}
}


//	@Test
//	public void familyOrc() throws Exception
//	{
//		
//		String fileName =dir +"ValidProducts.prod";
//		Family fam=dfc.importFamily(fileName);
//		FMCA faut = new FMCA(MSCAIO.parseXMLintoMSCA(dir +"(BusinessClientxHotelxEconomyClient).mxe"));
//		MSCA test = MSCAIO.parseXMLintoMSCA(dir +"Orc_family_(BusinessClientxHotelxEconomyClient)_test.mxe");
//
//		MSCA aut = faut.getAut();
//
//		MSCA controller = fam.getMPCofFamily(aut);		
//
//		assertEquals(MSCAtest.checkTransitions(controller, test),true);
//	}




//@Test
//public void testPO() throws Exception
//{
//	
//	String fileName =dir +"ValidProducts.prod";
//	Family fam=dfc.importFamily(fileName);
//	Product[] products = fam.getElements();
//	int[][] po = fam.getPartialOrder();
//	int[][] reversepo = fam.getReversePO();
//	Map<Product,Map<Boolean,Set<Product>>> map = new HashMap<>();
//	IntStream.range(0,products.length)
//	.forEach(i->{
//		Map<Boolean,Set<Product>> m2p = new HashMap<>();
//		m2p.put(true,IntStream.range(0,po[i].length)
//				.filter(o->po[i][o]==1)
//				.mapToObj(o->products[o])
//				.collect(Collectors.toSet()));
//		m2p.put(false,IntStream.range(0,reversepo[i].length)
//				.filter(o->reversepo[i][o]==1)
//				.mapToObj(o->products[o])
//				.collect(Collectors.toSet()));
//		map.put(products[i], m2p);
//	});
//
//	Map<Product,Map<Boolean,Set<Product>>> test = fam.getPom();
//
//	assertTrue(map.equals(test));
//
//}

