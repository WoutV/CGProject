package shading;

import java.awt.Color;
import java.util.List;

import shape.Shape;

import light.PointLight;
import math.Point;
import math.Ray;
import math.Vector;

public abstract class Material {

	public abstract Color getColor(Ray ray, PointLight pl, Point p, Vector normal);

	public abstract Color getAmbientColor();

}