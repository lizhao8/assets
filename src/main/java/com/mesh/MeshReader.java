package com.mesh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.assets.max.Buiding;
import com.assets.max.SubMesh;
import com.jogl.base.Element;
import com.jogl.base.Face;
import com.jogl.base.Normal;
import com.jogl.base.Point;
import com.jogl.base.Texture;
import com.mesh.data.Float;
import com.mesh.data.Long;
import com.mesh.data.Mesh;
import com.tool.BitConverter;

public class MeshReader {
	private String meshFilePath;
	private BufferedReader reader;
	Text text = new Text(0);
	Data data = new Data(null);
	public Mesh mesh;

	public MeshReader(String meshFilePath) throws Exception {
		this.meshFilePath = meshFilePath;
	}

	private void reader() throws Exception {
		FileReader fileReader = new FileReader(meshFilePath);
		reader = new BufferedReader(fileReader);
		String line = "";
		line = reader.readLine();
		text.setText(line);

		readerText(text, null);
		System.out.println("readerText over");

		data = getData(null, text);
		System.out.println("getData over");

		mesh = new Mesh(data);
		System.out.println("Mesh over");

	}

	private void saveData(Data data) throws Exception {
		FileWriter fileWriter = new FileWriter(newMeshFilePath);
		BufferedWriter writer = new BufferedWriter(fileWriter);
		data.save(writer);
		writer.flush();
		writer.close();

	}

	private Array analyseArray(Data data, Text text) {
		Array array = new Array(data);
		Data sizeData = getData(text.childList.get(0));
		int size = sizeData.intValue();
		if (size <= 0) {
			return array;
		}
		for (int i = 0; i < size; i++) {
			Text childText = text.childList.get(i + 1).childList.get(0);
			getData(array, childText);
		}

		return array;
	}

	private Data getData(Text text) {
		return getData(null, text);
	}

	private Data getData(Data parent, Text text) {
		String string = text.getText();
		// System.out.println(string);
		String[] splits = string.split(" ");
		Data data = new Data(parent);
		int i = 0;
		data.index = splits[i++];
		String type = splits[i++];
		if ("unsigned".equals(type)) {
			type = type + " " + splits[i++];
		}
		data.type = type;
		data.name = splits[i++];

		if (i < splits.length && "=".equals(splits[i++])) {
			data.value = splits[i++];
		}
		if (isArray(data)) {
			parent.remove(data);
			data = analyseArray(data, text);
		} else {
			for (Text child : text.childList) {
				getData(data, child);
			}
		}

		return data;
	}

	String newMeshFilePath = "D:\\data\\123\\body_Mesh_new.txt";

	private void saveText(Text text) throws Exception {
		FileWriter fileWriter = new FileWriter(newMeshFilePath);
		BufferedWriter writer = new BufferedWriter(fileWriter);
		text.save(writer);
		writer.flush();
		writer.close();
	}

	private String readerText(Text parent, String string) throws Exception {
		Text text = null;
		if (string != null) {
			string = string.replaceFirst(" ", "");
			text = new Text(parent);
			text.setText(string);
		}
		do {
			int deep = parent.deep + 1;
			String line = reader.readLine();
			if (line == null) {
				return "";
			}
			String regex = "\\s{" + deep + "}";
			if (line.matches(regex + ".*")) {
				line = line.replaceFirst(regex, "");
			} else {
				return line;
			}
			if (line.startsWith(" ")) {
				line = readerText(text, line);
				if (line.matches(regex + ".*")) {
					line = line.replaceFirst(regex, "");
					text = new Text(parent);
					text.setText(line);
				} else {
					return line;
				}
			} else {
				text = new Text(parent);
				text.setText(line);
			}
		} while (true);

	}

	public static boolean isArray(Data data) {
		if ("Array".equals(data.type) || "TypelessData".equals(data.type)) {
			return true;
		}
		return false;
	}

	StringBuilder sb;

	private void appendLine(String string) {
		sb.append(string);
		sb.append(System.getProperty("line.separator"));
	}

	private void appendFormat(String string, float... params) {
		java.lang.Float[] floats = new java.lang.Float[params.length];
		for (int i = 0; i < params.length; i++) {
			floats[i] = params[i];
		}
		string = String.format(string, floats);
		// System.out.println(string);
		sb.append(string);
		sb.append(System.getProperty("line.separator"));
	}

	public void appendFormat(String string, long... params) {
		java.lang.Long[] longs = new java.lang.Long[params.length];
		for (int i = 0; i < params.length; i++) {
			longs[i] = params[i];
		}
		string = String.format(string, longs);
		sb.append(string);
		sb.append(System.getProperty("line.separator"));
	}

	public float toFloat(List<Data> m_Vertices, int index) {
		int count = 4;
		byte[] bytes = new byte[count];
		for (int i = 0; i < count; i++) {
			bytes[i] = (byte) m_Vertices.get(index + i).intValue();
		}
		return BitConverter.toFloat(bytes);
	}

