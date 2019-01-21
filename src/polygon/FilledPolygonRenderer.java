package polygon;

import java.awt.Panel;

import geometry.Vertex3D;
import line.DDALineRenderer;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class FilledPolygonRenderer implements PolygonRenderer {

	private FilledPolygonRenderer() {
	}

	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader) {

		LineRenderer DDAdrawer = DDALineRenderer.make();

		polygon = Polygon.makeEnsuringClockwise(polygon.get(0), polygon.get(1), polygon.get(2));

		Chain LChain = polygon.leftChain();
		Chain RChain = polygon.rightChain();

		int lengthL = LChain.numVertices;
		int lengthR = RChain.numVertices;
		// System.out.println("LChain: " + lengthR + " RChain: " + lengthR);

		if ((lengthL + lengthR) >= 3) {

			Vertex3D p0 = RChain.vertices.get(0);
			Vertex3D p1 = LChain.vertices.get(1);
			Vertex3D p2 = RChain.vertices.get(1);

			int p0_x = p0.getIntX();
			int p0_y = p0.getIntY();
			// System.out.println("p0: (" + p0_x + "," + p0_y + ")");

			int p1_x = p1.getIntX();
			int p1_y = p1.getIntY();
			// System.out.println("p1: (" + p1_x + "," + p1_y + ")");

			int p2_x = p2.getIntX();
			int p2_y = p2.getIntY();
			// System.out.println("p2: (" + p2_x + "," + p2_y + ")");

			double dx_left = p0_x - p1_x;
			double dy_left = p0_y - p1_y;
			double m_left = dx_left / dy_left;

			double dx_right = p2_x - p0_x;
			double dy_right = p2_y - p0_y;
			double m_right = dx_right / dy_right;

			double dx_low = p1_x - p2_x;
			double dy_low = p1_y - p2_y;
			double m_low = dx_low / dy_low;

			double y_middle = Math.max(p1_y, p2_y);
			double y_bottom = Math.min(p1_y, p2_y);

			double fx_left = p0_x;
			double fx_right = p0_x;

			int xleft = p0_x;
			int xright = p0_x;

			if (dy_left == 0) {
				fx_left = p1_x;
			}

			if (dy_right == 0) {
				fx_right = p2_x;
			}

			Color color = Color.random();

			for (int y = p0_y; y > y_bottom; y--) {
				xleft = (int) Math.round(fx_left);
				xright = ((int) Math.round(fx_right));

				if (xleft <= xright - 1) {
					Vertex3D v3d_xleft = new Vertex3D(xleft, y, 0, color);
					Vertex3D v3d_xright = new Vertex3D(xright - 1, y, 0, color);
					DDAdrawer.drawLine(v3d_xleft, v3d_xright, drawable);

				}

				if (y > y_middle) {
					fx_left -= m_left;
					fx_right -= m_right;
				}

				if (y <= y_middle && p1_y > p2_y) {
					fx_left -= m_low;
					fx_right -= m_right;
				}

				if (y <= y_middle && p1_y < p2_y) {
					fx_left -= m_left;
					fx_right -= m_low;
				}

			}

		}
	}

	public static PolygonRenderer make() {
		return new FilledPolygonRenderer();
	}

}
