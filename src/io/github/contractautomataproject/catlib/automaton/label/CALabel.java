package io.github.contractautomataproject.catlib.automaton.label;

import io.github.contractautomataproject.catlib.automaton.label.action.Action;
import io.github.contractautomataproject.catlib.automaton.label.action.IdleAction;
import io.github.contractautomataproject.catlib.automaton.label.action.OfferAction;
import io.github.contractautomataproject.catlib.automaton.label.action.RequestAction;

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
public class CALabel extends Label<Action> {

	/**
	 * Constructor only used for requests or offer actions, i.e., only one principal is moving
	 * @param rank	rank of the label
	 * @param principal index of the principal
	 * @param action action of the label
	 */
	public CALabel(Integer rank, Integer principal, String action) {
		this(rank,principal,parseAction(action));
	}

	public CALabel(Integer rank, Integer principal, Action action) {
		super(IntStream.range(0, rank)
				.mapToObj(i->(i==principal)?action:new IdleAction())
				.collect(Collectors.toList()));
		if (principal>=rank)
			throw new IllegalArgumentException();

		if (!(action instanceof OfferAction)&&!(action instanceof RequestAction))
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
		this(rank,principal1,principal2,parseAction(action1),parseAction(action2));
	}

	public CALabel(Integer rank, Integer principal1, Integer principal2, Action action1, Action action2) {
		super(IntStream.range(0, rank)
				.mapToObj(i->(i==principal1)?action1:(i==principal2)?action2:new IdleAction())
				.collect(Collectors.toList()));
		if (principal1>=rank||principal2>=rank)
			throw new IllegalArgumentException();

		if ((action1 instanceof OfferAction &&!(action2 instanceof RequestAction))||
				(action1 instanceof RequestAction&&!(action2 instanceof OfferAction)))
			throw new IllegalArgumentException("The action must be an offer and a request");
	}

	/**
	 * Constructor using a list of strings. Each position in the list is an index  
	 * in the CALabel and each string is the action of the principal at that position.
	 * @param label the list of strings
	 */
	public CALabel(List<String> label)
	{		
		this(label.stream().map(CALabel::parseAction).collect(Collectors.toList()),null);
	}

	public CALabel(List<Action> label, Object dummy)
	{
		super(label);

		if (label.stream().anyMatch(l->!(l instanceof OfferAction)&&!(l instanceof RequestAction)&&!(l instanceof IdleAction)) ||
				label.stream().allMatch(l -> l instanceof IdleAction) ||
				label.stream().filter(l -> l instanceof  OfferAction).count()>1 ||
				label.stream().filter(l -> l instanceof RequestAction).count()>1)
			throw new IllegalArgumentException("The label is not well-formed ");
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

	private static List<Action> shift(CALabel lab, Integer rank, Integer shift){
		if (rank==null||rank<=0||lab==null||shift==null||shift<0||lab.getRank()+shift>rank)
			throw new IllegalArgumentException("Null argument or shift="+shift+" is negative "
					+ "or out of rank");

		List<Action> l = new ArrayList<>(rank);
		l.addAll(Stream.generate(IdleAction::new).limit(shift).collect(Collectors.toList()));
		l.addAll(lab.getAction());
		if (rank-l.size()>0)
			l.addAll(Stream.generate(IdleAction::new).limit(rank.longValue()-l.size()).collect(Collectors.toList()));
		return l;
	}

	private static Action parseAction(String action) {
		Objects.requireNonNull(action);
		if (OfferAction.isOffer(action))
			return new OfferAction(action.substring(1));
		else if (RequestAction.isRequest(action))
			return new RequestAction(action.substring(1));
		else if (IdleAction.isIdle(action))
			return new IdleAction();
		else throw new IllegalArgumentException();
	}
	
	private Integer getOffererIfAny() {
		List<Action> label = this.getAction();
		return IntStream.range(0, label.size())
				.filter(i->label.get(i) instanceof  OfferAction)
				.findAny().orElse(-1);
	}
	
	private Integer getRequesterIfAny() {
		List<Action> label = this.getAction();
		return IntStream.range(0, label.size())
				.filter(i->label.get(i) instanceof RequestAction)
				.findAny().orElse(-1);
	}

	public Integer getOfferer() {
		Integer offerer = getOffererIfAny();
		if (offerer ==-1) throw new UnsupportedOperationException();
		else return offerer;

	}

	public Integer getRequester() {
		Integer requester = getRequesterIfAny();
		if (requester ==-1) throw new UnsupportedOperationException();
		else return requester;
	}

	public boolean isMatch()
	{
		return getOffererIfAny() != -1 && getRequesterIfAny() != -1;
	}

	public boolean isOffer()
	{
		return getRequesterIfAny() == -1;
	}

	public boolean isRequest()
	{
		return getOffererIfAny() == -1;
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
		if (this.isRequest())
			return new RequestAction(this.getUnsignedAction()).toString();
		else
			return new OfferAction(this.getUnsignedAction()).toString();
		//in case of match, the action is always the offer
	}


	/**
	 * @return in case the calabel is a request it return the offer action, in case of offer or match returns the request action
	 */
	public  String getCoAction()
	{	
		String action = this.getPrincipalAction();
		if (OfferAction.isOffer(action))
			return new RequestAction(this.getUnsignedAction()).toString();
		else return new OfferAction(this.getUnsignedAction()).toString();
	}

	@Override
	public boolean match(Label<Action> label)
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
		Action act = this.getAction().stream()
				.filter(s->!(s instanceof IdleAction))
				.findAny().orElseThrow(IllegalArgumentException::new);
		return act.getLabel();
	}

	

}
