package io.github.contractautomata.catlib.family;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Class implementing the partial product generation operator. <br>
 * This operator takes in input a set of total products (with all features assigned),<br>
 * and returns a set of products comprehending total products and partial products (not all features assigned).<br>
 * This operator is similar to the <a href="https://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm">Quine-McCluskey algorithm</a>.<br>
 *
 * @author Davide Basile
 *
 */
public class PartialProductGenerator implements UnaryOperator<Set<Product>>{

	/**
	 * This operator takes in input a set of total products (with all features assigned),
	 * and returns a set of products comprehending total products and partial products (not all features assigned).<br>
	 * This operator is similar to the <a href="https://en.wikipedia.org/wiki/Quine%E2%80%93McCluskey_algorithm">Quine-McCluskey algorithm</a>.<br>
	 *
	 * Given two products p1 p2 identical but for a feature f activated in one 
	 * and deactivated in the other, a super product (a.k.a. sub-family) is generated such that f is left unresolved. <br>
	 * This method generates all possible super products. <br>
	 * All  generated super products are such that the corresponding feature model formula is satisfied. <br>
	 *
	 * @param setprod  the set of total products to start the generation of partial products.
	 * @return the set of all total and partial products.
	 */
	@Override
	public Set<Product> apply(Set<Product> setprod){
		/**
		 * 	 * All generated super products are such that the corresponding feature model formula is satisfied. <br>
		 * 	 * This condition holds for this method, and a brief argument follows.<br>
		 * 	 * Indeed, assume the feature model formula is in CNF, it is never the case that f is the only literal of a
		 * 	 * disjunct (i.e. a truth value must be assigned to f); otherwise either p1 or p2
		 * 	 * is not a valid product (p1 if f is negated in the disjunct, p2 otherwise).<br>
		 */
		Set<Feature> features =
				setprod.parallelStream()
		.flatMap(p->Stream.of(p.getRequired().stream(),p.getForbidden().stream()))
		.flatMap(Function.identity())
		.collect(Collectors.toSet());
		
		return Stream.iterate(setprod, s->!s.isEmpty(), sp->{
			Map<Product,Set<Product>> map = features.stream()
					.map(f->sp.parallelStream()
							.collect(Collectors.groupingByConcurrent(p->p.removeFeatures(Collections.singleton(f)),Collectors.toSet())))
					.reduce(new ConcurrentHashMap<>(),(x,y)->{x.putAll(y); return x;});	
			return map.entrySet().parallelStream()
					.filter(e->e.getValue().size()>1)
					.map(Entry::getKey)
					.collect(Collectors.toSet());})
		.reduce(new HashSet<>(),(x,y)->{x.addAll(y); return x;});
	}

}
