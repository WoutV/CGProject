package shape;

import java.awt.Color;
import java.util.List;

import light.PointLight;
import math.Point;
import math.Ray;

/**
 * Interface which should be implemented by all {@link Shape}s.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public interface Shape {
	/**
	 * Returns whether the given {@link Ray} intersects this {@link Shape}.
	 * False when the given ray is null.
	 * 
	 * @param ray
	 *            the ray to intersect with.
	 * @return true when the given {@link Ray} intersects this {@link Shape}.
	 */
	public Double intersect(Ray ray);

	public Color getColor(Ray ray, List<PointLight> lights, Point p);

	public Point getIntersection(Ray ray);
}
