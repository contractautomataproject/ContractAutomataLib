package MSCA;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import FSA.Transition;

/**
 * Utilities for MSCA: product, aproduct
 * 
 * @author Davide Basile
 *
 */
public class MSCAUtil 
{

	static boolean debug = true;
	/**
	 * compute the product automaton of the CA given in aut,  the complexity of this method can be improved 
	 * @param aut the operands of the product
	 * @return the composition of aut
	 */
	public static MSCA product(MSCA[] aut)
	{
		if (aut.length==1)
			return aut[0];
		/**
		 * compute rank, states, initial states, final states
		 */
		int prodrank = 0;
		for (int i=0;i<aut.length;i++)
		{
			prodrank = prodrank+(aut[i].getRank()); 
		}
		int[] statesprod = new int[prodrank];
		int[][] finalstatesprod = new int[prodrank][];
		int[] initialprod = new int[prodrank];
		int totnumstates=1;
		int pointerprodrank=0;
		for (int i=0;i<aut.length;i++)
		{
			for (int j=0;j<aut[i].getRank();j++)
			{
				statesprod[pointerprodrank]= aut[i].getStatesCA()[j];		
				totnumstates *= statesprod[pointerprodrank];
				finalstatesprod[pointerprodrank] = aut[i].getFinalStatesCA()[j];
				initialprod[pointerprodrank] = aut[i].getInitialCA()[j];
				pointerprodrank++;
			}
		}
		
		/**
		 * compute transitions, non associative
		 * 
		 * scan all pair of transitions, if there is a match
		 * then generate the match in all possible contexts		 
		 * it also generates the independent moves, then clean from invalid transitions 
		 */
		MSCATransition[][] prodtr = new MSCATransition[aut.length][];
	//	Transition[][] mustprodtr = new MSCATransition[aut.length][];
		int trlength = 0;
		for(int i=0;i<aut.length;i++)
		{
			prodtr[i]= aut[i].getTransition();
	//		mustprodtr[i]= aut[i].getMustTransition();
			trlength = trlength + prodtr[i].length;// + mustprodtr[i].length;
		}
		MSCATransition[] transprod = new MSCATransition[(trlength*(trlength-1)*totnumstates)]; //Integer.MAX_VALUE - 5];////upper bound to the total transitions 
		//int pointertemp;
		int pointertransprod = 0;
		for (int i=0;i<prodtr.length;i++) // for all the automaton in the product
		{
			Transition[] t = prodtr[i];
			for (int j=0;j<t.length;j++)  // for all transitions of automaton i
			{
				MSCATransition[][] temp = new MSCATransition[trlength*(trlength-1)][];
				//Transition[] trtemp = new MSCATransition[trlength*(trlength-1)];//stores the other transition involved in the match in temp
				int pointertemp=0; //reinitialize for each new transition
				boolean match=false;
				for (int ii=0;ii<prodtr.length;ii++) //for all others automaton
				{
					if (ii!=i)
					{
						MSCATransition[] tt = prodtr[ii];
						for (int jj=0;jj<tt.length;jj++) //for all transitions of other automatons
						{
							if (MSCATransition.match( ((MSCATransition)t[j]).getLabelP() ,((MSCATransition) tt[jj]).getLabelP() )) //match found
							{
								match=true;
								MSCATransition[] gen;
								if (i<ii)
									 gen = generateTransitions(t[j],tt[jj],i,ii,aut);
								else
									gen = generateTransitions(tt[jj],t[j],ii,i,aut);
								temp[pointertemp]=gen; //temp is temporarily used for comparing matches and offers/requests
								//trtemp[pointertemp]=tt[jj];
								pointertemp++;
								for (int ind=0;ind<gen.length;ind++)
									//copy all the matches in the transition of the product automaton, if not already in !
								{
									boolean copy=true;
									for (int ind2=0;ind2<pointertransprod;ind2++)
									{
										if (transprod[ind2].equals(gen[ind]))
										{
											copy=false;
											break;
										}
									}
									if(copy) 
									{
										transprod[pointertransprod]=gen[ind]; 
										if (debug)
											System.out.println(gen[ind].toString());
										pointertransprod++;
									}
								}
							}
						}
					}
				}
				
				/*insert only valid transitions of gen, that is a principle moves independently in a state only if it is not involved
				  in matches. The idea is  that firstly all the independent moves are generated, and then we remove the invalid ones.  
				  */
				MSCATransition[] gen = generateTransitions(t[j],null,i,-1,aut);	
				if ((match)&&(gen!=null))		
				{
					/**
					 * extract the first transition of gen to check the principal who moves 
					 * and its state 
					 */
					MSCATransition tra = gen[0];
					int[] lab = tra.getLabelP(); 
					int pr1=-1;
					for (int ind2=0;ind2<lab.length;ind2++)
					{
						if (lab[ind2]!=0)
						{
							pr1=ind2; //principal
						}
					}
					int label = tra.getLabelP()[pr1];  //the action of the principal who moves
					for (int ind3=0;ind3<gen.length;ind3++)
					{
						for (int ind=0;ind<pointertemp;ind++)
							for(int ind2=0;ind2<temp[ind].length;ind2++)
							{	
								if(gen[ind3]!=null)
								{
									if (Arrays.equals(gen[ind3].getSource(),temp[ind][ind2].getSource()) &&  //the state is the same
											label==temp[ind][ind2].getLabelP()[pr1]) //pr1 makes the same move
									{
										gen[ind3]=null;
									}
								}
							}
					}
								
				}
				/**
				 * finally insert only valid independent moves in transprod
				 */
				for (int ind=0;ind<gen.length;ind++)
				{
					if (gen[ind]!=null)
					{
						try{
						transprod[pointertransprod]=gen[ind];
						}catch(ArrayIndexOutOfBoundsException e){
							e.printStackTrace();
							}
						if (debug)
							System.out.println(gen[ind].toString());
						pointertransprod++;
					}
				}
			}
		}
			
		/**
		 * remove all unused space in transProd (null at the end of the array)
		 */
		MSCATransition[] finalTr = new MSCATransition[pointertransprod];
		for (int ind=0;ind<pointertransprod;ind++)
			finalTr[ind]= (MSCATransition)transprod[ind];
		
	
		MSCA prod =  new MSCA(prodrank,initialprod,statesprod,finalstatesprod,finalTr);
		
		if (debug)
			System.out.println("Remove unreachable ...");
		prod = removeUnreachable(prod);
		
		return prod;
	}
	
