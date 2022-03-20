package io.github.contractautomataproject.catlib.automaton.label;

import io.github.contractautomataproject.catlib.automaton.label.action.Action;
import io.github.contractautomataproject.catlib.automaton.label.action.IdleAction;
import io.github.contractautomataproject.catlib.automaton.label.action.OfferAction;
import io.github.contractautomataproject.catlib.automaton.label.action.RequestAction;
import io.github.contractautomataproject.catlib.converters.AutDataConverter;

import java.util.List;
import java.util.Objects;

/**
 * Class representing the label of a Communicating Machine
 * 
 * @author Davide Basile
 *
 */
public class CMLabel extends CALabel {

	private final String id;
	private final String partner;	

	private static final String ID_SEPARATOR = "_";
	private static final String ACTION_SEPARATOR = "@";

	/**
	 * 
	 * @param sender  the sender in the label
	 * @param receiver the receiver in the label
	 * @param action the action in the label
	 */
	public CMLabel(String sender, String receiver, Action action)
	{
		super(1,0,action);
		this.id=this.isOffer()?sender:receiver;
		this.partner=this.isOffer()?receiver:sender;

		if (this.id.isEmpty()||this.partner.isEmpty())
			throw new IllegalArgumentException();

	}


	/**
	 * @param lab the string must be in the format sender + id_separator + receiver + action_separator + action,
	 * 		  where either sender==this.id and action is an offer or receiver==this.id and action is a request.
	 */
	public static CMLabel parseCMLabel(String lab) {
		String[] f = lab.split(ACTION_SEPARATOR);
		if (f.length!=2)
			throw new IllegalArgumentException();
		Action act = AutDataConverter.parseAction(f[1]);
		String[] p = f[0].split(ID_SEPARATOR);
		if (p.length!=2)
			throw new IllegalArgumentException();

		return new CMLabel(p[0],p[1],act);
	}

	public static boolean isParsableCMLabel(String lab) {
		String[] f = lab.split(ACTION_SEPARATOR);
		if (f.length!=2)
			return false;
		if (!(OfferAction.isOffer(f[1])|| RequestAction.isRequest(f[1])|| IdleAction.isIdle(f[1])))
			return false;
		String[] p = f[0].split(ID_SEPARATOR);
		return (p.length==2);
	}

	@Override
	public boolean match(Label<Action> label)
	{
		if (label instanceof CMLabel)
		{
			CMLabel cl2 = (CMLabel) label;
			return super.match(label)
					&& partner.equals(cl2.getId())
					&& id.equals(cl2.getPartner());
		}
		else
			throw new IllegalArgumentException();
	}

	public String getPartner() {
		return partner;
	}

	public String getId() {
		return id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(),id.hashCode(),partner.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		CMLabel other = (CMLabel) obj;
		return Objects.equals(id, other.id)
				&&Objects.equals(partner, other.partner);
	}

	@Override
	public String toString() {
		if (this.isOffer())
			return "["+this.id+ID_SEPARATOR+this.partner+ACTION_SEPARATOR+super.getPrincipalAction().toString()+"]";
		else
			return "["+this.partner+ID_SEPARATOR+this.id+ACTION_SEPARATOR+this.getPrincipalAction().toString()+"]";
	}	
}