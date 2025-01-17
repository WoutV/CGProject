package shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import shading.Material;
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
public class Sphere extends Shape {
	public final double radius;
	private double[] minCoordinates;
	private double[] maxCoordinates;
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
	public Sphere(Transformation transformation, double radius, Material shading) {
		if (transformation == null)
			throw new NullPointerException("the given origin is null!");
		if (radius < 0)
			throw new IllegalArgumentException(
					"the given radius cannot be smaller than zero!");
		this.transformation = transformation;
		this.radius = radius;
		this.shading = shading;
		calculateMinCoordinates();
		setProjectedArea();
	}

	private void calculateMinCoordinates() {
		double[] min = {Double.MAX_VALUE,Double.MAX_VALUE, Double.MAX_VALUE};
		double[] max = {Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY};
		List<Point> testset = new ArrayList<Point>();
		testset.add(transformation.transform(new Point(-radius,-radius,-radius)));
		testset.add(transformation.transform(new Point(-radius,-radius,radius)));
		testset.add(transformation.transform(new Point(-radius,radius,-radius)));
		testset.add(transformation.transform(new Point(-radius,radius,radius)));
		testset.add(transformation.transform(new Point(radius,-radius,-radius)));
		testset.add(transformation.transform(new Point(radius,-radius,radius)));
		testset.add(transformation.transform(new Point(radius,radius,-radius)));
		testset.add(transformation.transform(new Point(radius,radius,radius)));
		for(Point p : testset) {
			if(p.x > max[0]) {max[0] = p.x;}
			if(p.y > max[1]) {max[1] = p.y;}
			if(p.z > max[2]) {max[2] = p.z;}
			if(p.x < min[0]) {min[0] = p.x;}
			if(p.y < min[1]) {min[1] = p.y;}
			if(p.z < min[2]) {min[2] = p.z;}
		}
		this.minCoordinates = min;
		this.maxCoordinates = max;
	}
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see shape.Shape#intersect(geometry3d.Ray3D)
	 */
	@Override
	public Intersection intersect(Ray ray) {
		ray.intersectionCount++;
		Ray transformed = transformation.transformInverse(ray);

		Vector o = transformed.origin.toVector3D();

		double a = transformed.direction.dot(transformed.direction);
		double b = 2.0 * (transformed.direction.dot(o));
		double c = o.dot(o) - radius * radius;

		double d = b * b - 4.0 * a * c;
		
		Double t;
		if (d < 0) {
			t = -1.0;
		} else { 
			double dr = Math.sqrt(d);
			double q = b < 0 ? -0.5 * (b - dr) : -0.5 * (b + dr);
	
			double t0 = q / a;
			double t1 = c / q;
	
			if( t0 >= 0 & t1 >= 0) {
				t = Math.min(t0, t1);
			} else if (t0 < 0 & t1 >= 0) {
				t =  t1;
			} else if (t1 < 0 & t0 >= 0) {
				t =  t0;
			} else {
				t =  -1.0;	
			}
			
		}
		Point hitPoint = ray.origin.add(ray.direction.scale(t));
		Point trans = transformation.transformInverse(hitPoint);
		Vector normal = trans.toVector3D().scale(1/trans.toVector3D().length());
		normal = transformation.inverseTransposeTransform(normal);
		return new Intersection(hitPoint, ray, shading, normal, t, null);
	}

	@Override
	public double[] getMinCoordinates() {
		return this.minCoordinates;
	}
	
	@Override
	public double[] getMaxCoordinates() {
		return this.maxCoordinates;
	}
	
	@Override
	public List<Intersectable> getAll() {
		List<Intersectable> toReturn = new ArrayList<Intersectable>();
		toReturn.add(this);
		return toReturn;
	}
	
	@Override
	public Intersectable getBoundingBox(String s) {
		double [] min = getMinCoordinates();
		double [] max = getMaxCoordinates();
		BoundingBox bb = new BoundingBox(min, max);
		bb.add(this);
		return bb;
	}

	@Override
	protected void setProjectedArea() {
		Point trans = transformation.transform(new Point(radius,0,0));
		Point o = transformation.transform(new Point());
		double d = trans.subtract(o).length();
		this.area = Math.PI*d*d;
	}

	@Override
	public double getCost() {
		return 1.3;
	}
}
