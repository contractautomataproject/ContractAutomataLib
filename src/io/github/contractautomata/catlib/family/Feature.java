package io.github.contractautomata.catlib.family;

import java.util.Objects;

/**
 * Class implementing a feature of product.
 *
 * @author Davide Basile
 *
 */
public class Feature {
	/**
	 * the name of the feature
	 */
	private final String name;

	/**
	 * Constructor for a feature
	 * @param name the name of the feature, must be non-null
	 */
	public Feature(String name) {
		if (name==null)
			throw new IllegalArgumentException();
		
		this.name = name;
	}

	/**
	 * Getter of the name of the feature
	 * @return the name of the feature
	 */
	public String getName() {
		return name;
	}

	/**
	 * Print a representation of this object as String
	 * @return  a representation of this object as String
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Overrides the method of the object class
	 * @return the hashcode of this object
	 */
	@Override
	public int hashCode() {
		return Objects.hash(name.hashCode());
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
		Feature other = (Feature) obj;
		return (name.equals(other.name));
	}
}