package io.github.contractautomata.catlib.family;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.operators.ProductOrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.operators.UnionFunction;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.operators.OrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.requirements.Agreement;

/**
 * Class implementing a Featured Modal Contract Automata
 *
 * @author Davide Basile
 *
 */
public class FMCA {

	private final Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> aut;
	private final Family family;

	public FMCA(Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut, Family family)
	{
		Objects.requireNonNull(aut);
		Objects.requireNonNull(family);
		this.aut=aut;
		this.family=family;
	}

	/**
	 * this constructor instantiates the family of products starting by refining products to
	 * remove redundant products (that are already known to have empty orchestrations).
	 * Only features of the products that are labels of the automaton aut are retained 
	 * Those products requiring features not present in the orchestration of aut in agreement 
	 * are discarded.
	 *
	 * @param aut the automaton
	 * @param products the set of products
	 */
	public FMCA(Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut,
				Set<Product> products)
	{
		Objects.requireNonNull(aut);
		Objects.requireNonNull(products);

		Function<Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>, Set<String>>
				getActions = a -> a.getTransition().parallelStream()
				.map(t->t.getLabel().getAction().getLabel())
				.collect(Collectors.toSet());

		Set<String> autActions = getActions.apply(aut);

		Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> orc =
				new OrchestrationSynthesisOperator<String>(new Agreement()).apply(aut);

		Set<String> orcActions =getActions.apply(orc);

		//products are polished from features not present in the automaton
		//(e.g. equivalent features, abstract features)
		//products requiring features not present in the orchestration are removed
		this.aut=aut;
		this.family= new Family(products.parallelStream()
				.filter(p->orcActions.containsAll(p.getRequired().stream()
						.map(Feature::getName)
						.filter(autActions::contains)
						.collect(Collectors.toSet())))
				.map(p->new Product(p.getRequired().stream() //retain features
						.filter(f->autActions.contains(f.getName()))
						.collect(Collectors.toSet()),
						p.getForbidden().stream()
								.filter(f->autActions.contains(f.getName()))
								.collect(Collectors.toSet())))
				.collect(Collectors.toSet()),
				(p1,p2) -> p2.getForbidden().containsAll(p1.getForbidden())||
						p1.getForbidden().containsAll(p2.getForbidden()),
				(p1,p2) -> p1.getForbidden().size()-p2.getForbidden().size());
	}

	public Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> getAut() {
		return aut;
	}

	public Family getFamily() {
		return family;
	}

	public Map<Product,Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> getCanonicalProducts()
	{
		if (aut.getForwardStar(aut.getInitial()).stream()
				.map(ModalTransition<String,Action,State<String>,CALabel>::getLabel)
				.anyMatch(l->l.getAction().getLabel().equals("dummy")))
			throw new UnsupportedOperationException();

		Set<String> act=aut.getTransition().parallelStream()
				.map(t->t.getLabel().getAction().getLabel())
				.collect(Collectors.toSet());

		Map<Set<Feature>, Map<Product,Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>>>  quotientClasses =
				this.family.getMaximalProducts().stream()
						.map(p-> new AbstractMap.SimpleEntry<>(p,
								new ProductOrchestrationSynthesisOperator<String>(new Agreement(), p).apply(aut)))
						.filter(e->e.getValue()!=null)
						.collect(Collectors.groupingBy(e->
										e.getKey().getForbidden().stream()
												.filter(f->act.contains(f.getName()))//ignoring forbidden features not present in aut (this is an improvement of Def.32 of JSCP2020).
												.collect(Collectors.toSet()),
								Collectors.toMap(Entry::getKey, Entry::getValue)));

		return quotientClasses.entrySet().parallelStream()
				.map(e->e.getValue().entrySet())
				.map(s->s.iterator().next())//the first element is the canonical of each equivalence class
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	/**
	 *
	 * @return computes the orchestration of the family as the union of orchestrations of total products
	 */
	public Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> getOrchestrationOfFamilyEnumerative()
	{
		return new UnionFunction().apply(new ArrayList<>(this.getTotalProductsWithNonemptyOrchestration().values()));
	}

	/**
	 *
	 * @return computes the orchestration of the family by only considering canonical products
	 */
	public Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> getOrchestrationOfFamily()
	{
		return new UnionFunction().apply(new ArrayList<>(this.getCanonicalProducts()
				.values()));
	}

	public Map<Product,Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>>> getTotalProductsWithNonemptyOrchestration()
	{
		return this.family.getPo().entrySet()
				.parallelStream()
				.filter(e->e.getValue().get(false).isEmpty())
				.map(Entry::getKey)
				.map(p-> new AbstractMap.SimpleEntry<>(p,
						new ProductOrchestrationSynthesisOperator<String>(new Agreement(), p).apply(aut)))
				.filter(e->e.getValue()!=null)
				.collect(Collectors.toMap(Entry::getKey, Entry::getValue));
	}

	/**
	 * respectingValidity see Theorem 3 of JSCP2020(Basile et al.),
	 * this method exploits the partial order so it starts from maximal products
	 *
	 * @return the set of products respecting validity
	 */
	public Set<Product> productsRespectingValidity()
	{
		return productsRespectingValidity(aut);
	}

	private Set<Product> productsRespectingValidity(Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> a)
	{
		return selectProductsSatisfyingPredicateUsingPO(a, p->p.isValid(a));
	}

	public Set<Product> productsWithNonEmptyOrchestration()
	{
		return this.productsWithNonEmptyOrchestration(aut);
	}

	private Set<Product> productsWithNonEmptyOrchestration(Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut)
	{
		//partial order exploited, one could also start the synthesis from the intersection of the controllers
		//of the sub-products
		return this.selectProductsSatisfyingPredicateUsingPO(aut, p->
				new ProductOrchestrationSynthesisOperator<String>(new Agreement(), p).apply(aut) != null);
	}

	private Set<Product> selectProductsSatisfyingPredicateUsingPO(Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> a,Predicate<Product> pred)
	{
		if (a.getForwardStar(a.getInitial()).stream()
				.map(ModalTransition<String,Action,State<String>,CALabel>::getLabel)
				.anyMatch(l->l.getAction().getLabel().equals("dummy")))
			throw new UnsupportedOperationException();

		return Stream.iterate(this.family.getMaximalProducts().parallelStream()
								.filter(pred)
								.collect(Collectors.toSet()),
						s->!s.isEmpty(),
						s->s.parallelStream()
								.map(this.family::getSubProductsNotClosedTransitively)
								.flatMap(Set::stream)
								.filter(pred)
								.collect(Collectors.toSet()))
				.flatMap(Set::stream)
				.collect(Collectors.toSet());
	}

}