package com.mesh;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import com.mesh.data.Channel;
import com.mesh.data.Element;
import com.mesh.data.Face;
import com.mesh.data.Float;
import com.mesh.data.Index;
import com.mesh.data.Long;
import com.mesh.data.Mesh;
import com.mesh.data.Normal;
import com.mesh.data.Point;
import com.mesh.data.SubMesh;
import com.mesh.data.Texture;
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

	private void saveData(Data data, String filePath) throws Exception {
		FileWriter fileWriter = new FileWriter(filePath);
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

	private void saveText(Text text, String filePath) throws Exception {
		FileWriter fileWriter = new FileWriter(filePath);
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
		sb.append(string);
		sb.append(System.getProperty("line.separator"));
	}

	private void appendFormat(String string, String... params) {
		string = String.format(string, params);
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
		if (mesh.getVertexCount() <= 0)
			return false;
		sb = new StringBuilder();
		appendLine("g " + mesh.name);

		// region Vertices
		if (mesh.m_Vertices == null || mesh.m_Vertices.size() == 0) {
			return false;
		}

		for (int v = 0; v < mesh.getVertexCount(); v++) {
			int c = 3;
			appendFormat("v  %s %s %s", mesh.m_Vertices.get(v * c + 0).stringValue(-1),
					mesh.m_Vertices.get(v * c + 1).stringValue(), mesh.m_Vertices.get(v * c + 2).stringValue());
		}
		// endregion

		// region Normals
		if (mesh.m_Normals != null && mesh.m_Normals.size() > 0) {
			int c = 3;
			for (int v = 0; v < mesh.getVertexCount(); v++) {
				appendFormat("vn %s %s %s", mesh.m_Normals.get(v * c + 0).stringValue(-1),
						mesh.m_Normals.get(v * c + 1).stringValue(), mesh.m_Normals.get(v * c + 2).stringValue());
			}
		}
		// endregion

		// region UV
		if (mesh.m_UV0 != null && mesh.m_UV0.size() > 0) {
			int c = 2;
			for (int v = 0; v < mesh.getVertexCount(); v++) {
				appendFormat("vt %s %s", mesh.m_UV0.get(v * c + 0).stringValue(),
						mesh.m_UV0.get(v * c + 1).stringValue());
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

	public void replace(String objectPath) throws Exception {
		FileReader fileReader = new FileReader(objectPath);
		BufferedReader reader = new BufferedReader(fileReader);
		String line = "";

		List<Point> pointList = new ArrayList<Point>();
		List<Texture> textureList = new ArrayList<Texture>();
		List<Normal> normalList = new ArrayList<Normal>();
		List<Face> faceList = new ArrayList<Face>();
		List<SubMesh> subMeshList = new ArrayList<SubMesh>();
		List<Texture> _textureList = new ArrayList<Texture>();
		List<Normal> _normalList = new ArrayList<Normal>();
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
				// _textureList.add(new Texture(line));
			} else if (line.startsWith("vn")) {
				line = line.replace("vn ", "");
				normalList.add(new Normal(line));
				// _normalList.add(new Normal(line));
			} else if (line.startsWith("g")) {
				line = reader.readLine();
				subMesh = mesh.subMeshList.get(subMeshIndex);
				// subMesh = new SubMesh(sm);
				subMeshList.add(subMesh);
				subMesh.faceList.clear();
				subMeshIndex++;
				// _textureList.addAll(textureList);
				// textureList=_textureList;
				// _textureList=new ArrayList<Texture>();
				// _normalList.addAll(normalList);
				// normalList=_normalList;
				// _normalList=new ArrayList<Normal>();
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
	}

	private void reset(List<Face> faceList, List<Point> pointList, List<Texture> textureList, List<Normal> normalList,
			List<SubMesh> subMeshList) {

		// Set<Point> pointSet = new LinkedHashSet<Point>();
		List<Point> _pointList = new ArrayList<Point>();

		for (int i = 0; i < faceList.size(); i++) {
			Face face = faceList.get(i);
			for (Index index : face._indexList) {
				Point point = pointList.get(index.v);
				point.index = index;
				point.texture = textureList.get(index.vt);
				point.normal = normalList.get(index.vn);

				// pointSet.add(point);
				int n = _pointList.indexOf(point);
				if (n >= 0) {
					point = _pointList.get(n);
				} else {
					_pointList.add(point);
				}

				face.addPoint(point);
			}
		}

		List<Texture> _textureList = new ArrayList<Texture>();
		List<Normal> _normalList = new ArrayList<Normal>();

		// List<Point> _pointList = new ArrayList<>(pointSet);
		// 对原来的点和现在的点进行比较替换
		Map<String, List<Point>> newPointMap = getPointMap(_pointList);
		Map<String, List<Point>> oldPointMap = getPointMap(mesh.pointList);
		replacePoint(newPointMap, oldPointMap, 5);

		for (int i = 0; i < faceList.size(); i++) {

			Face face = faceList.get(i);
			for (Point point : face.pointList) {
				face.indexList.add(new Long(_pointList.indexOf(point)));
			}

			/*
			 * Face face = faceList.get(i); List<Long> list = new ArrayList<Long>(); for
			 * (Point point : face.pointList) { list.add(new
			 * Long(pointList.indexOf(point))); } Collections.reverse(list); for (Long long1
			 * : list) { face.indexList.add(long1); }
			 */
		}

		for (Point point : _pointList) {
			_textureList.add(point.texture);
			_normalList.add(point.normal);
		}

		/*
		 * Set<Normal> _normalSet= new LinkedHashSet<Normal>(); for (Point point :
		 * _pointList) { _textureList.add(point.texture); _normalSet.add(point.normal);
		 * } _normalList = new ArrayList<Normal>(_normalSet);
		 */

		mesh.pointList = _pointList;
		mesh.m_Vertices = pointToArray(_pointList);
		mesh.m_UV0 = textureToArray(_textureList);
		mesh.m_Normals = normalToArray(_normalList);
		mesh.m_Indices = faceToList(faceList);
		mesh.subMeshList = subMeshList;
		mesh.faceList = faceList;

		mesh.resetSubMesh();
		// 根据面划分元素
		mesh.elementList = partitionElement(faceList);
		int count = 0;
		for (Point point : mesh.pointList) {
			if (point.floatMap.size() != 8) {
				count++;
			}
		}
		// System.out.println(all);
		System.out.println(count);

	}

	List<String> notReplaceList = new ArrayList<String>() {
		{
			add("m_Vertices");
			add("m_Normals");
			add("m_UV0");
		}
	};

	private void replacePoint(Map<String, List<Point>> newPointMap, Map<String, List<Point>> oldPointMap,
			int accuracy) {
		int all = 0;
		// 创建映射map
		for (Iterator<String> iterator = newPointMap.keySet().iterator(); iterator.hasNext();) {
			String key = iterator.next();
			List<Point> newList = newPointMap.get(key);
			for (Iterator<Point> iterator2 = newList.iterator(); iterator2.hasNext();) {
				Point newPoint = (Point) iterator2.next();
				if (oldPointMap.containsKey(key)) {
					List<Point> oldList = oldPointMap.get(key);
					Point oldPoint = oldList.get(0);
					for (Channel channel : oldPoint.floatMap.keySet()) {
						if ("m_Vertices".equals(channel.type)) {
							newPoint.floatMap.put(channel, newPoint.floatList);
						} else if ("m_Normals".equals(channel.type)) {
							newPoint.floatMap.put(channel, newPoint.normal.floatList);
						} else if ("m_UV0".equals(channel.type)) {
							newPoint.floatMap.put(channel, newPoint.texture.floatList);
						} else {
							newPoint.floatMap.put(channel, oldPoint.floatMap.get(channel));
						}
					}

					all++;
					oldList.remove(oldPoint);
					iterator2.remove();
					if (oldList.isEmpty()) {
						oldPointMap.remove(key);
					}
					if (newList.isEmpty()) {
						iterator.remove();
					}
				} else {
					// System.err.println("不存在的点=" + key);
				}
			}

		}
		System.out.println(all);
		System.out.println("newPointMap=");
		System.out.println("oldPointMap=");
		if (newPointMap.size() > 0) {
			newPointMap = getPointMap(newPointMap, accuracy);
			oldPointMap = getPointMap(oldPointMap, accuracy);
			replacePoint(newPointMap, oldPointMap, --accuracy);
		}
	}

	private Map<String, List<Point>> getPointMap(Map<String, List<Point>> pointMap, int accuracy) {
		String zero = "0.";
		for (int i = 0; i < accuracy; i++) {
			zero += "0";
		}
		Map<String, List<Point>> newPointMap = new LinkedHashMap<String, List<Point>>();
		for (Entry<String, List<Point>> entry : pointMap.entrySet()) {
			List<Point> list = entry.getValue();
			Point point = list.get(0);
			StringBuilder stringBuilder = new StringBuilder();
			for (Float float1 : point.floatList) {
				stringBuilder.append(getStringValue(float1, accuracy, zero));
				stringBuilder.append(" ");
			}
			stringBuilder.append("/");
			for (Float float1 : point.texture.floatList) {
				stringBuilder.append(getStringValue(float1, accuracy, zero));
				stringBuilder.append(" ");
			}
			stringBuilder.append("/");
			for (Float float1 : point.normal.floatList) {
				stringBuilder.append(getStringValue(float1, accuracy, zero));
				stringBuilder.append(" ");
			}
			String key = stringBuilder.toString();
			if (newPointMap.containsKey(key)) {
				newPointMap.get(key).addAll(list);
			} else {
				List<Point> _list = new ArrayList<Point>();
				_list.addAll(list);
				newPointMap.put(key, _list);
			}
		}
		int count = 0;
		for (List<Point> list : pointMap.values()) {
			count += list.size();
		}
		System.out.println(count);
		return newPointMap;
	}

	public String getStringValue(Float float1, int accuracy, String zero) {
		String value = String.format("%." + accuracy + "f", float1.value);
		if (("-" + zero).equals(value)) {
			value = zero;
		}
		return value;
	}

	private Map<String, List<Point>> getPointMap(List<Point> pointList) {
		Map<String, List<Point>> pointMap = new LinkedHashMap<String, List<Point>>();
		for (Point point : pointList) {
			StringBuilder stringBuilder = new StringBuilder();
			for (Float float1 : point.floatList) {
				stringBuilder.append(float1.stringValue());
				stringBuilder.append(" ");
			}
			stringBuilder.append("/");
			for (Float float1 : point.texture.floatList) {
				stringBuilder.append(float1.stringValue());
				stringBuilder.append(" ");
			}
			stringBuilder.append("/");
			for (Float float1 : point.normal.floatList) {
				stringBuilder.append(float1.stringValue());
				stringBuilder.append(" ");
			}
			String key = stringBuilder.toString();
			if (pointMap.containsKey(key)) {
				pointMap.get(key).add(point);
			} else {
				List<Point> list = new ArrayList<Point>();
				list.add(point);
				pointMap.put(key, list);
			}
		}
		int count = 0;
		for (List<Point> list : pointMap.values()) {
			count += list.size();
		}
		System.out.println(count);
		return pointMap;
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
			list.addAll(point.floatList);
		}
		return list;
	}

	public List<Float> normalToArray(List<Normal> normalList) {
		List<Float> list = new ArrayList<Float>();
		for (int i = 0; i < normalList.size(); i++) {
			Normal normal = normalList.get(i);
			list.addAll(normal.floatList);
		}
		return list;
	}

	public List<Float> textureToArray(List<Texture> textureList) {
		List<Float> list = new ArrayList<Float>();
		for (int i = 0; i < textureList.size(); i++) {
			Texture texture = textureList.get(i);
			list.add(texture.x());
			list.add(texture.y());
		}
		return list;
	}

	public List<Long> faceToList(List<Face> faces) {
		List<Long> longList = new ArrayList<Long>();
		for (int i = 0; i < faces.size(); i++) {
			Face face = faces.get(i);
			longList.add(face.indexList.get(0));
			longList.add(face.indexList.get(1));
			longList.add(face.indexList.get(2));
		}
		return longList;
	}

	public static void main(String[] args) throws Exception {

		// String meshFilePath = "D:\\data\\123\\body_Mesh_s.txt";
		String meshFilePath = "D:\\data\\123\\body_Mesh.txt";
		// String meshFilePath = "D:\\data\\123\\test\\123.txt";
		MeshReader reader = new MeshReader(meshFilePath);
		reader.reader();

		reader.exportToObj("D:\\data\\123\\" + "body_Mesh_body.obj");
		System.out.println("exportToObj over");

		// String replaceFilePath = "D:\\data\\123\\max.obj";
		// String replaceFilePath = "D:\\data\\123\\modify.obj";
		String replaceFilePath = "D:\\data\\123\\max_test.obj";

		reader.replace(replaceFilePath);
		System.out.println("replace over");
		reader.mesh.reset();

		// reader.saveText(reader.text);System.out.println("saveText over");
		// System.out.println(reader.data.getByName(meshFilePath));
		String newMeshFilePath = "D:\\data\\123\\body_Mesh_new.txt";

		reader.saveData(reader.data, newMeshFilePath);
		System.out.println("saveData over");

		reader.exportToObj("D:\\data\\123\\" + "body_Mesh_body_new.obj");
		System.out.println("exportToObj over");

		// Draw draw = new Draw(reader.mesh);
		// draw.starting();
	}

}
