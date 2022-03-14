package io.github.contractautomataproject.catlib.family;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class representing a family of products. 
 * A family contains its products/configurations and may contain also its subfamilies, organised as a partial order
 * 
 * @author Davide Basile
 *
 */
public class Family {

	private final Set<Product> products;
	private final Map<Product,Map<Boolean,Set<Product>>> po; 

	public Family(Set<Product> products)
	{
		this(products, (p1,p2) -> (p2.getForbidden().containsAll(p1.getForbidden()) && p2.getRequired().containsAll(p1.getRequired()))
				|| (p1.getForbidden().containsAll(p2.getForbidden())&&	p1.getRequired().containsAll(p2.getRequired())), 
				(p1,p2) -> p1.getForbiddenAndRequiredNumber()-p2.getForbiddenAndRequiredNumber());		
	}

	/**
	 * This constructor also instantiate the partial order
	 * 
	 * @param products the products of the family
	 * @param areComparable  a predicate to compare to check if two products can be compared
	 * @param compare the function to compare two products
	 */
	public Family(Set<Product> products, BiPredicate<Product,Product> areComparable, BiFunction<Product, Product, Integer> compare)
	{
		if (products==null||areComparable==null||compare==null)
			throw new IllegalArgumentException();
		
		this.products=new HashSet<>(products);
		this.po=products.parallelStream()
				.collect(Collectors.toMap(Function.identity(), 
						p1->products.parallelStream()
						.filter(p2-> areComparable.test(p1,p2))//are comparable
						.filter(p2-> compare.apply(p1,p2)==1||compare.apply(p1,p2)==-1)
						.collect(Collectors.partitioningBy(p2->compare.apply(p2, p1)==-1,Collectors.toSet()))));

	}

	public Set<Product> getProducts() {
		return new HashSet<>(products);
	}

	public Map<Product, Map<Boolean, Set<Product>>> getPo() {
		return new HashMap<>(po);
	}

	/**
	 * @return the maximum number of features available for a product i.e. the maximum depth of the po tree
	 */
	public int getMaximumDepth()
	{
		return products.parallelStream()
				.mapToInt(Product::getForbiddenAndRequiredNumber)
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
	 * @return all maximal products p s.t. there is no p'greater than p
	 */
	public Set<Product> getMaximalProducts()
	{
		return this.po.entrySet().parallelStream()
				.filter(e->e.getValue().get(true).isEmpty())
				.map(Entry::getKey)
				.collect(Collectors.toSet());
	}

	@Override
	public int hashCode() {
		return Objects.hash(products);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null||getClass() != obj.getClass())
			return false;
		Family other = (Family) obj;
		return products.equals(other.products);
	}

	@Override
	public String toString() {
		return "Family [products=" + products + "]";
	}
}