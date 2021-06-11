package familyTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
/*
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.junit.Test;
 */
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import family.Family;
import family.Product;

public class FamilyTest {

	//	@Test
	//	public void familyOrc() throws Exception
	//	{
	//		String dir = System.getProperty("user.dir");
	//		String fileName =dir+"/CAtest/ValidProducts.prod";
	//		Family fam=new Family(fileName);
	//		FMCA faut = new FMCA(MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(BusinessClientxHotelxEconomyClient).mxe"));
	//		MSCA test = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Orc_family_(BusinessClientxHotelxEconomyClient)_test.mxe");
	//
	//		MSCA aut = faut.getAut();
	//
	//		MSCA controller = fam.getMPCofFamily(aut);		
	//
	//		assertEquals(MSCAtest.checkTransitions(controller, test),true);
	//	}


	@Test
	public void maximalProducts() throws IOException
	{
		String dir = System.getProperty("user.dir");
		String fileName =dir+"/CAtest/ValidProducts.prod";
		Family fam=new Family(fileName);
		//		Set<Product> mp = Arrays.stream(fam.getMaximalProducts())
		//				.mapToObj(i->fam.getElements()[i])
		//				.collect(Collectors.toSet());

		Set<Product> mp= fam.getMaximalProducts();

		Set<Product> test = Family.readFileNew(dir+"/CAtest/maximalProductsTest.prod");

		assertTrue(mp.equals(test));
	}



	@Test
	public void testMaximumDepth() throws IOException
	{
		String dir = System.getProperty("user.dir");
		String fileName =dir+"/CAtest/ValidProducts.prod";
		Family fam=new Family(fileName);
		assertTrue(fam.getMaximumDepth()==11);
	}
	@Test
	public void testReadProducts() throws IOException
	{
		String dir = System.getProperty("user.dir");
		String fileName =dir+"/CAtest/ValidProducts.prod";
		Family fam=new Family(fileName);
		Set<Product> prod = fam.getProducts();
		Set<Product> test=Family.readFileNew(fileName);
		assertTrue(prod.equals(test));
	}

	@Test
	public void getSuperProductsofProduct() throws IOException
	{
		String dir = System.getProperty("user.dir");
		String fileName =dir+"/CAtest/ValidProducts.prod";
		Family fam=new Family(fileName);
		Set<Product> pr=fam.getProducts();
		List<Product> ar = new ArrayList<>(pr);

		int pindex=100;
		//		int[] subind = fam.getSuperProductsofProduct(pindex);
		//		Set<Product> products=Arrays.stream(subind)
		//				.mapToObj(i->pr[i])
		//				.collect(Collectors.toSet());

		Set<Product> products = fam.getSuperProductsofProduct(ar.get(pindex));

		Set<Product> test = Family.readFileNew(dir+"/CAtest/superProductsofProductTest.prod");		
		assertTrue(products.equals(test));
	}

	@Test
	public void getSubProductsofProduct() throws IOException
	{
		String dir = System.getProperty("user.dir");
		String fileName =dir+"/CAtest/ValidProducts.prod";
		Family fam=new Family(fileName);
		Set<Product> pr=fam.getProducts();
		List<Product> ar = new ArrayList<>(pr);

		int pindex=100;

		//		int[] subind = fam.getSubProductsofProduct(pindex);
		//		Set<Product> products=Arrays.stream(subind)
		//				.mapToObj(i->pr[i])
		//				.collect(Collectors.toSet());

		Set<Product> products = fam.getSubProductsofProduct(ar.get(pindex));

		Set<Product> test = Family.readFileNew(dir+"/CAtest/subProductsOfProductTest.prod");

		assertTrue(products.equals(test));
	}

	@Test
	public void writeTest() throws IOException
	{
		String dir = System.getProperty("user.dir");

		Set<Product> pr = Family.readFileNew(dir+"/CAtest/maximalProductsTest.prod");
		Family.writeFile(dir+"/CAtest/write_test", pr);

		Set<Product> test = Family.readFileNew(dir+"/CAtest/write_test.prod");

		assertTrue(pr.equals(test));

	}

	@Test
	public void constructorException()
	{
		Set<Product> pr = null;
		assertThatThrownBy(() -> new Family(pr))
		.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testImportFamily() throws IOException, ParserConfigurationException, SAXException
	{
		String dir = System.getProperty("user.dir");
		Set<Product> pr=Family.importFeatureIDEmodel(dir+"//CAtest//FeatureIDEmodel//",dir+"//CAtest//FeatureIDEmodel//model.xml");
		Family f1=new Family(pr);
		Family f2=new Family(dir+"/CAtest/ValidProducts.prod");
		assertTrue(f1.getProducts().equals(f2.getProducts()));
	}

	@Test
	public void testWriteException() throws IOException
	{
		String dir = System.getProperty("user.dir");

		Set<Product> pr = Family.readFileNew(dir+"/CAtest/maximalProductsTest.prod");
		assertThatThrownBy(() -> Family.writeFile("", pr))
		.isInstanceOf(IllegalArgumentException.class);

	}
}


//@Test
//public void testPO() throws IOException
//{
//	String dir = System.getProperty("user.dir");
//	String fileName =dir+"/CAtest/ValidProducts.prod";
//	Family fam=new Family(fileName);
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

