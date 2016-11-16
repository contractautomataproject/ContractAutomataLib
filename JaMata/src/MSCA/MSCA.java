package MSCA;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Scanner;

import MSCA.MSCA;
import MSCA.MSCATransition;
import MSCA.MSCAUtil;
import CA.CA;
import CA.CATransition;
import FSA.Transition;


/** 
 * Class implementing a Modal Service Contract Automaton and its functionalities
 * The class is under construction, some functionalities are not yet updated
 * 
 * 
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class MSCA  extends CA implements java.io.Serializable
{
	/*
	private int rank;
	private int[] initial;
	private int[] states;
	private int[][] finalstates; */
//	private static String message = "*** CA ***\n The alphabet is represented by integers: " +
//			" negative numbers are request actions, positive are offer actions, 0 stands for idle\n";
	
	/**
	 * Invoke the super constructor and take in input the added new parameters of the automaton
	 */
	public MSCA()
	{
		super();
	}
	
	public MSCA(int rank, int[] initial, int[] states, int[][] finalstates,MSCATransition[] maytra)
	{
		super(rank,initial,states,finalstates,maytra);
	}
	
	
	
	
	/**
	 * load a MSCA described in a text file, compared to CA it also loads the must transitions
	 * @param the name of the file
	 * @return	the CA loaded
	 */
	public static MSCA load(String fileName)
	{
		try
		{
			// Open the file
			FileInputStream fstream = new FileInputStream(fileName+".data");
			BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
			String strLine;
			int rank=0;
			int[] initial = new int[1];
			int[] states = new int[1];
			int[][] fin = new int[1][];
			MSCATransition[] t = new MSCATransition[1];
		//	MSCATransition[] mustt = new MSCATransition[1];
			int pointert=0;
	//		int pointermust=0;
			
			//Read File Line By Line
			while ((strLine = br.readLine()) != null)   
			{
				  // Print the content on the console
				  if (strLine.length()>0)
				  {
					  switch(strLine.substring(0,1))
					  {
						  case "R":  //Rank Line
						  {
							  Scanner s = new Scanner(strLine);
							  s.useDelimiter("");
							  while (s.hasNext())
							  {
								  if (s.hasNextInt())
								  {
									  rank = s.nextInt();
								  }
								  else
								  {
									  s.next();
								  }
							  }
							  initial = new int[rank];
							  states = new int[rank];
							  fin = new int[rank][];
							  s.close();
							  break;
						  }
						  case "N":	//Number of states line
						  {
							  Scanner s = new Scanner(strLine);
							  s.useDelimiter("");
							  int i=0;
							  int lengthT=1;
							  while (s.hasNext())
							  {
								  if (s.hasNextInt())
								  {
									  states[i] = s.nextInt();
									  lengthT*=states[i];
									  i++;
								  }
								  else
									  s.next();
							  }
							  t = new MSCATransition[lengthT*lengthT*4];//guessed upper bound WARNING
							  s.close();
							  break;
						  }
						  case "I": //Initial state
						  {
							  Scanner s = new Scanner(strLine);
							  s.useDelimiter("");
							  int i=0;
							  while (s.hasNext())
							  {
								  if (s.hasNextInt())
								  {
									  initial[i] = s.nextInt();
									  i++;
								  }
								  else
									  s.next();
							  }
							  s.close();
							  break;
						  }
						  case "F": //Final state
						  {
							  String[] ss=strLine.split("]");
							  for (int ind=0;ind<ss.length;ind++)
							  {
								  Scanner s = new Scanner(ss[ind]);
								  s.useDelimiter("");
								  int i=0;
								  int[] tf = new int[states[i]]; //upper bound
								  while (s.hasNext())
								  {
									  if (s.hasNextInt())
									  {
										  tf[i] = s.nextInt();
										  i++;
									  }
									  else
										  s.next();
								  }
								  fin[ind]= new int[i];
								  for (int ii=0;ii<i;ii++)
									  fin[ind][ii]=tf[ii];
								  s.close();
							  }
							  break;
						  }
						  case "(": //a may transition
						  {
							  String[] ss=strLine.split("]");
							  int what=0;
							  int[][] store=new int[2][];
							  for (int i=0;i<ss.length;i++)
							  {
								  int[] arr = new int[rank];
								  Scanner s = new Scanner(ss[i]);
								  s.useDelimiter(",|\\[| ");
								  int j=0;
								  while (s.hasNext())
								  {
									  if (s.hasNextInt())
									  {
										 arr[j]=s.nextInt();
										 j++;
									  }
									  else {
										   s.next();
									  }
								  }
								  s.close();
								  if (what==2)
								  {
									  t[pointert]=new MSCATransition(store[0],store[1],arr,false);
									  what=0;
									  pointert++;
								  }
								  else
									  store[what]=arr;
								  what++;
							  }						 
							  break;
						  }
						  case "!": //a must transition
						  {
							  String[] ss=strLine.split("]");
							  int what=0;
							  int[][] store=new int[2][];
							  for (int i=0;i<ss.length;i++)
							  {
								  int[] arr = new int[rank];
								  Scanner s = new Scanner(ss[i]);
								  s.useDelimiter(",|\\[| ");
								  int j=0;
								  while (s.hasNext())
								  {
									  if (s.hasNextInt())
									  {
										 arr[j]=s.nextInt();
										 j++;
									  }
									  else {
										   s.next();
									  }
								  }
								  s.close();
								  if (what==2)
								  {
									  t[pointert]=new MSCATransition(store[0],store[1],arr,true);
									  what=0;
									  pointert++;
								  }
								  else
									  store[what]=arr;
								  what++;
							  }						 
							  break;
						  }
					  }
				  }
			}
			br.close();	
			MSCATransition[] fintr = new MSCATransition[pointert]; //the length of the array is exactly the number of transitions
			  for (int i=0;i<pointert;i++)
			  {
				  fintr[i]=t[i];
			  }
			 
			return new MSCA(rank,initial,states,fin,fintr);
		} catch (Exception e) {e.printStackTrace();}
		return null;
	}
	
	

	
	/**
	 * @return	the array of transitions
	 */
	public  MSCATransition[] getTransition()
	{
		Transition[] temp = super.getTransition();
		MSCATransition[] t = new MSCATransition[temp.length];
		for (int i=0;i<temp.length;i++)
				t[i]=(MSCATransition)temp[i];
		return t;
	}
	
	
	/**
	 * compared to CA this method also clones the must transitions
	 * @return a new object CA clone
	 */
	public MSCA clone()
	{
		MSCATransition[] at = this.getTransition();
		MSCATransition[] finalTr = new MSCATransition[at.length];
		for(int i=0;i<finalTr.length;i++)
		{
			int[] in=at[i].getSource();
			int[] l=at[i].getLabelP();
			int[] f= at[i].getArrival();
			boolean must=at[i].isMust();
			finalTr[i] = new MSCATransition(Arrays.copyOf(in,in.length),Arrays.copyOf(l,l.length),Arrays.copyOf(f,f.length),must);
		}	
		int[][] finalstates=getFinalStatesCA();
		int[][] nf = new int[finalstates.length][];
		for (int i=0;i<finalstates.length;i++)
			nf[i]=Arrays.copyOf(finalstates[i], finalstates[i].length);
		
		return new MSCA(getRank(),Arrays.copyOf(getInitialCA(), getInitialCA().length),Arrays.copyOf(getStatesCA(), getStatesCA().length),finalstates,finalTr);
	}
	
	/**
	 * compute the projection on the i-th principal, or null if rank=1
	 * @param i		index of the CA
	 * @return		the ith principal
	 */
	public MSCA proj(int i)
	{
		/*
		if ((i<0)||(i>rank)) //check if the parameter i is in the rank of the CA
			return null;
		MSCATransition[] tra = this.getTransition();
		int[] init = new int[1];
		init[0]=initial[i];
		int[] st= new int[1];
		st[0]= states[i];
		int[][] fi = new int[1][];
		fi[0]=finalstates[i];
		MSCATransition[] t = new MSCATransition[tra.length];
		int pointer=0;
		for (int ind=0;ind<tra.length;ind++)
		{
			MSCATransition tt= ((MSCATransition)tra[ind]);
			int label = tt.getLabelP()[i];
			if(label!=0)
			{
				int source =  tt.getSource()[i];
				int dest = tt.getArrival()[i];
				int[] sou = new int[1];
				sou[0]=source;
				int[] des = new int[1];
				des[0]=dest;
				int[] lab = new int[1];
				lab[0]=label;
				MSCATransition selected = new MSCATransition(sou,lab,des);
				boolean skip=false;
				for(int j=0;j<pointer;j++)
				{
					if (t[j].equals(selected))
					{
						skip=true;
						break;
					}
				}
				if (!skip)
				{
					t[pointer]=selected;
					pointer++;
				}
			}
		}
		
		tra = new MSCATransition[pointer];
		for (int ind=0;ind<pointer;ind++)
			tra[ind]=t[ind];
		return new MSCA(1,init,st,fi,tra); */
		return null;  //TODO
	}
	

	
	/**
	 * compute the most permissive controller for modal agreement
	 * the algorithm is different from the corresponding of CA
	 * 
	 * @return the most permissive controller for modal agreement
	 */
	public MSCA mpc()
	{
		MSCA a = this.clone();
		MSCATransition[] tr = a.getTransition();
		MSCATransition[] rem= new MSCATransition[tr.length];  //solo per testing
		//int[][] fs=a.allFinalStates();
		int removed=0;
		MSCATransition[] mustrequest=new MSCATransition[tr.length]; //initial  transitions
		int pointer4=0;
		for (int i=0;i<tr.length;i++)
		{
			if ((tr[i].request()))
			{
				if (!tr[i].isMust())
				{
					rem[removed]=tr[i];
					tr[i] = null;
					removed++;
				}
				else
				{
					mustrequest[pointer4]= new MSCATransition(tr[i].getSource(),tr[i].getLabelP(),tr[i].getArrival(),true);
					pointer4++;
					//if ((unmatch==null)||(!MSCAUtil.contains(tr[i], unmatch)))
					if (tr[i].isMatched(a))
					{
						rem[removed]=tr[i];
						tr[i] = null;
						removed++;
					}
				}
			}
		}

		tr=  MSCAUtil.removeHoles(tr, removed);		
		mustrequest=MSCAUtil.removeTailsNull(mustrequest, pointer4);
		a.setTransition(tr); //K_0 
		int[][] R=a.getDanglingStates();
//		//all the source states of unmatched transitions
//		unmatch=a.getUnmatch();
//		if (unmatch!=null)
//		{
//			int pointer=0;
//			int[][] R_0= new int[unmatch.length][];
//			for (int i=0;i<unmatch.length;i++)
//			{
//				if (!MSCAUtil.contains(unmatch[i].getSource(),R_0))
//				{
//					R_0[pointer]=unmatch[i].getSource();
//					pointer++;
//				}
//			}
//			R_0=MSCAUtil.removeTailsNull(R_0, pointer);
//			R=MSCAUtil.setUnion(R, R_0);
//		}
		int[][] R_0=MSCATransition.sourcesUnmatched(mustrequest, a);
		R=MSCAUtil.setUnion(R, R_0);
		boolean update=false;
		do{
			MSCATransition[] trcheck= new MSCATransition[tr.length*R.length];//all must transitions without redundant source state
			//int[] index=new int[tr.length*R.length]; //the ith element of trcheck is the index[i] element of tr
			int pointer2=0;
			removed=0;
			 rem= new MSCATransition[tr.length]; 
			for (int i=0;i<tr.length;i++)  //for all transitions
			{
				if (!(tr[i]==null))
				{
					if (tr[i].isMust())
					{   
						if (MSCAUtil.contains(tr[i].getSource(), R))
						{
							rem[removed]=tr[i];
							tr[i]=null;
							removed++;
						}
						else
						{
							trcheck[pointer2]=tr[i]; //we will check if the target state is redundant to update R
							//index[pointer2]=i;
							pointer2++;
						}
					}
					else if (!tr[i].isMust()&&(MSCAUtil.contains(tr[i].getArrival(), R)))
					{
						rem[removed]=tr[i];
						tr[i]=null;
						removed++;
					}
				}
			} 
			tr=  MSCAUtil.removeHoles(tr, removed);
			a.setTransition(tr);  //K_i
			//update R
			int[][] newR=new int[pointer2][];
			int pointer3=0;
			for (int i=0;i<pointer2;i++)//for all must transitions without redundant source state
			{
				//for (int j=0;j<R.length;j++)//for all redundant states
				//.{
				//	if (Arrays.equals(trcheck[i].getArrival(), R[j])) 
				//	{
				//if arrival state is redundant,  add source state to R it has not been already added, we know that source state is not in R
				// setUnion removes duplicates we could skip the check
						if ((MSCAUtil.contains(trcheck[i].getArrival(), a.getDanglingStates())&&(!MSCAUtil.contains(trcheck[i].getSource(),newR))))//&&(!MSCAUtil.contains(trcheck[i].getSource(), fs)))
						{
							newR[pointer3]=trcheck[i].getSource();
							pointer3++;
						}
				//	}
				//}
			}
			update=(pointer3>0);
			if (update)
			{
				R=MSCAUtil.setUnion(R, MSCAUtil.removeTailsNull(newR, pointer3));
			}
			int[][] su= MSCATransition.sourcesUnmatched(mustrequest, a);
			int[][] newsources=	MSCAUtil.setUnion(R_0 ,su);
			if (newsources.length!=R_0.length)
			{
				R_0=newsources;
				R=MSCAUtil.setUnion(R, R_0);
				update=true;
			}
			int[][] danglingStates=a.getDanglingStates();
			int[][] RwithDang=	MSCAUtil.setUnion(R ,danglingStates);
			if (RwithDang.length!=R.length)
			{
				R=RwithDang;
				update=true;
			}
		}while(update);
		
		if (MSCAUtil.contains(a.getInitialCA(), R))
			return null;
		
//		tr=  MSCAUtil.removeHoles(tr, removed);
//		a.setTransition(tr);
		a = (MSCA) MSCAUtil.removeUnreachable(a);
		return a;
	}
	
	
	
