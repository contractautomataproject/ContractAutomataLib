package MSCA;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

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
		
		assertEquals(hasSameBasicStateLabelsOf(test, new int[] {0,1,2,0,0,4,10}),true);
		assertEquals(test.isInitial(),true);
		assertEquals(test.isFinalstate(),false);
	}
	
	private static boolean hasSameBasicStateLabelsOf(CAState cs, int[] s) {
		if (s.length!=cs.getState().size())
				return false;
		return IntStream.range(0, cs.getState().size())
		.allMatch(i->Integer.parseInt(cs.getState().get(i).getLabel())==s[i]);
	}
	
//	@Test
//	public void toStringTest() {
//		CAState cs = new CAState(new int[] {0,1,2},true,false);
//		System.out.println(cs.toString());
//		assertEquals(cs.getStateL().toString(),Arrays.toString(getArrayState(cs)));
//	}
	
	
	@Test
	public void hasSameLabels() {
		List<CAState> l = new ArrayList<>();
		l.add(new CAState(new int[] {0,1,2},true,false));
		l.add(new CAState(new int[] {0,1,2},false,false));
		
		assert(l.get(0).hasSameBasicStateLabelsOf(l.get(1)));
	}

}
