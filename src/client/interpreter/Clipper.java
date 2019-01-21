package client.interpreter;

import geometry.Vertex3D;
import polygon.Chain;
import polygon.Polygon;
import java.util.ArrayList;

public class Clipper {

	private Vertex3D[] vertices;
	private double front;
	private double back;
	private int screenleft;
	private int screenright;
	private int screentop;
	private int screenbottom;

	public Clipper(double front, double back, int left, int right, int top, int bottom) {
		this.front = front;
		this.back = back;
		this.screenleft = left;
		this.screenright = right;
		this.screentop = top;
		this.screenbottom = bottom;
	}

	public Chain clip_z(Chain verts) {
		Chain front = clip_front(verts);
		Chain back = clip_back(front);
		return back;
	}

	public Chain clip_xy(Chain verts) {
		Chain left = clip_left(verts);
		Chain right = clip_right(left);
		Chain top = clip_top(right);
		Chain bottom = clip_bottom(top);
		return bottom;
	}

	public Chain clip_front(Chain verts) {
		Chain vertices = new Chain();
		int n = verts.length();

		for (int i = 0; i < n; i++) {
			Vertex3D p1 = verts.get(i);
			Vertex3D p2 = verts.get((i + 1) % n);

			double dx = p2.getX() - p1.getX();
			double dy = p2.getY() - p1.getY();
			double dz = p2.getZ() - p1.getZ();

			double r = (front - p1.getZ()) / dz;
			double ix = p1.getX() + r * dx;
			double iy = p1.getY() + r * dy;
			double iz = front;

			Vertex3D pi = new Vertex3D(ix, iy, iz, p1.getColor());

			// Inside Inside
			if (p1.getZ() <= front && p2.getZ() <= front) {
				vertices.add(p2);
			}

			// Inside Outside
			if (p1.getZ() <= front && p2.getZ() > front) {
				vertices.add(pi);
			}

			// Outside Outside - do nothing

			// Outside Inside
			if (p1.getZ() > front && p2.getZ() <= front) {
				vertices.add(pi);
				vertices.add(p2);
			}
		}
		return vertices;
	}

	public Chain clip_back(Chain verts) {
		Chain vertices = new Chain();
		int n = verts.length();

		for (int i = 0; i < n; i++) {
			Vertex3D p1 = verts.get(i);
			Vertex3D p2 = verts.get((i + 1) % n);

			double dx = p2.getX() - p1.getX();
			double dy = p2.getY() - p1.getY();
			double dz = p2.getZ() - p1.getZ();

			double r = (back - p1.getZ()) / dz;
			double ix = p1.getX() + r * dx;
			double iy = p1.getY() + r * dy;
			double iz = back;

			Vertex3D pi = new Vertex3D(ix, iy, iz, p1.getColor());

			// Inside Inside
			if (p1.getZ() >= back && p2.getZ() >= back) {
				vertices.add(p2);
			}

			// Inside Outside
			if (p1.getZ() >= back && p2.getZ() < back) {
				vertices.add(pi);
			}

			// Outside Outside - do nothing

			// Outside Inside
			if (p1.getZ() < back && p2.getZ() >= back) {
				vertices.add(pi);
				vertices.add(p2);
			}
		}
		return vertices;
	}

	private double intercept_z(Vertex3D p0, Vertex3D p1, double r) {
		double z0_den = 1 / p0.getZ();
		double z1_den = 1 / p1.getZ();
		double z = 1 / (z0_den + r * (z1_den - z0_den));
		return z;
	}

	public Chain clip_left(Chain verts) {
		Chain vertices = new Chain();
		int n = verts.length();

		for (int i = 0; i < n; i++) {
			Vertex3D p1 = verts.get(i);
			Vertex3D p2 = verts.get((i + 1) % n);

			double dx = p2.getX() - p1.getX();
			double dy = p2.getY() - p1.getY();
			double dz = p2.getZ() - p1.getZ();

			double r = (screenleft - p1.getX()) / dx;
			double ix = screenleft;
			double iy = p1.getY() + r * dy;
			// double iz = p1.getZ() + r * dz;
			double iz = intercept_z(p1, p2, r);

			Vertex3D pi = new Vertex3D(ix, iy, iz, p1.getColor());

			// Inside Inside
			if (p1.getX() >= screenleft && p2.getX() >= screenleft) {
				vertices.add(p2);
			}

			// Inside Outside
			if (p1.getX() >= screenleft && p2.getX() < screenleft) {
				vertices.add(pi);
			}

			// Outside Outside - do nothing

			// Outside Inside
			if (p1.getX() < screenleft && p2.getX() >= screenleft) {
				vertices.add(pi);
				vertices.add(p2);
			}
		}
		return vertices;
	}

