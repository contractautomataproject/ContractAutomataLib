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

	public static final String IDLE="-";
	public static final String OFFER="!";
	public static final String REQUEST="?";


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
		if (action==null||rank<=0||action.length()<=1||principal>=rank)
			throw new IllegalArgumentException();

		if (!action.startsWith(OFFER)&&!action.startsWith(REQUEST))
			throw new IllegalArgumentException("The action is not a request nor an offer");
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
		if (action1==null||action2==null||rank<=0||action1.length()<=1||action2.length()<=1||principal1>=rank||principal2>=rank)
			throw new IllegalArgumentException();

		if ((action1.startsWith(OFFER)&&!action2.startsWith(REQUEST))||
				(action1.startsWith(REQUEST)&&!action2.startsWith(OFFER)))
			throw new IllegalArgumentException("The action must be an offer and a request");
	}

	/**
	 * Constructor using a list of strings. Each position in the list is an index  
	 * in the CALabel and each string is the action of the principal at that position.
	 * @param label the list of strings
	 */
	public CALabel(List<String> label)
	{		
		super(label);

		if (label.isEmpty())
			throw new IllegalArgumentException();
		
		if (label.stream()
				.anyMatch(Objects::isNull))
			throw new IllegalArgumentException("Label contains null references");

		 if (label.stream().anyMatch(l->!l.startsWith(OFFER)&&!l.startsWith(REQUEST)&&!l.equals(IDLE)) ||
				 label.stream().allMatch(l->l.equals(IDLE)) ||
				 label.stream().filter(l->l.startsWith(OFFER)).count()>1 || 
				 label.stream().filter(l->l.startsWith(REQUEST)).count()>1)
			 throw new IllegalArgumentException("The label is not well-formed");
	}

	/**
	 * Construct a CALabel by shifting of some positions the index of principals moving in the label
	 * @param lab the object label to shift
	 * @param rank the rank of the label to be created
	 * @param shift the position to shift
	 */
	public CALabel(CALabel lab, Integer rank, Integer shift) {
		super(shift(lab,rank,shift));
	}

	private static List<String> shift(CALabel lab, Integer rank, Integer shift){
		if (rank==null||rank<=0||lab==null||shift==null||shift<0||lab.getRank()+shift>rank)
			throw new IllegalArgumentException("Null argument or shift="+shift+" is negative "
					+ "or out of rank");
		System.out.println(lab.getRank() + " "+lab);

		List<String> l = new ArrayList<>(rank);
		l.addAll(Stream.generate(()->CALabel.IDLE).limit(shift).collect(Collectors.toList()));
		l.addAll(lab.getAction());
		if (rank-l.size()>0)
			l.addAll(Stream.generate(()->CALabel.IDLE).limit(rank.longValue()-l.size()).collect(Collectors.toList()));
		return l;
	}
	
	private Integer getOffererIfAny() {
		List<String> label = this.getAction();
		return IntStream.range(0, label.size())
				.filter(i->label.get(i).startsWith(OFFER))
				.findAny().orElse(-1);
	}
	
	private Integer getRequesterIfAny() {
		List<String> label = this.getAction();
		return IntStream.range(0, label.size())
				.filter(i->label.get(i).startsWith(REQUEST))
				.findAny().orElse(-1);
	}

	public Integer getOfferer() {
		Integer offerer = getOffererIfAny();
		if (offerer.intValue()==-1) throw new UnsupportedOperationException();
		else return offerer;

	}

	public Integer getRequester() {
		Integer requester = getRequesterIfAny();
		if (requester.intValue()==-1) throw new UnsupportedOperationException();
		else return requester;
	}

	public boolean isMatch()
	{
		return getOffererIfAny().intValue() != -1 && getRequesterIfAny().intValue() != -1;
	}

	public boolean isOffer()
	{
		return getRequesterIfAny().intValue() == -1;
	}

	public boolean isRequest()
	{
		return getOffererIfAny().intValue() == -1;
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
				.findAny()
				.orElseThrow(IllegalArgumentException::new);
		if (this.isRequest())
			return CALabel.REQUEST+act.substring(1);
		else
			return CALabel.OFFER+act.substring(1);
		//in case of match, the action is always the offer
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
	public String toString() {
		return this.getAction().toString();
	}

}
