package client.interpreter;

import line.DDALineRenderer;
import line.LineRenderer;
import polygon.FilledPolygonRenderer;
import polygon.PolygonRenderer;
import polygon.ColorPolygonRenderer;
import polygon.WireFramePolygonRenderer;

public class RendererTrio {

	public static LineRenderer getLineRenderer() {
		return DDALineRenderer.make();
	}

	public static PolygonRenderer getFilledRenderer() {
		return ColorPolygonRenderer.make();
	}

	public static PolygonRenderer getWireframeRenderer() {
		return WireFramePolygonRenderer.make();
	}

}