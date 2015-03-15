package shading;

import java.awt.Color;

import light.PointLight;
import math.Coordinate2D;
import math.Point;
import math.Ray;
import math.Vector;

public class Phong extends Material {
	private Diffuse diffuse;
	private Double exponent;
	private Double ks;

	public Phong(Color color, Double ka, Double e,Double ks, Diffuse diffuse, Color ambientColor) {
		super(color, ka, ambientColor);
		this.diffuse = diffuse;
		this.exponent = e;
		this.ks = ks;
	}

	@Override
	public ExtendedColor getColor(Ray ray, PointLight pl, Point p, Vector normal, Coordinate2D texture) {
		Vector direction = pl.getLocation().toVector3D().subtract(p.toVector3D());
		ExtendedColor diff = diffuse.getShading(ray, normal, direction, pl.getColor());
		ExtendedColor phong = getShading(ray, normal, direction, pl.getColor());
//		return phong;
		return phong.addColor(diff);
	}

	@Override
	public ExtendedColor getShading(Ray ray, Vector normal, Vector toTheLight, Color lightColor) {
		Vector unitNormal = normal.scale(1/normal.length());
		Vector reflect = toTheLight.subtract(unitNormal.scale(2*(toTheLight.dot(normal))));
		double cos = ray.direction.dot(reflect) / (ray.direction.length() * reflect.length());
		if (cos < 0 ) {
			cos = 0;
		}
		int r = (int) (color.getRed() * ks * Math.pow(cos,exponent) * lightColor.getRed()/255);
		int g = (int) (color.getGreen() * ks * Math.pow(cos,exponent)* lightColor.getGreen()/255);
		int b = (int) (color.getBlue() * ks * Math.pow(cos,exponent) * lightColor.getBlue()/255);
//		r = trim(r);
//		g =trim(g);
//		b = trim(b);
		return new ExtendedColor(r,g,b);
	}
	
	@Override
	public void setColor(Color color) {
		this.color = color;
		this.diffuse.setColor(color);
	}
}
