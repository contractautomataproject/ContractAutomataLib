package contractAutomata.automaton.label;

import java.util.List;
import java.util.Objects;
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
public class CALabel extends Label {
	/**
	 * the rank of the label (i.e. number of principals)
	 */
	private final /*@ spec_public @*/  Integer rank;
	
	/**
	 * the index of the offerer in the label or -1
	 */
	private final /*@ spec_public @*/ Integer offerer;
	
	/**
	 * the index of the requester in the label or -1
	 */
	private final /*@ spec_public @*/ Integer requester;
	
	/**
	 * the action performed by the label
	 */
//	private final /*@ spec_public @*/  String action; //in case of match, the action is always the offer

	final public static String idle="-";
	final public static String offer="!";
	final public static String request="?";
	
	//the actiontype is used for redundant checks
	enum ActionType{
		REQUEST,OFFER,MATCH
	}
	
	private final ActionType actiontype;


	/*@ invariant rank!=null && rank>0; @*/
	/*@ public invariant action!=null && action.length()>1 && 
			(action.startsWith(offer) || action.startsWith(request)); @*/
	
	
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
		super(CALabel.getUnsignedAction(action));
		if (rank==null||principal==null||action==null||rank<=0)
			throw new IllegalArgumentException(rank + " " + principal);

		if (action.startsWith(offer))
		{
			this.offerer=principal;
			this.actiontype=CALabel.ActionType.OFFER;
			this.requester=-1;
		}
		else if (action.startsWith(request))
		{
			this.requester=principal;
			this.actiontype=CALabel.ActionType.REQUEST;
			this.offerer=-1;
		}
		else
			throw new IllegalArgumentException("The action is not a request nor an offer");

