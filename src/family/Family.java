package family;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A family contains its products/configurations and subfamilies, organised as a partial order
 * 
 * @author Davide Basile
 *
 */
public class Family {

	private final Set<Product> products;
	private final Map<Product,Map<Boolean,Set<Product>>> po; 

	public Family(Set<Product> products)
	{
		if (products==null)
			throw new IllegalArgumentException();

		this.products=new HashSet<>(products);

		po=products.parallelStream()
				.collect(Collectors.toMap(Function.identity(), 
						prod->products.parallelStream()
						.filter(p->p.isComparableWith(prod))
						.filter(p->prod.compareTo(p)==1||prod.compareTo(p)==-1)
						.collect(Collectors.partitioningBy(p->prod.compareTo(p)==-1,Collectors.toSet()))));
	}

	public Set<Product> getProducts() {
		return products;
	}

	public Map<Product, Map<Boolean, Set<Product>>> getPo() {
		return po;
	}

	/**
	 * @return the maximum number of features available for a product i.e. the maximum depth of the po tree
	 */
	public int getMaximumDepth()
	{
		return products.parallelStream()
				.mapToInt(p->p.getForbiddenAndRequiredNumber())
				.max().orElse(0)+1; //also consider products with zero features	
	}

	public Set<Product> getSubProductsofProduct(Product prod)
	{
		return this.po.get(prod).get(false);
	}

	public Set<Product> getSuperProductsofProduct(Product prod)
	{
		return this.po.get(prod).get(true);
	}

	/**
	 * @return all maximal products p s.t. there is no p'>p
	 */
	public Set<Product> getMaximalProducts()
	{
		return this.po.entrySet().parallelStream()
				.filter(e->e.getValue().get(true).isEmpty())
				.map(Entry::getKey)
				.collect(Collectors.toSet());
	}
}

//END OF THE CLASS













/**
	 * @param p list of pairwise different products
	 * @param features  the features of the products
	 * @return  list containing all valid superproducts (aka subfamily)
	 */
	//	private static Product[] generateSuperProducts(Product[] p, String[] features)
	//	{
	//		Product[][] pl= new Product[features.length][];
	//		pl[features.length-1]=p;
	//		for (int level=features.length; level>1;level--)//start from the bottom of the tree, all features instantiated
	//		{
	//			Product[] newproducts= new Product[pl[level-1].length*(pl[level-1].length-1)]; //upperbound to the possible number of discovered new products 
	//			//String[]  featuresremoved = new String[pl[level-1].length*(pl[level-1].length-1)]; //debug
	//			int newprodind=0;
	//			for (int removedfeature=0; removedfeature<features.length;removedfeature++) //for each possible feature to be removed
	//			{
	//				for (int prodind=0; prodind<pl[level-1].length;prodind++)
	//				{
	//					if (pl[level-1][prodind].getForbiddenAndRequiredNumber()==level && 
	//							pl[level-1][prodind].containFeature(new Feature(features[removedfeature])))
	//					{
	//						for (int prodcompare=prodind+1; prodcompare<pl[level-1].length;prodcompare++)
	//						{
	//							//debug
	//							/*
	//							 * if ((prodind==0)&&(prodcompare==96)&&(removedfeature==9)) { boolean
	//							 * debug=true; }
	//							 */
	//
	//
	//							if (pl[level-1][prodcompare].getForbiddenAndRequiredNumber()==level 
	//									&& pl[level-1][prodcompare].containFeature(new Feature(features[removedfeature]))) 
	//								/*for each pair of products at the same level check if by removing the selected feature they 
	//								  are equals. This can happen only if the feature is forbidden in one product and required in the other 
	//								  product (the feature is contained in both products). No duplicates are inserted.
	//								 */
	//							{
	//								/*								Product debug=pl[level-1][prodind];
	//								Product debug2=pl[level-1][prodcompare];
	//								if (debug.equals(debug2))
	//								{
	//									boolean error=true;
	//								}
	//								 */								
	//								 String[] rf=new String[1];
	//								 rf[0]=features[removedfeature];
	//
	//								 Product p1 = new Product(FamilyUtils.setDifference(pl[level-1][prodind].getRequired(),rf,new String[] {}),
	//										 FamilyUtils.setDifference(pl[level-1][prodind].getForbidden(),rf,new String[] {}));
	//								 Product p2 = new Product(FamilyUtils.setDifference(pl[level-1][prodcompare].getRequired(),rf,new String[] {}),
	//										 FamilyUtils.setDifference(pl[level-1][prodcompare].getForbidden(),rf,new String[] {}));
	//								 if (p1.equals(p2))
	//								 {	//featuresremoved[newprodind]=features[removedfeature];
	//									 boolean alreadyinserted=false;
	//									 for (int z=0;z<newprodind;z++) //check if the product was not inserted previously by removing a different feature
	//									 {
	//										 if (p1.equals(newproducts[z]))
	//										 {
	//											 alreadyinserted=true;
	//											 break;
	//										 }
	//									 }
	//									 if (!alreadyinserted)
	//									 {
	//										 //new super product discovered!
	//										 newproducts[newprodind]=p1;
	//										 newprodind++;
	//									 }
	//
	//								 }
	//							}			
	//						}
	//					}
	//				}
	//			}
	//			if (newprodind>0)
	//			{
	//				newproducts=FamilyUtils.removeTailsNull(newproducts, newprodind, new Product[] {});
	//				//p=FMCAUtil.concat(p, newproducts);  // this can be optimised, because in the next iteration only newproducts need to be checked
	//				pl[level-2]=newproducts;
	//			}
	//			else
	//				break; //stop earlier when no products are discovered
	//		}
	//		for (int i=features.length-2;i>=0;i--)
	//		{	
	//			if (pl[i]!=null)
	//			{
	//				List<Product> lp= new ArrayList<>(Arrays.asList(p));
	//				lp.addAll(Arrays.asList(pl[i]));
	//				p=lp.toArray(p);
	//				//FMCAUtil.concat(p, pl[i]);  
	//			}
	//		}
	//		return p;
	//	}



