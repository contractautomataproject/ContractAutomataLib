package FSA;

import java.util.Vector;




/**
 * This class simulates a running of a FSA for accepting/refuting a string in input
 * 
 * @author Davide Basile
 */
@SuppressWarnings("rawtypes")
public class Simulator {
	protected int pointer_state;
	protected int pointer_string;
	protected FSA automa;
	
	/**
	 * The constructor
	 * @param automa The automaton to simulate
	 */
	public Simulator(FSA automa)
	{
		this.automa=automa;
	}
	
	/**
	 * Change the automaton to simulate
	 * @param automa
	 */
	public void changeAutoma(FSA automa)
	{
		this.automa=automa;
	}
	
	/**
	 * At every iteration:
	 * <ul>
	 * 		<li>
	 * 			<ul>
	 * 				<li>first check if the string is consumed, then if we are in a final state and the side conditions are verified then the string is accepted,</li> 
	 * 				<li>else backtracking if possible, otherwise return false</li>
	 * 			</ul>
	 * 		</li>
	 * 		<li>if the string is not consumed yet:
 	 *			<ul>
	 *	  					<li>if there is a continuation then performs the step stored</li>
	 *	  					<li>else if there are possible steps then performs one and store the others in the continuation</li>
	 *	  					<li>if no possible step are allowed, then performs backtracking or if the continuation is empty return false</li>
	 *	  		</ul>
	 * 		</li>
	 * </ul>
	 * @param s the string to be recognized
	 * @return true if s is recognized by the automata, false otherwise
	 */
	public boolean run(int[] s)
	{
		Vector<Continuation> continuation= new Vector<Continuation>();
		Vector<Continuation> visited = new Vector<Continuation>();
		Transition temp = null;
		this.pointer_state=automa.getInitial();
		this.pointer_string=0;
		while (true)
		{
			if (pointer_string == s.length) //if the string is consumed
			{
				if ((contains(pointer_state, automa.getFinalStates())!=-1)&&(sideConditions())) //if we reach a final state OK
					return true;
				else
				{
					temp = backtracking(continuation,visited);	// else check if there are other possible pending paths to follow
					if (temp==null) return false; //if it is not the case then the string is not accepted
				}
			}
			else
			{		
				if (!checkContinuation(temp,s[pointer_string]))  //if there is a continuation then move on
					{
						if (!step(s[pointer_string],continuation,visited))
						{
							temp = backtracking(continuation,visited); //if a step is not possible then backtracking
							if (temp==null) return false; //if backtracking is not possible the string is not accepted
						}
					}
					else
						temp=null;
			}
		}
	}
	
	/**
	 * If a transition was selected in the continuation (tr not null), then perform the step by updating the state of the simulator
	 * @param tr transition to perform
	 * @param symbol the symbol in input
	 * @return true if tr is not null, otherwise false
	 */
	protected boolean checkContinuation(Transition tr, int symbol)
	{
		if (tr!=null)
		{
			updateState(tr,symbol);
			return true;
		}
		else return false;
	}

	/**
	 * Select a transition in the continuation that is not visited,  save it in the visited continuation and restore the state of the continuation.
	 * If no transitions are available then return null
	 * 
	 * @param continuation  vector of continuation to be visited
	 * @param visited 	vector of visited continuation
	 * @return	the Transition to be performed or null if no transition are available
	 */
	protected Transition backtracking(Vector<Continuation> continuation,Vector<Continuation> visited)
	{
		boolean contains;
		Transition tr = null;
		Continuation c = null;
		if (continuation.isEmpty())
			return null;
		do{
			
			contains=false;
			c = continuation.elementAt(continuation.size()-1);
			Vector<Transition> v =  c.getTransition();
			tr= v.remove(v.size()-1);
			if (v.isEmpty())
				continuation.remove(continuation.size()-1);
			contains=isVisited(c,tr,visited);
		} while(contains&&!continuation.isEmpty());
		if (continuation.isEmpty()&&contains)
			return null;
		saveTransitionInContinuation(c,tr,visited);
		restoreStateContinuation(c);
		return tr;
	}
	
	/**
	 * Check if the transition was previously visited
	 * 
	 * @param c		the continuation to be searched
	 * @param tr	the transition in the continuation c to be searched
	 * @param visited	the vector of visited continuations
	 * @return	true if the transition t in the continuation c is already visited 
	 */
	private boolean isVisited(Continuation c,Transition tr, Vector<Continuation> visited)
	{
		for (int i=0;i<visited.size();i++)
		{
			Continuation co = visited.elementAt(i);
			if (co.containTransition(c, tr))
			{
				return true;
			}
		}
		return false;
	}
	
