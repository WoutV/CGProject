package main;

import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import light.Light;
import light.PointLight;
import math.Point;
import math.Transformation;
import shading.Diffuse;
import shading.Material;
import shading.Phong;
import shape.BoundingBox;
import shape.Intersectable;
import shape.Plane;
import shape.Shape;
import shape.Sphere;
import shape.TriangleMesh;

public class SceneCreator {
	private List<Light> lights = new ArrayList<Light>();
	private List<Intersectable> shapes = new ArrayList<Intersectable>();
	
	private static Random rand = new Random();
	
	private Map<String, TriangleMesh> objects = new HashMap<String, TriangleMesh>();
	
	
	public void add(Shape shape) {
		this.shapes.add(shape);
	}
	
	public List<Intersectable> getShapes(String method, String split, String metric, String whichaxis) {
//		return this.shapes;
		switch (method) {
		case "grid":
			return createRegularGrid();
		case "bvh":
			return createBVH(split, metric, whichaxis);
		default:
			return createBVH("sorted", "mid", "longest");
		}
	}
	
	private List<Intersectable> createBVH(String method, String metric, String whichaxis) {
		ArrayList<Intersectable> planes = new ArrayList<Intersectable>();
		ArrayList<Intersectable> nonplanes = new ArrayList<Intersectable>();
		if(method.equals(null)) {method = "geometric";}
		System.out.println(shapes.size());
		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;
		double minz = Double.MAX_VALUE;
		double maxx = Double.NEGATIVE_INFINITY;
		double maxy = Double.NEGATIVE_INFINITY;
		double maxz = Double.NEGATIVE_INFINITY;
		List<Intersectable> toReturn =  new ArrayList<Intersectable>();
		for(Intersectable t : shapes) {
			if(!t.getClass().equals(Plane.class)) {
				nonplanes.add(t);
				double[] minb =t.getMinCoordinates();
				double [] maxb = t.getMaxCoordinates();
				if(minb[0] < minx) { minx = minb[0];}
				if(minb[1] < miny) { miny = minb[1];}
				if(minb[2] < minz) { minz = minb[2];}
				if(maxb[0] > maxx) { maxx = maxb[0];}
				if(maxb[1] > maxy) { maxy = maxb[1];}
				if(maxb[2] > maxz) { maxz = maxb[2];}
			} else {
				planes.add(t);
			}
		}
		double[] min = {minx,miny,minz};
		double[] max = {maxx,maxy,maxz};
		BoundingBox box = new BoundingBox(min, max);
		for(Intersectable t : nonplanes) {
			box.add(t);
		}
		int firstaxis = 0;
		if(whichaxis.equals("longest")){
			double maxDiff = -1.0;
			for(int i = 0;i<3;i++) {
				if(max[i]-min[i]>maxDiff) {firstaxis = i;maxDiff = (max[i]-min[i]);}
			}
		}
		switch (method) {
		case "geometric":
			box.split(firstaxis, metric, whichaxis);
//			box.split(0);
			break;
		case "sorted":
			box.splitSorted(firstaxis, metric, whichaxis);
//			box.splitSorted(0);
			break;
		case "sah":
			box.splitSAH(firstaxis, metric, whichaxis);
			break;
		default:
			break;
		}
		toReturn.add(box);
		toReturn.addAll(planes);
		return toReturn;
	}
	
