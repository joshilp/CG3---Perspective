package client.testPages;

import java.util.Random;

import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class RandomLineTest {

	private final LineRenderer renderer;
	private final Drawable panel;

	static Random rand = new Random();
	static long seed = rand.nextInt(100000);

	public RandomLineTest(Drawable panel, LineRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;

		render();
	}

	private void render() {
		int height = panel.getHeight();
		int width = panel.getWidth();

		rand.setSeed(seed);

		int rand_x = 0;
		int rand_y = 0;

		for (int p = 0; p <= 50; p++) {

			rand_x = (int) (rand.nextDouble() * width);
			rand_y = (int) (rand.nextDouble() * height);
			Vertex3D p1 = new Vertex3D(rand_x, height - rand_y, 0, Color.random(rand));

			rand_x = (int) (rand.nextDouble() * width);
			rand_y = (int) (rand.nextDouble() * height);
			Vertex3D p2 = new Vertex3D(rand_x, height - rand_y, 0, Color.random(rand));

			renderer.drawLine(p1, p2, panel);

		}
	}
}