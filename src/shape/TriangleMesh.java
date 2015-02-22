package shape;

import java.util.ArrayList;
import java.util.List;

import math.Ray;
import math.Transformation;
import shading.Material;

public class TriangleMesh implements Shape {
	
	private Material shading;
	private Transformation transformation;
	private List<Triangle> triangles= new ArrayList<Triangle>();
	
	public TriangleMesh() {
		//empty
	}
	
	public void setShading(Material material) {
		this.shading = material;
	}
	
	public void setTransformation(Transformation t) {
		this.transformation = t;
		for(Triangle tr : triangles) {
			tr.setTransformation(transformation);
		}
	}
	
	@Override
	public Intersection intersect(Ray ray) {
		Intersection hitIntersection = null;
		Double min = Double.MAX_VALUE;
		for (Triangle triangle : triangles) {
			Intersection intersection = triangle.intersect(ray);
			Double t = intersection.getT();
			if (t+1 > 0.001 & t > 0.01) {
				if (t < min) {
					min = t;
					hitIntersection = intersection;
				}
			}
		}
		if(hitIntersection!=null) {
			hitIntersection.setMaterial(shading);
			System.err.println(hitIntersection.getNormal().y);
			return hitIntersection;
		}
		return new Intersection(null, null, null, null, -1.0);
	}
	
	public void addTriangle(Triangle triangle) {
		triangles.add(triangle);
	}

}
