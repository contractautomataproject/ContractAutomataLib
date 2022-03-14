package io.github.davidebasile.contractautomatatest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.label.Label;

public class LabelTest {
	
	@Test
	public void testEquals() {
		Label<String> lab = new Label<>("dummy");
		Assert.assertEquals(lab,lab);
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
