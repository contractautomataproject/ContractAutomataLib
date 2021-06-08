package familyTest;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
/*
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
 */
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.junit.Test;

import family.Feature;
import family.Product;

public class ProductTest {

	@Test
	public void testToString() {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
	//	System.out.println(p.toString());
		assertEquals(p.toString(),"R:[cherry, ananas];\n" + 
				"F:[blueberry];\n");
	}

	@Test
	public void testeEquals1() {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		assertEquals(p.equals(p),true);
	}

	@Test
	public void testeEquals2() {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		assertEquals(p.equals(null),false);
	}

	@Test
	public void testeEquals3() {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		assertEquals(p.equals("String"),false);
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
		assertFalse(p.equals(pp));
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
		assertEquals(p.toStringFile(0),"p0: R={cherry,ananas,} F={blueberry,}");
	}

	@Test
	public void testToHTMLString() {
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
//		System.out.println(p.toString());
		assertEquals(p.toHTMLString("P0"),"<html>P0 R:[cherry, ananas]<br />" + 
				"F:[blueberry]</html>");
	}

	@Test
	public void testContainFeature()
	{
		Product p = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		assertTrue(p.containFeature(new Feature("cherry")));
	}
	
	@Test
	public void testContainAllRequiredFeature()
	{
		Product p1 = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		Product p2 = new Product(new String[] {"cherry"}, new String[] {"blueberry"});
		
		assertTrue(p1.containsAllRequiredFeatures(p2));
	}
	
	@Test
	public void testContainAllForbiddenFeature()
	{
		Product p1 = new Product(new String[] {"cherry","ananas"}, new String[] {"blueberry"});
		Product p2 = new Product(new String[] {"cherry"}, new String[] {"blueberry","ananas"});
		
		assertTrue(p2.containsAllForbiddenFeatures(p1));
	}
	//***EXCEPTIONS


	@Test
	public void constructorTest_Exception_nullArgument() {
		assertThatThrownBy(() -> new Product(null,new String[0]))
		.isInstanceOf(NullPointerException.class);
	}

	@Test
	public void constructorTest_Exception_nullArgument2() {
		assertThatThrownBy(() -> new Product(null,new HashSet<Feature>()))
		.isInstanceOf(IllegalArgumentException.class);
	}
	
	@Test
	public void constructorTest_Exception_3() {
		assertThatThrownBy(() -> new Product(new String[] {"pippo"}, new String[] {"pippo"}))
		.isInstanceOf(IllegalArgumentException.class)
		.hasMessageContaining("A feature is both required and forbidden");
	}
	
	@Test 
	public void testCompareToException()
	{
		assertThatThrownBy(() -> new Product(new String[] {"pippo"}, new String[] {"pluto"})
		.compareTo(new Product(new String[] {"ananas"}, new String[] {"banana"})))
		.isInstanceOf(UnsupportedOperationException.class)
		.hasMessageContaining("Products are not comparable");
		
	}
}
