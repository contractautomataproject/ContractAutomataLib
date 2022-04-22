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
import io.github.contractautomata.catlib.operations.ProductOrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.operations.UnionFunction;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;
import io.github.contractautomata.catlib.operations.OrchestrationSynthesisOperator;
import io.github.contractautomata.catlib.requirements.Agreement;

/**
 * Class implementing a Featured Modal Contract Automaton (FMCA). <br>
 * An FMCA pairs a modal contract automaton with a family, and provides operations on this pair. <br>
 *
 * FMCA and their operations have been introduced in: <br>
 *
 *  * <ul>
 *  *  *     <li>Basile, D. et al., 2020.
 *  *  *     Controller synthesis of service contracts with variability. Science of Computer Programming, vol. 187, pp. 102344.
 *  *  *      (<a href="https://doi.org/10.1016/j.scico.2019.102344">https://doi.org/10.1016/j.scico.2019.102344</a>)</li>
 *  *  </ul>
 *
 * @author Davide Basile
 *
 */
public class FMCA {

	/**
	 * the modal contract automaton.
	 */
	private final Automaton<String, Action, State<String>, ModalTransition<String,Action,State<String>, CALabel>> aut;

	/**
	 * the family.
	 */
	private final Family family;

	/**
	 * Constructor for an FMCA from an automaton and a family.
	 * @param aut  the automaton (must be non-null).
	 * @param family the family (must be non-null).
	 */
	public FMCA(Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut, Family family)
	{
		Objects.requireNonNull(aut);
		Objects.requireNonNull(family);
		this.aut=aut;
		this.family=family;
	}

	/**
	 * This constructor instantiates the family of products by performing a pre-processing, to polish
	 * the set of products prod given as argument. <br>
	 * Firstly, all features that are not labels of the given automaton are removed from the products. <br>
	 * After that, redundant products are removed (those requiring features present in aut but not in its orchestration in agreement). <br>
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
		//(e.g. equivalent features, features that are never forbidden).
		//Once the orchestration is computed, products requiring features not present in the orchestration are removed.
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

	/**
	 * Getter of the automaton.
	 * @return the automaton.
	 */
	public Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> getAut() {
		return aut;
	}

	/**
	 * Getter of the family.
	 * @return the family.
	 */
	public Family getFamily() {
		return family;
	}

	/**
	 * Returns the canonical products of this FMCA. <br>
	 * A canonical product represents all the maximal elements in the FMCA that have the same set of forbidden actions. <br>
	 * It is required that the automaton does not contain transitions labelled with "dummy" (these labels are generated when
	 * computing the union of a set of automata). <br>
	 *
	 * @return  the canonical products of this FMCA.
	 */
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
	 * Returns the orchestration of the family as the union of orchestrations of total products.
	 *
	 * @return the orchestration of the family as the union of orchestrations of total products
	 */
	public Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> getOrchestrationOfFamilyEnumerative()
	{
		return new UnionFunction().apply(new ArrayList<>(this.getTotalProductsWithNonemptyOrchestration().values()));
	}

	/**
	 * Returns the orchestration of the family as the union of orchestrations of canonical products. <br>
	 *
	 * @return the orchestration of the family as the union of orchestrations of canonical products.
	 */
	public Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> getOrchestrationOfFamily()
	{
		return new UnionFunction().apply(new ArrayList<>(this.getCanonicalProducts()
				.values()));
	}

	/**
	 * Returns a map pairing a product with its non-empty orchestration in agreement.
	 * @return a map pairing a product with its non-empty orchestration in agreement.
	 */
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
	 * Returns the set of products respecting validity. <br>
	 * A product p is respecting validity iff all the mandatory actions in p correspond to executable transitions in the automaton and no action forbidden
	 * in p have executable counterparts in the automaton.  <br>
	 * This method exploits the partial order so it starts from maximal products.
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

	/**
	 * The set of products with non-empty orchestration in agreement.
	 *
	 * @return the set of products with non-empty orchestration in agreement.
	 */
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