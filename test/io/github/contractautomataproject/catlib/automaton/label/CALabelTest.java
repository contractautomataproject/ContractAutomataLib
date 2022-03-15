package io.github.contractautomataproject.catlib.automaton.label;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.Label;

public class CALabelTest {

	@Test
	public void constructor_getaction_Test() {
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.IDLE);
		lab.add(CALabel.OFFER+"a");
		lab.add(CALabel.REQUEST+"a");
		CALabel calab= new CALabel(lab);
		
		assertTrue(calab.getPrincipalAction().startsWith(CALabel.OFFER));
	}

	@Test
	public void constructorTest() {
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.IDLE);
		lab.add(CALabel.OFFER+"a");
		lab.add(CALabel.REQUEST+"a");
		CALabel calab= new CALabel(lab);
		
		assertTrue(calab.getPrincipalAction().startsWith(CALabel.OFFER));
	}

	@Test
	public void equalsSameTrue() {
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.IDLE);
		lab.add(CALabel.OFFER+"a");
		lab.add(CALabel.REQUEST+"a");
		CALabel calab= new CALabel(lab);
		Assert.assertEquals(calab,calab);
	}
	
	@Test
	public void equalsTwoInstancesTrue() {
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.IDLE);
		lab.add(CALabel.OFFER+"a");
		lab.add(CALabel.REQUEST+"a");
		Assert.assertEquals(new CALabel(lab),new CALabel(lab));
	}
	
	@Test
	public void equalsNullFalse() {
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.IDLE);
		lab.add(CALabel.OFFER+"a");
		lab.add(CALabel.REQUEST+"a");
		Assert.assertNotNull(new CALabel(lab));
	}
	
	@Test
	public void equalsFalse() {
		List<String> lab = new ArrayList<>();
		lab.add(CALabel.IDLE);
		lab.add(CALabel.OFFER+"a");
		lab.add(CALabel.REQUEST+"a");
		Assert.assertNotEquals(new CALabel(lab),new CALabel(1,0,"!a"));
	}

	//********************** testing exceptions *********************
	
	@Test
	public void constructorTest1_Exception_nullArgument() {
		assertThatThrownBy(() -> new CALabel(null,null,"!a"))
	    .isInstanceOf(NullPointerException.class);
	}
	@Test
	public void constructorTest2_Exception_nullArgument() {
		assertThatThrownBy(() -> new CALabel(null,null,null,"!a",null))
	    .isInstanceOf(NullPointerException.class);
	 //   .hasMessageContaining("Null argument");
	}
//	
	@Test
	public void constructorTest3_Exception() {
		CALabel ca = new CALabel(1,0,"!a");
		assertThatThrownBy(() -> new CALabel(ca,0,-2))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
//	@Test
//	public void constructorTest4_Exception_nullArgument() {
//		assertThatThrownBy(() -> new CALabel(3,null,null,"!a"))
//	    .isInstanceOf(IllegalArgumentException.class)
//	    .hasMessageContaining("Null argument");
//	}
	
	
	
	@Test
	public void constructorTest_Exception_Empty() {
		List<String> l = new ArrayList<String>();
		assertThatThrownBy(() -> new CALabel(l))
	    .isInstanceOf(IllegalArgumentException.class);
	}
	
	
	@Test
	public void constructorTest_Exception_noAction() {
		assertThatThrownBy(() -> new CALabel(2,0,"aa"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("The action is not a request nor an offer");
	}
	
	@Test
	public void constructorTest_Exception_noMatch() {
		assertThatThrownBy(() -> new CALabel(2,0,1,"!a","!a"))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("The actions must be an offer and a request");
	}
	
//	@Test
//	public void constructorTest_Exception_noOffer() {
//		assertThatThrownBy(() -> new CALabel(3,1,2,"?a"))
//	    .isInstanceOf(IllegalArgumentException.class)
//	    .hasMessageContaining("This constructor is only for matches and by convention action is the offer");
//	}
	

	@Test
	public void constructorTest_Exception_emptyLabel() {
		List<String> arg = new ArrayList<String>();
		assertThatThrownBy(() -> new CALabel(arg))
	    .isInstanceOf(IllegalArgumentException.class);
	//    .hasMessageContaining("Empty label");
	}
	
//	@Test
//	public void constructorTest_Exception_nullReferencesLabel() {
//		List<String> l = new ArrayList<String>();
//		l.add(null);
//		assertThatThrownBy(() -> new CALabel(l))
//	    .isInstanceOf(IllegalArgumentException.class)
//	    .hasMessageContaining("Label contains null references");
//	}

	@Test
	public void constructorTest_Exception_notWellFormedLabel() {
		List<String> l = new ArrayList<String>();
		l.add("aaa");
		assertThatThrownBy(() -> new CALabel(l))
	    .isInstanceOf(IllegalArgumentException.class);
	//    .hasMessageContaining("The label is not well-formed");
	}
		
	@Test
	public void constructorTest_Exception_notWellFormedIdleLabel() {
		List<String> l = new ArrayList<String>();
		l.add(CALabel.IDLE);
		assertThatThrownBy(() -> new CALabel(l))
	    .isInstanceOf(IllegalArgumentException.class);
	   // .hasMessageContaining("The label is not well-formed");
	}
	
	@Test
	public void constructorTest_Exception_notWellFormedOffersLabel() {
		List<String> l = new ArrayList<String>();
		l.add("!a");
		l.add("!a");
		assertThatThrownBy(() -> new CALabel(l))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("The label is not well-formed");
	}
	
	@Test
	public void constructorTest_Exception_notWellFormedRequestsLabel() {
		List<String> l = new ArrayList<String>();
		l.add("?a");
		l.add("?a");
		assertThatThrownBy(() -> new CALabel(l))
	    .isInstanceOf(IllegalArgumentException.class)
	    .hasMessageContaining("The label is not well-formed");
	}
	
	@Test
	public void getOffererTest_Exception() {
		CALabel cl = new CALabel(1,0,"?a");
		assertThatThrownBy(() -> cl.getOfferer())
	    .isInstanceOf(UnsupportedOperationException.class)
	    .hasMessageContaining("No offerer in a request action");
	}
	
	@Test
	public void getRequesterTest_Exception() {
		CALabel cl = new CALabel(1,0,"!a");
		assertThatThrownBy(() -> cl.getRequester())
	    .isInstanceOf(UnsupportedOperationException.class)
	    .hasMessageContaining("No requester in an offer action");
	}
	
//	@Test
//	public void getOffererOrRequesterTest_Exception() {
//		assertThatThrownBy(() -> new CALabel(2,0,1,"!a").getOffererOrRequester())
//	    .isInstanceOf(UnsupportedOperationException.class)
//	    .hasMessageContaining("Action is not a request nor an offer");
//	}
	
	@Test
	public void matchException() {
		Label<List<String>> l  = new Label<>(List.of("ei"));
		CALabel ca = new CALabel(1,0,"!a");
		assertThatThrownBy(() -> ca.match(l))
	    .isInstanceOf(IllegalArgumentException.class);
	
	}
	
}
