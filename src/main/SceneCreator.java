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
		return createBVH(method);
	}
	
	private List<Intersectable> createBVH(String method) {
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
	
	private List<Intersectable> createRegularGrid(int n) {		
		List<Intersectable> cells = new ArrayList<Intersectable>();
		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;
		double minz = Double.MAX_VALUE;
		double maxx = Double.NEGATIVE_INFINITY;
		double maxy = Double.NEGATIVE_INFINITY;
		double maxz = Double.NEGATIVE_INFINITY;
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
		double xStep = (maxx-minx)/n;
		double yStep = (maxy-miny)/n;
		double zStep = (maxz-minz)/n;
		
		List<Intersectable> allBoxes = getAllBoundingBoxes();
		
		for(int i = 0;i<n;i++) {
			for(int j = 0;j<n;j++) {
				for(int k = 0;k<n;k++) {
					double[] minBoundary = {min[0]+i*xStep,min[1]+j*yStep,min[3]+k*zStep};
					double[] maxBoundary = {min[0]+(i+1)*xStep,min[1]+(j+1)*yStep,min[3]+(k+1)*zStep};
					BoundingBox cell = new BoundingBox(minBoundary, maxBoundary);
					checkCell(cell, allBoxes);
					cells.add(cell);
				}
			}
		}
		List<Intersectable> result = new ArrayList<Intersectable>();
		result.add(new RegularGrid(cells,xStep,yStep,zStep,n));
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
		return allBoxes;
	}

	private void checkCell(BoundingBox cell, List<Intersectable> boxes) {
		for(Intersectable bb : boxes) {
			if(bb.overlap(cell)) {
				cell.add(bb.getAll());
			}
		}
	}

	public void add(Light light) {
		this.lights.add(light);
	}
	
	public List<Light> getLights() {
		return this.lights;
	}
}