	public float toLong(List<Data> m_Vertices, int index) {
		int count = 4;
		byte[] bytes = new byte[count];
		for (int i = 0; i < count; i++) {
			bytes[i] = (byte) m_Vertices.get(index + i).intValue();
		}
		return BitConverter.toLong(bytes);
	}

	public boolean exportToObj(String exportPath) {
		if (mesh.m_VertexCount <= 0)
			return false;
		sb = new StringBuilder();
		appendLine("g " + mesh.name);

		// region Vertices
		if (mesh.m_Vertices == null || mesh.m_Vertices.size() == 0) {
			return false;
		}

		for (int v = 0; v < mesh.m_VertexCount; v++) {
			int c = 3;
			appendFormat("v %s %s %s", -mesh.m_Vertices.get(v * c + 0).value, mesh.m_Vertices.get(v * c + 1).value,
					mesh.m_Vertices.get(v * c + 2).value);
		}
		// endregion

		// region UV

		if (mesh.m_UV0 != null && mesh.m_UV0.size() > 0) {
			int c = 2;
			for (int v = 0; v < mesh.m_VertexCount; v++) {
				appendFormat("vt %s %s", mesh.m_UV0.get(v * c + 0).value, mesh.m_UV0.get(v * c + 1).value);
			}
		}
		// endregion

		// region Normals
		if (mesh.m_Normals != null && mesh.m_Normals.size() > 0) {
			int c = 3;
			for (int v = 0; v < mesh.m_VertexCount; v++) {
				appendFormat("vn %s %s %s", -mesh.m_Normals.get(v * c + 0).value, mesh.m_Normals.get(v * c + 1).value,
						mesh.m_Normals.get(v * c + 2).value);
			}
		}

		// endregion
		// BitConverter.byteOrder = ByteOrder.BIG_ENDIAN;
		// region Face
		int sum = 0;
		for (int i = 0; i < mesh.subMeshList.size(); i++) {
			appendLine("g " + mesh.name + "_" + i);
			int indexCount = (int) mesh.subMeshList.get(i).indexCount;
			int end = sum + indexCount;
			for (int f = sum; f < end; f += 3) {
				long long1 = mesh.m_Indices.get(f + 0).value + 1;
				long long2 = mesh.m_Indices.get(f + 1).value + 1;
				long long3 = mesh.m_Indices.get(f + 2).value + 1;
				appendFormat("f %s/%s/%s %s/%s/%s %s/%s/%s", long1, long1, long1, long2, long2, long2, long3, long3,
						long3);
			}
			sum = end;
		}

		// endregion

		try {
			String result = sb.toString();
			result = result.replaceAll("NaN", "0");
			File file = new File(exportPath);
			FileWriter fileWriter = new FileWriter(file);
			fileWriter.write(result);
			fileWriter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	private void replace(String objectPath) throws Exception {
		FileReader fileReader = new FileReader(objectPath);
		BufferedReader reader = new BufferedReader(fileReader);
		String line = "";

		List<Point> pointList = new ArrayList<Point>();
		List<Texture> textureList = new ArrayList<Texture>();
		List<Normal> normalList = new ArrayList<Normal>();
		List<Face> faceList = new ArrayList<Face>();
		List<SubMesh> subMeshList = new ArrayList<SubMesh>();
		List<Texture> _textureList = new ArrayList<Texture>();

		int subMeshIndex = 0;
		SubMesh subMesh = null;
		while (true) {
			line = reader.readLine();
			if (line == null) {
				break;
			}
			if (line.startsWith("v  ")) {
				line = line.replace("v  ", "");
				pointList.add(new Point(line));
			} else if (line.startsWith("vt")) {
				line = line.replace("vt ", "");
				textureList.add(new Texture(line));				
			} else if (line.startsWith("vn")) {
				line = line.replace("vn ", "");
				normalList.add(new Normal(line));
			} else if (line.startsWith("g")) {
				line = reader.readLine();
				subMesh = mesh.subMeshList.get(subMeshIndex);
				// subMesh = new SubMesh(sm);
				subMeshList.add(subMesh);
				subMeshIndex++;
				//_textureList.addAll(textureList);
				//textureList=_textureList;
				//_textureList=new ArrayList<Texture>();
			} else if (line.startsWith("f")) {
				line = line.replace("f ", "");
				Face face = new Face(line, subMesh);
				faceList.add(face);
				subMesh.faceList.add(face);
			}

		}
		reader.close();

		// 根据面重置定点信息
		reset(faceList, pointList, textureList, normalList, subMeshList);

		//mesh.reset();
	}

	private void reset(List<Face> faceList, List<Point> pointList, List<Texture> textureList, List<Normal> normalList,
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
			List<Buiding> buidingList = new ArrayList<Buiding>(face.buidingList);
			buidingList.removeAll(allBuidingList);
			allBuidingList.addAll(buidingList);
			long a = allBuidingList.indexOf(face.getBuiding(0));
			long b = allBuidingList.indexOf(face.getBuiding(1));
			long c = allBuidingList.indexOf(face.getBuiding(2));
			/*Set<java.lang.Long> set = new TreeSet<java.lang.Long>();
			set.add(a);
			set.add(b);
			set.add(c);
			List<java.lang.Long> list = new ArrayList<java.lang.Long>(set);
			//Collections.reverse(list);
			a = list.get(0);
			b = list.get(1);
			c = list.get(2);*/
			face.a = a;
			face.b = b;
			face.c = c;

			for (Buiding buiding : buidingList) {
				_pointList.add(new Point(buiding.point.x, buiding.point.y, buiding.point.z));
				_textureList.add(new Texture(buiding.texture.x, buiding.texture.y));
				_normalList.add(new Point(buiding.normal.x, buiding.normal.y, buiding.normal.z));
			}
		}
		mesh.m_VertexCount = pointList.size();
		mesh.m_Vertices = pointToArray(_pointList);
		mesh.m_UV0 = textureToArray(_textureList);
		mesh.m_Normals = pointToArray(_normalList);
		mesh.m_Indices = faceToList(faceList);
		mesh.subMeshList = subMeshList;
		mesh.faceList = faceList;
		long firstByte = 0;
		long firstVertex = 0;
		for (SubMesh subMesh : subMeshList) {
			subMesh.setIndexCount(subMesh.faceList.size() * 3);
			subMesh.setFirstByte(firstByte);
			long vertexCount = getVertexCount(subMesh.faceList);
			subMesh.setVertexCount(vertexCount);
			subMesh.setFirstVertex(firstVertex);
			firstByte = subMesh.faceList.size() * 3 * 2;
			firstVertex += vertexCount;
		}
		mesh.m_VertexCount = allBuidingList.size();

		// 根据面划分元素
		mesh.elementList = partitionElement(faceList);

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

	private List<Element> partitionElement(List<Face> _faceList) {
		List<Face> faceList = new ArrayList<>(_faceList);
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

	public List<Float> pointToArray(List<? extends Point> pointList) {
		List<Float> list = new ArrayList<Float>();
		for (int i = 0; i < pointList.size(); i++) {
			Point point = pointList.get(i);
			list.add(new Float(point.x));
			list.add(new Float(point.y));
			list.add(new Float(point.z));
		}
		return list;
	}

	public List<Float> textureToArray(List<Texture> textureList) {
		List<Float> list = new ArrayList<Float>();
		for (int i = 0; i < textureList.size(); i++) {
			Texture texture = textureList.get(i);
			list.add(new Float(texture.x));
			list.add(new Float(texture.y));
		}
		return list;
	}

	public List<Long> faceToList(List<Face> faces) {
		List<Long> longList = new ArrayList<Long>();
		for (int i = 0; i < faces.size(); i++) {
			Face face = faces.get(i);
			longList.add(new Long(face.a));
			longList.add(new Long(face.b));
			longList.add(new Long(face.c));
		}
		return longList;
	}

	public static void main(String[] args) throws Exception {
		// String meshFilePath = "D:\\data\\123\\body_Mesh_s.txt";
		String meshFilePath = "D:\\data\\123\\body_Mesh.txt";
		//String meshFilePath = "D:\\data\\123\\test\\123.txt";
		MeshReader reader = new MeshReader(meshFilePath);
		reader.reader();
		reader.exportToObj("D:\\data\\123\\" + "body_Mesh_body.obj"); System.out.println("ExportMesh over");
//		System.out.println(reader.mesh.m_Indices.size());
//		System.out.println(reader.mesh.m_VertexCount);
//		System.out.println(reader.mesh.m_Vertices.size());
//		System.out.println(reader.mesh.m_Normals.size());
//		System.out.println(reader.mesh.m_UV0.size());
//		System.out.println("-------------");
		reader.replace("D:\\data\\123\\max.obj");System.out.println("replace over");
		//reader.replace("D:\\data\\123\\modify.obj");System.out.println("replace over");
		reader.mesh.reset();

//		System.out.println(reader.mesh.m_Indices.size());
//		System.out.println(reader.mesh.m_VertexCount);
//		System.out.println(reader.mesh.m_Vertices.size());
//		System.out.println(reader.mesh.m_Normals.size());
//		System.out.println(reader.mesh.m_UV0.size());
//		System.out.println("-------------");

		//reader.saveText(reader.text);System.out.println("saveText over");
		//System.out.println(reader.data.getByName(meshFilePath));
		
		reader.saveData(reader.data);
		System.out.println("saveData over");
		
		reader.exportToObj("D:\\data\\123\\" + "body_Mesh_body_new.obj");System.out.println("exportToObj over");

		//Draw draw = new Draw(reader.mesh);
		//draw.starting();
	}

}
