package main;

import java.util.ArrayList;
import java.util.List;

import math.Ray;
import shape.Intersectable;
import shape.Intersection;
import shape.Triangle;

public class RegularGrid extends Intersectable {
	
	private List<Intersectable> cells;
	private double x;
	private double y;
	private double z;
	private int n;

	public RegularGrid(List<Intersectable> cells, double xStep, double yStep, double zStep, int n) {
		this.cells = cells;
		this.x = xStep;
		this.y = yStep;
		this.z = zStep;
		this.n = n;
	}

	@Override
	public Intersection intersect(Ray ray) {
		Intersection hitIntersection = null;
		double min = Double.MAX_VALUE;
		Intersectable currentCell = null;
		Intersectable nextCell = getFirstCell(ray);
		double nextX;
		double nextY;
		double nextZ;
		
		
		
		boolean done = false;
		while(!done) {
			currentCell = nextCell;
			for(Intersectable i : currentCell.getAll()) {
				if(!ray.equals(i.getLastRay())) {
					Intersection newI = i.intersect(ray);
					i.setLastRay(ray);
					Double t = newI.getT();
					if (t+1 > 0.001 & t > 0.01) {
						if (t < min) {
							min = t;
							hitIntersection = newI;
						}
					}
				}
			}
		}
		return hitIntersection;
	}

	@Override
	public double[] getMinCoordinates() {
		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;
		double minz = Double.MAX_VALUE;
		for(Intersectable cell : cells) {
			double [] min = cell.getMinCoordinates();
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
		for(Intersectable cell : cells) {
			double [] max = cell.getMaxCoordinates();
			if(max[0] > minx) { minx = max[0];}
			if(max[1] > miny) { miny = max[1];}
			if(max[2] > minz) { minz = max[2];}
		}
		return new double[]{minx,miny,minz};
	}

	@Override
	public List<Intersectable> getAll() {
		List<Intersectable> toReturn = new ArrayList<Intersectable>();
		for(Intersectable i : cells) {
			toReturn.addAll(i.getAll());
		}
		return toReturn;
	}

}
