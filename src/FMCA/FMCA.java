package FMCA;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import CA.CAState;
import CA.CATransition;
import MSCA.MSCA;
import MSCA.MSCATransition;


/** 
 * Class implementing a Featured Modal Service Contract Automaton and its functionalities
 * The class is under construction, some functionalities are not yet updated
 * 
 * 
 * @author Davide Basile
 *
 */
public class FMCA  extends MSCA
{ 
	
	private Family family; 
	//TODO: add generation of products from a feature constraint, 
	//       the family now contains all valid products, they are generated and can be imported from a feature model in FeatureIDE


	public FMCA(int rank, CAState initial,  int[][] finalstates, Set<FMCATransition> tr, Set<CAState> states)
	{	
		super(rank, initial, finalstates, tr, states);
	}
	
	public FMCA(MSCA aut, Family f)	{	
		super(aut.getRank(), aut.getInitial(), aut.getFinalStatesofPrincipals(), aut.getTransition(), aut.getStates());
		this.family=f;
	}

	public void setFamily(Family f)
	{
		this.family=f;
	}

	public Family getFamily()
	{
		return family;
	}

	/**
	 * @return the synthesised orchestration/mpc of product p in agreement
	 */
	public FMCA orchestration(Product p)
	{
		MSCA a = synthesis(x-> {return (t,bad) -> 
		x.isRequest()||((FMCATransition) x).isForbidden(p)||bad.contains(x.getTarget());}, 
				x -> {return (t,bad) -> bad.contains(x.getTarget())&&x.isUncontrollableOrchestration(t, bad);});

		if (a!=null&&p.checkRequired(a.getTransition()))
			return new FMCA(a, this.getFamily());
		else
			return null;
	}	

	/**
	 * compute the projection on the i-th principal
	 * @param indexprincipal		index of the FMCA
	 * @return		the ith principal
	 * 
	 * @deprecated the XML representation must be fixed to store final states of principals for projection to work
	 */
	FMCA proj(int indexprincipal)
	{
		if ((indexprincipal<0)||(indexprincipal>this.getRank())) //TODO check if the parameter i is in the rank of the FMCA
			return null;
		if (this.getRank()==1)
			return this;
		FMCATransition[] tra = this.getTransition().toArray(new FMCATransition[] {});
		//int[] numberofstatesprincipal= new int[1];
		//numberofstatesprincipal[0]= this.getNumStatesPrinc()[indexprincipal];
		FMCATransition[] transitionsprincipal = new FMCATransition[tra.length];
		int pointer=0;
		for (int ind=0;ind<tra.length;ind++)
		{
			FMCATransition tt= ((FMCATransition)tra[ind]);
			String label = tt.getLabel()[indexprincipal];
			if(label!=CATransition.idle)
			{
				int source =  tt.getSource().getState()[indexprincipal];
				int dest = tt.getTarget().getState()[indexprincipal];
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

				if (!FMCAUtils.contains(selected, transitionsprincipal, pointer))
				{
					transitionsprincipal[pointer]=selected;
					pointer++;
				}
			}
		}

		transitionsprincipal = FMCAUtils.removeTailsNull(transitionsprincipal, pointer, new FMCATransition[] {});
		Set<FMCATransition> transitionprincipalset = new HashSet<FMCATransition>(Arrays.asList(transitionsprincipal));
		Set<CAState> fstates = CAState.extractCAStatesFromTransitions(transitionprincipalset);
		int[] init=new int[1]; init[0]=0;
		CAState initialstateprincipal = CAState.getCAStateWithValue(init, fstates);
		initialstateprincipal.setInitial(true);  //if is dangling will throw exception
		int[][] finalstatesprincipal = new int[1][];
		finalstatesprincipal[0]=this.getFinalStatesofPrincipals()[indexprincipal];
		for (int ind=0;ind<finalstatesprincipal[0].length;ind++)
		{
			int[] value=new int[1]; value[0]=finalstatesprincipal[0][ind];
			CAState.getCAStateWithValue(value, fstates).setFinalstate(true); //if is dangling will throw exception
		}

		return new FMCA(1,initialstateprincipal,
				finalstatesprincipal,transitionprincipalset,fstates); 
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

		float fur= (float)Arrays.stream(aut)
				.mapToDouble(x ->  x.getStates().parallelStream()
						.mapToDouble(CAState::getX)
						.max()
						.getAsDouble()
						)
				.max()
				.getAsDouble(); //furthestnode

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
			Set<? extends MSCATransition> tr= aut[i].getTransition();
			for (MSCATransition t : tr)
			{
				CAState source=t.getSource(); 
				//with the states of the FMCA
				CAState target=t.getTarget();
				for (int z=0;z<source.getState().length;z++)
				{
					source.getState()[z] = source.getState()[z] + upperbound*(i+1);
					target.getState()[z] = target.getState()[z] + upperbound*(i+1);
				}
				//t.setSource(source);
				//t.setTarget(target);
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
			t[i]=new FMCATransition(finitial,label,aut[i].getInitial(),FMCATransition.action.PERMITTED); 
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

}