	/**
	 * remove the unreachable transitions from aut
	 * @param at	the CA
	 * @return	a new CA clone of aut with only reachable transitions
	 */
	protected static MSCA removeUnreachable(MSCA at)
	{
		MSCA aut = at.clone();
		MSCATransition[] finalTr=aut.getTransition();
		
		/**
		 * remove unreachable transitions
		 */
		int removed=0;
		int pointerreachable=1;
		int pointerunreachable=0;
		int[][] reachable = new int[at.prodStates()][]; 
		int[][] unreachable = new int[at.prodStates()][];
		reachable[0]=aut.getInitialCA();
		for (int ind=0;ind<finalTr.length;ind++)
		{
			//for each transition t checks if the source state of t is reachable from the initial state of the CA
			MSCATransition t=(MSCATransition)finalTr[ind];
			int[] s = t.getSource();
			/**
			int[] debugg = {0,0,1};
			if (Arrays.equals(s,debugg))
				System.out.println("debug");*/
			int[] pointervisited = new int[1];
			pointervisited[0]=0;
			if (debug)
				System.out.println("Checking Reachability state "+Arrays.toString(s));
			if(!amIReachable(s,aut,aut.getInitialCA(),new int[aut.prodStates()][],pointervisited,reachable,unreachable,pointerreachable,pointerunreachable))
			{
				finalTr[ind]=null;
				removed++;
				boolean found=false;
				for (int i=0;i<pointerunreachable;i++)
				{
					if (Arrays.equals(unreachable[i],s))
					{
						found=true;
					}
				}
				if (!found)
				{
					unreachable[pointerunreachable]=s;
					pointerunreachable++;
				}
			}
			else
			{
				boolean found=false;
				for (int i=0;i<pointerreachable;i++)
				{
					if (Arrays.equals(reachable[i],s))
					{
						found=true;
					}
				}
				if (!found)
				{
					reachable[pointerreachable]=s;
					pointerreachable++;
				}
			}
		}
		
		/**
		 * remove holes (null) in finalTr2
		 */
		int pointer=0;
		MSCATransition[] finalTr2 = new MSCATransition[finalTr.length-removed];
		for (int ind=0;ind<finalTr.length;ind++)
		{
			if (finalTr[ind]!=null)
			{
				finalTr2[pointer]=finalTr[ind];
				pointer++;
			}
		}
		aut.setTransition(finalTr2);
		return aut;
	}
	
