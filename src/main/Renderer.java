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

import light.AreaLight;
import light.Light;
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

	private static final int AA_AMOUNT = 4;
	private static final int SHADOW_AMOUNT = 4;
	public static int MAX;

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

		SceneCreator scene = box();
		// createShowImage( width, height, "bunny.obj", "textures/dots.jpg");
		createImage(scene, width, height);
	}

//	private static void createShowImage(int width, int height, String obj, String texture) {
//		// initialize the camera
//		PerspectiveCamera camera = new PerspectiveCamera(width, height,
//				new Point(0, 0, 0), new Vector(0, 0, 1), new Vector(0, 1, 0),
//				60);
//
//		// initialize the graphical user interface
//		ImagePanel panel = new ImagePanel(width, height);
//		// ImagePanel panel = new ImagePanel(width*4, height*4);
//		RenderFrame frame = new RenderFrame("Sphere", panel);
//
//		// initialize the progress reporter
//		ProgressReporter reporter = new ProgressReporter("Rendering", 40, width
//				* height, false);
//		reporter.addProgressListener(frame);
//
//		SceneCreator scene = new SceneCreator();
//
//		Diffuse redDiffuse = new Diffuse(0.9, 0.0, Color.RED, Color.WHITE);
//		Material p2 = new Phong(Color.white, 0.0, 20.0, 0.8, redDiffuse,
//				Color.WHITE);
//		Material textureMaterial = createTexture(texture, p2);
//		// addComplexObject(scene, textureMaterial,
//		// Transformation.createTranslation(0,0,10) , obj);
//		addComplexObject(
//				scene,
//				textureMaterial,
//				Transformation.createTranslation(0, 0, 10).append(
//						Transformation.createRotationY(180)), obj);
//
//		scene.add(new PointLight(new Point(-1, 1, 0), Color.WHITE));
//
//		// render the scene
//		List<Intersectable> shapes = scene.getShapes();
//		List<PointLight> lights = scene.getLights();
//		int max = Integer.MIN_VALUE;
//		for (int x = 0; x < width; ++x) {
//			for (int y = 0; y < height; ++y) {
//				// create a ray through the center of the pixel.
//				Ray ray = camera.generateRay(new Sample(x + 0.5, y + 0.5));
//				Color color = new Color(0, 0, 0);
//				Intersection hitIntersection = getClosestIntersection(ray,
//						shapes);
//				color = shade(shapes, lights, hitIntersection);
//				panel.set(x, y, 255, color.getRed(), color.getGreen(),
//						color.getBlue());
//			}
//			reporter.update(height);
//		}
//		reporter.done();
//		System.out.println(max);
//
//		// save the output
//		try {
//			ImageIO.write(panel.getImage(), "png", new File("output.png"));
//		} catch (IOException e) {
//		}
//	}

	private static void createImage(SceneCreator scene, int width, int height) {
		System.err.println("STARTED RENDERING + BOXES CREATING");
		// initialize the camera
		PerspectiveCamera camera = new PerspectiveCamera(width, height,new Point(0, 0, -8), new Vector(0, 0, 1), new Vector(0, 1, 0),	60);

		// initialize the graphical user interface
		ImagePanel panel = new ImagePanel(width, height);
//		 ImagePanel panel = new ImagePanel(width*4, height*4);
		RenderFrame frame = new RenderFrame("Sphere", panel);

		// initialize the progress reporter
		ProgressReporter reporter = new ProgressReporter("Rendering", 40, width* height, false);
		reporter.addProgressListener(frame);

		List<Intersectable> shapes = scene.getShapes();
//		renderFalseColor(scene, width, height, camera, panel, reporter, shapes);
		renderTrueColor(scene, width, height, camera, panel, reporter, shapes);

		// save the output
		try {
			ImageIO.write(panel.getImage(), "png", new File("output.png"));
		} catch (IOException e) {
		}
	}

	private static void renderTrueColor(SceneCreator scene, int width, int height, PerspectiveCamera camera, ImagePanel panel,ProgressReporter reporter, List<Intersectable> shapes) {
		// render the scene
		List<Light> lights = scene.getLights();
		ExtendedColor total = new ExtendedColor(0,0,0);
		int max = Integer.MIN_VALUE;
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
		System.out.println(max);
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
				Intersection hitIntersection = getClosestIntersection(ray,
						shapes);
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

	private static SceneCreator teapot() {
		SceneCreator scene = new SceneCreator();

		Diffuse redDiffuse = new Diffuse(0.9, 0.0, Color.GREEN, Color.WHITE);

		Material red = new Phong(Color.WHITE, 0.0, 25.0, 0.8, redDiffuse,
				Color.WHITE);
		// scene.add(new Sphere(id, 4, texture));
		addComplexObject(scene, red, Transformation.createTranslation(0.5,-0,-9).append(Transformation.createRotationY(90)), "dragon.obj");

		scene.add(new PointLight(new Point(0, 0, -10000), Color.WHITE));
		return scene;
	}

	private static SceneCreator box() {
		SceneCreator scene = new SceneCreator();
		Transformation id = Transformation.createTranslation(0, 0, 10);
		Transformation toTheLeft = Transformation.createTranslation(6, -7, 10);
		Diffuse redDiffuse = new Diffuse(0.9, 0.0, Color.RED, Color.WHITE);
		Material p2 = new Phong(Color.white, 0.0, 20.0, 0.8, redDiffuse,
				Color.WHITE);
//		Material texture = createTexture("textures/dots.jpg", p2);
		Diffuse yellowDiffuse = new Diffuse(0.9, 0.3, Color.yellow, Color.yellow);
		Material whiteDiffuse = new Diffuse(0.9, 0.1, new Color(188, 195, 200),
				Color.WHITE);
		Material red = new Phong(Color.WHITE, 0.0, 25.0, 0.8, redDiffuse,
				Color.WHITE);
		addComplexObject(
				scene,
				redDiffuse,
				id.append(Transformation.createScale(2,2,2)).append(
						Transformation.createRotationY(0)), "dragon.obj");
//		scene.add(new Sphere(toTheLeft, 4, yellowDiffuse));
//		scene.add(new Cylinder(toTheLeft, yellowDiffuse, 5, 2));
		scene.add(new Plane(new Vector(0, 1, 0), whiteDiffuse, new Point(),
				Transformation.createTranslation(0, -4, 0)));
		scene.add(new Plane(new Vector(1, 0, 0), redDiffuse, new Point(),
				Transformation.createTranslation(-12, 0, 0)));
		scene.add(new Plane(new Vector(0, 0, -1), whiteDiffuse, new Point(),
				Transformation.createTranslation(0, 0, 12)));
//		addComplexObject(
//				scene,
//				yellowDiffuse,
//				Transformation.createTranslation(4, -2, 2).append(
//						Transformation.createScale(4, 4, 4).append(
//								Transformation.createRotationY(90))),
//				"bunny.obj");
//
//		//
//		scene.add(new PointLight(new Point(5, 5, 5), Color.WHITE));
		scene.add(new AreaLight(Color.white	, new Point(5,5,5), new Point(5,4,5), new Point(6,5,5)));
//		scene.add(new PointLight(new Point(10, 0, 5), Color.WHITE));
//		scene.add(new PointLight(new Point(0, 0, -10000), Color.WHITE));
		return scene;
	}

	/**
	 * Initialize the scene. Add other shapes and lights here.
	 * 
	 * @return
	 */
	private static SceneCreator rainbowDragons() {
		// materials

		SceneCreator scene = new SceneCreator();
		Diffuse redDiffuse = new Diffuse(0.9, 0.1, Color.RED, Color.WHITE);
		Diffuse yellowDiffuse = new Diffuse(0.9, 0.1, Color.yellow, Color.WHITE);
		Diffuse orange = new Diffuse(0.9, 0.1, new Color(255, 127, 0),
				Color.WHITE);
		Diffuse green = new Diffuse(0.9, 0.1, new Color(0, 255, 0), Color.WHITE);
		Diffuse blue = new Diffuse(0.9, 0.1, new Color(0, 0, 255), Color.WHITE);
		Diffuse indigo = new Diffuse(0.9, 0.1, new Color(75, 0, 130),
				Color.WHITE);
		Diffuse violet = new Diffuse(0.9, 0.1, new Color(143, 0, 255),
				Color.WHITE);
		Material red = new Phong(Color.WHITE, 0.0, 25.0, 0.8, redDiffuse,
				Color.WHITE);
		Material yellow = new Phong(Color.white, 0.0, 25.0, 0.8, yellowDiffuse,
				Color.WHITE);
		Material orangePhong = new Phong(Color.white, 0.0, 25.0, 0.8, orange,
				Color.WHITE);
		Material greenPhong = new Phong(Color.white, 0.0, 25.0, 0.8, green,
				Color.WHITE);
		Material bluePhong = new Phong(Color.white, 0.0, 25.0, 0.8, blue,
				Color.WHITE);
		Material indigoPhong = new Phong(Color.white, 0.0, 25.0, 0.8, indigo,
				Color.WHITE);

		Material violetPhong = new Phong(Color.white, 0.0, 25.0, 0.8, violet,
				Color.WHITE);

		// scene.add(new Sphere(id, 4, p1));
		// scene.add(new Cylinder(toTheLeft, yellowDiffuse, 5, 2));
		// scene.add(new Plane(new Vector(0,1,0), whiteDiffuse, new Point(),
		// Transformation.createTranslation(0, -4, 0)));
		// scene.add(new Plane(new Vector(1,0,0), redDiffuse, new Point(),
		// Transformation.createTranslation(-12, 0, 0)));
		// scene.add(new Plane(new Vector(0,0,-1), whiteDiffuse, new Point(),
		// Transformation.createTranslation(0, 0, 12)));

		//
		scene.add(new PointLight(new Point(5, 5, 0), Color.WHITE));
		scene.add(new PointLight(new Point(-10, 2, 5), Color.WHITE));
		scene.add(new PointLight(new Point(0, 0, -10000), Color.WHITE));
		scene.add(new PointLight(new Point(1, 0, -10000), Color.WHITE));
		scene.add(new PointLight(new Point(-1, 0, -10000), Color.WHITE));

		addComplexObject(scene, greenPhong,
				Transformation.createTranslation(0, 0, -7), "dragon.obj");
		addComplexObject(scene, yellow,
				Transformation.createTranslation(-1, 0, -7), "dragon.obj");
		addComplexObject(scene, bluePhong,
				Transformation.createTranslation(1, 0, -7), "dragon.obj");
		addComplexObject(scene, red,
				Transformation.createTranslation(-0.5, 1, -7), "dragon.obj");
		addComplexObject(scene, orangePhong,
				Transformation.createTranslation(0.5, 1, -7), "dragon.obj");
		addComplexObject(scene, indigoPhong,
				Transformation.createTranslation(-0.5, -1, -7), "dragon.obj");
		addComplexObject(scene, violetPhong,
				Transformation.createTranslation(0.5, -1, -7), "dragon.obj");
		return scene;
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

	private static void addComplexObject(SceneCreator scene, Material shading,Transformation transformation, String fileName) {
        ObjParser parser = new ObjParser("/home/wout/Documents/IDeaprojects/CGProject/bunny.obj");

//		ObjParser parser = new ObjParser("G:/School/CGProject/"+fileName);
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
					Vector n = hitIntersection.getNormal();
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
