package shape;

import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import shading.Diffuse;

public class Plane implements Shape {
	
	private static final double EPSILON = 0.00001;
	private Vector normal;
	private Point point;
	private Transformation transformation;
	private Diffuse shading;
	
	public Plane(Vector normal, Diffuse diff, Point point, Transformation transformation) {
		this.normal = normal;
		this.point = point;
		this.transformation = transformation;
		this.shading = diff;
	}
	
	@Override
	public Intersection intersect(Ray ray) {
		Ray transformed = transformation.transformInverse(ray);
		Double t = (point.toVector3D().subtract(transformed.origin.toVector3D()).dot(normal))/(transformed.direction.dot(normal));
		Double intersection;
		if(t >=  EPSILON) {
			intersection =  t;
		}else {
			System.err.println("PLANE NOT HIT");
			intersection = -1.0;
		}
		Point hitPoint = ray.origin.add(ray.direction.scale(intersection));
		Vector newNormal = transformation.inverseTransposeTransform(this.normal);
		return new Intersection(hitPoint, ray, shading, newNormal, t);
//		
	}

//	@Override
//	public Color getColor(Ray ray, List <PointLight> lights, List<Shape> shapes, Point p) {
//		
//		
//	}
}
