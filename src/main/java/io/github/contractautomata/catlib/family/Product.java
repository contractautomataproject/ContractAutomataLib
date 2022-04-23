package io.github.contractautomata.catlib.family;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.contractautomata.catlib.automaton.Automaton;
import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.transition.ModalTransition;

/**
 * A configuration/product of a product line/family, identified as set of required and forbidden features.
 * 
 * @author Davide Basile
 *
 */
public class Product {
	/**
	 * the set of required features
	 */
	private final Set<Feature> required;

	/**
	 * the set of forbidden features
	 */
	private final Set<Feature> forbidden;

	/**
	 * Constructor for a product from sets of features
	 * @param required the set of required features, must be non-null
	 * @param forbidden  the set of forbidden features, must be non-null
	 */
	public Product(Set<Feature> required, Set<Feature> forbidden)
	{
		Objects.requireNonNull(required);
		Objects.requireNonNull(forbidden);

		if (required.parallelStream()
				.anyMatch(forbidden::contains))
			throw new IllegalArgumentException("A feature is both required and forbidden");

		this.required= new HashSet<>(required);
		this.forbidden= new HashSet<>(forbidden);
	}

	/**
	 * Constructor for a product  from arrays of Strings
	 * @param r  array of  required features expressed as Strings
	 * @param f array of  forbidden features expressed as Strings
	 */
	public Product(String[] r, String[] f)
	{
		this(Arrays.stream(r).map(Feature::new).collect(Collectors.toSet()),
				Arrays.stream(f).map(Feature::new).collect(Collectors.toSet()));
	}

	/**
	 * Getter of the set of required features.
	 * @return the set of required features.
	 */
	public Set<Feature> getRequired()
	{
		return new HashSet<>(required);
	}

	/**
	 * Getter of the set of forbidden features.
	 * @return the set of forbidden features.
	 */
	public Set<Feature> getForbidden()
	{
		return new HashSet<>(forbidden);
	}

	/**
	 * Returns the number of forbidden and required features of this product.
	 * @return the number of forbidden and required features of this product.
	 */
	public int getForbiddenAndRequiredNumber()
	{
		return required.size()+forbidden.size();
	}

	/**
	 * Returns a new product where the features in sf have been removed (from both required and forbidden features).
	 * @param sf the set of features to remove.
	 * @return a new product where the features in sf have been removed (from both required and forbidden features).
	 */
	public Product removeFeatures(Set<Feature> sf)
	{
		return new Product(this.required.stream()
				.filter(f->!sf.contains(f))
				.collect(Collectors.toSet()),
				this.forbidden.stream()
				.filter(f->!sf.contains(f))
				.collect(Collectors.toSet()));
	}
	
	/**
	 * Returns true if all required actions are available in the transitions tr, i.e, all features name of this product
	 * are equal to the content of some action of some transition in tr.
	 *
	 * @param <S1> the type of the content of the state.
	 * @param tr the set of transitions to check.
	 * @return true if all required actions are available in the transitions tr.
	 */
	public <S1> boolean checkRequired(Set<? extends ModalTransition<S1, Action, State<S1>, CALabel>> tr)
	{
		Set<String> act=tr.parallelStream()
				.map(t->t.getLabel().getAction().getLabel())
				.collect(Collectors.toSet());
		return required.stream()
				.map(Feature::getName)
				.allMatch(act::contains);
	}

	/**
	 * Returns true if all forbidden actions of this product are not available in the transitions tr, i.e,
	 * all features name  are not equal to any of the content of the actions of the transitions in tr.
	 *
	 * @param tr the set of transitions to check.
	 * @return true if all forbidden actions are not available in the transitions tr.
	 */
	public boolean checkForbidden(Set<? extends ModalTransition<String,Action,State<String>,CALabel>> tr)
	{
		Set<String> act=tr.parallelStream()
				.map(t->t.getLabel().getAction().getLabel())
				.collect(Collectors.toSet());
		return forbidden.stream()
				.map(Feature::getName)
				.noneMatch(act::contains);
	}

	/**
	 * Returns true if the action of l is equal to some name of a forbidden feature.
	 *
	 * @param l the label to check.
	 * @return true if the action of l is equal to some name of a forbidden feature.
	 */
	public boolean isForbidden(CALabel l)
	{
		return forbidden.stream()
				.map(Feature::getName)
				.anyMatch(s->s.equals(l.getAction().getLabel()));
	}

	/**
	 * Returns true if the set of transitions of aut satisfies this.checkForbidden and this.checkRequired
	 * @param aut  the automaton to check
	 * @return true if the set of transitions of aut satisfies this.checkForbidden and this.checkRequired
	 */
	public boolean isValid(Automaton<String,Action,State<String>,ModalTransition<String,Action,State<String>,CALabel>> aut)
	{
		return this.checkForbidden(aut.getTransition()) && this.checkRequired(aut.getTransition());
	}


	/**
	 * Print a representation of this object as String
	 * @return  a representation of this object as String
	 */
	@Override
	public String toString()
	{

		String ln = System.lineSeparator();
		return "R:"+required.toString()+";"+ln+"F:"+forbidden.toString()+";"+ln;
	}

	/**
	 * Overrides the method of the object class
	 * @return the hashcode of this object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(required.hashCode(),forbidden.hashCode());
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
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		return forbidden.equals(other.forbidden)&&required.equals(other.required);
	}
	
}