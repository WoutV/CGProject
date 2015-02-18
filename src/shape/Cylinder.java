package shape;

import java.awt.Color;
import java.util.List;

import light.PointLight;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import shading.Diffuse;

public class Cylinder implements Shape {
	
	private static final double EPSILON = 0.00001;
	private final double height;
	private final double radius;
	private Transformation transformation;
	private Diffuse shading;
	
	public Cylinder (Transformation transformation, Diffuse diffuse, double height, double radius) {
		this.height = height;
		this.radius = radius;
		this.transformation = transformation;
		this.shading = diffuse;
	}

	@Override
	public Double intersect(Ray ray) {
		System.out.println("INTERSECTIN");
		Ray transformed = transformation.transformInverse(ray);
		
		Vector dir = transformed.direction;

		Vector o = transformed.origin.toVector3D();
		
		double a = dir.x*dir.x + dir.z*dir.z;
		double b = 2*o.x*dir.x + 2*o.z*dir.z;
		double c = o.x*o.x+o.z*o.z - radius*radius;
		
		double d = b * b - 4.0 * a * c;
		if (d < 0) {
			return -1.0;
		}
		double dr = Math.sqrt(d);
		
		double t1 = (-b+dr)/(2*a);
		double t2 = (-b-dr)/(2*a);

		double t0 = Math.min(t1, t2);
		
		System.out.println(t0);
		System.out.println(t1);
		
		

		boolean hitShell = (t1 >= 0 || t2 >= 0) & (transformed.origin.add(dir.scale(t0)).y >= EPSILON & transformed.origin.add(dir.scale(t0)).y < height);
		
		Point onTop = new Point(0.0,height,0.0);
		Vector normal = new Vector(0.0,1.0,0.0);
		Double hitTopPlane = (onTop.toVector3D().subtract(transformed.origin.toVector3D()).dot(normal))/(transformed.direction.dot(normal));
		boolean hitTop = Math.pow(o.add(dir.scale(hitTopPlane)).x,2)+ Math.pow(o.add(dir.scale(hitTopPlane)).z,2)- radius <= EPSILON &  Math.abs(hitTopPlane) >= EPSILON;
		
		Point onBottom = new Point();
		Vector bottomNormal = new Vector(0.0,-1.0,0.0);
		Double hitBottomPlane = (onBottom.toVector3D().subtract(transformed.origin.toVector3D()).dot(bottomNormal))/(transformed.direction.dot(bottomNormal));
		boolean hitBottom = Math.pow(o.add(dir.scale(hitBottomPlane)).x,2)+ Math.pow(o.add(dir.scale(hitBottomPlane)).z,2) - radius <= EPSILON &  Math.abs(hitBottomPlane) >= EPSILON;
		
		
		
		if(hitShell){
			return t0;
		}
		if(hitTop & hitBottom) {
			if(hitTopPlane < hitBottomPlane) {
				return hitTopPlane > 0 ? hitTopPlane : -1.0;
			}
			return hitBottomPlane > 0 ? hitBottomPlane : -1.0;
		} else if(hitBottom) {
			return hitTopPlane > 0 ? hitTopPlane : -1.0;
		} else if(hitTop) {
			return hitTopPlane > 0 ? hitTopPlane : -1.0;
		}
		return -1.0;
	}


	@Override
	public Color getColor(Ray ray, List<PointLight> lights, List<Shape> shapes, Point p) {
		Point trans = transformation.transformInverse(p);
		Vector normal = null;
		if(Math.abs(trans.y-height) < EPSILON) {
			normal = new Vector(0.0,1.0,0.0);
		} else if(Math.abs(trans.y) < EPSILON) {
			normal = new Vector(0.0,1.0,0.0);
		} else {
			normal = new Vector(trans.x,0.0, trans.z);
		}
		normal = transformation.inverseTransposeTransform(normal);
		transformation.getInverseTransformationMatrix().transpose();
		return this.shading.getColor(ray, lights,shapes, p, normal, this);
	}

}
