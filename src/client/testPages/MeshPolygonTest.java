package client.testPages;

import java.util.Random;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class MeshPolygonTest {

	public static int NO_PERTURBATION = 0;
	public static int USE_PERTURBATION = 1;
	private final PolygonRenderer renderer;
	private final Drawable panel;
	private int perturbation;

	static long seed = (int) (Math.random() * 100000);

	public MeshPolygonTest(Drawable panel, PolygonRenderer renderer, int perturbation) {
		this.panel = panel;
		this.renderer = renderer;
		this.perturbation = perturbation;

		render();
	}

	private void render() {

		int height = panel.getHeight();
		int width = panel.getWidth();
		int border = (height / 10) / 2;
		int tridim = height / 10;
		int tris = (width - (border * 2)) / tridim;

		Random rand = new Random();
		rand.setSeed(seed);

		double rand_x = 0.0;
		double rand_y = 0.0;

		// double random = (Math.random()*2-1) * perturbation;
		// System.out.println("random: " +random);

		int x = border;
		int y = height - border;

		int grid[][][] = new int[tris + 1][tris + 1][2];
		Vertex3D[][] vertex_grid = new Vertex3D[tris + 1][tris + 2];

		for (int row = 0; row < tris + 1; row++) {

			for (int col = 0; col < tris + 1; col++) {
				rand_x = ((rand.nextDouble() * 2 - 1) * (border / 2)) * perturbation;
				rand_y = ((rand.nextDouble() * 2 - 1) * (border / 2)) * perturbation;

				grid[row][col][0] = (int) (x + rand_x);
				grid[row][col][1] = (int) (y + rand_y);

				x += tridim;
			}

			x = border;
			y -= tridim;
		}

		for (int i = 0; i < tris + 1; i++) {

			for (int j = 0; j < tris + 1; j++) {
				vertex_grid[i][j] = new Vertex3D(grid[i][j][0], grid[i][j][1], 0, Color.random());
			}

		}

		for (int i = 0; i < tris; i++) {
			for (int j = 0; j < tris; j++) {
				Vertex3D p0 = vertex_grid[i][j];
				Vertex3D p1 = vertex_grid[i][j + 1];
				Vertex3D p2 = vertex_grid[i + 1][j];
				Vertex3D p3 = vertex_grid[i + 1][j + 1];
				Polygon poly1 = Polygon.make(p0, p1, p2);
				Polygon poly2 = Polygon.make(p1, p2, p3);

				poly1 = Polygon.makeEnsuringClockwise(poly1.get(0), poly1.get(1), poly1.get(2));
				renderer.drawPolygon(poly1, panel);
				renderer.drawPolygon(poly2, panel);
			}

		}
	}

}
