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
}
