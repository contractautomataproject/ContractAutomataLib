package family;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Objects;

/**
 * Utilities for Family and Product
 * 
 * TODO remove this class and use java.util library
 * 
 * @author Davide Basile
 *
 */
public class FamilyUtils 
{
	static int[] removeTailsNull(int[] q,int length)
	{
		int[] r=new int[length];
		for (int i=0;i<length;i++)
			r[i]=q[i];
		return r;
	}
	
	static <T> T[] removeTailsNull(T[] q, int length, T[] type)
	{
		return  Arrays.stream(q)
				.limit(length)
				.collect(toList())
				.toArray(type);
	}

	
	public static <T> boolean contains(T q, T[] listq)
	{
		if (q==null||listq==null) 
			return false;
		else if (q instanceof int[])
			return Arrays.stream(listq)
					.filter(x -> Arrays.equals((int[])q, (int[])x))
					.count()>0;
					else
						return Arrays.asList(listq)
								.indexOf(q)!=-1;
	}
	
	static <T> T[] setIntersection(T[] q1, T[] q2, T[] type)
	{
		if (q1==null || q2==null)
			return null;
		return Arrays.stream(q1)
				.filter(Objects::nonNull)
				.filter(x-> contains(x,q2))
				.collect(toList())
				.toArray(type);
	}

	/**
	 * @return  q1 / q2
	 */
	static <T> T[] setDifference(T[] q1, T[] q2, T[] type)
	{
		return Arrays.stream(q1)
				.distinct()
				.filter(Objects::nonNull)
				.filter(x -> !contains(x,q2))
				.collect(toList())
				.toArray(type);
	}

	static <T> int getIndex(T[] q, T e)
	{
		if (e==null||q==null)
			return -1;
		else
			return Arrays.asList(q)
					.indexOf(e);
	}
	
	static <T> T[] removeHoles(T[] l, T[] type)
	{
		if (l==null)
			return null;
		return (T[]) Arrays.stream(l)
				.filter(Objects::nonNull)
				.collect(toList())
				.toArray(type);
	}	
}

//END OF THE CLASS

//public static <T> T[] setUnion(T[] q1, T[] q2, T[] type)
//{
//	List<T> t= new ArrayList<T>(Arrays.asList(q1));
//	t.addAll(Arrays.asList(q2));
//	
//	return t.stream()
//			.filter(Objects::nonNull)
//			.distinct()
//			.collect(Collectors.toList())
//			.toArray(type);
//}


//public static <T> int indexContains(T q, T[] listq)
//{
//	if (q==null||listq==null) 
//		return -1;
//	return Arrays.asList(listq)
//				.indexOf(q);
//}


//public static <T> T[] removeDuplicates(T[] m, T[] type)
//{
//	if (m==null) 
//		return null;
//	
//	return 	Arrays.stream(m)
//			.filter(Objects::nonNull)
//			.distinct()
//			.collect(Collectors.toList())
//			.toArray(type);
//}


//	public static int indexContains(int[] q, int[][] listq)
//	{
//		for (int i=0;i<listq.length;i++)
//		{
//			if (Arrays.equals(q, listq[i]))
//					return i;
//		}
//		return -1;
//	}
//	public static boolean contains(int q, int[] listq,int listlength)
//	{
//		for (int i=0;i<listlength;i++)
//		{
//				if (q==listq[i])
//					return true;
//		}
//		return false;
//	}

//	/**
//	 * difficult to convert as generic, using 
//	 * Array.asList().stream()....map(Arrays::asList)
//	 * was giving List<List<int[]>> instead of List<List<Integer>>
//	 */
//	public static int[][] setIntersection(int[][] q1, int[][] q2)
//	{
//		int p=0;
//		int[][] m= new int[q1.length][];
//		for (int i=0;i<m.length;i++)
//		{
//			if (contains(q1[i],q2))
//			{
//				m[p]=q1[i];
//				p++;
//			}
//		}
//		m=removeTailsNull(m,p, new int[][] {});
//		return m;
//	}

/**
 * 	if (q1 instanceof int[][]) {
			return removeDuplicates(q1);
		}
		does not work
 */
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

//	/**
//	 * 
//	 *  streams distinct() does not use Arrays equals
//	 */
//	public static int[][] removeDuplicates(int[][] m)
//	{
//		//int removed=0;
//		for (int i=0;i<m.length;i++)
//		{
//			for (int j=i+1;j<m.length;j++)
//			{
//				if ((m[i]!=null)&&(m[j]!=null)&&(Arrays.equals(m[i],m[j])))
//				{
//					m[j]=null;
//		//			removed++;
//				}
//			}
//		}
//		m= removeHoles(m, new int[][] {}); 
//		return m;
//	}	



//public static int[] convertInt(Integer[] ar)
//{
//	return Arrays.stream(ar).mapToInt(Integer::intValue).toArray();
//}
//
//private static Integer[] convertInt(int[] ar)
//{
//	return Arrays.stream(ar).boxed().toArray(Integer[]::new);
//}
//
//public static double[] convertDouble(Double[] ar)
//{
//	return Arrays.stream(ar).mapToDouble(Double::doubleValue).toArray();
//}
//
//public static Double[] convertDouble(double[] ar)
//{
//	return Arrays.stream(ar).boxed().toArray(Double[]::new);
//}

/*	public static Product[] concat(Product[] q1, Product[] q2)
	{
		Product[] m= new Product[q1.length+q2.length];
		for (int i=0;i<m.length;i++)
		{
			if (i<q1.length)
				m[i]=q1[i];
			else
				m[i]=q2[i-q1.length];
		}
		return m;
	}*/