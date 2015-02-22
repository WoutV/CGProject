package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import light.PointLight;
import math.Point;
import math.Vector;
import shape.Shape;

public class SceneCreator {
	
	private final static String VERTEX = "v";
	private final static String TEXTURE_COORD = "vt";
    private final static String VERTEX_NORMAL = "vn";
    private final static String TRIANGLE = "f";
    
    File objFile = null;
	private List<PointLight> lights = new ArrayList<PointLight>();
	private List<Shape> shapes = new ArrayList<Shape>();
	private List<Point> vertices = new ArrayList<Point>();
	private List<Vector> normals = new ArrayList<Vector>();
	
	public void add(Shape shape) {
		this.shapes.add(shape);
	}
	
	public List<Shape> getShapes() {
		return this.shapes;
	}
	
	public void add(PointLight light) {
		this.lights.add(light);
	}
	
	public List<PointLight> getLights() {
		return this.lights;
	}
	
	private void parseObjFile(String objFilename) throws FileNotFoundException, IOException {
        int lineCount = 0;
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        
        Shape mesh = new TriangleMesh();
        
        objFile = new File(objFilename);
        fileReader = new FileReader(objFile);
        bufferedReader = new BufferedReader(fileReader);

        String line = null;

        while (true) {
            line = bufferedReader.readLine();
            if (null == line) {
                break;
            }

            line = line.trim();

            if (line.length() == 0) {
                continue;
            }

            if (line.startsWith("#"))
            {
                continue;
            } else if (line.startsWith(VERTEX)) {
                processVertex(line);
            } else if (line.startsWith(TEXTURE_COORD)) {
                processTexture(line);
            } else if (line.startsWith(VERTEX_NORMAL)) {
                processNormal(line);
            } else if (line.startsWith(TRIANGLE)) {
            	processTriangle(line);
            } else {
                System.err.println("Parse.parseObjFile: line " + lineCount + " unknown line |" + line + "|");
            }
            lineCount++;
        }
        bufferedReader.close();

        System.err.println("Loaded " + lineCount + " lines");
    }

	private void processTriangle(String line) {
		String[] splitted = line.split("");
		String[] point1 = splitted[1].split("/");
		String[] point2 = splitted[2].split("/");
		String[] point3 = splitted[3].split("/");
		
	}

	private void processNormal(String line) {
		String[] splitted = line.split("");
		normals.add(new Vector(Double.parseDouble(splitted[1]),Double.parseDouble(splitted[2]),Double.parseDouble(splitted[3])));
	}

	private void processTexture(String line) {
		// TODO
	}

	private void processVertex(String line) {
		String[] splitted = line.split("");
		vertices.add(new Point(Double.parseDouble(splitted[1]),Double.parseDouble(splitted[2]),Double.parseDouble(splitted[3])));
	}
}
