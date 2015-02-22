package main;

import java.util.ArrayList;
import java.util.List;

import light.PointLight;
import shape.Shape;

public class SceneCreator {
	private List<PointLight> lights = new ArrayList<PointLight>();
	private List<Shape> shapes = new ArrayList<Shape>();
	
	
	public void add(Shape shape) {
		this.shapes.add(shape);
	}
	
	public List<Shape> getShapes() {
		return this.shapes;
	}
	
	public void add(PointLight light) {
		this.lights.add(light);
	}
	
	public List<PointLight> getLights() {
		return this.lights;
	}
}
