package com.assets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.assets.data.BuildType;
import com.assets.data.Mesh;
import com.assets.max.Buiding;
import com.assets.max.SubMesh;
import com.jogl.base.Element;
import com.jogl.base.Face;
import com.jogl.base.Normal;
import com.jogl.base.Point;
import com.jogl.base.Texture;

public class ObjToMesh {
	private String objectPath = "D:\\data\\123\\body.obj";
	private Mesh m_Mesh;
	private Object object;

	public String getObjectPath() {
		return objectPath;
	}

	public void setObjectPath(String objectPath) {
		this.objectPath = objectPath;
	}

	public Mesh getM_Mesh() {
		return m_Mesh;
	}

	public void setM_Mesh(Mesh m_Mesh) {
		this.m_Mesh = m_Mesh;
	}

	public void run() throws Exception {
		if (m_Mesh == null) {
			m_Mesh = new Mesh();
		}

		FileReader fileReader = new FileReader(objectPath);
		BufferedReader reader = new BufferedReader(fileReader);
		String line = "";
		line = reader.readLine();
		if (line.startsWith("g")) {
			m_Mesh.m_Name = line.replace("g ", "");
		}
		List<Float> verticeList = new ArrayList<Float>();
		List<Float> UV0List = new ArrayList<Float>();
		List<Float> normalList = new ArrayList<Float>();
		List<Long> indiceList = new ArrayList<Long>();
		List<SubMesh> subMesheList = new ArrayList<SubMesh>();

		int v = 0;
		int vt = 0;
		int vn = 0;
		int sm = 0;
		int f = 0;
		SubMesh subMesh = null;
		while (true) {
			line = reader.readLine();
			if (line == null) {
				break;
			}
			if (line.startsWith("v ")) {
				int c = 3;
				String[] split = line.split(" ");
				// verticeList.set(v * c, -Float.parseFloat(split[1]));
				// verticeList.set(v * c + 1, Float.parseFloat(split[2]));
				// verticeList.set(v * c + 2, Float.parseFloat(split[3]));
				verticeList.add(-Float.parseFloat(split[1]));
				verticeList.add(Float.parseFloat(split[2]));
				verticeList.add(Float.parseFloat(split[3]));
				v += c;
			} else if (line.startsWith("vt")) {
				int c = 2;
				String[] split = line.split(" ");
				// UV0List.set(vt * c, Float.parseFloat(split[1]));
				// UV0List.set(vt * c + 1, Float.parseFloat(split[2]));
				UV0List.add(Float.parseFloat(split[1]));
				UV0List.add(Float.parseFloat(split[2]));
				vt += c;
			} else if (line.startsWith("vn")) {
				int c = 3;
				String[] split = line.split(" ");
				// normalList.set(vn * c, -Float.parseFloat(split[1]));
				// normalList.set(vn * c + 1, Float.parseFloat(split[2]));
				// normalList.set(vn * c + 2, Float.parseFloat(split[3]));
				normalList.add(-Float.parseFloat(split[1]));
				normalList.add(Float.parseFloat(split[2]));
				normalList.add(Float.parseFloat(split[3]));
				vn += c;
			} else if (line.startsWith("g")) {
				if (subMesh != null) {
					subMesh.indexCount = f;
					f = 0;
				}
				subMesh = m_Mesh.m_SubMeshes[sm];
				subMesheList.add(subMesh);
				sm++;
			} else if (line.startsWith("f")) {
				String[] split = line.split(" ");
				long long1 = Long.parseLong(split[1].split("/")[0]) - 1;
				long long2 = Long.parseLong(split[2].split("/")[0]) - 1;
				long long3 = Long.parseLong(split[3].split("/")[0]) - 1;
				indiceList.add(long3);
				indiceList.add(long2);
				indiceList.add(long1);
				f += 3;
			}

		}
		reader.close();

		if (subMesh != null) {
			subMesh.indexCount = f;
		}

		m_Mesh.m_VertexCount = v / 3;
		m_Mesh.m_Vertices = ByteReader.toArray(verticeList);
		m_Mesh.m_UV0 = ByteReader.toArray(UV0List);
		m_Mesh.m_Normals = ByteReader.toArray(normalList);
		m_Mesh.m_Indices = indiceList;
		// m_Mesh.m_SubMeshes = subMesheList.toArray(new SubMesh[0]);

		// AssetsReader.ExportMesh(m_Mesh, "D:\\data\\123\\my_export_" + m_Mesh.m_Name +
		// ".mesh");

	}

