package shading;

import java.awt.Color;

import light.Light;
import light.PointLight;
import math.Coordinate2D;
import math.Point;
import math.Ray;
import math.Vector;

public abstract class Material {
	protected Color color;
	protected Double ka;
	protected Color ambientColor;
	
	public Material(Color color, Double ka, Color ambientColor) {
		this.color = color;
		this.ka = ka;
		this.ambientColor = ambientColor;
	}

	public abstract ExtendedColor getColor(Ray ray, Color lightColor, Point lightSample, Point p, Vector normal, Coordinate2D textureCoordinate);

	public abstract ExtendedColor getShading(Ray ray, Vector normal, Vector direction, Color lightColor);

	public ExtendedColor getAmbientColor() {
		return new ExtendedColor((int)(ambientColor.getRed()*ka),(int)(ambientColor.getGreen()*ka),(int)(ambientColor.getBlue()*ka));
	}
	
//	protected Color addColor(Color color, Color color2) {
//		return new Color(trim(color.getRed() + color2.getRed()),
//				trim(color.getGreen() + color2.getGreen()),
//				trim(color.getBlue() + color2.getBlue()));
//	}
	
	protected void setColor(Color color) {
		this.color = color;
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