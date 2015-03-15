package light;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import math.Point;
import sampling.Sample;

public class PointLight extends Light {

	private Point location;
	public PointLight(Point location, Color color) { 
		super(color);
		this.location = location;
	}

	public Point getLocation() {
		return location;
	}

	@Override
	public List<Point> getSamples(int amount) {
		List<Point> result = new ArrayList<Point>();
		for(int i = 0;i<amount;i++) {
			result.add(location);
		}
		return result;
	}

}
