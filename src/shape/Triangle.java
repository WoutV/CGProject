package shape;

import java.awt.Color;
import java.util.List;

import light.PointLight;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import shading.Diffuse;

public class Triangle implements Shape {
	
	private static final double EPSILON = 0.0000001;

	private Transformation transformation;
	
	private Point point1;
	private Point point2;
	private Point point3;
	
	private Diffuse color;

	private Vector normal;
	
	public Triangle(Transformation transformation, Point p1, Point p2, Point p3, Diffuse color) {
		if (transformation == null)
			throw new NullPointerException("the given origin is null!");
		if ( p1 == null | p2 == null | p3 == null)
			throw new NullPointerException("one of the points is null!");
		this.transformation = transformation;
		this.point1 = p1;
		this.point2 = p2;
		this.point3 = p3;
		this.color = color;
		this.normal = point2.subtract(point1).cross(point3.subtract(point1));
	}

	@Override
	public Double intersect(Ray ray) {
		Ray transformed = transformation.transformInverse(ray);
		
		Vector o = transformed.origin.toVector3D();
		
		Double t = -(o.subtract(point1.toVector3D()).dot(normal)/(transformed.direction.dot(normal)));
		
		Point p = transformed.origin.add(transformed.direction.scale(t));
		System.out.println(p);
		
		if(isInTriangle(p)){
			return t;
		}
		return -1.0;
	}

	/**
	 * Checks whether or not a given point is inside this triangle. This is done by calculating the barycentric coordinates of the point.
	 * @param p the given point
	 * @return true if and only if the point is inside the triangle
	 */
	private boolean isInTriangle(Point p) {
		double l21 = point2.subtract(point1).lengthSquared();
		double lca = point2.subtract(point1).dot(point3.subtract(point1));
		double l32 = point3.subtract(point2).lengthSquared();
		double d2p = p.subtract(point1).dot(point2.subtract(point1));
		double d21 = p.subtract(point1).dot(point3.subtract(point1));
		
		double denom = l21 * l32 - lca* lca;
		
		double alpha = ((l32 * d2p) - (lca * d21)) / denom;
		double beta = ((l21 * d21) - (lca * d2p)) / denom;
		double gamma = 1 - alpha - beta;
		
		System.out.println("A= " + alpha);
		System.out.println("B= " +beta);
		System.out.println("C= " +gamma);
		return alpha <= 1 && alpha >= EPSILON && (beta <= 1) && beta >= EPSILON && (gamma <= 1) && gamma >= EPSILON;
	}

//	@Override
//	public Color getColor(Ray ray, PointLight light, Point p) {
////		Point trans1 = transformation.transformInverse(point1);
////		Point trans2 = transformation.transformInverse(point2);
////		Point trans3 = transformation.transformInverse(point3);
////		Vector newNormal = trans2.subtract(trans1).cross(trans3.subtract(trans1));
////		newNormal = newNormal.scale(newNormal.length());
//		Vector newNormal = transformation.inverseTransposeTransform(normal);
//		System.out.println(normal+"NORMAL");
//		return this.color.getColor(ray, light, p, newNormal);
//	}

	@Override
	public Point getIntersection(Ray ray) {
		Ray transformed = transformation.transformInverse(ray);
		Vector o = transformed.origin.toVector3D();
		Double t = -(o.subtract(point1.toVector3D()).dot(normal)/(transformed.direction.dot(normal)));
		Point p = ray.origin.add(ray.direction.scale(t));
		System.out.println(p+"HITPOINT");
		return p;
	}

	@Override
	public Color getColor(Ray ray, List<PointLight> lights, Point p) {
		Vector newNormal = transformation.inverseTransposeTransform(normal);
		System.out.println(normal+"NORMAL");
		return this.color.getColor(ray, lights, p, newNormal);
	}
}