// old snippet
//
//		Stream.iterate(this.getMaximalProductsNew().parallelStream()
//				.map(p->new AbstractMap.SimpleEntry<Product,MSCA>(p,new FMCA(aut.clone()).orchestration(p)))
//				.collect(Collectors.partitioningBy(e->e.getValue()!=null, Collectors.toMap(Entry::getKey, Entry::getValue))), 
//				mcp -> !mcp.get(false).isEmpty(), 
//				mcp-> {Map<Boolean, Map<Product, MSCA>>  temp = mcp.get(false).entrySet().parallelStream()
//				.map(Entry::getKey)
//				.flatMap(p->this.pom.get(p).get(false).stream())//going down of one level
//				.filter(p1->!mcp.get(true).entrySet().parallelStream()//filtering if it is not maximal
//						.map(Entry::getKey)
//						.filter(p2->p2.getForbiddenAndRequiredNumber()==p1.getForbiddenAndRequiredNumber()-1)
//						.flatMap(p2->this.pom.get(p2).get(false).stream())
//						.anyMatch(p2->p2.equals(p1)))
//				.map(p->new AbstractMap.SimpleEntry<Product,MSCA>(p,new FMCA(aut.clone()).orchestration(p)))
//				.collect(Collectors.partitioningBy(e->e.getValue()!=null, Collectors.toMap(Entry::getKey, Entry::getValue)));
//				mcp.merge(true, temp.get(true), (x,y)->{x.putAll(y); return x;});
//				mcp.put(false, temp.get(false));
//				return mcp;} );


//old methods

/**
 * generate po of products, no transitive closure!
 * @return
 */
