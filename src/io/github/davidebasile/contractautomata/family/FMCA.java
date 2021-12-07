package io.github.davidebasile.contractautomata.family;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.davidebasile.contractautomata.automaton.MSCA;
import io.github.davidebasile.contractautomata.automaton.transition.MSCATransition;
import io.github.davidebasile.contractautomata.operators.OrchestrationSynthesisOperator;
import io.github.davidebasile.contractautomata.operators.ProductOrchestrationSynthesisOperator;
import io.github.davidebasile.contractautomata.operators.UnionFunction;
import io.github.davidebasile.contractautomata.requirements.Agreement;

/**
 * Class implementing a Featured Modal Contract Automata
 * 
 * @author Davide Basile
 *
 */
public class FMCA {
	
	private final MSCA aut;
	private final Family family;

	public FMCA(MSCA aut, Family family)
	{
		if (aut==null||family==null)
			throw new IllegalArgumentException();
		this.aut=aut;
		this.family=family;
	}

	/**
	 * this constructor instatiates the family of products starting by refining products to 
	 * remove redundant products (that are already known to have empty orchestrations).
	 * Only features of the products that are labels of the automaton aut are retained 
	 * Those products requiring features not present in the orchestration of aut in agreement 
	 * are discarded.
	 * 
	 * @param aut the automaton
	 * @param products the set of products
	 */
	public FMCA(MSCA aut, Set<Product> products)
	{
		if (products==null||aut==null)
			throw new IllegalArgumentException();
		
		Set<Feature> actions = aut.getUnsignedActions().stream()
				.map(Feature::new)
				.collect(Collectors.toSet());
		
		MSCA orc = new OrchestrationSynthesisOperator(new Agreement()).apply(aut);
		Set<Feature> availableFeatures = orc.getUnsignedActions().stream()
				.map(Feature::new)
				.collect(Collectors.toSet());

		//products are polished from features not present in the automaton
		//(e.g. equivalent features, abstract features)
		//products requiring features not present in the orchestration are removed		
		this.aut=aut;
		this.family= new Family(products.parallelStream()
				.map(p->p.retainFeatures(actions))
				.filter(p->availableFeatures.containsAll(p.getRequired()))
				.collect(Collectors.toSet()),
				(p1,p2) -> p2.getForbidden().containsAll(p1.getForbidden())||
				p1.getForbidden().containsAll(p2.getForbidden()), 
				(p1,p2) -> p1.getForbidden().size()-p2.getForbidden().size());
	}
	
	public MSCA getAut() {
		return aut;
	}
	

	public Family getFamily() {
		return family;
	}
		
	public Map<Product,MSCA> getCanonicalProducts()
	{
		if (aut.getForwardStar(aut.getInitial()).stream()
				.map(MSCATransition::getLabel)
				.anyMatch(l->l.getUnsignedAction().equals("dummy")))
			throw new UnsupportedOperationException();

		Set<String> act=aut.getUnsignedActions(); 
//				.getTransition().parallelStream()
//		.map(x-> x.getLabel().getUnsignedAction())//CALabel.getUnsignedAction(x.getLabel().getAction()))
//		.collect(Collectors.toSet());
		
		Map<Set<Feature>, Map<Product,MSCA>>  quotientClasses = 
				this.family.getMaximalProducts().parallelStream()
				.map(p->new AbstractMap.SimpleEntry<Product,MSCA>(p,
						new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut)))
				.filter(e->e.getValue()!=null)
				.collect(Collectors.groupingBy(e->
					e.getKey().getForbidden().stream()
					.filter(f->act.contains(f.getName()))//ignoring forbidden features not present in aut (this is an improvement of Def.32 of JSCP2020).
					.collect(Collectors.toSet()),
						Collectors.toMap(Entry::getKey, Entry::getValue)));

