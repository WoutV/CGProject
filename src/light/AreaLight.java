package light;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import math.Point;
import sampling.Sample;

public class AreaLight extends Light {

	private Random r = new Random();
	private Point min;
	private Point max;

	public AreaLight(Color color, Point min, Point max) {
		super(color);
		this.min = min;
		this.max = max;
	}

	@Override
	public List<Point> getSamples(int amount) {
		int root = (int) Math.sqrt(amount);
		List<Point> result = new ArrayList<Point>();
		for(int i = 0; i < root; i++) {
			for(int j = 0; j < root; j++) {
				float randx = r.nextFloat();
				float randy = r.nextFloat();
				float randz = r.nextFloat();
				result.add(new Point(min.x));
				result.add(new Sample(x+(i +randx)*interval,y+(j+randy)*interval));
			}
		}
		return result;
	}

}
