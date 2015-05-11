package main;

import java.util.ArrayList;
import java.util.List;

import light.Light;
import shape.BoundingBox;
import shape.Intersectable;
import shape.Shape;

public class SceneCreator {
	private List<Light> lights = new ArrayList<Light>();
	private List<Intersectable> shapes = new ArrayList<Intersectable>();
	
	
	public void add(Shape shape) {
		this.shapes.add(shape);
	}
	
	public List<Intersectable> getShapes(String method) {
//		return this.shapes;
//		return createBVH(method);
		return createRegularGrid();
	}
	
	private List<Intersectable> createBVH(String method) {
		if(method.equals(null)) {method = "geometric";}
		System.out.println(shapes.size());
		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;
		double minz = Double.MAX_VALUE;
		double maxx = Double.NEGATIVE_INFINITY;
		double maxy = Double.NEGATIVE_INFINITY;
		double maxz = Double.NEGATIVE_INFINITY;
		List<Intersectable> toReturn =  new ArrayList<Intersectable>();
		for(Intersectable t : shapes) {
			double[] minb =t.getMinCoordinates();
			double [] maxb = t.getMaxCoordinates();
			if(minb[0] < minx) { minx = minb[0];}
			if(minb[1] < miny) { miny = minb[1];}
			if(minb[2] < minz) { minz = minb[2];}
			if(maxb[0] > maxx) { maxx = maxb[0];}
			if(maxb[1] > maxy) { maxy = maxb[1];}
			if(maxb[2] > maxz) { maxz = maxb[2];}
		}
		double[] min = {minx,miny,minz};
		double[] max = {maxx,maxy,maxz};
		BoundingBox box = new BoundingBox(min, max);
		for(Intersectable t : shapes) {
			box.add(t);
		}
		switch (method) {
		case "geometric":
			box.split(0);
			break;
		case "sorted":
			box.splitSorted(0);
			break;
		default:
			break;
		}
		toReturn.add(box);
		return toReturn;
	}
	
	private List<Intersectable> createRegularGrid() {		
		
		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;
		double minz = Double.MAX_VALUE;
		double maxx = Double.NEGATIVE_INFINITY;
		double maxy = Double.NEGATIVE_INFINITY;
		double maxz = Double.NEGATIVE_INFINITY;
		for(Intersectable t : shapes) {
			double[] minb =t.getMinCoordinates();
			double [] maxb = t.getMaxCoordinates();
//			for(int i = 0;i<3;i++)
//				System.out.println(minb[i]);
//			for(int i = 0;i<3;i++)
//				System.out.println(maxb[i]);
			if(minb[0] < minx) { minx = minb[0];}
			if(minb[1] < miny) { miny = minb[1];}
			if(minb[2] < minz) { minz = minb[2];}
			if(maxb[0] > maxx) { maxx = maxb[0];}
			if(maxb[1] > maxy) { maxy = maxb[1];}
			if(maxb[2] > maxz) { maxz = maxb[2];}
		}
		double[] min = {minx,miny,minz};
		double[] max = {maxx,maxy,maxz};
		
		
		List<Intersectable> allBoxes = getAllBoundingBoxes();
		int n =  (int) Math.pow(allBoxes.size(), 1.0/3);
//		int n = 15;
		System.out.println("size of grid: "+n);
		double xStep = Math.abs((maxx-minx)/n);
		double yStep = Math.abs((maxy-miny)/n);
		double zStep = Math.abs((maxz-minz)/n);
		System.out.println(xStep);
		System.out.println(yStep);
		System.out.println(zStep);
//		
		System.out.println("creating cellzzzzz");
		System.out.println("Amount of boxes to be checked " + allBoxes.size());
		Intersectable[][][] cells = new Intersectable[n][n][n];
		for(int i = 0;i<n;i++) {
			for(int j = 0;j<n;j++) {
				for(int k = 0;k<n;k++) {
					double[] minBoundary = {min[0]+i*xStep,min[1]+j*yStep,min[2]+k*zStep};
					double[] maxBoundary = {minBoundary[0]+xStep,minBoundary[1]+yStep,minBoundary[2]+zStep};
					BoundingBox cell = new BoundingBox(minBoundary, maxBoundary);
					checkCell(cell, allBoxes);
					cells[i][j][k] = cell;
				}
			}
		}
		List<Intersectable> result = new ArrayList<Intersectable>();
		Intersectable grid = new RegularGrid(cells,xStep,yStep,zStep,n);
		BoundingBox bb = new BoundingBox(min,max);
		bb.add(grid);
		result.add(bb);
		System.out.println("grid created G");
		return result;
	}

	private List<Intersectable> getAllBoundingBoxes() {
		List<Intersectable> allBoxes = new ArrayList<Intersectable>();
		List<Intersectable> allShapes = new ArrayList<Intersectable>();
		for(Intersectable in : shapes) {
			allShapes.addAll(in.getAll());
		}
		for(Intersectable shape : allShapes) {
			allBoxes.add(shape.getBoundingBox(null));
		}
		return allShapes;
	}

	private void checkCell(BoundingBox cell, List<Intersectable> boxes) {
		for(Intersectable bb : boxes) {
			if(bb.overlap(cell)) {
				cell.add(bb.getAll());
//				System.out.println(bb.getAll().get(0).getClass());
			}
		}
//		if(cell.getAll().size() > 0)
//			System.out.println("amount added: " + cell.getAll().size());
	}

	public void add(Light light) {
		this.lights.add(light);
	}
	
	public List<Light> getLights() {
		return this.lights;
	}
}
