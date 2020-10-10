package MSCA;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;

/**
 * Utilities for MSCA
 * 
 * TODO remove this class and use java.util library
 * 
 * @author Davide Basile
 *
 */
public class MSCAUtils 
{
	public static <T> T[] removeTailsNull(T[] q, int length, T[] type)
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

	public static <T> boolean contains(T q, T[] listq, int listlength) //only used in projection
	{
		if (listq==null||q==null)
			return false;
		return Arrays.asList(listq)
				.subList(0, listlength)
				.indexOf(q)!=-1;
	}
	
	//from now onward are duplicate methods for primitive types
	//TODO remove them and use collections

	public static int[] removeTailsNull(int[] q,int length)
	{
		int[] r=new int[length];
		for (int i=0;i<length;i++)
			r[i]=q[i];
		return r;
	}

	public static float[] removeTailsNull(float[] q,int length)
	{
		float[] r=new float[length];
		for (int i=0;i<length;i++)
			r[i]=q[i];
		return r;
	}
	
	public static int getIndex(int[] q, int e)
	{
		for (int i=0;i<q.length;i++)
		{
			if (q[i]==e)
				return i;
		}
		return -1;
	}


}
