package io.github.contractautomataproject.catlib.automaton.label;

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

	public static final String ID_SEPARATOR = "_";
	public static final String ACTION_SEPARATOR = "@";


	/**
	 * Construct a CMLabel encoded in a string lab
	 * 
	 * @param lab the string must be in the format sender + id_separator + receiver + action_separator + action, 
	 * 		  where either sender==this.id and action is an offer or receiver==this.id and action is a request. 
	 */
	public CMLabel(String lab) {
		super(1,0,lab.split(ACTION_SEPARATOR)[1]);
		String[] p = lab.split(ACTION_SEPARATOR)[0].split(ID_SEPARATOR);
		if (p.length!=2)
			throw new IllegalArgumentException();

		this.id=this.isOffer()?p[0]:p[1];
		this.partner=this.isOffer()?p[1]:p[0];
		
		if (this.id.isEmpty() 
				|| this.partner.isEmpty())
			throw new IllegalArgumentException();
	}
	
	public CMLabel(List<String> lab) {
		this(lab.get(0));
		if (lab.size()!=1)
			throw new IllegalArgumentException();
	}

	/**
	 * 
	 * @param sender  the sender in the label
	 * @param receiver the receiver in the label
	 * @param action the action in the label
	 */
	public CMLabel(String sender, String receiver, String action)
	{
		this(sender+ID_SEPARATOR+receiver+ACTION_SEPARATOR+action);

	}

	@Override
	public boolean match(Label<String> l2)
	{
		if (l2 instanceof CMLabel)
		{
			CMLabel cl2 = (CMLabel) l2;
			return super.match(l2)
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
			return "["+this.id+ID_SEPARATOR+this.partner+ACTION_SEPARATOR+super.getPrincipalAction()+"]";
		else
			return "["+this.partner+ID_SEPARATOR+this.id+ACTION_SEPARATOR+this.getPrincipalAction()+"]";
	}	
}