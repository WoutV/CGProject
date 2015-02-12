package shape;

import java.awt.Color;

import shading.Diffuse;
import light.PointLight;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;

/**
 * Represents a three dimensional sphere.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class Sphere implements Shape {
	public Transformation transformation;
	public final double radius;
	private Diffuse color;

	/**
	 * Creates a new {@link Sphere} with the given radius and which is
	 * transformed by the given {@link Transformation}.
	 * 
	 * @param transformation
	 *            the transformation applied to this {@link Sphere}.
	 * @param radius
	 *            the radius of this {@link Sphere}..
	 * @throws NullPointerException
	 *             when the transformation is null.
	 * @throws IllegalArgumentException
	 *             when the radius is smaller than zero.
	 */
	public Sphere(Transformation transformation, double radius, Diffuse diffuse) {
		if (transformation == null)
			throw new NullPointerException("the given origin is null!");
		if (radius < 0)
			throw new IllegalArgumentException(
					"the given radius cannot be smaller than zero!");
		this.transformation = transformation;
		this.radius = radius;
		this.setColor(diffuse);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see shape.Shape#intersect(geometry3d.Ray3D)
	 */
	@Override
	public Double intersect(Ray ray) {
		Ray transformed = transformation.transformInverse(ray);

		Vector o = transformed.origin.toVector3D();

		double a = transformed.direction.dot(transformed.direction);
		double b = 2.0 * (transformed.direction.dot(o));
		double c = o.dot(o) - radius * radius;

		double d = b * b - 4.0 * a * c;

		if (d < 0)
			return -1.0;
		double dr = Math.sqrt(d);
		double q = b < 0 ? -0.5 * (b - dr) : -0.5 * (b + dr);

		double t0 = q / a;
		double t1 = c / q;

		if( t0 >= 0 & t1 >= 0) {
			return Math.min(t0, t1);
		} else if (t0 < 0 & t1 >= 0) {
			return t1;
		} else if (t1 < 0 & t0 >= 0) {
			return t0;
		}
		return -1.0;
			
	}

	public Diffuse getColor() {
		return color;
	}

	public void setColor(Diffuse color) {
		this.color = color;
	}

	@Override
	public Color getColor(Ray ray, PointLight light, Point p) {
		Point trans = transformation.transformInverse(p);
		Vector normal = trans.toVector3D().scale(1/trans.toVector3D().length());
		System.out.println(normal+"NORMAL");
		return this.color.getColor(ray, light, p, normal);
	}

	@Override
	public Point getIntersection(Ray ray) {
		Ray transformed = transformation.transformInverse(ray);

		Vector o = transformed.origin.toVector3D();

		double a = transformed.direction.dot(transformed.direction);
		double b = 2.0 * (transformed.direction.dot(o));
		double c = o.dot(o) - radius * radius;

		double d = b * b - 4.0 * a * c;

		if (d < 0)
			return null;
		double dr = Math.sqrt(d);
		double t1 = (-b+dr)/(2*a);
		double t2 = (-b-dr)/(2*a);

		double t0 = Math.min(t1, t2);
		
		return ray.origin.add(ray.direction.scale(t0));
	}
}
