package main;

import gui.ImagePanel;
import gui.ProgressReporter;
import gui.RenderFrame;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

import light.PointLight;
import math.*;
import sampling.Sample;
import shading.*;
import shape.*;
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
		int width = 400;
		int height = 400;

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
		
		
		SceneCreator scene = createScene();
		createImage(scene, width, height);
	}

	private static void createImage(SceneCreator scene, int width, int height) {
		// initialize the camera
		PerspectiveCamera camera = new PerspectiveCamera(width, height,new Point(0,0,-10), new Vector(0, 0, 1), new Vector(0, 1, 0), 60);

		// initialize the graphical user interface
		ImagePanel panel = new ImagePanel(width, height);
//		ImagePanel panel = new ImagePanel(width*4, height*4);
		RenderFrame frame = new RenderFrame("Sphere", panel);

		// initialize the progress reporter
		ProgressReporter reporter = new ProgressReporter("Rendering", 40, width* height, false);
		reporter.addProgressListener(frame);

		// render the scene
		List<Intersectable> shapes = scene.getShapes();
		List<PointLight> lights = scene.getLights();
		int max = Integer.MIN_VALUE;
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// create a ray through the center of the pixel.
				Ray ray = camera.generateRay(new Sample(x + 0.5, y + 0.5));
				Color color = new Color(0, 0, 0);
				Intersection hitIntersection = getClosestIntersection(ray, shapes);
				// for shading
				if (hitIntersection!=null) {
					color = getShading(shapes, lights, hitIntersection);
				}
//				 for false color
//				if(ray.intersectionCount != 1) {
//					if(ray.intersectionCount > max) { max = ray.intersectionCount;}
//					color = new Color(0,0,trim(255*ray.intersectionCount/1055));
//				}
				//for bigger pixels
//				for(int i = 0; i < 4; i++) {
//					for(int j = 0; j<4; j++) {
//						panel.set(4*x+i, 4*y+j, 255, color.getRed(), color.getGreen(), color.getBlue());
//					}
//				}
//				for pixel sized pixels
				panel.set(x, y, 255, color.getRed(), color.getGreen(), color.getBlue());
			}
			reporter.update(height);
		}
		reporter.done();
		System.out.println(max);

		// save the output
		try {
			ImageIO.write(panel.getImage(), "png", new File("output.png"));
		} catch (IOException e) {
		}
	}

	/**
	 * Initialize the scene. Add other shapes and lights here.
	 * @return
	 */
	private static SceneCreator createScene() {
		// materials
		
		SceneCreator scene = new SceneCreator();
		Diffuse redDiffuse = new Diffuse(0.6, 0.1, Color.RED, Color.WHITE);
		Diffuse magentaDiffuse = new Diffuse(0.9, 0.1, Color.MAGENTA, Color.WHITE);
		Diffuse  yellowDiffuse = new Diffuse(0.9, 0.1, Color.yellow, Color.WHITE);
		Material whiteDiffuse = new Diffuse(0.9, 0.1, new Color(200,200,200), Color.WHITE);
		Material mutableDiffuse = new Diffuse(0.9, 0.1, new Color(200,200,200), Color.WHITE);
		Material p1 = new Phong(Color.WHITE, 0.0, 25.0,0.8, redDiffuse, Color.WHITE);
		Material p2 = new Phong(Color.white, 0.2, 25.0, 0.8, magentaDiffuse, Color.MAGENTA);
		
		Material texture = createTexture("apple/apple_texture.jpg", p2);

		
		Transformation id = Transformation.createTranslation(0, 0, 10);;
		Transformation toTheLeft = Transformation.createTranslation(-6, -4, 10);
		Transformation toTheRight = Transformation.createTranslation(4, 0, 20);
		
		scene.add(new Sphere(id, 4, p1));
		scene.add(new Cylinder(toTheLeft, yellowDiffuse, 5,  2));
		scene.add(new Plane(new Vector(0,1,0), whiteDiffuse, new Point(), Transformation.createTranslation(0, -4, 0)));
		scene.add(new Plane(new Vector(1,0,0), redDiffuse, new Point(), Transformation.createTranslation(-12, 0, 0)));
		scene.add(new Plane(new Vector(0,0,-1), whiteDiffuse, new Point(), Transformation.createTranslation(0, 0, 12)));

//		
		scene.add(new PointLight(new Point(5,5,0), Color.WHITE));
		scene.add(new PointLight(new Point(-10,2, 5), Color.WHITE));
		scene.add(new PointLight(new Point(0,0,-10000),Color.WHITE));
		
		
		
		
		addComplexObject(scene, texture, Transformation.createScale(3,3,3),  "apple/apple.obj");
//		addComplexObject(scene, p2, Transformation.createTranslation(-1,0,-7),  "dragon.obj");
//		addComplexObject(scene, p2, Transformation.createTranslation(1,0,-7),  "dragon.obj");
		return scene;
	}

	private static Material createTexture(String fileName, Material shadingModel) {
		File file= new File(fileName);
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Material texture = new TextureShading(Color.WHITE, 0.0, Color.WHITE, bi, shadingModel);
		return texture;
	}

	private static void addComplexObject(SceneCreator scene, Material shading, Transformation transformation,  String fileName) {
		ObjParser parser = new ObjParser(fileName);
		TriangleMesh object = null;
		try {
			object = parser.parseObjFile();
			object.setTransformation(transformation);
			object.setShading(shading);
			scene.add(object);
		} catch (FileNotFoundException e1) {
			System.err.println("File not found!");
		} catch (IOException e1) {
			System.err.println("Error in processing .obj file!");
		}
	}
	
	/**
	 * Calculates the intersection that is closest to the ray origin, and in positive direction.
	 * @param ray
	 * @param shapes
	 * @return
	 */
	private static Intersection getClosestIntersection(Ray ray, List<Intersectable> shapes) {
		Double min = Double.MAX_VALUE;
		Intersection hitIntersection = null;
		for (Intersectable shape : shapes) {
			Intersection intersection = shape.intersect(ray);
			if(intersection !=null) {
				Double t = intersection.getT();
				if (t+1 > 0.000001 & t > 0.00001) {
					if (t < min) {
						min = t;
						hitIntersection = intersection;
					}
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
	private static Color getShading(List<Intersectable> shapes,List<PointLight> lights,Intersection hitIntersection) {
		Point hitPoint = hitIntersection.getPoint();
		Color color = Color.BLACK;
		for(PointLight pl : lights) {
			Vector toTheLight = pl.getLocation().subtract(hitPoint);
			Double distanceToLight = toTheLight.length();
			Ray shadowRay = new Ray(hitPoint.add(toTheLight.scale(0.000001)),toTheLight.normalize());
			boolean inShadow = inShadow(shapes, pl, distanceToLight, shadowRay, hitPoint);
			if(!inShadow) {
				color = addColor(color, hitIntersection.getColor(pl));
			}
		}
		color = addColor(color,hitIntersection.getConstantColor());
		return color;
	}

	/**
	 * @param shapes
	 * @param pl
	 * @param distanceToLight
	 * @param shadowRay
	 * @param hitPoint 
	 * @return true if and only if there is a shape closer to the light than the given distance, on the shadow ray
	 */
	private static boolean inShadow(List<Intersectable> shapes, PointLight pl, Double distanceToLight, Ray shadowRay, Point hitPoint) {
		for (Intersectable shape : shapes) {
			Intersection intersection = shape.intersect(shadowRay);
			if(intersection!=null){
			Double t = intersection.getT();
			Point hit = intersection.getPoint();
			if (hit != null) {
				Double distanceToPoint = hit.subtract(hitPoint).length();
				Vector toLight = pl.getLocation().subtract(hit);
				Double distanceToLight2 = toLight.length();
				if (Math.abs(t + 1) > 0.00001 & Math.abs(t) > 0.000001 & distanceToPoint < distanceToLight & distanceToLight2 < distanceToLight) {
					return true;
				}
			}
			}
		}
		return false;
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