		this.rank = rank;
	//	this.action = action;
	}
	
	public CALabel(Integer rank, Integer principal1, Integer principal2, String action1, String action2) {
		super(CALabel.getUnsignedAction(action1));
		if (rank==null||principal1==null||action1==null||principal2==null||action2==null||rank<=0||action1.length()<=1||action2.length()<=1)
			throw new IllegalArgumentException("Null argument");

		if ((action1.startsWith(offer)&&!action2.startsWith(request))||
				(action2.startsWith(offer)&&!action1.startsWith(request)))
			throw new IllegalArgumentException("The actions must be an offer and a request");

		if (action1.startsWith(offer))
		{
			this.offerer=principal1;
			this.requester=principal2;
			//		this.action = action1;
		}
		else 
		{
			this.offerer=principal2;
			this.requester=principal1;
			//		this.action = action2;
		}
		this.rank = rank;
		this.actiontype=CALabel.ActionType.MATCH;
	}

	
	public CALabel(CALabel lab, Integer rank, Integer shift) {
		super(lab.getUnsignedAction());
		if (rank==null||rank<=0||lab==null||shift==null||shift<0 || 
				lab.offerer+shift>rank||lab.requester+shift>rank)
			throw new IllegalArgumentException("Null argument or shift="+shift+" is negative "
					+ "or out of rank");

		this.rank = rank;
		this.offerer=(lab.offerer==-1)?-1:lab.offerer+shift;
		this.requester=(lab.requester==-1)?-1:lab.requester+shift;
		//	this.action=lab.action;
		this.actiontype=lab.actiontype;
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
		super(CALabel.getUnsignedAction(offeraction));
		if (rank==null||offerer==null||requester==null||offeraction==null||rank<=0||offeraction.length()<=1)
			throw new IllegalArgumentException("Null argument");

		if (!offeraction.startsWith(CALabel.offer))
			throw new IllegalArgumentException("This constructor is only for matches and by convention action is the offer");

		this.rank = rank;
		//			this.action = offeraction;
		this.offerer = offerer;
		this.requester = requester;
		this.actiontype=CALabel.ActionType.MATCH;
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
		//TODO long function
		super(CALabel.getUnsignedAction(label.stream()
				.filter(s->!s.equals(CALabel.idle))
				.findAny().orElseThrow(IllegalArgumentException::new))
				);
//		if (label==null)
//			throw new IllegalArgumentException("Null argument");

		if (label.isEmpty())
			throw new IllegalArgumentException("Empty label");

		label.forEach(
				x -> {if (x==null) throw new IllegalArgumentException("Label contains null references");}
				);

		int offtemp=-1,requtemp=-1;//offerer and requester are final
		//				String acttemp=null;//action is final
		for (int i=0;i<label.size();i++)
		{
			if (label.get(i).startsWith(offer)) 
			{
				if (offtemp!=-1)
					throw new IllegalArgumentException("The label is not well-formed");
				offtemp=i; 
				//						acttemp=label.get(i);
			}
			else if (label.get(i).startsWith(request))
			{

				if (requtemp!=-1)
					throw new IllegalArgumentException("The label is not well-formed");
				requtemp=i; 
				//								acttemp = (acttemp==null)?label.get(i):acttemp;
			}
			else if (!label.get(i).equals(idle)) 
				throw new IllegalArgumentException("The label is not well-formed");
		}
		this.offerer=offtemp;
		this.requester=requtemp;
		//			this.action=acttemp;
		if (offerer==-1 && requester==-1)
			throw new IllegalArgumentException("The label is not well-formed");
		else if (offerer!=-1&&requester!=-1)
			this.actiontype=CALabel.ActionType.MATCH;
		else if (offerer!=-1)
			this.actiontype=CALabel.ActionType.OFFER;
		else
			this.actiontype=CALabel.ActionType.REQUEST;

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
		if (this.isRequest())
			throw new UnsupportedOperationException("No offerer in a request action");
		else 
			return offerer;
	}

	/*
	 * @ public normal behavior
	 * @	requires this.requester!=null;
	 * @	ensures !this.isOffer() ==> \result == this.offerer
	 */	
	public /*@ pure @*/ Integer getRequester() {
		if (this.isOffer())
			throw new UnsupportedOperationException("No requester in an offer action");
		else 
			return requester;
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
	@Override
	public String getAction() {
		if (this.actiontype==ActionType.REQUEST)
			return CALabel.request+super.getAction();
		else
			return CALabel.offer+super.getAction();
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
		String action = this.getAction();
		if (this.getAction().startsWith(offer))
			return request+action.substring(1,action.length());
		else // if (action.startsWith(request))
			return offer+action.substring(1,action.length());
//		else
//			throw new IllegalArgumentException("The label is not an action");
	}
	
	/*
	  @ public normal_behavior
	  @     requires this.rank!=null && this.rank >=0;
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
		String action = this.getAction();
		if (this.isMatch())
		{
			return IntStream.range(0, rank)
					.mapToObj(i->(i==offerer)?action:(i==requester)?this.getCoAction():idle)
					.collect(Collectors.toList());
		}
		else
		{
			return IntStream.range(0, rank)
					.mapToObj(i->((this.isOffer()&&i==offerer)
							||(this.isRequest()&&i==requester))?action:idle)
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
//		if ((this.offerer!=-1 && this.requester!=-1 && this.action.startsWith(offer))
//				&&this.actiontype!=CALabel.ActionType.MATCH)
//			throw new RuntimeException("The type of the label is not correct");
		
		return this.offerer!=-1 && this.requester!=-1 && this.getAction().startsWith(offer)
				&&this.actiontype==CALabel.ActionType.MATCH;
	}
	
	/*
	 * @ public normal behavior
	 * @ 	requires this.action != null;
	 * @ 	ensures \result == this.offerer!=-1 && this.requester==-1 && this.action.startsWith(offer);
	 */
	public /*@ pure @*/ boolean isOffer()
	{
//		if ( (this.offerer!=-1 && this.requester==-1 && this.action.startsWith(offer)) 
//				&& this.actiontype!=CALabel.ActionType.OFFER)
//			throw new RuntimeException("The type of the label is not correct");
		
		return this.offerer!=-1 && this.requester==-1 && this.getAction().startsWith(offer)
				&& this.actiontype==CALabel.ActionType.OFFER;
	}

	/*
	 * @ public normal behavior
	 * @ 	requires this.action != null;
	 * @ 	ensures \result == this.offerer==-1 && this.requester!=-1 && this.action.startsWith(request);
	 */
	public /*@ pure @*/ boolean isRequest()
	{
//		if ( (this.offerer==-1 && this.requester!=-1 && this.action.startsWith(request)) 
//				&& this.actiontype!=CALabel.ActionType.REQUEST)
//			throw new RuntimeException("The type of the label is not correct");
	
		return this.offerer==-1 && this.requester!=-1 && this.getAction().startsWith(request)
				&& this.actiontype==CALabel.ActionType.REQUEST;
	}


	@Override
	public boolean match(Label label)
	{
		if (label instanceof CALabel)
		{
			CALabel l2 = (CALabel) label;
			if (l2!=null&&(this.isOffer()&&l2.isRequest())||this.isRequest()&&l2.isOffer())
			{
				String la1 = this.getUnsignedAction();
				String la2 = l2.getUnsignedAction();
				return la1.equals(la2);			
			}
			else
				return false;
		}
		else 
			throw new IllegalArgumentException();
	}
	
	/*
	 * @ public normal_behaviour
	 * @	requires this.getAction() != null;
	 * @	requires (this.getAction().startsWith(offer)||this.getAction().startsWith(request));
	 * @	ensures \result == this.getAction().substring(1);
	 */
	public String getUnsignedAction()
	{
		String action = this.getAction();
		return action.substring(1,action.length());
	}

	private static String getUnsignedAction(String action)
	{
		return action.substring(1,action.length());
	}

	
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(),offerer.hashCode(),rank.hashCode(),requester.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (obj == null || getClass() != obj.getClass())
			return false;
		CALabel other = (CALabel) obj;
		return offerer.equals(other.offerer)
				&&rank.equals(other.rank)&&requester.equals(other.requester);
	}

	
	
	public String toCSV() {
		return "[rank=" + rank + ", offerer=" + offerer + ", requester=" + requester
				+ ", actiontype=" + actiontype + "]";
	}
	
}






/*
@ public normal_behaviour
@     requires this.rank >=0;
@     requires this.action != null; 
@		requires this.action.length()>=2;
@		requires this.action.startsWith(offer) || this.action.startsWith(request);
*/
//@Override
//public String toString() {
//	return this.getLabelAsList().toString();		
//}