package windowing.drawable;

public class ZBufferingDrawable extends DrawableDecorator {

	private int row;
	private int col;
	private double[][] zbuffer;

	public ZBufferingDrawable(Drawable delegate) {
		super(delegate);
		row = delegate.getHeight();
		col = delegate.getWidth();
		zbuffer = new double[row][col];
		reset_z();

	}

	@Override
	public void clear() {
		fill(ARGB_BLACK, Double.MAX_VALUE);
		reset_z();
	}

	@Override
	public void setPixel(int x, int y, double z, int argbColor) {

		if ((x > 0 && x < col) && (y > 0 && y < row)) {
			if (z >= zbuffer[y][x]) {
				delegate.setPixel(x, y, z, argbColor);
				zbuffer[y][x] = z;
			}
		}
	}

	private void reset_z() {
		for (int y = 0; y < row; y++) {
			for (int x = 0; x < col; x++) {
				zbuffer[y][x] = -200;
			}
		}
	}

}
