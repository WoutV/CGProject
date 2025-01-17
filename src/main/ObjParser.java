package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import math.Coordinate2D;
import math.Point;
import math.Vector;
import shape.Triangle;
import shape.TriangleMesh;

public class ObjParser {
	
	private final static String VERTEX = "v";
	private final static String TEXTURE_COORD = "vt";
    private final static String VERTEX_NORMAL = "vn";
    private final static String TRIANGLE = "f";
    
    private List<Point> vertices = new ArrayList<Point>();
	private List<Vector> normals = new ArrayList<Vector>();
	private List<Coordinate2D> textureCoordinates = new ArrayList<Coordinate2D>();
	private TriangleMesh mesh = new TriangleMesh();
	private String fileName;
	
	public ObjParser(String objFilename) {
		this.fileName = objFilename;
	}
    
    File objFile = null;
	public TriangleMesh parseObjFile() throws FileNotFoundException, IOException {
        FileReader fr = null;
        BufferedReader br = null;
        
        objFile = new File(fileName);
        fr = new FileReader(objFile);
        br = new BufferedReader(fr);

        String line = null;

        while (true) {
            line = br.readLine();
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
            } else if (line.startsWith(TEXTURE_COORD)) {
                    processTexture(line);
            } else if (line.startsWith(VERTEX_NORMAL)) {
                    processNormal(line);
            } else if (line.startsWith(VERTEX)) {
                processVertex(line);
            }  else if (line.startsWith(TRIANGLE)) {
            	processTriangle(line);
            } else {
                System.err.println("This line could not be parsed");
            }
        }
        br.close();
        return this.mesh;
    }

	private void processfancyTriangle(String line) {
		String[] splitted = line.split(" ");
		String point1 = splitted[1];
		String point2 = splitted[2];
		String point3 = splitted[3];
		Point p1 = vertices.get(Integer.parseInt(point1.trim())-1);
		Point p2 = vertices.get(Integer.parseInt(point2.trim())-1);
		Point p3 = vertices.get(Integer.parseInt(point3.trim())-1);
		mesh.addTriangle(new Triangle(p1,p2,p3));
	}

	private void processTriangle(String line) {
		String[] splitted = line.split(" ");
		String[] point1 = splitted[1].split("/");
		String[] point2 = splitted[2].split("/");
		String[] point3 = splitted[3].split("/");
		Point p1 = vertices.get(Integer.parseInt(point1[0].trim())-1);
		Coordinate2D c1 = textureCoordinates.get(Integer.parseInt(point1[1].trim())-1);
		Vector n1 = normals.get(Integer.parseInt(point1[2].trim())-1);
		Point p2 = vertices.get(Integer.parseInt(point2[0].trim())-1);
		Coordinate2D c2 = textureCoordinates.get(Integer.parseInt(point2[1].trim())-1);
		Vector n2 = normals.get(Integer.parseInt(point2[2].trim())-1);
		Point p3 = vertices.get(Integer.parseInt(point3[0].trim())-1);
		Coordinate2D c3 = textureCoordinates.get(Integer.parseInt(point3[1].trim())-1);
		Vector n3 = normals.get(Integer.parseInt(point3[2].trim())-1);
		mesh.addTriangle(new Triangle(p1, p2, p3, n1, n2, n3, c1, c2, c3));
	}

	private void processNormal(String line) {
		String[] splitted = line.split(" ");
		normals.add(new Vector(Double.parseDouble(splitted[1]),Double.parseDouble(splitted[2]),Double.parseDouble(splitted[3])));
	}

	private void processTexture(String line) {
		String[] splitted = line.split(" ");
		textureCoordinates.add(new Coordinate2D(Double.parseDouble(splitted[1].trim()),Double.parseDouble(splitted[2].trim())));
	}

	private void processVertex(String line) {
		String[] splitted = line.split(" ");
		vertices.add(new Point(Double.parseDouble(splitted[1].trim()),Double.parseDouble(splitted[2].trim()),Double.parseDouble(splitted[3].trim())));
	}
}
