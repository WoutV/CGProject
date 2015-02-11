package light;

import java.awt.Color;

import math.Point;

public class PointLight {

	private Point location;
	private Color color;
	
	public PointLight(Point location, Color color) { 
		this.location = location;
		this.color = color;
	}

	public Point getLocation() {
		return location;
	}

	public Color getColor() {
		return color;
	}

}
