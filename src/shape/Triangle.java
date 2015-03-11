package shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import math.Coordinate2D;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;
import shading.Material;

public class Triangle extends Shape {
	
	private static final double EPSILON = 0.0000001;

	private Point point1;
	private Point point2;
	private Point point3;
	
	private Vector surfaceNormal;

	private Vector normal1;
	private Vector normal2;
	private Vector normal3;
	
	private Coordinate2D textureCoord1;
	private Coordinate2D textureCoord2;
	private Coordinate2D textureCoord3;
	
	public Triangle(Transformation transformation, Point p1, Point p2, Point p3, Material shading) {
		
		if (transformation == null)
			throw new NullPointerException("the given transformation is null!");
		if ( p1 == null | p2 == null | p3 == null)
			throw new NullPointerException("one of the points is null!");
		if ( shading == null)
			throw new NullPointerException("the material is null!");
		
		this.transformation = transformation;
		this.point1 = p1;
		this.point2 = p2;
		this.point3 = p3;
		this.shading = shading;
		
		Vector normal = point2.subtract(point1).cross(point1.subtract(point1));
		this.surfaceNormal = normal;
		this.normal1 = normal;
		this.normal2 = normal;
		this.normal3 = normal;
	}
	
public Triangle(Transformation transformation, Point p1, Point p2, Point p3, Vector n1, Vector n2, Vector n3,  Material shading) {
		
		if (transformation == null)
			throw new NullPointerException("the given transformation is null!");
		if ( p1 == null | p2 == null | p3 == null)
			throw new NullPointerException("one of the points is null!");
		if ( n1 == null | n2 == null | n3 == null)
			throw new NullPointerException("one of the normals is null!");
		if (shading == null)
			throw new NullPointerException("The material3 is null!");	
		
		this.transformation = transformation;
		this.point1 = p1;
		this.point2 = p2;
		this.point3 = p3;
		this.shading = shading;
		
		Vector normal = point2.subtract(point1).cross(point3.subtract(point1));
		this.surfaceNormal = normal;
		this.normal1 = n1;
		this.normal2 = n2;
		this.normal3 = n3;
	}

	public Triangle(Point p1, Point p2, Point p3, Vector n1, Vector n2, Vector n3, Coordinate2D c1, Coordinate2D c2, Coordinate2D c3) {
		this.point1 = p1;
		this.point2 = p2;
		this.point3 = p3;
		Vector normal = point2.subtract(point1).cross(point3.subtract(point1));
		this.surfaceNormal = normal;
		this.normal1 = n1;
		this.normal2 = n2;
		this.normal3 = n3;
		this.textureCoord1 = c1;
		this.textureCoord2 = c2;
		this.textureCoord3 = c3;
	}

	public Triangle(Point p1, Point p2, Point p3) {
		this.point1 = p1;
		this.point2 = p2;
		this.point3 = p3;
		Vector normal = point2.subtract(point1).cross(point3.subtract(point1));
		this.surfaceNormal = normal;
		this.normal1 = normal;
		this.normal2 = normal;
		this.normal3 = normal;
	}

	/* (non-Javadoc)
	 * @see shape.Shape#intersect(math.Ray)
	 */
	@Override
	public Intersection intersect(Ray ray) {
		ray.intersectionCount++;
		Ray transformed = transformation.transformInverse(ray);

		ray.intersectionCount++;
		Vector o = transformed.origin.toVector3D();
		
		Double t = -(o.subtract(point1.toVector3D()).dot(surfaceNormal)/(transformed.direction.dot(surfaceNormal)));
		Double intersection;
		Point p = transformed.origin.add(transformed.direction.scale(t));
		
		if(isInTriangle(p)){
			intersection = t;
		} else {
			intersection =  -1.0;
		}
		Point hitPoint = ray.origin.add(ray.direction.scale(t));
		Vector normal = getNormal(p);
		Vector newNormal = null;
		if(normal!=null){
			newNormal = transformation.inverseTransposeTransform(normal);
			newNormal = newNormal.normalize();
		}
		
		Coordinate2D uv = getUVcoordinates(p);
		return new Intersection(hitPoint, ray, shading, newNormal, intersection, uv);
	}