//	/**
//	 * similar to the corresponding method in CA class, with CATransition swapped with MSCATransition and CA swapped with MSCA  
//	 * 
//	 * @return all the reachable states 
//	 */
//	private int[][] reachableStates()
//	{
//		MSCA aut=this.clone();
//		aut = (MSCA) MSCAUtil.removeUnreachable(aut);
//		aut = (MSCA) MSCAUtil.removeDanglingTransitions(aut);
//		int[][] s = new int[this.prodStates()][];
//		s[0]=aut.getInitialCA();
//		MSCATransition[] t = aut.getTransition();
//		int pointer=1;
//		for (int i=0;i<t.length;i++)
//		{
//			int[] p = t[i].getArrival();
//			boolean found=false;
//			int j=0;
//			while((!found)&&(s[j]!=null))
//			{
//				found = Arrays.equals(p, s[j]);
//				j++;
//			}
//			if (!found)
//			{
//				s[pointer]=p;
//				pointer++;
//			}
//		}
//	    int[][] f = new int[pointer][];
//	    for (int i=0;i<pointer;i++)
//	    	f[i]=s[i];
//		return f;
//	}
	
	/**
	 * 
	 * 
	 * @return all  states that appear in at least one transition
	 */
	public int[][] allStates()
	{
		MSCA aut=this.clone();
		int[][] s = new int[this.prodStates()][];
		s[0]=aut.getInitialCA();
		MSCATransition[] t = aut.getTransition();
		int pointer=1;
		for (int i=0;i<t.length;i++)
		{
			int[] start = t[i].getSource();
			int[] arr = t[i].getArrival();
			
			if (!MSCAUtil.contains(arr, s))
			{
				s[pointer]=arr;
				pointer++;
			}
			if (!MSCAUtil.contains(start, s))
			{
				s[pointer]=start;
				pointer++;
			}
		}
		s=MSCAUtil.removeTailsNull(s, pointer);
//	    int[][] f = new int[pointer][];
//	    for (int i=0;i<pointer;i++)
//	    	f[i]=s[i];
		return s;
	}
	