	public void runMax() throws Exception {

		if (m_Mesh == null) {
			m_Mesh = new Mesh();
		}

		FileReader fileReader = new FileReader(objectPath);
		BufferedReader reader = new BufferedReader(fileReader);
		String line = "";

		List<Point> pointList = new ArrayList<Point>();
		List<Texture> textureList = new ArrayList<Texture>();
		List<Normal> normalList = new ArrayList<Normal>();
		List<Face> faceList = new ArrayList<Face>();
		List<SubMesh> subMeshList = new ArrayList<SubMesh>();

		int v = 0;
		int vt = 0;
		int vn = 0;
		int sm = 0;
		int f = 0;
		SubMesh subMesh = null;
		while (true) {
			line = reader.readLine();
			if (line == null) {
				break;
			}
			if (line.startsWith("v  ")) {
				int c = 3;
				line = line.replace("v  ", "");
				pointList.add(new Point(line));
				v += c;
			} else if (line.startsWith("vt")) {
				int c = 2;
				line = line.replace("vt ", "");
				textureList.add(new Texture(line));
				vt += c;
			} else if (line.startsWith("vn")) {
				int c = 3;
				line = line.replace("vn ", "");
				normalList.add(new Normal(line));
				vn += c;
			} else if (line.startsWith("g")) {
				line = reader.readLine();
				subMesh = m_Mesh.m_SubMeshes[sm];
				subMeshList.add(subMesh);
				sm++;
			} else if (line.startsWith("f")) {
				line = line.replace("f ", "");
				Face face = new Face(line, subMesh);
				faceList.add(face);
				subMesh.faceList.add(face);
				f += 3;
			}

		}
		reader.close();

		// 根据面重置定点信息
		rest(faceList, pointList, textureList, normalList, subMeshList);

	}

	public float[] pointToArray(List<? extends Point> pointList) {
		float[] floats = new float[pointList.size() * 3];
		for (int i = 0; i < pointList.size(); i++) {
			Point point = pointList.get(i);
			floats[i * 3] = point.x;
			floats[i * 3 + 1] = point.y;
			floats[i * 3 + 2] = point.z;
		}
		return floats;
	}

	public float[] textureToArray(List<Texture> textureList) {
		float[] floats = new float[textureList.size() * 2];
		for (int i = 0; i < textureList.size(); i++) {
			Texture texture = textureList.get(i);
			floats[i * 2] = texture.x;
			floats[i * 2 + 1] = texture.y;
		}
		return floats;
	}

	public List<Long> faceToList(List<Face> faces) {
		List<Long> longList = new ArrayList<Long>();
		for (int i = 0; i < faces.size(); i++) {
			Face face = faces.get(i);
			longList.add(face.a);
			longList.add(face.b);
			longList.add(face.c);
		}
		return longList;
	}

