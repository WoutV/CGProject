package main;

import gui.ImagePanel;
import gui.ProgressReporter;
import gui.RenderFrame;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import light.PointLight;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import sampling.Sample;
import shading.Diffuse;
import shading.Material;
import shading.Phong;
import shape.Cone;
import shape.Cylinder;
import shape.Intersection;
import shape.Plane;
import shape.Shape;
import shape.Sphere;
import shape.Triangle;
import shape.TriangleMesh;
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
		int width = 500;
		int height = 500;

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
		SceneCreator scene = new SceneCreator();
		Diffuse redDiffuse = new Diffuse(1.0, 0.0, Color.RED);
		Material d2 = new Diffuse(0.9, 0.2, Color.BLUE);
		Material d3 = new Diffuse(1, 0.0, Color.white);
		Material p1 = new Phong(Color.WHITE, 0.0, 25.0,0.8, redDiffuse);
		
		Transformation id = Transformation.IDENTITY;
		Transformation t1 = Transformation.createTranslation(0, -1, 1);
		Transformation tc = Transformation.createTranslation(0, 0, 10).append(
				Transformation.createRotationX(-45));
		Transformation ts = Transformation.createTranslation(0.0, -1.0, 5);
		Transformation tt = Transformation.createTranslation(0.0, -3.0, 9);
		Transformation t2 = Transformation.createTranslation(0, -1, 15);
		Transformation t3 = Transformation.createTranslation(-4, -4, 3);
		Transformation t4 = Transformation.createTranslation(4, 4, 12);
		Transformation t5 = Transformation.createTranslation(-4, 4, 8);
		Transformation t6 = Transformation.createTranslation(5.5, -5, 12);
		PointLight light = new PointLight(new Point(0.0, 1.0, 35.0), Color.WHITE);
		PointLight light2 = new PointLight(new Point(-5.0, 1.0, 4.0), Color.white);
		PointLight light3 = new PointLight(new Point(5.0, 1.0, 4.0), Color.white);

//		scene.add(new Sphere(tt, 2, p1));
//		 shapes.add(new Sphere(tt, 3,d2));
//		 shapes.add(new Sphere(t2, 3, d2));
//		 shapes.add(new Sphere(t4, 4, d2));
		// shapes.add(new Sphere(t5, 4));
//		scene.add(new Plane(new Vector(0.0, 1.0, 0.0), d3, new Point(0.0,-5.0,0.0),id));
//		scene.add(new Triangle(ts, new Point(1.0,0.0,1.0), new Point(-1.0, 0.0, -1.0), new Point(1.0, 0.0, -1.0), d1));
//		scene.add(new Cylinder(t6, p1, 3, 1));
//		scene.add(new Cone(3, 1, t3, p1));
		Triangle tr = new Triangle(new Point(), new Point(2.0,0.0,0.0), new Point(0.0,2.0,0.0), new Vector(1,0,0), new Vector(0,1,0), new Vector(0,0,1));
		tr.setShading(p1);
		tr.setTransformation(ts);
//		scene.add(tr);
//		scene.add(light);
		scene.add(light2);
		scene.add(light3);
		
		ObjParser parser = new ObjParser("bunny.obj");
		TriangleMesh mesh = null;
		try {
			mesh = parser.parseObjFile();
			mesh.setTransformation(t2.append(Transformation.createRotationY(45)));
			mesh.setShading(redDiffuse);
			scene.add(mesh);
		} catch (FileNotFoundException e1) {
			System.err.println("File not found!");
		} catch (IOException e1) {
			System.err.println("Error in processing .obj file!");
		}

		// render the scene
		List<Shape> shapes = scene.getShapes();
		List<PointLight> lights = scene.getLights();
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// create a ray through the center of the pixel.
				Ray ray = camera.generateRay(new Sample(x + 0.5, y + 0.5));
				Color color = new Color(0, 0, 0);
				Intersection hitIntersection = getClosestIntersection(ray, shapes);
				if (hitIntersection!=null) {
					color = getShading(shapes, lights, hitIntersection);
				}
				panel.set(x, y, 255, color.getRed(), color.getGreen(), color.getBlue());
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
	
	/**
	 * Calculates the intersection that is closest to the ray origin, and in positive direction.
	 * @param ray
	 * @param shapes
	 * @return
	 */
	private static Intersection getClosestIntersection(Ray ray, List<Shape> shapes) {
		Double min = Double.MAX_VALUE;
		Intersection hitIntersection = null;
		for (Shape shape : shapes) {
			Intersection intersection = shape.intersect(ray);
			Double t = intersection.getT();
			if (t+1 > 0.000001 & t > 0.00001) {
				if (t < min) {
					min = t;
					hitIntersection = intersection;
				}
			}
		}
		return hitIntersection;
	}

	/**
	 * Calculates the shading in an Intersection, based on certain parameters.
	 * @param shapes
	 * @param lights
	 * @param color
	 * @param hitIntersection
	 * @return
	 */
	private static Color getShading(List<Shape> shapes,List<PointLight> lights,Intersection hitIntersection) {
		Point hitPoint = hitIntersection.getPoint();
		Color color = Color.BLACK;
		for(PointLight pl : lights) {
			boolean inShadow = false;
			Vector toTheLight = pl.getLocation().toVector3D().subtract(hitPoint.toVector3D());
			Double distanceToLight = toTheLight.length();
			Ray shadowRay = new Ray(hitPoint.add(toTheLight.scale(0.00001)),toTheLight);
			for (Shape shape : shapes) {
				Intersection intersection = shape.intersect(shadowRay);
				Double t = intersection.getT();
				if (Math.abs(t+1)>0.01 & t > 0.00001 & t < distanceToLight ) {
					inShadow = true;
					break;
				}
			}
			if(!inShadow) {
				color = addColor(color, hitIntersection.getColor(pl));
			}
		}
		color = addColor(color,hitIntersection.getConstantColor());
		return color;
	}

	private static  int trim(int number) {
		if (number > 255)
			return 255;
		if (number < 0)
			return 0;
		else
			return number;
	}

	private static Color addColor(Color color, Color color2) {
		return new Color(trim(color.getRed() + color2.getRed()),
				trim(color.getGreen() + color2.getGreen()),
				trim(color.getBlue() + color2.getBlue()));
	}

}
