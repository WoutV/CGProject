package test;

import static org.junit.Assert.*;

import math.Point;
import math.Transformation;
import math.Vector;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import shape.Triangle;

public class TriangleTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testBarycentric() {
		Triangle tr = new Triangle(new Point(), new Point(2.0,0.0,0.0), new Point(0.0,2.0,0.0), new Vector(1,0,0), new Vector(0,1,0), new Vector(0,0,1));
		Transformation ts = Transformation.createTranslation(0.0, -1.0, 5);
		tr.setTransformation(ts);
		tr.isInTriangle(new Point(1,1,0));
		tr.isInTriangle(new Point());
		Vector v = tr.getNormal(new Point());
		System.out.println(v);
		v = ts.inverseTransposeTransform(v);
		System.out.println(v);
		Vector v2 = tr.getNormal(new Point(1,1,0));
		v2 = ts.inverseTransposeTransform(v2);
		System.out.println(v2);
	}

}
