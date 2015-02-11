package shape;

import java.awt.Color;

import light.PointLight;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;

public class Plane implements Shape {
	
	private static final double EPSILON = 0.00001;
	private Vector normal;
	private Point point;
	private Transformation transformation;
	
	public Plane(Vector normal, Point point, Transformation transformation) {
		this.normal = normal;
		this.point = point;
		this.transformation = transformation;
	}
	
	@Override
	public boolean intersect(Ray ray) {
		Ray transformed = transformation.transformInverse(ray);
		
		Vector o = transformed.origin.toVector3D();
		
		Double t = (point.toVector3D().subtract(transformed.origin.toVector3D()).dot(normal))/(transformed.direction.dot(normal));
		System.out.println(t);
		return t >= (0 + EPSILON);
	}

	@Override
	public Color getColor(Ray ray, PointLight light, Point p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Point getIntersection(Ray ray) {
		// TODO Auto-generated method stub
		return null;
	}

}