//protected void generatePO()
//{
//	Product[] p=this.elements;
//
//	//warning: the level of depth is determined by the number of features. There could be only one product 
//	//at level of depth 3 if it has 3 features and it is the only valid product
//
//	int maximumDepth = this.getMaximumDepthNew();
//
//	depth=new int[maximumDepth][p.length];
//	int[] depthcount=new int[maximumDepth]; 
//
//	for (int i=0;i<depthcount.length;i++)
//		depthcount[i]=0;
//
//	po=new int[p.length][p.length]; 
//	reversepo=new int[p.length][p.length]; 
//	hasParents=new boolean[p.length];
//	for (int i=0;i<p.length;i++)
//		hasParents[i]=false;
//	pointerToLevel=new int[p.length];
//	int maxdepth=0;
//	for (int i=0;i<p.length;i++)
//	{
//		//: there should be no need anymore to compute the maximum depth 
//		if (p[i].getForbiddenAndRequiredNumber()>maxdepth)
//			maxdepth=p[i].getForbiddenAndRequiredNumber();
//		try{
//			depth[p[i].getForbiddenAndRequiredNumber()][depthcount[p[i].getForbiddenAndRequiredNumber()]]=i;
//		}
//		catch (Exception e)
//		{
//			//	int debug=p[i].getForbiddenAndRequiredNumber();
//			//	int debug2=depthcount[debug];
//		}
//		pointerToLevel[i]=depthcount[p[i].getForbiddenAndRequiredNumber()];
//
//
//
//		depthcount[p[i].getForbiddenAndRequiredNumber()]+=1;
//		for (int j=i+1;j<p.length;j++)
//		{
//			if (p[i].getForbiddenAndRequiredNumber()==p[j].getForbiddenAndRequiredNumber()+1)//1 level of depth
//			{
//				if (p[i].containsAllFeatures(p[j]))
//				{
//					po[i][j]=1;
//					reversepo[j][i]=1;
//					hasParents[i]=true;
//				}
//				else
//				{
//					po[i][j]=0;
//					reversepo[j][i]=0;
//				}
//			}
//			else
//			{
//				po[i][j]=0;
//				reversepo[j][i]=0;
//			}
//
//			if (p[j].getForbiddenAndRequiredNumber()==p[i].getForbiddenAndRequiredNumber()+1)//1 level of depth
//			{
//				//this condition is never satisfied in the tests
//				if (p[j].containsAllFeatures(p[i]))
//				{
//					po[j][i]=1;
//					reversepo[i][j]=1;
//					hasParents[j]=true;
//				}
//				else
//				{
//					po[j][i]=0;
//					reversepo[i][j]=0;
//				}
//			}
//			else
//			{
//				po[j][i]=0;
//				reversepo[i][j]=0;
//			}
//		}
//	}
//
//	//remove tail null
//	int newdepth[][] = new int[maxdepth+1][];
//	for (int i=0;i<newdepth.length;i++)
//	{
//		try
//		{
//			newdepth[i]= new int[depthcount[i]];
//		} catch (Exception e) {
//			System.out.println("debug"); 
//			e.printStackTrace();
//		}
//		for (int j=0;j<newdepth[i].length;j++)
//		{
//			newdepth[i][j]=depth[i][j];
//		}
//	}
//	depth=newdepth;
//}

//public int[] getSubProductsofProduct(int poindex)
//{
//	int[] ptl=this.pointerToLevel;
//	int[][] rpo=this.getReversePO();
//	int[][] depth=this.getDepth();
//	Product[] prod = this.getElements();
//	int[] subproducts= new int[rpo[poindex].length];
//	int pointer=0;
//	for (int ind=0;ind<rpo[poindex].length;ind++)
//	{
//		if (rpo[poindex][ind]==1)
//		{
//			//this condition is never satisfied in the tests
//
//			subproducts[pointer]=depth[prod[ind].getForbiddenAndRequiredNumber()][ptl[ind]];
//			pointer++;
//		}
//	}
//	subproducts = FamilyUtils.removeTailsNull(subproducts, pointer);
//	return subproducts;
//}

//public int getMaximumDepth()
//{
//	int max=0;
//	for (int i=0;i<this.elements.length;i++)
//	{
//		if (max<elements[i].getForbiddenAndRequiredNumber())
//			max = elements[i].getForbiddenAndRequiredNumber();
//	}
//	return max+1; //also consider products with zero features
//
//}
//public int[] getSuperProductsofProduct(int poindex)
//{
//	int[] ptl=this.pointerToLevel;
//	int[][] po=this.getPartialOrder();
//	int[][] depth=this.getDepth();
//	Product[] prod = this.getElements();
//	int[] supproducts= new int[po[poindex].length];
//	int pointer=0;
//	for (int ind=0;ind<po[poindex].length;ind++)
//	{
//		if (po[poindex][ind]==1)
//		{
//			supproducts[pointer]=depth[prod[ind].getForbiddenAndRequiredNumber()][ptl[ind]];
//			pointer++;
//		}
//	}
//	supproducts = FamilyUtils.removeTailsNull(supproducts, pointer);
//	return supproducts;
//}
/**
 * read products from file
 * @param currentdir
 * @param filename
 * @return
 * @throws IOException 
 */
//private static Product[] readFile(String filename) throws IOException{
//	//Path p=Paths.get(currentdir, filename);
//	Path p=Paths.get("", filename);
//
//	Charset charset = Charset.forName("ISO-8859-1");
//	List<String> lines = Files.readAllLines(p, charset);
//
//	String[] arr = lines.toArray(new String[lines.size()]);
//	Product[] arrproducts=new Product[arr.length];//fix max products
//	for(int productsind=0;productsind<arr.length;productsind++)
//	{
//		String[] s=arr[productsind].split("}"); //each line identifies a product			
//		String required=s[0].substring(s[0].indexOf("{")+1);
//		String requireds[]=required.split(",");
//
//		String forbidden=s[1].substring(s[1].indexOf("{")+1);
//		String forbiddens[]=forbidden.split(",");
//
//		arrproducts[productsind]=new Product(requireds,forbiddens);
//	}
//	return arrproducts;
//}
/**
 * 
 * the valid product method exploits the partial order so it starts from maximal products
 * 
 * @param aut
 * @return a new family with only products valid in aut
 */
