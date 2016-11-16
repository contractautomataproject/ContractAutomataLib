package MSCA;


import java.util.Arrays;




import CA.CATransition;



/**
 * Transition of a contract automaton
 * 
 * @author Davide Basile
 *
 */
@SuppressWarnings("serial")
public class MSCATransition extends CATransition implements java.io.Serializable{ 
	private boolean must;  
	
	/**
	 * 
	 * @param initial		source state
	 * @param label2			label
	 * @param fina			arrival state
	 */
	public MSCATransition(int[] initial, int[] label2, int[] fina,boolean must){
		super(initial,label2,fina);
		this.must=must;
	}
	
	
	
		public boolean isMust()
	{
		return this.must;
	}
	
	/**
	 * override of toString
	 */
	public String toString()
	{
		if (this.must)
			return "!("+Arrays.toString(getSource())+","+Arrays.toString(getLabelP())+","+Arrays.toString(getArrival())+")";
		else
			return "("+Arrays.toString(getSource())+","+Arrays.toString(getLabelP())+","+Arrays.toString(getArrival())+")";
	}

	public boolean equals(MSCATransition t)
	{
		MSCATransition tr=(MSCATransition) t;
		int[] ip =tr.getSource();
		int[] lp=tr.getLabelP();
		int[] dp=tr.getArrival();
		boolean must=tr.isMust();
		return ( Arrays.equals(ip,getSource()))&&(Arrays.equals(lp,getLabelP()))&&(Arrays.equals(dp,this.getArrival())&&(this.must==must));
	}	
	
	/**
	 * 
	 * @return	true if the  must transition request is matched 
	 */
	protected  boolean isMatched(MSCA aut)
	{
		MSCATransition[] tr = aut.getTransition();
//		int[][] fs=aut.allFinalStates();
		int[][] R=aut.getDanglingStates();
		//MSCATransition[] unmatch = new MSCATransition[tr.length];
		if ((this.request())
			&&(this.isMust()))
		{

//			if (!MSCAUtil.contains(this.getSource(), fs)) // if source state is not final
//				return true;
			for (int j=0;j<tr.length;j++)	
			{
				if ((tr[j].match())
					&&(tr[j].isMust())
					&&(tr[j].receiver()==this.receiver())	//the same principal
					&&(tr[j].getSource()[tr[j].receiver()]==this.getSource()[this.receiver()]) //the same source state					
					&&(tr[j].getLabelP()[tr[j].receiver()]==this.getLabelP()[this.receiver()]) //the same request
					&&(!MSCAUtil.contains(this.getSource(), R))) //source state is not redundant
					{
						return true;
					}
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param t
	 * @param aut
	 * @return   source states of transitions in t that are unmatched in aut
	 */
	protected static int[][] sourcesUnmatched(MSCATransition[] t, MSCA aut)
	{
		int[][] s= new int[t.length][];
		int pointer=0;
		for (int i=0;i<t.length;i++)
		{
			if (!t[i].isMatched(aut))
			{
				if (!MSCAUtil.contains(t[i].getSource(), s))
				{
					s[pointer]=t[i].getSource();
					pointer++;
				}
			}
		}
		s=MSCAUtil.removeTailsNull(s, pointer);
		return s;
	}
}