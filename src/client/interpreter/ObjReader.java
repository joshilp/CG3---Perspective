package client.interpreter;

import java.util.ArrayList;
import java.util.List;

import client.interpreter.ObjReader.ObjFace;
import client.interpreter.SimpInterpreter.RenderStyle;
import geometry.Point3DH;
import geometry.Vertex3D;
import polygon.Chain;
import polygon.Polygon;
import polygon.Shader;
import windowing.graphics.Color;
//import client.interpreter.SimpInterpreter;

class ObjReader {
	private static final char COMMENT_CHAR = '#';
	private static final int NOT_SPECIFIED = -1;

	private class ObjVertex {
		// TODO: fill this class in. Store indices for a vertex, a texture, and a
		// normal. Have getters for them.

		private int v;
		private int vt;
		private int vn;

		public ObjVertex(int v, int vt, int vn) {
			this.v = v;
			this.vt = vt;
			this.vn = vn;
		}

		public int getv() {
			return v;
		}

		public int getvt() {
			return vt;
		}

		public int getvn() {
			return vn;
		}

	}

	public class ObjFace extends ArrayList<ObjVertex> {
		private static final long serialVersionUID = -4130668677651098160L;
		// ArrayList<ObjVertex> face;
	}

	private LineBasedReader reader;

	private List<Vertex3D> objVertices;
	private List<Vertex3D> transformedVertices;
	private List<Point3DH> objNormals;
	private List<ObjFace> objFaces;

	private Color defaultColor;

	ObjReader(String filename, Color defaultColor) {
		// TODO: Initialize an instance of this class.
		this.reader = new LineBasedReader(filename);
		this.objVertices = new ArrayList<Vertex3D>();
		this.transformedVertices = new ArrayList<Vertex3D>();
		this.objNormals = new ArrayList<Point3DH>();
		this.objFaces = new ArrayList<ObjFace>();
		this.defaultColor = defaultColor;
		read();
	}

	private Chain polygontochain(Polygon polygon) {
		int n = polygon.length();

		Chain c = new Chain();

		for (int i = 0; i < n; i++) {
			Vertex3D vertex = polygon.get(i);
			c.add(vertex);
		}
		return c;
	}

	public void render(SimpInterpreter interpreter) {
		// TODO: Implement. All of the vertices, normals, and faces have been defined.
		// First, transform all of the vertices.

		transformVertices(interpreter);
		// transformNormals(interpreter);

		for (ObjFace face : objFaces) {
			Polygon polygon = polygonForFace(face);
			Chain c = polygontochain(polygon);

			c = interpreter.clipper.clip_z(c);

			Chain proj_vertices = new Chain();

			int n = c.length();

			for (int i = 0; i < n; i++) {
				Vertex3D vertex = c.get(i);
				vertex = interpreter.projectVertex(vertex);
				vertex = interpreter.projectedToScreen.transformV3D(vertex);
				proj_vertices.add(vertex);
			}

			proj_vertices = interpreter.clipper.clip_xy(proj_vertices);

			n = proj_vertices.length();

			if (interpreter.renderStyle == RenderStyle.WIREFRAME) {
				Polygon polygon3 = Polygon.chaintopolygon(proj_vertices);
				interpreter.wireframeRenderer.drawPolygon(polygon3, interpreter.drawable);
			}

			if (interpreter.renderStyle == RenderStyle.FILLED) {
				for (int k = 1; k < n - 1; k++) {
					Vertex3D p0 = proj_vertices.get(0);
					Vertex3D p1 = proj_vertices.get(k);
					Vertex3D p2 = proj_vertices.get(k + 1);
					Polygon polygon2 = Polygon.make(p0, p1, p2);

					interpreter.filledRenderer.drawPolygon(polygon2, interpreter.depthCueingDrawable,
							interpreter.ambientShader);
				}

			}
		}
	}

