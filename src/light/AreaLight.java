package light;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import math.Point;
import math.Vector;
import sampling.Sample;

public class AreaLight extends Light {

	private Random r = new Random();
	private Point p1;
	private Point p2;
	private Point p3;
	
	//TODO : arealight needs more then 2 points. :) 

	public AreaLight(Color color, Point p1, Point p2, Point p3) {
		super(color);
		this.p1 = p1;
		this.p2 = p2;
		this.p3 = p3;
	}

	@Override
	public List<Point> getSamples(int amount) {
		int root = (int) Math.sqrt(amount);
		double interval = 1.0/root;
		Vector x = p3.subtract(p2);
		Vector y = p1.subtract(p2);
		List<Point> result = new ArrayList<Point>();
		for(int i = 0; i < root; i++) {
			for(int j = 0; j < root; j++) {
				float randx = r.nextFloat();
				float randy = r.nextFloat();
//				result.add(new Sample(x+(i +randx)*interval,y+(j+randy)*interval));
				result.add(p2.add(x.scale((i+randx)*interval)).add(y.scale((j+randy)*interval)));
			}
		}
		return result;
	}

}
