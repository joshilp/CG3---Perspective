package client.testPages;

import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class ParallelogramTest {

	private final LineRenderer renderer;
	private final Drawable panel;
	Vertex3D center;

	public ParallelogramTest(Drawable panel, LineRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;

		render();
	}

	private void render() {
		int top = panel.getHeight();
		for (int p = 0; p <= 50; p++) {
			Vertex3D p1_start = new Vertex3D(20, top - 80 - p, 0, Color.WHITE);
			Vertex3D p1_end = new Vertex3D(150, top - 150 - p, 0, Color.WHITE);
			renderer.drawLine(p1_start, p1_end, panel);

			Vertex3D p2_start = new Vertex3D(160 + p, top - 270, 0, Color.WHITE);
			Vertex3D p2_end = new Vertex3D(240 + p, top - 40, 0, Color.WHITE);
			renderer.drawLine(p2_start, p2_end, panel);
		}
	}
}

// (20, 80+p) to (150, 150+p)
// For p = 0 to 50.
//
// Also draw the lines:
//
// (160+p, 270) to (240+p, 40)
//
// Again, for p = 0 to 50.