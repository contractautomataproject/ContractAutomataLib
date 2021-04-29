package familyTest;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

import contractAutomata.FMCA;
import contractAutomata.MSCA;
import contractAutomata.MSCAIO;
import contractAutomataTest.MSCATest;
import family.Product;

import static org.junit.Assert.assertEquals;
/*
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import org.junit.Test;
 */

/**
 * 
 * using for testing that code cleaning is correct
 * @author Davide
 *
 */
public class FMCATest {
	
			
	@Test
	public void constructorTest_Exception_nullArgument() {
		assertThatThrownBy(() -> new FMCA(null))
	    .isInstanceOf(IllegalArgumentException.class);
	}

	@Test
	public void getAut_test() throws ParserConfigurationException, SAXException, IOException 
	{
		String dir = System.getProperty("user.dir");
		MSCA a = MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(BusinessClientxHotelxEconomyClient).mxe");
		FMCA aut = new FMCA(a);	
		
		assertEquals(aut.getAut(),a);
	}
	
	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_product4858_transitions() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		FMCA aut = new FMCA(MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(BusinessClientxHotelxEconomyClient).mxe"));
		
		MSCA test= MSCAIO.parseXMLintoMSCA(dir+"/CAtest/Orc_(BusinessClientxHotelxEconomyClient).mxe");
		assertEquals(MSCATest.checkTransitions(aut.orchestration(new Product(new String[] {"card","sharedBathroom"}, new String[] {"cash"})),test),true);
	}
	
	@Test
	public void orcTestSCP2020_BusinessClientxHotelxEconomyClient_empty_transitions() throws NumberFormatException, IOException, ParserConfigurationException, SAXException
	{

		String dir = System.getProperty("user.dir");
		FMCA aut = new FMCA(MSCAIO.parseXMLintoMSCA(dir+"/CAtest/(BusinessClientxHotelxEconomyClient).mxe"));
		
		assertEquals(aut.orchestration(new Product(new String[] {"card","sharedBathroom"}, new String[] {"singleRoom"})),null);
	}	
}






//END OF THE CLASS

//old FMCA util 

