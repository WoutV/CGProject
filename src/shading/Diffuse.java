package shading;

import java.awt.Color;

import light.PointLight;
import math.Point;
import math.Ray;
import math.Vector;

public class Diffuse extends Material {
	private double kd;
	private static final double EPSILON = 0.00000001;

	public Diffuse(double kd, double ka, Color color) {
		super(color,ka);
		this.kd = kd;
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
	public Color getColor(Ray ray, PointLight light, Point p, Vector normal) {
		Color color = Color.BLACK;
		Vector direction = light.getLocation().toVector3D().subtract(p.toVector3D());
		color = getShading(ray, normal, direction, light.getColor());
//		return addColor(color,new Color((int)(color.getRed()*ka),(int)(color.getGreen()*ka),(int)(color.getBlue()*ka)));
		return color;
	}
	
	@Override
	public Color getShading(Ray ray, Vector normal, Vector toTheLight, Color lightColor) {
		double cos = normal.dot(toTheLight) / (normal.length() * toTheLight.length());
		double viewingCos = normal.dot(ray.direction) / (normal.length() * ray.direction.length());
		if (cos < 0 & viewingCos > 0) {
			cos = 0;
		}
		int r = (int) (color.getRed() * kd * cos / Math.PI * lightColor.getRed()/255);
		int g = (int) (color.getGreen() * kd * cos / Math.PI* lightColor.getGreen()/255);
		int b = (int) (color.getBlue() * kd * cos / Math.PI* lightColor.getBlue()/255);
		r = trim(r);
		g = trim(g);
		b = trim(b);
		return new Color(r,g,b);
	}

}
