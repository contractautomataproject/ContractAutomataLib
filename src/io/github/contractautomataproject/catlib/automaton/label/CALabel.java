package io.github.contractautomataproject.catlib.automaton.label;

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
 * @author Davide Basile
 *
 */
public class CALabel extends Label<List<String>> {
	
	/**
	 * the rank of the label (i.e. number of principals)
	 */
	private final Integer rank;

	/**
	 * the index of the offerer in the label or -1
	 */
	private final Integer offerer;

	/**
	 * the index of the requester in the label or -1
	 */
	private final Integer requester;

	//in case of match, the action is always the offer

	public static final String IDLE="-";
	public static final String OFFER="!";
	public static final String REQUEST="?";

	/**
	 * the actiontype is used for redundant checks
	 *
	 */
	enum ActionType{
		REQUEST_TYPE,OFFER_TYPE,MATCH_TYPE
	}

	private final ActionType actiontype;

	/**
	 * Constructor only used for requests or offer actions, i.e., only one principal is moving
	 * @param rank	rank of the label
	 * @param principal index of the principal
	 * @param action action of the label
	 */
	public CALabel(Integer rank, Integer principal, String action) {
		super(IntStream.range(0, rank)
				.mapToObj(i->(i==principal)?action:IDLE)
				.collect(Collectors.toList()));
		if (principal==null||action==null||rank<=0)
			throw new IllegalArgumentException(rank + " " + principal);

		if (action.startsWith(OFFER))
		{
			this.offerer=principal;
			this.actiontype=CALabel.ActionType.OFFER_TYPE;
			this.requester=-1;
		}
		else if (action.startsWith(REQUEST))
		{
			this.requester=principal;
			this.actiontype=CALabel.ActionType.REQUEST_TYPE;
			this.offerer=-1;
		}
		else
			throw new IllegalArgumentException("The action is not a request nor an offer");

		this.rank = rank;
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
				.mapToObj(i->(i==principal1)?action1:(i==principal2)?action2:IDLE)
				.collect(Collectors.toList()));
		if (principal1==null||principal2==null||action2==null||rank<=0||action2.length()<=1)
			throw new IllegalArgumentException("Null argument");

		if ((action1.startsWith(OFFER)&&!action2.startsWith(REQUEST))||
				(action2.startsWith(OFFER)&&!action1.startsWith(REQUEST)))
			throw new IllegalArgumentException("The actions must be an offer and a request");