	private List<Intersectable> createRegularGrid() {	
		ArrayList<Intersectable> planes = new ArrayList<Intersectable>();
		ArrayList<Intersectable> nonplanes = new ArrayList<Intersectable>();
		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;
		double minz = Double.MAX_VALUE;
		double maxx = Double.NEGATIVE_INFINITY;
		double maxy = Double.NEGATIVE_INFINITY;
		double maxz = Double.NEGATIVE_INFINITY;
		for(Intersectable t : shapes) {
			if(!t.getClass().equals(Plane.class)) {
				nonplanes.add(t);
				double[] minb =t.getMinCoordinates();
				double [] maxb = t.getMaxCoordinates();
				if(minb[0] < minx) { minx = minb[0];}
				if(minb[1] < miny) { miny = minb[1];}
				if(minb[2] < minz) { minz = minb[2];}
				if(maxb[0] > maxx) { maxx = maxb[0];}
				if(maxb[1] > maxy) { maxy = maxb[1];}
				if(maxb[2] > maxz) { maxz = maxb[2];}
			} else {
				planes.add(t);
			}
		}
		double[] min = {minx,miny,minz};
		double[] max = {maxx,maxy,maxz};
		
		
		List<Intersectable> allBoxes = getAllShapes(nonplanes);
		int n =  (int) Math.pow(allBoxes.size(), 1.0/3);
//		int n = 15;
		System.out.println("size of grid: "+n);
		double xStep = Math.abs((maxx-minx)/n);
		double yStep = Math.abs((maxy-miny)/n);
		double zStep = Math.abs((maxz-minz)/n);
		System.out.println(xStep);
		System.out.println(yStep);
		System.out.println(zStep);
//		
		System.out.println("creating cellzzzzz");
		System.out.println("Amount of boxes to be checked " + allBoxes.size());
		Intersectable[][][] cells = new Intersectable[n][n][n];
		for(int i = 0;i<n;i++) {
			for(int j = 0;j<n;j++) {
				for(int k = 0;k<n;k++) {
					double[] minBoundary = {min[0]+i*xStep,min[1]+j*yStep,min[2]+k*zStep};
					double[] maxBoundary = {minBoundary[0]+xStep,minBoundary[1]+yStep,minBoundary[2]+zStep};
					BoundingBox cell = new BoundingBox(minBoundary, maxBoundary);
					checkCell(cell, allBoxes);
					cells[i][j][k] = cell;
				}
			}
		}
		List<Intersectable> result = new ArrayList<Intersectable>();
		Intersectable grid = new RegularGrid(cells,xStep,yStep,zStep,n);
		BoundingBox bb = new BoundingBox(min,max);
		bb.add(grid);
		result.add(bb);
		result.addAll(planes);
		System.out.println("grid created G");
		return result;
	}

	private List<Intersectable> getAllShapes(ArrayList<Intersectable> nonplanes) {
		List<Intersectable> allBoxes = new ArrayList<Intersectable>();
		List<Intersectable> allShapes = new ArrayList<Intersectable>();
		for(Intersectable in : nonplanes) {
			allShapes.addAll(in.getAll());
		}
//		for(Intersectable shape : allShapes) {
//			allBoxes.add(shape.getBoundingBox(null));
//		}
		return allShapes;
	}

	private void checkCell(BoundingBox cell, List<Intersectable> boxes) {
		for(Intersectable bb : boxes) {
			if(bb.overlap(cell)) {
				cell.add(bb.getAll());
//				System.out.println(bb.getAll().get(0).getClass());
			}
		}
//		if(cell.getAll().size() > 0)
//			System.out.println("amount added: " + cell.getAll().size());
	}

	public void add(Light light) {
		this.lights.add(light);
	}
	
	public List<Light> getLights() {
		return this.lights;
	}
	
	private static SceneCreator teapot() {
		SceneCreator scene = new SceneCreator();

		Diffuse redDiffuse = new Diffuse(0.9, 0.0, Color.GREEN, Color.WHITE);

		Material red = new Phong(Color.WHITE, 0.0, 25.0, 0.8, redDiffuse,
				Color.WHITE);
		// scene.add(new Sphere(id, 4, texture));
		addComplexObject(scene, red, Transformation.createTranslation(0,0,0).append(Transformation.createRotationY(90)), "teapot.obj");

		scene.add(new PointLight(new Point(0, 0, -10000), Color.WHITE));
		return scene;
	}

