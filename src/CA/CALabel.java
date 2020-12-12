package CA;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 
 * Implementing a label of a transition.
 * 
 * Note: in this class Java Modelling Language contracts have been experimented,  
 * only using Extended Static Checker analysis of OpenJML. 
 * 
 * @author Davide Basile
 *
 */
public class CALabel {

	private final Integer rank;
	private /*@ spec_public @*/ Integer offerer;
	private /*@ spec_public @*/ Integer requester;
	private /*@ spec_public @*/  String action; //in case of match, the action is always the offer

	final public static String idle="-";
	final public static String offer="!";
	final public static String request="?";

	/*
	 * @ public normal_behavior
	 * @ 	requires rank!=null && principal !=null && action != null;
	 * @ 	requires rank>0 && principal>=0 && principal<rank;
	 * @ 	ensures this.rank==rank && this.action==action && 
	 * 			action.startsWith(offer)==>(this.offerer==principal && this.requester==-1) &&
	 * 			action.startsWith(request)==>(this.requester==principal && this.offerer==-1);
	 * @	ensures (offerer!=-1 && requester !=-1) ==> action.startsWith(offer);
	 * @ 	ensures this.rank>0 && principal>=0 && principal < this.rank;
	 */
	public CALabel(Integer rank, Integer principal, String action) {
		super();
		if (rank==null||principal==null||action==null)
			throw new IllegalArgumentException("Null argument");

		if (action.startsWith(offer))
		{
			this.offerer=principal;
			this.requester=-1;
		}
		else if (action.startsWith(request))
		{
			this.requester=principal;
			this.offerer=-1;
		}
		else
			throw new IllegalArgumentException("The action is not a request nor an offer");

		this.rank = rank;
		this.action = action;
	}

	/*
	 * @ public normal_behavior
	 * @ 	requires rank!=null && offerer !=null && && requester !=null && offeraction != null;
	 * @ 	requires offeraction.startsWith(offer);
	 * @ 	requires rank>0 && offerer>=0 && offerer < this.rank && requester>=0 && requester< this.rank;
	 * @ 	requires offerer!=requester;
	 * @ 	ensures this.rank==rank && this.action==offeraction && this.offerer==offerer && this.requester==requester && 
	 * 				this.action==offeraction;
	 * @ 	ensures this.action.startsWith(offer);
     * @ 	ensures this.rank>0 && this.offerer>=0 && this.offerer < this.rank && this.requester>=0 && this.requester< this.rank;
     * @ 	ensures this.offerer!=this.requester;
 	 */
	public CALabel(Integer rank, Integer offerer, Integer requester, String offeraction) {
		super();
		if (rank==null||offerer==null||requester==null||offeraction==null)
			throw new IllegalArgumentException("Null argument");

		if (!offeraction.startsWith(CALabel.offer))
			throw new IllegalArgumentException("Bug: this constructor is only for matches and by convention action is the offer");

		this.rank = rank;
		this.action = offeraction;
		this.offerer = offerer;
		this.requester = requester;
	}

	/*
	  @ public normal_behavior
	  @ 	requires label != null;
	  @ 	requires !label.isEmpty();
	  @ 	requires \forall int i; 0 <= i < label.size(); label.get(i)!=null; 
      @ 	requires \exists int i; 0 <= i < label.size(); 
    			((label.get(i).startsWith(offer) || label.get(i).startsWith(request)) &&
    				(\forall int j;  0 <= j < label.size() && j!=i; label.get(j)==idle))
    				||
    			(\exists int j; 0 <= j < label.size() && j!=i; 
    		    	label.get(i).startsWith(offer) && label.get(j).startsWith(request) && 
    			  	(\forall int z;  0 <= z < label.size() && z!=i && z!=j; label.get(z)==idle));
      @ 	ensures rank == label.size();
      @ 	ensures (\exists int i; 0 <= i < label.size(); 
    			(label.get(i).startsWith(offer)&&
    				(\forall int j;  0 <= j < label.size() && j!=i; label.get(j)==idle) 
    				&& offerer==i && requester == -1 && action.startsWith(offer)))
    				||
    			(\exists int i; 0 <= i < label.size(); 
    			(label.get(i).startsWith(request)&&
    				(\forall int j;  0 <= j < label.size() && j!=i; label.get(j)==idle) 
    				&& offerer==-1 && requester == i && action.startsWith(request)))
    				||
    			(\exists int i; 0 <= i < label.size(); 
    			(\exists int j; 0 <= j < label.size() && j!=i; 
    		    	label.get(i).startsWith(offer) && label.get(j).startsWith(request) && 
    			  	(\forall int z;  0 <= z < label.size() && z!=i && z!=j; label.get(z)==idle) 
    			  	&& offerer==i && requester==j && action.startsWith(offer));
    	@ 	ensures (offerer!=-1 && requester !=-1) ==> action.startsWith(offer);  
    	@ 	ensures this.rank>0;		
  	*/
	public CALabel(List<String> label)
	{		
		super();
		if (label==null)
			throw new IllegalArgumentException("Null argument");

		if (label.isEmpty())
			throw new IllegalArgumentException("Empty label");

		label.forEach(
			x -> {if (x==null) throw new IllegalArgumentException("Label contains null references");}
		);
		
		this.offerer=-1;this.requester=-1;
		for (int i=0;i<label.size();i++)
		{
			if (label.get(i).startsWith(offer)) 
			{
				this.offerer=i; 
				this.action=label.get(i);
			}
			else if (label.get(i).startsWith(request))
			{
				this.requester=i; 
				this.action = (this.action!=null)?this.action:label.get(i);
			}
			else if (!label.get(i).equals(idle)) 
				throw new IllegalArgumentException("The label is not well-formed");
		}

		if (offerer==-1 && requester==-1)
			throw new IllegalArgumentException("The label is not well-formed");
		
		this.rank = label.size();
	}

