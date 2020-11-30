package FMCA;

import java.util.Arrays;
import java.util.Set;

import CA.CALabel;
import MSCA.MSCA;
import MSCA.MSCATransition;

/**
 * A configuration/product of a product line/family, identified as set of required and forbidden features
 * 
 * @author Davide Basile
 *
 */
public class Product {
	private String[] required; //TODO use ArrayList
	private String[] forbidden;
	
	public Product(String[] r, String[] f)
	{
		//all positive integers, to avoid sign mismatches
		String[] rp = new String[r.length];
		for (int i=0;i<r.length;i++)
			rp[i]=CALabel.getUnsignedAction(r[i]);

		String[] fp = new String[f.length];
		for (int i=0;i<f.length;i++)
			fp[i]=CALabel.getUnsignedAction(f[i]);

		this.required=rp;
		this.forbidden=fp;
	}

	/**
	 * 
	 * instantiate a product considering only one element of those that are equals
	 * 
	 * @param r
	 * @param f
	 * @param eq an array of elements such that eq[i][0] is equal to eq[i][1]
	 */
	public Product(String[] r, String[] f, String[][] eq)
	{
		//all positive integers, to avoid sign mismatches
		String[] rp = new String[r.length];
		for (int i=0;i<r.length;i++)
			rp[i]=CALabel.getUnsignedAction(r[i]);

		String[] fp = new String[f.length];
		for (int i=0;i<f.length;i++)
			fp[i]=CALabel.getUnsignedAction(f[i]);

//		int countreq=0;
//		int countforb=0;
//		
		for (int i=0;i<eq.length;i++)
		{
			if (FMCAUtils.contains(eq[i][0], rp)&&FMCAUtils.contains(eq[i][1], rp))
			{
				int index=FMCAUtils.getIndex(rp, eq[i][1]);
				rp[index]=null;
				//countreq++;
			}
			else if (FMCAUtils.contains(eq[i][0], fp)&&FMCAUtils.contains(eq[i][1], fp)) //the feature cannot be both required and forbidden
			{
				int index=FMCAUtils.getIndex(fp, eq[i][1]);
				fp[index]=null;
				//countforb++;
			}
		}
		rp=FMCAUtils.removeHoles(rp, new String[] {}); //countreq
		fp=FMCAUtils.removeHoles(fp, new String[] {}); //countforb
		this.required=rp;
		this.forbidden=fp;
	}
	
	public String[] getRequired()
	{
		return required;
	}
	
	public String[] getForbidden()
	{
		return forbidden;
	}
	
	public int getForbiddenAndRequiredNumber()
	{
		return required.length+forbidden.length;
	}
	
	/**
	 * check if all features of p are contained 
	 * @param p
	 * @return
	 */
	public boolean containsFeatures(Product p)
	{
		String[] rp=p.getRequired();
		String[] rf=p.getForbidden();
		for(int i=0;i<rp.length;i++)
			if (!FMCAUtils.contains(rp[i], this.required))
				return false;
		for(int i=0;i<rf.length;i++)
			if (!FMCAUtils.contains(rf[i], this.forbidden))
				return false;
		
		return true;
	}
	
	/**
	 * check if all forbidden features of p are contained 
	 * @param p
	 * @return
	 */
	public boolean containsForbiddenFeatures(Product p)
	{
		String[] rf=p.getForbidden();
		for(int i=0;i<rf.length;i++)
			if (!FMCAUtils.contains(rf[i], this.forbidden))
				return false;
		
		return true;
	}
	
	/**
	 * check if all required features of p are contained 
	 * @param p
	 * @return
	 */
	public boolean containsRequiredFeatures(Product p)
	{
		String[] rf=p.getRequired();
		for(int i=0;i<rf.length;i++)
			if (!FMCAUtils.contains(rf[i], this.required))
				return false;
		
		return true;
	}
	
	/**
	 * 
	 * @param f
	 * @return  true if feature f is contained (either required or forbidden)
	 */
	public boolean containFeature(String f)
	{
		String[] s= new String[1];
		s[0]=f;
		Product temp = new Product(s,s);
		return (this.containsRequiredFeatures(temp)||this.containsForbiddenFeatures(temp));
	}
	
	
	/**
	 * 
	 * @param t
	 * @return true if all required actions are available in the transitions t
	 */
	public boolean checkRequired(Set<? extends MSCATransition> set)
	{
		for (int i=0;i<this.required.length;i++)
		{
			boolean found=false;
			for (MSCATransition t : set)
			{
				if (CALabel.getUnsignedAction(t.getLabel().getAction()).equals(this.required[i]))  //do not differ between requests and offers
					found=true;
			}
			if (!found)
				return false;
		}
		return true;
	}
	

	public boolean isForbidden(MSCATransition t)
	{
		return (FMCAUtils.getIndex(this.getForbidden(),t.getLabel().getUnsignedAction())>=0);
	}

//	private boolean isRequired(MSCATransition t)
//	{
//		return (FMCAUtils.getIndex(this.getRequired(),t.getLabel().getUnsignedAction())>=0);		
//	}

	
	/**
	 * 
	 * @param t
	 * @return true if all forbidden actions are not available in the transitions t
	 */
	public boolean checkForbidden(Set<? extends MSCATransition> tr)
	{
		
		for (int i=0;i<this.forbidden.length;i++)
		{
			for (MSCATransition t : tr)
			{
				if (CALabel.getUnsignedAction(t.getLabel().getAction()).equals(this.forbidden[i]))  //do not differ between requests and offers
					return false;
			}
		}
		return true;
	}
	
	
	public boolean isValid(MSCA aut)
	{
		Set<? extends MSCATransition> t=aut.getTransition();
		return this.checkForbidden(t)&&this.checkRequired(t);
	}
	
	public String toString()
	{
		return "R:"+Arrays.toString(required)+";\nF:"+Arrays.toString(forbidden)+";\n";
	}
	
	public String toStringFile(int id)
	{
		String req="";
		for (int i=0;i<required.length;i++)
		{
			req+=required[i]+",";
		}
		String forb="";
		for (int i=0;i<forbidden.length;i++)
		{
			forb+=forbidden[i]+",";
		}
		return "p"+id+": R={"+req+"} F={"+forb+"}";
	}
	public String toHTMLString(String s)
	{
        return "<html>"+s+"R:"+Arrays.toString(required)+"<br />F:"+Arrays.toString(forbidden)+"</html>";
	
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(forbidden);
		result = prime * result + Arrays.hashCode(required);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (!Arrays.equals(forbidden, other.forbidden))
			return false;
		if (!Arrays.equals(required, other.required))
			return false;
		return true;
	}
}

//END OF CLASS

//	/**
//	 * 
//	 * @param p
//	 * @return true if both products have the same required and forbidden features
//	 */
//	public boolean equals(Product p)
//	{
//		return (
//			((p.getRequired().length==required.length)&&(this.containsRequiredFeatures(p)))
//			&&
//			((p.getForbidden().length==forbidden.length)&&(this.containsForbiddenFeatures(p)))			
//			);
//	}
	
//	@Override
//	public Product clone()
//	{
//		return new Product(Arrays.copyOf(this.getRequired(), this.getRequired().length), 
//				Arrays.copyOf(this.getForbidden(), this.getForbidden().length));
//	}

