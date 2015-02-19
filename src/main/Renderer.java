package main;

import gui.ImagePanel;
import gui.ProgressReporter;
import gui.RenderFrame;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import light.PointLight;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import sampling.Sample;
import shading.Diffuse;
import shape.Cylinder;
import shape.Intersection;
import shape.Plane;
import shape.Shape;
import shape.Sphere;
import shape.Triangle;
import camera.PerspectiveCamera;

/**
 * Entry point of your renderer.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class Renderer {
	/**
	 * Entry point of your renderer.
	 * 
	 * @param arguments
	 *            command line arguments.
	 */
	public static void main(String[] arguments) {
		int width = 1000;
		int height = 1000;

		// parse the command line arguments
		for (int i = 0; i < arguments.length; ++i) {
			if (arguments[i].startsWith("-")) {
				try {
					if (arguments[i].equals("-width"))
						width = Integer.parseInt(arguments[++i]);
					else if (arguments[i].equals("-height"))
						height = Integer.parseInt(arguments[++i]);
					else if (arguments[i].equals("-help")) {
						System.out.println("usage: "
								+ "[-width  width of the image] "
								+ "[-height  height of the image]");
						return;
					} else {
						System.err.format("unknown flag \"%s\" encountered!\n",
								arguments[i]);
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					System.err.format("could not find a value for "
							+ "flag \"%s\"\n!", arguments[i]);
				}
			} else
				System.err.format("unknown value \"%s\" encountered! "
						+ "This will be skipped!\n", arguments[i]);
		}

		// validate the input
		if (width <= 0)
			throw new IllegalArgumentException("the given width cannot be "
					+ "smaller than or equal to zero!");
		if (height <= 0)
			throw new IllegalArgumentException("the given height cannot be "
					+ "smaller than or equal to zero!");

		// initialize the camera
		PerspectiveCamera camera = new PerspectiveCamera(width, height,
				new Point(), new Vector(0, 0, 1), new Vector(0, 1, 0), 90);

		// initialize the graphical user interface
		ImagePanel panel = new ImagePanel(width, height);
		RenderFrame frame = new RenderFrame("Sphere", panel);

		// initialize the progress reporter
		ProgressReporter reporter = new ProgressReporter("Rendering", 40, width
				* height, false);
		reporter.addProgressListener(frame);

		// initialize the scene
		Diffuse d1 = new Diffuse(0.9, 0.0, new Color(255, 0, 0));
		Diffuse d2 = new Diffuse(0.9, 0.0, Color.BLUE);
		Diffuse d3 = new Diffuse(0.9, 0.5, Color.white);
		Transformation id = Transformation.IDENTITY;
		Transformation t1 = Transformation.createTranslation(0, -1, 1);
		Transformation tc = Transformation.createTranslation(0, 0, 10).append(
				Transformation.createRotationX(-45));
		Transformation ts = Transformation.createTranslation(0.0, 0.0, 10);
		Transformation tt = Transformation.createTranslation(0.0, -3.0, 9);
		Transformation t2 = Transformation.createTranslation(0, -4, 10);
		Transformation t3 = Transformation.createTranslation(-4, -4, 3);
		Transformation t4 = Transformation.createTranslation(4, 4, 12);
		Transformation t5 = Transformation.createTranslation(-4, 4, 8);
		Transformation t6 = Transformation.createTranslation(5, -5, 12);
		List<Shape> shapes = new ArrayList<Shape>();
		List<PointLight> lights = new ArrayList<PointLight>();
		PointLight light = new PointLight(new Point(20.0, 1.0, 4.0), Color.CYAN);
		PointLight light2 = new PointLight(new Point(-5.0, 1.0, 4.0), Color.MAGENTA);
		shapes.add(new Sphere(tt, 2, d1));
//		 shapes.add(new Sphere(tt, 3,d2));
//		 shapes.add(new Sphere(t2, 3, d2));
//		 shapes.add(new Sphere(t4, 4, d2));
		// shapes.add(new Sphere(t5, 4));
		shapes.add(new Plane(new Vector(0.0, 1.0, 0.0), d3, new Point(0.0,-5.0,0.0),id));
		shapes.add(new Triangle(ts, new Point(0.0,0.0,0.0), new Point(0.0, 1.0, 0.0), new Point(1.0, 0.0, 0.0), d1));
		shapes.add(new Cylinder(t6, d2, 3, 1));
		lights.add(light);
		lights.add(light2);

		// render the scene
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// create a ray through the center of the pixel.
				Ray ray = camera.generateRay(new Sample(x + 0.5, y + 0.5));
				Color color = new Color(0, 0, 0);
				boolean hit = false;
				Intersection hitIntersection = null;
				Double min = Double.MAX_VALUE;
				for (Shape shape : shapes) {
					Intersection intersection = shape.intersect(ray);
					Double t = intersection.getT();
					if (t+1 > 0.000001) {
						hit = true;
						if (t < min) {
							min = t;
							hitIntersection = intersection;
						}
					}
				}
				if (hit) {
//					Point hitPoint = ray.origin.add(ray.direction.scale(min));
					color = hitIntersection.getColor(lights);
					panel.set(x, y, 255, color.getRed(), color.getGreen(), color.getBlue());
				} else {
					System.err.println("NOTHIGN WAS HIT");
					panel.set(x, y, 255, 0, 0, 0);
				}
			}
			reporter.update(height);
		}
		reporter.done();

		// save the output
		try {
			ImageIO.write(panel.getImage(), "png", new File("output.png"));
		} catch (IOException e) {
		}
	}

}