	/*
	  @ public normal_behavior
	  @     requires this.rank != null; 
	  @     ensures \result == this.rank;
	  @*/
	public Integer getRank() {
			return rank;
	}

	/*
	 * @ public normal behavior
	 * @	requires this.offerer!=null;
	 * @	ensures !this.isRequest() ==> \result == this.offerer
	 */
	public /*@ pure @*/ Integer getOfferer() {
		if (!this.isRequest())
			return offerer;
		else 
			throw new UnsupportedOperationException("No offerer in a request action");
	}

	/*
	 * @ public normal behavior
	 * @	requires this.requester!=null;
	 * @	ensures !this.isOffer() ==> \result == this.offerer
	 */	
	public /*@ pure @*/ Integer getRequester() {
		if (!this.isOffer())
			return requester;
		else
			throw new UnsupportedOperationException("No requester in an offer action");
	}

	/*
	 * @ public normal behavior
	 * @ 	requires this.getRequester!=null;
	 * @ 	requires this.getOfferer !=null;
	 * @ 	ensures this.isOffer() ==> \result == this.getOfferer()
	 * @ 	ensures this.isRequest() ==> \result == this.getRequester()
	 */
	public Integer getOffererOrRequester() {
		if (this.isOffer()) 
			return this.getOfferer();
		else if (this.isRequest())
			return this.getRequester();
		else
			throw new UnsupportedOperationException("Action is not a request nor an offer");
	}

	/*
	  @ public normal_behavior
	  @     requires this.action != null; 
	  @     ensures \result == this.action; 
	  @*/
	public String getAction() {
		return this.action;
	}


	/*	  
	 *@ public normal_behavior
	  @     requires this.action != null; 
	  @		requires this.action.length()>=2;
	  @		requires this.action.startsWith(offer) || this.action.startsWith(request);
	  @		ensures \result.substring(1) == this.action.substring(1); 
	  @	  	ensures (\result.startsWith(offer) && this.action.startsWith(request))
	  @				|| (\result.startsWith(request) && this.action.startsWith(offer));
	  @*/
	public  /*@ pure @*/ String getCoAction()
	{	
		if (action==null)
			throw new UnsupportedOperationException("The action is null");
		if (action.length()<2)
			throw new UnsupportedOperationException("The action not legal");
		
		if (action.startsWith(offer))
			return request+action.substring(1,action.length());
		else if (action.startsWith(request))
			return offer+action.substring(1,action.length());
		else
			throw new IllegalArgumentException("The label is not an action");
	}
	
	/*
	  @ public normal_behavior
	  @     requires this.rank >=0;
  	  @     requires this.action != null; 
	  @		requires this.action.length()>=2;
	  @		requires this.action.startsWith(offer) || this.action.startsWith(request);
	  @		ensures \result.size()==this.rank;
	  @		ensures \forall int i; 0<=i && i<this.rank && i!=offerer && i!=requester; \result.get(i)==idle;
	  @		ensures this.isOffer() ==> (\result.get(offerer)==action && \result.get(requester)==idle)
	  @		ensures this.isRequest() ==> (\result.get(offerer)==idle && \result.get(requester)==action)
	  @		ensures this.isMatch() ==> (\result.get(offerer)==action && \result.get(requester)==this.getCoAction())
	  */
	public List<String> getLabelAsList(){
		assert !(rank<0):"Negative rank";
		if (!this.isMatch())
		{
			return IntStream.range(0, rank)
					.mapToObj(i->((this.isOffer()&&i==offerer)
							||(this.isRequest()&&i==requester))?action:idle)
					.collect(Collectors.toList());
		}
		else
		{
			return IntStream.range(0, rank)
					.mapToObj(i->(i==offerer)?action:(i==requester)?this.getCoAction():idle)
					.collect(Collectors.toList());
		}		
	}

