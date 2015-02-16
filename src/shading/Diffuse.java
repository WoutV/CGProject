package shading;

import java.awt.Color;
import java.util.List;

import light.PointLight;
import math.Point;
import math.Ray;
import math.Vector;

public class Diffuse {
	private double kd;
	private double ka;
	private Color cr;

	public Diffuse(double kd, double ka, Color color) {
		this.kd = kd;
		this.ka = ka;
		this.cr = color;
	}

	public Color getColor(Ray ray, List<PointLight> lights, Point p, Vector normal) {
		Color color = Color.BLACK;
		for (PointLight pl : lights) {
			Vector direction = pl.getLocation().toVector3D()
					.subtract(p.toVector3D());
			System.out.println(direction + "TOTHELIGHT");
			System.out.println(normal);
			double cos = normal.dot(direction)
					/ (normal.length() * direction.length());
			double viewingCos = normal.dot(ray.direction)
					/ (normal.length() * ray.direction.length());
			System.out.println(cos + "COS");
			if (cos < 0 & viewingCos > 0) {
				cos = 0;
			}
			int r = (int) (cr.getRed() * kd * cos / Math.PI + cr.getRed() * ka);
			int g = (int) (cr.getGreen() * kd * cos / Math.PI + cr.getGreen()* ka);
			int b = (int) (cr.getBlue() * kd * cos / Math.PI + cr.getBlue()* ka);
			r = trim(r);
			g = trim(g); 
			b = trim(b);
			color = addColor(color, new Color(r,g,b));
		}
		return color;
	}

	private int trim(int number) {
		if (number > 255)
			return 255;
		if (number < 0)
			return 0;
		else
			return number;
	}
	
	private Color addColor(Color color, Color color2) {
		return new Color(trim(color.getRed() + color2.getRed()), trim(color.getGreen()+ color2.getGreen()), trim(color.getBlue() + color2.getBlue()));
	}

}
