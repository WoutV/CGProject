package shading;

import java.awt.Color;

import light.PointLight;
import math.Point;
import math.Ray;
import math.Vector;

public class Phong extends Material {
	private Diffuse diffuse;
	private Double exponent;
	private Double ks;

	public Phong(Color color, Double ka, Double e,Double ks, Diffuse diffuse) {
		super(color, ka);
		this.diffuse = diffuse;
		this.exponent = e;
		this.ks = ks;
	}

	@Override
	public Color getColor(Ray ray, PointLight pl, Point p, Vector normal) {
		Vector direction = pl.getLocation().toVector3D().subtract(p.toVector3D());
		Color diff = diffuse.getShading(ray, normal, direction, pl.getColor());
		Color phong = getShading(ray, normal, direction, pl.getColor());
		return addColor(diff,phong);
//		return phong;
	}

	@Override
	public Color getShading(Ray ray, Vector normal, Vector toTheLight, Color lightColor) {
		Vector unitNormal = normal.scale(1/normal.length());
		Vector reflect = toTheLight.subtract(unitNormal.scale(2*(toTheLight.dot(normal))));
		double cos = ray.direction.dot(reflect) / (ray.direction.length() * reflect.length());
//		double cos = 30;
		double viewingCos = normal.dot(ray.direction) / (normal.length() * ray.direction.length());
//		if (cos < 0 & viewingCos > 0) {
//			cos = 0;
//		}
		int r = (int) (color.getRed() * ks * Math.pow(cos,exponent) * lightColor.getRed()/255);
		int g = (int) (color.getGreen() * ks * Math.pow(cos,exponent)* lightColor.getGreen()/255);
		int b = (int) (color.getBlue() * ks * Math.pow(cos,exponent) * lightColor.getBlue()/255);
		r = trim(r);
		g = trim(g);
		b = trim(b);
//		 = addColor(color, new Color(r, g, b));
		return new Color(r,g,b);
	}

}