	private Coordinate2D getUVcoordinates(Point p) {
		double[] bary = getBarycentric(p);
		if(bary!=null & textureCoord1 != null) {
			Coordinate2D uv = textureCoord1.scale(bary[0]).add(textureCoord2.scale(bary[1])).add(textureCoord3.scale(bary[2]));
			return uv;
		}
		return null;
	}

	/**
	 * @param p
	 * @return The normal in the given point, based on the barycentric coordinates. Not necessarily perpendicular to triangle, but perpendicular
	 * to the mesh the triangle is part of.
	 */
	public Vector getNormal(Point p) {
		double[] bary = getBarycentric(p);
		if(bary!=null) {
			Vector normal = normal1.scale(bary[0]).add(normal2.scale(bary[1])).add(normal3.scale(bary[2]));
			return normal.normalize();
		}
		return null;
	}

	/**
	 * Checks whether or not a given point is inside this triangle. This is done by calculating the barycentric coordinates of the point.
	 * @param p the given point
	 * @return true if and only if the point is inside the triangle
	 */
	public boolean isInTriangle(Point p) {
		double[] bary = getBarycentric(p);
		if(bary!=null) {
			double beta = bary[0];
	    	double gamma = bary[1];
	    	double alpha  = bary[2];
	    
	    	return alpha <= 1 && alpha >= EPSILON && (beta <= 1) && beta >= EPSILON && (gamma <= 1) && gamma >= EPSILON;
		} return false;
	}
	
	/**
	 * @param p
	 * @return the barycentric coordinates of point p in the base of this triangle
	 * based on: http://gamedev.stackexchange.com/questions/23743/whats-the-most-efficient-way-to-find-barycentric-coordinates
	 */
	public double[] getBarycentric(Point p) {
		Vector u = point2.subtract(point1);
	    Vector v = point3.subtract(point1);
	    Vector w = p.subtract(point1);
	 
	    Vector vCrossW = v.cross(w);
	    Vector vCrossU = v.cross(u);
	    
	    if(vCrossW.dot(vCrossU)<0) {
	    	return null;
	    }
	    
	    Vector uCrossW = u.cross(w);
	    Vector uCrossV = u.cross(v);
	    
	    if(uCrossW.dot(uCrossV) < 0) {
	    	return null;
	    }
	    
	    double invArea = 1/uCrossV.length();
	    
	    double beta = vCrossW.length()*invArea;
	    double gamma = uCrossW.length()*invArea;
	    double alpha  = 1-gamma-beta;
	    
	    return new double[]{alpha,beta,gamma};
	}
	
	public void setTransformation(Transformation transformation) {
		this.transformation = transformation;
	}
	
	public void setShading(Material shading) {
		this.shading = shading;
	}
	
	@Override
	public double[] getMinCoordinates() {
		Point trans1 = transformation.transform(point1);
		Point trans2 = transformation.transform(point2);
		Point trans3 = transformation.transform(point3);
		double minx = Math.min(trans1.x, Math.min(trans2.x,trans3.x));
		double miny = Math.min(trans1.y, Math.min(trans2.y,trans3.y));
		double minz = Math.min(trans1.z, Math.min(trans2.z,trans3.z));

		return new double[]{minx,miny,minz};
	}
	
	@Override
	public double[] getMaxCoordinates() {
		Point trans1 = transformation.transform(point1);
		Point trans2 = transformation.transform(point2);
		Point trans3 = transformation.transform(point3);
		double minx = Math.max(trans1.x, Math.max(trans2.x,trans3.x));
		double miny = Math.max(trans1.y, Math.max(trans3.y,trans2.y));
		double minz = Math.max(trans1.z, Math.max(trans2.z,trans3.z));

		return new double[]{minx,miny,minz};
	}
	
	@Override
	public Collection<Intersectable> getAll() {
		List<Intersectable> toReturn = new ArrayList<Intersectable>();
		toReturn.add(this);
		return toReturn;
	}
	
	@Override
	public Intersectable getBoundingBox() {
		double [] min = getMinCoordinates();
		double [] max = getMaxCoordinates();
		BoundingBox bb = new BoundingBox(min, max);
		bb.add(this);
		return bb;
	}
	
}