	/**
	 * remove transitions who do not reach a final state
	 * @param at	the CA
	 * @return	CA without hanged transitions
	 */
	protected static MSCA removeRedundantTransitions(MSCA at)
	{
		MSCA aut = at.clone();
		MSCATransition[] finalTr=aut.getTransition();
		int removed=0;
		int pointerreachable=0;
		int pointerunreachable=0;
		int[][] reachable = new int[at.prodStates()][]; 
		int[][] unreachable = new int[at.prodStates()][];
		int[][] fs = aut.allFinalStates();
		
		
		for (int ind=0;ind<finalTr.length;ind++)
		{
			//if (!finalTr[ind].isMust()) //must transitions cannot be removed
				//{
				// for each transition checks if the arrival state s is reachable from one of the final states of the ca
				MSCATransition t=(MSCATransition)finalTr[ind];
				int[] arr = t.getArrival();
	
				boolean remove=true;
				
				for (int i=0;i<fs.length;i++)
				{
					int[] pointervisited = new int[1];
					pointervisited[0]=0;
					if(amIReachable(fs[i],aut,arr,new int[aut.prodStates()][],pointervisited,reachable,unreachable,pointerreachable,pointerunreachable)) //if final state fs[i] is reachable from arrival state arr
						remove = false;
				}
				//if t does not reach any final state then remove
				if(remove)
				{
					finalTr[ind]=null;
					removed++;
				}
			//}											
		}
			
			
		/**
		 * remove holes (null) in finalTr2
		 */
		int pointer=0;
		MSCATransition[] finalTr2 = new MSCATransition[finalTr.length-removed];
		for (int ind=0;ind<finalTr.length;ind++)
		{
			if (finalTr[ind]!=null)
			{
				finalTr2[pointer]=finalTr[ind];
				pointer++;
			}
		}
		aut.setTransition(finalTr2);
		return aut;
	}
			
	
		
	
	
