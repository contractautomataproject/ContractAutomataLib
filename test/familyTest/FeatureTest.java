package familyTest;


import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
/*
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;
 */
import static org.junit.Assert.assertFalse;

import org.junit.Test;

import family.Feature;

public class FeatureTest {

	

	@Test
	public void testeEquals1() {
		Feature p = new Feature("cherry");
		assertEquals(p.equals(p),true);
	}

	@Test
	public void testeEquals2() {
		Feature p = new Feature("cherry");
		assertEquals(p.equals(null),false);
	}

	@Test
	public void testeEquals3() {
		Feature p = new Feature("cherry");
		assertEquals(p.equals("String"),false);
	}

	@Test
	public void testEquals4 () {
		Feature p = new Feature("cherry");
		
		Feature pp = new Feature("cherry");
		assertEquals(p,pp);
	}

	@Test
	public void testEquals5() {
		Feature p = new Feature("cherry");
		
		Feature pp = new Feature("ananas");
		assertFalse(p.equals(pp));
	}

	//***EXCEPTIONS


	@Test
	public void constructorTest_Exception_nullArgument() {
		assertThatThrownBy(() -> new Feature(null))
		.isInstanceOf(IllegalArgumentException.class);
	}

}
