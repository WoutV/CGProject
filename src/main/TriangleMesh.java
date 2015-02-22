package main;

import java.util.ArrayList;
import java.util.List;

import math.Ray;
import shape.Intersection;
import shape.Shape;
import shape.Triangle;

public class TriangleMesh implements Shape {
	
	private List<Triangle> triangles= new ArrayList<Triangle>();
	
	@Override
	public Intersection intersect(Ray ray) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void addTriangle(Triangle triangle) {
		triangles.add(triangle);
	}

}
