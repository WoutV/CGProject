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
		Double intersection = c1.intersect(fromTop);
		Point intersect = fromTop.origin.add(fromTop.direction.scale(intersection));
		assertEquals(intersect,new Point(0.0,3.0,0.0));
		Ray fromTop2 = new Ray(new Point (1.0,5.0,0.0), new Vector(0.0,-1.0,0.0));
		Double intersection2 = c1.intersect(fromTop2);
		Point intersect2 = fromTop2.origin.add(fromTop2.direction.scale(intersection2));
		assertEquals(intersect2,new Point(1.0,3.0,0.0));
		
		Transformation down = Transformation.createTranslation(0.0, -1.0, 0.0);
		Cylinder c2 = new Cylinder(down,null,3,1);
		Ray fromBottom = new Ray(new Point (0.0,-5.0,0.0), new Vector(0.0,1.0,0.0));
		Double intersection3 = c2.intersect(fromBottom);
		Point intersect3 = fromBottom.origin.add(fromBottom.direction.scale(intersection3));
		assertEquals(intersect3,new Point(0.0,-1.0,0.0));
	}

}
