package shading;

import java.awt.Color;

import light.PointLight;
import math.Point;
import math.Ray;
import math.Vector;

public abstract class Material {
	protected Color color;
	protected Double ka;
	
	public Material(Color color, Double ka) {
		this.color = color;
		this.ka = ka;
	}

	public abstract Color getColor(Ray ray, PointLight pl, Point p, Vector normal);

	public abstract Color getShading(Ray ray, Vector normal, Vector direction, Color lightColor);

	public Color getAmbientColor() {
		return new Color((int)(color.getRed()*ka),(int)(color.getGreen()*ka),(int)(color.getBlue()*ka));
	}
	
	protected Color addColor(Color color, Color color2) {
		return new Color(trim(color.getRed() + color2.getRed()),
				trim(color.getGreen() + color2.getGreen()),
				trim(color.getBlue() + color2.getBlue()));
	}
	
	protected int trim(int number) {
		if (number > 255)
			return 255;
		if (number < 0)
			return 0;
		else
			return number;
	}
}