package shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
		ray.intersectionCount++;
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
	
	@Override
	public double[] getMinCoordinates() {
		return new double[]{Double.MIN_VALUE,Double.MIN_VALUE,Double.MIN_VALUE};
	}
	
	@Override
	public double[] getMaxCoordinates() {
		return new double[]{Double.MAX_VALUE,Double.MAX_VALUE,Double.MAX_VALUE};
	}
	
	@Override
	public List<Intersectable> getAll() {
		List<Intersectable> toReturn = new ArrayList<Intersectable>();
		toReturn.add(this);
		return toReturn;
	}
}
