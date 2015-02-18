package shading;

import java.awt.Color;
import java.util.List;

import light.PointLight;
import math.Point;
import math.Ray;
import math.Vector;
import shape.Shape;

public class Diffuse {
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
	public Color getColor(Ray ray, List<PointLight> lights, List<Shape> shapes, Point p, Vector normal, Shape hitShape) {
		Color color = Color.BLACK;
		for (PointLight pl : lights) {
			Vector direction = pl.getLocation().toVector3D().subtract(p.toVector3D());
			Double distanceToLight = direction.length();
			direction = direction.scale(1/direction.length());
			boolean shaded = false;
			Shape shadowShape = null;
			p = p.add(direction.scale(EPSILON));
			Ray shadow = new Ray(p, direction);
			if (shapes.size() == 1) {
				color = getShading(ray, normal, color, direction);
			} else {
				Double min = distanceToLight;
				for (Shape other : shapes) {
					Double intersection = other.intersect(shadow);
					if (!intersection.equals(-1.0)) {
						if (intersection < min) {
							shaded = true;
							min = intersection;
							shadowShape = other;
						}
					}
				}
			}
			if(!shaded | hitShape.equals(shadowShape)) {
				color = getShading(ray, normal, color, direction);
			}
		}
		return color;
	}

	private Color getShading(Ray ray, Vector normal, Color color, Vector toTheLight) {
		double cos = normal.dot(toTheLight) / (normal.length() * toTheLight.length());
		double viewingCos = normal.dot(ray.direction) / (normal.length() * ray.direction.length());
		if (cos < 0 & viewingCos > 0) {
			cos = 0;
		}
		int r = (int) (cr.getRed() * kd * cos / Math.PI + cr.getRed()
				* ka);
		int g = (int) (cr.getGreen() * kd * cos / Math.PI + cr
				.getGreen() * ka);
		int b = (int) (cr.getBlue() * kd * cos / Math.PI + cr.getBlue()
				* ka);
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

}
