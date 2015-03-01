package shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import shading.Material;

public class Cone extends Shape {
	
	private static final double EPSILON = 0.00001;
	private final double height;
	private final double radius;
	public Cone(double height, double radius, Transformation transformation, Material shading) {
		this.height = height;
		this.radius = radius;
		this.transformation = transformation;
		this.shading = shading;
	}
	@Override
	public Intersection intersect(Ray ray) {
		ray.intersectionCount++;
		Ray transformed = transformation.transformInverse(ray);
		Double intersection;
		Vector dir = transformed.direction;

		Vector o = transformed.origin.toVector3D();
		
		double a = dir.x*dir.x + dir.z*dir.z - radius*radius*dir.y*dir.y/(height*height);
		double b = 2*o.x*dir.x + 2*o.z*dir.z + (2*radius*radius/height)*dir.y - 2*radius*radius*o.y*dir.y/(height*height);
		double c = o.x*o.x+o.z*o.z - radius*radius + 2*radius*radius*o.y/height - radius*radius*o.y*o.y/(height*height);
		
		double d = b * b - 4.0 * a * c;
		if (d < 0) {
			intersection = -1.0;
		} else {
			double dr = Math.sqrt(d);
			
			double t1 = (-b+dr)/(2*a);
			double t2 = (-b-dr)/(2*a);
	
			double t0 = Math.min(t1, t2);
			
			boolean hitShell = (t0 >= 0) & (transformed.origin.add(dir.scale(t0)).y >= EPSILON & transformed.origin.add(dir.scale(t0)).y < height);
			
			Point onBottom = new Point();
			Vector bottomNormal = new Vector(0.0,-1.0,0.0);
			Double hitBottomPlane = (onBottom.toVector3D().subtract(transformed.origin.toVector3D()).dot(bottomNormal))
					/(transformed.direction.dot(bottomNormal));
			boolean hitBottom = Math.pow(o.add(dir.scale(hitBottomPlane)).x,2)+ 
					Math.pow(o.add(dir.scale(hitBottomPlane)).z,2) - Math.pow(radius,2) <= EPSILON 
					&  Math.abs(hitBottomPlane) >= EPSILON;
					
			if(hitShell) {
				intersection = t0;
			} else if(hitBottom) {
				intersection = hitBottomPlane;
			} else {
				intersection = -1.0;
			}
		}
		Point hitPoint = ray.origin.add(ray.direction.scale(intersection));
		Point trans = transformation.transformInverse(hitPoint);
		Vector normal = null;
		if(Math.abs(trans.y)<EPSILON) {
			normal = new Vector(0.0,-1.0,0.0);
		} else {
			double y = Math.sqrt(trans.x*trans.x+trans.z*trans.z)*(height/radius);
			normal = new Vector(trans.x,y,trans.z);
		}
		normal = transformation.inverseTransposeTransform(normal);
		return new Intersection(hitPoint, ray, shading, normal, intersection, null);
	}
	
	@Override
	public double[] getMinCoordinates() {
		Point trans = transformation.transform(new Point(-radius,0,-radius));
		return new double[]{trans.x,trans.y,trans.z};
	}
	
	@Override
	public double[] getMaxCoordinates() {
		Point trans = transformation.transform(new Point(radius,height,radius));
		return new double[]{trans.x,trans.y,trans.z};
	}
	
	@Override
	public Collection<Intersectable> getAll() {
		List<Intersectable> toReturn = new ArrayList<Intersectable>();
		toReturn.add(this);
		return toReturn;
	}
	
	@Override
	public Intersectable getBoundingBox() {
		double [] min = getMinCoordinates();
		double [] max = getMaxCoordinates();
		BoundingBox bb = new BoundingBox(min[0],max[0],min[1],max[1],min[2],max[2]);
		bb.add(this);
		return bb;
	}

}