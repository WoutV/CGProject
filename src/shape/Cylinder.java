package shape;

import java.util.ArrayList;
import java.util.List;

import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import shading.Material;

public class Cylinder extends Shape {
	
	private static final double EPSILON = 0.01;
	private final double height;
	private final double radius;
	private double[] minCoordinates;
	private double[] maxCoordinates;
	public Cylinder (Transformation transformation, Material shading, double height, double radius) {
		this.height = height;
		this.radius = radius;
		this.transformation = transformation;
		this.shading = shading;
		calculateMinCoordinates();
		setProjectedArea();
	}

	private void calculateMinCoordinates() {
		double[] min = {Double.MAX_VALUE,Double.MAX_VALUE, Double.MAX_VALUE};
		double[] max = {Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY,Double.NEGATIVE_INFINITY};
		List<Point> testset = new ArrayList<Point>();
		testset.add(transformation.transform(new Point(-radius,0,-radius)));
		testset.add(transformation.transform(new Point(radius,0,-radius)));
		testset.add(transformation.transform(new Point(-radius,0,radius)));
		testset.add(transformation.transform(new Point(radius,0,radius)));
		testset.add(transformation.transform(new Point(-radius,height,-radius)));
		testset.add(transformation.transform(new Point(radius,height,-radius)));
		testset.add(transformation.transform(new Point(-radius,height,radius)));
		testset.add(transformation.transform(new Point(radius,height,radius)));
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

	@Override
	public Intersection intersect(Ray ray) {
		ray.intersectionCount++;
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
	
			boolean hitShell = (t1 > EPSILON  || t2 > EPSILON) & (transformed.origin.add(dir.scale(t0)).y >= EPSILON & transformed.origin.add(dir.scale(t0)).y < height);
			
			Point onTop = new Point(0.0,height,0.0);
			Vector normal = new Vector(0.0,1.0,0.0);
			Double hitTopPlane = (onTop.toVector3D().subtract(transformed.origin.toVector3D()).dot(normal))/(transformed.direction.dot(normal));
			boolean hitTop = Math.pow(o.add(dir.scale(hitTopPlane)).x,2)+ Math.pow(o.add(dir.scale(hitTopPlane)).z,2)- Math.pow(radius,2) <= EPSILON &  Math.abs(hitTopPlane) >= EPSILON;
			
			Point onBottom = new Point();
			Vector bottomNormal = new Vector(0.0,-1.0,0.0);
			Double hitBottomPlane = (onBottom.toVector3D().subtract(transformed.origin.toVector3D()).dot(bottomNormal))/(transformed.direction.dot(bottomNormal));
			boolean hitBottom = Math.pow(o.add(dir.scale(hitBottomPlane)).x,2)+ Math.pow(o.add(dir.scale(hitBottomPlane)).z,2) - Math.pow(radius,2) < EPSILON &  Math.abs(hitBottomPlane) > EPSILON;
			
			if(hitShell){
				intersection = t0;
			} else if(hitTop & hitBottom) {
				if(hitTopPlane < hitBottomPlane) {
					intersection = hitTopPlane > EPSILON ? hitTopPlane : -1.0;
				} else {
					intersection = hitBottomPlane > EPSILON ? hitBottomPlane : -1.0;
				}
			} else if(hitBottom) {
				intersection =  hitBottomPlane > EPSILON ? hitBottomPlane : -1.0;
			} else if(hitTop) {
				intersection =  hitTopPlane > EPSILON ? hitTopPlane : -1.0;
			} else {
				intersection =  -1.0;
			}
		}
		Point hitPoint = ray.origin.add(ray.direction.scale(intersection));
		Vector normal = getNormal(hitPoint);
		normal = transformation.inverseTransposeTransform(normal);
		if(!((intersection + 1) < EPSILON)) {
			return new Intersection(hitPoint, ray, shading, normal, intersection, null);
		}
		return null;
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
	
	@Override
	public double[] getMinCoordinates() {
//		Point trans = transformation.transform(new Point(-radius,0,-radius));
//		return new double[]{trans.x,trans.y,trans.z};
		return minCoordinates;
	}
	
	@Override
	public double[] getMaxCoordinates() {
//		Point trans = transformation.transform(new Point(radius,height,radius));
//		return new double[]{trans.x,trans.y,trans.z};
		return maxCoordinates;
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
		Point top = transformation.transform(new Point(0,height, 0));
		Point side = transformation.transform(new Point(radius,0,0));
		Point o = transformation.transform(new Point());
		double h = top.subtract(o).length();
		double r = side.subtract(o).length();
		this.area = Math.PI*r*(h+r)/2;
	}

	@Override
	public double getCost() {
		return 2.0;
	}

}