//	/**
//	 * 
//	 * similar to the corresponding method in CA class, with CATransition swapped with MSCATransition and CA swapped with MSCA  
//	 * @return all the final states of the CA
//	 */
//	public  int[][] allFinalStates()
//	{
////		if (rank==1)
////			return finalstates;
//
//		int[][] finalstates=getFinalStatesCA();
//		int[] states=new int[finalstates.length];
//		int comb=1;
//		int[] insert= new int[states.length];
//		for (int i=0;i<states.length;i++)
//		{
//			states[i]=finalstates[i].length;
//			comb*=states[i];
//			insert[i]=0;
//		}
//		int[][] modif = new int[comb][];
//		int[] indstates = new int[1];
//		indstates[0]= states.length-1;
//		int[] indmod = new int[1];
//		indmod[0]= 0; 
//		//CAUtil.recGen(finalstates, modif,  states, 0, states.length-1, insert);
//		MSCAUtil.recGen(finalstates, modif,  states, indmod, indstates, insert);
//		return modif;
//	}
	
	
	
	
	/**
	 * return redundant states who do not reach a final state or are unreachable
	 * not inherited from CA
	 * @return	redundant states of at
	 */
	protected int[][] getDanglingStates()
	{
		int pointerreachable=0;
		int pointerunreachable=0;
		int[][] reachable = new int[this.prodStates()][]; 
		int[][] unreachable = new int[this.prodStates()][];
		int[][] fs = this.allFinalStates();
		int[][] redundantStates = new int[this.prodStates()][];
		int[][] allStates = this.allStates();
		int pointer=0;
		for (int ind=0;ind<allStates.length;ind++)
		{
				// for each state checks if  is reachable from one of the final states of the ca, and if it is reachable
				boolean remove=true;
				for (int i=0;i<fs.length;i++)
				{
					int[] pointervisited = new int[1];
					pointervisited[0]=0;
					if((MSCAUtil.amIReachable(fs[i],this,allStates[ind],new int[this.prodStates()][],pointervisited,reachable,unreachable,pointerreachable,pointerunreachable)&&remove)  
						&&(MSCAUtil.amIReachable(allStates[ind],this,getInitialCA(),new int[this.prodStates()][],pointervisited,reachable,unreachable,pointerreachable,pointerunreachable)&&remove))  	
						remove=false;
					pointervisited = new int[1];
					pointervisited[0]=0;
				}
				if ((remove)&&(!MSCAUtil.contains(allStates[ind],redundantStates)))//non dovrebbe essercene bisogno
				{
					redundantStates[pointer]=allStates[ind];
					pointer++;
				}
													
		}
		//remove null space in array redundantStates
		redundantStates = MSCAUtil.removeTailsNull(redundantStates, pointer);
		
		return redundantStates;
	}
	
	/**
	 * this method is not inherited from CA
	 * @return	all the  must transitions request that are not matched 
	 */
	protected  MSCATransition[] getUnmatch()
	{
		MSCATransition[] tr = this.getTransition();
		int[][] fs=this.allFinalStates();
		int pointer=0;
		int[][] R=this.getDanglingStates();
		MSCATransition[] unmatch = new MSCATransition[tr.length];
		for (int i=0;i<tr.length;i++)
		{
			if ((tr[i].request())
				&&((tr[i].isMust())
				&&(!MSCAUtil.contains(tr[i].getSource(), fs)))) // if source state is not final
			{
				boolean matched=false;
				for (int j=0;j<tr.length;j++)	
				{
					if ((tr[j].match())
						&&(tr[j].isMust())
						&&(tr[j].receiver()==tr[i].receiver())	//the same principal
						&&(tr[j].getSource()[tr[j].receiver()]==tr[i].getSource()[tr[i].receiver()]) //the same source state					
						&&(tr[j].getLabelP()[tr[j].receiver()]==tr[i].getLabelP()[tr[i].receiver()]) //the same request
						&&(!MSCAUtil.contains(tr[i].getSource(), R))) //source state is not redundant
						{
							matched=true; // the request is matched
						}
				}
				if (!matched)
				{
					unmatch[pointer]=tr[i];
					pointer++;
				}
			}
		}
		if (pointer>0)
		{
			unmatch = MSCAUtil.removeTailsNull(unmatch, pointer);
			return unmatch;
		}
		else
			return null;
	}
	
	public MSCATransition[] createArrayTransition(int length)
	{
		return new MSCATransition[length];
	}
	public MSCATransition[][] createArrayTransition2(int length)
	{
		return new MSCATransition[length][];
	}
	public MSCA createNew(int rank, int[] initial, int[] states, int[][] finalstates,CATransition[] tra)
	{
		return new MSCA(rank,initial,states,finalstates,(MSCATransition[])tra);
	}
}