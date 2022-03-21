package io.github.contractautomataproject.catlib.automaton.label;

import io.github.contractautomataproject.catlib.automaton.label.action.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
	 * Constructor using a list of strings. Each position in the list is an index  
	 * in the CALabel and each string is the action of the principal at that position.
	 * @param label the list of strings
	 */
	public CALabel(List<Action> label)
	{
		super(label);

		if (label.stream().anyMatch(l->!(l instanceof OfferAction)&&!(l instanceof RequestAction)&&!(l instanceof IdleAction)) ||
				label.stream().allMatch(l -> l instanceof IdleAction) ||
				label.stream().filter(l -> l instanceof  OfferAction).count()>1 ||
				label.stream().filter(l -> l instanceof RequestAction).count()>1)
			throw new IllegalArgumentException("The label is not well-formed");
	}
	
	private Integer getOffererIfAny() {
		List<Action> label = this.getLabel();
		return IntStream.range(0, label.size())
				.filter(i->label.get(i) instanceof  OfferAction)
				.findAny().orElse(-1);
	}
	
	private Integer getRequesterIfAny() {
		List<Action> label = this.getLabel();
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
	public Action getAction() {
		if (this.isRequest())
			return this.getLabel()
					.stream()
					.filter(a->a instanceof RequestAction)
					.findAny()
					.orElseThrow(RuntimeException::new);
		else
			return this.getLabel()
					.stream()
					.filter(a->a instanceof OfferAction)
					.findAny()
					.orElseThrow(RuntimeException::new);
		//in case of match, the action is always the offer
	}

	/**
	 * @return in case the calabel is a request it return the offer action, in case of offer or match returns the request action
	 */
	public Action getCoAction()
	{
		Action action =  this.getLabel().stream()
				.filter(s->!(s instanceof IdleAction))
				.findAny().orElseThrow(IllegalArgumentException::new);

		if (!this.isRequest()) {
			if (action instanceof AddressedAction)
				return new AddressedRequestAction(action.getLabel(),((AddressedAction) action).getAddress());
			else
				return new RequestAction(action.getLabel());
		}
		else {
			if (action instanceof AddressedAction)
				return new AddressedOfferAction(action.getLabel(),((AddressedAction) action).getAddress());
			else
				return new OfferAction(action.getLabel());
		}
	}

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
					((OfferAction) act1).match((RequestAction) act2))
					||
					(act1 instanceof RequestAction && act2 instanceof OfferAction &&
				    ((RequestAction) act1).match((OfferAction) act2));
		}
		else
			throw new IllegalArgumentException();
	}
}
