package minicraft.graphic;

public class Insets {

	public int left, top, right, bottom;

	public Insets() {
		this(0);
	}

	public Insets(int dist) {
		this(dist, dist, dist, dist);
	}

	public Insets(int left, int top, int right, int bottom) {
		this.left = left;
		this.top = top;
		this.right = right;
		this.bottom = bottom;
	}

	public Rectangle addTo(Rectangle rectangle) {
		return new Rectangle(rectangle.getLeft() - left, rectangle.getTop() - top, rectangle.getRight() + right, rectangle.getBottom() + bottom, Rectangle.CORNERS);
	}

	public Rectangle subtractFrom(Rectangle rectangle) {
		return new Rectangle(rectangle.getLeft() + left, rectangle.getTop() + top, rectangle.getRight() - right, rectangle.getBottom() - bottom, Rectangle.CORNERS);
	}

	public Dimension addTo(Dimension dimension) {
		return new Dimension(dimension.width + left + right, dimension.height + top + bottom);
	}

	public Dimension subtractFrom(Dimension dimension) {
		return new Dimension(dimension.width - left - right, dimension.height - top - bottom);
	}

	public Insets addInsets(Insets insets) {
		return new Insets(left + insets.left, top + insets.top, right + insets.right, bottom + insets.bottom);
	}

	public Insets subtractInsets(Insets s) {
		return new Insets(left - s.left, top - s.top, right - s.right, bottom - s.bottom);
	}

	public String toString() {
		return super.toString() + "[left=" + left + ",top=" + top + ",right=" + right + ",bottom=" + bottom + "]";
	}
}
