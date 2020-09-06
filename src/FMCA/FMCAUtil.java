package FMCA;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import CA.CAState;
import CA.CATransition;

/**
 * Utilities for FMCA
 * 
 * Mainly the composition method and some accessory methods
 * 
 * @author Davide Basile
 *
 */
public class FMCAUtil 
{

	private static boolean debug = false;
	
	public static FMCA BFScomposition(ArrayList<FMCA> aut)
	{
		/**
		 * 
		 */
		return null;
	}

	/**
	 * this is the most important method of the tool, computing the composition.
	 * 
	 * compute the product automaton of the CA given in aut
	 * @param aut the operands of the product
	 * @return the composition of aut
	 */
	public static FMCA composition(FMCA[] aut)
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
		int[] statesprod = new int[prodrank]; //TODO remove
		int[][] finalstatesprod = new int[prodrank][];
		int[] initialprod = new int[prodrank];
		int totnumstates=1;
		int pointerprodrank=0;
		for (int i=0;i<aut.length;i++)
		{
			for (int j=0;j<aut[i].getRank();j++)
			{
				statesprod[pointerprodrank]= aut[i].getNumStatesPrinc()[j];		
				totnumstates *= statesprod[pointerprodrank];
				finalstatesprod[pointerprodrank] = aut[i].getFinalStatesofPrincipals()[j];
				initialprod[pointerprodrank] = aut[i].getInitialCA().getState()[j];
				pointerprodrank++;
			}
		}
		
		/**
		 * compute transitions, non associative
		 * 
		 * scan all pair of transitions, if there is a match
		 * then generate the match in all possible contexts		 
		 * it also generates the independent moves, then clean from invalid transitions 
		 * 
		 * TODO it could be possible to avoid to generate invalid transitions, in a BFS visit
		 */
		FMCATransition[][] prodtr = new FMCATransition[aut.length][];
		int trlength = 0;
		for(int i=0;i<aut.length;i++)
		{
			prodtr[i]= aut[i].getTransition().toArray(new FMCATransition[] {});
			trlength += prodtr[i].length;
		}
		FMCATransition[] transprod = new FMCATransition[(trlength*(trlength-1)*totnumstates)]; //Integer.MAX_VALUE - 5];////upper bound to the total transitions 

