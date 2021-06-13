package family;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import contractAutomata.CALabel;
import contractAutomata.MSCA;
import contractAutomata.MSCATransition;
import contractAutomata.operators.ProductOrchestrationSynthesisOperator;
import contractAutomata.operators.UnionFunction;
import contractAutomata.requirements.Agreement;

/**
 * Featured Modal Contract Automata
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

	public MSCA getAut() {
		return aut;
	}
		
	public Map<Product,MSCA> getCanonicalProducts()
	{
		if (aut.getForwardStar(aut.getInitial()).stream()
				.map(MSCATransition::getLabel)
				.anyMatch(l->CALabel.getUnsignedAction(l.getAction()).equals("dummy")))
			throw new UnsupportedOperationException();

		Set<String> act=aut.getTransition().parallelStream()
		.map(x->CALabel.getUnsignedAction(x.getLabel().getAction()))
		.collect(Collectors.toSet());
		
		Map<Set<Feature>, Map<Product,MSCA>>  quotientClasses = 
				this.family.getMaximalProducts().parallelStream()
				.map(p->new AbstractMap.SimpleEntry<Product,MSCA>(p,
						new ProductOrchestrationSynthesisOperator(new Agreement(),p).apply(aut)))
				.filter(e->e.getValue()!=null)
				.collect(Collectors.groupingBy(e->
					e.getKey().getForbidden().stream()
					.filter(f->act.contains(f.getName()))
					.collect(Collectors.toSet()),//ignoring forbidden features not present in aut (this is an improvement of Def.32 of JSCP2020).
						Collectors.toMap(Entry::getKey, Entry::getValue)));

		return quotientClasses.entrySet().parallelStream()
		.map(e->e.getValue().entrySet())
		.filter(s->!s.isEmpty())
		.map(s->s.iterator().next())//the first element for each equivalence class 
		.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}
	
	public MSCA getOrchestrationOfFamilyEnumerative()
	{
		 return new UnionFunction().apply(this.getTotalProductsWithNonemptyOrchestration().entrySet()
					.stream()
					.map(Entry::getValue)
					.collect(Collectors.toList()));	
	}
	
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
	 * respectingValidity see Theorem 3 of JSCP2020
	 * this method exploits the partial order so it starts from maximal products
	 * 
	 * @param aut
	 * @return
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
				.anyMatch(l->CALabel.getUnsignedAction(l.getAction()).equals("dummy")))
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
