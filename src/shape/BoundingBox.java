package shape;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import math.Ray;

/**
 * This class represents an axis aligned bounding box.
 * 
 * @author wout
 *
 */
public class BoundingBox extends Intersectable {
	private double[] min;
	private double[] max;
	private List<Intersectable> content = new ArrayList<Intersectable>();

	public BoundingBox(double[] min, double[] max) {
		this.min = min;
		this.max = max;
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
			minx = (min[0]-ray.origin.x) / ray.direction.x;
			maxx = (max[0]-ray.origin.x) / ray.direction.x;
		} else {
			minx = (max[0]-ray.origin.x) / ray.direction.x;
			maxx = (min[0]-ray.origin.x) / ray.direction.x;
		}
		
		if(ray.direction.y >= 0){
			miny = (min[1]-ray.origin.y) / ray.direction.y;
			maxy = (max[1]-ray.origin.y) / ray.direction.y;
		} else {
			miny = (max[1]-ray.origin.y) / ray.direction.y;
			maxy = (min[1]-ray.origin.y) / ray.direction.y;
		}

		if(ray.direction.z >= 0){
			minz= (min[2]-ray.origin.z) / ray.direction.z;
			maxz = (max[2]-ray.origin.z) / ray.direction.z;
		} else {
			minz = (max[2]-ray.origin.z) / ray.direction.z;
			maxz = (min[2]-ray.origin.z) / ray.direction.z;
		}
		
		double max = Math.max(minx, Math.max(miny, minz));
		double min = Math.min(maxx, Math.min(maxy, maxz));
		
		if (min > max) {
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
		return min;
	}

	@Override
	public double[] getMaxCoordinates() {
		return max;
	}

	public void add(Intersectable intersectable) {
		content.add(intersectable);
	}
	
	@Override
	public List<Intersectable> getAll() {
		List<Intersectable> toReturn = new ArrayList<Intersectable>();
		for(Intersectable i : content) {
			toReturn.addAll(i.getAll());
		}
		return toReturn;
	}
	
	public void split(int axis) {
		List<Intersectable> all = new ArrayList<Intersectable>();
		for(Intersectable in : content) {
			all.addAll(in.getAll());
		}
		double limit = min[axis] + (max[axis]-min[axis])/2;
		if(!(all.size() < 2)){
			double minx = Double.MAX_VALUE;
			double miny = Double.MAX_VALUE;
			double minz = Double.MAX_VALUE;
			double maxx = Double.NEGATIVE_INFINITY;
			double maxy = Double.NEGATIVE_INFINITY;
			double maxz = Double.NEGATIVE_INFINITY;
			double minx2 = Double.MAX_VALUE;
			double miny2 = Double.MAX_VALUE;
			double minz2 = Double.MAX_VALUE;
			double maxx2 = Double.NEGATIVE_INFINITY;
			double maxy2 = Double.NEGATIVE_INFINITY;
			double maxz2 = Double.NEGATIVE_INFINITY;
			List<Intersectable> firstList = new ArrayList<Intersectable>();
			List<Intersectable> secondList = new ArrayList<Intersectable>();
			for(Intersectable t : all) {
				double [] min = t.getMinCoordinates();
				if(min[axis] > limit) {
					secondList.add(t);
					if(min[0] < minx2) { minx2 = min[0];}
					if(min[1] < miny2) { miny2 = min[1];}
					if(min[2] < minz2) { minz2 = min[2];}
					double [] max = t.getMaxCoordinates();
					if(max[0] > maxx2) { maxx2 = max[0];}
					if(max[1] > maxy2) { maxy2 = max[1];}
					if(max[2] > maxz2) { maxz2 = max[2];}
				} else {
					firstList.add(t);
					if(min[0] < minx) { minx = min[0];}
					if(min[1] < miny) { miny = min[1];}
					if(min[2] < minz) { minz = min[2];}
					double [] max = t.getMaxCoordinates();
					if(max[0] > maxx) { maxx = max[0];}
					if(max[1] > maxy) { maxy = max[1];}
					if(max[2] > maxz) { maxz = max[2];}
				}
			}
			double[] min1 = {minx,miny, minz};
			double[] min2 = {minx2,miny2, minz2};
			double[] max1 = {maxx, maxy, maxz};
			double[] max2 = {maxx2, maxy2, maxz2};
			setNewContent(axis, min1, max1, min2, max2, firstList, secondList);
		}
	}

	private void setNewContent(int axis, double[] min1, double[] max1,double[] min2, double[] max2, List<Intersectable> firstList,
			List<Intersectable> secondList) {
		
		BoundingBox first;
		BoundingBox second;
		List<Intersectable> newContent = new ArrayList<Intersectable>();
		if(!(firstList.isEmpty() | secondList.isEmpty())) {
			first = new BoundingBox(min1, max1);
			for(int i = 0;i<firstList.size();i++) {
				first.add(firstList.get(i));
			}
			second = new BoundingBox(min2, max2);
			for(int i = 0;i<secondList.size();i++) {
				second.add(secondList.get(i));
			}
			first.split((axis+1)%3);
			newContent.add(first);
			second.split((axis+1)%3);
			newContent.add(second);
			content = newContent;
		}
	}
	
