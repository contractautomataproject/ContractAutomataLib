package FMCA;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Family {
	private Product[] elements;
	private int[][] po; //matrix po[i][j]==1 iff elements[i]<elements[j]
	
	public Family(Product[] elements, int[][] po)
	{
		this.elements=elements;
		this.po=po;
	}
	
	public Family(Product[] elements)
	{
		this.elements=elements;
		this.po=this.generatePO();
	}
	
	public Family(String filename)
	{
		this.elements=Family.readFile(System.getProperty("user.dir"),filename);
		this.po=this.generatePO();
	}
	
	public Product[] getProducts()
	{
		return elements;
	}
	
	public int[][] getPartialOrder()
	{
		return po;
	}
	
	/**
	 * generate po of products, no transitive closure!
	 * @return
	 */
	protected int[][] generatePO()
	{
		Product[] p=this.elements;
		int[][] po=new int[p.length][p.length]; 
		for (int i=0;i<p.length;i++)
		{
			for (int j=i+1;j<p.length;j++)
			{
				if (p[i].getForbiddenAndRequiredNumber()==p[j].getForbiddenAndRequiredNumber()+1)//1 level of depth
				{
					if (p[i].containsFeature(p[j]))
						po[i][j]=1;
					else
						po[i][j]=0;
				}
				else
					po[i][j]=0;
				
				if (p[j].getForbiddenAndRequiredNumber()==p[i].getForbiddenAndRequiredNumber()+1)//1 level of depth
				{
					if (p[j].containsFeature(p[i]))
						po[j][i]=1;
					else
						po[j][i]=0;
				}
				else
					po[j][i]=0;
			}
		}
		return po;
	}
	
	/**
	 * read products from file
	 * @param currentdir
	 * @param filename
	 * @return
	 */
	protected static Product[] readFile(String currentdir, String filename){
		//Path p=Paths.get(currentdir, filename);
		Path p=Paths.get("", filename);
		
		Charset charset = Charset.forName("ISO-8859-1");
		List<String> lines = null;
		try {
			lines = Files.readAllLines(p, charset);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String[] arr = lines.toArray(new String[lines.size()]);
		Product[] products=new Product[arr.length];//TODO fix max products
		for(int productsind=0;productsind<arr.length;productsind++)
		{
			String[] s=arr[productsind].split("}"); //each line identifies a product			
			String required=s[0].substring(s[0].indexOf("{")+1);
			String requireds[]=required.split(",");

			String forbidden=s[1].substring(s[1].indexOf("{")+1);
			String forbiddens[]=forbidden.split(",");

			products[productsind]=new Product(requireds,forbiddens);
		}
		return products;
	}
	
	public String toString()
	{
		String s="";
		for (int i=0;i<elements.length;i++)
			s+="Product "+i+"\n"+elements[i].toString()+"\n";
		s+="< Matrix:\n";
		for (int i=0;i<elements.length;i++)
			s+=Arrays.toString(po[i])+"\n";
		return s;
	}
	
	public static void main(String[] args){
		//Product[] t=Family.readFile(System.getProperty("user.dir"),"fa.txt");
		Family test= new Family("fa.txt");
		System.out.println(test.toString());
	}
}
