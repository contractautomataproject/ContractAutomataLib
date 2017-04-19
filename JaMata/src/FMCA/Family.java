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
	private int[][] reversepo; //matrix po[i][j]==1 iff elements[i]>elements[j]
	private int[][] depth; //depth[i] level i -- list of products
	private int[] pointerToLevel; //i index to po, pointerLevel[i] index to depth[totfeatures i]
	private boolean[] hasParents;// hasParents[i]==true iff there exists j s.t. reversepo[i][j]=1
	public Family(Product[] elements, int[][] po)
	{
		this.elements=elements;
		this.po=po;
	}
	
	public Family(Product[] elements)
	{
		this.elements=elements;
		this.generatePO();
	}
	
	public Family(String filename)
	{
		this.elements=Family.readFile(System.getProperty("user.dir"),filename);
		this.generatePO();
	}
	
	public Product[] getProducts()
	{
		return elements;
	}
	
	public int[][] getPartialOrder()
	{
		return po;
	}
	
	public int[][] getReversePO()
	{
		return reversepo;
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
	protected void generatePO()
	{
		depth=new int[100][100];//TODO upper bounds;	
		int[] depthcount=new int[100];//TODO upperbound  count the number of products at each level of depth
		for (int i=0;i<depthcount.length;i++)
			depthcount[i]=0;
		Product[] p=this.elements;
		po=new int[p.length][p.length]; 
		reversepo=new int[p.length][p.length]; 
		hasParents=new boolean[p.length];
		for (int i=0;i<p.length;i++)
			hasParents[i]=false;
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
					if (p[i].containsFeatures(p[j]))
					{
						po[i][j]=1;
						reversepo[j][i]=1;
						hasParents[i]=true;
					}
					else
					{
						po[i][j]=0;
						reversepo[j][i]=0;
					}
				}
				else
				{
					po[i][j]=0;
					reversepo[j][i]=0;
				}
				
				if (p[j].getForbiddenAndRequiredNumber()==p[i].getForbiddenAndRequiredNumber()+1)//1 level of depth
				{
					if (p[j].containsFeatures(p[i]))
					{
						po[j][i]=1;
						reversepo[i][j]=1;
						hasParents[j]=true;
					}
					else
					{
						po[j][i]=0;
						reversepo[i][j]=0;
					}
				}
				else
				{
					po[j][i]=0;
					reversepo[i][j]=0;
				}
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
	
	/**
	 * 
	 * @param aut
	 * @return an new family with only products valid in aut
	 */
	public Family validProducts(FMCA aut)
	{
		boolean[] valid=new boolean[elements.length];
		for (int i=0;i<elements.length;i++)
			valid[i]=false; //initialise
		int[] tv = getTopProducts();
		for (int i=0;i<tv.length;i++)
			valid(valid,tv[i],aut);
		
		Product[] newp=new Product[elements.length];
		int count=0;
		for (int i=0;i<newp.length;i++)
		{
			if (valid[i])
			{
				newp[count]=elements[i];
				count++;
			}
		}
		newp=FMCAUtil.removeTailsNull(newp, count);
		return new Family(newp);
	}
	
	private void valid(boolean[] valid, int i, FMCA aut)
	{
		if (elements[i].isValid(aut))
		{
			valid[i]=true;
			for (int j=0;j<reversepo[i].length;j++)
			{
				if (reversepo[i][j]==1)
					valid(valid,j,aut);
			}
		}//do not visit subtree if not valid
	}
	
	/**
	 * 
	 * @return all top products p s.t. there not exists p'>p
	 */
	public int[] getTopProducts()
	{
		int[] tp=new int[elements.length];
		int count=0;
		for (int i=0;i<elements.length;i++) 
		{
			if (!hasParents[i])
			{
				tp[count]=i;
				count++;
			}
		}
		tp=FMCAUtil.removeTailsNull(tp, count);
		return tp;
	}
	
	
	/**
	 * 
	 * @param aut
	 * @return  the indexes in this.elements of canonical products
	 */
	public int[] getCanonicalProducts(FMCA aut)
	{
		Family f=this.validProducts(aut); //prefilter
		Product[] p=f.getProducts();
		int[] ind= f.getTopProducts(); 
		FMCA[] K= new FMCA[p.length];
		int nonemptylength=0;
		int[] nonemptyindex= new int[p.length];
		for (int i=0;i<ind.length;i++)
		{
			K[i]=aut.mpc(p[ind[i]]);
			if (K[i]!=null)
			{
				nonemptyindex[nonemptylength]=ind[i]; //index in the array of products
				nonemptylength++;
			}
		}
		
		//quotient by forbidden actions: initialise
		int[][] quotient = new int[nonemptylength][nonemptylength]; //upperbound
		int quotientclasses=0;
		int[] classlength=new int[nonemptylength]; //upperbound
		boolean[] addedToClass=new boolean[nonemptylength];
		for (int i=0;i<nonemptylength;i++)
		{
			addedToClass[i]=false;
			classlength[i]=0;
		}
		//build
		for (int i=0;i<nonemptylength;i++) 
		{
			if (addedToClass[i]==false) //not added previously
			{
				addedToClass[i]=true;
				quotient[quotientclasses][classlength[quotientclasses]]=nonemptyindex[i]; //index in the array of products
				classlength[quotientclasses]++;
				for (int j=i+1;j<nonemptylength;j++)
				{
					if (p[nonemptyindex[i]].containsForbiddenFeatures(p[nonemptyindex[j]]))
					{
						addedToClass[j]=true;
						quotient[quotientclasses][classlength[quotientclasses]]=nonemptyindex[j]; //index in the array of products
						classlength[quotientclasses]++;
					}
				}
				quotientclasses++;
			}
		}
		//take as canonical product the first element of each class
		int[] canonicalproducts=new int[quotientclasses];
		for (int i=0;i<quotientclasses;i++)
		{
			canonicalproducts[i]=quotient[i][0];
		}
		return canonicalproducts;
	}
	
	public FMCA getMPCofFamily(FMCA aut)
	{
		Family f=this.validProducts(aut); //prefilter
		Product[] p=f.getProducts();
		int[] ind= f.getTopProducts(); 
		FMCA[] K= new FMCA[p.length];
		int nonemptylength=0;
		int[] nonemptyindex= new int[p.length];
		for (int i=0;i<ind.length;i++)
		{
			K[i]=aut.mpc(p[ind[i]]);
			if (K[i]!=null)
			{
				nonemptyindex[nonemptylength]=ind[i]; //index in the array of products
				nonemptylength++;
			}
		}
		
		//quotient by forbidden actions: initialise
		int[][] quotient = new int[nonemptylength][nonemptylength]; //upperbound
		int quotientclasses=0;
		int[] classlength=new int[nonemptylength]; //upperbound
		boolean[] addedToClass=new boolean[nonemptylength];
		for (int i=0;i<nonemptylength;i++)
		{
			addedToClass[i]=false;
			classlength[i]=0;
		}
		//build
		for (int i=0;i<nonemptylength;i++) 
		{
			if (addedToClass[i]==false) //not added previously
			{
				addedToClass[i]=true;
				quotient[quotientclasses][classlength[quotientclasses]]=nonemptyindex[i]; //index in the array of products
				classlength[quotientclasses]++;
				for (int j=i+1;j<nonemptylength;j++)
				{
					if (p[nonemptyindex[i]].containsForbiddenFeatures(p[nonemptyindex[j]]))
					{
						addedToClass[j]=true;
						quotient[quotientclasses][classlength[quotientclasses]]=nonemptyindex[j]; //index in the array of products
						classlength[quotientclasses]++;
					}
				}
				quotientclasses++;
			}
		}
		//take as canonical product the first element of each class
		
		FMCA[] K2= new FMCA[quotientclasses]; //K of all canonical products
		for (int i=0;i<quotientclasses;i++)
		{
			K2[i]=K[quotient[i][0]]; 
		}
		return FMCAUtil.union(K2);
	}
}
