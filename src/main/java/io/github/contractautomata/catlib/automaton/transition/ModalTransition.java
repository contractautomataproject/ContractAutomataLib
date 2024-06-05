package io.github.contractautomata.catlib.automaton.transition;

import io.github.contractautomata.catlib.automaton.label.CALabel;
import io.github.contractautomata.catlib.automaton.label.Label;
import io.github.contractautomata.catlib.automaton.label.action.Action;
import io.github.contractautomata.catlib.automaton.label.action.TauAction;
import io.github.contractautomata.catlib.automaton.state.State;
import io.github.contractautomata.catlib.operations.interfaces.TriPredicate;

import java.util.Objects;
import java.util.Set;

/**
 * Class implementing a Modal Transition of an Automaton. <br>
 * A modal transition is a transition further equipped with a modality. <br>
 * Modalities are either permitted and necessary. <br>
 * A permitted transition is controllable. <br>
 * Necessary transitions can be either urgent (i.e., uncontrollable) or lazy. <br>
 * A lazy transition can be either controllable or uncontrollable according <br>
 * to a controllability predicate that predicates over the set of transitions of an automaton. <br>
 *
 * @author Davide Basile
 *
 * @param <S1> generic type of the content of S
 * @param <L1> generic type of the content of L
 * @param <S> generic type of the state
 * @param <L> generic type of the label
 */
public class ModalTransition<S1,L1, S extends State<S1>,L extends Label<L1>> extends Transition<S1,L1,S,L>  {

	/**
	 * The enum of possible modalities of a transition
	 */
	public enum Modality{
		/**
		 * the permitted modality
		 */
		PERMITTED,
		/**
		 * the urgent modality
		 */
		URGENT,
		/**
		 * the lazy modality
		 */
		LAZY
	}

	/**
	 * Constant symbol denoting a urgent modality
	 */
	public static final String URGENT = "U";


	/**
	 * Constant symbol denoting a lazy modality
	 */
	public static final String LAZY = "L";


	/**
	 * Constant symbol denoting a necessary modality
	 */
	public static final String NECESSARY = "!";

	/**
	 * the modality of this transition
	 */
	private final Modality mod;

	/**
	 * Constructing a modal transition from the source, target states, the label
	 * and the modality. The modality must be non-null.
	 * Requirements of the constructor of the super-class must hold.
	 *
	 * @param source the source state
	 * @param label the label
	 * @param target the target state
	 * @param type the modality
	 */
	public ModalTransition(S source, L label, S target, Modality type)
	{
		super(source,label,target);
		if (type==null || label.getContent()
				.stream()
				.anyMatch(TauAction.class::isInstance)
				&& type!=Modality.URGENT)
			throw new IllegalArgumentException();
		this.mod=type;
	}

	/**
	 * Returns true if the transition is urgent
	 * @return true if the transition is urgent
	 */
	public boolean isUrgent()
	{
		return (this.mod==Modality.URGENT);
	}

	/**
	 * Returns true if the transition is lazy
	 * @return true if the transition is lazy
	 */
	public boolean isLazy()
	{
		return (this.mod==Modality.LAZY);
	}

	/**
	 * Returns  true if the transition is necessary
	 * @return true if the transition is necessary
	 */
	public boolean isNecessary()
	{
		return (this.mod!=Modality.PERMITTED);
	}

	/**
	 * Returns true if the transition is permitted
	 * @return true if the transition is permitted
	 */
	public boolean isPermitted()
	{
		return (this.mod==Modality.PERMITTED);
	}

	/**
	 * Getter of modality
	 * @return the modality
	 */
	public Modality getModality()
	{
		return this.mod;
	}


	/**
	 * Overrides the method of the object class
	 * @return the hashcode of this object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(super.hashCode(),mod.hashCode());
	}


	/**
	 * Overrides the method of the object class
	 * @param obj the other object to compare to
	 * @return true if the two objects are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj))
			return false;
		ModalTransition<?,?,?,?> other = (ModalTransition<?,?,?,?>) obj;
		return mod==other.mod;
	}

	/**
	 * Print a String representing this object
	 * @return a String representing this object
	 */
	@Override
	public String toString()
	{
		if (this.mod==Modality.URGENT)
			return NECESSARY+URGENT+ super.toString();
		else if (this.mod==Modality.LAZY)
			return NECESSARY+LAZY+super.toString();
		else
			return super.toString();
	}

	/**
	 * Returns true if the transition is uncontrollable.
	 * An urgent transition is uncontrollable, a permitted transition is not uncontrollable.
	 * A lazy transition is uncontrollable if and only if  none of the pairs formed
	 * by this transition and a transition t belonging to tr satisfies the controllability predicate,
	 * where t must be a match and the source state of t must not be contained in the set badStates.
	 *
	 * @param tr the set of transitions to check
	 * @param badStates the set of badstates to check
	 * @param controllabilityPred the controllability predicate
	 * @return true if the transition is uncontrollable
	 */
	public boolean isUncontrollable(Set<ModalTransition<S1, Action,S, CALabel>> tr, Set<State<S1>> badStates,
									TriPredicate<ModalTransition<S1,L1,S,L>, Set<ModalTransition<S1,Action,S,CALabel>>, Set<State<S1>>> controllabilityPred)
	{
		if (this.isUrgent())
			return true;
		if (this.isPermitted())
			return false;
		return controllabilityPred.test(this,tr,badStates);
	}
}

