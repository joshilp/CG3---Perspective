package windowing.drawable;

public class ColoredDrawable extends DrawableDecorator {

	private int color;

	public ColoredDrawable(Drawable delegate, int inputcolor) {
		super(delegate);
		// TODO Auto-generated constructor stub
		this.color = inputcolor;
	}

	@Override
	public void clear() {
		fill(color, Double.MAX_VALUE);
	}

}
