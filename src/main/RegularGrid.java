package main;

import java.util.ArrayList;
import java.util.List;

import math.Point;
import math.Ray;
import math.Vector;
import shape.Intersectable;
import shape.Intersection;
import shape.Triangle;

public class RegularGrid extends Intersectable {
	
	private Intersectable[][][] cells;
	private double x; // the dimensions of each cell
	private double y;
	private double z;
	private int n; // amount of cells in each direction

	public RegularGrid(Intersectable[][][] cells, double xStep, double yStep, double zStep, int n) {
		this.cells = cells;
		this.x = xStep;
		this.y = yStep;
		this.z = zStep;
		this.n = n;
	}

	@Override
	public Intersection intersect(Ray ray) {
		Intersectable currentCell = null;
		System.out.println("first");
		int[] next = getFirstCell(ray);
		
		Vector dir = ray.direction;
		int traversed = 0;
		
		double nextX = x / dir.x;;
		double nextY = y / dir.y;
		double nextZ = z / dir.z;
		double tStepx = Math.abs(nextX);
		double tStepy = Math.abs(nextY);
		double tStepz = Math.abs(nextZ);
//		System.out.println("X "+nextX);
//		System.out.println("Y "+nextY);
//		System.out.println("Z "+nextZ);
		Intersection hitIntersection = null;
		double min = Double.MAX_VALUE;
		boolean done = false;
		while(!done) {
			if(next[0] == -1 | next[1] == -1 | next[2] == -1) {return null;}
			try {
				currentCell = cells[next[0]][next[1]][next[2]];
			} catch (IndexOutOfBoundsException e) {
				System.out.println("crossed "+traversed);
				return hitIntersection;
			}
			for (Intersectable i : currentCell.getAll()) {
				System.out.println("checking");
				Intersection newI = i.intersect(ray);
				if (isInCell(currentCell, newI.getPoint())) {
					if (!ray.equals(i.getLastRay())) {
						i.setLastRay(ray);
						Double t = newI.getT();
						if (t + 1 > 0.001 & t > 0.01) {
							if (t < min) {
								min = t;
								hitIntersection = newI;
							}
						}
					}
				}
			}
			traversed++;
			if (getMin(nextX,nextY,nextZ).equals(nextX)) {
				next[0]++;
				nextX+=tStepx;
			} else if (getMin(nextX,nextY,nextZ).equals(nextY)) {
				next[1]++;
				nextY+=tStepy;
			} else {
				next[2]++;
				nextZ+=tStepz;
			}
		}
		System.out.println("crossed "+traversed);
		return hitIntersection;
	}

	private Double getMin(double a, double b, double c) {
		return Double.min(Double.min(a,b),c);
	}

	private int[] getFirstCell(Ray ray) {
		int[] cellNb = {-1,-1,-1};
//		Intersectable result = null;
		double min = Double.MAX_VALUE;
		for(int i = 0;i<n;i++) {
			for(int j = 0;j<n;j++) {
				for(int k = 0;k < n;k++) {
					Intersectable cell = cells[i][j][k];
					double newT = getIntersection(cell, ray);
					if (newT+1 > 0.001 & newT > 0.01) {
						if (newT < min) {
							min = newT;
							cellNb[0] = i;
							cellNb[1] = j;
							cellNb[2] = k;
						}
					}
				}
			}
		}
//		for (int i = 0; i < 3; i++) {
//			System.out.println(cellNb[i]);
//		}
		return cellNb;
	}

	private double getIntersection(Intersectable cell, Ray ray) {
		double minx, maxx;
		double miny,maxy;
		double minz,maxz;
		double[] min = cell.getMinCoordinates();
		double[] max = cell.getMaxCoordinates();
		if(ray.direction.x >= 0){
			minx = (min[0]-ray.origin.x) / ray.direction.x;
			maxx = (max[0]-ray.origin.x) / ray.direction.x;
		} else {
			minx = (max[0]-ray.origin.x) / ray.direction.x;
			maxx = (min[0]-ray.origin.x) / ray.direction.x;
		}
		
		if(ray.direction.y >= 0){
			miny = (min[1]-ray.origin.y) / ray.direction.y;
			maxy = (max[1]-ray.origin.y) / ray.direction.y;
		} else {
			miny = (max[1]-ray.origin.y) / ray.direction.y;
			maxy = (min[1]-ray.origin.y) / ray.direction.y;
		}

		if(ray.direction.z >= 0){
			minz= (min[2]-ray.origin.z) / ray.direction.z;
			maxz = (max[2]-ray.origin.z) / ray.direction.z;
		} else {
			minz = (max[2]-ray.origin.z) / ray.direction.z;
			maxz = (min[2]-ray.origin.z) / ray.direction.z;
		}
		
		double tmin = Math.max(minx, Math.max(miny, minz));
		double tmax = Math.min(maxx, Math.min(maxy, maxz));
		
		if(tmin < tmax) {
			return tmin;
		}
		return Double.MAX_VALUE;
	}

	private boolean isInCell(Intersectable currentCell, Point point) {
		double[] min = currentCell.getMinCoordinates();
		double[] max = currentCell.getMaxCoordinates();
		
		return(point.x >= min[0] && point.x <= max[0] && point.y >= min[1] && point.y <= max[1] && point.z >= min[2] && point.z <= max[2]);
	}

	@Override
	public double[] getMinCoordinates() {
		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;
		double minz = Double.MAX_VALUE;
		for(int i = 0;i<n;i++) {
			for(int j = 0;j<n;j++) {
				for(int k = 0;k < n;k++) {
					Intersectable cell = cells[i][j][k];
					double [] min = cell.getMinCoordinates();
					if(min[0] < minx) { minx = min[0];}
					if(min[1] < miny) { miny = min[1];}
					if(min[2] < minz) { minz = min[2];}
				}
			}
		}
		return new double[]{minx,miny,minz};
	}

	@Override
	public double[] getMaxCoordinates() {
		double minx = Double.NEGATIVE_INFINITY;
		double miny = Double.NEGATIVE_INFINITY;
		double minz =Double.NEGATIVE_INFINITY;
		for(int i = 0;i<n;i++) {
			for(int j = 0;j<n;j++) {
				for(int k = 0;k < n;k++) {
					Intersectable cell = cells[i][j][k];
					double [] max = cell.getMaxCoordinates();
					if(max[0] > minx) { minx = max[0];}
					if(max[1] > miny) { miny = max[1];}
					if(max[2] > minz) { minz = max[2];}
				}
			}
		}
		return new double[]{minx,miny,minz};
	}

	@Override
	public List<Intersectable> getAll() {
		List<Intersectable> toReturn = new ArrayList<Intersectable>();
		for(int i = 0;i<n;i++) {
			for(int j = 0;j<n;j++) {
				for(int k = 0;k < n;k++) {
					Intersectable cell = cells[i][j][k];
					toReturn.addAll(cell.getAll());
				}
			}
		}
		return toReturn;
	}
}