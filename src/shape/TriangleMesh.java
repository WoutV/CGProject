package shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import math.Ray;
import math.Transformation;
import shading.Material;

public class TriangleMesh extends Shape {
	
	private List<Triangle> triangles= new ArrayList<Triangle>();
	
	public TriangleMesh() {
		//empty
	}
	
	public void setShading(Material material) {
		this.shading = material;
		for(Triangle tr : triangles) {
			tr.setShading(material);
		}
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
			return hitIntersection;
		}
		return new Intersection(null, null, null, null, -1.0, null);
	}
	
	public void addTriangle(Triangle triangle) {
		triangles.add(triangle);
	}
	
	public List<Triangle> getTriangles() {
		return new ArrayList<Triangle>(this.triangles);
	}

	@Override
	public double[] getMinCoordinates() {
		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;
		double minz = Double.MAX_VALUE;
		for(Triangle t : triangles) {
			double [] min = t.getMinCoordinates();
			if(min[0] < minx) { minx = min[0];}
			if(min[1] < miny) { miny = min[1];}
			if(min[2] < minz) { minz = min[2];}
		}
		
		return new double[]{minx,miny,minz};
	}

	@Override
	public double[] getMaxCoordinates() {
		double minx = Double.NEGATIVE_INFINITY;
		double miny = Double.NEGATIVE_INFINITY;
		double minz =Double.NEGATIVE_INFINITY;
		for(Triangle t : triangles) {
			double [] max = t.getMaxCoordinates();
			if(max[0] > minx) { minx = max[0];}
			if(max[1] > miny) { miny = max[1];}
			if(max[2] > minz) { minz = max[2];}
		}
		
		return new double[]{minx,miny,minz};
	}
	
	@Override
	public Collection<Intersectable> getAll() {
		List<Intersectable> toReturn = new ArrayList<Intersectable>();
		toReturn.addAll(triangles);
		return toReturn;
	}
	
	@Override
	public Intersectable getBoundingBox() {
		double [] min = getMinCoordinates();
		double [] max = getMaxCoordinates();
		BoundingBox bb = new BoundingBox(min, max);
		bb.add(this);
		bb.split(0);
		return bb;
	}

}
