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
	private int[][] depth; //depth[i] level i -- list of products
	private int[] pointerToLevel; //i index to po, pointerLevel[i] index to depth[totfeatures i]
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
	
	public int[][] getDepth()
	{
		return this.depth;
	}
	
	public int[] getPointerToLevel()
	{
		return this.pointerToLevel;
	}
	
	
	/**
	 * generate po of products, no transitive closure!
	 * @return
	 */
	protected int[][] generatePO()
	{
		depth=new int[100][100];//TODO upper bounds;	
		int[] depthcount=new int[100];//TODO upperbound  count the number of products at each level of depth
		for (int i=0;i<depthcount.length;i++)
			depthcount[i]=0;
		Product[] p=this.elements;
		int[][] po=new int[p.length][p.length]; 
		pointerToLevel=new int[p.length];
		int maxdepth=0;
		for (int i=0;i<p.length;i++)
		{
			if (p[i].getForbiddenAndRequiredNumber()>maxdepth)
				maxdepth=p[i].getForbiddenAndRequiredNumber();
			depth[p[i].getForbiddenAndRequiredNumber()][depthcount[p[i].getForbiddenAndRequiredNumber()]]=i;
			pointerToLevel[i]=depthcount[p[i].getForbiddenAndRequiredNumber()];
			depthcount[p[i].getForbiddenAndRequiredNumber()]+=1;
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
		
		//remove tails null
		int newdepth[][] = new int[maxdepth+1][];
		for (int i=0;i<newdepth.length;i++)
		{
			newdepth[i]= new int[depthcount[i]];
			for (int j=0;j<newdepth[i].length;j++)
			{
				newdepth[i][j]=depth[i][j];
			}
		}
		depth=newdepth;
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
}
