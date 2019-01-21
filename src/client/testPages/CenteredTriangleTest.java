package client.testPages;

import java.util.Random;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class CenteredTriangleTest {
	private static final int NUM_TRIS = 6;
	static int seed = (int) (Math.random() * 100000);

	private final PolygonRenderer renderer;
	private final Drawable panel;
	Vertex3D center;

	public CenteredTriangleTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;

		makeCenter();
		render();
	}

	private void render() {

		Random rand = new Random();

		double radius = 275;
		double angle = 0.0;
		double rot = (2 * Math.PI) / 3;

		double v = 1; // 1, 0.85, 0.7, 0.55, 0.4, 0.25

		for (int ray = 0; ray < NUM_TRIS; ray++) {
			double z = rand.nextDouble() * -198 - 1;
			angle = (rand.nextDouble() * 120);
			Vertex3D p1 = radialPoint(radius, angle, v, z);

			angle = angle + rot;
			Vertex3D p2 = radialPoint(radius, angle, v, z);

			angle = angle + rot;
			Vertex3D p3 = radialPoint(radius, angle, v, z);

			Vertex3D[] points = { p1, p2, p3 };
			Polygon poly = Polygon.make(points);
			renderer.drawPolygon(poly, panel);

			v = v - 0.15;

		}
	}

	private void makeCenter() {
		int centerX = panel.getWidth() / 2;
		int centerY = panel.getHeight() / 2;
		center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
	}

	private Vertex3D radialPoint(double radius, double angle, double v, double z) {
		double x = center.getX() + radius * Math.cos(angle);
		double y = center.getY() + radius * Math.sin(angle);
		return new Vertex3D(x, y, z, Color.WHITE.scale(v));
	}

}
