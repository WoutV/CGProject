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
//			Intersectable bb = t.getBoundingBox(method);
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

	public void add(Light light) {
		this.lights.add(light);
	}
	
	public List<Light> getLights() {
		return this.lights;
	}
}
