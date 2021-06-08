package family;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import contractAutomata.CALabel;
import contractAutomata.MSCA;
import contractAutomata.MSCATransition;

/**
 * A configuration/product of a product line/family, identified as set of required and forbidden features
 * 
 * @author Davide Basile
 *
 */
public class Product {
	private final Set<Feature> required;
	private final Set<Feature> forbidden;
	
	public Product(String[] r, String[] f)
	{
		if (r==null || f==null)
			throw new IllegalArgumentException();
		//all positive integers, to avoid sign mismatches
//		String[] rp = new String[r.length];
//		for (int i=0;i<r.length;i++)
//			rp[i]=CALabel.getUnsignedAction(r[i]);
//
//		String[] fp = new String[f.length];
//		for (int i=0;i<f.length;i++)
//			fp[i]=CALabel.getUnsignedAction(f[i]);
		
		this.required=Arrays.stream(r).map(s->new Feature(CALabel.getUnsignedAction(s))).collect(Collectors.toSet());
		this.forbidden=Arrays.stream(f).map(s->new Feature(CALabel.getUnsignedAction(s))).collect(Collectors.toSet());
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
		//this method is only using when importing from featureide, probably 
		//there are duplicate strings and this method remove duplicates... need
		//to check better
		
		String[] rp = new String[r.length];
		for (int i=0;i<r.length;i++)
			rp[i]=CALabel.getUnsignedAction(r[i]);

		String[] fp = new String[f.length];
		for (int i=0;i<f.length;i++)
			fp[i]=CALabel.getUnsignedAction(f[i]);

		for (int i=0;i<eq.length;i++)
		{
			if (FamilyUtils.contains(eq[i][0], rp)&&FamilyUtils.contains(eq[i][1], rp))
			{
				//condition never satisfied during tests!
				
				int index=FamilyUtils.getIndex(rp, eq[i][1]);
				rp[index]=null;
			}
			else if (FamilyUtils.contains(eq[i][0], fp)&&FamilyUtils.contains(eq[i][1], fp)) //the feature cannot be both required and forbidden
			{
				//condition never satisfied during tests!
				
				int index=FamilyUtils.getIndex(fp, eq[i][1]);
				fp[index]=null;
			}
		}
		rp=FamilyUtils.removeHoles(rp, new String[] {});
		fp=FamilyUtils.removeHoles(fp, new String[] {}); 

		this.required=Arrays.stream(rp).map(s->new Feature(s)).collect(Collectors.toSet());
		this.forbidden=Arrays.stream(fp).map(s->new Feature(s)).collect(Collectors.toSet());
	}
	
	public Product(Set<Feature> required, Set<Feature> forbidden)
	{
		if (required==null||forbidden==null)
			throw new IllegalArgumentException();
		this.required=required;
		this.forbidden=forbidden;
	}
	
	public String[] getRequired()
	{
		return required.stream().map(f->f.getName()).toArray(String[]::new);
	}
	
	public String[] getForbidden()
	{
		return forbidden.stream().map(f->f.getName()).toArray(String[]::new);
	}
	
	public Set<Feature> getRequiredf()
	{
		return required;
	}
	public Set<Feature> getForbiddenf()
	{
		return forbidden;
	}
	
	public int getForbiddenAndRequiredNumber()
	{
		return required.size()+forbidden.size();
	}
	
	/**
	 * check if all features of p are contained 
	 * @param p
	 * @return
	 */
	public boolean containsAllFeatures(Product p)
	{
//		String[] rp=p.getRequired();
//		String[] rf=p.getForbidden();
//		for(int i=0;i<rp.length;i++)
//			if (!FamilyUtils.contains(rp[i], this.required))
//				return false;
//		for(int i=0;i<rf.length;i++)
//			if (!FamilyUtils.contains(rf[i], this.forbidden))
//				return false;
//		return true;
		return this.forbidden.containsAll(p.getForbiddenf())&&this.required.containsAll(p.getRequiredf());
	}
	
	/**
	 * check if all forbidden features of p are contained 
	 * @param p
	 * @return
	 */
	public boolean containsAllForbiddenFeatures(Product p)
	{
//		String[] rf=p.getForbidden();
//		for(int i=0;i<rf.length;i++)
//			if (!FamilyUtils.contains(rf[i], this.forbidden))
//				return false;
//		
//		return true;

		return this.forbidden.containsAll(p.getForbiddenf());
	}
	
