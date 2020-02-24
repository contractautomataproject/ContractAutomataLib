package FMCA;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import CA.CAState;
import CA.CAUtil;

/**
 * Utilities for FMCA: product, aproduct
 * 
 * @author Davide Basile
 *
 */
public class FMCAUtil extends CAUtil
{

	static boolean debug = true;
	
	public static FMCA composition(FMCA[] aut)
	{
		/**
		 * 
		 * array of states of product 
		 * start from initial state (composed)
		 * {
		 * project on states of principals
		 * extract all transitions of principals
		 * add transitions to FMCAT (check match)
		 * load all target states in the array of states to be visited
		 * (use CAState so that you have pointers, through reachable you avoid visiting 
		 * states already visited)
		 * terminates when no more states are added
		 * }
		 * 
		 */
		return null;
	}
	public static float furthestNodesX(FMCA[] aut)
	{
		float max=0;
		for (int i=0;i<aut.length;i++)
		{
			float x=aut[i].furthestNodeX();
			if (max<x)
				max=x;
		}
		return max;
	}
	
	/**
	 * 
	 * @param aut
	 * @return compute the union of the FMCA in aut
	 */
	public static FMCA union(FMCA[] aut)
	{
		if (aut.length==0)
			return null;
		int upperbound=100; //TODO upperbound check
		int rank=aut[0].getRank(); //the aut must have all the same rank
		
		float fur=FMCAUtil.furthestNodesX(aut);
		
		for (int i=0;i<aut.length;i++)
		{
			int[][] fs=aut[i].getFinalStatesCA();
			int[][] newfs=new int[fs.length][];
			for (int j=0;j<newfs.length;j++)
			{
				newfs[j]=Arrays.copyOf(fs[j], fs[j].length);
			}
			for (int j=0;j<newfs.length;j++)
			{
				for (int z=0;z<newfs[j].length;z++)
					newfs[j][z]+=upperbound*(i+1);
			}
			aut[i].setFinalStatesCA(newfs);
		}
		for (int i=0;i<aut.length;i++)
		{
			if (aut[i].getRank()!=rank)
				return null;
			
			//renaming states of operands
			CAState initial=aut[i].getInitialCA().clone();
			for (int z=0;z<initial.getState().length;z++)
				initial.getState()[z]=initial.getState()[z]+upperbound*(i+1);
			aut[i].setInitialCA(initial);
			FMCATransition[] t=aut[i].getTransition();
			for (int j=0;j<t.length;j++)
			{
				CAState source=t[j].getSourceP().clone();
				CAState target=t[j].getTargetP().clone();
				for (int z=0;z<source.getState().length;z++)
				{
					source.getState()[z] = source.getState()[z] + upperbound*(i+1);
					target.getState()[z] = target.getState()[z] + upperbound*(i+1);
				}
				t[j].setSourceP(source);
				t[j].setTargetP(target);
			}
			
			//repositioning states and renaming
			CAState[] fst=aut[i].getState();
			CAState[] newfst=new CAState[fst.length];
			for (int j=0;j<fst.length;j++)
			{
				int[] value=Arrays.copyOf(fst[j].getState(),fst[j].getState().length);
				for (int z=0;z<value.length;z++)
					value[z]=value[z] + upperbound*(i+1); //rename state
				newfst[j]=new CAState(value, fst[j].getX()+fur*(i)+25*i, fst[j].getY()+50, //repositinioning
						fst[j].isInitial(),fst[j].isFinalstate());			
			}
			aut[i].setState(newfst);
		}
	
		int[] initial = new int[rank]; //special initial state
		String[] label = new String[rank];
		label[0]="!dummy";				
		for (int i=0;i<rank;i++)
		{
			initial[i]=0;
			if (i!=0)
				label[i]="-";
		}
		CAState finitial = new CAState(initial,(float)((aut.length)*fur)/2,0,true,false);
		//dummy transitions to initial states
		FMCATransition[] t=new FMCATransition[aut.length];
		for (int i=0;i<t.length;i++)
		{
			t[i]=new FMCATransition(finitial,label,aut[i].getInitialCA(),FMCATransition.action.PERMITTED); 
		}
		int trlength=t.length;
		FMCATransition[][] tr=new FMCATransition[aut.length][];
		for (int i=0;i<aut.length;i++)
		{
			tr[i]=aut[i].getTransition();
			trlength+=tr[i].length;
		}
		FMCATransition[] uniontr=new FMCATransition[trlength];//union of all transitions
		int count=0;
		for (int i=0;i<t.length;i++)
		{
			uniontr[count]=t[i];
			count++;
		}
		for (int i=0;i<aut.length;i++)
		{
			for (int j=0;j<tr[i].length;j++)
			{
				uniontr[count]=tr[i][j];
				count++;
			}
		}
		
		int[] states = new int[rank];
		int[] finalstateslength = new int[rank];
		for (int i=0;i<rank;i++)
		{
			states[i]=0;
			finalstateslength[i]=0; //initialise
		}
		int numoffstate=0; //the overall sum of fmcastates of all operands
		for (int i=0;i<aut.length;i++)
		{
			numoffstate+=aut[i].getState().length;
			int[][] fs = aut[i].getFinalStatesCA();
			for (int j=0;j<rank;j++)
			{
				states[j]+= aut[i].getStatesCA()[j]; //sum of states		
				finalstateslength[j] += fs[j].length; //number of final states of operands
			}
		}

		int[][] finalstates = new int[rank][];
		int[] finalstatescount= new int[rank];
		for (int i=0;i<finalstates.length;i++)
		{
			finalstatescount[i]=0;
			finalstates[i]=new int[finalstateslength[i]];
		}
		for (int i=0;i<aut.length;i++)
		{
			int[][] fs = aut[i].getFinalStatesCA();
			for (int j=0;j<rank;j++)
			{		
				for (int z=0;z<fs[j].length;z++)
				{
					finalstates[j][finalstatescount[j]]=fs[j][z];//TODO check
					finalstatescount[j]++;
				}
			}
		}
		
		// copying states of operands
		CAState[] ufst = new CAState[numoffstate+1];
		int countfs=0;
		for (int i=0;i<aut.length;i++)
		{
			CAState[] so = aut[i].getState();
			for (int j=0;j<so.length;j++)
			{
				ufst[countfs]=so[j];
				countfs++;
			}
		}
		ufst[countfs]=finitial;
		/*int[][] finalstates = new int[rank][];
		for (int i=0;i<rank;i++)
		{
			int[][] fs=aut[i].getFinalStatesCA();
			
		}*/
	
		return new FMCA(rank, finitial, states, finalstates, uniontr, ufst);
	}

