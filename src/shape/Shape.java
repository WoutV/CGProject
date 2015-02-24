package shape;

import shading.Material;
import math.Ray;
import math.Transformation;

/**
 * Interface which should be implemented by all {@link Shape}s.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public abstract class Shape {
	protected Material shading;
	protected Transformation transformation;

	/**
	 * Returns whether the given {@link Ray} intersects this {@link Shape}.
	 * False when the given ray is null.
	 * 
	 * @param ray
	 *            the ray to intersect with.
	 * @return true when the given {@link Ray} intersects this {@link Shape}.
	 */
	public abstract Intersection intersect(Ray ray);
}