//public int[] validProducts(MSCA aut)
//{
//	boolean[] valid=new boolean[elements.length];
//	for (int i=0;i<elements.length;i++)
//		valid[i]=false; //initialise
//	int[] tv = getMaximalProducts();
//	if (aut.getTransition().parallelStream()
//			.anyMatch(x->CALabel.getUnsignedAction(x.getLabel().getAction()).equals("dummy"))) //dummy is an epsilon move, it is only used in the union
//	{
//
//		CAState storeinitial=aut.getInitial();
//		for (int i=0;i<tv.length;i++)
//		{
//			//aut.setInitialCA(storeinitial); 
//			//FMCATransition[] tr=FMCATransition.getTransitionFrom(aut.getInitialCA(),aut.getTransition());
//
//			//this methods are used for union, try to not use them so that they are private
//			for (MSCATransition t : aut.getForwardStar(storeinitial))
//			{	
//				aut.setInitialCA(t.getTarget());
//				//MSCA aut = new MSCA()
//				MSCA newaut = aut.clone();//new FMCA(aut.clone()).orchestration(new Product(new String[0],new String[0]));
//
//				valid(valid,tv[i],newaut); //recursive method
//			}				
//		}
//
//	}
//	else
//	{
//		for (int i=0;i<tv.length;i++)
//			valid(valid,tv[i],aut); //recursive method
//	}
//
//	int[] newp=new int[elements.length];
//	int count=0;
//	for (int i=0;i<newp.length;i++)
//	{
//		if (valid[i])
//		{
//
//			newp[count]=i;
//			count++;
//		}
//	}
//	newp=FamilyUtils.removeTailsNull(newp, count);
//	return newp;
//}

/**
 * recursive method, if element[i] is valid than iterates on its children
 * @param valid   valid[i]=true if element[i] is valid
 * @param i   current element
 * @param aut  automaton to check
 */
//private void valid(boolean[] valid, int i, MSCA aut)
//{
//	if (elements[i].isValid(aut))
//	{
//		//condition never satisfied during tests
//
//		valid[i]=true;
//		for (int j=0;j<reversepo[i].length;j++)
//		{
//			if (reversepo[i][j]==1)
//				valid(valid,j,aut);
//		}
//	}//do not visit subtree if not valid
//}

//public int[] productsWithNonEmptyMPC(MSCA aut)
//{
//	int[] pr = new int[this.elements.length];
//	int count=0;
//	
//	for (int i=0;i<pr.length;i++)
//	{
//		if (new FMCA(aut.clone()).orchestration(this.elements[i])!=null)
//		{
//			pr[count]=i;
//			count++;
//		}
//	}
//	pr=FamilyUtils.removeTailsNull(pr, count);
//	return pr;
//}
/**
 * compute the MPC of family of all valid total products
 * @param aut
 * @param progressMonitor
 * @param pr[]   -- side effect, pr[0] indexes of total products with non-empty mpc
 * @return
 * @throws ExecutionException 
 * @throws InterruptedException 
 */
//public MSCA getMPCofFamilyWithoutPO(MSCA aut, int[][] pr) 
//{
//
//	int[] tot = depth[this.getMaximumDepth()-1]; //total are at maximum depth
//	Product[] p = this.getElements();
//	//compute the non-empty list of mpc
//	MSCA K[] = new MSCA[tot.length];
//	pr[0] = new int[tot.length]; 
//	int ind=0;
//
//	for (int i=0;i<tot.length;i++)
//	{	
//		//System.out.println(i);	
//		K[ind]= new FMCA(aut.clone()).orchestration(p[tot[i]]);
//		if (K[ind]!=null)
//		{
//			pr[0][ind]=i;
//			ind++;
//
//		}
//	}
//	K = Arrays.copyOf(K, ind);	
//	pr[0]=FamilyUtils.removeTailsNull(pr[0], ind);
//
//	return MSCA.union(Arrays.asList(K));//List.of(K));
//}

/**
 * 
 * @param aut		the plant
 * @param mpcOfFamily		side effect: if flag==true it will points to the mpc of family
 * @param getMpcOfFamily		flag
 * @param indexOfProducts		index in the array of products
 * @return	the array of canonical products
 */
