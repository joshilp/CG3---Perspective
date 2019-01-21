package polygon;

import java.awt.Panel;

import geometry.Vertex3D;
import line.DDALineRenderer;
import line.LineRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class ColorPolygonRenderer implements PolygonRenderer {

	private static int X = 0;
	private static int Y = 1;
	private static int Z = 2;

	private static int R = 0;
	private static int G = 1;
	private static int B = 2;

	private static int DX = 0;
	private static int DY = 1;
	private static int DZ = 2;

	private static int DR = 3;
	private static int DG = 4;
	private static int DB = 5;

	private static int MX = 0;
	private static int MZ = 1;
	private static int MR = 2;
	private static int MG = 3;
	private static int MB = 4;

	private ColorPolygonRenderer() {
	}

	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader) {

		LineRenderer DDAdrawer = DDALineRenderer.make();

		// polygon =
		// Polygon.makeEnsuringClockwise(polygon.get(0),polygon.get(1),polygon.get(2));

		Chain LChain = polygon.leftChain();
		Chain RChain = polygon.rightChain();

		int lengthL = LChain.numVertices;
		int lengthR = RChain.numVertices;
		// System.out.println("LChain: " + lengthR + " RChain: " + lengthR);

		if ((lengthL + lengthR) >= 3) {

			Vertex3D p0 = RChain.vertices.get(0);
			Vertex3D p1 = LChain.vertices.get(1);
			Vertex3D p2 = RChain.vertices.get(1);

			double[] p0_xyz = get_xyz(p0);
			double[] p1_xyz = get_xyz(p1);
			double[] p2_xyz = get_xyz(p2);

			double[] p0_rgb = get_rgb(p0);
			double[] p1_rgb = get_rgb(p1);
			double[] p2_rgb = get_rgb(p2);

			double[] d_left = get_d(p0, p1);
			double[] d_right = get_d(p2, p0);
			double[] d_low = get_d(p1, p2);

			double[] m_left = get_m(p0, p1);
			double[] m_right = get_m(p2, p0);
			double[] m_low = get_m(p1, p2);

			double y_middle = Math.max(p1_xyz[Y], p2_xyz[Y]);
			double y_bottom = Math.min(p1_xyz[Y], p2_xyz[Y]);

			double fx_left = p0_xyz[X];
			double fx_right = p0_xyz[X];
			double fz_left = p0_xyz[Z];
			double fz_right = p0_xyz[Z];

			int xleft = (int) p0_xyz[X];
			int xright = (int) p0_xyz[X];

			double[] rgb_left = get_rgb(p0);
			double[] rgb_right = get_rgb(p0);

			if (d_left[DY] == 0) {
				fx_left = p1_xyz[X];
				rgb_left[R] = p1_rgb[R];
				rgb_left[G] = p1_rgb[G];
				rgb_left[B] = p1_rgb[B];
				fz_left = p1_xyz[Z];
			}

			if (d_right[DY] == 0) {
				fx_right = p2_xyz[X];
				rgb_right[R] = p2_rgb[R];
				rgb_right[G] = p2_rgb[G];
				rgb_right[B] = p2_rgb[B];
				fz_right = p2_xyz[Z];
			}

			for (int y = (int) p0_xyz[Y]; y > y_bottom; y--) {
				Color color_left = new Color(rgb_left[R] / fz_left, rgb_left[G] / fz_left, rgb_left[B] / fz_left);
				Color color_right = new Color(rgb_right[R] / fz_right, rgb_right[G] / fz_right,
						rgb_right[B] / fz_right);

				// System.out.println("L:" +color_left.getR() +" R: " +color_right.getR());
				// System.out.println("fz_left: " +fz_left +" fz_right: " +fz_right);

				xleft = (int) Math.round(fx_left);
				xright = (int) Math.round(fx_right);

				if (xleft <= xright - 1) {
					Vertex3D v3d_xleft = new Vertex3D(xleft, y, 1.0 / fz_left, vertexShader.shade(color_left));
					Vertex3D v3d_xright = new Vertex3D(xright - 1, y, 1.0 / fz_right, vertexShader.shade(color_right));
					DDAdrawer.drawLine(v3d_xleft, v3d_xright, drawable);
				}

				if (y > y_middle) {
					fx_left -= m_left[MX];
					fx_right -= m_right[MX];

					rgb_left[R] -= m_left[MR];
					rgb_left[G] -= m_left[MG];
					rgb_left[B] -= m_left[MB];

					rgb_right[R] -= m_right[MR];
					rgb_right[G] -= m_right[MG];
					rgb_right[B] -= m_right[MB];

					fz_left -= m_left[MZ];
					fz_right -= m_right[MZ];
				}

				if (y <= y_middle && p1_xyz[Y] > p2_xyz[Y]) {
					fx_left -= m_low[MX];
					fx_right -= m_right[MX];

					rgb_left[R] -= m_low[MR];
					rgb_left[G] -= m_low[MG];
					rgb_left[B] -= m_low[MB];

					rgb_right[R] -= m_right[MR];
					rgb_right[G] -= m_right[MG];
					rgb_right[B] -= m_right[MB];

					fz_left -= m_low[MZ];
					fz_right -= m_right[MZ];
				}

				if (y <= y_middle && p1_xyz[Y] < p2_xyz[Y]) {
					fx_left -= m_left[MX];
					fx_right -= m_low[MX];

					rgb_left[R] -= m_left[MR];
					rgb_left[G] -= m_left[MG];
					rgb_left[B] -= m_left[MB];

					rgb_right[R] -= m_low[MR];
					rgb_right[G] -= m_low[MG];
					rgb_right[B] -= m_low[MB];

					fz_left -= m_left[MZ];
					fz_right -= m_low[MZ];
				}

			}

		}
	}

	public static PolygonRenderer make() {
		return new ColorPolygonRenderer();
	}

	double[] get_xyz(Vertex3D point) {
		double[] xyz = new double[3];

		xyz[X] = point.getIntX();
		xyz[Y] = point.getIntY();
		xyz[Z] = 1.0 / point.getZ();

		return xyz;
	}

	double[] get_rgb(Vertex3D point) {
		double[] rgb = new double[3];
		double[] xyz = get_xyz(point);

		rgb[R] = point.getColor().getR() * xyz[Z];
		rgb[G] = point.getColor().getG() * xyz[Z];
		rgb[B] = point.getColor().getB() * xyz[Z];

		return rgb;
	}

	double[] get_d(Vertex3D point1, Vertex3D point2) {
		double[] p1_xyz = get_xyz(point1);
		double[] p2_xyz = get_xyz(point2);
		double[] p1_rgb = get_rgb(point1);
		double[] p2_rgb = get_rgb(point2);
		double[] d = new double[6];

		d[DX] = p1_xyz[X] - p2_xyz[X];
		d[DY] = p1_xyz[Y] - p2_xyz[Y];
		d[DZ] = p1_xyz[Z] - p2_xyz[Z];

		d[DR] = p1_rgb[R] - p2_rgb[R];
		d[DG] = p1_rgb[G] - p2_rgb[G];
		d[DB] = p1_rgb[B] - p2_rgb[B];

		return d;
	}

	double[] get_m(Vertex3D point1, Vertex3D point2) {
		double[] d = get_d(point1, point2);
		double[] m = new double[5];

		m[MX] = d[DX] / d[DY];
		m[MZ] = d[DZ] / d[DY];
		m[MR] = d[DR] / d[DY];
		m[MG] = d[DG] / d[DY];
		m[MB] = d[DB] / d[DY];

		return m;
	}

}
