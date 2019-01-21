package client.interpreter;

import java.io.File;
import java.util.Stack;

import client.interpreter.LineBasedReader;
import client.interpreter.Clipper;
import geometry.Point3DH;
import geometry.Rectangle;
import geometry.Vertex3D;
import line.LineRenderer;
import client.interpreter.RendererTrio;
import client.interpreter.SimpInterpreter.RenderStyle;
import geometry.Transformation;
import polygon.Polygon;
import polygon.PolygonRenderer;
import polygon.Chain;
import polygon.ColorPolygonRenderer;
import polygon.Shader;
import windowing.drawable.DepthCueingDrawable;
import windowing.drawable.Drawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;

public class SimpInterpreter {
	private static final int NUM_TOKENS_FOR_POINT = 3;
	private static final int NUM_TOKENS_FOR_COMMAND = 1;
	private static final int NUM_TOKENS_FOR_COLORED_VERTEX = 6;
	private static final int NUM_TOKENS_FOR_UNCOLORED_VERTEX = 3;
	private static final char COMMENT_CHAR = '#';
	public RenderStyle renderStyle;

	private static Transformation CTM;
	private Transformation worldToScreen;
	private static Transformation worldToCamera;
	private Transformation cameraToScreen;
	public Transformation projectedToScreen;

	private static int WORLD_LOW_X = -100;
	private static int WORLD_HIGH_X = 100;
	private static int WORLD_LOW_Y = -100;
	private static int WORLD_HIGH_Y = 100;

	private LineBasedReader reader;
	private Stack<LineBasedReader> readerStack;
	private Stack<Transformation> CTMStack;

	private Color defaultColor = Color.WHITE;
	public Color ambientLight = Color.BLACK;
	public Shader ambientShader = lightingcalc -> ambientLight.multiply(lightingcalc);

	public Drawable drawable;
	public Drawable depthCueingDrawable;

	private LineRenderer lineRenderer;
	public PolygonRenderer filledRenderer;
	public PolygonRenderer wireframeRenderer;

	public Clipper clipper;

	public enum RenderStyle {
		FILLED, WIREFRAME;
	}

	public SimpInterpreter(String filename, Drawable drawable, RendererTrio renderers) {
		this.drawable = drawable;
		this.depthCueingDrawable = drawable;
		this.lineRenderer = RendererTrio.getLineRenderer();
		this.filledRenderer = renderers.getFilledRenderer();
		this.wireframeRenderer = renderers.getWireframeRenderer();
		this.defaultColor = Color.WHITE;
		this.ambientLight = Color.BLACK;
		makeWorldToScreenTransform(drawable.getDimensions());

		reader = new LineBasedReader(filename);
		readerStack = new Stack<>();
		CTMStack = new Stack<>();
		renderStyle = RenderStyle.FILLED;
		CTM = Transformation.identity();
		worldToCamera = Transformation.identity();

		makeWorldToScreenTransform(drawable.getDimensions());
	}

	private void makeWorldToScreenTransform(Dimensions dimensions) {
		// TODO: fill this in
		worldToScreen = Transformation.identity();
		worldToScreen.scale(dimensions.getWidth() / (WORLD_HIGH_X - WORLD_LOW_X),
				dimensions.getHeight() / (WORLD_HIGH_Y - WORLD_LOW_Y), 1, false);
		worldToScreen.translate(dimensions.getWidth() / 2.0, dimensions.getHeight() / 2.0, 0, false);
	}

	public void interpret() {
		while (reader.hasNext()) {
			String line = reader.next().trim();
			interpretLine(line);
			while (!reader.hasNext()) {
				if (readerStack.isEmpty()) {
					return;
				} else {
					reader = readerStack.pop();
				}
			}
		}
	}

	public void interpretLine(String line) {
		if (!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if (tokens.length != 0) {
				interpretCommand(tokens);
			}
		}
	}

	private void interpretCommand(String[] tokens) {
		// System.out.println("tokens: " +tokens[0]);
		switch (tokens[0]) {
		case "{":
			push();
			break;
		case "}":
			pop();
			break;
		case "wire":
			wire();
			break;
		case "filled":
			filled();
			break;

		case "file":
			interpretFile(tokens);
			break;
		case "scale":
			interpretScale(tokens);
			break;
		case "translate":
			interpretTranslate(tokens);
			break;
		case "rotate":
			interpretRotate(tokens);
			break;
		case "line":
			interpretLine(tokens);
			break;
		case "polygon":
			interpretPolygon(tokens);
			break;
		case "camera":
			interpretCamera(tokens);
			break;
		case "surface":
			interpretSurface(tokens);
			break;
		case "ambient":
			interpretAmbient(tokens);
			break;
		case "depth":
			interpretDepth(tokens);
			break;
		case "obj":
			interpretObj(tokens);
			break;

		default:
			System.err.println("bad input line: " + tokens);
			break;
		}
	}

