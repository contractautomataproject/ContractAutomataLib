package io.github.contractautomataproject.catlib.family;

import java.util.Objects;

import io.github.contractautomataproject.catlib.automaton.label.CALabel;
import io.github.contractautomataproject.catlib.automaton.label.action.OfferAction;
import io.github.contractautomataproject.catlib.automaton.label.action.RequestAction;

/**
 * Class implementing a feature
 * @author Davide Basile
 *
 */
public class Feature {
	private final String name;
	
	public Feature(String name) {
		if (name==null)
			throw new IllegalArgumentException();
		
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name.hashCode());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feature other = (Feature) obj;
		return (name.equals(other.name));
	}
}