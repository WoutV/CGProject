package shape;

import java.awt.Color;

import light.PointLight;
import math.Coordinate2D;
import math.Point;
import math.Ray;
import math.Vector;
import shading.Material;

public class Intersection {
	private Point point;
	private Ray ray;
	private Material material;
	private Vector normal;
	private Double t;
	private Coordinate2D uv;
	
	public Intersection(Point point, Ray ray, Material material, Vector normal, Double t, Coordinate2D uv) {
		this.point = point;
		this.ray = ray;
		this.material = material;
		this.normal = normal;
		this.t = t;
		this.uv = uv;
	}
	
	public Point getPoint() {
		return point;
	}
	public void setPoint(Point point) {
		this.point = point;
	}
	public Ray getRay() {
		return ray;
	}
	public void setRay(Ray ray) {
		this.ray = ray;
	}
	public Material getMaterial() {
		return material;
	}
	public void setMaterial(Material material) {
		this.material = material;
	}
	public Vector getNormal() {
		return normal;
	}
	public void setNormal(Vector normal) {
		this.normal = normal;
	}
	public Double getT() {
		return t;
	}
	public void setT(Double t) {
		this.t = t;
	}
	
	public Color getColor(PointLight pl) {
		return material.getColor(ray, pl, point	, normal, uv);
	}

	public Color getConstantColor() {
		return material.getAmbientColor();
	}
}