	/*
	 * @ public normal behavior
	 * @ 	requires this.action != null;
	 * @ 	ensures \result == this.offerer!=-1 && this.requester!=-1 && this.action.startsWith(offer);
	 */
	public /*@ pure @*/ boolean isMatch()
	{
		return this.offerer!=-1 && this.requester!=-1 && this.action.startsWith(offer);
	}
	
	/*
	 * @ public normal behavior
	 * @ 	requires this.action != null;
	 * @ 	ensures \result == this.offerer!=-1 && this.requester==-1 && this.action.startsWith(offer);
	 */
	public /*@ pure @*/ boolean isOffer()
	{
		return this.offerer!=-1 && this.requester==-1 && this.action.startsWith(offer);
	}

	/*
	 * @ public normal behavior
	 * @ 	requires this.action != null;
	 * @ 	ensures \result == this.offerer==-1 && this.requester!=-1 && this.action.startsWith(request);
	 */
	public /*@ pure @*/ boolean isRequest()
	{
		return this.offerer==-1 && this.requester!=-1 && this.action.startsWith(request);
	}


	/*
	 * @ public normal behavior
	 * @ 	requires l1 != null && l2!=null;
	 * @ 	requires l1.getAction()!=null && l2.getAction()!=null;
	 * @    requires l1.getAction().length()>1 && l2.getAction().length()>1;
	 * @ 	ensures ( (l1.isMatch()||l2.isMatch()) || (l1.isOffer()&&l2.isOffer()) || (l1.isRequest()&&l2.isRequest()) ) 
	 * 				==> \result == false;
	 * @ 	ensures !( (l1.isMatch()||l2.isMatch()) || (l1.isOffer()&&l2.isOffer()) || (l1.isRequest()&&l2.isRequest()) ) 
	 * 				==> \result == l1.getAction().substring(1,l1.getAction().length()).equals(l2.getAction().substring(1,l2.getAction().length()));
	 */
	public static boolean match(CALabel l1,CALabel l2)
	{
		if (l1.isMatch()||l2.isMatch())	//both transitions must not be match (non-associative)
			return false;
		if (l1.isOffer()&&l2.isOffer())
			return false;
		if (l1.isRequest()&&l2.isRequest())
			return false;

		String la1 = l1.getAction();
		String la2 = l2.getAction();
		return la1.substring(1,la1.length()).equals(la2.substring(1,la2.length()));
	}

	/*
	 * @ public normal_behaviour
	 * @	requires this.getAction() != null;
	 * @	requires (this.getAction().startsWith(offer)||this.getAction().startsWith(request));
	 * @	ensures \result == this.getAction().substring(1);
	 */
	public String getUnsignedAction()
	{
		String action=this.getAction();
		if (action.startsWith(offer)||action.startsWith(request))
			return action.substring(1,action.length());
		else
			throw new RuntimeException("Bug: irregular label action"); //label;
	}

	/*
	 * @ public normal_behaviour
	 * @	requires action != null;
	 * @	requires (action.startsWith(offer)||action.startsWith(request));
	 * @	ensures \result == action.substring(1);
	 */
	public static String getUnsignedAction(String action)
	{
		if (action.startsWith(offer)||action.startsWith(request))
			return action.substring(1,action.length());
		else
			return action;
	}
	
	@Override
	public CALabel clone() {
		if (!this.isMatch())
			return new CALabel(rank,(this.isOffer())?offerer:requester,action);
		else 
			return new CALabel(rank,offerer,requester,action);
	}
	
	/*
	 * @ public normal_behaviour
  	  @     requires this.rank >=0;
  	  @     requires this.action != null; 
	  @		requires this.action.length()>=2;
	  @		requires this.action.startsWith(offer) || this.action.startsWith(request);
	 */
	@Override
	public String toString() {
		return this.getLabelAsList().toString();		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((offerer == null) ? 0 : offerer.hashCode());
		result = prime * result + ((rank == null) ? 0 : rank.hashCode());
		result = prime * result + ((requester == null) ? 0 : requester.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CALabel other = (CALabel) obj;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (offerer == null) {
			if (other.offerer != null)
				return false;
		} else if (!offerer.equals(other.offerer))
			return false;
		if (rank == null) {
			if (other.rank != null)
				return false;
		} else if (!rank.equals(other.rank))
			return false;
		if (requester == null) {
			if (other.requester != null)
				return false;
		} else if (!requester.equals(other.requester))
			return false;
		return true;
	}
}