//@Test
//public void testUnionRenaming()
//{
//	int[][] a1 = { {2,3}, {5,6}};
//	int[][] a2 = { {1,4}, {7,8}};
//	int[][] a3 = { {9}, {10}};
//
//	int[][] u = { {2,3,1,4,9}, {5,6,7,8,10}};
//
//	int[][] test =
//	Arrays.stream(a1)
//	.map(s->{return Arrays.stream(s)
//			.map(ar->ar+10)
//			.toArray();})
//	.toArray(int[][]::new);
//
//	//System.out.print(Arrays.deepToString(test));
//	assertEquals(u,test);
//
//}
//

	//	@Test
	//	public void removeHolesTest()
	//	{
	//		int[][] test = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}, {1,0,2} };
	//		assertEquals(FMCAUtilOld.removeHoles(test, 1),FMCAUtil.removeHoles(test, new int[][] {}));
	//	}
	//	
	//	@Test
	//	public void removeHolesStringTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		assertEquals(FMCAUtilOld.removeHoles(test, 1),FMCAUtil.removeHoles(test, new String[] {}));
	//	}
	//	
	//	@Test
	//	public void removeTailsNullTest()
	//	{
	//		int[][] test = new int[][] { {0,1,3}, {1,0,2}, {0,1,3}, {1,0,2}, null, null };
	//		assertEquals(FMCAUtilOld.removeTailsNull(test, 2),FMCAUtil.removeTailsNull(test,2, new int[][] {}));
	//	}
	//	
	//	@Test
	//	public void containsIntArTest()
	//	{
	//		int[][] test = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}, null, {1,0,4} };
	//		assertEquals(FMCAUtilOld.contains(new int[] {0,1,3}, test),FMCAUtil.contains(new int[] {0,1,3}, test));
	//	}
	//
	//	@Test
	//	public void containsIntArTestNull()
	//	{
	//		int[][] test = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}, null, {1,0,4} };
	//		assertEquals(FMCAUtilOld.contains(null, test),FMCAUtil.contains(null, test));
	//	}
	//	
	//	@Test
	//	public void containsTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		assertEquals(FMCAUtilOld.contains("1", test),FMCAUtil.contains("1",test));
	//	}
	//	
	//	@Test
	//	public void containsFalseTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		assertEquals(FMCAUtilOld.contains("5", test),FMCAUtil.contains("5",test));
	//	}
	//	
	//	@Test
	//	public void containsNullTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		assertEquals(FMCAUtilOld.contains(null, test),FMCAUtil.contains(null,test));
	//	}
	//	
	//	@Test
	//	public void containsLTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3","5","0"};
	//		assertEquals(FMCAUtilOld.contains("1", test,5),FMCAUtil.contains("1",test,5));
	//	}
	//	
	//	@Test
	//	public void containsLFalseTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3","5","0"};
	//		assertEquals(FMCAUtilOld.contains("5", test,5),FMCAUtil.contains("5",test,5));
	//	}
	//	
	//	@Test
	//	public void containsLNullTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3","5","0"};
	//		assertEquals(FMCAUtilOld.contains(null, test,5),FMCAUtil.contains(null,test,5));
	//	}
	//	
	//	@Test
	//	public void getIndexTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		assertEquals(FMCAUtilOld.getIndex(test, "1"),FMCAUtil.getIndex(test, "1"));
	//	}
	//	
	//	@Test
	//	public void getIndexNullTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		assertEquals(FMCAUtilOld.getIndex(test, null),FMCAUtil.getIndex(test, null));
	//	}
	//	
	//	@Test
	//	public void setDifferenceTest()
	//	{
	//		int[][] test = new int[][] { {0,1,3}, {1,0,2}, {0,1,3}, {1,0,4} };
	//		int[][] test2 = new int[][] { {0,1,3}, {1,0,2}, {0,1,3}};
	//		
	//		assertEquals(FMCAUtilOld.setDifference(test, test2),FMCAUtil.setDifference(test, test2, new int[][] {}));
	//	}
	//	
	//	@Test
	//	public void setDifferenceNullTest()
	//	{
	//		int[][] test = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}, null, {1,0,4} };
	//		int[][] test2 = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}};
	//		
	//		assertEquals(FMCAUtilOld.setDifference(test, test2),FMCAUtil.setDifference(test, test2, new int[][] {}));
	//	}
	//	
	//	
	//	@Test
	//	public void removeDuplicatesStringTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		//assertEquals(FMCAUtilOld.removeDuplicates(test),FMCAUtil.removeDuplicates(test));
	//		assertEquals(new String[] {"0","1","3"},FMCAUtil.removeDuplicates(test, new String[] {}));
	//
	//	}
	//	
	//	@Test
	//	public void setUnionTest()
	//	{
	//		int[][] test = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}, null, {1,0,4} };
	//		int[][] test2 = new int[][] { {0,1,3}, {1,0,2}, null, {0,1,3}};
	//	
	//		assertEquals(FMCAUtilOld.setUnion(test, test2),FMCAUtil.setUnion(test, test2));
	//	}
	//	
	//	@Test
	//	public void setUnion2Test()
	//	{
	//		int[][] test = new int[][] { {0,1,3}  };
	//		int[][] test2 = new int[][] { {0,1,3} };
	//	
	//		assertEquals(FMCAUtilOld.setUnion(test, test2),FMCAUtil.setUnion(test, test2));
	//	}
	//	
	//
	//	@Test
	//	public void setUnionStringTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		String[] test2 = new String[] {"0", null, "3"};
	//		
	//		assertEquals(new String[] {"0","1","3"},FMCAUtil.setUnion(test, test2, new String[] {}));
	//	}
	//	
	//	@Test
	//	public void setIntersectionTest()
	//	{
	//		String[] test = new String[] {"0","1","3", null, "3"};
	//		String[] test2 = new String[] {"0", null, "3"};
	//		
	//		assertEquals(FMCAUtilOld.setIntersection(test, test2),FMCAUtil.setIntersection(test, test2, new String[] {}));
	//	}
	//	
	//	@Test
	//	public void maxTest() {
	//		int[] test = new int[] {0, 1, 2 ,3, 2, 1};
	//		
	//		//taken from ProductFrame.java
	//		int columns = Collections.max(Arrays.stream(test).boxed().collect(Collectors.toList()));  // FMCAUtil.max(deleng);
	//		assertEquals(columns, FMCAUtilOld.max(test));
	//	}
	//}
	//
	//class FMCAUtilOld {
	//	public static int[][] removeHoles(int[][] l, int holes )
	//	{
	//		/**
	//		 * remove holes (null) in t
	//		 */
	//		int pointer=0;
	//		int[][] fin = new int[l.length-holes][];
	//		for (int ind=0;ind<l.length;ind++)
	//		{
	//			if (l[ind]!=null)
	//			{
	//				fin[pointer]=l[ind];
	//				pointer++;
	//			}
	//		}
	//		return fin;
	//	}
	//	
	//	protected static String[] removeHoles(String[] l, int holes )
	//	{
	//		/**
	//		 * remove holes (null) in t
	//		 */
	//		int pointer=0;
	//		String[] fin = new String[l.length-holes];
	//		for (int ind=0;ind<l.length;ind++)
	//		{
	//			if (l[ind]!=null)
	//			{
	//				fin[pointer]=l[ind];
	//				pointer++;
	//			}
	//		}
	//		return fin;
	//	}
	//	
	//	public static int[][] removeTailsNull(int[][] q,int length)
	//	{
	//		int[][] r=new int[length][];
	//		for (int i=0;i<length;i++)
	//			r[i]=q[i];
	//		return r;
	//	}
	//	
	//	public static String[] removeTailsNull(String[] q,int length)
	//	{
	//		String[] r=new String[length];
	//		for (int i=0;i<length;i++)
	//			r[i]=q[i];
	//		return r;
	//	}
	//	
	//	public static boolean contains(int[] q, int[][] listq)
	//	{
	//		if (q==null)
	//			return false;
	//		for (int i=0;i<listq.length;i++)
	//		{
	//			if (listq[i]!=null)
	//				if (Arrays.equals(q, listq[i]))
	//					return true;
	//		}
	//		return false;
	//	}
	//
	//	public static int getIndex(String[] q, String e)
	//	{
	//		for (int i=0;i<q.length;i++)
	//		{
	//			if ((q[i]!=null) &&(q[i].equals(e)))
	//					return i;
	//		}
	//		return -1;
	//	}
	//	protected static int[][] setDifference(int[][] q1, int[][] q2)
	//	{
	//		int p=0;
	//		int[][] m= new int[q1.length][];
	//		for (int i=0;i<m.length;i++)
	//		{
	//			if (q1[i]!=null&&!contains(q1[i],q2)&&!contains(q1[i],m))
	//			{
	//				m[p]=q1[i];
	//				p++;
	//			}
	//		}
	//		m=removeTailsNull(m,p);
	//		return m;
	//	}
	//	
	//	protected static boolean contains(String q, String[] listq)
	//	{
	//		if (q==null)
	//			return false;
	//		for (int i=0;i<listq.length;i++)
	//		{
	//			if (listq[i]!=null)
	//				if (q.equals(listq[i]))
	//					return true;
	//		}
	//		return false;
	//	}
	//	
	//	
	//	//it does not remove duplicates null, but 
	//	//remove holes remove nulls so there is a error there
	//	public static String[] removeDuplicates(String[] m)
	//	{
	//		int removed=0;
	//		for (int i=0;i<m.length;i++)
	//		{
	//			for (int j=i+1;j<m.length;j++)
	//			{
	//				if ((m[i]!=null)&&(m[j]!=null)&&(m[i].equals(m[j])))
	//				{
	//					m[j]=null;
	//		 			removed++;
	//				}
	//			}
	//		}
	//		m=  removeHoles(m,removed);
	//		return m;		
	//	}
	//	
	//	public static String[] setIntersection(String[] q1, String[] q2)
	//	{
	//		int p=0;
	//		String[] m= new String[q1.length];
	//		for (int i=0;i<m.length;i++)
	//		{
	//			if (contains(q1[i],q2))
	//			{
	//				m[p]=q1[i];
	//				p++;
	//			}
	//		}
	//		m=FMCAUtilOld.removeTailsNull(m,p);
	//		return m;
	//	}
	//	
	//	protected static boolean contains(String t, String[] listq, int listlength)
	//	{
	//		if (t==null)
	//			return false;
	//		for (int i=0;i<listlength;i++)
	//		{
	//			if (t.equals(listq[i]))
	//					return true;
	//		}
	//		return false;
	//	}
	//	
	//	public static int[][] setUnion(int[][] q1, int[][] q2)
	//	{
	//		int[][] m= new int[q1.length+q2.length][];
	//		for (int i=0;i<m.length;i++)
	//		{
	//			if (i<q1.length)
	//				m[i]=q1[i];
	//			else
	//				m[i]=q2[i-q1.length];
	//		}
	//		m=FMCAUtil.removeDuplicates(m);
	//		return m;
	//	}
	//	
	//	public static int max(int[] n)
	//	{
	//		int max=0;
	//		for (int i=0;i<n.length;i++)
	//			if(n[i]>max)
	//				max=n[i];
	//		return max;
	//	}



