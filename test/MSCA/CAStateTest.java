package MSCA;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import contractAutomata.CAState;


public class CAStateTest {
	
	@Test
	public void constructorTest() {
		List<CAState> l = new ArrayList<>();
		l.add(new CAState(new int[] {0,1,2},true,false));
		l.add(new CAState(new int[] {0,0},true,false));
		l.add(new CAState(new int[] {4,10},true,false));
		
		CAState test = new CAState(l);
		
		assertEquals(Arrays.equals(test.getState(), new int[] {0,1,2,0,0,4,10}),true);
		assertEquals(test.isInitial(),true);
		assertEquals(test.isFinalstate(),false);
	}
	
	@Test
	public void toStringTest() {
		CAState cs = new CAState(new int[] {0,1,2},true,false);
		System.out.println(cs.toString());
		assertEquals(cs.getStateL().toString(),Arrays.toString(cs.getState()));
	}

}