		return quotientClasses.entrySet().parallelStream()
		.map(e->e.getValue().entrySet())
		.filter(s->!s.isEmpty())
		.map(s->s.iterator().next())//the first element is the canonical of each equivalence class 
		.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}
	
	/**
	 * 
	 * @return computes the orchestration of the family as the union of orchestrations of total products
	 */
	public MSCA getOrchestrationOfFamilyEnumerative()
	{
		 return new UnionFunction().apply(this.getTotalProductsWithNonemptyOrchestration().entrySet()
					.stream()
					.map(Entry::getValue)
					.collect(Collectors.toList()));	
	}
	
	/**
	 * 
	 * @return computes the orchestration of the family by only considering canonical products
	 */
	public MSCA getOrchestrationOfFamily()
	{
		return new UnionFunction().apply(this.getCanonicalProducts()
		.values()
		.stream()
		.collect(Collectors.toList()));
	}

	public Map<Product,MSCA> getTotalProductsWithNonemptyOrchestration()
	{
		return this.family.getPo().entrySet()
				.parallelStream()
				.filter(e->e.getValue().get(false).isEmpty())
				.map(Entry::getKey)
				.map(p->new AbstractMap.SimpleEntry<Product, MSCA>(p,
						new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut)))
				.filter(e->e.getValue()!=null)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}
	
	/**
	 * respectingValidity see Theorem 3 of JSCP2020 (author Basile),
	 * this method exploits the partial order so it starts from maximal products
	 * 
	 * @return the set of products respecting validity
	 */
	public Set<Product> productsRespectingValidity()
	{
		return productsRespectingValidity(aut);
	}
	
	private Set<Product> productsRespectingValidity(MSCA a)
	{
		return selectProductsSatisfyingPredicateUsingPO(a, p->p.isValid(a));
	}
	
	public Set<Product> productsWithNonEmptyOrchestration()
	{
		return this.productsWithNonEmptyOrchestration(aut);
	}
	
	private Set<Product> productsWithNonEmptyOrchestration(MSCA aut)
	{
		//partial order exploited, one could also start the synthesis from the intersection of the controllers
		//of the sub-products
		return this.selectProductsSatisfyingPredicateUsingPO(aut, p->new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut)!=null);
	}
	
	private Set<Product> selectProductsSatisfyingPredicateUsingPO(MSCA a,Predicate<Product> pred)
	{
		if (a.getForwardStar(a.getInitial()).stream()
				.map(MSCATransition::getLabel)
				.anyMatch(l->l.getUnsignedAction().equals("dummy")))
			throw new UnsupportedOperationException();

		return Stream.iterate(this.family.getMaximalProducts().parallelStream()
				.filter(pred)
				.collect(Collectors.toSet()),
				s->!s.isEmpty(),
				s->this.family.getPo().entrySet().parallelStream()
				.filter(e->s.contains(e.getKey()))
				.flatMap(e->e.getValue().get(false).stream())
				.filter(pred)
				.collect(Collectors.toSet()))
				.flatMap(Set::stream)
				.collect(Collectors.toSet());	
	}

}

//public Set<Product> productsWithNonEmptyOrchestrationFamily()
//{
//	return applyOnFamilyOrchestration(aut,this::productsWithNonEmptyOrchestration);
//}

//public Set<Product> respectingValidityFamily()
//{
//	return applyOnFamilyOrchestration(aut, this::productsRespectingValidity);
//}

//private Set<Product> applyOnFamilyOrchestration(MSCA aut, Function<MSCA,Set<Product>> fun)
//{
//	if (!aut.getForwardStar(aut.getInitial()).stream()
//			.map(MSCATransition::getLabel)
//			.allMatch(l->CALabel.getUnsignedAction(l.getAction()).equals("dummy")))
//		throw new UnsupportedOperationException();
//
//	return aut.getForwardStar(aut.getInitial()).stream()
//			.map(MSCATransition::getTarget)
//			.map(s1->{
//				MSCA a=aut.clone();
//				a.getInitial().setInitial(false);
//				CAState s = a.getStates().parallelStream()
//						.filter(s2->s2.getState().toString().equals(s1.getState().toString()))//ignoring initial flag
//						.findAny().orElseThrow(IllegalArgumentException::new);
//				s.setInitial(true);
//				return fun.apply(a);
//			})
//			.flatMap(Set::stream)
//			.collect(Collectors.toSet());
//}