	private void rest(List<Face> faceList, List<Point> pointList, List<Texture> textureList, List<Normal> normalList,
			List<SubMesh> subMeshList) {
		List<Point> _pointList = new ArrayList<Point>();
		List<Texture> _textureList = new ArrayList<Texture>();
		List<Point> _normalList = new ArrayList<Point>();
		for (int i = 0; i < faceList.size(); i++) {
			Face face = faceList.get(i);
			face.addPoint(pointList.get(face.index1.v));
			face.addPoint(pointList.get(face.index2.v));
			face.addPoint(pointList.get(face.index3.v));

			face.addTexture(textureList.get(face.index1.vt));
			face.addTexture(textureList.get(face.index2.vt));
			face.addTexture(textureList.get(face.index3.vt));

			face.addNormal(normalList.get(face.index1.vn));
			face.addNormal(normalList.get(face.index2.vn));
			face.addNormal(normalList.get(face.index3.vn));

			face.addBuiding(new Buiding(face.getPoint(0), face.getTexture(0), face.getNormal(0)));
			face.addBuiding(new Buiding(face.getPoint(1), face.getTexture(1), face.getNormal(1)));
			face.addBuiding(new Buiding(face.getPoint(2), face.getTexture(2), face.getNormal(2)));
		}
		List<Buiding> allBuidingList = new ArrayList<>();
		for (Face face : faceList) {
			List<Buiding> buidingList =new ArrayList<Buiding>(face.buidingList);
			buidingList.removeAll(allBuidingList);
			allBuidingList.addAll(buidingList);
			long a = allBuidingList.indexOf(face.getBuiding(0));
			long b = allBuidingList.indexOf(face.getBuiding(1));
			long c = allBuidingList.indexOf(face.getBuiding(2));
			Set<Long> set = new TreeSet<Long>();
			set.add(a);
			set.add(b);
			set.add(c);
			List<Long> list = new ArrayList<Long>(set);
			Collections.reverse(list);
			a = list.get(0);
			b = list.get(1);
			c = list.get(2);
			face.a = a;
			face.b = b;
			face.c = c;

			for (Buiding buiding : buidingList) {
				_pointList.add(new Point(buiding.point.x, buiding.point.y, buiding.point.z));
				_textureList.add(new Texture(buiding.texture.x, buiding.texture.y));
				_normalList.add(new Point(buiding.normal.x, buiding.normal.y, buiding.normal.z));
			}
		}
		m_Mesh.m_VertexCount = pointList.size();
		m_Mesh.m_Vertices = pointToArray(pointList);
		m_Mesh.m_UV0 = textureToArray(textureList);
		m_Mesh.m_Normals = pointToArray(normalList);
		m_Mesh.m_Indices = faceToList(faceList);
		m_Mesh.m_SubMeshes = subMeshList.toArray(new SubMesh[0]);
		m_Mesh.faceList = faceList;
		int firstByte = 0;

		for (SubMesh subMesh : subMeshList) {
			subMesh.indexCount = subMesh.faceList.size() * 3;
			subMesh.firstByte = firstByte;
			firstByte = subMesh.faceList.size() * 3 * 2;
			subMesh.vertexCount = getVertexCount(subMesh.faceList);
		}
		m_Mesh.m_VertexData.m_VertexCount = allBuidingList.size();

		// 根据面划分元素
		m_Mesh.elementList = partitionElement(faceList);

	}

	private List<Element> partitionElement(List<Face> _faceList) {
		List<Face> faceList=new ArrayList<>(_faceList);
		List<Element> elementList = new ArrayList<Element>();
		while (faceList.size() > 0) {
			Element element = new Element();
			Face face = faceList.get(0);
			element.addFace(face);
			faceList.removeAll(element.faceList);
			elementList.add(element);
		}
		return elementList;
	}

	private long getVertexCount(List<Face> faceList) {
		List<Buiding> allBuidingList = new ArrayList<>();
		for (Face face : faceList) {
			List<Buiding> buidingList = face.buidingList;
			buidingList.removeAll(allBuidingList);
			allBuidingList.addAll(buidingList);
		}
		return allBuidingList.size();
	}

