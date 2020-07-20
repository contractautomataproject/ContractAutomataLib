package FMCA;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.junit.Test;

/**
 * using for testing that code cleaning is correct
 * @author Davide
 *
 */

@SuppressWarnings("deprecation")
public class FMCAUtilTest {

	@Test
	public void removeHolesTest()
	{
		int[][] test = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}, {1,0,2} };
		assertEquals(FMCAUtilOld.removeHoles(test, 1),FMCAUtil.removeHoles(test));
	}
	
	
	@Test
	public void removeTailsNullTest()
	{
		int[][] test = new int[][] { {0,1,3}, {1,0,2}, {0,1,3}, {1,0,2}, null, null };
		assertEquals(FMCAUtilOld.removeTailsNull(test, 2),FMCAUtil.removeTailsNull(test,2));
	}
	
	@Test
	public void containsIntArTest()
	{
		int[][] test = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}, null, {1,0,4} };
		assertEquals(FMCAUtilOld.contains(new int[] {0,1,3}, test),FMCAUtil.contains(new int[] {0,1,3}, test));
	}

	@Test
	public void containsIntArTestNull()
	{
		int[][] test = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}, null, {1,0,4} };
		assertEquals(FMCAUtilOld.contains(null, test),FMCAUtil.contains(null, test));
	}
	
	@Test
	public void containsTest()
	{
		String[] test = new String[] {"0","1","3", null, "3"};
		assertEquals(FMCAUtilOld.contains("1", test),FMCAUtil.contains("1",test));
	}
	
	@Test
	public void containsNullTest()
	{
		String[] test = new String[] {"0","1","3", null, "3"};
		assertEquals(FMCAUtilOld.contains(null, test),FMCAUtil.contains(null,test));
	}
	
	
	@Test
	public void getIndexTest()
	{
		String[] test = new String[] {"0","1","3", null, "3"};
		assertEquals(FMCAUtilOld.getIndex(test, "1"),FMCAUtil.getIndex(test, "1"));
	}
	
	@Test
	public void getIndexNullTest()
	{
		String[] test = new String[] {"0","1","3", null, "3"};
		assertEquals(FMCAUtilOld.getIndex(test, null),FMCAUtil.getIndex(test, null));
	}
	
	@Test
	public void setDifferenceTest()
	{
		int[][] test = new int[][] { {0,1,3}, {1,0,2}, {0,1,3}, {1,0,4} };
		int[][] test2 = new int[][] { {0,1,3}, {1,0,2}, {0,1,3}};
		
		assertEquals(FMCAUtilOld.setDifference(test, test2),FMCAUtil.setDifference(test, test2));
	}
	
	@Test
	public void setDifferenceNullTest()
	{
		int[][] test = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}, null, {1,0,4} };
		int[][] test2 = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}};
		
		assertEquals(FMCAUtilOld.setDifference(test, test2),FMCAUtil.setDifference(test, test2));
	}

	
}

class FMCAUtilOld {
	public static int[][] removeHoles(int[][] l, int holes )
	{
		/**
		 * remove holes (null) in t
		 */
		int pointer=0;
		int[][] fin = new int[l.length-holes][];
		for (int ind=0;ind<l.length;ind++)
		{
			if (l[ind]!=null)
			{
				fin[pointer]=l[ind];
				pointer++;
			}
		}
		return fin;
	}
	public static int[][] removeTailsNull(int[][] q,int length)
	{
		int[][] r=new int[length][];
		for (int i=0;i<length;i++)
			r[i]=q[i];
		return r;
	}
	
	public static boolean contains(int[] q, int[][] listq)
	{
		if (q==null)
			return false;
		for (int i=0;i<listq.length;i++)
		{
			if (listq[i]!=null)
				if (Arrays.equals(q, listq[i]))
					return true;
		}
		return false;
	}

	public static int getIndex(String[] q, String e)
	{
		for (int i=0;i<q.length;i++)
		{
			if ((q[i]!=null) &&(q[i].equals(e)))
					return i;
		}
		return -1;
	}
	protected static int[][] setDifference(int[][] q1, int[][] q2)
	{
		int p=0;
		int[][] m= new int[q1.length][];
		for (int i=0;i<m.length;i++)
		{
			if (q1[i]!=null&&!contains(q1[i],q2)&&!contains(q1[i],m))
			{
				m[p]=q1[i];
				p++;
			}
		}
		m=removeTailsNull(m,p);
		return m;
	}
	
	protected static boolean contains(String q, String[] listq)
	{
		if (q==null)
			return false;
		for (int i=0;i<listq.length;i++)
		{
			if (listq[i]!=null)
				if (q.equals(listq[i]))
					return true;
		}
		return false;
	}
}

