package FMCA;

public class Product {
	private int[] required;
	private int[] forbidden;
	
	public Product(int[] r, int[] f)
	{
		//all positive integers, to avoid sign mismatches
		int[] rp = new int[r.length];
		for (int i=0;i<r.length;i++)
			rp[i]=Math.abs(r[i]);

		int[] fp = new int[f.length];
		for (int i=0;i<f.length;i++)
			fp[i]=Math.abs(f[i]);

		this.required=rp;
		this.forbidden=fp;
	}

	public int[] getRequired()
	{
		return required;
	}
	
	public int[] getForbidden()
	{
		return forbidden;
	}
	
	/**
	 * 
	 * @param t
	 * @return checks that all required actions are available in the transitions t
	 */
	public boolean checkRequired(FMCATransition[] t)
	{
		
		for (int i=0;i<this.required.length;i++)
		{
			boolean found=false;
			for (int j=0;j<t.length;j++)
			{
				if (Math.abs(t[j].getAction())==Math.abs(this.required[i]))  //do not differ between requests and offers
					found=true;
			}
			if (!found)
				return false;
		}
		return true;
	}
}
