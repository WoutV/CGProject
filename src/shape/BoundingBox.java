package shape;

import java.util.ArrayList;
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
	private List<Intersectable> content;
	private Intersectable[] boundingboxes = new Intersectable[2];

	public BoundingBox(double[] min, double[] max) {
		this.min = min;
		this.max = max;
		content = new ArrayList<Intersectable>();
		setProjectedArea();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see shape.Intersectable#intersect(math.Ray)
	 */
	@Override
	public Intersection intersect(Ray ray) {
		// if(content != null) {
		// System.out.println(content.size());
		// }
		Intersection hitIntersection = null;
		ray.intersectionCount++;
		double minx, maxx;
		double miny, maxy;
		double minz, maxz;
		if (ray.direction.x >= 0) {
			minx = (min[0] - ray.origin.x) / ray.direction.x;
			maxx = (max[0] - ray.origin.x) / ray.direction.x;
		} else {
			minx = (max[0] - ray.origin.x) / ray.direction.x;
			maxx = (min[0] - ray.origin.x) / ray.direction.x;
		}

		if (ray.direction.y >= 0) {
			miny = (min[1] - ray.origin.y) / ray.direction.y;
			maxy = (max[1] - ray.origin.y) / ray.direction.y;
		} else {
			miny = (max[1] - ray.origin.y) / ray.direction.y;
			maxy = (min[1] - ray.origin.y) / ray.direction.y;
		}

		if (ray.direction.z >= 0) {
			minz = (min[2] - ray.origin.z) / ray.direction.z;
			maxz = (max[2] - ray.origin.z) / ray.direction.z;
		} else {
			minz = (max[2] - ray.origin.z) / ray.direction.z;
			maxz = (min[2] - ray.origin.z) / ray.direction.z;
		}

		double max = Math.max(minx, Math.max(miny, minz));
		double min = Math.min(maxx, Math.min(maxy, maxz));

		double minInt = Double.MAX_VALUE;
		if (min > max) {
			if (content == null) {
				double firstInter = getIntersect(boundingboxes[0], ray);
				double secondInter = getIntersect(boundingboxes[1], ray);
				Intersectable first = boundingboxes[0];
				Intersectable second = boundingboxes[1];
				if (firstInter > secondInter) {
					first = boundingboxes[1];
					second = boundingboxes[0];
				}
				Intersection firstHit = first.intersect(ray);
				if (firstHit != null) {
					Double t = firstHit.getT();
					if (t + 1 > EPSILON & t > EPSILON) {
						if (t < minInt) {
							minInt = t;
							hitIntersection = firstHit;
						}
					}
				}
				if(secondInter < minInt) {
					Intersection secondHit = second.intersect(ray);
					if (secondHit != null) {
						Double t = secondHit.getT();
						if (t + 1 > EPSILON & t > EPSILON) {
							if (t < minInt) {
								minInt = t;
								hitIntersection = secondHit;
							}
						}
					}
				}
			} else {
				for (Intersectable intersectable : content) {
					Intersection intersection = intersectable.intersect(ray);
					if (intersection != null) {
						Double t = intersection.getT();
						if (t + 1 > EPSILON & t > EPSILON) {
							if (t < minInt) {
								minInt = t;
								hitIntersection = intersection;
							}
						}
					}
				}
			}
			return hitIntersection;
		}
		return null;
	}

	private double getIntersect(Intersectable intersectable, Ray ray) {
		double[] boxmin = intersectable.getMinCoordinates();
		double[] boxmax = intersectable.getMaxCoordinates();
		double minx, maxx;
		double miny, maxy;
		double minz, maxz;
		if (ray.direction.x >= 0) {
			minx = (boxmin[0] - ray.origin.x) / ray.direction.x;
			maxx = (boxmax[0] - ray.origin.x) / ray.direction.x;
		} else {
			minx = (boxmax[0] - ray.origin.x) / ray.direction.x;
			maxx = (boxmin[0] - ray.origin.x) / ray.direction.x;
		}

		if (ray.direction.y >= 0) {
			miny = (boxmin[1] - ray.origin.y) / ray.direction.y;
			maxy = (boxmax[1] - ray.origin.y) / ray.direction.y;
		} else {
			miny = (boxmax[1] - ray.origin.y) / ray.direction.y;
			maxy = (boxmin[1] - ray.origin.y) / ray.direction.y;
		}

		if (ray.direction.z >= 0) {
			minz = (boxmin[2] - ray.origin.z) / ray.direction.z;
			maxz = (boxmax[2] - ray.origin.z) / ray.direction.z;
		} else {
			minz = (boxmax[2] - ray.origin.z) / ray.direction.z;
			maxz = (boxmin[2] - ray.origin.z) / ray.direction.z;
		}

		double max = Math.max(minx, Math.max(miny, minz));
		double min = Math.min(maxx, Math.min(maxy, maxz));
		if (min > max) {
			if (max > 0) {
				return max;
			} else {
				return Double.MAX_VALUE;
			}
		} else {
			return Double.MAX_VALUE;
		}
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
		for (Intersectable i : content) {
			toReturn.addAll(i.getAll());
		}
		return toReturn;
	}

	public void split(int axis, String metric, String whichaxis) {
		List<Intersectable> all = new ArrayList<Intersectable>();
		for (Intersectable in : content) {
			all.addAll(in.getAll());
		}
		// System.out.println("All :" + all.size());
		if (whichaxis.equals("longest")) {
			double maxDiff = -1.0;
			for (int i = 0; i < 3; i++) {
				if (max[i] - min[i] > maxDiff) {
					axis = i;
					maxDiff = (max[i] - min[i]);
				}
			}
		}
		double limit = (max[axis] + min[axis]) / 2;
		if (!(all.size() < 2)) {
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

			for (Intersectable t : all) {
				double[] minc = t.getMinCoordinates();
				double[] maxc = t.getMaxCoordinates();
				boolean right = false;
				switch (metric) {
				case "min":
					right = (minc[axis] > limit);
					break;
				case "max":
					right = (maxc[axis] > limit);
					break;
				case "mid":
					right = (maxc[axis] + minc[axis]) / 2 > limit;
					break;
				default:
					right = (minc[axis] > limit);
				}
				if (right) {
					secondList.add(t);
					if (minc[0] < minx2) {
						minx2 = minc[0];
					}
					if (minc[1] < miny2) {
						miny2 = minc[1];
					}
					if (minc[2] < minz2) {
						minz2 = minc[2];
					}
					if (maxc[0] > maxx2) {
						maxx2 = maxc[0];
					}
					if (maxc[1] > maxy2) {
						maxy2 = maxc[1];
					}
					if (maxc[2] > maxz2) {
						maxz2 = maxc[2];
					}
				} else {
					firstList.add(t);
					if (minc[0] < minx) {
						minx = minc[0];
					}
					if (minc[1] < miny) {
						miny = minc[1];
					}
					if (minc[2] < minz) {
						minz = minc[2];
					}
					if (maxc[0] > maxx) {
						maxx = maxc[0];
					}
					if (maxc[1] > maxy) {
						maxy = maxc[1];
					}
					if (maxc[2] > maxz) {
						maxz = maxc[2];
					}
					// if(minc[0] < minx2) { minx2 = minc[0];}
					// if(minc[1] < miny2) { miny2 = minc[1];}
					// if(minc[2] < minz2) { minz2 = minc[2];}
					// double [] max = t.getMaxCoordinates();
					// if(max[0] > maxx2) { maxx2 = max[0];}
					// if(max[1] > maxy2) { maxy2 = max[1];}
					// if(max[2] > maxz2) { maxz2 = max[2];}
				}
			}
			double[] min1 = { minx, miny, minz };
			double[] min2 = { minx2, miny2, minz2 };
			double[] max1 = { maxx, maxy, maxz };
			double[] max2 = { maxx2, maxy2, maxz2 };
			setNewContent(axis, min1, max1, min2, max2, firstList, secondList,
					metric, whichaxis);
		}
	}

	private void setNewContent(int axis, double[] min1, double[] max1,
			double[] min2, double[] max2, List<Intersectable> firstList,
			List<Intersectable> secondList, String metric, String whichaxis) {
		BoundingBox first;
		BoundingBox second;
		List<Intersectable> newContent = new ArrayList<Intersectable>();
		if (!(firstList.isEmpty() | secondList.isEmpty())) {
			first = new BoundingBox(min1, max1);
			for (int i = 0; i < firstList.size(); i++) {
				first.add(firstList.get(i));
			}
			second = new BoundingBox(min2, max2);
			for (int i = 0; i < secondList.size(); i++) {
				second.add(secondList.get(i));
			}
			first.split((axis + 1) % 3, metric, whichaxis);
			boundingboxes[0] = first;
			newContent.add(first);
			second.split((axis + 1) % 3, metric, whichaxis);
			newContent.add(second);
			boundingboxes[1] = second;
//			content = newContent;
			content = null;
		}
	}

	private void setNewSortedContent(int axis, double[] min1, double[] max1,
			double[] min2, double[] max2, List<Intersectable> firstList,
			List<Intersectable> secondList, String metric, String whichaxis) {

		BoundingBox first;
		BoundingBox second;
		List<Intersectable> newContent = new ArrayList<Intersectable>();
		if (!(firstList.isEmpty() | secondList.isEmpty())) {
			first = new BoundingBox(min1, max1);
			for (int i = 0; i < firstList.size(); i++) {
				first.add(firstList.get(i));
			}
			second = new BoundingBox(min2, max2);
			for (int i = 0; i < secondList.size(); i++) {
				second.add(secondList.get(i));
			}
			first.splitSorted((axis + 1) % 3, metric, whichaxis);
			newContent.add(first);
			 boundingboxes[0] = first;
			// content = newContent;
			second.splitSorted((axis + 1) % 3, metric, whichaxis);
			 boundingboxes[1] = second;
//			newContent.add(second);
//			content = newContent;
			 content = null;
		}
	}

	public void splitSorted(int axis, String metric, String whichaxis) {
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
		if (whichaxis.equals("longest")) {
			double maxDiff = -1.0;
			for (int i = 0; i < 3; i++) {
				if (max[i] - min[i] > maxDiff) {
					axis = i;
					maxDiff = (max[i] - min[i]);
				}
			}
		}
		List<Intersectable> all = new ArrayList<Intersectable>();
		for (Intersectable in : content) {
			all.addAll(in.getAll());
		}
		Intersectable[] sorted = new Intersectable[all.size()];
		for (int i = 0; i < all.size(); i++) {
			sorted[i] = all.get(i);
		}
		quickSort(sorted, 0, sorted.length - 1, axis, metric);

		List<Intersectable> firstList = new ArrayList<Intersectable>();
		List<Intersectable> secondList = new ArrayList<Intersectable>();
		for (int i = 0; i < sorted.length / 2; i++) {
			firstList.add(sorted[i]);
			double[] min = sorted[i].getMinCoordinates();
			if (min[0] < minx) {
				minx = min[0];
			}
			if (min[1] < miny) {
				miny = min[1];
			}
			if (min[2] < minz) {
				minz = min[2];
			}
			double[] max = sorted[i].getMaxCoordinates();
			if (max[0] > maxx) {
				maxx = max[0];
			}
			if (max[1] > maxy) {
				maxy = max[1];
			}
			if (max[2] > maxz) {
				maxz = max[2];
			}
		}
		for (int i = sorted.length / 2; i < sorted.length; i++) {
			secondList.add(sorted[i]);
			double[] min = sorted[i].getMinCoordinates();
			if (min[0] < minx2) {
				minx2 = min[0];
			}
			if (min[1] < miny2) {
				miny2 = min[1];
			}
			if (min[2] < minz2) {
				minz2 = min[2];
			}
			double[] max = sorted[i].getMaxCoordinates();
			if (max[0] > maxx2) {
				maxx2 = max[0];
			}
			if (max[1] > maxy2) {
				maxy2 = max[1];
			}
			if (max[2] > maxz2) {
				maxz2 = max[2];
			}
		}
		double[] min1 = { minx, miny, minz };
		double[] min2 = { minx2, miny2, minz2 };
		double[] max1 = { maxx, maxy, maxz };
		double[] max2 = { maxx2, maxy2, maxz2 };
		setNewSortedContent(axis, min1, max1, min2, max2, firstList,
				secondList, metric, whichaxis);
	}

	private int partition(Intersectable[] list, int left, int right, int axis,
			String metric) {

		switch (metric) {
		case "min":
			return partitionMin(list, left, right, axis);
		case "max":
			return partitionMax(list, left, right, axis);
		case "mid":
			return partitionMiddle(list, left, right, axis);
		default:
			return partitionMin(list, left, right, axis);
		}
	}

	private int partitionMax(Intersectable[] list, int left, int right, int axis) {
		int i = left, j = right;
		Intersectable tmp;
		double pivot = list[(left + right) / 2].getMaxCoordinates()[axis];
		while (i <= j) {

			while (list[i].getMaxCoordinates()[axis] < pivot) {
				i++;
			}
			while (list[j].getMaxCoordinates()[axis] > pivot) {
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

	private int partitionMiddle(Intersectable[] list, int left, int right,
			int axis) {
		int i = left, j = right;
		Intersectable tmp;
		// double min = list[(left + right) / 2].getMinCoordinates()[axis];
		// double max = list[(left + right) / 2].getMaxCoordinates()[axis];
		double pivot = list[(left + right) / 2].getMiddleCoordinates(axis);
		while (i <= j) {
			while (list[i].getMiddleCoordinates(axis) < pivot) {
				i++;
			}
			while (list[j].getMiddleCoordinates(axis) > pivot) {
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

	private int partitionMin(Intersectable[] list, int left, int right, int axis) {
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

	private void quickSort(Intersectable[] list, int left, int right, int axis,
			String metric) {
		int index = partition(list, left, right, axis, metric);
		if (left < index - 1) {
			quickSort(list, left, index - 1, axis, metric);
		}
		if (index < right) {
			quickSort(list, index, right, axis, metric);
		}
	}

	// private void quickSort(Intersectable[] list, int left, int right, int
	// axis, String metric) {
	// int index = partition(list, left, right, axis, metric);
	// if (left < index - 1) {
	// quickSort(list, left, index - 1, axis, metric);
	// }
	// if (index < right) {
	// quickSort(list, index, right, axis, metric);
	// }
	// }

	public void add(List<Intersectable> all) {
		for (Intersectable i : all) {
			add(i);
		}
	}

	@Override
	public boolean overlap(BoundingBox other) {
		for (int i = 0; i < 3; i++) {
			if (max[i] < other.min[i]) {
				return false;
			}
			if (min[i] > other.max[i]) {
				return false;
			}
		}
		return true;
	}

	@Override
	protected void setProjectedArea() {
		double[] diff = new double[3];
		for (int i = 0; i < 3; i++) {
			diff[i] = max[i] - min[i];
		}
		for (int i = 0; i < 3; i++) {
			area = area + 2 * (diff[i] * diff[(i + 1) % 3]);
		}
	}

	public void splitSAH(int axis, String metric, String whichaxis) {
		List<Intersectable> all = new ArrayList<Intersectable>();
		for (Intersectable in : content) {
			all.addAll(in.getAll());
		}
		double minCost = Double.MAX_VALUE;
		if (whichaxis.equals("longest")) {
			double maxDiff = -1.0;
			for (int i = 0; i < 3; i++) {
				if (max[i] - min[i] > maxDiff) {
					axis = i;
					maxDiff = (max[i] - min[i]);
				}
			}
		}
		double split = 0.0;
		for (Intersectable t : all) {
			double minC = t.getMinCoordinates()[axis];
			double maxC = t.getMaxCoordinates()[axis];
			double newCost = getCost(minC, axis, metric, all);
			if (newCost < minCost) {
				minCost = newCost;
				split = minC;
			}
			newCost = getCost(maxC, axis, metric, all);
			if (newCost < minCost) {
				minCost = newCost;
				split = maxC;
			}
		}
		if (!(all.size() < 2)) {
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

			for (Intersectable t : all) {
				double[] minc = t.getMinCoordinates();
				double[] maxc = t.getMaxCoordinates();
				boolean right = false;
				switch (metric) {
				case "min":
					right = (minc[axis] > split);
					break;
				case "max":
					right = (maxc[axis] > split);
					break;
				case "mid":
					right = (maxc[axis] + minc[axis]) / 2 > split;
					break;
				default:
					right = (minc[axis] > split);
				}
				if (right) {
					secondList.add(t);
					if (minc[0] < minx2) {
						minx2 = minc[0];
					}
					if (minc[1] < miny2) {
						miny2 = minc[1];
					}
					if (minc[2] < minz2) {
						minz2 = minc[2];
					}
					double[] max = t.getMaxCoordinates();
					if (max[0] > maxx2) {
						maxx2 = max[0];
					}
					if (max[1] > maxy2) {
						maxy2 = max[1];
					}
					if (max[2] > maxz2) {
						maxz2 = max[2];
					}
				} else {
					firstList.add(t);
					if (min[0] < minx) {
						minx = min[0];
					}
					if (min[1] < miny) {
						miny = min[1];
					}
					if (min[2] < minz) {
						minz = min[2];
					}
					double[] max = t.getMaxCoordinates();
					if (max[0] > maxx) {
						maxx = max[0];
					}
					if (max[1] > maxy) {
						maxy = max[1];
					}
					if (max[2] > maxz) {
						maxz = max[2];
					}
				}
			}
			double[] min1 = { minx, miny, minz };
			double[] min2 = { minx2, miny2, minz2 };
			double[] max1 = { maxx, maxy, maxz };
			double[] max2 = { maxx2, maxy2, maxz2 };
			setNewContentSAH(axis, min1, max1, min2, max2, firstList,
					secondList, metric, whichaxis);
		}
	}

	private void setNewContentSAH(int axis, double[] min1, double[] max1,
			double[] min2, double[] max2, List<Intersectable> firstList,
			List<Intersectable> secondList, String metric, String whichaxis) {
		BoundingBox first;
		BoundingBox second;
		List<Intersectable> newContent = new ArrayList<Intersectable>();
		if (!(firstList.isEmpty() | secondList.isEmpty())) {
			first = new BoundingBox(min1, max1);
			for (int i = 0; i < firstList.size(); i++) {
				first.add(firstList.get(i));
			}
			second = new BoundingBox(min2, max2);
			for (int i = 0; i < secondList.size(); i++) {
				second.add(secondList.get(i));
			}
			first.splitSAH((axis + 1) % 3, metric, whichaxis);
			newContent.add(first);
			second.splitSAH((axis + 1) % 3, metric, whichaxis);
			newContent.add(second);
			content = newContent;
		}
	}

	private double getCost(double split, int axis, String metric,
			List<Intersectable> all) {
		double cost = 0.0;
		double chanceFirst = 0;
		double chanceSecond = 0;
		double[] diffFirst = new double[3];
		for (int i = 0; i < 3; i++) {
			if (i == axis) {
				diffFirst[i] = split - min[i];
			} else {
				diffFirst[i] = max[i] - min[i];
			}
		}
		for (int i = 0; i < 3; i++) {
			chanceFirst = chanceFirst + 2
					* (diffFirst[i] * diffFirst[(i + 1) % 3]);
		}
		chanceFirst = chanceFirst / area;
		double[] diffSecond = new double[3];
		for (int i = 0; i < 3; i++) {
			if (i == axis) {
				diffSecond[i] = max[i] - split;
			} else {
				diffSecond[i] = max[i] - min[i];
			}
		}
		for (int i = 0; i < 3; i++) {
			chanceSecond = chanceSecond + 2
					* (diffSecond[i] * diffSecond[(i + 1) % 3]);
		}
		chanceSecond = chanceSecond / area;

		double costFirst = 0;
		double costSecond = 0;
		for (Intersectable t : all) {
			double[] minc = t.getMinCoordinates();
			double[] maxc = t.getMaxCoordinates();
			boolean right = false;
			switch (metric) {
			case "min":
				right = (minc[axis] > split);
				break;
			case "max":
				right = (maxc[axis] > split);
				break;
			case "mid":
				right = (maxc[axis] + minc[axis]) / 2 > split;
				break;
			default:
				right = (minc[axis] > split);
			}
			if (right) {
				costSecond = costSecond + t.getCost();
			} else {
				costFirst = costFirst + t.getCost();
			}
		}
		cost = chanceFirst * costFirst + chanceSecond * costSecond;
		return cost;
	}

	@Override
	public double getCost() {
		return 1;
	}
}
