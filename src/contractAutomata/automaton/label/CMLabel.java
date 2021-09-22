package contractAutomata.automaton.label;

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
	
	public static final String id_separator = "_";
	public static final String action_separator = "@";
	
	
	/**
	 * Construct a CMLabel enconded in a string lab
	 * 
	 * @param lab the string must be in the format sender + id_separator + receiver + action_separator + action, 
	 * 		  where either sender==this.id and action is an offer or receiver==this.id and action is a request. 
	 */
	public CMLabel(String lab) {
		super(1,0,lab.split(action_separator)[1]);
		String[] p = lab.split(action_separator)[0].split(id_separator);
		if (p.length!=2)
			throw new IllegalArgumentException();
		
		this.id=this.isOffer()?p[0]:p[1];
		this.partner=this.isOffer()?p[1]:p[0];
	}
	
	/**
	 * 
	 * @param sender  the sender in the label
	 * @param receiver the receiver in the label
	 * @param action the action in the label
	 */
	public CMLabel(String sender, String receiver, String action)
	{
		this(sender+id_separator+receiver+action_separator+action);
		
	}
	
	@Override
	public boolean match(Label l2)
	{
		if (l2 instanceof CMLabel)
		{
			CMLabel cl2 = (CMLabel) l2;
			return super.match(l2)
					&& this.getPartner().equals(cl2.getId()) 
					&& cl2.getId().equals(this.getPartner());
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
		if (this == obj)
			return true;
		if (!super.equals(obj) || getClass() != obj.getClass())
			return false;
		CMLabel other = (CMLabel) obj;
		return Objects.equals(id, other.id)&&Objects.equals(partner, other.partner);
	}
	
	@Override
	public String toString() {
		if (this.isOffer())
			return "["+this.id+id_separator+this.partner+action_separator+super.getAction()+"]";
		else
			return "["+this.partner+id_separator+this.id+action_separator+this.getAction()+"]";
	}
	


}








//	@Override
//	public String getUnsignedAction()
//	{
//		System.out.println(super.getUnsignedAction());
//		return this.getAction().split(action_separator)[1];
//	}

//public CMLabel(Integer rank, Integer principal, String action,Integer id, Integer partner) {
//	super(rank,principal,action);
//	this.id=id;
//	this.partner=partner;
//}

//public CMLabel(CALabel lab,Integer id, Integer partner) {
//	super(lab,1,0);
//	this.id=id;
//	this.partner=partner;
//}