	/**
	 * true if state[] is reachable from  from[]  in aut
	 * @param state
	 * @param aut
	 * @param visited
	 * @param pointervisited
	 * @return  true if state[] is reachable from  from[]  in aut
	 */
	protected static boolean amIReachable( int[] state, MSCA aut, int[] from, int[][] visited, int[] pointervisited,int[][] reachable,int[][] unreachable, int pointerreachable,int pointerunreachable )
	{
		if (Arrays.equals(state,from))
			return true;
		for (int i=0;i<pointerunreachable;i++)
		{
			if (Arrays.equals(unreachable[i],state))
				return false;
		}
		for (int i=0;i<pointerreachable;i++)
		{
			if (Arrays.equals(reachable[i],state))
				return true;
		}
		
		for (int j=0;j<pointervisited[0];j++)
		{
			if (Arrays.equals(visited[j],state))
			{
				return false;
			}
		}
		visited[pointervisited[0]]=state;
		pointervisited[0]++;
		
		//if (debug)
		//	System.out.println("Visited "+pointervisited[0]+" "+Arrays.toString(visited[pointervisited[0]-1]));
		MSCATransition[] t = aut.getTransition();
		for (int i=0;i<t.length;i++)
		{
			if (t[i]!=null)
			{
				if (Arrays.equals(state,t[i].getArrival()))
				{
					if (amIReachable(t[i].getSource(),aut,from,visited,pointervisited,reachable,unreachable,pointerreachable,pointerunreachable))
						return true;
				}
			}
		}
		return false;
	}
	
	
	/**
	 * 
	 * @param t  first transition made by one CA
	 * @param tt second transition if it is a match, otherwise null
	 * @param i  the index of the CA whose transition is t
	 * @param ii the index of the CA whose transition is tt or -1
	 * @param aut all the CA to be in the transition
	 * @return an array of transitions where i (and ii) moves and the other stays idle in each possible state 
	 */
	private static MSCATransition[] generateTransitions(Transition t, Transition tt, int i, int ii, MSCA[] aut)
	{
		/**
		 * preprocessing to the recursive method recgen:
		 * it computes  the values firstprinci,firstprincii,numtransitions,states
		 */
		int prodrank = 0; //the sum of rank of each CA in aut, except i and ii
		int firstprinci=-1; //index of first principal in aut[i] in the list of all principals in aut
		int firstprincii=-1; //index of first principal in aut[ii] in the list of all principals in aut
		int[] states=null; //the number of states of each principal, except i and ii
		int numtransitions=1; //contains the product of the number of states of each principals, except for those of i and ii
		if (tt!= null) //if is a match
		{			
			/**
			 * first compute prodrank, firstprinci,firstprincii
			 */
			for (int ind=0;ind<aut.length;ind++)
			{
				if ((ind!=i)&&(ind!=ii))
					prodrank += (aut[ind].getRank()); 
				else 
				{
					if (ind==i)
						firstprinci=prodrank; //these values are handled inside generateATransition static method
					else 
						firstprincii=prodrank; //note that firstprinci and firstprincii could be equal
				}
					
			}
			if (prodrank!=0)
			{
				states = new int[prodrank]; 
				int indstates=0;
				//filling the array states with number of states of all principals of CA in aut except of i and ii
				for (int ind=0;ind<aut.length;ind++) 
				{
					if ((ind!=i)&&(ind!=ii))
					{
						int[] statesprinc=aut[ind].getStatesCA();
						for(int ind2=0;ind2<statesprinc.length;ind2++)
							{						
								states[indstates]=statesprinc[ind2];
								numtransitions*=states[indstates];
								indstates++;
							}
					}
				}		
			}
		}
		else	//is not a match
		{
			for (int ind=0;ind<aut.length;ind++)
			{
				if (ind!=i)
					prodrank = prodrank+(aut[ind].getRank()); 
				else if (ind==i)
					firstprinci=prodrank;					
			}
			if(prodrank!=0)
			{
				states = new int[prodrank]; //the number of states of each principal except i 
				int indstates=0;
				//filling the array states
				for (int ind=0;ind<aut.length;ind++)
				{
					if (ind!=i)
					{
						int[] statesprinc=aut[ind].getStatesCA();
						for(int ind2=0;ind2<statesprinc.length;ind2++)
							{						
								states[indstates]=statesprinc[ind2];
								numtransitions*=states[indstates];
								indstates++;
							}
					}
				}	
			}
		}
		MSCATransition[] tr = new MSCATransition[numtransitions];
		if(prodrank!=0)
		{
			int[] insert= new int[states.length];
			//initialize insert to zero in all component
			for (int ind=0;ind<insert.length;ind++)
				insert[ind]=0;
			recGen(t,tt,firstprinci, firstprincii,tr,states,0, states.length-1, insert);
		}
		else
			tr[0]=generateATransition(t,tt,0,0,new int[0]);
		return tr;
	}
	
	
	/**
	 * 
	 * recursive method that generates all combinations of transitions with all possible states of principals that are idle 
	 * it must start from the end of array states
	 * 
	 * @param t		first transition who moves
	 * @param tt	second transition who moves or null if it is not a match
	 * @param fi	offset of first CA who moves in list of principals
	 * @param fii	offset of second CA who moves in list of principals or empty
	 * @param cat	side effect: modifies cat by adding the generated transitions
	 * @param states	the number of states of each idle principals
	 * @param indcat	pointer in the array cat, the first call must be 0
	 * @param indstates	pointer in the array states, the first call must be states.length-1
	 * @param insert    it is used to generate all the combinations of states of idle principals, the first must be all zero
	 */
	private static void recGen(Transition t, Transition tt, int fi, int fii, MSCATransition[] cat,  int[] states, int indcat, int indstates, int[] insert)
	{
		if (indstates==-1)
			return;
		if (insert[indstates]==states[indstates])
		{
			insert[indstates]=0;
			indstates--;
			recGen(t,tt,fi,fii,cat,states,indcat,indstates,insert);
		}
		else
		{
			if (indstates==states.length-1)
			{
				cat[indcat]=generateATransition(t,tt,fi,fii,insert);
				indcat++;
				insert[indstates]++;
				recGen(t,tt,fi,fii,cat,states,indcat,indstates,insert);
			}
			else
			{
				insert[indstates]++; 
				if (insert[indstates]!=states[indstates])
					indstates=states.length-1;
				recGen(t,tt,fi,fii,cat,states,indcat,indstates,insert);				
			}
		}
	}
	
	
	/**
	 * 
	 * Generates all possible combinations of the states in fin, stored in modif
	 * 
	 * @param fin	the array of final states of each principal
	 * @param modif		the array of final states of the composition, modified by side effect
	 * @param states	states[i] = fin[i].length
	 * @param indmod	index in modif, the first call must be 0
	 * @param indstates		the index in states, the first call must be states.length-1
	 * @param insert	it is used to generate all the combinations of final states, the first call must be all zero
	 */
	protected static void recGen(int[][] fin, int[][] modif,  int[] states, int indmod[], int indstates[], int[] insert)
	{
		if (indstates[0]==-1)
			return;
		if (insert[indstates[0]]==states[indstates[0]])
		{
			insert[indstates[0]]=0;
			indstates[0]--;
			recGen(fin,modif,states,indmod,indstates,insert);
		}
		else
		{
			if (indstates[0]==states.length-1)
			{
				modif[indmod[0]]=new int[insert.length];
				for(int i=0;i<insert.length;i++)
				{
					modif[indmod[0]][i]=fin[i][insert[i]];
				}
				indmod[0]++;
				insert[indstates[0]]++;
				recGen(fin,modif,states,indmod,indstates,insert);
			}
			else
			{
				insert[indstates[0]]++; 
				if (insert[indstates[0]]!=states[indstates[0]])
					indstates[0]=states.length-1;
				recGen(fin,modif,states,indmod,indstates,insert);				
			}
		}
	}
	
