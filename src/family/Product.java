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
	
	public Product(Set<Feature> required, Set<Feature> forbidden)
	{
		if (required==null||forbidden==null)
			throw new IllegalArgumentException();
		if (required.parallelStream()
				.anyMatch(f->forbidden.contains(f))
				||
				forbidden.parallelStream()
				.anyMatch(f->required.contains(f)))
			throw new IllegalArgumentException("A feature is both required and forbidden");
		
		this.required=required;
		this.forbidden=forbidden;
	}
	
	public Product(String[] r, String[] f)
	{
		this(Arrays.stream(r).map(s->new Feature(CALabel.getUnsignedAction(s))).collect(Collectors.toSet()),
		Arrays.stream(f).map(s->new Feature(CALabel.getUnsignedAction(s))).collect(Collectors.toSet()));
	}
	
	public Set<Feature> getRequired()
	{
		return required;
	}
	
	public Set<Feature> getForbidden()
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
		return this.forbidden.containsAll(p.getForbidden())&&this.required.containsAll(p.getRequired());
	}
	
	/**
	 * check if all forbidden features of p are contained 
	 * @param p
	 * @return
	 */
	public boolean containsAllForbiddenFeatures(Product p)
	{
		return this.forbidden.containsAll(p.getForbidden());
	}
	
	/**
	 * check if all required features of p are contained 
	 * @param p
	 * @return
	 */
	public boolean containsAllRequiredFeatures(Product p)
	{
		return this.required.containsAll(p.getRequired());
	}
	
	
	/**
	 * 
	 * @param f
	 * @return  true if feature f is contained (either required or forbidden)
	 */
	public boolean containFeature(Feature f)
	{
		Product rp = new Product(new HashSet<Feature>(Arrays.asList(f)),new HashSet<Feature>());
		Product fp = new Product(new HashSet<Feature>(),new HashSet<Feature>(Arrays.asList(f)));
		return (this.containsAllRequiredFeatures(rp)||this.containsAllForbiddenFeatures(fp));
	}
	
	public Product removeFeature(Feature f)
	{
		Set<Feature> req = new HashSet<>(this.required);
		Set<Feature> frb = new HashSet<>(this.forbidden);
		
		if (!req.remove(f))
			if (!frb.remove(f))
				return this;
		return new Product(req,frb);
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
	}

	public boolean isForbidden(MSCATransition t)
	{
		Feature f = new Feature(t.getLabel().getUnsignedAction());
		return this.getForbidden().contains(f);
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
	}
	
	public String toStringFile(int id)
	{
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

