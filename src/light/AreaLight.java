package light;

import java.awt.Color;
import java.util.List;

import math.Point;

public class AreaLight extends Light {

	private Point min;
	private Point max;

	public AreaLight(Color color, Point min, Point max) {
		super(color);
		this.min = min;
		this.max = max;
	}
	
	public List<Point> getSamples() {
		return new ArrayList<Point>();
	}

}
