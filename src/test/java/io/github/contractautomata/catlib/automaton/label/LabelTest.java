package io.github.contractautomata.catlib.automaton.label;

import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.IdleAction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.Strict.class)
public class LabelTest {
	
	Label<String> lab;

	@Mock IdleAction ia;
	@Mock Action a1;
	@Mock Action a2;

	@Before
	public void setup() {

		when(a1.getLabel()).thenReturn("a");
		when(a2.getLabel()).thenReturn("a");
		lab = new Label<>(List.of("a"));
	}
	
	
	@Test
	public void testGetLabel() {
		assertEquals(List.of("a"), lab.getContent());
	}
	
	@Test
	public void testMatchTrue() {
		Assert.assertTrue(lab.match(new Label<>(List.of("a"))));
	}
	

	@Test
	public void testMatchFalse() {
		Assert.assertFalse(lab.match(new Label<>(List.of("b"))));
	}


    @Test
    public void testGetActionAllIdles() {
        Label<Action> lab = new Label<>(List.of(ia,ia));
        assertThrows(IllegalArgumentException.class, () -> lab.getAction());
    }

	@Test
	public void testGetActionNoActions() {
		assertThrows(IllegalArgumentException.class, () -> lab.getAction());
	}

	@Test
    public void testGetActionDifferentActions() {
        when(a2.getLabel()).thenReturn("b");
		Label<Action> lab = new Label<>(List.of(a1,ia,a2));
        assertThrows(IllegalArgumentException.class, () -> lab.getAction());
    }

    @Test
    public void testGetAction() {
		Label<Action> lab = new Label<>(List.of(a1,a2));
        assertEquals(a1,lab.getAction());
    }

	@Test
	public void testHashCode() {
		assertEquals(lab.hashCode(), new Label<>(List.of("a")).hashCode());
	}

	@Test
	public void testHashCodeFalse() {
		Assert.assertNotEquals(lab.hashCode(), new Label<>(List.of("b")).hashCode());
	}

	@Test
	public void testGetRank() {
		assertEquals(1, lab.getRank().intValue());
	}

	@Test
	public void testGetRank2() {
		Label<String> l = new Label<>(List.of("a","b"));	
		assertEquals(2, l.getRank().intValue());
	}
	
	@Test
	public void testToString() {
		assertEquals(List.of("a").toString(), lab.toString());
	}
	
	@Test
	public void equalsSameTrue() {
		assertEquals(lab,lab);
	}
	
	@Test
	public void equalsTwoInstancesTrue() {

		assertEquals(lab, new Label<>(List.of("a")));
	}
	
	@Test
	public void equalsNullFalse() {
		Assert.assertNotEquals(lab,null);
	}
	
	@Test
	public void equalsClassFalse() {
		Assert.assertNotEquals(lab,List.of("b"));
	}
	
	@Test
	public void equalsFalse() {
		Assert.assertNotEquals(lab, new Label<>(List.of("b")));
	}
	
	@Test
	public void constructorExceptionNull() {
		assertThrows(IllegalArgumentException.class, () -> new Label<String>(null));
	}
	
	@Test
	public void constructorExceptionEmpty() {
		List<String> list = List.of();
		assertThrows(IllegalArgumentException.class, () -> new Label<>(list));
	}
}
