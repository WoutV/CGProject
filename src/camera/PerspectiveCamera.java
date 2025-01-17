package camera;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import math.OrthonormalBasis;
import math.Point;
import math.Ray;
import math.Vector;
import sampling.Sample;

/**
 * Implementation of a perspective {@link Camera}.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class PerspectiveCamera implements Camera {
	private final int xResolution;
	private final int yResolution;
	private final Point origin;
	private final OrthonormalBasis basis;
	
	private Random r = new Random();

	private final double width;
	private final double height;

	/**
	 * Creates a new {@link PerspectiveCamera} for an image with the given
	 * resolution, at the given position, looking into the given direction with
	 * the given up vector as the up direction. The field of view parameter
	 * specifies the horizontal field of view in degrees.
	 * 
	 * @param xResolution
	 *            x resolution of the image this camera is for.
	 * @param yResolution
	 *            y resolution of the image this camera is for.
	 * @param origin
	 *            origin of the camera.
	 * @param lookat
	 *            direction of the camera.
	 * @param up
	 *            up vector.
	 * @param fov
	 *            horizontal field of view (in degrees).
	 * @throws NullPointerException
	 *             when the origin, look at or up vector is null.
	 * @throws IllegalArgumentException
	 *             when the given horizontal or vertical resolution is smaller
	 *             than one.
	 * @throws IllegalArgumentException
	 *             when the field of view is smaller than or equal to zero.
	 * @throws IllegalArgumentException
	 *             when the field of view is larger than or equal to pi (180
	 *             degrees).
	 */
	public PerspectiveCamera(int xResolution, int yResolution, Point origin,
			Vector lookat, Vector up, double fov) throws NullPointerException,
			IllegalArgumentException {
		if (xResolution < 1)
			throw new IllegalArgumentException("the horizontal resolution "
					+ "cannot be smaller than one!");
		if (yResolution < 1)
			throw new IllegalArgumentException("the vertical resolution "
					+ "cannot be smaller than one!");
		if (fov <= 0)
			throw new IllegalArgumentException("the field of view cannot be "
					+ "smaller than or equal to zero degrees!");
		if (fov >= 180)
			throw new IllegalArgumentException("the field of view cannot be "
					+ "larger than or equal to 180 degrees!");

		this.xResolution = xResolution;
		this.yResolution = yResolution;
		this.origin = origin;
		this.basis = new OrthonormalBasis(lookat, up);

		width = 2.0 * Math.tan(0.5 * Math.toRadians(fov));
		height = ((double) yResolution * width) / (double) xResolution;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see camera.Camera#generateRay(sampling.Sample)
	 */
	public Ray generateRay(Sample sample) throws NullPointerException {
		double u = width * (sample.x / (double) xResolution - 0.5);
		double v = height * (sample.y / (double) yResolution - 0.5);

		Vector direction = basis.w.add(basis.u.scale(u).add(basis.v.scale(v)));

		return new Ray(origin, direction);
	}
	
	
	/**
	 * Creates a list of samples, size of the list is equal to amount. The samples are jittered on a uniform grid in pixel (x,y)
	 */
	public List<Sample> generateSamples(double x, double y, int amount) {
        if(amount!=1) {
            int root = (int) Math.sqrt(amount);
            double interval = 1.0 / root;
            List<Sample> result = new ArrayList<Sample>();
//            System.out.println("new pixel");
            for (int i = 0; i < root; i++) {
                for (int j = 0; j < root; j++) {
                    float randx = r.nextFloat();
                    float randy = r.nextFloat();
                    result.add(new Sample(x + (i + randx) * interval, y + (j + randy) * interval));
//                    System.out.println(x + (i + randx) * interval);
//                    System.out.println(y + (j + randy) * interval);
                }
            }
            return result;
        }
        else {
            List<Sample> result = new ArrayList<Sample>();
            result.add(new Sample(x+0.5,y+0.5));
            return result;
        }
	}
}