	private void interpretObj(String[] tokens) {
		// TODO Auto-generated method stub
		String file = tokens[1];
		file = file.replace("\"", "");
		file = file + ".obj";
		// ObjReader objfile = new ObjReader(file, defaultColor);
		// objfile.render(this);

		objFile(file);
	}

	private void interpretDepth(String[] tokens) {
		// TODO Auto-generated method stub
		int nearclip = (int) cleanNumber(tokens[1]);
		int farclip = (int) cleanNumber(tokens[2]);

		double r = cleanNumber(tokens[3]);
		double g = cleanNumber(tokens[4]);
		double b = cleanNumber(tokens[5]);

		Color farColor = new Color(r, g, b);

		depthCueingDrawable = new DepthCueingDrawable(drawable, nearclip, farclip, farColor);
	}

	private void interpretSurface(String[] tokens) {
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);
		defaultColor = new Color(r, g, b);
	}

	private void interpretAmbient(String[] tokens) {
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);
		ambientLight = new Color(r, g, b);
	}

	private void interpretCamera(String[] tokens) {

		double xlow = cleanNumber(tokens[1]);
		double ylow = cleanNumber(tokens[2]);
		double xhigh = cleanNumber(tokens[3]);
		double yhigh = cleanNumber(tokens[4]);

		double front = cleanNumber(tokens[5]);
		double back = cleanNumber(tokens[6]);

		int left = 0;
		int right = drawable.getWidth();

		int top = drawable.getHeight();
		int bottom = 0;

		double read_width = xhigh - xlow;
		double read_height = yhigh - ylow;

		double scale = right / read_width;
		double r = read_height / read_width;

		int adjust = (int) ((top - top * r) / 2.0);

		clipper = new Clipper(front, back, left, right, top - adjust, bottom + adjust);

		worldToCamera.copy(CTM);
		worldToCamera.invert();

		projectedToScreen(drawable.getDimensions(), xhigh, xlow, yhigh, ylow);
	}

	private void projectedToScreen(Dimensions dimensions, double xhigh, double xlow, double yhigh, double ylow) {
		// TODO: fill this in
		int height = dimensions.getHeight();
		int width = dimensions.getWidth();

		double read_width = xhigh - xlow;
		double read_height = yhigh - ylow;

		double scale = width / read_width;
		double r = read_height / read_width;

		double new_xhigh = xhigh * scale;
		double new_yhigh = yhigh * scale;
		double new_ylow = ylow * scale;

		double tx = width - new_xhigh;
		double ty = height / 2 - ((new_yhigh + new_ylow) / 2);

		projectedToScreen = Transformation.identity();
		projectedToScreen.scale(width / read_width, height / read_height * r, 1, false);
		projectedToScreen.translate(tx, ty, 0, false);
	}

	public Vertex3D transformToCamera(Vertex3D vertex) {
		// TODO: finish this method
		return worldToCamera.transformV3D(CTM.transformV3D(vertex));
	}

	private void push() {
		// TODO: finish this method
		Transformation CTM2 = Transformation.identity();
		CTM2.copy(CTM);
		CTMStack.push(CTM2);

	}

	private void pop() {
		// TODO: finish this method
		CTM = CTMStack.pop();
	}

	private void wire() {
		// TODO: finish this method
		// System.out.println("wire");
		renderStyle = RenderStyle.WIREFRAME;
	}

	private void filled() {
		// TODO: finish this method
		// System.out.println("filled");
		renderStyle = RenderStyle.FILLED;
	}

	// this one is complete.
	private void interpretFile(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length - 1) == '"';
		String filename = quotedFilename.substring(1, length - 1);
		file(filename + ".simp");
	}

	private void file(String filename) {
		readerStack.push(reader);
		reader = new LineBasedReader(filename);
	}

	private void interpretScale(String[] tokens) {
		double sx = cleanNumber(tokens[1]);
		double sy = cleanNumber(tokens[2]);
		double sz = cleanNumber(tokens[3]);
		// TODO: finish this method
		CTM.scale(sx, sy, sz, true);
	}

	private void interpretTranslate(String[] tokens) {
		double tx = cleanNumber(tokens[1]);
		double ty = cleanNumber(tokens[2]);
		double tz = cleanNumber(tokens[3]);
		// TODO: finish this method
		CTM.translate(tx, ty, tz, true);
	}

	private void interpretRotate(String[] tokens) {
		String axisString = tokens[1];
		double angleInDegrees = cleanNumber(tokens[2]);
		// TODO: finish this method
		double angleinRad = Math.toRadians(angleInDegrees);

		switch (axisString) {
		case ("X"):
			CTM.rotateX(angleinRad, true);
			break;

		case ("Y"):
			CTM.rotateY(angleinRad, true);
			break;

		case ("Z"):
			CTM.rotateZ(angleinRad, true);
			break;
		}
	}

	private static double cleanNumber(String string) {
		return Double.parseDouble(string);
	}

	private enum VertexColors {
		COLORED(NUM_TOKENS_FOR_COLORED_VERTEX), UNCOLORED(NUM_TOKENS_FOR_UNCOLORED_VERTEX);

		private int numTokensPerVertex;

		private VertexColors(int numTokensPerVertex) {
			this.numTokensPerVertex = numTokensPerVertex;
		}

		public int numTokensPerVertex() {
			return numTokensPerVertex;
		}
	}

	private void interpretLine(String[] tokens) {
		Vertex3D[] vertices = interpretVertices(tokens, 2, 1);

		// TODO: finish this method
		Vertex3D p0 = vertices[0];
		Vertex3D p1 = vertices[1];
		line(p0, p1);
	}

	private void interpretPolygon(String[] tokens) {
		Vertex3D[] vertices = interpretVertices(tokens, 3, 1);
		Chain c = new Chain(vertices[0], vertices[1], vertices[2]);

		c = clipper.clip_z(c);

		Chain pv = new Chain();
		int n = c.length();

		for (int j = 0; j < n; j++) {
			Vertex3D vertex = c.get(j);
			pv.add(projectedToScreen.transformV3D(projectVertex(vertex)));
		}

		pv = clipper.clip_xy(pv);

		n = pv.length();

		if (renderStyle == RenderStyle.WIREFRAME) {
			if (pv.length() >= 3) {
				Polygon polygon3 = Polygon.chaintopolygon(pv);
				wireframeRenderer.drawPolygon(polygon3, drawable);
			}

		}

		if (renderStyle == RenderStyle.FILLED) {
			for (int k = 1; k < n - 1; k++) {
				Vertex3D p0 = pv.get(0);
				Vertex3D p1 = pv.get(k);
				Vertex3D p2 = pv.get(k + 1);
				Polygon polygon2 = Polygon.make(p0, p1, p2);

				filledRenderer.drawPolygon(polygon2, depthCueingDrawable, ambientShader);
			}

		}
	}

	public Vertex3D[] interpretVertices(String[] tokens, int numVertices, int startingIndex) {
		VertexColors vertexColors = verticesAreColored(tokens, numVertices);
		Vertex3D vertices[] = new Vertex3D[numVertices];

		for (int index = 0; index < numVertices; index++) {
			vertices[index] = interpretVertex(tokens, startingIndex + index * vertexColors.numTokensPerVertex(),
					vertexColors);
		}
		return vertices;
	}

	public VertexColors verticesAreColored(String[] tokens, int numVertices) {
		return hasColoredVertices(tokens, numVertices) ? VertexColors.COLORED : VertexColors.UNCOLORED;
	}

	public boolean hasColoredVertices(String[] tokens, int numVertices) {
		return tokens.length == numTokensForCommandWithNVertices(numVertices);
	}

	public int numTokensForCommandWithNVertices(int numVertices) {
		return NUM_TOKENS_FOR_COMMAND + numVertices * (NUM_TOKENS_FOR_COLORED_VERTEX);
	}

	public Vertex3D projectVertex(Vertex3D vertex) {
		// int d = -1;
		double i = -1 / vertex.getZ();
		double x = vertex.getX();
		double y = vertex.getY();
		double z = vertex.getZ();

		Vertex3D v = new Vertex3D((x * i), (y * i), z, vertex.getColor());
		return v;
	}

	private Vertex3D interpretVertex(String[] tokens, int startingIndex, VertexColors colored) {
		Point3DH point = interpretPoint(tokens, startingIndex);

		Color color = defaultColor;
		if (colored == VertexColors.COLORED) {
			color = interpretColor(tokens, startingIndex + NUM_TOKENS_FOR_POINT);
		}

		// TODO: finish this method
		Vertex3D v = new Vertex3D(point, color);

		v = transformToCamera(v);
		// v = projectedToScreen.transformV3D(projectVertex(v));

		return v;
	}

	public static Point3DH interpretPoint(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);

		// TODO: finish this method
		return new Point3DH(x, y, z);
	}

	public static Color interpretColor(String[] tokens, int startingIndex) {
		double r = cleanNumber(tokens[startingIndex]);
		double g = cleanNumber(tokens[startingIndex + 1]);
		double b = cleanNumber(tokens[startingIndex + 2]);

		// TODO: finish this method
		return new Color(r, g, b);
	}

	private void line(Vertex3D p1, Vertex3D p2) {
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);
		// TODO: finish this method
	}

	private void polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3) {
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);
		Vertex3D screenP3 = transformToCamera(p3);
		// // TODO: finish this method

	}

	public static Point3DH interpretPointWithW(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
		double w = cleanNumber(tokens[startingIndex + 3]);
		Point3DH point = new Point3DH(x, y, z, w);
		return point;
	}

	private void objFile(String filename) {
		ObjReader objReader = new ObjReader(filename, defaultColor);
		objReader.read();
		objReader.render(this);
	}

}