	/**
	 * If there exists a continuation without the transition to be performed, then add this transition 
	 * to the selected continuation.
	 * If such continuation does not exists, create a new one and add the transition to be performed.
	 * 
	 * @param c		the continuation to be saved
	 * @param tr	the transition in the continuation c to be saved
	 * @param visited	vector of visited continuation
	 */
	private void saveTransitionInContinuation(Continuation c, Transition tr, Vector<Continuation> visited)
	{
		boolean contains = false;
		for (int i=0;i<visited.size();i++)
		{
			Continuation co = visited.elementAt(i);
			if (co.equalWithoutTransition(c))
			{
				co.getTransition().add(tr);
				contains=true;
				break;
			}
		}
		if(!contains)
		{
			Continuation co = c.cloneWithoutTransition();
			co.getTransition().add(tr);
			visited.add(co);
		}
	}
	 
	/**
	 * restore the state of the simulator freezed in the continuation c
	 * @param c the continuation to be restored
	 */
	protected void restoreStateContinuation(Continuation c)
	{
		pointer_state=c.getPointerState();
		pointer_string=c.getPointerString();
	}
	
	/**
	 * A method to be extended in case of additional conditions of acceptance
	 * @return true
	 */
	protected boolean sideConditions()
	{
		return true;
	}
	
	/**
	 * Update the state of the simulator by performing the transition tr
	 * 
	 * @param tr		the transition to be performed
	 * @param symbol	the symbol in input
	 */
	protected void updateState(Transition tr,int symbol)
	{
		pointer_string++;
		pointer_state=tr.getFinal();
	}
	
	/**
	 * Check if the transition can be performed
	 * 
	 * @param tr		transition to be performed
	 * @param symbol	symbol in input
	 * @return			true if the transition is valid
	 */
	protected boolean isValid(Transition tr,int symbol)
	{
		return tr.getLabel()==symbol;
	}
	
	/**
	 * 	Select all the possible transition that can be executed in this configuration, then perform the first transition and store all the other in the continuation.
	 * 	Finally save the performed transition in the visited continuation. 
	 * 
	 * @param symbol			the symbol in input
	 * @param continuation		the vector of continuation to be performed
	 * @param visited			the vector of visited continuation
	 * @return	true if a step is performed, otherwise false
	 */
	protected boolean step(int symbol, Vector<Continuation> continuation, Vector<Continuation> visited)
	{
		Transition[] t= Transition.getTransitionFrom(pointer_state, automa.getTransition()); //all the transition from this state
		if (t==null)
			return false;
		// select the transitions allowed
		Continuation c = createContinuation().cloneWithoutTransition();  
		Transition[] allowed = new Transition[t.length];
		int j=0;
		for (int i=0; i<t.length; i++)
		{
			Transition tr= t[i];
			if (this.isValid(tr,symbol)&&!this.isVisited(c, tr, visited))
			{
				allowed[j]=tr;
				j++;
			}	
			
		}		
		if (allowed[0]!=null) 
		{ 
			//move with the first transition and store all the others in the continuation
			if (allowed.length>1)
			{
				Vector<Transition> tr = new Vector<Transition>(allowed.length-1);
				for (int i=1;i<allowed.length;i++)
				{
					if (allowed[i]!=null){
						tr.add(t[i]);
					}
					else
						break;
				}
				if (tr.size()>0)
				{
					c.setTransition(tr);
					continuation.add(c);
				}
			}
			/**
			 * Store the transition in the visited continuations
			 */
			saveTransitionInContinuation(c,allowed[0],visited);
			updateState(allowed[0],symbol);
			return true;
		}
		else 
			return false; //no step available
	}
	
	
	/**
	 * Freeze the actual state of the simulator in a continuation
	 * @return a new Continuation with the actual state of the simulator freezed
	 */
	protected Continuation createContinuation()
	{
		return new Continuation(pointer_state,pointer_string, null);
	}
	
	/**
	 * check if n is contained in a
	 * @param n integer to search
	 * @param a array of integer
	 * @return the index of the element of the array that contains n, -1 if the element is not contained
	 */
	protected static int contains(int n, int[] a) 
	{
		for (int i=0;i<a.length;i++)
		{
			if (a[i]==n)
				return i;
		}
		return -1;
	}
	
	/**
	 * Check if n is contained in the vector v
	 * @param n		the integer to be searched
	 * @param v		a vector of integer
	 * @return		if n is found then return the index of n in v, otherwise -1
	 */
	protected static int contains(int n, Vector v){
		for (int i=0; i<v.size();i++)
		{
			if ( (int)v.elementAt(i)==n)
				return i;
		}
		return -1;
	}
	
}
