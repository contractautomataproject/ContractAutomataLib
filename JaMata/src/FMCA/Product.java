package FMCA;

import java.util.Arrays;

import CA.CATransition;

public class Product {
	private String[] required;
	private String[] forbidden;
	
	public Product(String[] r, String[] f)
	{
		//all positive integers, to avoid sign mismatches
		String[] rp = new String[r.length];
		for (int i=0;i<r.length;i++)
			rp[i]=CATransition.getUnsignedAction(r[i]);

		String[] fp = new String[f.length];
		for (int i=0;i<f.length;i++)
			fp[i]=CATransition.getUnsignedAction(f[i]);

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
	public boolean containsFeature(Product p)
	{
		String[] rp=p.getRequired();
		String[] rf=p.getForbidden();
		for(int i=0;i<rp.length;i++)
			if (!FMCAUtil.contains(rp[i], this.required))
				return false;
		for(int i=0;i<rp.length;i++)
			if (!FMCAUtil.contains(rf[i], this.forbidden))
				return false;
		
		return true;
	}
	
	/**
	 * 
	 * @param t
	 * @return true if all required actions are available in the transitions t
	 */
	public boolean checkRequired(FMCATransition[] t)
	{
		
		for (int i=0;i<this.required.length;i++)
		{
			boolean found=false;
			for (int j=0;j<t.length;j++)
			{
				if (CATransition.getUnsignedAction(t[j].getAction()).equals(this.required[i]))  //do not differ between requests and offers
					found=true;
			}
			if (!found)
				return false;
		}
		return true;
	}
	
	public String toString()
	{
		return "Required : "+Arrays.toString(required)+";\nForbidden : "+Arrays.toString(forbidden)+";\n";
	}
}
