package shading;

import java.awt.Color;

import light.PointLight;
import math.Point;
import math.Ray;
import math.Vector;

public class Diffuse {
	private double kd;
	private double ka;
	private Color cr;
	
	public Diffuse (double kd, double ka, Color color) {
		this.kd = kd;
		this.ka = ka;
		this.cr = color;
	}
	
	public Color getColor(Ray ray, PointLight light, Point p, Vector normal) {
		Vector direction = light.getLocation().toVector3D().subtract(p.toVector3D());
		System.out.println(direction+"TOTHELIGHT");
		System.out.println(normal);
		double cos = normal.dot(direction)/(normal.length()*direction.length());
		System.out.println(cos+"COS");
		if(cos < 0) {cos = 0;}
		int r = (int) (cr.getRed()*kd*cos/Math.PI + cr.getRed()*ka);
		int g = (int) (cr.getGreen()*kd*cos/Math.PI+ cr.getGreen()*ka);
		int b = (int) (cr.getBlue()*kd*cos/Math.PI+ cr.getBlue()*ka);
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