	/**
	 * Similar to CATest but with only the operations implemented for FMCA
	 * 
	 * Testing the CA
	 */
	public static void FMCATest()
	{
		try{
			InputStreamReader reader = new InputStreamReader (System.in);
			BufferedReader myInput = new BufferedReader (reader);
			FMCA prod;
			FMCA[] aut=null;
			FMCA a;
			String s="";
			do
			{
				System.out.println("Select an operation");
				System.out.println("1 : product \n2 : projection \n3 : aproduct \n9 : most permissive controller \n15 : exit ");
				s = myInput.readLine();
				if(!s.equals("15"))
				{
					System.out.println("Reset stored automaton...");
					aut= load();
				}
				switch (s)
				{
				case "1":
					System.out.println("Computing the product automaton ... ");
					prod = (FMCA) CAUtil.composition(aut);
					prod.print();
			        prod.printToFile("");
					break;

				case "2":
					System.out.println("Computing the projection of the last FMCA loaded, insert the index of the principal:");
					s=myInput.readLine();
					//int ind = Integer.parseInt(s);
					//FMCA projected = aut[aut.length-1].proj(ind);
					//projected.print();
					//projected.printToFile();
					break;

				case "3":
					System.out.println("Computing the associative product automaton ... ");
					prod = (FMCA) FMCAUtil.aproduct(aut);
					prod.print();
					prod.printToFile("");
					break;

				
				case "9":
					System.out.println("The most permissive controller of modal agreement for the last FMCA loaded is");
					a = aut[aut.length-1];
					//TODO fix
					String[] R={};
					String[] F={};
					Product p=new Product(R,F);
					FMCA mpc = a.mpc(p);
					if (mpc!=null)
					{
						mpc.print();
						mpc.printToFile("");
					}
					break;								}				
			}while(!s.equals("15"));

		}catch(Exception e){e.printStackTrace();}
	} 
	
	/**
	 * 
	 * identical to the method of CAUtil,  I just substituted CA with FMCA and CATransition with FMCATransition,
	 * TODO remove  
	 * @return
	 */
	protected static FMCA[] load()
	{
		try
		{
			FMCA[] a = new FMCA[10];
			int i=0;
			FMCA automa;
			String s="";
			InputStreamReader reader = new InputStreamReader (System.in);
			BufferedReader myInput = new BufferedReader (reader);
			while (!s.equals("no")&&i<10)
			{
				System.out.println("Do you want to create/load other CA? (yes/no, default yes)");
				s = myInput.readLine();
				//s = "yes";
				if(!s.equals("no"))
				{
					do{
						System.out.println("Insert the name of the automaton (without .data extension) to load or leave empty for create a new one");
						s = myInput.readLine();
				        if (!s.isEmpty())
				        {
				        	automa = FMCA.load(s);
				        }
				        else
				        	{
					        automa = new FMCA();
				        	}
					} while (automa==null);
			        automa.print();
			        a[i] = automa;
			        //s="no";
			        i++;
				}
			}
			FMCA[] aut;
			if (i<10)
			{
				aut=new FMCA[i];
				for (int ind=0;ind<i;ind++)
					aut[ind]=a[ind];
			}
			else
				aut=a;
			return aut;
		}catch(Exception e){e.printStackTrace();return null;}
	}
	
