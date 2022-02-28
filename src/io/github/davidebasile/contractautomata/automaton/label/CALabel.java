package io.github.davidebasile.contractautomata.automaton.label;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 
 * Class implementing a label of a contract automaton transition.
 * 
 * Note: in this class Java Modelling Language contracts have been experimented,  
 * only using Extended Static Checker analysis of OpenJML.  However, the specs are 
 * outdated w.r.t. the current implementation.
 * 
 * @author Davide Basile
 *
 */
public class CALabel extends Label<List<String>> {
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
	
//	private final /*@ spec_public @*/  String action; //in case of match, the action is always the offer

	final public static String idle="-";
	final public static String offer="!";
	final public static String request="?";
	
	/**
	 * the actiontype is used for redundant checks
	 *
	 */
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
	/**
	 * Constructor only used for requests or offer actions, i.e., only one principal is moving
	 * @param rank	rank of the label
	 * @param principal index of the principal
	 * @param action action of the label
	 */
	public CALabel(Integer rank, Integer principal, String action) {
		super(IntStream.range(0, rank)
				.mapToObj(i->(i==principal)?action:idle)
				.collect(Collectors.toList()));
		if (principal==null||action==null||rank<=0)
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
	
	
	/**
	 * Constructor for a match transition
	 * 
	 * @param rank rank of the label
	 * @param principal1 index of first principal moving in the label
	 * @param principal2 index of second principal moving in the label
	 * @param action1 action of the first principal
	 * @param action2 action of the second principal
	 */
	public CALabel(Integer rank, Integer principal1, Integer principal2, String action1, String action2) {
		super(IntStream.range(0, rank)
				.mapToObj(i->(i==principal1)?action1:(i==principal2)?action2:idle)
				.collect(Collectors.toList()));
		if (principal1==null||principal2==null||action2==null||rank<=0||action2.length()<=1)
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
	/**
	 * Constructor using a list of strings. Each position in the list is an index  
	 * in the CALabel and each string is the action of the principal at that position.
	 * @param label the list of strings
	 */
	public CALabel(List<String> label)
	{		
		super(label);

		label.forEach(
				x -> {if (x==null) throw new IllegalArgumentException("Label contains null references");}
				);

		if (label.isEmpty())
			throw new IllegalArgumentException();
		
		int offtemp=-1,requtemp=-1;//offerer and requester are final
		for (int i=0;i<label.size();i++)
		{
			if (label.get(i).startsWith(offer)) 
			{
				if (offtemp!=-1)
					throw new IllegalArgumentException("The label is not well-formed");
				offtemp=i; 
			}
			else if (label.get(i).startsWith(request))
			{
				if (requtemp!=-1)
					throw new IllegalArgumentException("The label is not well-formed");
				requtemp=i; 
			}
			else if (!label.get(i).equals(idle)) 
				throw new IllegalArgumentException("The label is not well-formed "+label.get(i));
		}
		this.offerer=offtemp;
		this.requester=requtemp;
		
		if (offerer!=-1&&requester!=-1)
			this.actiontype=CALabel.ActionType.MATCH;
		else if (offerer!=-1)
			this.actiontype=CALabel.ActionType.OFFER;
		else if (requester!=-1)
			this.actiontype=CALabel.ActionType.REQUEST;
		else
			throw new IllegalArgumentException("The action is not a request nor an offer "+label);

		this.rank = label.size();
	}
	
	/**
	 * Construct a CALabel by shifting of some positions the index of principals moving in the label
	 * @param lab the object label to shift
	 * @param rank the rank of the label to be created
	 * @param shift the position to shift
	 */
	public CALabel(CALabel lab, Integer rank, Integer shift) {
		super(shift(lab,rank,shift));
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
	
//	public List<String> getLabelAsList(Integer rank, Integer principal1, Integer principal2, String action1, String action2){
//		return IntStream.range(0, rank)
//				.mapToObj(i->(i==principal1)?action1:(i==principal2)?action2:idle)
//				.collect(Collectors.toList());
//	}
//	
//	public List<String> getLabelAsList(Integer rank, Integer principal, String action){
//			return IntStream.range(0, rank)
//					.mapToObj(i->(i==principal)?action:idle)
//					.collect(Collectors.toList());
//	}
	
	private static List<String> shift(CALabel lab, Integer rank, Integer shift){
		List<String> l = new ArrayList<String>(rank);
		l.addAll(Stream.generate(()->CALabel.idle).limit(shift).collect(Collectors.toList()));
		l.addAll(lab.getAction());
		if (rank-l.size()>0)
			l.addAll(Stream.generate(()->CALabel.idle).limit(rank-l.size()).collect(Collectors.toList()));
		return l;
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
			throw new UnsupportedOperationException("No offerer in a request action ");
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
	/**
	 * 
	 * @return the index of the offerer or requester, does not support match transitions
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
	/**
	 * @return in case the calabel is a request it return the requests action, in case of offer or match returns the offer action
	 */
	public String getTheAction() {
		String act = this.getAction().stream()
						.filter(s->!s.equals(CALabel.idle))
						.findAny().orElseThrow(IllegalArgumentException::new);
		if (this.actiontype==ActionType.REQUEST)
			return CALabel.request+act.substring(1);
		else
			return CALabel.offer+act.substring(1);
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
	/**
	 * @return in case the calabel is a request it return the offer action, in case of offer or match returns the request action
	 */
	public  /*@ pure @*/ String getCoAction()
	{	
		String action = this.getTheAction();
		if (action.startsWith(offer))
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
	/**
	 * 
	 * @return the calabel encoded in a list of strings, at each position there is the action performed by that principal in that position
	 */
//	@Override
//	public List<String> getLabelAsList(){
//		return this.getAction();	
//	}
	
	

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
		
		return this.offerer!=-1 && this.requester!=-1 && this.getTheAction().startsWith(offer)
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
		
		return this.offerer!=-1 && this.requester==-1 && this.getTheAction().startsWith(offer)
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
	
		return this.offerer==-1 && this.requester!=-1 && this.getTheAction().startsWith(request)
				&& this.actiontype==CALabel.ActionType.REQUEST;
	}


	@Override
	public boolean match(Label<List<String>> label)
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
	/**
	 * 
	 * @return a string containing the action without request/offer sign
	 */
	public String getUnsignedAction()
	{

		String act = this.getAction().stream()
						.filter(s->!s.equals(CALabel.idle))
						.findAny().orElseThrow(IllegalArgumentException::new);
		return act.substring(1);
	}

//	private static String getUnsignedAction(String action)
//	{
//		return action.substring(1,action.length());
//	}

	
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
		CALabel other = (CALabel) obj;
		return offerer.equals(other.offerer)
				&&rank.equals(other.rank)&&requester.equals(other.requester);
	}


/*
@ public normal_behaviour
@     requires this.rank >=0;
@     requires this.action != null; 
@		requires this.action.length()>=2;
@		requires this.action.startsWith(offer) || this.action.startsWith(request);
*/
	@Override
	public String toString() {
		return this.getAction().toString();
	}
	
	/**
	 * 
	 * @return a string description of the calabel in comma separated values
	 */
	@Override
	public String toCSV() {
		return "[rank=" + rank + ", offerer=" + offerer + ", requester=" + requester
				+ ", actiontype=" + actiontype + "]";
	}
	
//	@Override
//	public CALabel getCopy() {
//		if (this.isMatch())
//			return new CALabel(this.getRank(),this.getOfferer(),this.getRequester(),this.getAction());
//		else 
//			return new CALabel(this.getRank(),(this.isOffer())?this.getOfferer():this.getRequester(),this.getAction());
//	}
	
//	@Override
//	public CALabel getCopy() {
//		if (this.isMatch())
//			return new CALabel(this.getRank(),this.getOfferer(),this.getRequester(),this.getTheAction(),this.getCoAction());
//			//TODO check I removed this constructor call return new CALabel(la.getRank(),la.getOfferer(),la.getRequester(),la.getAction());
//		else 
//			return new CALabel(this.getRank(),(this.isOffer())?this.getOfferer():this.getRequester(),this.getTheAction());
//	}

}