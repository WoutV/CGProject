package math;

import java.util.Locale;


public class Coordinate2D {
	public final double x;
	public final double y;

	public Coordinate2D(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Coordinate2D add(double x, double y) {
		return new Coordinate2D(this.x + x, this.y+y);
	}

	public Coordinate2D add(Coordinate2D coord) throws NullPointerException {
		return add(coord.x, coord.y);
	}

	public Coordinate2D scale(double scalar) {
		return new Coordinate2D(x * scalar, y * scalar);
	}

	public double[] toArray() {
		return new double[] { x, y};
	}
	
	@Override
	public String toString() {
		return String.format(Locale.ENGLISH, "[%s]:\n%g %g", getClass()
				.getName(), x, y);
	};
}
