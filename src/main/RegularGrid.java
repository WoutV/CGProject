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
		Intersection hitIntersection = null;
		int[] next = getFirstCell(ray);
		
		Vector dir = ray.direction;
		
		try {
			currentCell = cells[next[0]][next[1]][next[2]];
		} catch (IndexOutOfBoundsException e) {
			return hitIntersection;
		}
		Intersection inters = currentCell.intersect(ray);
		double[] nextXYZ = new double[3];
		if(inters!=null){
			Point p = inters.getPoint();
			nextXYZ = getClosest(p,ray, currentCell);
		}
		
		double nextX = nextXYZ[0];
		double nextY = nextXYZ[1];
		double nextZ =  nextXYZ[2];
		double tStepx = Math.abs(x / dir.x);
		double tStepy = Math.abs(y / dir.y);
		double tStepz = Math.abs(z / dir.z);
		double min = Double.MAX_VALUE;
		boolean done = false;
		while(!done) {
			if(next[0] == -1 | next[1] == -1 | next[2] == -1) {return null;}
			try {
				currentCell = cells[next[0]][next[1]][next[2]];
			} catch (IndexOutOfBoundsException e) {
				return hitIntersection;
			}
			for (Intersectable i : currentCell.getAll()) {
				Intersection newI = i.intersect(ray);
				if (newI!=null && isInCell(currentCell, newI.getPoint())) {
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
			if(hitIntersection!=null) {return hitIntersection;}
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
		return hitIntersection;
	}

	private double[] getClosest(Point p, Ray ray, Intersectable currentCell) {
		double[] result = new double[3];
		if(ray.direction.x != 0) {
			if(ray.direction.x < 0) {
				result[0] = Math.abs((currentCell.getMinCoordinates()[0]-p.x)/ray.direction.x);
			} else {
				result[0] = Math.abs((currentCell.getMaxCoordinates()[0]-p.x)/ray.direction.x);
			}
		} else { 
			result[0] = Double.MAX_VALUE;
		}
		if(ray.direction.y != 0) {
			if(ray.direction.y < 0) {
				result[1] = Math.abs((currentCell.getMinCoordinates()[1]-p.y)/ray.direction.y);
			} else {
				result[1] = Math.abs((currentCell.getMaxCoordinates()[1]-p.y)/ray.direction.y);
			}
		} else { 
			result[1] = Double.MAX_VALUE;
		}
		if(ray.direction.z != 0) {
			if(ray.direction.z < 0) {
				result[2] = Math.abs((currentCell.getMinCoordinates()[2]-p.z)/ray.direction.z);
			} else {
				result[2] = Math.abs((currentCell.getMaxCoordinates()[2]-p.z)/ray.direction.z);
			}
		} else { 
			result[2] = Double.MAX_VALUE;
		}
		return result;
	}

	private Double getMin(double a, double b, double c) {
		return Math.min(Math.min(a,b),c);
	}

	private int[] getFirstCell(Ray ray) {
		int[] cellNb = {-1,-1,-1};
		double min = Double.MAX_VALUE;
		for(int i = 0;i<n;i++) {
			for(int j = 0;j<n;j++) {
				for(int k = 0;k < n;k++) {
					Intersectable cell = cells[i][j][k];
					Intersection ints = cell.intersect(ray);
					double newT = -1.0;
					if(ints!=null) {
						newT = cell.intersect(ray).getT();
					}
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
		return cellNb;
	}

	private boolean isInCell(Intersectable currentCell, Point point) {
		double[] min = currentCell.getMinCoordinates();
		double[] max = currentCell.getMaxCoordinates();
//		return true;
		return (Math.abs(point.x-min[0]) > 0 && Math.abs(point.y-min[1]) > 0 && Math.abs(point.z-min[2]) > 0
				&& Math.abs(point.x-max[0]) > 0 && Math.abs(point.y-max[1]) > 0 && Math.abs(point.z-max[2]) > 0);
//		return(point.x >= min[0] && point.x <= max[0] && point.y >= min[1] && point.y <= max[1] && point.z >= min[2] && point.z <= max[2]);
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

	@Override
	protected void setProjectedArea() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getCost() {
		// TODO Auto-generated method stub
		return 0;
	}
}