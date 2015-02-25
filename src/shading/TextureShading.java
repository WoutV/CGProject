package shading;

import java.awt.Color;
import java.awt.image.BufferedImage;

import light.PointLight;
import math.Coordinate2D;
import math.Point;
import math.Ray;
import math.Vector;

public class TextureShading extends Material {
	
	private BufferedImage texture;

	public TextureShading(Color color, Double ka, Color ambientColor, BufferedImage bi) {
		super(color, ka, ambientColor);
		this.texture =  bi;
	}

	@Override
	public Color getShading(Ray ray, Vector normal, Vector direction, Color lightColor) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Color getAmbientColor() {
		return Color.BLACK;
	}

	@Override
	public Color getColor(Ray ray, PointLight pl, Point p, Vector normal,
			Coordinate2D textureCoordinate) {
		int width = texture.getWidth();
		int height = texture.getHeight();
		int x = (int) (textureCoordinate.x*width);
		int y = (int) (textureCoordinate.y*height);
		if(x == 0) x =1;
		if(y==0) y = 1;
		System.out.println("X"+x+"Y"+y);
		int clr=  texture.getRGB(x,y); 
		int  red   = (clr & 0x00ff0000) >> 16;
		int  green = (clr & 0x0000ff00) >> 8;
		int  blue  =  clr & 0x000000ff;
		
		return new Color(red,green,blue);
	}
}
