package io.github.davidebasile.contractautomata.familytest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertFalse;
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

import org.junit.Test;

import io.github.davidebasile.contractautomata.family.Family;
import io.github.davidebasile.contractautomata.family.Product;
import io.github.davidebasile.contractautomata.family.converters.FamilyConverter;
import io.github.davidebasile.contractautomata.family.converters.ProdFamilyConverter;

public class FamilyTest {
	private final String dir = System.getProperty("user.dir")+File.separator+"CAtest"+File.separator;
	private final FamilyConverter dfc = new ProdFamilyConverter();
	
	@Test
	public void maximalProducts() throws Exception
	{
		
		String fileName =dir+"ValidProducts.prod";
		Family fam=  new Family(dfc.importProducts(fileName));
		//		Set<Product> mp = Arrays.stream(fam.getMaximalProducts())
		//				.mapToObj(i->fam.getElements()[i])
		//				.collect(Collectors.toSet());

		Set<Product> mp= fam.getMaximalProducts();

		Set<Product> test = dfc.importProducts(dir +"maximalProductsTest.prod");

		assertTrue(mp.equals(test));
	}



	@Test
	public void testMaximumDepth() throws Exception
	{
		
		String fileName =dir +"ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		assertTrue(fam.getMaximumDepth()==11);
	}

	@Test
	public void getSuperProductsofProduct() throws Exception
	{
		
		String fileName =dir +"ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		Set<Product> pr=fam.getProducts();
		List<Product> ar = new ArrayList<>(pr);

		int pindex=100;
		//		int[] subind = fam.getSuperProductsofProduct(pindex);
		//		Set<Product> products=Arrays.stream(subind)
		//				.mapToObj(i->pr[i])
		//				.collect(Collectors.toSet());

		Set<Product> products = fam.getSuperProductsofProduct(ar.get(pindex));
		//Family.writeFile(dir +"superProductsOfProduct_test", products);
		
		Set<Product> test = dfc.importProducts(dir +"superProductsOfProduct_test.prod"); //)superProductsofProductTest.prod");	
		
		
		assertTrue(products.equals(test));
	}

	@Test
	public void getSubProductsofProduct() throws Exception
	{
		
		String fileName =dir +"ValidProducts.prod";
		Family fam=new Family(dfc.importProducts(fileName));
		Set<Product> pr=fam.getProducts();
		List<Product> ar = new ArrayList<>(pr);

		int pindex=100;

		Set<Product> products = fam.getSubProductsofProduct(ar.get(pindex));

		//Family.writeFile(dir +"subProductsOfProduct_test", products);
		Set<Product> test = dfc.importProducts(dir +"subProductsOfProduct_test.prod"); 
		
		assertTrue(products.equals(test));
	}


	@Test
	public void constructorException()
	{
		Set<Product> pr = null;
		assertThatThrownBy(() -> new Family(pr))
		.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void constructorException2()
	{
		Set<Product> pr = null;
		assertThatThrownBy(() -> new Family(pr,null,null))
		.isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void testEquals1() throws Exception {
		Family fam = new Family(Set.of(new Product(new String[] {"apple"},new String[] {})));
		assertTrue(fam.equals(fam));
	}
	
	@Test
	public void testEquals2() throws Exception {
		Family fam = new Family(Set.of(new Product(new String[] {"apple"},new String[] {})));
		assertFalse(fam.equals(null));
	}

	@Test
	public void testToString() throws Exception {
		String ln = System.lineSeparator();
		Family fam = new Family(Set.of(new Product(new String[] {"apple"},new String[] {})));
		assertTrue(fam.toString().equals("Family [products=[R:[apple];"+ln+"F:[];"+ln+"]]"));
	}
	
	@Test
	public void testHashCode() throws Exception {
		Family fam = new Family(Set.of(new Product(new String[] {"apple"},new String[] {})));

		Family fam2 = new Family(Set.of(new Product(new String[] {"apple"},new String[] {})));
		
		assertTrue(fam.hashCode()==fam2.hashCode());
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

