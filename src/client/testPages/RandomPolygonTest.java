package client.testPages;

import java.util.Random;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class RandomPolygonTest {

	private final PolygonRenderer renderer;
	private final Drawable panel;

	static int seed = (int) (Math.random() * 100000);

	public RandomPolygonTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;

		render();
	}

	private void render() {
		Random rand = new Random();
		rand.setSeed(seed);

		for (int i = 0; i < 20; i++) {
			Vertex3D p0 = new Vertex3D(randx(rand), randy(rand), 0, Color.WHITE);
			Vertex3D p1 = new Vertex3D(randx(rand), randy(rand), 0, Color.WHITE);
			Vertex3D p2 = new Vertex3D(randx(rand), randy(rand), 0, Color.WHITE);

			Vertex3D[] points = { p0, p1, p2 };
			Polygon poly = Polygon.make(points);
			renderer.drawPolygon(poly, panel);
		}

	}

	private int randx(Random rand) {

		int width = panel.getWidth();
		int random = (int) (rand.nextDouble() * width);
		// System.out.println("random: " +random);
		return random;
	}

	private int randy(Random rand) {
		int height = panel.getHeight();
		int random = (int) (rand.nextDouble() * height);
		// System.out.println("random: " +random);
		return random;
	}
}