	private void transformVertices(SimpInterpreter interpreter) {
		// TODO Auto-generated method stub
		for (Vertex3D vertex : objVertices) {
			vertex = interpreter.transformToCamera(vertex);
			transformedVertices.add(vertex);
		}
	}

	private Polygon polygonForFace(ObjFace face) {
		// TODO: This function might be used in render() above. Implement it if you find
		// it handy.
		Polygon result = Polygon.makeEmpty();
		int v;

		for (ObjVertex objVertex : face) {
			v = objVertex.getv();
			Vertex3D vertex = transformedVertices.get(v - 1);
			result.add(vertex);
		}
		return result;
	}

	public void read() {
		while (reader.hasNext()) {
			String line = reader.next().trim();
			interpretObjLine(line);
		}
	}

	private void interpretObjLine(String line) {
		if (!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if (tokens.length != 0) {
				interpretObjCommand(tokens);
			}
		}
	}

	private void interpretObjCommand(String[] tokens) {
		switch (tokens[0]) {
		case "v":
		case "V":
			interpretObjVertex(tokens);
			break;
		case "vn":
		case "VN":
			interpretObjNormal(tokens);
			break;
		case "f":
		case "F":
			interpretObjFace(tokens);
			break;
		default: // do nothing
			break;
		}
	}

	private void interpretObjFace(String[] tokens) {
		ObjFace face = new ObjFace();

		for (int i = 1; i < tokens.length; i++) {
			String token = tokens[i];
			String[] subtokens = token.split("/");

			int vertexIndex = objIndex(subtokens, 0, objVertices.size());
			int textureIndex = objIndex(subtokens, 1, 0);
			int normalIndex = objIndex(subtokens, 2, objNormals.size());

			// TODO: fill in action to take here.
			ObjVertex obj = new ObjVertex(vertexIndex, textureIndex, normalIndex);
			face.add(obj);
		}
		// TODO: fill in action to take here.
		objFaces.add(face);
	}

	private int objIndex(String[] subtokens, int tokenIndex, int baseForNegativeIndices) {
		// TODO: write this. subtokens[tokenIndex], if it exists, holds a string for an
		// index.

		// use Integer.parseInt() to get the integer value of the index.
		// Be sure to handle both positive and negative indices.

		String index = subtokens[tokenIndex];
		int i = Integer.parseInt(index);

		if (i >= 0) {
			return i;
		} else {
			return baseForNegativeIndices + i;
		}
	}

	private void interpretObjNormal(String[] tokens) {
		int numArgs = tokens.length - 1;
		if (numArgs != 3) {
			throw new BadObjFileException("vertex normal with wrong number of arguments : " + numArgs + ": " + tokens);
		}
		Point3DH normal = SimpInterpreter.interpretPoint(tokens, 1);
		// TODO: fill in action to take here.
		objNormals.add(normal);
	}

	private void interpretObjVertex(String[] tokens) {
		int numArgs = tokens.length - 1;
		Point3DH point = objVertexPoint(tokens, numArgs);
		Color color = objVertexColor(tokens, numArgs);

		// TODO: fill in action to take here.
		objVertices.add(new Vertex3D(point, color));
	}

	private Color objVertexColor(String[] tokens, int numArgs) {
		if (numArgs == 6) {
			return SimpInterpreter.interpretColor(tokens, 4);
		}
		if (numArgs == 7) {
			return SimpInterpreter.interpretColor(tokens, 5);
		}
		return defaultColor;
	}

	private Point3DH objVertexPoint(String[] tokens, int numArgs) {
		if (numArgs == 3 || numArgs == 6) {
			return SimpInterpreter.interpretPoint(tokens, 1);
		} else if (numArgs == 4 || numArgs == 7) {
			return SimpInterpreter.interpretPointWithW(tokens, 1);
		}
		throw new BadObjFileException("vertex with wrong number of arguments : " + numArgs + ": " + tokens);
	}
}