	public static SceneCreator box() {
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
//		addComplexObject(
//				scene,
//				redDiffuse,
//				id.append(Transformation.createScale(1,1,1)).append(
//						Transformation.createTranslation(0,0,0)), "bunny.obj");
//		scene.add(new Sphere(toTheLeft, 4, redDiffuse));
//		scene.add(new Sphere(Transformation.createTranslation(-6,7, 10), 4, redDiffuse));
//		scene.add(new Cylinder(toTheLeft, redDiffuse, 5, 2));
//		scene.add(new Plane(new Vector(0, 1, 0), yellowDiffuse, new Point(),
//				Transformation.createTranslation(0, -4, 0)));
//		scene.add(new Plane(new Vector(1, 0, 0), redDiffuse, new Point(),
//				Transformation.createTranslation(-12, 0, 0)));
//		scene.add(new Plane(new Vector(0, 0, -1), whiteDiffuse, new Point(),
//				Transformation.createTranslation(0, 0, 12)));
		addComplexObject(
				scene,
				yellowDiffuse,
				Transformation.createTranslation(0, -2,0).append(
								Transformation.createRotationY(90)),
				"bunny.obj");

//		//
//		scene.add(new PointLight(new Point(5, 5, 5), Color.WHITE));
//		scene.add(new AreaLight(Color.white	, new Point(5,5,5), new Point(5,2,5), new Point(7,2,5)));
//		scene.add(new PointLight(new Point(10, 0, 5), Color.WHITE));
		scene.add(new PointLight(new Point(0, 0, -10000), Color.WHITE));
		return scene;
	}
	
	public static SceneCreator randomBalls() {
		SceneCreator scene = new SceneCreator();
		for(int i = 0;i<5000;i++) {
			float x = rand.nextFloat();
			float y = rand.nextFloat();
			float z = rand.nextFloat();
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
			Diffuse color = new Diffuse(0.9, 0.0, new Color(r,g,b), Color.WHITE);
			Transformation trans = Transformation.createTranslation(-2.5+x*5,-2.5+y*5,z);
			scene.add(new Sphere(trans, 0.1, color));
		}
//		Diffuse red = new Diffuse(0.9, 0.0, Color.RED, Color.white);
//		addComplexObject(scene, red, Transformation.createTranslation(0,0,0).append(Transformation.createRotationY(90)), "bunny.obj");
		scene.add(new PointLight(new Point(0, 0, -10000), Color.WHITE));
		return scene;
	}
	
	public static SceneCreator teapots(int nb) {
		SceneCreator scene = new SceneCreator();
		for(int i = 0;i<nb;i++) {
			float x = rand.nextFloat();
			float y = rand.nextFloat();
			float z = rand.nextFloat();
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
			Diffuse color = new Diffuse(0.9, 0.0, new Color(r,g,b), Color.WHITE);
			addComplexObject(scene, color, Transformation.createTranslation(-2.5+x*5,-2.5+y*5,z).append(Transformation.createScale(0.1,0.1,0.1)), "bunny.obj");
		}		
		scene.add(new PointLight(new Point(0, 0, -10000), Color.WHITE));
		return scene;
	}
	
	static SceneCreator randomBallsCorner() {
		SceneCreator scene = new SceneCreator();
		for(int i = 0;i<5000;i++) {
			float x = rand.nextFloat();
			float y = rand.nextFloat();
			float z = rand.nextFloat();
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
			System.out.println(255*r);
			Diffuse color = new Diffuse(0.9, 0.0, new Color(r,g,b), Color.WHITE);
			Transformation trans = Transformation.createTranslation(x+5,y+5,z);
			scene.add(new Sphere(trans, 0.1, color));
		}
		scene.add(new PointLight(new Point(0, 0, -10000), Color.WHITE));
		return scene;
	}
	
