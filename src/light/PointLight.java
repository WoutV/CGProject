package light;

import java.awt.Color;

import math.Point;

public class PointLight extends Light {

	private Point location;
	public PointLight(Point location, Color color) { 
		super(color);
		this.location = location;
	}

	public Point getLocation() {
		return location;
	}

}
