package shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import math.Ray;
import math.Transformation;

/**
 * This class represents an axis aligned bounding box.
 * 
 * @author wout
 *
 */
public class BoundingBox extends Intersectable {
	private double xmin;
	private double ymin;
	private double zmin;
	private double xmax;
	private double ymax;
	private double zmax;
	private List<Intersectable> content = new ArrayList<Intersectable>();

	public BoundingBox(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax) {
		this.xmin = xmin;
		this.xmax = xmax;
		this.ymin = ymin;
		this.ymax = ymax;
		this.zmin = zmin;
		this.zmax = zmax;
		System.err.println(xmin+" "+ymin+" "+zmin);
		System.err.println(xmax+" "+ymax+" "+zmax);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see shape.Intersectable#intersect(math.Ray)
	 */
	@Override
	public Intersection intersect(Ray ray) {
		ray.intersectionCount++;
		double minx, maxx;
		double miny,maxy;
		double minz,maxz;
		if(ray.direction.x >= 0){
			minx = (xmin-ray.origin.x) / ray.direction.x;
			maxx = (xmax-ray.origin.x) / ray.direction.x;
		} else {
			minx = (xmax-ray.origin.x) / ray.direction.x;
			maxx = (xmin-ray.origin.x) / ray.direction.x;
		}
		
		if(ray.direction.y >= 0){
			miny = (ymin-ray.origin.y) / ray.direction.y;
			maxy = (ymax-ray.origin.y) / ray.direction.y;
		} else {
			miny = (ymax-ray.origin.y) / ray.direction.y;
			maxy = (ymin-ray.origin.y) / ray.direction.y;
		}

		if(ray.direction.z >= 0){
			minz= (zmin-ray.origin.z) / ray.direction.z;
			maxz = (zmax-ray.origin.z) / ray.direction.z;
		} else {
			minz = (zmax-ray.origin.z) / ray.direction.z;
			maxz = (zmin-ray.origin.z) / ray.direction.z;
		}
//
		double max = Math.max(minx, Math.max(miny, minz));
		double min = Math.min(maxx, Math.min(maxy, maxz));
		if (min > max) {
//			System.err.println("INTERSECTED THE BOX");
			Intersection hitIntersection = null;
			for (Intersectable intersectable : content) {
				Intersection intersection = intersectable.intersect(ray);
				if(intersection != null) {
					Double t = intersection.getT();
					if (t + 1 > EPSILON & t > EPSILON) {
						if (t < min) {
							min = t;
							hitIntersection = intersection;
						}
					}
				}
			}
			return hitIntersection;
		}
		return null;
	}

	public void setContent(List<Intersectable> shapes) {
		this.content = shapes;
	}

	@Override
	public double[] getMinCoordinates() {
		return new double[]{xmin,ymin,zmin};
	}

	@Override
	public double[] getMaxCoordinates() {
		return new double[]{xmax,ymax,zmax};
	}

	public void split() {
//		
		List<Intersectable> all = new ArrayList<Intersectable>();
		for(Intersectable in : content) {
			all.addAll(in.getAll());
		}
		if(!(all.size() < 2)){
			double minx = Double.MAX_VALUE;
			double miny = Double.MAX_VALUE;
			double minz = Double.MAX_VALUE;
			double maxx = Double.MIN_VALUE;
			double maxy = Double.MIN_VALUE;
			double maxz = Double.MIN_VALUE;
			BoundingBox first = null;
			BoundingBox second = null;
			List<Intersectable> newContent = new ArrayList<Intersectable>();
			for(int i = 0;i<all.size()/2;i++) {
				Intersectable t = all.get(i);
				double [] min = t.getMinCoordinates();
				if(min[0] < minx) { minx = min[0];}
				if(min[1] < miny) { miny = min[1];}
				if(min[2] < minz) { minz = min[2];}
				double [] max = t.getMaxCoordinates();
				if(max[0] > maxx) { maxx = max[0];}
				if(max[1] > maxy) { maxy = max[1];}
				if(max[2] > maxz) { maxz = max[2];}
			}
			first = new BoundingBox(minx,maxx,miny,maxy,minz,maxz);
			for(int i = 0;i<all.size()/2;i++) {
				first.add(all.get(i));
			}
			
			minx = Double.MAX_VALUE;
			miny = Double.MAX_VALUE;
			minz = Double.MAX_VALUE;
			maxx = Double.MIN_VALUE;
			maxy = Double.MIN_VALUE;
			maxz = Double.MIN_VALUE;
			for(int i = all.size()/2;i<all.size();i++) {
				Intersectable t = all.get(i);
				double [] min = t.getMinCoordinates();
				if(min[0] < minx) { minx = min[0];}
				if(min[1] < miny) { miny = min[1];}
				if(min[2] < minz) { minz = min[2];}
				double [] max = t.getMaxCoordinates();
				if(max[0] > maxx) { maxx = max[0];}
				if(max[1] > maxy) { maxy = max[1];}
				if(max[2] > maxz) { maxz = max[2];}
			}
			second = new BoundingBox(minx,maxx,miny,maxy,minz,maxz);
			for(int i = all.size()/2;i<all.size();i++) {
				second.add(all.get(i));
			}
			first.split();
			newContent.add(first);
			second.split();
			newContent.add(second);
			content = newContent;
		}
		
	}

	public void add(Intersectable intersectable) {
		content.add(intersectable);
	}
	
	@Override
	public Collection<Intersectable> getAll() {
		List<Intersectable> toReturn = new ArrayList<Intersectable>();
		toReturn.addAll(content);
		return toReturn;
	}

}
