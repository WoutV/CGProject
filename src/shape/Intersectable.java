package shape;

import java.util.Collection;

import math.Ray;
import math.Transformation;

/**
 * Represents objects that can be tested on intersections with rays.
 * 
 * @author wout
 *
 */
public abstract class Intersectable {

	protected Transformation transformation;
	protected final double EPSILON = 0.00001;

	public Intersectable() {
		super();
	}

	/**
	 * Returns the intersection object where the ray hits the intersectable.
	 * Returns an intersection with t = -1 if there is no intersection
	 * 
	 * @param ray
	 * @return
	 */
	public abstract Intersection intersect(Ray ray);

	public abstract double[] getMinCoordinates();

	public abstract double[] getMaxCoordinates();

	public abstract Collection<Intersectable> getAll();

	/**
	 * @return the bounding box for this intersectable (default for bounding
	 *         boxes, and shapes that cannot be contained e.g. planes)
	 */
	public Intersectable getBoundingBox() {
		return this;
	}

}