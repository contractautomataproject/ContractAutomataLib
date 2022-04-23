package io.github.contractautomata.catlib.automaton.label;

import io.github.contractautomata.catlib.automaton.label.action.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class implementing a label of a Contract Automaton, by extending the super class <tt>Label</tt>. <br>
 * The content of each label is a list of actions. <br>
 * Contract automata labels can be of three types:<br>
 * <ul>
 * <li> offer: one action is an offer action and all the others are idle actions,</li>
 * <li> request: one action is a request action and all the others are idle actions,</li>
 * <li> match: two actions are matching (i.e., one is a request, the other an offer, and h
 * 			the content is the same) and all the others are idle.</li>
 * </ul>
 * @author Davide Basile
 *
 */
public class CALabel extends Label<Action> {

	/**
	 * Constructor only used for requests or offer actions, i.e., only one principal is moving.
	 * The action must be either a request action or an offer action.
	 * The index of the principal moving must be lower than the rank.
	 *
	 * @param rank	rank of the label
	 * @param principal index of the principal
	 * @param action action of the label
	 */
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
	 * Constructor using a list of strings. Each element in the list is
	 * the action of the principal at that position.
	 * The label must be well-formed (see description of this class).
	 *
	 * @param label the list of actions
	 */
	public CALabel(List<Action> label)
	{
		super(label);

		if (label.stream().anyMatch(l->!(l instanceof OfferAction)&&!(l instanceof RequestAction)&&!(l instanceof IdleAction)) ||
				label.stream().allMatch(IdleAction.class::isInstance) ||
				label.stream().filter(OfferAction.class::isInstance).count()>1 ||
				label.stream().filter(RequestAction.class::isInstance).count()>1)
			throw new IllegalArgumentException("The label is not well-formed");
	}

	/**
	 * Returns the index of the principal performing the offer action, or -1 in
	 * case no principal is performing an offer.
	 * @return the index of the principal performing the offer actions, or -1 in
	 * 	  case no principal is performing an offer.
	 */
	private Integer getOffererIfAny() {
		List<Action> label = this.getContent();
		return IntStream.range(0, label.size())
				.filter(i->label.get(i) instanceof  OfferAction)
				.findAny().orElse(-1);
	}

	/**
	 * Returns the index of the principal performing the request action, or -1 in
	 * 	  	  case no principal is performing a request
	 * @return the index of the principal performing the request action, or -1 in
	 * 	  case no principal is performing a request.
	 */
	private Integer getRequesterIfAny() {
		List<Action> label = this.getContent();
		return IntStream.range(0, label.size())
				.filter(i->label.get(i) instanceof RequestAction)
				.findAny().orElse(-1);
	}

	/**
	 * Returns the index of the principal performing the offer action.
	 * There must be a principal performing an offer action.
	 *
	 * @return the index of the principal performing the offer action.
	 * 	      There must be a principal performing an offer action.
	 */
	public Integer getOfferer() {
		Integer offerer = getOffererIfAny();
		if (offerer ==-1) throw new UnsupportedOperationException();
		else return offerer;

	}


	/**
	 * Returns the index of the principal performing the request action.
	 * There must be a principal performing a request action.
	 *
	 * @return the index of the principal performing the request action.
	 * 	      There must be a principal performing a request action.
	 */
	public Integer getRequester() {
		Integer requester = getRequesterIfAny();
		if (requester ==-1) throw new UnsupportedOperationException();
		else return requester;
	}

	/**
	 * Returns true if the action is a match
	 * @return true if the action is a match
	 */
	public boolean isMatch()
	{
		return getOffererIfAny() != -1 && getRequesterIfAny() != -1;
	}


	/**
	 * Returns true if the action is an offer
	 * @return true if the action is an offer
	 */
	public boolean isOffer()
	{
		return getRequesterIfAny() == -1;
	}


	/**
	 * Returns true if the action is a request
	 * @return true if the action is a request
	 */
	public boolean isRequest()
	{
		return getOffererIfAny() == -1;
	}
	
	/**
	 * Returns the index of the offerer or requester.
	 * The label must not be a match.
	 *
	 * @return the index of the offerer or requester.
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
	 * If the label is a request it returns the requests action, if it is an offer or match returns the offer action.
	 *
	 * @return if the label is a request it returns the requests action, if it is an offer or match returns the offer action
	 */
	@Override
	public Action getAction() {
		if (this.isRequest())
			return this.getContent()
					.stream()
					.filter(RequestAction.class::isInstance)
					.findAny()
					.orElseThrow(RuntimeException::new);
		else
			return this.getContent()
					.stream()
					.filter(OfferAction.class::isInstance)
					.findAny()
					.orElseThrow(RuntimeException::new);
		//in case of match, the action is always the offer
	}

	/**
	 * Returns the complementary action of the one returned by getAction().
	 * If, for example, getAction() returns an offer, getCoAction() returns a request
	 * with the same content.
	 * @return   the complementary action of the one returned by getAction().
	 */
	public Action getCoAction()
	{
		Action action =  this.getContent().stream()
				.filter(s->!(s instanceof IdleAction))
				.findAny().orElseThrow(IllegalArgumentException::new);

		if (this.isRequest()) {
			if (action instanceof AddressedAction)
				return new AddressedOfferAction(action.getLabel(),((AddressedAction) action).getAddress());
			else
				return new OfferAction(action.getLabel());
		}
		else {
			if (action instanceof AddressedAction)
				return new AddressedRequestAction(action.getLabel(),((AddressedAction) action).getAddress());
			else
				return new RequestAction(action.getLabel());
		}
	}

	/**
	 * Implementation of the match method of interface Matchable.
	 * Two contract automata labels are matching if  their corresponding actions have the same content
	 * but with complementary type (i.e., one is a request and the other an offer).
	 * The argument must be an instance of CALabel.
	 *
	 * @param label the label to match
	 * @return true if this action matches the label passed as argument
	 */
	@Override
	public boolean match(Label<Action> label)
	{
		if (label instanceof CALabel)
		{
			CALabel l2 = (CALabel) label;

			if (this.isMatch() || l2.isMatch())
				return false;

			Action act1 = this.getAction();
			Action act2 = l2.getAction();
			return (act1 instanceof OfferAction && act2 instanceof RequestAction &&
					act1.match(act2))
					||
					(act1 instanceof RequestAction && act2 instanceof OfferAction &&
				    act1.match(act2));
		}
		else
			throw new IllegalArgumentException();
	}
}
