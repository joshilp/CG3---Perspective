package geometry;

import windowing.graphics.Color;

public class Transformation {

	private double[][] matrix;
	private static final int N = 4;

	public Transformation() {
		this.matrix = new double[N][N];
		makeIdentity();
	}

	private void makeIdentity() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (i == j) {
					matrix[i][j] = 1;
				} else {
					matrix[i][j] = 0;
				}
			}
		}
	}

	private void init_matrix(double[][] v, int value) {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				if (i == j) {
					v[i][j] = value;
				} else {
					v[i][j] = 0;
				}
			}
		}
	}

	private double[][] multiply(double[][] a, double[][] b, boolean flag) {
		double[][] new_matrix = new double[N][N];
		init_matrix(new_matrix, 0);

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				for (int k = 0; k < 4; k++) {
					if (flag == false) {
						new_matrix[i][j] += a[i][k] * b[k][j];
					} // for premult
					else {
						new_matrix[i][j] += b[i][k] * a[k][j];
					} // for postmult
				}
			}
		}
		return new_matrix;
	}

	public void translate(double tx, double ty, double tz, boolean flag) {
		double[][] t = new double[N][N];
		init_matrix(t, 1);
		t[0][3] = tx;
		t[1][3] = ty;
		t[2][3] = tz;

		matrix = multiply(t, matrix, flag);
	}

	public void rotateX(double a, boolean flag) {
		double[][] r = new double[N][N];
		init_matrix(r, 1);
		r[1][1] = Math.cos(a);
		r[1][2] = -Math.sin(a);
		r[2][1] = Math.sin(a);
		r[2][2] = Math.cos(a);

		matrix = multiply(r, matrix, flag);
	}

	public void rotateY(double a, boolean flag) {
		double[][] r = new double[N][N];
		init_matrix(r, 1);
		r[0][0] = Math.cos(a);
		r[0][2] = Math.sin(a);
		r[2][0] = -Math.sin(a);
		r[2][2] = Math.cos(a);

		matrix = multiply(r, matrix, flag);
	}

	public void rotateZ(double a, boolean flag) {
		double[][] r = new double[N][N];
		init_matrix(r, 1);
		r[0][0] = Math.cos(a);
		r[0][1] = -Math.sin(a);
		r[1][0] = Math.sin(a);
		r[1][1] = Math.cos(a);

		matrix = multiply(r, matrix, flag);
	}

	public void scale(double sx, double sy, double sz, boolean flag) {
		double[][] s = new double[N][N];
		init_matrix(s, 1);
		s[0][0] = sx;
		s[1][1] = sy;
		s[2][2] = sz;
		s[3][3] = 1;

		matrix = multiply(s, matrix, flag);
	}

	public void printMatrix() {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(matrix[i][j] + "	");
			}
			System.out.println("");
		}
		System.out.println("");
	}

	public void printTransform(double[][] trans) {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(trans[i][j] + "	");
			}
			System.out.println("");
		}
		System.out.println("");
	}

	public static Transformation identity() {
		return new Transformation();
	}

	public Vertex3D transformV3D(Vertex3D a) {
		double[][] new_matrix = { { 0 }, { 0 }, { 0 }, { 0 } };
		double[][] b = new double[N][1];
		b[0][0] = a.getX();
		b[1][0] = a.getY();
		b[2][0] = a.getZ();
		b[3][0] = 1;

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 1; j++) {
				for (int k = 0; k < 4; k++) {
					new_matrix[i][j] += matrix[i][k] * b[k][j];
				}
			}
		}

		return new Vertex3D(new_matrix[0][0], new_matrix[1][0], new_matrix[2][0], a.getColor());
	}

	public void copy(Transformation copy_from) {
		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				this.matrix[i][j] = copy_from.matrix[i][j];
			}
		}
	}

	public void invert() {
		double[][] inverted = new double[4][4];

		inverted[0][0] = (matrix[1][2] * matrix[2][3] * matrix[3][1]) - (matrix[1][3] * matrix[2][2] * matrix[3][1])
				+ (matrix[1][3] * matrix[2][1] * matrix[3][2]) - (matrix[1][1] * matrix[2][3] * matrix[3][2])
				- (matrix[1][2] * matrix[2][1] * matrix[3][3]) + (matrix[1][1] * matrix[2][2] * matrix[3][3]);
		inverted[0][1] = (matrix[0][3] * matrix[2][2] * matrix[3][1]) - (matrix[0][2] * matrix[2][3] * matrix[3][1])
				- (matrix[0][3] * matrix[2][1] * matrix[3][2]) + (matrix[0][1] * matrix[2][3] * matrix[3][2])
				+ (matrix[0][2] * matrix[2][1] * matrix[3][3]) - (matrix[0][1] * matrix[2][2] * matrix[3][3]);
		inverted[0][2] = (matrix[0][2] * matrix[1][3] * matrix[3][1]) - (matrix[0][3] * matrix[1][2] * matrix[3][1])
				+ (matrix[0][3] * matrix[1][1] * matrix[3][2]) - (matrix[0][1] * matrix[1][3] * matrix[3][2])
				- (matrix[0][2] * matrix[1][1] * matrix[3][3]) + (matrix[0][1] * matrix[1][2] * matrix[3][3]);
		inverted[0][3] = (matrix[0][3] * matrix[1][2] * matrix[2][1]) - (matrix[0][2] * matrix[1][3] * matrix[2][1])
				- (matrix[0][3] * matrix[1][1] * matrix[2][2]) + (matrix[0][1] * matrix[1][3] * matrix[2][2])
				+ (matrix[0][2] * matrix[1][1] * matrix[2][3]) - (matrix[0][1] * matrix[1][2] * matrix[2][3]);
		inverted[1][0] = (matrix[1][3] * matrix[2][2] * matrix[3][0]) - (matrix[1][2] * matrix[2][3] * matrix[3][0])
				- (matrix[1][3] * matrix[2][0] * matrix[3][2]) + (matrix[1][0] * matrix[2][3] * matrix[3][2])
				+ (matrix[1][2] * matrix[2][0] * matrix[3][3]) - (matrix[1][0] * matrix[2][2] * matrix[3][3]);
		inverted[1][1] = (matrix[0][2] * matrix[2][3] * matrix[3][0]) - (matrix[0][3] * matrix[2][2] * matrix[3][0])
				+ (matrix[0][3] * matrix[2][0] * matrix[3][2]) - (matrix[0][0] * matrix[2][3] * matrix[3][2])
				- (matrix[0][2] * matrix[2][0] * matrix[3][3]) + (matrix[0][0] * matrix[2][2] * matrix[3][3]);
		inverted[1][2] = (matrix[0][3] * matrix[1][2] * matrix[3][0]) - (matrix[0][2] * matrix[1][3] * matrix[3][0])
				- (matrix[0][3] * matrix[1][0] * matrix[3][2]) + (matrix[0][0] * matrix[1][3] * matrix[3][2])
				+ (matrix[0][2] * matrix[1][0] * matrix[3][3]) - (matrix[0][0] * matrix[1][2] * matrix[3][3]);
		inverted[1][3] = (matrix[0][2] * matrix[1][3] * matrix[2][0]) - (matrix[0][3] * matrix[1][2] * matrix[2][0])
				+ (matrix[0][3] * matrix[1][0] * matrix[2][2]) - (matrix[0][0] * matrix[1][3] * matrix[2][2])
				- (matrix[0][2] * matrix[1][0] * matrix[2][3]) + (matrix[0][0] * matrix[1][2] * matrix[2][3]);
		inverted[2][0] = (matrix[1][1] * matrix[2][3] * matrix[3][0]) - (matrix[1][3] * matrix[2][1] * matrix[3][0])
				+ (matrix[1][3] * matrix[2][0] * matrix[3][1]) - (matrix[1][0] * matrix[2][3] * matrix[3][1])
				- (matrix[1][1] * matrix[2][0] * matrix[3][3]) + (matrix[1][0] * matrix[2][1] * matrix[3][3]);
		inverted[2][1] = (matrix[0][3] * matrix[2][1] * matrix[3][0]) - (matrix[0][1] * matrix[2][3] * matrix[3][0])
				- (matrix[0][3] * matrix[2][0] * matrix[3][1]) + (matrix[0][0] * matrix[2][3] * matrix[3][1])
				+ (matrix[0][1] * matrix[2][0] * matrix[3][3]) - (matrix[0][0] * matrix[2][1] * matrix[3][3]);
		inverted[2][2] = (matrix[0][1] * matrix[1][3] * matrix[3][0]) - (matrix[0][3] * matrix[1][1] * matrix[3][0])
				+ (matrix[0][3] * matrix[1][0] * matrix[3][1]) - (matrix[0][0] * matrix[1][3] * matrix[3][1])
				- (matrix[0][1] * matrix[1][0] * matrix[3][3]) + (matrix[0][0] * matrix[1][1] * matrix[3][3]);
		inverted[2][3] = (matrix[0][3] * matrix[1][1] * matrix[2][0]) - (matrix[0][1] * matrix[1][3] * matrix[2][0])
				- (matrix[0][3] * matrix[1][0] * matrix[2][1]) + (matrix[0][0] * matrix[1][3] * matrix[2][1])
				+ (matrix[0][1] * matrix[1][0] * matrix[2][3]) - (matrix[0][0] * matrix[1][1] * matrix[2][3]);
		inverted[3][0] = (matrix[1][2] * matrix[2][1] * matrix[3][0]) - (matrix[1][1] * matrix[2][2] * matrix[3][0])
				- (matrix[1][2] * matrix[2][0] * matrix[3][1]) + (matrix[1][0] * matrix[2][2] * matrix[3][1])
				+ (matrix[1][1] * matrix[2][0] * matrix[3][2]) - (matrix[1][0] * matrix[2][1] * matrix[3][2]);
		inverted[3][1] = (matrix[0][1] * matrix[2][2] * matrix[3][0]) - (matrix[0][2] * matrix[2][1] * matrix[3][0])
				+ (matrix[0][2] * matrix[2][0] * matrix[3][1]) - (matrix[0][0] * matrix[2][2] * matrix[3][1])
				- (matrix[0][1] * matrix[2][0] * matrix[3][2]) + (matrix[0][0] * matrix[2][1] * matrix[3][2]);
		inverted[3][2] = (matrix[0][2] * matrix[1][1] * matrix[3][0]) - (matrix[0][1] * matrix[1][2] * matrix[3][0])
				- (matrix[0][2] * matrix[1][0] * matrix[3][1]) + (matrix[0][0] * matrix[1][2] * matrix[3][1])
				+ (matrix[0][1] * matrix[1][0] * matrix[3][2]) - (matrix[0][0] * matrix[1][1] * matrix[3][2]);
		inverted[3][3] = (matrix[0][1] * matrix[1][2] * matrix[2][0]) - (matrix[0][2] * matrix[1][1] * matrix[2][0])
				+ (matrix[0][2] * matrix[1][0] * matrix[2][1]) - (matrix[0][0] * matrix[1][2] * matrix[2][1])
				- (matrix[0][1] * matrix[1][0] * matrix[2][2]) + (matrix[0][0] * matrix[1][1] * matrix[2][2]);

		double det = determinant();

		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				matrix[i][j] = inverted[i][j] / det;
			}
		}
	}

	private double determinant() {
		double value = (matrix[0][3] * matrix[1][2] * matrix[2][1] * matrix[3][0])
				- (matrix[0][2] * matrix[1][3] * matrix[2][1] * matrix[3][0])
				- (matrix[0][3] * matrix[1][1] * matrix[2][2] * matrix[3][0])
				+ (matrix[0][1] * matrix[1][3] * matrix[2][2] * matrix[3][0])
				+ (matrix[0][2] * matrix[1][1] * matrix[2][3] * matrix[3][0])
				- (matrix[0][1] * matrix[1][2] * matrix[2][3] * matrix[3][0])
				- (matrix[0][3] * matrix[1][2] * matrix[2][0] * matrix[3][1])
				+ (matrix[0][2] * matrix[1][3] * matrix[2][0] * matrix[3][1])
				+ (matrix[0][3] * matrix[1][0] * matrix[2][2] * matrix[3][1])
				- (matrix[0][0] * matrix[1][3] * matrix[2][2] * matrix[3][1])
				- (matrix[0][2] * matrix[1][0] * matrix[2][3] * matrix[3][1])
				+ (matrix[0][0] * matrix[1][2] * matrix[2][3] * matrix[3][1])
				+ (matrix[0][3] * matrix[1][1] * matrix[2][0] * matrix[3][2])
				- (matrix[0][1] * matrix[1][3] * matrix[2][0] * matrix[3][2])
				- (matrix[0][3] * matrix[1][0] * matrix[2][1] * matrix[3][2])
				+ (matrix[0][0] * matrix[1][3] * matrix[2][1] * matrix[3][2])
				+ (matrix[0][1] * matrix[1][0] * matrix[2][3] * matrix[3][2])
				- (matrix[0][0] * matrix[1][1] * matrix[2][3] * matrix[3][2])
				- (matrix[0][2] * matrix[1][1] * matrix[2][0] * matrix[3][3])
				+ (matrix[0][1] * matrix[1][2] * matrix[2][0] * matrix[3][3])
				+ (matrix[0][2] * matrix[1][0] * matrix[2][1] * matrix[3][3])
				- (matrix[0][0] * matrix[1][2] * matrix[2][1] * matrix[3][3])
				- (matrix[0][1] * matrix[1][0] * matrix[2][2] * matrix[3][3])
				+ (matrix[0][0] * matrix[1][1] * matrix[2][2] * matrix[3][3]);

		return value;
	}
}
