package light;

import java.awt.Color;
import java.util.List;

import math.Point;

public abstract class Light {

	protected Color color;

	public Light(Color color) {
		this.color = color;
	}

	public Color getColor() {
		return color;
	}
	
	public abstract List<Point> getSamples(int amount);

}