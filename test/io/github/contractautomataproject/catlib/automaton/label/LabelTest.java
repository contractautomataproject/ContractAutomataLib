package io.github.contractautomataproject.catlib.automaton.label;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LabelTest {
	
	Label<String> lab;
	
	@Before
	public void setup() {
		lab = new Label<>("a");
	}
	
	
	@Test
	public void testGetAction() {
		Assert.assertEquals("a", lab.getAction());
	}
	
	@Test
	public void testMatch() {
		Assert.assertTrue(lab.match(new Label<>("a")));
	}

	@Test
	public void testHashcode() {

		Assert.assertEquals(lab.hashCode(),new Label<String>("a").hashCode());
	}

	@Test
	public void testGetRank() {
		Assert.assertEquals(1, lab.getRank().intValue());
	}

	@Test
	public void testGetRank2() {
		Label<List<String>> l = new Label<>(List.of("a","b"));	
		Assert.assertEquals(2, l.getRank().intValue());
	}
	
	@Test
	public void testToString() {
		Assert.assertEquals("a", lab.toString());
	}
	
//	@Test
//	public void testToCSV() {
//		Assert.assertEquals("[action=a]", lab.toCSV());
//	}
	
	@Test
	public void equalsSameTrue() {
		Assert.assertEquals(lab,lab);
	}
	
	@Test
	public void equalsTwoInstancesTrue() {

		Assert.assertEquals(lab,new Label<String>("a"));
	}
	
	@Test
	public void equalsNullFalse() {
		Assert.assertNotEquals(lab,null);
	}
	
	@Test
	public void equalsClassFalse() {
		Assert.assertNotEquals(lab,"b");
	}
	
	@Test
	public void equalsFalse() {
		Assert.assertNotEquals(lab,new Label<String>("b"));
	}
	
	@Test
	public void constructorException1() {
		assertThatThrownBy(() -> new Label<String>(null))
	    .isInstanceOf(IllegalArgumentException.class);

	}

	
//	@Test
//	public void constructorException2() {
//		assertThatThrownBy(() -> new Label<String>(""))
//	    .isInstanceOf(IllegalArgumentException.class);
//
//	}

}