	/**
	 * check if all required features of p are contained 
	 * @param p
	 * @return
	 */
	public boolean containsAllRequiredFeatures(Product p)
	{
//		String[] rf=p.getRequired();
//		for(int i=0;i<rf.length;i++)
//			if (!FamilyUtils.contains(rf[i], this.required))
//				return false;		
//		return true;
		return this.required.containsAll(p.getRequiredf());
	}
	
	
	/**
	 * 
	 * @param f
	 * @return  true if feature f is contained (either required or forbidden)
	 */
	public boolean containFeature(Feature f)
	{
		Product temp = new Product(new HashSet<Feature>(Arrays.asList(f)),new HashSet<Feature>(Arrays.asList(f)));
		return (this.containsAllRequiredFeatures(temp)||this.containsAllForbiddenFeatures(temp));
	}
	
	
	/**
	 * 
	 * @param tr
	 * @return true if all required actions are available in the transitions tr
	 */
	public boolean checkRequired(Set<? extends MSCATransition> tr)
	{
		Set<String> act=tr.parallelStream()
				.map(t->CALabel.getUnsignedAction(t.getLabel().getAction()))
				.collect(Collectors.toSet());
		return required.stream()
		.map(Feature::getName)
		.allMatch(s->act.contains(s));

//		for (int i=0;i<this.required.length;i++)
//		{
//			boolean found=false;
//			for (MSCATransition t : tr)
//			{
//				if (CALabel.getUnsignedAction(t.getLabel().getAction()).equals(this.required[i]))  //do not differ between requests and offers
//					found=true;
//			}
//			if (!found)
//				return false;
//		}
//		return true;
	}
	
	/**
	 * 
	 * @param t
	 * @return true if all forbidden actions are not available in the transitions t
	 */
	public boolean checkForbidden(Set<? extends MSCATransition> tr)
	{
		Set<String> act=tr.parallelStream()
				.map(t->CALabel.getUnsignedAction(t.getLabel().getAction()))
				.collect(Collectors.toSet());
		return forbidden.stream()
		.map(Feature::getName)
		.allMatch(s->!act.contains(s));
//		for (int i=0;i<this.forbidden.length;i++)
//		{
//			for (MSCATransition t : tr)
//			{
//				if (CALabel.getUnsignedAction(t.getLabel().getAction()).equals(this.forbidden[i]))  //do not differ between requests and offers
//					return false;
//			}
//		}
//		return true;
	}

	public boolean isForbidden(MSCATransition t)
	{
		Feature f = new Feature(t.getLabel().getUnsignedAction());
		return this.getForbiddenf().contains(f);
//		return (FamilyUtils.getIndex(this.getForbidden(),t.getLabel().getUnsignedAction())>=0);
	}

//	private boolean isRequired(MSCATransition t)
//	{
//		return (FMCAUtils.getIndex(this.getRequired(),t.getLabel().getUnsignedAction())>=0);		
//	}

		
	public boolean isValid(MSCA aut)
	{
		return this.checkForbidden(aut.getTransition())&&this.checkRequired(aut.getTransition());
	}
	
	@Override
	public String toString()
	{
		return "R:"+required.toString()+";\nF:"+forbidden.toString()+";\n";
//		return "R:"+Arrays.toString(required)+";\nF:"+Arrays.toString(forbidden)+";\n";
	}
	
	public String toStringFile(int id)
	{
//		String req="";
//		for (int i=0;i<required.length;i++)
//		{
//			req+=required[i]+",";
//		}
//		String forb="";
//		for (int i=0;i<forbidden.length;i++)
//		{
//			forb+=forbidden[i]+",";
//		}
		String req=required.stream()
				.map(f->f.getName())
				.collect(Collectors.joining(","));
		String forb=forbidden.stream()
				.map(f->f.getName())
				.collect(Collectors.joining(","));
		return "p"+id+": R={"+req+",} F={"+forb+",}";
	}
	
	public String toHTMLString(String s)
	{
        return "<html>"+s+" R:"+required.toString()+"<br />F:"+forbidden.toString()+"</html>";
	
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + forbidden.hashCode();
		result = prime * result + required.hashCode();
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
		return forbidden.equals(other.forbidden)&&required.equals(other.required);
	}

	public boolean isComparableWith(Product p)
	{
		return this.containsAllFeatures(p)||p.containsAllFeatures(this);
	}
	
	
	public int compareTo(Product p) {
		if (this.isComparableWith(p))
			return p.getForbiddenAndRequiredNumber()-this.getForbiddenAndRequiredNumber();
		else 
			throw new UnsupportedOperationException("Products are not comparable");
			
	}

}

//END OF CLASS

	
//	@Override
//	public Product clone()
//	{
//		return new Product(Arrays.copyOf(this.getRequired(), this.getRequired().length), 
//				Arrays.copyOf(this.getForbidden(), this.getForbidden().length));
//	}