	/**
	 * 
	 * @param t				first transition to move
	 * @param tt			second transition to move only in case of match
	 * @param firstprinci  the index to start to copy the principals in t
	 * @param firstprincii the index to start to copy the principals in tt
	 * @param insert		the states of all other principals who stays idle
	 * @return				a new transition where only principals in t (and tt) moves while the other stays idle in their state given in insert[]
	 */
	private static MSCATransition generateATransition(Transition t, Transition tt, int firstprinci, int firstprincii,int[] insert)
	{
		if (tt!=null) //if it is a match
		{
			int[] s=((MSCATransition) t).getSource();
			int[] l=((MSCATransition) t).getLabelP();
			int[] d=((MSCATransition) t).getArrival();
			int[] ss = ((MSCATransition) tt).getSource();
			int[] ll=((MSCATransition) tt).getLabelP();
			int[] dd =((MSCATransition) tt).getArrival();
			int[] initial = new int[insert.length+s.length+ss.length];
			int[] dest = new int[insert.length+s.length+ss.length];
			int[] label = new int[insert.length+s.length+ss.length];
			boolean must = ((MSCATransition) t).isMust() || ((MSCATransition) tt).isMust();
			int counter=0;
			for (int i=0;i<insert.length;i++)
			{
				if (i==firstprinci)
				{
					for (int j=0;j<s.length;j++)
					{
						initial[i+j]=s[j];
						label[i+j]=l[j];
						dest[i+j]=d[j];
					}
					counter+=s.length; //record the shift due to the first CA 
					i--;
					firstprinci=-1;
				}
				else 
				{
					if (i==firstprincii)
					{
						for (int j=0;j<ss.length;j++)
						{
							initial[i+counter+j]=ss[j];
							label[i+counter+j]=ll[j];
							dest[i+counter+j]=dd[j];
						}
						counter+=ss.length;//record the shift due to the second CA 
						i--;
						firstprincii=-1;
					}	
					else 
					{
						initial[i+counter]=insert[i];
						dest[i+counter]=insert[i];
						label[i+counter]=0;
					}
				}
			}
			if (firstprinci==insert.length)//case limit, the first CA was the last of aut
			{
				for (int j=0;j<s.length;j++)
				{
					initial[insert.length+j]=s[j];
					label[insert.length+j]=l[j];
					dest[insert.length+j]=d[j];
				}
				counter+=s.length; //record the shift due to the first CA 
			}
			if (firstprincii==insert.length) //case limit, the second CA was the last of aut
			{
				for (int j=0;j<ss.length;j++)
				{
					initial[insert.length+counter+j]=ss[j];
					label[insert.length+counter+j]=ll[j];
					dest[insert.length+counter+j]=dd[j];
				}
			}
			return new MSCATransition(initial,label,dest,must);	
		}
		else
		{
			int[] s=((MSCATransition) t).getSource();
			int[] l=((MSCATransition) t).getLabelP();
			int[] d=((MSCATransition) t).getArrival();
			int[] initial = new int[insert.length+s.length];
			int[] dest = new int[insert.length+s.length];
			int[] label = new int[insert.length+s.length];
			int counter=0;
			for (int i=0;i<insert.length;i++)
			{
				if (i==firstprinci)
				{
					for (int j=0;j<s.length;j++)
					{
						initial[i+j]=s[j];
						label[i+j]=l[j];
						dest[i+j]=d[j];
					}
					counter+=s.length; //record the shift due to the first CA 
					i--;
					firstprinci=-1;
				}
				else
				{
					initial[i+counter]=insert[i];
					dest[i+counter]=insert[i];
					label[i+counter]=0;
				}
			}
			if (firstprinci==insert.length)//case limit, the first CA was the last of aut
			{
				for (int j=0;j<s.length;j++)
				{
					initial[insert.length+j]=s[j];
					label[insert.length+j]=l[j];
					dest[insert.length+j]=d[j];
				}
				counter+=s.length; //record the shift due to the first CA 
			}
			return new MSCATransition(initial,label,dest,((MSCATransition) t).isMust());	
		}
	}
	