		if (action1.startsWith(OFFER))
		{
			this.offerer=principal1;
			this.requester=principal2;
		}
		else 
		{
			this.offerer=principal2;
			this.requester=principal1;
		}
		this.rank = rank;
		this.actiontype=CALabel.ActionType.MATCH_TYPE;
	}

	/**
	 * Constructor using a list of strings. Each position in the list is an index  
	 * in the CALabel and each string is the action of the principal at that position.
	 * @param label the list of strings
	 */
	public CALabel(List<String> label)
	{		
		super(label);

		this.rank = label.size();

		if (label.stream()
				.anyMatch(Objects::isNull))
			throw new IllegalArgumentException("Label contains null references");

		if (label.isEmpty())
			throw new IllegalArgumentException();

		 if (label.stream().anyMatch(l->!l.startsWith(OFFER)&&!l.startsWith(REQUEST)&&!l.equals(IDLE)) ||
				 label.stream().allMatch(l->l.equals(IDLE)) ||
				 label.stream().filter(l->l.startsWith(OFFER)).count()>1 || 
				 label.stream().filter(l->l.startsWith(REQUEST)).count()>1)
			 throw new IllegalArgumentException("The label is not well-formed");
		 
		this.offerer= IntStream.range(0, rank)
				.filter(i->label.get(i).startsWith(OFFER))
				.findAny().orElse(-1);
		this.requester=IntStream.range(0, rank)
				.filter(i->label.get(i).startsWith(REQUEST))
				.findAny().orElse(-1);

		if (offerer!=-1&&requester!=-1)
			this.actiontype=CALabel.ActionType.MATCH_TYPE;
		else if (offerer!=-1)
			this.actiontype=CALabel.ActionType.OFFER_TYPE;
		else if (requester!=-1)
			this.actiontype=CALabel.ActionType.REQUEST_TYPE;
		else
			throw new IllegalArgumentException("The action is not a request nor an offer "+label);
	}

	/**
	 * Construct a CALabel by shifting of some positions the index of principals moving in the label
	 * @param lab the object label to shift
	 * @param rank the rank of the label to be created
	 * @param shift the position to shift
	 */
	public CALabel(CALabel lab, Integer rank, Integer shift) {
		super(shift(lab,rank,shift));
		this.rank = rank;
		this.offerer=(lab.offerer==-1)?-1:lab.offerer+shift;
		this.requester=(lab.requester==-1)?-1:lab.requester+shift;
		this.actiontype=lab.actiontype;
	}


	private static List<String> shift(CALabel lab, Integer rank, Integer shift){
		if (rank==null||rank<=0||lab==null||shift==null||shift<0 || 
				lab.offerer+shift>rank||lab.requester+shift>rank)
			throw new IllegalArgumentException("Null argument or shift="+shift+" is negative "
					+ "or out of rank");

		List<String> l = new ArrayList<>(rank);
		l.addAll(Stream.generate(()->CALabel.IDLE).limit(shift).collect(Collectors.toList()));
		l.addAll(lab.getAction());
		if (rank-l.size()>0)
			l.addAll(Stream.generate(()->CALabel.IDLE).limit(rank.longValue()-l.size()).collect(Collectors.toList()));
		return l;
	}

	@Override
	public Integer getRank() {
		return rank;
	}

	public Integer getOfferer() {
		if (this.isRequest())
			throw new UnsupportedOperationException("No offerer in a request action "+this.toString());
		else 
			return offerer;
	}

	public Integer getRequester() {
		if (this.isOffer())
			throw new UnsupportedOperationException("No requester in an offer action");
		else 
			return requester;
	}

	/**
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

	/**
	 * @return in case the calabel is a request it return the requests action, in case of offer or match returns the offer action
	 */
	public String getPrincipalAction() {
		String act = this.getAction().stream()
				.filter(s->!s.equals(CALabel.IDLE))
				.findAny().orElseThrow(IllegalArgumentException::new);
		if (this.actiontype==ActionType.REQUEST_TYPE)
			return CALabel.REQUEST+act.substring(1);
		else
			return CALabel.OFFER+act.substring(1);
	}


	/**
	 * @return in case the calabel is a request it return the offer action, in case of offer or match returns the request action
	 */
	public  String getCoAction()
	{	
		String action = this.getPrincipalAction();
		if (action.startsWith(OFFER))
			return REQUEST+action.substring(1,action.length());
		else return OFFER+action.substring(1,action.length());
	}


	public boolean isMatch()
	{
		return this.offerer!=-1 && this.requester!=-1 && this.getPrincipalAction().startsWith(OFFER)
				&&this.actiontype==CALabel.ActionType.MATCH_TYPE;
	}

	public boolean isOffer()
	{
		return this.offerer!=-1 && this.requester==-1 && this.getPrincipalAction().startsWith(OFFER)
				&& this.actiontype==CALabel.ActionType.OFFER_TYPE;
	}

	public boolean isRequest()
	{
		return this.offerer==-1 && this.requester!=-1 && this.getPrincipalAction().startsWith(REQUEST)
				&& this.actiontype==CALabel.ActionType.REQUEST_TYPE;
	}


	@Override
	public boolean match(Label<List<String>> label)
	{
		if (label instanceof CALabel)
		{
			CALabel l2 = (CALabel) label;
			if (this.isOffer()&&l2.isRequest()||this.isRequest()&&l2.isOffer())
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

	/**
	 * 
	 * @return a string containing the action without request/offer sign
	 */
	public String getUnsignedAction()
	{

		String act = this.getAction().stream()
				.filter(s->!s.equals(CALabel.IDLE))
				.findAny().orElseThrow(IllegalArgumentException::new);
		return act.substring(1);
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
		CALabel other = (CALabel) obj;
		return offerer.equals(other.offerer)
				&&rank.equals(other.rank)&&requester.equals(other.requester);
	}

	@Override
	public String toString() {
		return this.getAction().toString();
	}

	/**
	 * @return a string description of the calabel in comma separated values
	 */
	@Override
	public String toCSV() {
		return "[rank=" + rank + ", offerer=" + offerer + ", requester=" + requester
				+ ", actiontype=" + actiontype + "]";
	}
}