	// from now on all methods are utilities not available in CAUtil
	
	protected static int[] getArray(String arr)
	{
		 String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");

		 int[] results = new int[items.length];

		 for (int ii = 0; ii < items.length; ii++) {
		    // try {
		         results[ii] = Integer.parseInt(items[ii]);
		     /*} catch (NumberFormatException nfe) {
		         nfe.printStackTrace();
		     };*/
		 }
		 return results;
	}
	
	protected static String[] getArrayString(String arr) throws Exception
	{
		 String[] items = arr.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll("\\s", "").split(",");
		 for (int i=0;i<items.length;i++)
		 {
			 if (!(items[i].startsWith("!")||items[i].startsWith("?")||items[i].startsWith("-")))
				 throw new Exception();
		 }
		 /*int[] results = new int[items.length];

		 for (int ii = 0; ii < items.length; ii++) {
		     try {
		         results[ii] = Integer.parseInt(items[ii]);
		     } catch (NumberFormatException nfe) {
		         nfe.printStackTrace();
		     };
		 }*/
		 return items;
	}
	
	protected static boolean contains(int[] q, int[][] listq)
	{
		for (int i=0;i<listq.length;i++)
		{
			if (listq[i]!=null)
				if (Arrays.equals(q, listq[i]))
					return true;
		}
		return false;
	}
	
	protected static boolean contains(int q, int[] listq,int listlength)
	{
		for (int i=0;i<listlength;i++)
		{
				if (q==listq[i])
					return true;
		}
		return false;
	}
	
	protected static boolean contains(CAState q, CAState[] listq)
	{
		for (int i=0;i<listq.length;i++)
		{
			if (listq[i]!=null)
				if (q.equals(listq[i])) 
					return true;
		}
		return false;
	}
	
	protected static boolean contains(String q, String[] listq)
	{
		for (int i=0;i<listq.length;i++)
		{
			if (listq[i]!=null)
				if (q.equals(listq[i]))
					return true;
		}
		return false;
	}
	
	protected static int getIndex(int[] q, int e)
	{
		for (int i=0;i<q.length;i++)
		{
			if (q[i]==e)
					return i;
		}
		return -1;
	}
	
