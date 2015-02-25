package shape;

import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import shading.Material;

public class Cylinder extends Shape {
	
	private static final double EPSILON = 0.00001;
	private final double height;
	private final double radius;
	public Cylinder (Transformation transformation, Material shading, double height, double radius) {
		this.height = height;
		this.radius = radius;
		this.transformation = transformation;
		this.shading = shading;
	}

	@Override
	public Intersection intersect(Ray ray) {
		Ray transformed = transformation.transformInverse(ray);
		Double intersection;
		Vector dir = transformed.direction;

		Vector o = transformed.origin.toVector3D();
		
		double a = dir.x*dir.x + dir.z*dir.z;
		double b = 2*o.x*dir.x + 2*o.z*dir.z;
		double c = o.x*o.x+o.z*o.z - radius*radius;
		
		double d = b * b - 4.0 * a * c;
		if (d < 0) {
			intersection = -1.0;
		} else {
			double dr = Math.sqrt(d);
			double t1 = (-b+dr)/(2*a);
			double t2 = (-b-dr)/(2*a);
			double t0 = Math.min(t1, t2);
	
			boolean hitShell = (t0 >= 0) & (transformed.origin.add(dir.scale(t0)).y >= EPSILON & transformed.origin.add(dir.scale(t0)).y < height);
			
			Point onTop = new Point(0.0,height,0.0);
			Vector normal = new Vector(0.0,1.0,0.0);
			Double hitTopPlane = (onTop.toVector3D().subtract(transformed.origin.toVector3D()).dot(normal))/(transformed.direction.dot(normal));
			boolean hitTop = Math.pow(o.add(dir.scale(hitTopPlane)).x,2)+ Math.pow(o.add(dir.scale(hitTopPlane)).z,2)- Math.pow(radius,2) <= EPSILON &  Math.abs(hitTopPlane) >= EPSILON;
			
			Point onBottom = new Point();
			Vector bottomNormal = new Vector(0.0,-1.0,0.0);
			Double hitBottomPlane = (onBottom.toVector3D().subtract(transformed.origin.toVector3D()).dot(bottomNormal))/(transformed.direction.dot(bottomNormal));
			boolean hitBottom = Math.pow(o.add(dir.scale(hitBottomPlane)).x,2)+ Math.pow(o.add(dir.scale(hitBottomPlane)).z,2) - Math.pow(radius,2) <= EPSILON &  Math.abs(hitBottomPlane) >= EPSILON;
			
			if(hitShell){
				intersection = t0;
			} else if(hitTop & hitBottom) {
				if(hitTopPlane < hitBottomPlane) {
					intersection = hitTopPlane > 0 ? hitTopPlane : -1.0;
				} else {
					intersection = hitBottomPlane > 0 ? hitBottomPlane : -1.0;
				}
			} else if(hitBottom) {
				intersection =  hitBottomPlane > 0 ? hitBottomPlane : -1.0;
			} else if(hitTop) {
				intersection =  hitTopPlane > 0 ? hitTopPlane : -1.0;
			} else {
				intersection =  -1.0;
			}
		}
		Point hitPoint = ray.origin.add(ray.direction.scale(intersection));
		Vector normal = getNormal(hitPoint);
		normal = transformation.inverseTransposeTransform(normal);
		return new Intersection(hitPoint, ray, shading, normal, intersection, null);
	}

	private Vector getNormal(Point hitPoint) {
		Point trans = transformation.transformInverse(hitPoint);
		Vector normal = null;
		if(Math.abs(trans.y-height) < EPSILON) {
			normal = new Vector(0.0,1.0,0.0);
		} else if(Math.abs(trans.y) < EPSILON) {
			normal = new Vector(0.0,-1.0,0.0);
		} else {
			normal = new Vector(trans.x,0.0, trans.z);
		}
		return normal;
	}

}