	   //int pointertemp = 0;
		int pointertransprod = 0;
		for (int i=0;i<prodtr.length;i++)// for all the automaton in the product
		{
			FMCATransition[] t = prodtr[i];

		
			for (int j=0;j<t.length;j++)  // for all transitions of automaton i
			{
				CATransition[][] temp = new FMCATransition[trlength*(trlength-1)][];
				//Transition[] trtemp = new CATransition[trlength*(trlength-1)];//stores the other transition involved in the match in temp
				int pointertemp=0; //reinitialized each new transition
				boolean match=false;
				for (int ii=0;ii<prodtr.length;ii++)    //for all others automaton
				{
					if (ii!=i)
					{
						FMCATransition[] tt = prodtr[ii];
						for (int jj=0;jj<tt.length;jj++)    //for all transitions of other automatons
						{
							if (CATransition.match(t[j].getLabel() ,tt[jj].getLabel())) //match found
							{
								match=true;
								FMCATransition[] gen;
								if (i<ii)
									 gen = generateTransitions(t[j],tt[jj],i,ii,aut);
								else
									gen = generateTransitions(tt[jj],t[j],ii,i,aut);
								temp[pointertemp]=gen; //temp is temporary used for comparing matches and offers/requests
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

				FMCATransition[] gen = generateTransitions(t[j],null,i,-1,aut);
				if ((match)&&(gen!=null))		
				{
					/**
					 * extract the first transition of gen to check the principal who move 
					 * and its state 
					 */
					CATransition tra = gen[0];
					String[] lab = tra.getLabel(); 
					int pr1=-1;
					for (int ind2=0;ind2<lab.length;ind2++)
					{
						if (lab[ind2]!=CATransition.idle)
						{
							pr1=ind2; //principal
						}
					}
					String label = tra.getLabel()[pr1];  //the action of the principal who moves
					for (int ind3=0;ind3<gen.length;ind3++)
					{
						for (int ind=0;ind<pointertemp;ind++)
							for(int ind2=0;ind2<temp[ind].length;ind2++)
							{	
								if(gen[ind3]!=null)
								{
									if (Arrays.equals(gen[ind3].getSource().getState(),temp[ind][ind2].getSource().getState()) &&  //the state is the same
											label==temp[ind][ind2].getLabel()[pr1]) //pr1 makes the same move
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
		Set<FMCATransition> finalTr = new HashSet<FMCATransition>(pointertransprod);
		for (int ind=0;ind<pointertransprod;ind++)
			finalTr.add(transprod[ind]);
		
		
				
		FMCA prod = new FMCA(prodrank,new CAState(initialprod, true,false),
				finalstatesprod, finalTr,CAState.extractCAStatesFromTransitions(finalTr)); 
		
		prod.setReachableAndSuccessfulStates();
		prod.removeDanglingTransitions();
		
		return prod;
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
	private static FMCATransition[] generateTransitions(FMCATransition t, FMCATransition tt, int i, int ii, FMCA[] aut)
	{
		/**
		 * preprocessing to the recursive method recgen:
		 * it computes  the values firstprinci,firstprincii,numtransitions,states
		 */
		int prodrank = 0; //the sum of rank of each CA in aut, except i and ii
		int firstprinci=-1; //index of first principal in aut[i] in the list of all principals in aut
		int firstprincii=-1; //index of first principal in aut[ii] in the list of all principals in aut
		int[] states=null; //the number of states of each principal, except i and ii
		int productNumberOfStatesExceptIandII=1; //contains the product of the number of states of each principals, except for those of i and ii
		

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
						int[] statesprinc=aut[ind].getNumStatesPrinc();
						for(int ind2=0;ind2<statesprinc.length;ind2++)
							{						
								states[indstates]=statesprinc[ind2];
								productNumberOfStatesExceptIandII*=states[indstates];
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
						int[] statesprinc=aut[ind].getNumStatesPrinc();
						for(int ind2=0;ind2<statesprinc.length;ind2++)
							{						
								states[indstates]=statesprinc[ind2];
								productNumberOfStatesExceptIandII*=states[indstates];
								indstates++;
							}
					}
				}	
			}
		}
		
		FMCATransition[] tr = new FMCATransition[productNumberOfStatesExceptIandII];//TODO: check if it is the right upperbound
		
		aut= FMCAUtil.extractAllPrincipals(aut);//TODO this must be shift to method composition, to be called only once!
		
		
		if(prodrank!=0)
		{
			int[] insert= new int[states.length];
			//initialize insert to zero in all component
			for (int ind=0;ind<insert.length;ind++)
				insert[ind]=0;
			recGen(t,tt,firstprinci, firstprincii,tr,states,0, states.length-1, insert,aut); //first call insert = [0,0,0, ...,0]
		}
		else
			tr[0]=t.generateATransition(t,tt,0,0,new int[0],aut);
		return tr;
	}
	
	
	/**
	 * 
	 * recursive method that generates all combinations of transitions with all possible states of principals that are idle 
	 * it must start from the end of array states
	 * 
	 * 
	 * example of evolution considering  array insert where states = [2,2,2], the dot is indstates the array is insert
	 *  
	 * [0,0,0.] -> C1 : [0,0,1.] -> C1 : [0,0,2.] -> C2 : [0,0.,0] -> C3 : [0,1,0.]     
	 * -> C1 : [0,1,1.] -> C1 : [0,1,2.] -> C2 : [0,1.,0] -> C3 : [0,2,0.]
	 * -> C1 : [0,2,1.] -> C1 : [0,2,2.] -> C2 : [0,2.,0] -> C2 : [0.,0, 0] -> C3 : [1, 0, 0.]
	 * -> C1 : [1, 0, 1.] ->  ... -> C2 [2, 2, 2] (indstates=-1) -> C4 termination
	 * 
	 * 
	 * @param t		first transition who moves
	 * @param tt	second transition who moves or null if it is not a match
	 * @param fi	offset of first CA who moves in list of principals
	 * @param fii	offset of second CA who moves in list of principals or empty
	 * @param cat	side effect: modifies cat by adding the generated transitions   -->modified at each iteration
	 * @param states	the number of states of each idle principal
	 * @param indcat	pointer in the array cat, the first call must be 0			-->modified at each iteration
	 * @param indstates	pointer in the array states, the first call must be states.length-1 	-->modified at each iteration
	 * @param insert    it is used to generate all the combinations of states of idle principals, the first must be all zero  -->modified at each iteration
	 * @param aut		array of automata, it is used in generateATransition of FMCA to retrieve the states of idle principals using insert as pointer  --> not modified
	 */
	private static void recGen(FMCATransition t, FMCATransition tt, int fi, int fii, FMCATransition[] cat,  int[] states, int indcat, int indstates, int[] insert, FMCA[] aut)
	{
		if (indstates==-1) //C4
			return;
		if (insert[indstates]==states[indstates]) /// C2
		{
			insert[indstates]=0;
			indstates--;
			recGen(t,tt,fi,fii,cat,states,indcat,indstates,insert,aut);
		}
		else
		{
			if (indstates==states.length-1)//C1   first calls
			{
				cat[indcat]=t.generateATransition(t,tt,fi,fii,insert,aut); //here insert contains the states of the idle principals in the transition
				indcat++;
				insert[indstates]++;
				recGen(t,tt,fi,fii,cat,states,indcat,indstates,insert,aut);
			}
			else  //C3
			{
				insert[indstates]++; 
				if (insert[indstates]!=states[indstates])
					indstates=states.length-1;
				recGen(t,tt,fi,fii,cat,states,indcat,indstates,insert,aut);				
			}
		}
	}
	
	
	/**
	 * 
	 * Generates all possible combinations of the states in fin, stored in modif. Here for indmod I used 
	 * an array where I always read the element indmod[0] instead of directly passing an integer.
	 * 
	 * very similar to recGen for transitions. The only difference is that here instead of generateATransition a novel state 
	 * is added to modif, where basically the array insert generates all combinations of final states.
	 * 
	 * @param fin	the array of final states of each principal
	 * @param modif		the array of final states of the composition, modified by side effect
	 * @param states	states[i] = fin[i].length
	 * @param indmod	index in modif, the first call must be 0		modified by the method
	 * @param indstates		the index in states, the first call must be states.length-1		modified by the method
	 * @param insert	it is used to generate all the combinations of final states, the first call must be all zero		modified by the method
	 */
	public static void recGen(int[][] fin, int[][] modif,  int[] states, int indmod[], int indstates[], int[] insert)
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
	 * TODO this method needs to be tested again
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
			int[][] fs=aut[i].getFinalStatesofPrincipals();
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
			aut[i].setFinalStatesofPrincipals(newfs); //TODO I modified this method, not tested
		}
		for (int i=0;i<aut.length;i++)
		{
			if (aut[i].getRank()!=rank)
				return null;
			
			//renaming states of operands
//			CAState initial=aut[i].getInitialCA().clone();
//			for (int z=0;z<initial.getState().length;z++)
//				initial.getState()[z]=initial.getState()[z]+upperbound*(i+1);
//			aut[i].setInitialCA(initial); 
			//TODO check I changed the setInitial method, now there is no more initial state instance variable
			Set<FMCATransition> tr=aut[i].getTransition();
			for (FMCATransition t : tr)
			{
				CAState source=t.getSource(); //TODO check this clone operations are decoupling the states of transitions
														  //with the states of the FMCA
				CAState target=t.getTarget();
				for (int z=0;z<source.getState().length;z++)
				{
					source.getState()[z] = source.getState()[z] + upperbound*(i+1);
					target.getState()[z] = target.getState()[z] + upperbound*(i+1);
				}
				t.setSource(source);
				t.setTarget(target);
			}
			
			//repositioning states and renaming
			CAState[] fst=aut[i].getStates().toArray(new CAState[] {});
			CAState[] newfst=new CAState[fst.length];
			for (int j=0;j<fst.length;j++)
			{
				int[] value=Arrays.copyOf(fst[j].getState(),fst[j].getState().length);
				for (int z=0;z<value.length;z++)
					value[z]=value[z] + upperbound*(i+1); //rename state TODO this is already done if not cloned
				newfst[j]=new CAState(value, fst[j].getX()+fur*(i)+25*i, fst[j].getY()+50, //repositioning
						fst[j].isInitial(),fst[j].isFinalstate()); //TODO not clear why I instantiate a new state	
			}
			aut[i].setStates(new HashSet<CAState>(Arrays.asList(newfst)));
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
			tr[i]=aut[i].getTransition().toArray(new FMCATransition[] {});
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
		
		int[] states = new int[rank]; //TODO remove
		int[] finalstateslength = new int[rank];
		for (int i=0;i<rank;i++)
		{
			states[i]=0;
			finalstateslength[i]=0; //initialise
		}
		int numoffstate=0; //the overall sum of fmcastates of all operands
		for (int i=0;i<aut.length;i++)
		{
			numoffstate+=aut[i].getStates().size();
			int[][] fs = aut[i].getFinalStatesofPrincipals();
			for (int j=0;j<rank;j++)
			{
				states[j]+= aut[i].getNumStatesPrinc()[j]; //sum of states		
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
			int[][] fs = aut[i].getFinalStatesofPrincipals();
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
			CAState[] so = aut[i].getStates().toArray(new CAState[] {});
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
	
		return new FMCA(rank, finitial, //states, 
				finalstates, 
				new HashSet<FMCATransition>(Arrays.asList(uniontr)), 
				new HashSet<CAState>(Arrays.asList(ufst)));
	}
	
	
	/**
	 * only used by generateTransitions
	 * 
	 * @param aut
	 * @return
	 */
	private static FMCA[] extractAllPrincipals(FMCA[] aut)
	{
		FMCA[][] principals = new FMCA[aut.length][];
		int allprincipals=0;
		for (int j=0;j<principals.length;j++)
		{
			principals[j] = new FMCA[aut[j].getRank()]; //extracting principals of aut[j]
			for (int i=0;i<principals[j].length;i++)
			{
				principals[j][i] = aut[j].proj(i);
			}
			
			 //TODO: there are idle transitions in principals, to be fixed in the future,
			//now this method is only used for the states of principals not their transitions
			allprincipals+=principals[j].length;
		}
		FMCA[] onlyprincipal = new FMCA[allprincipals];
		int countonlyprincipal=0;
		for (int j=0;j<principals.length;j++)
		{
			for (int z=0;z<principals[j].length;z++)
			{
				onlyprincipal[countonlyprincipal]=principals[j][z];
				countonlyprincipal++;
			}
		}
		return onlyprincipal;
	}
	
	private static float furthestNodesX(FMCA[] aut)
	{
		return (float)Arrays.stream(aut)
				.mapToDouble(x ->  x.getStates().parallelStream()
									.mapToDouble(CAState::getX)
									.max()
									.getAsDouble()
						)
				.max()
				.getAsDouble();
	}
	
	public static <T> T[] setUnion(T[] q1, T[] q2, T[] type)
	{
		List<T> t= new ArrayList<T>(Arrays.asList(q1));
		t.addAll(Arrays.asList(q2));
		
		return t.stream()
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList())
				.toArray(type);
	}
	
	public static <T> T[] setIntersection(T[] q1, T[] q2, T[] type)
	{
		if (q1==null || q2==null)
			return null;
		return Arrays.stream(q1)
				.filter(Objects::nonNull)
				.filter(x-> contains(x,q2))
				.collect(Collectors.toList())
				.toArray(type);
	}
	
	/**
	 * @return  q1 / q2
	 */
	public static <T> T[] setDifference(T[] q1, T[] q2, T[] type)
	{
		return Arrays.stream(q1)
				.distinct()
				.filter(Objects::nonNull)
				.filter(x -> !contains(x,q2))
				.collect(Collectors.toList())
				.toArray(type);
	}
	
	
	public static <T> int indexContains(T q, T[] listq)
	{
		if (q==null||listq==null) 
			return -1;
		return Arrays.asList(listq)
					.indexOf(q);
	}

	public static <T> int getIndex(T[] q, T e)
	{
		if (e==null||q==null)
			return -1;
		else
			return Arrays.asList(q)
					.indexOf(e);
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
	
	public static <T> boolean contains(T q, T[] listq, int listlength)
	{
		if (listq==null||q==null)
			return false;
		return Arrays.asList(listq)
				.subList(0, listlength)
				.indexOf(q)!=-1;
	}
	

	public static <T> T[] removeDuplicates(T[] m, T[] type)
	{
		if (m==null) 
			return null;
		
		return 	Arrays.stream(m)
				.filter(Objects::nonNull)
				.distinct()
				.collect(Collectors.toList())
				.toArray(type);
	}


	public static <T> T[] removeTailsNull(T[] q, int length, T[] type)
	{
		return  Arrays.stream(q)
				.limit(length)
				.collect(Collectors.toList())
				.toArray(type);
	}
	
	public static <T> T[] removeHoles(T[] l, T[] type)
	{
		if (l==null)
			return null;
		return (T[]) Arrays.stream(l)
				.filter(Objects::nonNull)
				.collect(Collectors.toList())
				.toArray(type);
	}

	//from now onward are duplicate methods for primitive types
	//TODO change arrays to collections
	
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

	public static int indexContains(int[] q, int[][] listq)
	{
		for (int i=0;i<listq.length;i++)
		{
			if (Arrays.equals(q, listq[i]))
					return i;
		}
		return -1;
	}
	public static boolean contains(int q, int[] listq,int listlength)
	{
		for (int i=0;i<listlength;i++)
		{
				if (q==listq[i])
					return true;
		}
		return false;
	}
	
	/**
	 * difficult to convert as generic, using 
	 * Array.asList().stream()....map(Arrays::asList)
	 * was giving List<List<int[]>> instead of List<List<Integer>>
	 */
	public static int[][] setIntersection(int[][] q1, int[][] q2)
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
		m=removeTailsNull(m,p, new int[][] {});
		return m;
	}
	
	/**
	 * 	if (q1 instanceof int[][]) {
			return removeDuplicates(q1);
		}
		does not work
	 */
	public static int[][] setUnion(int[][] q1, int[][] q2)
	{
		int[][] m= new int[q1.length+q2.length][];
		for (int i=0;i<m.length;i++)
		{
			if (i<q1.length)
				m[i]=q1[i];
			else
				m[i]=q2[i-q1.length];
		}
		m=FMCAUtil.removeDuplicates(m);
		return m;
	}
	
	/**
	 * 
	 *  streams distinct() does not use Arrays equals
	 */
	public static int[][] removeDuplicates(int[][] m)
	{
		//int removed=0;
		for (int i=0;i<m.length;i++)
		{
			for (int j=i+1;j<m.length;j++)
			{
				if ((m[i]!=null)&&(m[j]!=null)&&(Arrays.equals(m[i],m[j])))
				{
					m[j]=null;
		//			removed++;
				}
			}
		}
		m= removeHoles(m, new int[][] {}); 
		return m;
	}	


}
	
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