	private void rest2(List<Face> faceList, List<Point> pointList, List<Texture> textureList, List<Normal> normalList) {
		StringBuilder pointText = new StringBuilder();
		StringBuilder textureText = new StringBuilder();
		StringBuilder normalText = new StringBuilder();
		StringBuilder faceText = new StringBuilder();

		for (int i = 0; i < faceList.size(); i++) {
			Face face = faceList.get(i);
			face.addPoint(pointList.get(face.index1.v));
			face.addPoint(pointList.get(face.index2.v));
			face.addPoint(pointList.get(face.index3.v));

			face.addTexture(textureList.get(face.index1.vt));
			face.addTexture(textureList.get(face.index2.vt));
			face.addTexture(textureList.get(face.index3.vt));

			face.addNormal(normalList.get(face.index1.vn));
			face.addNormal(normalList.get(face.index2.vn));
			face.addNormal(normalList.get(face.index3.vn));

			face.addBuiding(new Buiding(face.getPoint(0), face.getTexture(0), face.getNormal(0)));
			face.addBuiding(new Buiding(face.getPoint(1), face.getTexture(1), face.getNormal(1)));
			face.addBuiding(new Buiding(face.getPoint(2), face.getTexture(2), face.getNormal(2)));
		}
		List<Buiding> allBuidingList = new ArrayList<>();
		List<Buiding> buidingList = new ArrayList<>();
		for (int i = 0; i < faceList.size(); i++) {
			Face face = faceList.get(i);
			buidingList = face.buidingList;
			buidingList.removeAll(allBuidingList);
			allBuidingList.addAll(buidingList);

			long a = allBuidingList.indexOf(face.getBuiding(1));
			long b = allBuidingList.indexOf(face.getBuiding(2));
			long c = allBuidingList.indexOf(face.getBuiding(3));
			Set<Long> set = new TreeSet<Long>();
			set.add(a);
			set.add(b);
			set.add(c);
			List<Long> list = new ArrayList<Long>(set);
			Collections.reverse(list);
			a = list.get(0);
			b = list.get(1);
			c = list.get(2);
			face.a = a;
			face.b = b;
			face.c = c;
			appendFormat(faceText, "f %s/%s/%s %s/%s/%s %s/%s/%s", a + 1, a + 1, a + 1, b + 1, b + 1, b + 1, c + 1,
					c + 1, c + 1);

			for (Buiding buiding : buidingList) {
				appendFormat(pointText, "v %s %s %s", buiding.point.x, buiding.point.y, buiding.point.z);
			}
			for (Buiding buiding : buidingList) {
				appendFormat(textureText, "vt %s %s", buiding.texture.x, buiding.texture.y);
			}
			for (Buiding buiding : buidingList) {
				appendFormat(normalText, "vn %s %s %s", buiding.normal.x, buiding.normal.y, buiding.normal.z);
			}
		}

		/*
		 * try { StringBuilder stringBuilder = new StringBuilder();
		 * stringBuilder.append(pointText);
		 * stringBuilder.append(System.getProperty("line.separator"));
		 * stringBuilder.append(textureText);
		 * stringBuilder.append(System.getProperty("line.separator"));
		 * stringBuilder.append(normalText);
		 * stringBuilder.append(System.getProperty("line.separator"));
		 * stringBuilder.append(faceText);
		 * stringBuilder.append(System.getProperty("line.separator"));
		 * 
		 * String result = stringBuilder.toString(); File file = new
		 * File("D:\\data\\123\\test.obj"); FileWriter fileWriter = new
		 * FileWriter(file); fileWriter.write(result); fileWriter.close(); } catch
		 * (Exception e) { e.printStackTrace(); }
		 */
	}

	private void appendLine(StringBuilder stringBuilder, String string) {
		stringBuilder.append(string);
		stringBuilder.append(System.getProperty("line.separator"));
	}

	private void appendFormat(StringBuilder stringBuilder, String string, float... params) {
		Float[] floats = new Float[params.length];
		for (int i = 0; i < params.length; i++) {
			floats[i] = params[i];
		}
		string = String.format(string, floats);
		stringBuilder.append(string);
		stringBuilder.append(System.getProperty("line.separator"));
	}

	public void appendFormat(StringBuilder stringBuilder, String string, long... params) {
		Long[] longs = new Long[params.length];
		for (int i = 0; i < params.length; i++) {
			longs[i] = params[i];
		}
		string = String.format(string, longs);
		stringBuilder.append(string);
		stringBuilder.append(System.getProperty("line.separator"));
	}

	StringBuilder sb;

	private void appendLine(String string) {
		sb.append(string);
		sb.append(System.getProperty("line.separator"));
	}

	private void appendFormat(String string, float... params) {
		Float[] floats = new Float[params.length];
		for (int i = 0; i < params.length; i++) {
			floats[i] = params[i];
		}
		string = String.format(string, floats);
		sb.append(string);
		sb.append(System.getProperty("line.separator"));
	}

	public void appendFormat(String string, long... params) {
		Long[] longs = new Long[params.length];
		for (int i = 0; i < params.length; i++) {
			longs[i] = params[i];
		}
		string = String.format(string, longs);
		sb.append(string);
		sb.append(System.getProperty("line.separator"));
	}
}
