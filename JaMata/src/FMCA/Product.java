package FMCA;

public class Product {
	private int[] required;
	private int[] forbidden;
	
	public Product(int[] r, int[] f)
	{
		this.required=r;
		this.forbidden=f;
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
