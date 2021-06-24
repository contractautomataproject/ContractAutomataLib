package contractAutomata.automaton.label;

import java.util.Objects;

public class CMLabel extends CALabel {

	private final String id;

	private final String partner;	
	
	public CMLabel(String lab) {
		super(1,0,lab.split("@")[1]);
		String[] p = lab.split("@")[0].split("_");
		
		this.id=this.isOffer()?p[0]:p[1];
		this.partner=this.isOffer()?p[1]:p[0];
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
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CMLabel other = (CMLabel) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (partner == null) {
			if (other.partner != null)
				return false;
		} else if (!partner.equals(other.partner))
			return false;
		return true;
	}


}








//	@Override
//	public String getUnsignedAction()
//	{
//		System.out.println(super.getUnsignedAction());
//		return this.getAction().split("@")[1];
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