	/**
	 * compute the associative product of the CA in the array a
	 * @param a  array of CA
	 * @return  the associative product
	 */
	public static MSCA aproduct(MSCA[] a)
	{
		int tot=0;
		for (int i=0;i<a.length;i++)
			tot+=a[i].getRank();
		if (tot==a.length)
			return product(a);
		else
		{
			MSCA[] a2=new MSCA[tot];
			int pointer=0;
			for(int i=0;i<a.length;i++)
			{
				if(a[i].getRank()>1)
				{
					for (int j=0;j<a[i].getRank();j++)
					{
						a2[pointer]=a[i].proj(j);
						pointer++;
					}
				}
				else
				{
					a2[pointer]=a[i];
					pointer++;
				}
			}
			return product(a2);
		}
			
	}

	/**
	 * Testing the CA
	 */
	public static void MSCATest()
	{
		try{
			InputStreamReader reader = new InputStreamReader (System.in);
			BufferedReader myInput = new BufferedReader (reader);
			MSCA prod;
			MSCA[] aut=null;
			MSCA a;
			String s="";
			do
			{
				System.out.println("Select an operation");
				System.out.println("1 : product \n2 : projection \n3 : aproduct \n4 : strongly safe \n5 : strong agreement \n6 : safe \n7 : agreement \n8 : strong most permissive controller \n9 : most permissive controller \n10 : branching condition \n11 : mixed choice  \n12 : extended branching condition \n13 : liable \n14 : strongly liable \n15 : exit ");
				s = myInput.readLine();
				if(!s.equals("15"))
				{
					System.out.println("Reset stored automaton...");
					aut=load();
				}
				switch (s)
				{
				case "1":
					System.out.println("Computing the product automaton ... ");
					prod = MSCAUtil.product(aut);
					prod.print();
			        //FSA.write(prod);
					prod.printToFile();
					break;

				case "2":
					System.out.println("Computing the projection of the last CA loaded, insert the index of the principal:");
					s=myInput.readLine();
					int ind = Integer.parseInt(s);
					MSCA projected = aut[aut.length-1].proj(ind);
					projected.print();
					//FSA.write(projected);
					projected.printToFile();
					break;

				case "3":
					System.out.println("Computing the associative product automaton ... ");
					prod = MSCAUtil.aproduct(aut);
					prod.print();
			        //FSA.write(prod);
					prod.printToFile();
					break;

				case "4":
					a = aut[aut.length-1];
					a.print();
					if (a.strongSafe())
						System.out.println("The CA is strongly safe");
					else
						System.out.println("The CA is not strongly safe");
			        //FSA.write(a);
					a.printToFile();
					break;

				case "5":
					a = aut[aut.length-1];
					a.print();
					if (a.strongAgreement())
						System.out.println("The CA admits strong agreement");
					else
						System.out.println("The CA does not admit strong agreement");
			        //FSA.write(a);
					a.printToFile();
					break;

				case "6":
					a = aut[aut.length-1];
					a.print();
					if (a.safe())
						System.out.println("The CA is safe");
					else
						System.out.println("The CA is not safe");
			        //FSA.write(a);
					a.printToFile();
					break;

				case "7":
					a = aut[aut.length-1];
					a.print();
					if (a.agreement())
						System.out.println("The CA admits agreement");
					else
						System.out.println("The CA does not admit agreement");
			        //FSA.write(a);
					a.printToFile();
					break;

				case "8":
					System.out.println("The most permissive controller of strong agreement for the last CA loaded is");
					a = aut[aut.length-1];
					MSCA smpc = a.smpc();
					smpc.print();
					//FSA.write(smpc);
					smpc.printToFile();
					break;

				case "9":
					System.out.println("The most permissive controller of agreement for the last CA loaded is");
					a = aut[aut.length-1];
					MSCA mpc = a.mpc();
					if (mpc!=null)
					{
						mpc.print();
						mpc.printToFile();
					}
					break;

				case "10":
					a = aut[aut.length-1];
					a.print();
					int[][] bc = a.branchingCondition();
					if (bc==null)
						System.out.println("The CA enjoys the branching condition");
					else
					{
						System.out.println("The CA does not enjoy the branching condition ");
						System.out.println("State "+Arrays.toString(bc[2])+" violates the branching condition because it has no transition labelled "+Arrays.toString(bc[1])+" which is instead enabled in state "+Arrays.toString(bc[0]));
					}
			        //FSA.write(a);
			        a.printToFile();
					break;

				case "11":
					a = aut[aut.length-1];
					a.print();
					int[] st = a.mixedChoice();
					if (st!=null)
						System.out.println("The CA has a mixed choice state  "+Arrays.toString(st));
					else
						System.out.println("The CA has no mixed choice states");
			        //FSA.write(a);
			        a.printToFile();
					break;

				case "12":
					a = aut[aut.length-1];
					a.print();
					int[][] ebc = a.extendedBranchingCondition();
					if (ebc==null)
						System.out.println("The CA enjoys the extended branching condition");
					else
					{
						System.out.println("The CA does not enjoy the extended branching condition ");
						System.out.println("State "+Arrays.toString(ebc[2])+" violates the branching condition because it has no transition labelled "+Arrays.toString(ebc[1])+" which is instead enabled in state "+Arrays.toString(ebc[0]));
					}
			        //FSA.write(a);
			        a.printToFile();
					break;
				case"13":
					a = aut[aut.length-1];
					a.print();
					MSCATransition[] l = a.liable();
					System.out.println("The liable transitions are:");
					for(int i=0;i<l.length;i++)
						System.out.println(l[i].toString());
					//FSA.write(a);
					a.printToFile();
					break;
				case"14":
					a = aut[aut.length-1];
					a.print();
					MSCATransition[] sl = a.strongLiable();
					System.out.println("The strongly liable transitions are:");
					for(int i=0;i<sl.length;i++)
						System.out.println(sl[i].toString());
					//FSA.write(a);
					a.printToFile();
					break;
				}				
			}while(!s.equals("15"));

		}catch(Exception e){e.printStackTrace();}
	} 
	
