package shape;

import java.util.List;

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
	private Ray lastRay = null;

	public Intersectable() {
		super();
		lastRay = null;
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

	public abstract List<Intersectable> getAll();

	/**
	 * @param method 
	 * @return the bounding box for this intersectable (default for bounding
	 *         boxes, and shapes that cannot be contained e.g. planes)
	 */
	public Intersectable getBoundingBox(String method) {
		return this;
	}

	public boolean overlap(BoundingBox cell) {
		for(int i = 0; i<3;i++){
			if(getMaxCoordinates()[i]<cell.getMinCoordinates()[i]) {return false;}
			if(getMinCoordinates()[i]>cell.getMaxCoordinates()[i]) {return false;}
		}
		return true;
	}

	public Ray getLastRay() {
		return this.lastRay;
	}
	
	public void setLastRay(Ray ray) {
		this.lastRay = ray;
	}

}