package contractAutomata;

/**
 * Utilities for MSCA
 * 
 * TODO remove this class and use java.util library
 * This class contains utility methods to be removed from the package 
 * when the representation of principals final states in MSCA is amended
 * 
 * @author Davide Basile
 *
 */
public class MSCAUtils 
{

	
//	public static <T> boolean contains(T q, T[] listq, int listlength) //only used in projection
//	{
//		if (listq==null||q==null)
//			return false;
//		return Arrays.asList(listq)
//				.subList(0, listlength)
//				.indexOf(q)!=-1;
//	}
	
	//from now onward are duplicate methods for primitive types
	//TODO remove them and use collections

	public static int[] removeTailsNull(int[] q,int length)
	{
		int[] r=new int[length];
		for (int i=0;i<length;i++)
			r[i]=q[i];
		return r;
	}
}