	private static MSCA[] load()
	{
		try
		{
			MSCA[] a = new MSCA[10];
			int i=0;
			MSCA automa;
			String s="";
			InputStreamReader reader = new InputStreamReader (System.in);
			BufferedReader myInput = new BufferedReader (reader);
			while (!s.equals("no")&&i<10)
			{
				System.out.println("Do you want to create/load other CA? (yes/no)");
				s = myInput.readLine();
				//s = "yes";
				if(!s.equals("no"))
				{
					System.out.println("Insert the name of the automaton (without file extension) to load or leave empty for create a new one");
					s = myInput.readLine();
					//s = "CA1";
			        if (!s.isEmpty())
			        {
			        	automa = MSCA.load(s);
			        }
			        else
			        	{
				        automa = new MSCA();
			        	}
			        automa.print();
			        a[i] = automa;
			        //s="no";
			        i++;
				}
			}
			MSCA[] aut;
			if (i<10)
			{
				aut=new MSCA[i];
				for (int ind=0;ind<i;ind++)
					aut[ind]=a[ind];
			}
			else
				aut=a;
			return aut;
		}catch(Exception e){e.printStackTrace();return null;}
	}

	protected static boolean contains(int[] q, int[][] listq)
	{
		for (int i=0;i<listq.length;i++)
		{
			if (Arrays.equals(q, listq[i]))
					return true;
		}
		return false;
	}
	protected static boolean contains(MSCATransition t, MSCATransition[] listq)
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
	protected static int[][] removeTailsNull(int[][] q,int length)
	{
		int[][] r=new int[length][];
		for (int i=0;i<length;i++)
			r[i]=q[i];
		return r;
	}
	protected static MSCATransition[] removeTailsNull(MSCATransition[] q,int length)
	{
		MSCATransition[] r=new MSCATransition[length];
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
	protected static MSCATransition[] removeHoles(MSCATransition[] l, int holes )
	{
		/**
		 * remove holes (null) in t
		 */
		int pointer=0;
		MSCATransition[] fin = new MSCATransition[l.length-holes];
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
