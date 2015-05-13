package main;

import gui.ImagePanel;
import gui.ProgressReporter;
import gui.RenderFrame;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import light.Light;
import light.PointLight;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import sampling.Sample;
import shading.Diffuse;
import shading.ExtendedColor;
import shading.Material;
import shading.Phong;
import shading.TextureShading;
import shape.Cylinder;
import shape.Intersectable;
import shape.Intersection;
import shape.Sphere;
import shape.TriangleMesh;
import camera.PerspectiveCamera;

/**
 * Entry point of your renderer.
 * 
 * @author Niels Billen
 * @version 1.0
 */
public class Renderer {

	private static final int AA_AMOUNT = 1;
	private static final int SHADOW_AMOUNT = 1;
	public static int MAX;
	
	private static HashMap<String,TriangleMesh> objects = new HashMap<String,TriangleMesh>();

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

//		SceneCreator scene = SceneCreator.box();
//		 createShowImage( width, height, "bunny.obj", "textures/dots.jpg");
//		createImage(scene, width, height);
		testHeuristic(width, height);
	}

	private static void createImage(SceneCreator scene, int width, int height) {
		System.err.println("STARTED RENDERING + BOXES CREATING");
		// initialize the camera
		

		List<Intersectable> shapes = scene.getShapes("bvh","geometric","");
		System.out.println("rendering");
		PerspectiveCamera camera = new PerspectiveCamera(width, height,new Point(0, 0, -8), new Vector(0, 0, 1), new Vector(0, 1, 0),	60);

		// initialize the graphical user interface
		ImagePanel panel = new ImagePanel(width, height);
//		 ImagePanel panel = new ImagePanel(width*4, height*4);
		RenderFrame frame = new RenderFrame("Sphere", panel);

		// initialize the progress reporter
		ProgressReporter reporter = new ProgressReporter("Rendering", 40, width* height, false);
		reporter.addProgressListener(frame);
//		renderFalseColor(scene, width, height, camera, panel, reporter, shapes);
		renderTrueColor(scene, width, height, camera, panel, reporter, shapes);

		// save the output
		try {
			ImageIO.write(panel.getImage(), "png", new File("output.png"));
		} catch (IOException e) {
		}
	}
	
	private static void testHeuristic(int width, int height) {
		PerspectiveCamera camera = new PerspectiveCamera(width, height,new Point(0,0, -8), new Vector(0, 0, 1), new Vector(0, 1, 0),	60);
		ImagePanel panel = new ImagePanel(width, height);
//		generateMultipleScenes(width, height, camera, panel, "bvh",  "geometric","", "randomballs");
//		generateMultipleScenes(width, height, camera, panel, "bvh", "sorted","", "randomballs");
//		generateMultipleScenes(width, height, camera, panel, "grid", "","", "randomballs");

//		generateMultipleScenes(width, height, camera, panel, "bvh",  "geometric","", "cornerballs");
//		generateMultipleScenes(width, height, camera, panel, "bvh", "sorted", "","cornerballs");
//		generateMultipleScenes(width, height, camera, panel, "grid", "","", "cornerballs");
		
		generateMultipleScenes(width, height, camera, panel, "bvh",  "geometric", "mid", "teapots");
		generateMultipleScenes(width, height, camera, panel, "bvh",  "geometric", "min", "teapots");
		generateMultipleScenes(width, height, camera, panel, "bvh", "sorted", "mid", "teapots");
		generateMultipleScenes(width, height, camera, panel, "bvh", "sorted", "min", "teapots");
//		generateMultipleScenes(width, height, camera, panel, "grid", null, "teapots");
		// save the output
		
	}

	private static void generateMultipleScenes(int width,
			int height, PerspectiveCamera camera, ImagePanel panel, String method, String sort, String metric, String sceneType) {
		
		RenderFrame frame = new RenderFrame("Sphere", panel);
		File file = new File("results.txt");
        BufferedWriter output;
		try {
			output = new BufferedWriter(new FileWriter(file,true));
			output.write(sceneType);
			output.newLine();
			output.write(method);
			output.newLine();
			if(sort!=null){
				output.write(sort);
				output.newLine();
			}
			if(metric!=null){
				output.write(metric);
				output.newLine();
			}
			output.write("#########");
			output.newLine();
            output.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		for(int i = 0;i<100;i++) {
			SceneCreator scene = null;
			switch (sceneType) {
			case "randomballs":
				scene = SceneCreator.randomBalls();
				break;
			case "cornerballs":
				scene = SceneCreator.randomBallsCorner2();
				break;
			case "teapots":
				scene = SceneCreator.teapots();
				break;
			default:
				break;
			}
			List<Intersectable> shapes = scene.getShapes(method,sort, metric);
			ProgressReporter reporter = new ProgressReporter("Rendering", 40, width* height, false);
			reporter.addProgressListener(frame);
//			renderFalseColor(scene, width, height, camera, panel, reporter, shapes);
			renderTrueColor(scene, width, height, camera, panel, reporter, shapes);
	
			String time = reporter.time;
			
			File out = new File("results.txt");
            BufferedWriter outp;
			try {
				outp = new BufferedWriter(new FileWriter(out,true));
				outp.write(time);
				outp.newLine();
	            outp.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				ImageIO.write(panel.getImage(), "png", new File("output.png"));
			} catch (IOException e) {
			}
		}
	}

	private static void renderTrueColor(SceneCreator scene, int width, int height, PerspectiveCamera camera, ImagePanel panel,ProgressReporter reporter, List<Intersectable> shapes) {
		// render the scene
		List<Light> lights = scene.getLights();
		ExtendedColor total = new ExtendedColor(0,0,0);
//		int max = Integer.MIN_VALUE;
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				ExtendedColor color = new ExtendedColor(0, 0, 0);
				List<Sample> samples = camera.generateSamples(x, y, AA_AMOUNT);
				for(Sample s : samples ) {
					Ray ray = camera.generateRay(s);
					Intersection hitIntersection = getClosestIntersection(ray,shapes);
					color = shade(shapes, lights, hitIntersection);
					total.addColor(color);
				}
				total.divide(AA_AMOUNT);
				Color finalColor = color.toColor();
				panel.set(x, y, 255, finalColor.getRed(), finalColor.getGreen(),finalColor.getBlue());
//				for (int i = 0; i < 4; i++) {
//					for (int j = 0; j < 4; j++) {
//						panel.set(4 * x + i, 4 * y + j, 255, finalColor.getRed(),
//								finalColor.getGreen(), finalColor.getBlue());
//					}
//				}
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

	private static void renderFalseColor(SceneCreator scene, int width, int height, PerspectiveCamera camera, ImagePanel panel,
			ProgressReporter reporter, List<Intersectable> shapes) {
		// render the scene
		List<Light> lights = scene.getLights();
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// create a ray through the center of the pixel.
				Ray ray = camera.generateRay(new Sample(x + 0.5, y + 0.5));
				getClosestIntersection(ray, shapes);
				if (ray.intersectionCount > MAX) {
					MAX = ray.intersectionCount;
				}
			}
			reporter.update(height);
		}
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// create a ray through the center of the pixel.
				Ray ray = camera.generateRay(new Sample(x + 0.5, y + 0.5));
				Color color = new Color(0, 0, 0);
				getClosestIntersection(ray,shapes);
				color = getFalsecolorBW(ray);
				panel.set(x, y, 255, color.getRed(), color.getGreen(),color.getBlue());
			}
			reporter.update(height);
		}
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				// create a ray through the center of the pixel.
				Ray ray = camera.generateRay(new Sample(x + 0.5, y + 0.5));
				Color color = new Color(0, 0, 0);
				Intersection hitIntersection = getClosestIntersection(ray, shapes);
				color = getFalsecolor(ray);
				panel.set(x, y, 255, color.getRed(), color.getGreen(),color.getBlue());
			}
			reporter.update(height);
		}
		reporter.done();
	}

	private static ExtendedColor shade(List<Intersectable> shapes, List<Light> lights, Intersection hitIntersection) {
		ExtendedColor color = new ExtendedColor(0,0,0);
		if (hitIntersection != null) {
			color = getShading(shapes, lights, hitIntersection);
		}
		return color;
	}

	private static Color getFalsecolor(Ray ray) {
		Color color = Color.BLACK;
		if (ray.intersectionCount != 1) {
			if (ray.intersectionCount < MAX / 3) {
				double factor = ray.intersectionCount / (MAX / 3.0);
				color = new Color(0, (int) (255 * factor),
						(int) (255 - 255 * factor));
			} else if (ray.intersectionCount < 2 * MAX / 3.0) {
				double factor = (ray.intersectionCount - (MAX / 3.0))
						/ (MAX / 3.0);
				color = new Color((int) (255 * factor), 255, 0);
			} else {
				double factor = (ray.intersectionCount - (MAX * 2 / 3.0))/ (MAX / 3.0);
				color = new Color(255, (int) (255 - 255 * factor), 0);
			}
		}
		return color;
	}
	
	private static Color getFalsecolorBW(Ray ray) {
		Color color = Color.BLACK;
		color = new Color (255*ray.intersectionCount/MAX,255*ray.intersectionCount/MAX,255*ray.intersectionCount/MAX);
		return color;
	}

	

	private static Material createTexture(String fileName, Material shadingModel) {
		File file = new File(fileName);
		BufferedImage bi = null;
		try {
			bi = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Material texture = new TextureShading(Color.WHITE, 0.0, Color.WHITE,
				bi, shadingModel);
		return texture;
	}

	

	/**
	 * Calculates the intersection that is closest to the ray origin, and in
	 * positive direction.
	 * 
	 * @param ray
	 * @param shapes
	 * @return
	 */
	private static Intersection getClosestIntersection(Ray ray,
			List<Intersectable> shapes) {
		Double min = Double.MAX_VALUE;
		Intersection hitIntersection = null;
		for (Intersectable shape : shapes) {
			Intersection intersection = shape.intersect(ray);
			if (intersection != null) {
				Double t = intersection.getT();
				if (t + 1 > 0.000001 & t > 0.00001) {
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
	 * 
	 * @param shapes
	 * @param lights
	 * @param color
	 * @param hitIntersection
	 * @return
	 */
	private static ExtendedColor getShading(List<Intersectable> shapes,List<Light> lights, Intersection hitIntersection) {
		Point hitPoint = hitIntersection.getPoint();
		ExtendedColor total = new ExtendedColor(0,0,0);
		for (Light pl : lights) {
			ExtendedColor color = new ExtendedColor(0, 0, 0);
			List<Point> samples = pl.getSamples(SHADOW_AMOUNT);
			for(Point s : samples ) {
				Vector toTheLight = s.subtract(hitPoint);
				Double distanceToLight = toTheLight.length();
				Ray shadowRay = new Ray(hitPoint.add(toTheLight.scale(0.000001)), toTheLight.normalize());
				boolean inShadow = inShadow(shapes, s, distanceToLight, shadowRay, hitPoint);
				if (!inShadow) {
//					Vector n = hitIntersection.getNormal();
//					color = color.addColor(color,hitIntersection.getColor(pl));
					color = color.addColor(hitIntersection.getColor(pl.getColor(),s));
	//				color = color.addColor(new ExtendedColor((int)Math.abs(n.x*255), (int)Math.abs(n.y*255), (int)Math.abs(n.z*255)));
				} 
			}
			color = color.divide(SHADOW_AMOUNT);
			total = total.addColor(color);
		}
//		color.divide(SHADOW_AMOUNT);
//		System.out.println(hitIntersection.getConstantColor().r+"CONST");
		total = total.addColor(hitIntersection.getConstantColor());
//		color = addColor(color,hitIntersection.getConstantColor());
//		return new ExtendedColor(color.getRed(),color.getGreen(),color.getBlue());
		return total;
	}
	
	private static Color addColor(Color color, ExtendedColor color2) {
		return new Color(trim(color.getRed()+color2.r), trim(color.getGreen()+color2.g),trim(color.getBlue()+color2.b));
	}
	
	private static int trim(int number) {
		if (number > 255)
			return 255;
		if (number < 0)
			return 0;
		else
			return number;
	}

	/**
	 * @param shapes
	 * @param pl
	 * @param distanceToLight
	 * @param shadowRay
	 * @param hitPoint
	 * @return true if and only if there is a shape closer to the light than the
	 *         given distance, on the shadow ray
	 */
	private static boolean inShadow(List<Intersectable> shapes, Point p, Double distanceToLight, Ray shadowRay, Point hitPoint) {
		for (Intersectable shape : shapes) {
			Intersection intersection = shape.intersect(shadowRay);
			if (intersection != null) {
				Double t = intersection.getT();
				Point hit = intersection.getPoint();
				if (hit != null) {
					Double distanceToPoint = hit.subtract(hitPoint).length();
//					Vector toLight = pl.getLocation().subtract(hit);
					Vector toLight = p.subtract(hit);
					Double distanceToLight2 = toLight.length();
					if (Math.abs(t + 1) > 0.00001 & Math.abs(t) > 0.00001 & distanceToPoint < distanceToLight & distanceToLight2 < distanceToLight) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
