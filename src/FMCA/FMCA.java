package FMCA;
import java.util.Arrays;
import java.util.List;

import CA.CA;
import CA.CAState;
import CA.CATransition;


/** 
 * Class implementing a Featured Modal Service Contract Automaton and its functionalities
 * The class is under construction, some functionalities are not yet updated
 * 
 * 
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class FMCA  extends CA implements java.io.Serializable
{
	
	private CAState[] fstates;
	private Family family; 
	//TODO: add generation of products from a feature constraint, 
	//       the family now contains all valid products, they are generated and can be imported from a feature model in FeatureIDE
	
	public FMCA(int rank, CAState initial, int[] states, int[][] finalstates,FMCATransition[] trans)
	{
		super(rank,initial,states,finalstates,trans);
	}
	
	public FMCA(int rank, CAState initial, int[] states, int[][] finalstates, FMCATransition[] trans, CAState[] fstates)
	{

		super(rank,initial,states,finalstates,trans);
		this.fstates=fstates;
	}
	
	public FMCA(int rank, CAState initial, int[][] states, int[][] finalstates, FMCATransition[] trans, CAState[] fstate)
	{

		super(rank,initial,FMCA.numberOfPrincipalsStates(FMCAUtil.setUnion(states, finalstates, new int[][] {})),
				FMCA.principalsFinalStates(finalstates),trans);
		System.out.println("");
		this.fstates=fstate;
	}
	
	public void setFamily(Family f)
	{
		this.family=f;
	}
	
	public void setState(CAState[] s)
	{
		this.fstates=s;
	}
	
	public CAState[] getState()
	{
		return this.fstates;
	}
	
	public Family getFamily()
	{
		return family;
	}
	
	public boolean containAction(String act)
	{
		String[] actions = this.getActions();
		return FMCAUtil.contains(act, actions);
	}
	
	
	/**
	 * @return	the array of transitions
	 */
	public  FMCATransition[] getTransition()
	{
		CATransition[] temp = super.getTransition();
		FMCATransition[] t = new FMCATransition[temp.length];
		for (int i=0;i<temp.length;i++)
				t[i]=(FMCATransition)temp[i];
		return t;
	}
	
	
	/**
	 * 
	 * @return  the x coordinate of the furthest state (to the right)
	 */
	public float furthestNodeX()
	{
		float max=0;
		for (int i=0;i<fstates.length;i++)
		{
			if (max<fstates[i].getX())
				max=fstates[i].getX();
		}
		return max;
	}
	
	/**
	 * @return	copy Transitions
	 */
	public  FMCATransition[] copyTransition()
	{
		FMCATransition[] at = this.getTransition();
		FMCATransition[] finalTr = new FMCATransition[at.length];
		for(int i=0;i<finalTr.length;i++)
		{
			CAState in=at[i].getSourceP();
			String[] l=at[i].getLabelP();
			CAState out= at[i].getTargetP();
			//TODO this is not good, the CAState of transitions should point to fstates field of the FMCA,
			//		I removed the clone operation, previously an Arrays.copy operation was also called
			//finalTr[i] = new FMCATransition(in.clone(),Arrays.copyOf(l,l.length),f.clone(),at[i].getType());
			finalTr[i] = new FMCATransition(in,Arrays.copyOf(l,l.length),out,at[i].getType());
		}
		return finalTr;
	}
	
	/**
	 * compared to CA this method also clones the must transitions
	 * @return a new object CA clone
	 */
	public FMCA clone()
	{
		CAState[] clonefstates= this.getState();
		if (fstates!=null)
		{
			for (int i=0;i<clonefstates.length;i++)
			{
				clonefstates[i]=clonefstates[i].clone();
			}
			//TODO: call copyTransitions method and use inherited method
			FMCATransition[] at = this.getTransition();
			FMCATransition[] finalTr = new FMCATransition[at.length];
			for(int i=0;i<finalTr.length;i++)
			{
				CAState in=at[i].getSourceP();
				String[] l=at[i].getLabelP();
				CAState out= at[i].getTargetP();
				in = CAState.getCAStateWithValue(in.getState(), clonefstates);  //retrieve cloned states
				out = CAState.getCAStateWithValue(out.getState(), clonefstates);
				finalTr[i] = new FMCATransition(in,Arrays.copyOf(l,l.length),out,at[i].getType());
			}	
			int[][] finalstates=getFinalStatesCA();
			int[][] nf = new int[finalstates.length][];
			for (int i=0;i<finalstates.length;i++)
				nf[i]=Arrays.copyOf(finalstates[i], finalstates[i].length);
			return new FMCA(getRank(),
					 CAState.getCAStateWithValue(getInitialCA().getState(),clonefstates), 
					 Arrays.copyOf(getStatesCA(), getStatesCA().length), 
					 finalstates,
					 finalTr,
					 clonefstates); 
		}
		else
		{
			//TODO probably this should be fixed
			FMCATransition[] at = this.getTransition();
			FMCATransition[] finalTr = new FMCATransition[at.length];
			for(int i=0;i<finalTr.length;i++)
			{
				CAState in=at[i].getSourceP();
				String[] l=at[i].getLabelP();
				CAState f= at[i].getTargetP();
				finalTr[i] = new FMCATransition(in.clone(),Arrays.copyOf(l,l.length),f.clone(),at[i].getType());
			}	
			int[][] finalstates=getFinalStatesCA();
			int[][] nf = new int[finalstates.length][];
			for (int i=0;i<finalstates.length;i++)
				nf[i]=Arrays.copyOf(finalstates[i], finalstates[i].length);
			return new FMCA(getRank(),getInitialCA().clone(), 
					 Arrays.copyOf(getStatesCA(), getStatesCA().length), 
					 finalstates,
					 finalTr);
		}		
	}
	
	
	/**
	 * compute the projection on the i-th principal
	 * @param indexprincipal		index of the FMCA
	 * @return		the ith principal
	 */
	public FMCA proj(int indexprincipal)
	{
		if ((indexprincipal<0)||(indexprincipal>this.getRank())) //check if the parameter i is in the rank of the FMCA
			return null;
		if (this.getRank()==1)
			return this;
		FMCATransition[] tra = this.getTransition();
		int[] numberofstatesprincipal= new int[1];
		numberofstatesprincipal[0]= this.getStatesCA()[indexprincipal];
		FMCATransition[] transitionsprincipal = new FMCATransition[tra.length];
		int pointer=0;
		for (int ind=0;ind<tra.length;ind++)
		{
			FMCATransition tt= ((FMCATransition)tra[ind]);
			String label = tt.getLabelP()[indexprincipal];
			if(label!=CATransition.idle)
			{
				int source =  tt.getSourceP().getState()[indexprincipal];
				int dest = tt.getTargetP().getState()[indexprincipal];
				int[] sou = new int[1];
				sou[0]=source;
				int[] des = new int[1];
				des[0]=dest;
				String[] lab = new String[1];
				lab[0]=label;
				FMCATransition selected = null;
				if (label.substring(0,1).equals(CATransition.offer))
				{
					selected = new FMCATransition(new CAState(sou),lab, new CAState(des),FMCATransition.action.PERMITTED);
				}
				else {
					selected = new FMCATransition(new CAState(sou),lab, new CAState(des),tt.getType());
				}
				
				if (!FMCAUtil.contains(selected, transitionsprincipal, pointer))
				{
					transitionsprincipal[pointer]=selected;
					pointer++;
				}
			}
		}
		
		transitionsprincipal = FMCAUtil.removeTailsNull(transitionsprincipal, pointer, new FMCATransition[] {});
		CAState[] fstates = CAState.extractCAStatesFromTransitions(transitionsprincipal);
		int[] init=new int[1]; init[0]=0;
		CAState initialstateprincipal = CAState.getCAStateWithValue(init, fstates);
		initialstateprincipal.setInitial(true);  //if is dangling will throw exception
		int[][] finalstatesprincipal = new int[1][];
		finalstatesprincipal[0]=this.getFinalStatesCA()[indexprincipal];
		for (int ind=0;ind<finalstatesprincipal[0].length;ind++)
		{
			int[] value=new int[1]; value[0]=finalstatesprincipal[0][ind];
			CAState.getCAStateWithValue(value, fstates).setFinalstate(true); //if is dangling will throw exception
		}
		
		return new FMCA(1,initialstateprincipal,numberofstatesprincipal,finalstatesprincipal,transitionsprincipal,fstates); 
	}
	
	
	/**
	 * @return
	 */
	public FMCA[] allPrincipals()
	{
		
		FMCA[] principals = new FMCA[this.getRank()];
		for (int i=0;i<principals.length;i++)
		{
			principals[i] = this.proj(i);
		}
		return principals;
	}
	
	/**
	 * compute the orchestration (as most permissive controller) of product p
	 * 
	 * @return the most permissive controller in agreement
	 */
	public FMCA mpc(Product p)
	{
		FMCA a = this.clone();
		FMCATransition[] tr = a.getTransition();
		FMCATransition[] rem = new FMCATransition[tr.length];  //solo per testing
		//int[][] fs=a.allFinalStates();
		int removed = 0;
		
		//I need to store the transitions, to check later on 
		//if some controllable transition becomes uncontrollable (i.e. semi-controllable)
		FMCATransition[] potentiallyUncontrollable = new FMCATransition[tr.length]; 
		int potentiallyUncontrollableCounter = 0;
		
		FMCATransition[] badtransitions=new FMCATransition[tr.length]; 
		int badtransitioncounter=0;
		
		//I need a copy of the actual transitions of K_i because in the loop I remove transitions 
		//and this operation affects the set of uncontrollable transitions in K_i
		FMCATransition[] trcopy=a.copyTransition();
		
		
		for (int i=0;i<tr.length;i++)
		{
		//	System.out.println("transition "+i);
			if (!tr[i].isUncontrollable(a)) //controllable and bad
			{
				if (tr[i].isRequest()||tr[i].isForbidden(p))
				{
					rem[removed]=tr[i]; //only for testing
					trcopy[i] = null;
					removed++;
				}
			}
			else 
			{	
				if (tr[i].isRequest()||tr[i].isForbidden(p))
				{
					badtransitions[badtransitioncounter]= new FMCATransition(tr[i].getSourceP(),tr[i].getLabelP(),tr[i].getTargetP(),tr[i].getType());
					badtransitioncounter++;		
				}
	
			}
			if(	(tr[i].isGreedy()&&tr[i].isRequest())	||	(tr[i].isLazy()))
			{
				potentiallyUncontrollable[potentiallyUncontrollableCounter]= new FMCATransition(tr[i].getSourceP(),tr[i].getLabelP(),tr[i].getTargetP(),tr[i].getType());
				potentiallyUncontrollableCounter++;		
			}
		}
		
		tr=trcopy;
		tr=  FMCAUtil.removeHoles(tr,new FMCATransition[] {}); //, removed);		
		a.setTransition(tr); //K_0 
	
		badtransitions=FMCAUtil.removeTailsNull(badtransitions, badtransitioncounter, new FMCATransition[] {});
		potentiallyUncontrollable = FMCAUtil.removeTailsNull(potentiallyUncontrollable, potentiallyUncontrollableCounter, new FMCATransition[] {});
		
		//
		CAState[] unmatchedOrLazyunmatchable=new CAState[potentiallyUncontrollable.length];
		CAState[] R=FMCAUtil.setUnion(a.getDanglingStates(), FMCATransition.getSources(badtransitions), new CAState[] {}); //R_0
		boolean update=false;
		if (R.length>0)
		{
			do{
				update=false;
				FMCATransition[] trcheck= new FMCATransition[tr.length*R.length];//used for storing all uncontrollable transitions without bad source state
				int trcheckpointer=0;
				removed=0;
				rem= new FMCATransition[tr.length]; 
				
				//I need a copy of the actual transitions of K_i because in the loop I remove transitions 
				//and this operation affects the set of uncontrollable transitions in K_i
				trcopy=a.copyTransition();
				for (int i=0;i<tr.length;i++)  //for all transitions
				{
					if (!(tr[i]==null))
					{
						if (tr[i].isUncontrollable(a)) 
						{   
							if (FMCAUtil.contains(tr[i].getSourceP(), R)) //remove uncontrollable with bad source
							{
								rem[removed]=tr[i];//solo per testing
								trcopy[i]=null;
								removed++;
								update=true;
							}
							else
							{
								trcheck[trcheckpointer]=tr[i]; //store all uncontrollable transitions without bad source state
								trcheckpointer++;
							}
						}
						else if //(!tr[i].isUncontrollable(a)&&   you already know that this is true because of the else
								(FMCAUtil.contains(tr[i].getTargetP(), R)) //remove controllable with bad target
						{
							rem[removed]=tr[i]; //only for testing
							trcopy[i]=null;
							removed++;
							update=true;
						}
					}
				} 
				tr=trcopy;
				tr=  FMCAUtil.removeHoles(tr,new FMCATransition[] {}); //, removed);
				a.setTransition(tr);  //K_i
				//
				//
				// building R_i
				//
				//
				CAState[] danglingStates = a.getDanglingStates();
				CAState[] newR=new CAState[trcheckpointer];
				int newRpointer=0;
				
				for (int i=0;i<trcheckpointer;i++)//for all uncontrollable transitions without bad source state
				{
					//if target state is bad,  add source state to R if it has not been already added, we know that source state is not in R
					// setUnion removes duplicates we could skip the check
					if ((FMCAUtil.contains(trcheck[i].getTargetP(), R)&&(!FMCAUtil.contains(trcheck[i].getSourceP(),R))))
					{
						newR[newRpointer]=trcheck[i].getSourceP();
						newRpointer++;
					}
				}
				//add dangling states to R
				CAState[] RwithDang =	FMCAUtil.setUnion(R ,danglingStates,new CAState[] {});
				
				if (RwithDang.length!=R.length)
				{
					R = RwithDang;
					update = true;
				}
				
				//add source states of uncontrollable transitions with redundant target to R
				if (newRpointer>0)
				{
					R=FMCAUtil.setUnion(R, FMCAUtil.removeTailsNull(newR, newRpointer, new CAState[] {}),new CAState[] {});
					update=true;
				}
				
				//add source states of uncontrollable transitions that were previously controllable
				CAState[] su= FMCATransition.areUnmatchedOrLazyUnmatchable(potentiallyUncontrollable, a);
				CAState[] newUnmatchedOrLazyunmatchable =	FMCAUtil.setUnion(unmatchedOrLazyunmatchable,su,new CAState[] {});
				if (newUnmatchedOrLazyunmatchable.length!=unmatchedOrLazyunmatchable.length)
				{
					unmatchedOrLazyunmatchable=newUnmatchedOrLazyunmatchable;
					R=FMCAUtil.setUnion(R, unmatchedOrLazyunmatchable,new CAState[] {});
					update=true;
				}
				
			}while(update);
		}
		
		//a.getDanglingStates();
		a = (FMCA) FMCAUtil.removeUnreachableTransitions(a);
		
		//if initial state is bad or not all required actions are fired
		if (FMCAUtil.contains(a.getInitialCA(), R)||(!p.checkRequired(a.getTransition())))
			return null;
		
		return a;
	}
		

	/**
	 * compute the choreography
	 * 
	 * 
	 * @return the choreography in strong agreement
	 */
	public FMCA choreography()
	{
		FMCA a = this.clone();
		FMCATransition[] tr = a.getTransition();
		FMCATransition[] rem = new FMCATransition[tr.length];  //only for testing
		//int[][] fs=a.allFinalStates();
		int removed = 0;
		
		//I need to store the transitions, to check later on 
		//if some controllable transition becomes uncontrollable
		FMCATransition[] potentiallyUncontrollable = new FMCATransition[tr.length]; 
		int potentiallyUncontrollableCounter = 0;
		
		FMCATransition[] badtransitions=new FMCATransition[tr.length]; 
		int badtransitioncounter=0;
		
		//I need a copy of the actual transitions of K_i because in the loop I remove transitions 
		//and this operation affects the set of uncontrollable transitions in K_i
		FMCATransition[] trcopy=a.copyTransition();
		
		//computing K_0 and R_0
		for (int i=0;i<tr.length;i++)
		{
		//	System.out.println("transition "+i);

			if (!tr[i].isUncontrollableChoreography(a)) //controllable and bad
			{
				if (!tr[i].isMatch())
				{
					rem[removed]=tr[i]; //solo per testing
					trcopy[i] = null;
					removed++;
				}
			}
			else 
			{	
				if (!tr[i].isMatch())
				{
					badtransitions[badtransitioncounter]= new FMCATransition(tr[i].getSourceP(),tr[i].getLabelP(),tr[i].getTargetP(),tr[i].getType());
					badtransitioncounter++;		
				}
	
			}
	
			if(	//(tr[i].isGreedy()&&tr[i].isRequest())	||	
					(tr[i].isLazy()))
			{
				potentiallyUncontrollable[potentiallyUncontrollableCounter]= new FMCATransition(tr[i].getSourceP(),tr[i].getLabelP(),tr[i].getTargetP(),tr[i].getType());
				potentiallyUncontrollableCounter++;		
			}
		}
		
		tr=trcopy;
		tr=  FMCAUtil.removeHoles(tr,new FMCATransition[] {}); //removed);		
		a.setTransition(tr); //K_0 
		
		/*//computing R_0
		for (int i=0;i<tr.length;i++)
		{
			if (tr[i].isUncontrollableChoreography(a) && (!tr[i].isMatch()))//||tr[i].isForbidden(p))) 	//uncontrollable and bad
			{	
				badtransitions[badtransitioncounter]= new FMCATransition(tr[i].getSourceP(),tr[i].getLabelP(),tr[i].getTargetP(),tr[i].getType());
				badtransitioncounter++;		
			}
			if(	//(tr[i].isGreedy()&&tr[i].isRequest())	||	
					(tr[i].isLazy()))
			{
				potentiallyUncontrollable[potentiallyUncontrollableCounter]= new FMCATransition(tr[i].getSourceP(),tr[i].getLabelP(),tr[i].getTargetP(),tr[i].getType());
				potentiallyUncontrollableCounter++;		
			}
		}*/

		badtransitions=FMCAUtil.removeTailsNull(badtransitions, badtransitioncounter, new FMCATransition[] {});
		potentiallyUncontrollable = FMCAUtil.removeTailsNull(potentiallyUncontrollable, potentiallyUncontrollableCounter, new FMCATransition[] {});
		
		//
		CAState[] unmatchedOrLazyunmatchable=new CAState[potentiallyUncontrollable.length];
		CAState[] R=FMCAUtil.setUnion(a.getDanglingStates(), FMCATransition.getSources(badtransitions),new CAState[] {}); //R_0
		boolean update=false;
		do{
			update=false;
			FMCATransition[] trcheck= new FMCATransition[tr.length*R.length];//used for storing all uncontrollable transitions without bad source state
			int trcheckpointer=0;
			removed=0;
			rem= new FMCATransition[tr.length]; 
			
			//I need a copy of the actual transitions of K_i because in the loop I remove transitions 
			//and this operation affects the set of uncontrollable transitions in K_i
			trcopy=a.copyTransition();
			for (int i=0;i<tr.length;i++)  //for all transitions
			{
				if (!(tr[i]==null))
				{
					if (tr[i].isUncontrollableChoreography(a)) 
					{   
						if (FMCAUtil.contains(tr[i].getSourceP(), R)) 
									//remove if uncontrollable with bad source 
						{
							rem[removed]=tr[i];//solo per testing
							trcopy[i]=null;
							removed++;
							update=true;
						}
						else
						{
							trcheck[trcheckpointer]=tr[i]; //store all uncontrollable transitions without bad source state 
							trcheckpointer++;
						}
					}
					else if //(!tr[i].isUncontrollableChoreography(a) 
							//&&
							((FMCAUtil.contains(tr[i].getTargetP(), R))) //remove controllable with bad target
					{
						rem[removed]=tr[i]; //solo per testing
						trcopy[i]=null;
						removed++;
						update=true;
					}
				}
			}
			
			/**
			 * a transition violating the branching condition is pruned after all other transitions have been pruned in that state.
			 */
			tr=trcopy;
			tr=  FMCAUtil.removeHoles(tr,new FMCATransition[] {}); //, removed);
			a.setTransition(tr);
			
			removed=0;
			rem= new FMCATransition[tr.length]; 
			trcopy=a.copyTransition();
			
			for (int i=0;i<tr.length;i++)  //for all transitions
			{
				if (!(tr[i]==null))
				{
					boolean violatesBranchingCondition = this.violatesBranchingCondition(tr[i],tr,R, a);
					if (violatesBranchingCondition) //remove if violates branching condition
					{
						rem[removed]=tr[i];//solo per testing
						trcopy[i]=null;
						this.violatesBranchingCondition(tr[i],tr,R, a);
						removed++;
						update=true;
						break;
					}
				}	
			}
			
			tr=trcopy;
			tr=  FMCAUtil.removeHoles(tr,new FMCATransition[] {}); //, removed);
			a.setTransition(tr);  //K_i
			
			
			//
			//
			// building R_i
			//
			//
			CAState[] danglingStates = a.getDanglingStates();
			CAState[] newR=new CAState[trcheckpointer];
			int newRpointer=0;
			
			for (int i=0;i<trcheckpointer;i++)//for all uncontrollable transitions without bad source state and that do not violates branching condition
			{
				//if target state is bad,  add source state to R if it has not been already added, we know that source state is not in R
				// setUnion removes duplicates we could skip the check
				if ((FMCAUtil.contains(trcheck[i].getTargetP(), R)&&(!FMCAUtil.contains(trcheck[i].getSourceP(),R))))
				{
					newR[newRpointer]=trcheck[i].getSourceP();
					newRpointer++;
				}
			}
			//add dangling states to R
			CAState[] RwithDang =	FMCAUtil.setUnion(R ,danglingStates, new CAState[] {});
			if (RwithDang.length!=R.length)
			{
				R = RwithDang;
				update=true;
			}
			
			//add source states of uncontrollable transitions with redundant (bad? dangling?) target to R
			if (newRpointer>0)
			{
				R=FMCAUtil.setUnion(R, FMCAUtil.removeTailsNull(newR, newRpointer, new CAState[] {}),new CAState[] {});
				update=true;
			}
			
			//add source states of uncontrollable transitions that were previously controllable
			CAState[] su= FMCATransition.areUnmatchedOrLazyUnmatchableChoreography(potentiallyUncontrollable, a);
			CAState[] newUnmatchedOrLazyunmatchable =	FMCAUtil.setUnion(unmatchedOrLazyunmatchable,su, new CAState[] {});
			if (newUnmatchedOrLazyunmatchable.length!=unmatchedOrLazyunmatchable.length)
			{
				unmatchedOrLazyunmatchable=newUnmatchedOrLazyunmatchable;
				R=FMCAUtil.setUnion(R, unmatchedOrLazyunmatchable, new CAState[] {});
				update=true;
			}
			
		}while(update);
		
		
		//a.getDanglingStates();
		a = (FMCA) FMCAUtil.removeUnreachableTransitions(a);
		
		//if initial state is bad or [not all required actions are fired [deprecated]]
		if (FMCAUtil.contains(a.getInitialCA(), R))//||(!p.checkRequired(a.getTransition())))
			return null;
		
		return a;
	}
	
	
	/**
	 * 
	 * @param t
	 * @return true if transition t violates the branching condition
	 */
	public boolean violatesBranchingCondition(CATransition t, CATransition[] tr, CAState[] R, FMCA a) 
	{
		
		CAState[] dang = a.getDanglingStates();
		CAState[] bad = FMCAUtil.setUnion(dang, R, new CAState[] {});
		if (FMCAUtil.contains(t.getSourceP(), bad) || FMCAUtil.contains(t.getTargetP(), bad))
			return false;		//ignore this transition because it is going to be pruned
		
		
		List<CAState> visit = Arrays.asList(FMCAUtil.setDifference(this.getState(), bad, new CAState[] {})); //TODO conversion Array-List many times
		if (t.isMatch())
		{

			int sender = t.getSender();  
			String[] label=t.getLabelP();
			for (CAState e : visit)
			{
				int[] state= e.getState();

				//for all (good) states with the sender in the same state as in t
				if (   (!Arrays.equals(state,t.getSourceP().getState())) //it's not the same state
						&&(state[sender]==t.getSourceP().getState()[sender])) //but sender is in the same state
						
				{
					int z=0;
					boolean found = false;
					while ((!found)&&(z<tr.length)) //see if from that state there exists the transition with same label
					{
						found=Arrays.equals(state, tr[z].getSourceP().getState())
								&& Arrays.equals(tr[z].getLabelP(), label);
						z++;
					}
					if (!found)
					{
						return true;  //if not branching condition is violated
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * an array containing the number of  states of each principal
	 * @param states  all the states of the MSCA enumerated
	 * @return
	 */
	public static int[] numberOfPrincipalsStates(int[][] states)
	{
		int[] rank = new int[states[0].length];
		for (int i=0;i<rank.length;i++)
		{
			int[] principalstates=new int[states.length];//upperbound
			for (int ind=0; ind<principalstates.length;ind++)
				principalstates[ind] = -1;    //the next loop will not work otherwise because 0 is the initialization value but can also be a state
			
			int count=0;
			for (int j=0;j<principalstates.length;j++)
			{
				if (!FMCAUtil.contains(states[j][i],principalstates,count))
				{
					principalstates[count]=states[j][i];
					count++;
				}
			}
			rank[i]=count;
		}
		
//		the old code was selecting the state with higher value, not working for states renaming (e.g. FMCA union)
//		for (int j=0;j<max.length;j++)
//			max[j]=-1;
//		for (int i=0;i<states.length;i++)
//		{
//			for (int j=0;j<max.length;j++)
//			{
//				if (max[j]<states[i][j])
//					max[j]=states[i][j];
//			}
//		}
//		for (int j=0;j<max.length;j++)
//			max[j]+=1;		
		return rank;
	}
	
	
	/**
	 * 
	 * @param all final states of the composed automaton
	 * @return the final states of each principal
	 */
	public static int[][] principalsFinalStates(int[][] states)
	{
		if (states.length<=0)
			return null;
		int rank=states[0].length;
		int[] count=new int[rank];
		int[][] pfs=new int[rank][states.length];

		for (int ind=0; ind<pfs.length;ind++)
			for (int ind2=0; ind2<pfs[ind].length;ind2++)
			pfs[ind][ind2] = -1;    //the check FMCAUtil.getIndex(pfs[j], states[i][j])==-1  will not work otherwise because 0 is the initialization value but can also be a state

		for (int j=0;j<rank;j++)
		{
			pfs[j][0]=states[0][j];
			count[j]=1;		//initialising count[j] and doing first iteration (I guess..)
		}
		for (int i=1;i<states.length;i++)
		{
			for (int j=0;j<rank;j++)
			{
				if (FMCAUtil.getIndex(pfs[j], states[i][j])==-1 )  // if states[i][j] is not in pfs[j]
				{
					pfs[j][count[j]]=states[i][j];
					count[j]++;
				}
			}
		}
		for (int j=0;j<rank;j++)
			pfs[j]=FMCAUtil.removeTailsNull(pfs[j], count[j]);
		return pfs;
	}
	
	/**
	 * 
	 * 
	 * @return all  states that appear in at least one transition
	 */
	public int[][] allStates()
	{
		FMCA aut=this.clone();
		int[][] s = new int[this.prodStates()+1][]; //there could be a dummy initial state
		s[0]=aut.getInitialCA().getState();
		FMCATransition[] t = aut.getTransition();
		int pointer=1;
		for (int i=0;i<t.length;i++)
		{
			int[] start = t[i].getSourceP().getState();
			int[] arr = t[i].getTargetP().getState();
			
			if (!FMCAUtil.contains(arr, s))
			{
				s[pointer]=arr;
				pointer++;
			}
			if (!FMCAUtil.contains(start, s))
			{
				s[pointer]=start;
				pointer++;
			}
		}
		s=FMCAUtil.removeTailsNull(s, pointer, new int[][] {});
//	    int[][] f = new int[pointer][];
//	    for (int i=0;i<pointer;i++)
//	    	f[i]=s[i];
		return s;
	}
	
	
	public int getStates()
	{
		return this.allStates().length;
	}
	

	/**
	 * 
	 * 
	 * @return all the final states of the CA
	 */
	public  int[][] allFinalStates()
	{
//		if (rank==1)
//			return finalstates;
		int[][] finalstates = this.getFinalStatesCA();
		int[] states=new int[finalstates.length];
		int comb=1;
		int[] insert= new int[states.length];
		for (int i=0;i<states.length;i++)
		{
			states[i]=finalstates[i].length;
			comb*=states[i];
			insert[i]=0;
		}
		int[][] modif = new int[comb][];
		int[] indstates = new int[1];
		indstates[0]= states.length-1;
		int[] indmod = new int[1];
		indmod[0]= 0; 
		
		FMCAUtil.recGen(finalstates, modif,  states, indmod, indstates, insert);  
		return modif;
	}
	
	public int[][][] allNonFinalAndFinalStates()
	{
		int[][][] r = new int[2][][];
		int[][] states=this.allStates();
		int[][] finalstates=FMCAUtil.setIntersection(states, this.allFinalStates());//only reachable final states
		int[][] nonfinalstates=FMCAUtil.setDifference(states, finalstates, new int[][] {});
		
		r[0]=nonfinalstates;
		r[1]=finalstates;
		return r;
	}
	
	/**
	 * 
	 * @return all actions present in the automaton
	 */
	public String[] getActions()
	{
		FMCATransition[] tr=this.getTransition();
		String[] act = new String[tr.length];
		for (int i=0;i<tr.length;i++)
			act[i]=	CATransition.getUnsignedAction(tr[i].getAction());
		act=FMCAUtil.removeDuplicates(act, new String[] {});
		return act;
	}
	
	/**
	 * return dangling states who do not reach a final state or are unreachable
	 * not inherited from CA
	 * @return	redundant states of at
	 */
	protected CAState[] getDanglingStates()
	{
		
//		int[][] fs = this.allFinalStates();
//		int[][] redundantStates = new int[this.prodStates()][];
//		//int[][] allStates = this.allStates();		
//		int redundantStatesPointer=0;
//		this.setReachableStates();
//		for (int ind=0;ind<this.fstates.length;ind++) //for all states
//		{
//				//TODO check if it is possible to check reachability from initial state only once
//				// for each state checks if it reaches one of the final states  and if it is reachable from the initial state
//				int[] pointervisited = new int[1];
//				pointervisited[0]=0;
//				
//				//I need to check the reachability from initial state only once!
//				boolean remove=!FMCAUtil.amIReachable(allStates[ind],this,getInitialCA().getState(),new int[this.prodStates()][],
//						pointervisited,null,null,0,0);  	
//				
//				if (fstates[ind].isSetReachable())//!remove) //if it is reachable from initial state
//				{
//					remove=true;  // at the end of the loop if remove=true none of final states is reachable
//					for (int i=0;i<fs.length;i++)
//					{
//						pointervisited = new int[1];
//						pointervisited[0]=0;
//						if((FMCAUtil.amIReachable(fs[i],this,allStates[ind],new int[this.prodStates()][],pointervisited,
//								null,null,0,0)&&remove))  
//							remove=false;
//					}
//				}
//				if ((remove))
//				{
//					redundantStates[redundantStatesPointer]=fstates[ind].getState();
//					redundantStatesPointer++;
//				}													
//		}
//		//remove null space in array redundantStates
//		redundantStates = FMCAUtil.removeTailsNull(redundantStates, redundantStatesPointer);
//		
//		return redundantStates;
		this.resetReachableAndSuccessfulStates();
		this.setReachableAndSuccessfulStates();
		CAState[] dang=new CAState[fstates.length];
		
		int dangcounter=0;
		for (int i=0;i<dang.length;i++)
		{
			if (!(fstates[i].isReachable()&&fstates[i].isSuccessfull()))
			{
				dang[dangcounter]=fstates[i];
				dangcounter++;
			}	
		}
		return FMCAUtil.removeTailsNull(dang, dangcounter, new CAState[] {});
	}
	
	/**
	 * this method is not inherited from MSCA
	 * @return	all the  must transitions request that are not matched 
	 */
	protected  FMCATransition[] getUnmatch()
	{
		FMCATransition[] tr = this.getTransition();
		int[][] fs=this.allFinalStates();
		int pointer=0;
		CAState[] R=this.getDanglingStates();
		FMCATransition[] unmatch = new FMCATransition[tr.length];
		for (int i=0;i<tr.length;i++)
		{
			if ((tr[i].isRequest())
				&&((tr[i].isMust())
				&&(!FMCAUtil.contains(tr[i].getSourceP().getState(), fs)))) // if source state is not final
			{
				boolean matched=false;
				for (int j=0;j<tr.length;j++)	
				{
					if ((tr[j].isMatch())
						&&(tr[j].isMust())
						&&(tr[j].getReceiver()==tr[i].getReceiver())	//the same principal
						&&(tr[j].getSourceP().getState()[tr[j].getReceiver()]==tr[i].getSourceP().getState()[tr[i].getReceiver()]) //the same source state					
						&&(tr[j].getLabelP()[tr[j].getReceiver()]==tr[i].getLabelP()[tr[i].getReceiver()]) //the same request
						&&(!FMCAUtil.contains(tr[i].getSourceP(), R))) //source state is not redundant
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
			unmatch = FMCAUtil.removeTailsNull(unmatch, pointer, new FMCATransition[] {});
			return unmatch;
		}
		else
			return null;
	}

	private int[] getIndexOfLazyTransitions()
	{
		
		FMCATransition[] tr = this.getTransition();
		int[] arr = new int[tr.length];
		int count=0;
		for (int i=0;i< tr.length;i++)
		{
			if (tr[i].isLazy())
			{
				arr[count]=i;
				count++;
			}
		}
		arr = FMCAUtil.removeTailsNull(arr, count);
		return arr;
	}
	
	public String removeLazy()
	{
		int[] arr = this.getIndexOfLazyTransitions();
		int l = arr.length;
	//	long ll = (long) Math.pow(2.0, (double)arr.length);
		long ns = this.getStates()+1;
		//FMCA[] aut = new FMCA[(int)Math.pow(2.0, (double)arr.length)];
		return "The automaton contains the following number of lazy transitions : "+l+" \n"
				//+"There are 2^"+l+" possible combinations of removing such transitions.\n"
				+"The resulting automaton with only urgent transitions will have the following number of states ("+ns+") * (2^"+l+"-1)";
	}
		
	 public void setReachableAndSuccessfulStates()
	 {
		 visit(this.getInitialCA()); //firstly reachability must be set !
		 //TODO record the set of final states this is a fix
		 int[][] fs = this.allFinalStates();
		 for (int i=0; i<fs.length; i++)
		 {
			 CAState f = CAState.getCAStateWithValue(fs[i], this.getState());
			 if (f!=null)//not all combinations of final states could be available (in case a controller is checked)
				 reverseVisit(f);
		 }
	 }
	 
	 public void resetReachableAndSuccessfulStates()
	 {
		 for (int i=0;i<fstates.length;i++)
		 {
			 fstates[i].setReachable(false);
			 fstates[i].setSuccessfull(false);
		 } 
	 }
	 
	/**
	 * each reachable states will be set
	 */
	public void setReachableStates()
	{
		visit(this.getInitialCA());
	}
	
	/**
	 * s = current state
	 * forall t in FS(s)
	 * 		if target(t) not visited
	 * 			visited += target(t); iterate( target(t))
	 * 		else
	 * 			do nothing
	 * 
	 */
	private void visit(CAState currentstate)
	{ 
		currentstate.setReachable(true);
		FMCATransition[] tr=FMCATransition.getTransitionFrom(currentstate, this.getTransition());
//		if (tr==null)
//		{
//			tr=FMCATransition.getTransitionFrom(currentstate, this.getTransition());
//		}
		for (int i=0;i<tr.length;i++)
		{
			CAState target=tr[i].getTargetP();
			if (!target.isReachable())
				visit(target);
		}
	}
	
	private void reverseVisit(CAState currentstate)
	{ 
		currentstate.setSuccessfull(true);
		FMCATransition[] tr=FMCATransition.getTransitionTo(currentstate, this.getTransition());
		for (int i=0;i<tr.length;i++)
		{
			CAState source=tr[i].getSourceP();
			if (source.isReachable()&&!source.isSuccessfull()) //warning: it requires to compute reachability
				reverseVisit(source);
		}
	}
	 
}