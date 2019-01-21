package polygon;

import java.awt.Panel;
import java.util.List;

import geometry.Vertex3D;
import line.DDALineRenderer;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class WireFramePolygonRenderer implements PolygonRenderer {

	private WireFramePolygonRenderer() {
	}

	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader) {
		LineRenderer DDAdrawer = DDALineRenderer.make();

		int n = polygon.length();

		for (int i = 0; i < n; i++) {
			DDAdrawer.drawLine(polygon.get(i), polygon.get((i + 1) % n), drawable);
		}

	}

	public static PolygonRenderer make() {
		return new WireFramePolygonRenderer();
	}

}
