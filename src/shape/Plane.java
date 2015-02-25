package shape;

import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import shading.Material;

public class Plane extends Shape {
	
	private static final double EPSILON = 0.00001;
	private Vector normal;
	private Point point;
	public Plane(Vector normal, Material shading, Point point, Transformation transformation) {
		this.normal = normal;
		this.point = point;
		this.transformation = transformation;
		this.shading = shading;
	}
	
	@Override
	public Intersection intersect(Ray ray) {
		Ray transformed = transformation.transformInverse(ray);
		Double t = (point.toVector3D().subtract(transformed.origin.toVector3D()).dot(normal))/(transformed.direction.dot(normal));
		Double intersection;
		if(t >=  EPSILON) {
			intersection =  t;
		}else {
			intersection = -1.0;
		}
		Point hitPoint = ray.origin.add(ray.direction.scale(intersection));
		Vector newNormal = transformation.inverseTransposeTransform(this.normal);
		return new Intersection(hitPoint, ray, shading, newNormal, t, null);
//		
	}

//	@Override
//	public Color getColor(Ray ray, List <PointLight> lights, List<Shape> shapes, Point p) {
//		
//		
//	}
}
