package line;

import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class AntialiasingLineRenderer implements LineRenderer {

	private static final double width = 0.5;

	private AntialiasingLineRenderer() {
	}

	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		double deltaX = p2.getIntX() - p1.getIntX();
		double deltaY = p2.getIntY() - p1.getIntY();

		double m = deltaY / deltaX;
		double c = p2.getIntY() - m * p2.getIntX();

		double y = p1.getIntY();

		double d = 0;
		double width = 0.5;

		Color color = p1.getColor();

		int newcolor = 0;
		int oldcolor = 0;

		for (int x = p1.getIntX(); x <= p2.getIntX(); x++) {
			d = distance(x, Math.round(y), m, c);
			d = d - width;
			newcolor = newcolor(d, color);
			oldcolor = drawable.getPixel(x, (int) Math.round(y));
			drawable.setPixel(x, (int) Math.round(y), 0.0, Math.max(oldcolor, newcolor));

			d = distance(x, Math.round(y + 1), m, c);
			d = d - width;
			newcolor = newcolor(d, color);
			oldcolor = drawable.getPixel(x, (int) Math.round(y + 1));
			drawable.setPixel(x, (int) Math.round(y + 1), 0.0, Math.max(oldcolor, newcolor));

			d = distance(x, Math.round(y - 1), m, c);
			d = d - width;
			newcolor = newcolor(d, color);
			oldcolor = drawable.getPixel(x, (int) Math.round(y - 1));
			drawable.setPixel(x, (int) Math.round(y - 1), 0.0, Math.max(oldcolor, newcolor));

			d = distance(x, Math.round(y + 2), m, c);
			d = d - width;
			newcolor = newcolor(d, color);
			oldcolor = drawable.getPixel(x, (int) Math.round(y + 2));
			drawable.setPixel(x, (int) Math.round(y + 2), 0.0, Math.max(oldcolor, newcolor));

			d = distance(x, Math.round(y - 2), m, c);
			d = d - width;
			newcolor = newcolor(d, color);
			oldcolor = drawable.getPixel(x, (int) Math.round(y - 2));
			drawable.setPixel(x, (int) Math.round(y - 2), 0.0, Math.max(oldcolor, newcolor));

			y = y + m;
		}
	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new AntialiasingLineRenderer());
	}

	private double distance(int x, double yval, double slope, double intercept) {
		double m = slope;
		double c = intercept;
		double b = -1.0;
		int y = (int) yval;

		double d = Math.abs(m * x + b * y + c) / Math.sqrt(Math.pow(m, 2) + 1);
		return d;
	}

	private int newcolor(double d, Color color) {
		double r = 0.5;
		double theta = Math.acos(d / r);

		double scale = 1.0 - ((1.0 - theta / Math.PI)
				+ d * (Math.sqrt((Math.pow(r, 2)) - (Math.pow(d, 2))) / (Math.PI * Math.pow(r, 2))));

		return color.scale(scale).asARGB();
	}

}
