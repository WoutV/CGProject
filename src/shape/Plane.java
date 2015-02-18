package shape;

import java.awt.Color;
import java.util.List;

import light.PointLight;
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
	public Double intersect(Ray ray) {
		Ray transformed = transformation.transformInverse(ray);
		Double t = (point.toVector3D().subtract(transformed.origin.toVector3D()).dot(normal))/(transformed.direction.dot(normal));
		
		if(t >=  EPSILON) {
			return t;
		}
		return -1.0;
	}

	@Override
	public Color getColor(Ray ray, List <PointLight> lights, List<Shape> shapes, Point p) {
		
		Vector newNormal = transformation.inverseTransposeTransform(this.normal);
		
		return this.shading.getColor(ray, lights,shapes, p, newNormal, this);
	}
}