	public static SceneCreator randomBallsCorner2() {
		SceneCreator scene = new SceneCreator();
		for(int i = 0;i<1250;i++) {
			float x = rand.nextFloat();
			float y = rand.nextFloat();
			float z = rand.nextFloat();
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
//			System.out.println(255*r);
			Diffuse color = new Diffuse(0.9, 0.0, new Color(r,g,b), Color.WHITE);
			Transformation trans = Transformation.createTranslation(x+1.5,y+1.5,z);
			scene.add(new Sphere(trans, 0.1, color));
		}
		for(int i = 0;i<1250;i++) {
			float x = rand.nextFloat();
			float y = rand.nextFloat();
			float z = rand.nextFloat();
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
			System.out.println(255*r);
			Diffuse color = new Diffuse(0.9, 0.0, new Color(r,g,b), Color.WHITE);
			Transformation trans = Transformation.createTranslation(-2.5+x,-2.5+y,z);
			scene.add(new Sphere(trans, 0.1, color));
		}
		for(int i = 0;i<1250;i++) {
			float x = rand.nextFloat();
			float y = rand.nextFloat();
			float z = rand.nextFloat();
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
			System.out.println(255*r);
			Diffuse color = new Diffuse(0.9, 0.0, new Color(r,g,b), Color.WHITE);
			Transformation trans = Transformation.createTranslation(1.5+x,-2.5+y,z);
			scene.add(new Sphere(trans, 0.1, color));
		}
		for(int i = 0;i<1250;i++) {
			float x = rand.nextFloat();
			float y = rand.nextFloat();
			float z = rand.nextFloat();
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
			System.out.println(255*r);
			Diffuse color = new Diffuse(0.9, 0.0, new Color(r,g,b), Color.WHITE);
			Transformation trans = Transformation.createTranslation(-2.5+x,1.5+y,z);
			scene.add(new Sphere(trans, 0.1, color));
		}
		scene.add(new PointLight(new Point(0, 0, -10000), Color.WHITE));
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
//		scene.add(new PointLight(new Point(5, 5, 0), Color.WHITE));
//		scene.add(new PointLight(new Point(-10, 2, 5), Color.WHITE));
		scene.add(new PointLight(new Point(0, 0, -10000), Color.WHITE));
//		scene.add(new PointLight(new Point(1, 0, -10000), Color.WHITE));
//		scene.add(new PointLight(new Point(-1, 0, -10000), Color.WHITE));

		addComplexObject(scene, greenPhong,
				Transformation.createTranslation(0, 0, 0), "dragon.obj");
//		addComplexObject(scene, yellow,
//				Transformation.createTranslation(-1, 0, -7), "dragon.obj");
//		addComplexObject(scene, bluePhong,
//				Transformation.createTranslation(1, 0, -7), "dragon.obj");
//		addComplexObject(scene, red,
//				Transformation.createTranslation(-0.5, 1, -7), "dragon.obj");
//		addComplexObject(scene, orangePhong,
//				Transformation.createTranslation(0.5, 1, -7), "dragon.obj");
//		addComplexObject(scene, indigoPhong,
//				Transformation.createTranslation(-0.5, -1, -7), "dragon.obj");
//		addComplexObject(scene, violetPhong,
//				Transformation.createTranslation(0.5, -1, -7), "dragon.obj");
		return scene;
	}
	
	private static void addComplexObject(SceneCreator scene, Material shading,Transformation transformation, String fileName) {
        
		ObjParser parser = new ObjParser(fileName);
//
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

	public static SceneCreator bunnies(int nb) {
		SceneCreator scene = new SceneCreator();
		for(int i = 0;i<nb;i++) {
			float x = rand.nextFloat();
			float y = rand.nextFloat();
			float z = rand.nextFloat();
			float r = rand.nextFloat();
			float g = rand.nextFloat();
			float b = rand.nextFloat();
			Diffuse color = new Diffuse(0.9, 0.0, new Color(r,g,b), Color.WHITE);
			addComplexObject(scene, color, Transformation.createTranslation(-2.5+x*5,-2.5+y*5,z).append(Transformation.createScale(0.1,0.1,0.1)), "teapot.obj");
		}		
		scene.add(new PointLight(new Point(0, 0, -10000), Color.WHITE));
		return scene;
	}
}
