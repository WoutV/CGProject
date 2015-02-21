package shape;

import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import shading.Material;

public class Triangle implements Shape {
	
	private static final double EPSILON = 0.0000001;

	private Transformation transformation;
	
	private Point point1;
	private Point point2;
	private Point point3;
	
	private Material shading;

	private Vector normal;
	
	public Triangle(Transformation transformation, Point p1, Point p2, Point p3, Material shading) {
		if (transformation == null)
			throw new NullPointerException("the given origin is null!");
		if ( p1 == null | p2 == null | p3 == null)
			throw new NullPointerException("one of the points is null!");
		this.transformation = transformation;
		this.point1 = p1;
		this.point2 = p2;
		this.point3 = p3;
		this.shading = shading;
		this.normal = point2.subtract(point1).cross(point3.subtract(point1));
	}

	@Override
	public Intersection intersect(Ray ray) {
		Ray transformed = transformation.transformInverse(ray);
		
		Vector o = transformed.origin.toVector3D();
		
		Double t = -(o.subtract(point1.toVector3D()).dot(normal)/(transformed.direction.dot(normal)));
		Double intersection;
		Point p = transformed.origin.add(transformed.direction.scale(t));
		System.out.println(p);
		
		if(isInTriangle(p)){
			intersection = t;
		} else {
			intersection =  -1.0;
		}
		Point hitPoint = ray.origin.add(ray.direction.scale(t));
		Vector newNormal = transformation.inverseTransposeTransform(normal);
		return new Intersection(hitPoint, ray, shading, newNormal, intersection);
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
//	public Color getColor(Ray ray, List<PointLight> lights, List<Shape> shapes,  Point p) {
//		Vector newNormal = transformation.inverseTransposeTransform(normal);
//		System.out.println(normal+"NORMAL");
//		return this.color.getColor(ray, lights,shapes, p, newNormal, this);
//	}
}
