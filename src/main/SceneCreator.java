package main;

import java.util.ArrayList;
import java.util.List;

import light.Light;
import shape.Intersectable;
import shape.Shape;

public class SceneCreator {
	private List<Light> lights = new ArrayList<Light>();
	private List<Intersectable> shapes = new ArrayList<Intersectable>();
	
	
	public void add(Shape shape) {
		this.shapes.add(shape);
	}
	
	public List<Intersectable> getShapes() {
//		return this.shapes;
		return createBVH();
	}
	
	private List<Intersectable> createBVH() {
		List<Intersectable> toReturn =  new ArrayList<Intersectable>();
		for(Intersectable t : shapes) {
			Intersectable bb = t.getBoundingBox();
			toReturn.add(bb);
		}
		return toReturn;
	}

	public void add(Light light) {
		this.lights.add(light);
	}
	
	public List<Light> getLights() {
		return this.lights;
	}
}