	private void setNewSortedContent(int axis, double[] min1, double[] max1,double[] min2, double[] max2, List<Intersectable> firstList,
			List<Intersectable> secondList) {
		
		BoundingBox first;
		BoundingBox second;
		List<Intersectable> newContent = new ArrayList<Intersectable>();
		if(!(firstList.isEmpty() | secondList.isEmpty())) {
			first = new BoundingBox(min1, max1);
			for(int i = 0;i<firstList.size();i++) {
				first.add(firstList.get(i));
			}
			second = new BoundingBox(min2, max2);
			for(int i = 0;i<secondList.size();i++) {
				second.add(secondList.get(i));
			}
			first.splitSorted((axis+1)%3);
			newContent.add(first);
			second.splitSorted((axis+1)%3);
			newContent.add(second);
			content = newContent;
		}
	}
	
	public void splitSorted(int axis) {
		double minx = Double.MAX_VALUE;
		double miny = Double.MAX_VALUE;
		double minz = Double.MAX_VALUE;
		double maxx = Double.NEGATIVE_INFINITY;
		double maxy = Double.NEGATIVE_INFINITY;
		double maxz = Double.NEGATIVE_INFINITY;
		double minx2 = Double.MAX_VALUE;
		double miny2 = Double.MAX_VALUE;
		double minz2 = Double.MAX_VALUE;
		double maxx2 = Double.NEGATIVE_INFINITY;
		double maxy2 = Double.NEGATIVE_INFINITY;
		double maxz2 = Double.NEGATIVE_INFINITY;
		List<Intersectable> all = new ArrayList<Intersectable>();
		for(Intersectable in : content) {
			all.addAll(in.getAll());
		}
		Intersectable[] sorted = new Intersectable[all.size()];
		for(int i = 0; i < all.size(); i++) {
			sorted[i] = all.get(i);
		}
		quickSort(sorted, 0, sorted.length-1, axis);
		List<Intersectable> firstList = new ArrayList<Intersectable>();
		List<Intersectable> secondList = new ArrayList<Intersectable>();
		for(int i = 0;i < sorted.length/2; i++) {
			firstList.add(sorted[i]);
			double [] min = sorted[i].getMinCoordinates();
			if(min[0] < minx) { minx = min[0];}
			if(min[1] < miny) { miny = min[1];}
			if(min[2] < minz) { minz = min[2];}
			double [] max = sorted[i].getMaxCoordinates();
			if(max[0] > maxx) { maxx = max[0];}
			if(max[1] > maxy) { maxy = max[1];}
			if(max[2] > maxz) { maxz = max[2];}
		}
		for(int i = sorted.length/2; i< sorted.length;i++) {
			secondList.add(sorted[i]);
			double [] min = sorted[i].getMinCoordinates();
			if(min[0] < minx2) { minx2 = min[0];}
			if(min[1] < miny2) { miny2 = min[1];}
			if(min[2] < minz2) { minz2 = min[2];}
			double [] max = sorted[i].getMaxCoordinates();
			if(max[0] > maxx2) { maxx2 = max[0];}
			if(max[1] > maxy2) { maxy2 = max[1];}
			if(max[2] > maxz2) { maxz2 = max[2];}
		}
		double[] min1 = {minx,miny, minz};
		double[] min2 = {minx2,miny2, minz2};
		double[] max1 = {maxx, maxy, maxz};
		double[] max2 = {maxx2, maxy2, maxz2};
		setNewSortedContent(axis, min1, max1, min2, max2, firstList, secondList);
	}

	private int partition(Intersectable[] list, int left, int right, int axis) {
	      int i = left, j = right;
	      Intersectable tmp;
	      double pivot = list[(left + right) / 2].getMinCoordinates()[axis];
	      while (i <= j) {

	            while (list[i].getMinCoordinates()[axis] < pivot) {
	                  i++;
	            }
	            while (list[j].getMinCoordinates()[axis] > pivot) {
	                  j--;
	            }
	            if (i <= j) {
	                  tmp = list[i];
	                  list[i] = list[j];
	                  list[j] = tmp;
	                  i++;
	                  j--;
	            }
	      }
	      return i;
	}

	 

	private void quickSort(Intersectable[] list, int left, int right, int axis) {
	      int index = partition(list, left, right, axis);
	      if (left < index - 1) {
	            quickSort(list, left, index - 1, axis);
	      }
	      if (index < right) {
	            quickSort(list, index, right, axis);
	      }	
	}

	public void add(List<Intersectable> all) {
		for(Intersectable i : all) {
			add(i);
		}
	}
	
	@Override
	public boolean overlap(BoundingBox other) {
		for(int i = 0; i<3;i++){
			if(max[i]<other.min[i]) {return false;}
			if(min[i]>other.max[i]) {return false;}
		}
		return true;
	}

	
}
