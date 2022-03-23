package io.github.contractautomataproject.catlib.family;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.family.Feature;
import io.github.contractautomataproject.catlib.family.Product;

public class ProductTest {

	@Test
	public void testToString() {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
	//	System.out.println(p.toString());
		assertEquals(p.toString(),"R:[cherry, ananas];" + System.lineSeparator()+ 
				"F:[blueberry];"+System.lineSeparator());
	}

	@Test
	public void testEquals1() {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		assertEquals(p,p);
	}

	@Test
	public void testEquals2() {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		Assert.assertNotNull(p);
	}

	@Test
	public void testEquals3() {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		assertNotEquals(p,new Object());
	}

	@Test
	public void testEquals4 () {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		Product pp = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		assertEquals(p,pp);
	}

	@Test
	public void testEquals5() {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		Product pp = new Product(new String[] {"cherry","ananas"}, new String[] {"lemon"});
		assertNotEquals(p,pp);
	}
	@Test
	public void testHashcode() {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		Product pp = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		assertEquals(p.hashCode(),pp.hashCode());
	}

	@Test
	public void testToStringId() {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		assertEquals("p0: R={cherry,ananas,} F={blueberry,}", p.toStringFile(0));
	}

	@Test
	public void testToHTMLString() {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		assertEquals("<html>P0 R:[cherry, ananas]<br />" + 
				"F:[blueberry]</html>", p.toHTMLString("P0"));
	}

//	@Test
//	public void testContainFeature()
//	{
//		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
//		assertTrue(p.containFeature(new Feature("cherry")));
//	}
	
//	@Test
//	public void testContainAllRequiredFeature()
//	{
//		Product p1 = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
//		Product p2 = new Product(new String[] {"cherry"}, new String[] {"blueberry"});
//		
//		assertTrue(p1.containsAllRequiredFeatures(p2));
//	}
//	
//	@Test
//	public void testContainAllForbiddenFeature()
//	{
//		Product p1 = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
//		Product p2 = new Product(new String[] {"cherry"}, new String[] {"blueberry","ananas"});
//		
//		assertTrue(p2.containsAllForbiddenFeatures(p1));
//	}
	//***EXCEPTIONS


	@Test
	public void constructorTest_Exception_nullArgument() {
		String[] arg = new String[0];
		assertThatThrownBy(() -> new Product(null,arg))
		.isInstanceOf(NullPointerException.class);
	}

	@Test
	public void constructorTest_Exception_nullArgument2() {
		Set<Feature> arg = new HashSet<>();
		assertThatThrownBy(() -> new Product(null,arg))
		.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void constructorTest_Exception_3() {
		String[] arg = new String[] {"pippo"};
		assertThatThrownBy(() -> new Product(arg, arg))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("A feature is both required and forbidden");
	}

}
