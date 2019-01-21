package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class DDALineRenderer implements LineRenderer {

	private DDALineRenderer() {
	}

	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {

		double deltaX = p2.getIntX() - p1.getIntX();
		double deltaY = p2.getIntY() - p1.getIntY();
		double deltaZ = 1.0 / p2.getZ() - 1 / p1.getZ();

		double deltaR = p2.getColor().getR() / p2.getZ() - p1.getColor().getR() / p1.getZ();
		double deltaG = p2.getColor().getG() / p2.getZ() - p1.getColor().getG() / p1.getZ();
		double deltaB = p2.getColor().getB() / p2.getZ() - p1.getColor().getB() / p1.getZ();

		double slope = deltaY / deltaX;
		double slopeZ = deltaZ / deltaX;

		double slopeR = deltaR / deltaX;
		double slopeG = deltaG / deltaX;
		double slopeB = deltaB / deltaX;

		// double intercept = p2.getIntY() - slope * p2.getIntX();

		// int argbColor = p1.getColor().asARGB();

		double r = p1.getColor().getR() / p1.getZ();
		double g = p1.getColor().getG() / p1.getZ();
		double b = p1.getColor().getB() / p1.getZ();

		double y = p1.getIntY();
		double z = 1.0 / p1.getZ();

		for (int x = p1.getIntX(); x <= p2.getIntX(); x++) {
			Color rgb = new Color(r / z, g / z, b / z);
			drawable.setPixel(x, (int) Math.round(y), 1 / z, rgb.asARGB());
			y = y + slope;
			z = z + slopeZ;
			r = r + slopeR;
			g = g + slopeG;
			b = b + slopeB;
		}
	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new DDALineRenderer());
	}
}