//public Product[] getCanonicalProducts(MSCA aut, MSCA[] mpcOfFamily,boolean getMpcOfFamily,int[][] indexOfProducts)
//{
//	//Family f=this.validProducts(aut); //prefilter WARNING
//	Product[] p=this.getElements();
//	int[] ind= this.getMaximalProducts(); 
//	MSCA[] K= new MSCA[p.length];
//	int nonemptylength=0;
//	int[] nonemptyindex= new int[p.length];
//
//	//compute the non-empty list of mpc for maximal (aka top) products
//	for (int i=0;i<ind.length;i++)
//	{
//		K[ind[i]]=new FMCA(aut.clone()).orchestration(p[ind[i]]);
//		if (K[ind[i]]!=null)
//		{
//			nonemptyindex[nonemptylength]=ind[i]; //index in the array of products
//			nonemptylength++;
//		}
//	}
//
//	//quotient by forbidden actions: initialise
//	int[][] quotient = new int[nonemptylength][nonemptylength]; //upperbound
//	int quotientclasses=0;
//	int[] classlength=new int[nonemptylength]; //upperbound
//	boolean[] addedToClass=new boolean[nonemptylength];
//	for (int i=0;i<nonemptylength;i++)
//	{
//		addedToClass[i]=false;
//		classlength[i]=0;
//	}
//	//build
//	for (int i=0;i<nonemptylength;i++) 
//	{
//		if (addedToClass[i]==false) //not added previously
//		{
//			addedToClass[i]=true;
//			quotient[quotientclasses][classlength[quotientclasses]]=nonemptyindex[i]; //index in the array of products
//			classlength[quotientclasses]++;
//			for (int j=i+1;j<nonemptylength;j++)
//			{
//				/**
//				 * The quotient class considers all products with the same set of forbidden features, and 
//				 * <<ignoring  those features that are never displayed in the automaton>> (this is an improvement of Def.32 of JSCP2020).
//				 */
//				String[] act=aut.getTransition().parallelStream()
//						.map(x->CALabel.getUnsignedAction(x.getLabel().getAction()))
//						.collect(Collectors.toSet())
//						.toArray(new String[] {});
//				Product test1=new Product(new String[0],FamilyUtils.setIntersection(p[nonemptyindex[i]].getForbidden(),act, new String[] {}));
//				Product test2=new Product(new String[0],FamilyUtils.setIntersection(p[nonemptyindex[j]].getForbidden(),act,new String[] {}));
//				if (test1.containsAllForbiddenFeatures(test2)
//						&&	
//						test2.containsAllForbiddenFeatures(test1)//condition never satisfied during tests
//						)
//				{
//					//condition never satisfied during tests
//
//					addedToClass[j]=true;
//					quotient[quotientclasses][classlength[quotientclasses]]=nonemptyindex[j]; //index in the array of products
//					classlength[quotientclasses]++;
//				}
//			}
//			quotientclasses++;
//		}
//	}
//	//take as canonical product the first element of each class
//	Product[] canonicalproducts=new Product[quotientclasses];
//	MSCA[] K2= new MSCA[quotientclasses]; //K of all canonical products
//	indexOfProducts[0]=new int[quotientclasses];
//	for (int i=0;i<quotientclasses;i++)
//	{
//		indexOfProducts[0][i]=quotient[i][0];
//		canonicalproducts[i]=p[quotient[i][0]];
//		K2[i]=K[quotient[i][0]]; 
//	}
//	if (getMpcOfFamily)
//		mpcOfFamily[0]=MSCA.union(Arrays.asList(K2));  //List.of(K2)); //store the mpc of family if needed
//	return canonicalproducts;
//}

//public MSCA getMPCofFamily(MSCA aut)
//{
//	MSCA[] mpcf=new MSCA[1];
//	this.getCanonicalProducts(aut, mpcf,true,new int[1][]); //as side effect the mpc is computed
//	return mpcf[0];
//}
//public int[] getMaximalProducts()
//{
//	int[] tp=new int[elements.length];
//	int count=0;
//	for (int i=0;i<elements.length;i++) 
//	{
//		if (!hasParents[i])
//		{
//			tp[count]=i;
//			count++;
//		}
//	}
//	tp=FamilyUtils.removeTailsNull(tp, count);
//	return tp;
//}
//	private int[][] po; //matrix po[i][j]==1 iff elements[i]<elements[j]
//private int[][] reversepo; //matrix reversepo[i][j]==1 iff elements[i]>elements[j]
////po[i][j]==reversepo[j][i]
//private int[][] depth; //depth[i] level i -- list of products, depth[i][j] index to elements
//private int[] pointerToLevel; //i index to elements, pointerLevel[i] index to depth[totfeatures i] 
////depth[i][j]=z iff pointerToLevel[z]=j
//private boolean[] hasParents;// hasParents[i]==true iff there exists j s.t. (reverse??)po[i][j]=1
