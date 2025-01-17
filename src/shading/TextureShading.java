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
	private Material shadingModel;

	public TextureShading(Color color, Double ka, Color ambientColor, BufferedImage bi, Material shadingModel) {
		super(color, ka, ambientColor);
		this.texture =  bi;
		this.shadingModel = shadingModel;
	}

	@Override
	public ExtendedColor getShading(Ray ray, Vector normal, Vector direction, Color lightColor) {
		return null;
	}
	
	public ExtendedColor getAmbientColor() {
		return shadingModel.getAmbientColor();
	}

//	@Override
//	public ExtendedColor getColor(Ray ray, PointLight pl, Point p, Vector normal, Coordinate2D textureCoordinate) {
//		int width = texture.getWidth();
//		int height = texture.getHeight();
//		int x = (int) (textureCoordinate.x*width);
//		int y = (int) (textureCoordinate.y*height);
//		if(x == 0) x =1;
//		if(y == 0) y = 1;
//		int clr=  texture.getRGB(x,y); 
//		int  red   = (clr & 0x00ff0000) >> 16;
//		int  green = (clr & 0x0000ff00) >> 8;
//		int  blue  =  clr & 0x000000ff;
//		shadingModel.setColor(new Color(red,green,blue));
//		return shadingModel.getColor(ray, pl, p, normal, textureCoordinate);
////		return new Color(red,green,blue);
//	}

	@Override
	public ExtendedColor getColor(Ray ray, Color lightColor, Point lightSample,
			Point p, Vector normal, Coordinate2D textureCoordinate) {
		int width = texture.getWidth();
		int height = texture.getHeight();
		int x = (int) (textureCoordinate.x*width);
		int y = (int) (textureCoordinate.y*height);
		if(x == 0) x =1;
		if(y == 0) y = 1;
		int clr=  texture.getRGB(x,y); 
		int  red   = (clr & 0x00ff0000) >> 16;
		int  green = (clr & 0x0000ff00) >> 8;
		int  blue  =  clr & 0x000000ff;
		shadingModel.setColor(new Color(red,green,blue));
		return shadingModel.getColor(ray, lightColor, lightSample, p, normal, textureCoordinate);
	}
}
