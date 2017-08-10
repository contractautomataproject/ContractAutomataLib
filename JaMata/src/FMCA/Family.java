package FMCA;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import CA.CAState;

public class Family {
	private Product[] elements;
	private int[][] po; //matrix po[i][j]==1 iff elements[i]<elements[j]
	private int[][] reversepo; //matrix po[i][j]==1 iff elements[i]>elements[j]
	private int[][] depth; //depth[i] level i -- list of products, depth[i][j] index to elements
	private int[] pointerToLevel; //i index to elements, pointerLevel[i] index to depth[totfeatures i]
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
		Product[] p=this.elements;
		depth=new int[p.length][p.length];//TODO upper bounds;	
		int[] depthcount=new int[p.length];//TODO upperbound  count the number of products at each level of depth
		for (int i=0;i<depthcount.length;i++)
			depthcount[i]=0;
		
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
			//try{
				depth[p[i].getForbiddenAndRequiredNumber()][depthcount[p[i].getForbiddenAndRequiredNumber()]]=i;
			//}
			//catch (Exception e)
			//{
			//	int debug=p[i].getForbiddenAndRequiredNumber();
			//	int debug2=depthcount[debug];
			//	int x;
			//}
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
			try
			{
			newdepth[i]= new int[depthcount[i]];
			} catch (Exception e) {
				System.out.println("debug"); 
				e.printStackTrace();
				}
			for (int j=0;j<newdepth[i].length;j++)
			{
				newdepth[i][j]=depth[i][j];
			}
		}
		depth=newdepth;
	}
	
	public int[] getSubProductsofProduct(int poindex)
	{
        int[] ptl=this.getPointerToLevel();
        int[][] rpo=this.getReversePO();
        int[][] depth=this.getDepth();
        Product[] prod = this.getProducts();
        int[] subproducts= new int[rpo[poindex].length];
        int pointer=0;
        for (int ind=0;ind<rpo[poindex].length;ind++)
        {
        	if (rpo[poindex][ind]==1)
        	{
            	subproducts[pointer]=depth[prod[ind].getForbiddenAndRequiredNumber()][ptl[ind]];
            	pointer++;
        	}
        }
        subproducts = FMCAUtil.removeTailsNull(subproducts, pointer);
        return subproducts;
	}
	
	public int[] getSuperProductsofProduct(int poindex)
	{
        int[] ptl=this.getPointerToLevel();
        int[][] po=this.getPartialOrder();
        int[][] depth=this.getDepth();
        Product[] prod = this.getProducts();
        int[] supproducts= new int[po[poindex].length];
        int pointer=0;
        for (int ind=0;ind<po[poindex].length;ind++)
        {
        	if (po[poindex][ind]==1)
        	{
            	supproducts[pointer]=depth[prod[ind].getForbiddenAndRequiredNumber()][ptl[ind]];
            	pointer++;
        	}
        }
        supproducts = FMCAUtil.removeTailsNull(supproducts, pointer);
        return supproducts;
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
	
	/**
	 * loads the list of products generated through FeatureIDE
	 * 
	 * @param currentdir
	 * @param filename
	 * @return
	 */
	public static Product[] importFamily(String currentdir, String filename)
	{	
		String[] features=getFeatures(filename);
		String[][] eq = detectDuplicates(filename);
		int counteq=0;
		for (int i=0;i<eq.length;i++)
		{
			if (FMCAUtil.contains(eq[i][0], features)&&FMCAUtil.contains(eq[i][1], features))
			{
				int index=FMCAUtil.getIndex(features, eq[i][1]);
				features[index]=null;
				counteq++;
			}
		}
		features=FMCAUtil.removeHoles(features,counteq);
		currentdir=currentdir.substring(0, currentdir.lastIndexOf("\\"));
		currentdir+="\\products\\";
		File folder = new File(currentdir);
		File[] listOfFiles = folder.listFiles();
		Product[] pr=new Product[listOfFiles.length];
		int prlength=0;
		    for (int i = 0; i < listOfFiles.length; i++) {
				Path p=null;
				boolean found=true;
				if (listOfFiles[i].isFile()&&listOfFiles[i].getName().contains("config")) { //no sub-directory on products
					p=Paths.get("", listOfFiles[i].getAbsolutePath());
				}
				else if (listOfFiles[i].isDirectory())
				{
					File[] ff = listOfFiles[i].listFiles();
					if (ff!=null && ff.length>0 && ff[0]!=null && ff[0].isFile()&&ff[0].getName().contains("config")) //each product has its own sub-directory
					{
						p=Paths.get("", ff[0].getAbsolutePath());
					}
					else
						found=false;
				}
				if (found)
				{
					Charset charset = Charset.forName("ISO-8859-1");
					List<String> lines = null;
					try {
						lines = Files.readAllLines(p, charset);
					} catch (IOException e) {
						e.printStackTrace();
					}
					/*if (prlength==0||prlength==15)
					{
						System.out.println();
					}
*/					String[] f1 = lines.toArray(new String[lines.size()]); //required features
					Product pro = new Product(FMCAUtil.setIntersection(f1, features), FMCAUtil.setDifference(features, f1), eq); 
					boolean alreadyinserted=false;
					/**
					 * product generation of featureide may generate duplicate product!
					 */
					for (int z=0;z<prlength;z++) 
					{
						if (pro.equals(pr[z]))
						{
							alreadyinserted=true;
							break;
						}
					}
					if (!alreadyinserted)
					{
						pr[prlength]=pro;
						prlength++;
					}
				}		      
		    }
		pr=FMCAUtil.removeTailsNull(pr, prlength);
		return generateSuperProducts(pr,features);
	}
	
	/**
	 * 
	 * @param index  index[i]  index in elements
	 * @return	the array of products indexed by index[]
	 */
	public Product[] subsetOfProductsFromIndex(int[] index)
	{
		Product[] subset = new Product[index.length];
		for (int i=0;i<index.length;i++)
		{
			subset[i]=this.elements[index[i]];
		}
		return subset;
					
	}
	
	
	/**
	 * 
	 * given two products p1 p2 identical but for a feature f activated in one 
	 * and deactivated in the other, a super product (a.k.a. sub-family) is generated such that f is left unresolved. 
	 * This method generates all possible super products. 
	 * It is required that all super products are such that the corresponding feature model formula is satisfied. 
	 * This condition holds for the method.
	 * Indeed, assume the feature model formula is in CNF, it is never the case that f is the only literal of a 
	 * disjunct (i.e. a truth value must be assigned to f); otherwise either p1 or p2 
	 * is not a valid product (p1 if f is negated in the disjunct, p2 otherwise).
	 * 
	 * 
	 * @param p list of pairwise different products
	 * @param features  the features of the products
	 * @return  list containing all valid superproducts (aka subfamily)
	 */
	private static Product[] generateSuperProducts(Product[] p, String[] features)
	{
		if ((p==null)||features==null)
			return null;

		Product[][] pl= new Product[features.length][];
		pl[features.length-1]=p;
		for (int level=features.length; level>1;level--)//start from the bottom of the tree, all features instantiated
		{
			Product[] newproducts= new Product[pl[level-1].length*(pl[level-1].length-1)]; //upperbound to the possible number of discovered new products 
			//String[]  featuresremoved = new String[pl[level-1].length*(pl[level-1].length-1)]; //debug
			int newprodind=0;
			for (int removedfeature=0; removedfeature<features.length;removedfeature++) //for each possible feature to be removed
			{
				for (int prodind=0; prodind<pl[level-1].length;prodind++)
				{
					if (pl[level-1][prodind].getForbiddenAndRequiredNumber()==level && pl[level-1][prodind].containFeature(features[removedfeature]))
					{
						for (int prodcompare=prodind+1; prodcompare<pl[level-1].length;prodcompare++)
						{
							//debug
							if ((prodind==0)&&(prodcompare==96)&&(removedfeature==9))
							{
								boolean debug=true;
							}
							
							
							if (pl[level-1][prodcompare].getForbiddenAndRequiredNumber()==level && pl[level-1][prodcompare].containFeature(features[removedfeature])) 
								/*for each pair of products at the same level check if by removing the selected feature they 
								  are equals. This can happen only if the feature is forbidden in one product and required in the other 
								  product (the feature is contained in both products). No duplicates are inserted.
								 */
							{
/*								Product debug=pl[level-1][prodind];
								Product debug2=pl[level-1][prodcompare];
								if (debug.equals(debug2))
								{
									boolean error=true;
								}
*/								String[] rf=new String[1];
								rf[0]=features[removedfeature];
								Product p1 = new Product(FMCAUtil.setDifference(pl[level-1][prodind].getRequired(),rf),
										FMCAUtil.setDifference(pl[level-1][prodind].getForbidden(),rf));
								Product p2 = new Product(FMCAUtil.setDifference(pl[level-1][prodcompare].getRequired(),rf),
										FMCAUtil.setDifference(pl[level-1][prodcompare].getForbidden(),rf));
								if (p1.equals(p2))
								{	//featuresremoved[newprodind]=features[removedfeature];
									boolean alreadyinserted=false;
									for (int z=0;z<newprodind;z++) //check if the product was not inserted previously by removing a different feature
									{
										if (p1.equals(newproducts[z]))
										{
											alreadyinserted=true;
											break;
										}
									}
									if (!alreadyinserted)
									{
										//new super product discovered!
										newproducts[newprodind]=p1;
										newprodind++;
									}
									
								}
							}			
						}
					}
				}
			}
			if (newprodind>0)
			{
				newproducts=FMCAUtil.removeTailsNull(newproducts, newprodind);
				//p=FMCAUtil.concat(p, newproducts);  // this can be optimised, because in the next iteration only newproducts need to be checked
				pl[level-2]=newproducts;
			}
			else
				break; //stop earlier when no products are discovered
		}
		for (int i=features.length-2;i>=0;i--)
		{	
			if (pl[i]!=null)
				p=FMCAUtil.concat(p, pl[i]);  
		}
		return p;
	}
	
	private static String[] getFeatures(String filename)
	{
		String[] features=null;
		try {
	         File inputFile = new File(filename);
	         DocumentBuilderFactory dbFactory 
	            = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder;

	         dBuilder = dbFactory.newDocumentBuilder();

	         Document doc = dBuilder.parse(inputFile);
	         doc.getDocumentElement().normalize();

	         //XPath xPath =  XPathFactory.newInstance().newXPath();
	         
	         //NodeList nodeList = (NodeList) xPath.compile("").evaluate(doc, XPathConstants.NODESET);
	         NodeList nodeList = (NodeList) doc.getElementsByTagName("feature");
	         
	         features=new String[nodeList.getLength()];
	         
	         int ind =0;
	         for (int i = 0; i < nodeList.getLength(); i++) 
	         {
	            Node nNode = nodeList.item(i);
	          //  System.out.println("\nCurrent Element :" 
	          //     + nNode.getNodeName());
	            if ((nNode.getNodeType() == Node.ELEMENT_NODE))//&&(nNode.getNodeName()=="mxCell")) {
	            {
	               Element eElement = (Element) nNode;
	               features[i]=eElement.getAttribute("name");    
	               ind++;
	            }       
	        }
	        features=FMCAUtil.removeTailsNull(features, ind);
	      } catch (ParserConfigurationException e) {
	         e.printStackTrace();
	      } catch (SAXException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (Exception e) {
		         e.printStackTrace();
		      } 
		return features;		
	}

	/**
	 * reads all iff constraints (eq node) and returns a table such that forall i table[i][0] equals table[i][1]
	 * @param filename
	 * @return
	 */
	private static String[][] detectDuplicates(String filename)
	{
		String[][] table = null;
		try {
	         File inputFile = new File(filename);
	         DocumentBuilderFactory dbFactory 
	            = DocumentBuilderFactory.newInstance();
	         DocumentBuilder dBuilder;

	         dBuilder = dbFactory.newDocumentBuilder();

	         Document doc = dBuilder.parse(inputFile);
	         doc.getDocumentElement().normalize();

	         //XPath xPath =  XPathFactory.newInstance().newXPath();
	         
	         //NodeList nodeList = (NodeList) xPath.compile("").evaluate(doc, XPathConstants.NODESET);
	         NodeList nodeList = (NodeList) doc.getElementsByTagName("eq");
	         
	         table= new String[nodeList.getLength()][2]; //exact length
	         
	         int ind =0;
	         for (int i = 0; i < nodeList.getLength(); i++) 
	         {
	            Node nNode = nodeList.item(i);
	          //  System.out.println("\nCurrent Element :" 
	          //     + nNode.getNodeName());
	            if ((nNode.getNodeType() == Node.ELEMENT_NODE))//&&(nNode.getNodeName()=="mxCell")) {
	            {
	            	NodeList childs = (NodeList) nNode.getChildNodes();
	               Node first = childs.item(1);
	               Node second = childs.item(3);
	               table[ind][0]= first.getTextContent();    
	               table[ind][1]= second.getTextContent();          
	               ind++;
	            }       
	        }
	      } catch (ParserConfigurationException e) {
	         e.printStackTrace();
	      } catch (SAXException e) {
	         e.printStackTrace();
	      } catch (IOException e) {
	         e.printStackTrace();
	      } catch (Exception e) {
		         e.printStackTrace();
		      } 
		return table;		
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
	 * the valid product method exploit the partial order so it starts from top products
	 * 
	 * @param aut
	 * @return a new family with only products valid in aut
	 */
	public int[] validProducts(FMCA aut)
	{
		boolean[] valid=new boolean[elements.length];
		for (int i=0;i<elements.length;i++)
			valid[i]=false; //initialise
		int[] tv = getTopProducts();
		if (aut.containAction("dummy")) //dummy is an epsilon move
		{
			CAState storeinitial=aut.getInitialCA();
			for (int i=0;i<tv.length;i++)
			{
				aut.setInitialCA(storeinitial);
				FMCATransition[] tr=FMCATransition.getTransitionFrom(aut.getInitialCA(),aut.getTransition());
				for (int j=0;j<tr.length;j++)
				{	
					aut.setInitialCA(tr[j].getTargetP());
					FMCA newaut = aut.mpc(new Product(new String[0],new String[0]));
					valid(valid,tv[i],newaut); //recursive method
				}				
			}
		
		}
		else
		{
			for (int i=0;i<tv.length;i++)
				valid(valid,tv[i],aut); //recursive method
		}
		
		int[] newp=new int[elements.length];
		int count=0;
		for (int i=0;i<newp.length;i++)
		{
			if (valid[i])
			{
				newp[count]=i;
				count++;
			}
		}
		newp=FMCAUtil.removeTailsNull(newp, count);
		return newp;
	}
	
	public int[] productsWithNonEmptyMPC(FMCA aut)
	{
		int[] pr = new int[this.elements.length];
		int count=0;
		//TODO exploit theoretical results to speed up the computation (intersection of MPC, lattice)
		for (int i=0;i<pr.length;i++)
		{
			if (aut.mpc(this.elements[i])!=null)
			{
				pr[count]=i;
				count++;
			}
		}
		pr=FMCAUtil.removeTailsNull(pr, count);
		return pr;
	}
	
	
	/**
	 * recursive method, if element[i] is valid than iterates on its children
	 * @param valid   valid[i]=true if element[i] is valid
	 * @param i   current element
	 * @param aut  automaton to check
	 */
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
	 * @param aut		the plant
	 * @param mpcOfFamily		side effect: if flag==true it will points to the mpc of family
	 * @param getMpcOfFamily		flag
	 * @param indexOfProducts		index in the array of products
	 * @return	the array of canonical products
	 */
	public Product[] getCanonicalProducts(FMCA aut, FMCA[] mpcOfFamily,boolean getMpcOfFamily,int[][] indexOfProducts)
	{
		//Family f=this.validProducts(aut); //prefilter WARNING
		Product[] p=this.getProducts();
		int[] ind= this.getTopProducts(); 
		FMCA[] K= new FMCA[p.length];
		int nonemptylength=0;
		int[] nonemptyindex= new int[p.length];
		for (int i=0;i<ind.length;i++)
		{
			Product ppp=p[ind[i]];
			K[ind[i]]=aut.mpc(p[ind[i]]);
			if (K[ind[i]]!=null)
			{
				aut.mpc(p[ind[i]]);
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
					/**
					 * The quotient class consider all products with the same set of forbidden features, ignoring 
					 * those features that are never displayed in the automaton
					 */
					String[] act=aut.getActions();
					Product test1=new Product(new String[0],FMCAUtil.setIntersection(p[nonemptyindex[i]].getForbidden(),act));
					Product test2=new Product(new String[0],FMCAUtil.setIntersection(p[nonemptyindex[j]].getForbidden(),act));
					if (test1.containsForbiddenFeatures(test2)
						&&	
						test2.containsForbiddenFeatures(test1)
						)
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
		Product[] canonicalproducts=new Product[quotientclasses];
		FMCA[] K2= new FMCA[quotientclasses]; //K of all canonical products
		indexOfProducts[0]=new int[quotientclasses];
		for (int i=0;i<quotientclasses;i++)
		{
			indexOfProducts[0][i]=quotient[i][0];
			canonicalproducts[i]=p[quotient[i][0]];
			K2[i]=K[quotient[i][0]]; 
		}
		if (getMpcOfFamily)
			mpcOfFamily[0]=FMCAUtil.union(K2); //store the mpc of family if needed
		return canonicalproducts;
	}
	
	public FMCA getMPCofFamily(FMCA aut)
	{
		FMCA[] mpcf=new FMCA[1];
		this.getCanonicalProducts(aut, mpcf,true,new int[1][]);
		return mpcf[0];
	}
}