	public Chain clip_right(Chain verts) {
		Chain vertices = new Chain();
		int n = verts.length();

		for (int i = 0; i < n; i++) {
			Vertex3D p1 = verts.get(i);
			Vertex3D p2 = verts.get((i + 1) % n);

			double dx = p2.getX() - p1.getX();
			double dy = p2.getY() - p1.getY();
			double dz = p2.getZ() - p1.getZ();

			double r = (screenright - p1.getX()) / dx;
			double ix = screenright;
			double iy = p1.getY() + r * dy;
			// double iz = p1.getZ() + r * dz;
			double iz = intercept_z(p1, p2, r);

			Vertex3D pi = new Vertex3D(ix, iy, iz, p1.getColor());

			// Inside Inside
			if (p1.getX() <= screenright && p2.getX() <= screenright) {
				vertices.add(p2);
			}

			// Inside Outside
			if (p1.getX() <= screenright && p2.getX() > screenright) {
				vertices.add(pi);
			}

			// Outside Outside - do nothing

			// Outside Inside
			if (p1.getX() > screenright && p2.getX() <= screenright) {
				vertices.add(pi);
				vertices.add(p2);
			}
		}
		return vertices;
	}

	public Chain clip_top(Chain verts) {
		Chain vertices = new Chain();
		int n = verts.length();

		for (int i = 0; i < n; i++) {
			Vertex3D p1 = verts.get(i);
			Vertex3D p2 = verts.get((i + 1) % n);

			double dx = p2.getX() - p1.getX();
			double dy = p2.getY() - p1.getY();
			double dz = p2.getZ() - p1.getZ();

			double r = (screentop - p1.getY()) / dy;
			double ix = p1.getX() + r * dx;
			double iy = screentop;
			// double iz = p1.getZ() + r * dz;
			double iz = intercept_z(p1, p2, r);

			Vertex3D pi = new Vertex3D(ix, iy, iz, p1.getColor());

			// Inside Inside
			if (p1.getY() <= screentop && p2.getY() <= screentop) {
				vertices.add(p2);
			}

			// Inside Outside
			if (p1.getY() <= screentop && p2.getY() > screentop) {
				vertices.add(pi);
			}

			// Outside Outside - do nothing

			// Outside Inside
			if (p1.getY() > screentop && p2.getY() <= screentop) {
				vertices.add(pi);
				vertices.add(p2);
			}
		}
		return vertices;
	}

	public Chain clip_bottom(Chain verts) {
		Chain vertices = new Chain();
		int n = verts.length();

		for (int i = 0; i < n; i++) {
			Vertex3D p1 = verts.get(i);
			Vertex3D p2 = verts.get((i + 1) % n);

			double dx = p2.getX() - p1.getX();
			double dy = p2.getY() - p1.getY();
			double dz = p2.getZ() - p1.getZ();

			double r = (screenbottom - p1.getY()) / dy;
			double ix = p1.getX() + r * dx;
			double iy = screenbottom;
			// double iz = p1.getZ() + r * dz;
			double iz = intercept_z(p1, p2, r);

			Vertex3D pi = new Vertex3D(ix, iy, iz, p1.getColor());

			// Inside Inside
			if (p1.getY() >= screenbottom && p2.getY() >= screenbottom) {
				vertices.add(p2);
			}

			// Inside Outside
			if (p1.getY() >= screenbottom && p2.getY() < screenbottom) {
				vertices.add(pi);
			}

			// Outside Outside - do nothing

			// Outside Inside
			if (p1.getY() < screenbottom && p2.getY() >= screenbottom) {
				vertices.add(pi);
				vertices.add(p2);
			}
		}
		return vertices;
	}

}
