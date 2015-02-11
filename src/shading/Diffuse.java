package shading;

import java.awt.Color;

import light.PointLight;
import math.Point;
import math.Ray;
import math.Vector;

public class Diffuse {
	private double kd;
	private Color cr;
	
	public Diffuse (double kd, Color color) {
		this.kd = kd;
		this.cr = color;
	}
	
	public Color getColor(Ray ray, PointLight light, Point p, Vector normal) {
		Vector direction = light.getLocation().toVector3D().subtract(normal);
		System.out.println(direction+"TOTHELIGHT");
		//Vector direction = p.subtract(light.getLocation());
		System.out.println(normal);
		double cos = normal.dot(direction)/(normal.length()*direction.length());
		System.out.println(cos+"COS");
		int r = (int) (cr.getRed()*kd*cos/Math.PI);
		int g = (int) (cr.getGreen()*kd*cos/Math.PI);
		int b = (int) (cr.getBlue()*kd*cos/Math.PI);
		return new Color(trim(r),trim(g),trim(b));
	}

	private int trim(int number) {
		if (number > 255)
			return 255;
		if(number < 0) 
			return 0;
		else return number;
	}

}
