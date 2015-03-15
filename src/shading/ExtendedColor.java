package shading;

import java.awt.Color;

public class ExtendedColor {
	public int r;
	public int g;
	public int b;
	
	public ExtendedColor(int r, int g, int b) {
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public ExtendedColor addColor(ExtendedColor color) {
		return new ExtendedColor(r+color.r, g+color.g,b+color.b);
	}
	
	public ExtendedColor divide(double factor) {
		return new ExtendedColor((int)(r/factor),(int)(g/factor),(int)(b/factor));
	}
	
	public Color toColor() {
		return new Color(trim(r),trim(g),trim(b));
	}
	
	private static int trim(int number) {
		if (number > 255)
			return 255;
		if (number < 0)
			return 0;
		else
			return number;
	}
}
