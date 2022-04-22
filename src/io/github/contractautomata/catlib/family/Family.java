package io.github.contractautomata.catlib.family;

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
 * Class implementing a family of products (i.e., a product line). <br>
 * A family is represented by its products (or configurations). <br>
 * In featured modal contract automata, partial products are also considered, also known as sub-families. <br>
 * In a partial product not all features are rendered as required or forbidden. <br>
 * The sub-products are partially ordered.<br>
 *
 * The formal definitions can be found in:
 *  * <ul>
 *  *  *     <li>Basile, D. et al., 2020.
 *  *  *     Controller synthesis of service contracts with variability. Science of Computer Programming, vol. 187, pp. 102344.
 *  *  *      (<a href="https://doi.org/10.1016/j.scico.2019.102344">https://doi.org/10.1016/j.scico.2019.102344</a>)</li>
 *  *  </ul>
 *
 * @author Davide Basile
 *
 */
public class Family {

	/**
	 * the set of products.
	 */
	private final Set<Product> products;

	/**
	 * the partial order of products.
	 * A map such that for each product (key) a map is returned (value).
	 * The value is amp partitioning in false/true the sub/super products of the key, where
	 * a sub product contains all the features (required/forbidden) of its
	 * super product.
	 */
	private final Map<Product,Map<Boolean,Set<Product>>> po;

	/**
	 * a predicate for checking if two products are comparable.
	 */
	private final BiPredicate<Product,Product> areComparable;

	/**
	 * a predicate for comparing two comparable products.
	 */
	private final BiFunction<Product, Product, Integer> compare;

	/**
	 * Constructor of a family from a set of products.
	 * In this constructor, two products are comparable if one (p1) contains all required and forbidden features of the other (p2),
	 * and in this case  p1 is less than p2.
	 *
	 * @param products the set of products.
	 */
	public Family(Set<Product> products)
	{
		this(products,
				(p1,p2) -> (p2.getForbidden().containsAll(p1.getForbidden())
						&& p2.getRequired().containsAll(p1.getRequired()))
						|| (p1.getForbidden().containsAll(p2.getForbidden())
						&& p1.getRequired().containsAll(p2.getRequired())),
				(p1,p2) -> p1.getForbiddenAndRequiredNumber()-p2.getForbiddenAndRequiredNumber());
	}

	/**
	 * Constructor of a family from a set of products, and the predicates for the partial order.
	 *
	 * @param products the set of products of the family  (must be non-null).
	 * @param areComparable  a bipredicate to check if two products can be compared (must be non-null).
	 * @param compare the function to compare two products (must be non-null).
	 */
	public Family(Set<Product> products, BiPredicate<Product,Product> areComparable, BiFunction<Product, Product, Integer> compare)
	{
		Objects.requireNonNull(products);
		Objects.requireNonNull(areComparable);
		Objects.requireNonNull(compare);

		this.products=new HashSet<>(products);
		this.areComparable=areComparable;
		this.compare=compare;
		this.po=products.parallelStream()
				.collect(Collectors.toMap(Function.identity(),
						p1->products.parallelStream()
								.filter(p2-> !p1.equals(p2) && areComparable.test(p1,p2))//are comparable
								.collect(Collectors.partitioningBy(p2->compare.apply(p2, p1)<=-1,Collectors.toSet())))); //changed from <0 to <=-1 for mutation testing

	}

	/**
	 * Getter of the set of products.
	 * @return the set of products.
	 */
	public Set<Product> getProducts() {
		return new HashSet<>(products);
	}

	/**
	 * Getter of the partial order of products.
	 * @return the partial order of products.
	 */
	public Map<Product, Map<Boolean, Set<Product>>> getPo() {
		return new HashMap<>(po);
	}

	/**
	 * Returns the maximum number of features available for a product in this product-line, i.e., the maximum depth of the partial order.
	 *
	 * @return the maximum number of features available for a product in this product-line, i.e., the maximum depth of the partial order.
	 */
	public int getMaximumDepth()
	{
		return products.parallelStream()
				.mapToInt(Product::getForbiddenAndRequiredNumber)
				.max().orElse(0)+1; //also consider products with zero features	
	}

	/**
	 * Returns the sub-products of prod.
	 * @param prod  the product whose sub-products are returned
	 * @return the sub-products of prod.
	 */
	public Set<Product> getSubProductsOfProduct(Product prod)
	{
		return this.po.get(prod).get(false);
	}


	/**
	 * Returns the super-products of prod.
	 * @param prod  the product whose super-products are returned
	 * @return the super-products of prod.
	 */
	public Set<Product> getSuperProductsOfProduct(Product prod)
	{
		return this.po.get(prod).get(true);
	}

	/**
	 * Returns the sub-products of prod not closed transitively. These are all sub-products of p
	 * such that, given two of them, it is never the case that one is a sub-product of the other.
	 *
	 * @param p  the product whose sub-products are returned
	 * @return the sub-products not closed transitively of prod.
	 */
	public Set<Product> getSubProductsNotClosedTransitively(Product p) {
		return this.getSubProductsOfProduct(p)
				.parallelStream()
				.filter(subProduct1->this.getSubProductsOfProduct(p)
						.parallelStream()
						.noneMatch(subProduct2-> areComparable.test(subProduct1,subProduct2)
								&&	compare.apply(subProduct2, subProduct1)<=-1))
				.collect(Collectors.toSet());
	}

	/**
	 * Returns all maximal products p s.t. there is no p' greater than p.
	 * @return all maximal products p s.t. there is no p' greater than p.
	 */
	public Set<Product> getMaximalProducts()
	{
		return this.po.entrySet().parallelStream()
				.filter(e->e.getValue().get(true).isEmpty())
				.map(Entry::getKey)
				.collect(Collectors.toSet());
	}

	/**
	 * Overrides the method of the object class
	 * @return the hashcode of this object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(products);
	}


	/**
	 * Overrides the method of the object class
	 * @param obj the other object to compare to
	 * @return true if the two objects are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null||getClass() != obj.getClass())
			return false;
		Family other = (Family) obj;
		return products.equals(other.products);
	}


	/**
	 * Print a representation of this object as String
	 * @return  a representation of this object as String
	 */
	@Override
	public String toString() {
		return "Family [products=" + products + "]";
	}
}