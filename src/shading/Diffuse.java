package shading;

import java.awt.Color;
import java.util.List;

import light.PointLight;
import math.Point;
import math.Ray;
import math.Vector;
import shape.Shape;

public class Diffuse extends Material {
	private double kd;
	private double ka;
	private Color cr;
	private static final double EPSILON = 0.00000001;

	public Diffuse(double kd, double ka, Color color) {
		this.kd = kd;
		this.ka = ka;
		this.cr = color;
	}

	
	/**
	 * @param ray
	 * @param lights
	 * @param shapes
	 * @param p
	 * @param normal
	 * @param hitShape
	 * @return the color of the given point, depending on the lights and shapes in the scene, and the surface normal of the shape to be shaded
	 */
	@Override
	public Color getColor(Ray ray, List<PointLight> lights, Point p, Vector normal) {
		Color color = Color.BLACK;
		for (PointLight pl : lights) {
			Vector direction = pl.getLocation().toVector3D().subtract(p.toVector3D());
			color = getShading(ray, normal, color, direction, pl.getColor());
		}
		return addColor(color,new Color((int)(cr.getRed()*ka),(int)(cr.getGreen()*ka),(int)(cr.getBlue()*ka)));
	}

	private Color getShading(Ray ray, Vector normal, Color color, Vector toTheLight, Color lightColor) {
		double cos = normal.dot(toTheLight) / (normal.length() * toTheLight.length());
		double viewingCos = normal.dot(ray.direction) / (normal.length() * ray.direction.length());
		if (cos < 0 & viewingCos > 0) {
			cos = 0;
		}
		int r = (int) (cr.getRed() * kd * cos / Math.PI * lightColor.getRed()/255);
		int g = (int) (cr.getGreen() * kd * cos / Math.PI* lightColor.getGreen()/255);
		int b = (int) (cr.getBlue() * kd * cos / Math.PI* lightColor.getBlue()/255);
		r = trim(r);
		g = trim(g);
		b = trim(b);
		color = addColor(color, new Color(r, g, b));
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
		return new Color(trim(color.getRed() + color2.getRed()),
				trim(color.getGreen() + color2.getGreen()),
				trim(color.getBlue() + color2.getBlue()));
	}


//	@Override
//	public Color getColor(List<PointLight> lights) {
//		// TODO Auto-generated method stub
//		return null;
//	}

}