	protected static int getIndex(String[] q, String e)
	{
		for (int i=0;i<q.length;i++)
		{
			if ((q[i]!=null) &&(q[i].equals(e)))
					return i;
		}
		return -1;
	}
	protected static int indexContains(int[] q, int[][] listq)
	{
		for (int i=0;i<listq.length;i++)
		{
			if (Arrays.equals(q, listq[i]))
					return i;
		}
		return -1;
	}
	protected static boolean contains(FMCATransition t, FMCATransition[] listq)
	{
		for (int i=0;i<listq.length;i++)
		{
			if (t.equals(listq[i]))
					return true;
		}
		return false;
	}
	protected static boolean contains(FMCATransition t, FMCATransition[] listq, int listlength)
	{
		for (int i=0;i<listlength;i++)
		{
			if (t.equals(listq[i]))
					return true;
		}
		return false;
	}
	public static boolean contains(CAState t, CAState[] listq, int listlength)
	{
		for (int i=0;i<listlength;i++)
		{
			if (t.equals(listq[i]))
					return true;
		}
		return false;
	}
	protected static int[][] setUnion(int[][] q1, int[][] q2)
	{
		int[][] m= new int[q1.length+q2.length][];
		for (int i=0;i<m.length;i++)
		{
			if (i<q1.length)
				m[i]=q1[i];
			else
				m[i]=q2[i-q1.length];
		}
		m=removeDuplicates(m);
		return m;
	}
	protected static CAState[] setUnion(CAState[] q1, CAState[] q2)
	{
		CAState[] m= new CAState[q1.length+q2.length];
		for (int i=0;i<m.length;i++)
		{
			if (i<q1.length)
				m[i]=q1[i];
			else
				m[i]=q2[i-q1.length];
		}
		m=removeDuplicates(m);
		return m;
	}
	protected static Product[] concat(Product[] q1, Product[] q2)
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
	}
	protected static int[][] setDifference(int[][] q1, int[][] q2)
	{
		int p=0;
		int[][] m= new int[q1.length][];
		for (int i=0;i<m.length;i++)
		{
			if (!contains(q1[i],q2))
			{
				m[p]=q1[i];
				p++;
			}
		}
		m=removeTailsNull(m,p);
		return m;
	}
	protected static int[][] setIntersection(int[][] q1, int[][] q2)
	{
		int p=0;
		int[][] m= new int[q1.length][];
		for (int i=0;i<m.length;i++)
		{
			if (contains(q1[i],q2))
			{
				m[p]=q1[i];
				p++;
			}
		}
		m=removeTailsNull(m,p);
		return m;
	}
	protected static String[] setDifference(String[] q1, String[] q2)
	{
		int p=0;
		String[] m= new String[q1.length];
		for (int i=0;i<m.length;i++)
		{
			if (!contains(q1[i],q2))
			{
				m[p]=q1[i];
				p++;
			}
		}
		m=removeTailsNull(m,p);
		return m;
	}
	protected static String[] setIntersection(String[] q1, String[] q2)
	{
		int p=0;
		String[] m= new String[q1.length];
		for (int i=0;i<m.length;i++)
		{
			if (contains(q1[i],q2))
			{
				m[p]=q1[i];
				p++;
			}
		}
		m=removeTailsNull(m,p);
		return m;
	}
	public static int[][] removeDuplicates(int[][] m)
	{
		int removed=0;
		for (int i=0;i<m.length;i++)
		{
			for (int j=i+1;j<m.length;j++)
			{
				if ((m[i]!=null)&&(m[j]!=null)&&(Arrays.equals(m[i],m[j])))
				{
					m[j]=null;
					removed++;
				}
			}
		}
		m= (int[][])removeHoles(m,removed);
		return m;
			
	}
	protected static CAState[] removeDuplicates(CAState[] m)
	{
		int removed=0;
		for (int i=0;i<m.length;i++)
		{
			for (int j=i+1;j<m.length;j++)
			{
				if ((m[i]!=null)&&(m[j]!=null)&&(m[i].equals(m[j])))
				{
					m[j]=null;
					removed++;
				}
			}
		}
		m= (CAState[])removeHoles(m,removed);
		return m;
			
	}
	protected static String[] removeDuplicates(String[] m)
	{
		int removed=0;
		for (int i=0;i<m.length;i++)
		{
			for (int j=i+1;j<m.length;j++)
			{
				if ((m[i]!=null)&&(m[j]!=null)&&(m[i].equals(m[j])))
				{
					m[j]=null;
					removed++;
				}
			}
		}
		m= (String[])removeHoles(m,removed);
		return m;
			
	}
	protected static CAState[] removeTailsNull(CAState[] q,int length)
	{
		CAState[] r=new CAState[length];
		for (int i=0;i<length;i++)
			r[i]=q[i];
		return r;
	}
	protected static int[] removeTailsNull(int[] q,int length)
	{
		int[] r=new int[length];
		for (int i=0;i<length;i++)
			r[i]=q[i];
		return r;
	}
	protected static float[] removeTailsNull(float[] q,int length)
	{
		float[] r=new float[length];
		for (int i=0;i<length;i++)
			r[i]=q[i];
		return r;
	}
	protected static int[][] removeTailsNull(int[][] q,int length)
	{
		int[][] r=new int[length][];
		for (int i=0;i<length;i++)
			r[i]=q[i];
		return r;
	}
	public static String[] removeTailsNull(String[] q,int length)
	{
		String[] r=new String[length];
		for (int i=0;i<length;i++)
			r[i]=q[i];
		return r;
	}
	protected static FMCATransition[] removeTailsNull(FMCATransition[] q,int length)
	{
		FMCATransition[] r=new FMCATransition[length];
		for (int i=0;i<length;i++)
			r[i]=q[i];
		return r;
	}
	protected static Product[] removeTailsNull(Product[] q,int length)
	{
		Product[] r=new Product[length];
		for (int i=0;i<length;i++)
			r[i]=q[i];
		return r;
	}
	
	public static <T> T[] removeTailsNull(T[] q, T[] r)
	{
		for (int i=0;i<r.length;i++)
			r[i]=q[i];
		return r;
	}
	
	protected static int[][] removeHoles(int[][] l, int holes )
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
	protected static CAState[] removeHoles(CAState[] l, int holes )
	{
		/**
		 * remove holes (null) in t
		 */
		int pointer=0;
		CAState[] fin = new CAState[l.length-holes];
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
	protected static String[] removeHoles(String[] l, int holes )
	{
		/**
		 * remove holes (null) in t
		 */
		int pointer=0;
		String[] fin = new String[l.length-holes];
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
	protected static FMCATransition[] removeHoles(FMCATransition[] l, int holes )
	{
		/**
		 * remove holes (null) in t
		 */
		int pointer=0;
		FMCATransition[] fin = new FMCATransition[l.length-holes];
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
	public static int max(int[] n)
	{
		int max=0;
		for (int i=0;i<n.length;i++)
			if(n[i]>max)
				max=n[i];
		return max;
		
	}
}
