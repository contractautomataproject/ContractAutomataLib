package FMCA;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

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
	
	/**
	 * 
	 * @param aut
	 * @return compute the union of the FMCA in aut
	 */
	public static FMCA union(FMCA[] aut)
	{
		int upperbound=100; //TODO upperbound check
		int rank=aut[0].getRank(); //the aut must have all the same rank
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
			int[] initial=aut[i].getInitialCA();
			for (int z=0;z<initial.length;z++)
				initial[z]=initial[z]+upperbound*(i+1);
			aut[i].setInitialCA(initial);
			FMCATransition[] t=aut[i].getTransition();
			for (int j=0;j<t.length;j++)
			{
				int[] source=Arrays.copyOf(t[j].getSourceP(),t[j].getSourceP().length);
				int[] target=Arrays.copyOf(t[j].getTargetP(),t[j].getTargetP().length);
				for (int z=0;z<source.length;z++)
				{
					source[z] = source[z] + upperbound*(i+1);
					target[z] = target[z] + upperbound*(i+1);
				}
				t[j].setSourceP(source);
				t[j].setTargetP(target);
			}
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
		//dummy transitions to initial states
		FMCATransition[] t=new FMCATransition[aut.length];
		for (int i=0;i<t.length;i++)
		{
			t[i]=new FMCATransition(initial,label,aut[i].getInitialCA(),FMCATransition.action.PERMITTED); 
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
		for (int i=0;i<aut.length;i++)
		{
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
		
		/*int[][] finalstates = new int[rank][];
		for (int i=0;i<rank;i++)
		{
			int[][] fs=aut[i].getFinalStatesCA();
			
		}*/
	
		return new FMCA(rank, initial, states, finalstates, uniontr);
	}
	
//	/**
//	 * identical to the method of CAUtil,  I just substituted CA with MSCA and CATransition with MSCATransition, 
//	 * 
//	 * @param aut the operands of the product
//	 * @return the composition of aut
//	 */
//	public static MSCA product(MSCA[] aut)
//	{
//		if (aut.length==1)
//			return aut[0];
//		/**
//		 * compute rank, states, initial states, final states
//		 */
//		int prodrank = 0;
//		for (int i=0;i<aut.length;i++)
//		{
//			prodrank = prodrank+(aut[i].getRank()); 
//		}
//		int[] statesprod = new int[prodrank];
//		int[][] finalstatesprod = new int[prodrank][];
//		int[] initialprod = new int[prodrank];
//		int totnumstates=1;
//		int pointerprodrank=0;
//		for (int i=0;i<aut.length;i++)
//		{
//			for (int j=0;j<aut[i].getRank();j++)
//			{
//				statesprod[pointerprodrank]= aut[i].getStatesCA()[j];		
//				totnumstates *= statesprod[pointerprodrank];
//				finalstatesprod[pointerprodrank] = aut[i].getFinalStatesCA()[j];
//				initialprod[pointerprodrank] = aut[i].getInitialCA()[j];
//				pointerprodrank++;
//			}
//		}
//		
//		/**
//		 * compute transitions, non associative
//		 * 
//		 * scan all pair of transitions, if there is a match
//		 * then generate the match in all possible contexts		 
//		 * it also generates the independent moves, then clean from invalid transitions 
//		 */
//		MSCATransition[][] prodtr = new MSCATransition[aut.length][];
//		int trlength = 0;
//		for(int i=0;i<aut.length;i++)
//		{
//			prodtr[i]= aut[i].getTransition();
//			trlength = trlength + prodtr[i].length;// + mustprodtr[i].length;
//		}
//		MSCATransition[] transprod = new MSCATransition[(trlength*(trlength-1)*totnumstates)]; //Integer.MAX_VALUE - 5];////upper bound to the total transitions 
//		//int pointertemp;
//		int pointertransprod = 0;
//		for (int i=0;i<prodtr.length;i++) // for all the automaton in the product
//		{
//			Transition[] t = prodtr[i];
//			for (int j=0;j<t.length;j++)  // for all transitions of automaton i
//			{
//				MSCATransition[][] temp = new MSCATransition[trlength*(trlength-1)][];
//				//Transition[] trtemp = new MSCATransition[trlength*(trlength-1)];//stores the other transition involved in the match in temp
//				int pointertemp=0; //reinitialize for each new transition
//				boolean match=false;
//				for (int ii=0;ii<prodtr.length;ii++) //for all others automaton
//				{
//					if (ii!=i)
//					{
//						MSCATransition[] tt = prodtr[ii];
//						for (int jj=0;jj<tt.length;jj++) //for all transitions of other automatons
//						{
//							if (MSCATransition.match( ((MSCATransition)t[j]).getLabelP() ,((MSCATransition) tt[jj]).getLabelP() )) //match found
//							{
//								match=true;
//								MSCATransition[] gen;
//								if (i<ii)
//									 gen = (MSCATransition[]) generateTransitions(t[j],tt[jj],i,ii,aut);
//								else
//									gen = (MSCATransition[]) generateTransitions(tt[jj],t[j],ii,i,aut);
//								temp[pointertemp]=gen; //temp is temporarily used for comparing matches and offers/requests
//								//trtemp[pointertemp]=tt[jj];
//								pointertemp++;
//								for (int ind=0;ind<gen.length;ind++)
//									//copy all the matches in the transition of the product automaton, if not already in !
//								{
//									boolean copy=true;
//									for (int ind2=0;ind2<pointertransprod;ind2++)
//									{
//										if (transprod[ind2].equals(gen[ind]))
//										{
//											copy=false;
//											break;
//										}
//									}
//									if(copy) 
//									{
//										transprod[pointertransprod]=gen[ind]; 
//										if (debug)
//											System.out.println(gen[ind].toString());
//										pointertransprod++;
//									}
//								}
//							}
//						}
//					}
//				}
//				
//				/*insert only valid transitions of gen, that is a principle moves independently in a state only if it is not involved
//				  in matches. The idea is  that firstly all the independent moves are generated, and then we remove the invalid ones.  
//				  */
//				CATransition[] gen = generateTransitions(t[j],null,i,-1,aut);	
//				if ((match)&&(gen!=null))		
//				{
//					/**
//					 * extract the first transition of gen to check the principal who moves 
//					 * and its state 
//					 */
//					MSCATransition tra = (MSCATransition)gen[0];
//					int[] lab = tra.getLabelP(); 
//					int pr1=-1;
//					for (int ind2=0;ind2<lab.length;ind2++)
//					{
//						if (lab[ind2]!=0)
//						{
//							pr1=ind2; //principal
//						}
//					}
//					int label = tra.getLabelP()[pr1];  //the action of the principal who moves
//					for (int ind3=0;ind3<gen.length;ind3++)
//					{
//						for (int ind=0;ind<pointertemp;ind++)
//							for(int ind2=0;ind2<temp[ind].length;ind2++)
//							{	
//								if(gen[ind3]!=null)
//								{
//									if (Arrays.equals(gen[ind3].getSource(),temp[ind][ind2].getSource()) &&  //the state is the same
//											label==temp[ind][ind2].getLabelP()[pr1]) //pr1 makes the same move
//									{
//										gen[ind3]=null;
//									}
//								}
//							}
//					}
//								
//				}
//				/**
//				 * finally insert only valid independent moves in transprod
//				 */
//				for (int ind=0;ind<gen.length;ind++)
//				{
//					if (gen[ind]!=null)
//					{
//						try{
//						transprod[pointertransprod]=(MSCATransition)gen[ind];
//						}catch(ArrayIndexOutOfBoundsException e){
//							e.printStackTrace();
//							}
//						if (debug)
//							System.out.println(gen[ind].toString());
//						pointertransprod++;
//					}
//				}
//			}
//		}
//			
//		/**
//		 * remove all unused space in transProd (null at the end of the array)
//		 */
//		MSCATransition[] finalTr = new MSCATransition[pointertransprod];
//		for (int ind=0;ind<pointertransprod;ind++)
//			finalTr[ind]= (MSCATransition)transprod[ind];
//		
//	
//		MSCA prod =  new MSCA(prodrank,initialprod,statesprod,finalstatesprod,finalTr);
//		
//		if (debug)
//			System.out.println("Remove unreachable ...");
//		prod = (MSCA)removeUnreachable(prod);
//		
//		return prod;
//	}
	
//	private static MSCATransition[] cast(CATransition[] tr)
//	{
//		CATransition[] r = new MSCATransition[tr.length];
//		for (int i=0;i<r.length;i++)
//			r[i]=(MSCATransition) tr[i];
//		return r;
//	}
	
//	/**
//	 * identical to the method of CAUtil,  I just substituted CA with MSCA and CATransition with MSCATransition
//	 * @param at	the CA
//	 * @return	a new CA clone of aut with only reachable transitions
//	 */
//	protected static MSCA removeUnreachable(MSCA at)
//	{
//		MSCA aut = at.clone();
//		MSCATransition[] finalTr=aut.getTransition();
//		
//		/**
//		 * remove unreachable transitions
//		 */
//		int removed=0;
//		int pointerreachable=1;
//		int pointerunreachable=0;
//		int[][] reachable = new int[at.prodStates()][]; 
//		int[][] unreachable = new int[at.prodStates()][];
//		reachable[0]=aut.getInitialCA();
//		for (int ind=0;ind<finalTr.length;ind++)
//		{
//			//for each transition t checks if the source state of t is reachable from the initial state of the CA
//			MSCATransition t=(MSCATransition)finalTr[ind];
//			int[] s = t.getSource();
//			/**
//			int[] debugg = {0,0,1};
//			if (Arrays.equals(s,debugg))
//				System.out.println("debug");*/
//			int[] pointervisited = new int[1];
//			pointervisited[0]=0;
//			if (debug)
//				System.out.println("Checking Reachability state "+Arrays.toString(s));
//			if(!amIReachable(s,aut,aut.getInitialCA(),new int[aut.prodStates()][],pointervisited,reachable,unreachable,pointerreachable,pointerunreachable))
//			{
//				finalTr[ind]=null;
//				removed++;
//				boolean found=false;
//				for (int i=0;i<pointerunreachable;i++)
//				{
//					if (Arrays.equals(unreachable[i],s))
//					{
//						found=true;
//					}
//				}
//				if (!found)
//				{
//					unreachable[pointerunreachable]=s;
//					pointerunreachable++;
//				}
//			}
//			else
//			{
//				boolean found=false;
//				for (int i=0;i<pointerreachable;i++)
//				{
//					if (Arrays.equals(reachable[i],s))
//					{
//						found=true;
//					}
//				}
//				if (!found)
//				{
//					reachable[pointerreachable]=s;
//					pointerreachable++;
//				}
//			}
//		}
//		
//		/**
//		 * remove holes (null) in finalTr2
//		 */
//		int pointer=0;
//		MSCATransition[] finalTr2 = new MSCATransition[finalTr.length-removed];
//		for (int ind=0;ind<finalTr.length;ind++)
//		{
//			if (finalTr[ind]!=null)
//			{
//				finalTr2[pointer]=finalTr[ind];
//				pointer++;
//			}
//		}
//		aut.setTransition(finalTr2);
//		return aut;
//	}
	
//	/**
//	 * identical to the method of CAUtil,  I just substituted CA with MSCA and CATransition with MSCATransition
//	 * @param at	the CA
//	 * @return	CA without hanged transitions
//	 */
//	protected static MSCA removeDanglingTransitions(MSCA at)
//	{
//		MSCA aut = at.clone();
//		MSCATransition[] finalTr=aut.getTransition();
//		int removed=0;
//		int pointerreachable=0;
//		int pointerunreachable=0;
//		int[][] reachable = new int[at.prodStates()][]; 
//		int[][] unreachable = new int[at.prodStates()][];
//		int[][] fs = aut.allFinalStates();
//		
//		
//		for (int ind=0;ind<finalTr.length;ind++)
//		{
//				// for each transition checks if the arrival state s is reachable from one of the final states of the ca
//				MSCATransition t=(MSCATransition)finalTr[ind];
//				int[] arr = t.getArrival();
//	
//				boolean remove=true;
//				
//				for (int i=0;i<fs.length;i++)
//				{
//					int[] pointervisited = new int[1];
//					pointervisited[0]=0;
//					if(amIReachable(fs[i],aut,arr,new int[aut.prodStates()][],pointervisited,reachable,unreachable,pointerreachable,pointerunreachable)) //if final state fs[i] is reachable from arrival state arr
//						remove = false;
//				}
//				//if t does not reach any final state then remove
//				if(remove)
//				{
//					finalTr[ind]=null;
//					removed++;
//				}
//			//}											
//		}
//			
//			
//		/**
//		 * remove holes (null) in finalTr2
//		 */
//		int pointer=0;
//		MSCATransition[] finalTr2 = new MSCATransition[finalTr.length-removed];
//		for (int ind=0;ind<finalTr.length;ind++)
//		{
//			if (finalTr[ind]!=null)
//			{
//				finalTr2[pointer]=finalTr[ind];
//				pointer++;
//			}
//		}
//		aut.setTransition(finalTr2);
//		return aut;
//	}
			
	
		
	
	
//	/**
//	 * 
//	 * identical to the method of CAUtil,  I just substituted CA with MSCA and CATransition with MSCATransition
//	 * I changed visibility too
//	 * @param state
//	 * @param aut
//	 * @param visited
//	 * @param pointervisited
//	 * @return  true if state[] is reachable from  from[]  in aut
//	 */
//	protected static boolean amIReachable( int[] state, MSCA aut, int[] from, int[][] visited, int[] pointervisited,int[][] reachable,int[][] unreachable, int pointerreachable,int pointerunreachable )
//	{
//		if (Arrays.equals(state,from))
//			return true;
//		for (int i=0;i<pointerunreachable;i++)
//		{
//			if (Arrays.equals(unreachable[i],state))
//				return false;
//		}
//		for (int i=0;i<pointerreachable;i++)
//		{
//			if (Arrays.equals(reachable[i],state))
//				return true;
//		}
//		
//		for (int j=0;j<pointervisited[0];j++)
//		{
//			if (Arrays.equals(visited[j],state))
//			{
//				return false;
//			}
//		}
//		visited[pointervisited[0]]=state;
//		pointervisited[0]++;
//		
//		//if (debug)
//		//	System.out.println("Visited "+pointervisited[0]+" "+Arrays.toString(visited[pointervisited[0]-1]));
//		MSCATransition[] t = aut.getTransition();
//		for (int i=0;i<t.length;i++)
//		{
//			if (t[i]!=null)
//			{
//				if (Arrays.equals(state,t[i].getArrival()))
//				{
//					if (amIReachable(t[i].getSource(),aut,from,visited,pointervisited,reachable,unreachable,pointerreachable,pointerunreachable))
//						return true;
//				}
//			}
//		}
//		return false;
//	}
	
//	
//	/**
//	 * identical to the method of CAUtil,  I just substituted CA with MSCA and CATransition with MSCATransition,  
//	 * 
//	 * @param t  first transition made by one CA
//	 * @param tt second transition if it is a match, otherwise null
//	 * @param i  the index of the CA whose transition is t
//	 * @param ii the index of the CA whose transition is tt or -1
//	 * @param aut all the CA to be in the transition
//	 * @return an array of transitions where i (and ii) moves and the other stays idle in each possible state 
//	 */
//	private static MSCATransition[] generateTransitions(Transition t, Transition tt, int i, int ii, MSCA[] aut)
//	{
//		/**
//		 * preprocessing to the recursive method recgen:
//		 * it computes  the values firstprinci,firstprincii,numtransitions,states
//		 */
//		int prodrank = 0; //the sum of rank of each CA in aut, except i and ii
//		int firstprinci=-1; //index of first principal in aut[i] in the list of all principals in aut
//		int firstprincii=-1; //index of first principal in aut[ii] in the list of all principals in aut
//		int[] states=null; //the number of states of each principal, except i and ii
//		int numtransitions=1; //contains the product of the number of states of each principals, except for those of i and ii
//		if (tt!= null) //if is a match
//		{			
//			/**
//			 * first compute prodrank, firstprinci,firstprincii
//			 */
//			for (int ind=0;ind<aut.length;ind++)
//			{
//				if ((ind!=i)&&(ind!=ii))
//					prodrank += (aut[ind].getRank()); 
//				else 
//				{
//					if (ind==i)
//						firstprinci=prodrank; //these values are handled inside generateATransition static method
//					else 
//						firstprincii=prodrank; //note that firstprinci and firstprincii could be equal
//				}
//					
//			}
//			if (prodrank!=0)
//			{
//				states = new int[prodrank]; 
//				int indstates=0;
//				//filling the array states with number of states of all principals of CA in aut except of i and ii
//				for (int ind=0;ind<aut.length;ind++) 
//				{
//					if ((ind!=i)&&(ind!=ii))
//					{
//						int[] statesprinc=aut[ind].getStatesCA();
//						for(int ind2=0;ind2<statesprinc.length;ind2++)
//							{						
//								states[indstates]=statesprinc[ind2];
//								numtransitions*=states[indstates];
//								indstates++;
//							}
//					}
//				}		
//			}
//		}
//		else	//is not a match
//		{
//			for (int ind=0;ind<aut.length;ind++)
//			{
//				if (ind!=i)
//					prodrank = prodrank+(aut[ind].getRank()); 
//				else if (ind==i)
//					firstprinci=prodrank;					
//			}
//			if(prodrank!=0)
//			{
//				states = new int[prodrank]; //the number of states of each principal except i 
//				int indstates=0;
//				//filling the array states
//				for (int ind=0;ind<aut.length;ind++)
//				{
//					if (ind!=i)
//					{
//						int[] statesprinc=aut[ind].getStatesCA();
//						for(int ind2=0;ind2<statesprinc.length;ind2++)
//							{						
//								states[indstates]=statesprinc[ind2];
//								numtransitions*=states[indstates];
//								indstates++;
//							}
//					}
//				}	
//			}
//		}
//		MSCATransition[] tr = new MSCATransition[numtransitions];
//		if(prodrank!=0)
//		{
//			int[] insert= new int[states.length];
//			//initialize insert to zero in all component
//			for (int ind=0;ind<insert.length;ind++)
//				insert[ind]=0;
//			recGen(t,tt,firstprinci, firstprincii,tr,states,0, states.length-1, insert);
//		}
//		else
//			tr[0]=generateATransition(t,tt,0,0,new int[0]);
//		return tr;
//	}
	
	
//	/**
//	 * identical to the method of CAUtil,  I just substituted CA with MSCA and CATransition with MSCATransition,
//	 * 
//	 * recursive method that generates all combinations of transitions with all possible states of principals that are idle 
//	 * it must start from the end of array states
//	 * 
//	 * @param t		first transition who moves
//	 * @param tt	second transition who moves or null if it is not a match
//	 * @param fi	offset of first CA who moves in list of principals
//	 * @param fii	offset of second CA who moves in list of principals or empty
//	 * @param cat	side effect: modifies cat by adding the generated transitions
//	 * @param states	the number of states of each idle principals
//	 * @param indcat	pointer in the array cat, the first call must be 0
//	 * @param indstates	pointer in the array states, the first call must be states.length-1
//	 * @param insert    it is used to generate all the combinations of states of idle principals, the first must be all zero
//	 */
//	private static void recGen(Transition t, Transition tt, int fi, int fii, MSCATransition[] cat,  int[] states, int indcat, int indstates, int[] insert)
//	{
//		if (indstates==-1)
//			return;
//		if (insert[indstates]==states[indstates])
//		{
//			insert[indstates]=0;
//			indstates--;
//			recGen(t,tt,fi,fii,cat,states,indcat,indstates,insert);
//		}
//		else
//		{
//			if (indstates==states.length-1)
//			{
//				cat[indcat]=generateATransition(t,tt,fi,fii,insert);
//				indcat++;
//				insert[indstates]++;
//				recGen(t,tt,fi,fii,cat,states,indcat,indstates,insert);
//			}
//			else
//			{
//				insert[indstates]++; 
//				if (insert[indstates]!=states[indstates])
//					indstates=states.length-1;
//				recGen(t,tt,fi,fii,cat,states,indcat,indstates,insert);				
//			}
//		}
//	}
	
	
//	/**
//	 * 
//	 * 
//	 * identical to the method of CAUtil,  I just substituted CA with MSCA and CATransition with MSCATransition,  
//	 * 
//	 * @param fin	the array of final states of each principal
//	 * @param modif		the array of final states of the composition, modified by side effect
//	 * @param states	states[i] = fin[i].length
//	 * @param indmod	index in modif, the first call must be 0
//	 * @param indstates		the index in states, the first call must be states.length-1
//	 * @param insert	it is used to generate all the combinations of final states, the first call must be all zero
//	 */
//	protected static void recGen(int[][] fin, int[][] modif,  int[] states, int indmod[], int indstates[], int[] insert)
//	{
//		if (indstates[0]==-1)
//			return;
//		if (insert[indstates[0]]==states[indstates[0]])
//		{
//			insert[indstates[0]]=0;
//			indstates[0]--;
//			recGen(fin,modif,states,indmod,indstates,insert);
//		}
//		else
//		{
//			if (indstates[0]==states.length-1)
//			{
//				modif[indmod[0]]=new int[insert.length];
//				for(int i=0;i<insert.length;i++)
//				{
//					modif[indmod[0]][i]=fin[i][insert[i]];
//				}
//				indmod[0]++;
//				insert[indstates[0]]++;
//				recGen(fin,modif,states,indmod,indstates,insert);
//			}
//			else
//			{
//				insert[indstates[0]]++; 
//				if (insert[indstates[0]]!=states[indstates[0]])
//					indstates[0]=states.length-1;
//				recGen(fin,modif,states,indmod,indstates,insert);				
//			}
//		}
//	}
	

	
//	/**
//	 * 
//	 * identical to the method of CAUtil,  I just substituted CA with MSCA and CATransition with MSCATransition,  
//	 * @param a  array of CA
//	 * @return  the associative product
//	 */
//	public static MSCA aproduct(MSCA[] a)
//	{
//		int tot=0;
//		for (int i=0;i<a.length;i++)
//			tot+=a[i].getRank();
//		if (tot==a.length)
//			return product(a);
//		else
//		{
//			MSCA[] a2=new MSCA[tot];
//			int pointer=0;
//			for(int i=0;i<a.length;i++)
//			{
//				if(a[i].getRank()>1)
//				{
//					for (int j=0;j<a[i].getRank();j++)
//					{
//						a2[pointer]=a[i].proj(j);
//						pointer++;
//					}
//				}
//				else
//				{
//					a2[pointer]=a[i];
//					pointer++;
//				}
//			}
//			return product(a2);
//		}
//			
//	}

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
					prod = (FMCA) CAUtil.product(aut);
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
			 if (!(items[i].startsWith("!")||items[i].startsWith("?")))
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
			if (q[i].equals(e))
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
	protected static int[][] removeDuplicates(int[][] m)
	{
		int removed=0;
		for (int i=0;i<m.length;i++)
		{
			for (int j=i+1;j<m.length;j++)
			{
				if ((m[i]!=null)&&(Arrays.equals(m[i],m[j])))
				{
					m[j]=null;
					removed++;
				}
			}
		}
		m= (int[][])removeHoles(m,removed);
		return m;
			
	}
	protected static int[] removeTailsNull(int[] q,int length)
	{
		int[] r=new int[length];
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
}
