package test;

import static org.junit.Assert.assertEquals;
import math.Point;
import math.Ray;
import math.Transformation;
import math.Vector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import shape.Cylinder;
import shape.Intersection;

public class CylinderTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIntersection() {
		Transformation id = Transformation.createIdentity();
		Cylinder c1 = new Cylinder(id,null,3,1);
		Ray fromTop = new Ray(new Point (0.0,5.0,0.0), new Vector(0.0,-1.0,0.0));
		Intersection intersection = c1.intersect(fromTop);
		Point intersect = intersection.getPoint();
		assertEquals(intersect,new Point(0.0,3.0,0.0));
		Ray fromTop2 = new Ray(new Point (1.0,5.0,0.0), new Vector(0.0,-1.0,0.0));
		Intersection intersection2 = c1.intersect(fromTop2);
		Point intersect2 = intersection2.getPoint();
		assertEquals(intersect2,new Point(1.0,3.0,0.0));
		
		Transformation down = Transformation.createTranslation(0.0, -1.0, 0.0);
		Cylinder c2 = new Cylinder(down,null,3,1);
		Ray fromBottom = new Ray(new Point (0.0,-5.0,0.0), new Vector(0.0,1.0,0.0));
		Intersection intersection3 = c2.intersect(fromBottom);
		Point intersect3 = intersection3.getPoint();
		assertEquals(intersect3,new Point(0.0,-1.0,0.0));
	}

}
