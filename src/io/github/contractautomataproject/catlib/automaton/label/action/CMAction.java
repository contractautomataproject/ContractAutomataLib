package io.github.contractautomataproject.catlib.automaton.label.action;

import io.github.contractautomataproject.catlib.automaton.label.CMLabel;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;

import java.util.Objects;

/**
 * Class representing the label of a Communicating Machine
 * 
 * @author Davide Basile
 *
 */
public class CMAction extends Action {

	private final String id;
	private final String partner;
	private final Action action;
	private static final String ID_SEPARATOR = "_";
	private static final String ACTION_SEPARATOR = "@";

	/**
	 *
	 * @param sender  the sender in the label
	 * @param receiver the receiver in the label
	 * @param action the action in the label
	 */
	public CMAction(String sender, String receiver, Action action)
	{
		super(action.getLabel());
		if ((!(action instanceof OfferAction)||(action instanceof RequestAction)))
			throw new IllegalArgumentException();

		this.action=action;

		this.id=(action instanceof OfferAction)?sender:receiver;
		this.partner=(action instanceof OfferAction)?receiver:sender;

		if (this.id.isEmpty()||this.partner.isEmpty())
			throw new IllegalArgumentException();
	}

	/**
	 * @param lab the string must be in the format sender + id_separator + receiver + action_separator + action,
	 * 		  where either sender==this.id and action is an offer or receiver==this.id and action is a request.
	 */
	public static CMAction parseCMAction(String lab) {
		String[] f = lab.split(ACTION_SEPARATOR);
		if (f.length!=2)
			throw new IllegalArgumentException();
		Action act = AutDataConverter.parseAction(f[1]);
		String[] p = f[0].split(ID_SEPARATOR);
		if (p.length!=2)
			throw new IllegalArgumentException();

		return new CMAction(p[0],p[1],act);
	}


	public static boolean isParsableCMAction(String lab) {
		String[] f = lab.split(ACTION_SEPARATOR);
		if (f.length!=2)
			return false;
		if (!(OfferAction.isOffer(f[1])|| RequestAction.isRequest(f[1])|| IdleAction.isIdle(f[1])))
			return false;
		String[] p = f[0].split(ID_SEPARATOR);
		return (p.length==2);
	}



	@Override
	public String toString() {
		if (action instanceof OfferAction)
			return "["+this.id+ID_SEPARATOR+this.partner+ACTION_SEPARATOR+action.toString()+"]";
		else
			return "["+this.partner+ID_SEPARATOR+this.id+ACTION_SEPARATOR+action.toString()+"]";
	}
}
